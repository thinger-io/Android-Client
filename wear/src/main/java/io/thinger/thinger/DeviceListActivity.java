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
import android.support.wearable.view.ProgressSpinner;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import io.thinger.wearinterface.WearDeviceInfo;
import io.thinger.wearinterface.WearMessages;

public class DeviceListActivity extends MessageActivity implements WearableListView.ClickListener{
    private WearableListView mListView;
    private MyAdapter mAdapter;
    private TextView mHeader;
    private ProgressSpinner spinner;
    public static String TAG = "WearListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mAdapter = new MyAdapter(DeviceListActivity.this);
                mListView.setAdapter(mAdapter);
                mListView.setClickListener(DeviceListActivity.this);

                mHeader = (TextView) stub.findViewById(R.id.header);

                spinner = (ProgressSpinner) stub.findViewById(R.id.spinner);

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
            }
        });
    }

    @Override
    protected void onReady() {
        mAdapter.clear();
        spinner.setVisibility(View.VISIBLE);
        sendMessage(WearMessages.LIST_DEVICES);
    }

    @Override
    protected void onDeviceConnectionError() {
        super.onDeviceConnectionError();
        spinner.setVisibility(View.GONE);
    }

    @Override
    protected void onResponseReceived(String key, String value) {
        if(key.equals(WearMessages.LIST_DEVICES_OK)){
            spinner.setVisibility(View.GONE);
            Gson gson = new Gson();
            final WearDeviceInfo[] arr = gson.fromJson(value, WearDeviceInfo[].class);
            for(WearDeviceInfo device : arr) {
                mAdapter.add(device);
            }
        }else if(key.equals(WearMessages.LIST_DEVICES_ERROR)){
            spinner.setVisibility(View.GONE);
            Toast.makeText(this, "Cannot read devices!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        WearDeviceInfo device = mAdapter.get(viewHolder.getAdapterPosition());
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "You tapped on Top empty area", Toast.LENGTH_SHORT).show();
    }

    private class MyAdapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;
        private ArrayList<WearDeviceInfo> data;

        public WearDeviceInfo get(int index){
            return data.get(index);
        }

        public void add(WearDeviceInfo device){
            data.add(device);
            notifyDataSetChanged();
        }

        private MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            data = new ArrayList<>();
        }

        public void clear(){
            data.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty(){
            return data.isEmpty();
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.row_device, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.textView);
            view.setText(get(position).getDevice());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}