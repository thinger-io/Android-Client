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
import android.support.wearable.view.CircularButton;
import android.support.wearable.view.ProgressSpinner;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.thinger.wearinterface.WearDeviceResource;
import io.thinger.wearinterface.WearMessages;

public class ResourceControlActivity extends MessageActivity {

    private TextView mHeader;
    private WearDeviceResource mResource;
    private ProgressSpinner spinner;
    private LinearLayout content;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_control);

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mResource = (WearDeviceResource) extras.getSerializable("resource");
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHeader = (TextView) stub.findViewById(R.id.header);
                mHeader.setText(mResource.getResource());
                spinner = (ProgressSpinner) stub.findViewById(R.id.spinner);
                content = (LinearLayout) stub.findViewById(R.id.content);
            }
        });
    }

    @Override
    protected void onResponseReceived(String key, String value) {
        if(key.equals(WearMessages.GET_RESOURCE_API_OK)){
            spinner.setVisibility(View.GONE);
            mResource.setValue(value);
            JsonElement input = mResource.getInput();
            JsonElement output = mResource.getOutput();
            // only input
            if(input!=null && output==null){
                setInput(input);
            // only output
            }else if(output!=null && input==null){
                setOutput(output);
            }
        }else if(key.equals(WearMessages.GET_RESOURCE_API_ERROR)){
            spinner.setVisibility(View.GONE);
        }else if(key.equals(WearMessages.GET_DEVICE_RESOURCE_OK)){
            if(mResource.getType()== DeviceResourceDescription.ResourceType.RUN.getNumber()){
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                startActivity(intent);
            }else{
                mResource.setValue(value);
                setOutput(mResource.getOutput());
            }
        }else if(key.equals(WearMessages.GET_DEVICE_RESOURCE_ERROR)){
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Cannot Read the Resource");
            startActivity(intent);
        }else if(key.equals(WearMessages.POST_DEVICE_RESOURCE_OK)){
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
            startActivity(intent);
        }else if(key.equals(WearMessages.POST_DEVICE_RESOURCE_ERROR)){
            Intent intent = new Intent(this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Cannot Change the Resource");
            startActivity(intent);
        }
    }

    @Override
    protected void onReady() {
        if(mResource.getType()==DeviceResourceDescription.ResourceType.RUN.getNumber()){
            spinner.setVisibility(View.GONE);
            setRun();
        }else{
            sendMessage(WearMessages.GET_RESOURCE_API, mResource.serializeToJson());
        }
    }

    private void setInput(JsonElement input){
        if(input.isJsonPrimitive()){
            if(input.getAsJsonPrimitive().isBoolean()) {
                setInput(input.getAsJsonPrimitive().getAsBoolean());
            }
        }
    }

    private void setOutput(JsonElement output){
        if(output!=null){
            if(output.isJsonPrimitive()){
                if(output.getAsJsonPrimitive().isBoolean()){
                    setOutput(output.getAsJsonPrimitive().getAsBoolean());
                }else if(output.getAsJsonPrimitive().isNumber()){
                    setOutput(output.getAsJsonPrimitive().getAsNumber());
                }else if(output.getAsJsonPrimitive().isString()){
                    setOutput(output.getAsJsonPrimitive().getAsString());
                }
            }
        }
    }

    private void setInput(boolean state){
        Switch check;
        if(content.getChildCount()>0){
            check = (Switch) content.getChildAt(0);
            check.setChecked(state);
        }else{
            check = new Switch(this);
            content.addView(check);
            check.setChecked(state); // do not set later as it will raise a change listener
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("in", isChecked);
                    mResource.setValue(jsonObject.toString());
                    sendMessage(WearMessages.POST_DEVICE_RESOURCE, mResource.serializeToJson());
                }
            });
        }
    }

    private void setOutput(String text){
        TextView textView;
        if(content.getChildCount()>0){
            textView = (TextView) content.getChildAt(0);
        }else{
            textView = new TextView(this);
            content.addView(textView);
            setOutputClickListener(textView);
        }
        textView.setText(text);
    }

    private void setRun(){
        CircularButton circularButton;
        if(content.getChildCount()>0){
            circularButton = (CircularButton) content.getChildAt(0);
        }else{
            circularButton = (CircularButton) mInflater.inflate(R.layout.resource_run_view, null);
            content.addView(circularButton);
            circularButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(WearMessages.GET_DEVICE_RESOURCE, mResource.serializeToJson());
                }
            });
        }
    }

    private void setOutput(Number number){
        TextView textView;
        if(content.getChildCount()>0){
            textView = (TextView) content.getChildAt(0);
        }else{
            textView = (TextView) mInflater.inflate(R.layout.resource_number_view, null);
            content.addView(textView);
            setOutputClickListener(textView);
        }
        textView.setText(number.toString());
    }

    private void setOutput(boolean value){
        Switch control;
        if(content.getChildCount()>0){
            control = (Switch) content.getChildAt(0);
        }else{
            control = new Switch(this);
            control.setEnabled(false);
            content.addView(control);
            setOutputClickListener(control);
        }
        control.setChecked(value);
    }

    private void setOutputClickListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(WearMessages.GET_DEVICE_RESOURCE, mResource.serializeToJson());
            }
        });
    }
}
