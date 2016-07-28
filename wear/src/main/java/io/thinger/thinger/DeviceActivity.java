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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.ProgressSpinner;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.thinger.wearinterface.WearDeviceInfo;
import io.thinger.wearinterface.WearDeviceResource;
import io.thinger.wearinterface.WearMessages;

public class DeviceActivity extends MessageActivity implements WearableListView.ClickListener, DelayedConfirmationView.DelayedConfirmationListener {
    private WearableListView mListView;
    public static String TAG = "DeviceActivity";
    private WearDeviceInfo deviceInfo;
    private ProgressSpinner spinner;
    private MyAdapter mAdapter;
    private TextView mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            deviceInfo = (WearDeviceInfo) extras.getSerializable("device");
        }

        mAdapter = new MyAdapter(this);

        setContentView(R.layout.activity_device);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHeader = (TextView) stub.findViewById(R.id.header);
                mHeader.setText(deviceInfo.getDevice());
                mListView = (WearableListView) stub.findViewById(R.id.listViewAPI);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(DeviceActivity.this);

                mListView.addOnScrollListener(new WearableListView.OnScrollListener() {
                    @Override
                    public void onScroll(int i) {
                        mHeader.setY(mHeader.getY() - i);
                    }

                    @Override
                    public void onAbsoluteScrollChange(int i) {

                    }

                    @Override
                    public void onScrollStateChanged(int i) {
                    }

                    @Override
                    public void onCentralPositionChanged(int i) {
                    }
                });

                spinner = (ProgressSpinner) stub.findViewById(R.id.spinner);
            }
        });
    }

    @Override
    protected void onReady() {
        mAdapter.clear();
        spinner.setVisibility(View.VISIBLE);
        // send a message to get the devices
        sendMessage(WearMessages.GET_DEVICE_API, deviceInfo.serializeToJson());
    }

    @Override
    protected void onDeviceConnectionError() {

    }

    void addResourcesFromAPIResponse(JsonObject object, String parent){
        if(object == null) return;
        if(parent == null) parent = "";
        for(Map.Entry<String, JsonElement> entry : object.entrySet()){
            // create a new resource
            String res = parent + entry.getKey();
            final DeviceResourceDescription resource = new DeviceResourceDescription(res, entry.getValue());

            // only add those resources with some functionality
            if(resource.getResourceType()!=DeviceResourceDescription.ResourceType.NONE){
                mAdapter.addResource(resource);
            }

            // keep iterating if resource has sub-resources
            JsonObject value = entry.getValue().getAsJsonObject();
            if(value.has("/")){
                addResourcesFromAPIResponse(value.getAsJsonObject("/"), res + "/");
            }
        }
    }

    @Override
    protected void onResponseReceived(final String key, final String value) {
        if(key.equals(WearMessages.GET_DEVICE_API_ERROR)){
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Device Not Available");
            startActivityForResult(intent, 0);
        }else if(key.equals(WearMessages.GET_DEVICE_API_OK)){
            spinner.setVisibility(View.GONE);
            JsonElement jsonElement = new JsonParser().parse(value);
            addResourcesFromAPIResponse(jsonElement.getAsJsonObject(), null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        DeviceResourceDescription device = mAdapter.getResource(viewHolder.getAdapterPosition());
        WearDeviceResource deviceResource = new WearDeviceResource(deviceInfo, device.getResourceName(), device.getResourceType().getNumber());
        Intent intent = new Intent(this, ResourceControlActivity.class);
        intent.putExtra("resource", deviceResource);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onTimerFinished(View view) {

    }

    @Override
    public void onTimerSelected(View view) {

    }

    private class MyAdapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;
        private List<DeviceResourceDescription> resources;

        private MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            resources = new ArrayList<>();
        }

        public void clear(){
            resources.clear();
            notifyDataSetChanged();
        }

        void addResource(DeviceResourceDescription resource){
            resources.add(resource);
            notifyDataSetChanged();
        }

        DeviceResourceDescription getResource(int position){
            return resources.get(position);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(mInflater.inflate(R.layout.row_device_resource, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
            view.setText(resources.get(position).getResourceName());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return resources.size();
        }
    }

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            final Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidein_top);
            // find the header or cache it elsewhere
            mHeader.startAnimation(slide);
        }
    }*/
}
