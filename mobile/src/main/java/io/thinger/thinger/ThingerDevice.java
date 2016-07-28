/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 THINK BIG LABS S.L.
 * Author: alvarolb@gmail.com (Alvaro Luis Bustamante)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.thinger.thinger;

import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.thinger.api.ThingerAPI;
import io.thinger.thinger.model.DeviceToken;
import io.thinger.thinger.views.DeviceResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThingerDevice extends AppCompatActivity implements DeviceResourceAdapter.OnDeviceResourceClicked {
    private DeviceToken token;

    private RecyclerView mRecyclerView;
    private DeviceResourceAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thinger_device);

        mRecyclerView = (RecyclerView) findViewById(R.id.resources_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mLayoutManager = new LinearLayoutManager(this);

        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 16, getResources().getDisplayMetrics());

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(value));
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DeviceResourceAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDeviceAPI();
            }
        });

        mSwipeRefreshLayout.setEnabled(false);

        token = DeviceToken.parse(getIntent().getExtras().getString("token"));
        if(token!=null){
            setTitle(token.getDevice());
            loadDeviceAPI();
        }
    }

    void loadDeviceAPI(){
        // some log
        Log.v("API", "Loading device API with token:\n" + token.toString());
        // clear all current resources
        mAdapter.clear();
        // initialize pending resource request to zero
        pendingResources = 0;
        // start showing the loading widget
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        // check if the device token is limited to some resources
        List<String> limitedResources = token.getResources();
        // we have access to everything, as there is no limited resources in the token
        if(limitedResources==null){
            Log.v("API", "Using Auth: " + token.getAuthorizationHeader());
            Call<JsonObject> call = ThingerAPI.getInstance().deviceAPI(token.getAuthorizationHeader(), token.getUser(), token.getDevice());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        addResourcesFromAPIResponse(response.body(), "");
                    }else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        // TODO DISPLAY ERROR
                        try {
                            Log.e("API", "Error while fetching the query: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.e("API", t.toString());
                }
            });
        // we have access to only some known resources so add them directly
        }else{
            // there is no any resource available
            if(limitedResources.isEmpty()){
                mSwipeRefreshLayout.setRefreshing(false);
            }else{
                for(String resource : limitedResources){
                    addResourceFromTokenRestriction(resource);
                }
            }
        }
    }

    void addResourceFromTokenRestriction(String res){
        final DeviceResource resource = new DeviceResource(res);
        resource.setDeviceToken(token);
        addResource(resource);
    }

    private int pendingResources = 0;

    void addResource(final DeviceResource deviceResource){
        // avoid querying resources that does not define a function
        if(deviceResource.getResourceType()==DeviceResource.ResourceType.NONE) return;

        // fetch the resource api
        Call<JsonObject> call = ThingerAPI.getInstance().resourceAPI(token.getAuthorizationHeader(), token.getUser(), token.getDevice(), deviceResource.getResourceName());
        pendingResources++;
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    deviceResource.setJsonObject(response.body());
                    mAdapter.addResource(deviceResource);
                } else {
                    // TODO DISPLAY ERROR
                    Log.e("API", "Error while fetching the resource API: " + response.errorBody().toString());
                }
                pendingResources--;
                if(pendingResources<=0){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API", "Error while fetching the resource API: " + t.toString());
                pendingResources--;
                if(pendingResources<=0){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    void addResourcesFromAPIResponse(JsonObject object, String parent){
        if(object == null) return;
        if(parent == null) parent = "";
        for(Map.Entry<String, JsonElement> entry : object.entrySet()){

            // create a new resource
            String res = parent + entry.getKey();
            final DeviceResource resource = new DeviceResource(res, entry.getValue());
            resource.setDeviceToken(token);
            addResource(resource);

            // keep iterating if resource has sub-resources
            JsonObject value = entry.getValue().getAsJsonObject();
            if(value.has("/")){
                addResourcesFromAPIResponse(value.getAsJsonObject("/"), res + "/");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thinger_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_device_resources) {
            loadDeviceAPI();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeviceTokenClick(DeviceResource token) {

    }
}
