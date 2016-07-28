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

package io.thinger.thinger.services;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.thinger.api.ThingerAPI;
import io.thinger.thinger.model.DeviceFromToken;
import io.thinger.wearinterface.WearDeviceInfo;
import io.thinger.wearinterface.WearDeviceResource;
import io.thinger.wearinterface.WearMessages;
import retrofit2.Call;
import retrofit2.Response;

public class WearListCallListenerService extends WearableListenerService {
    public static String TAG = "WearListCallListenerService";

    private void reply(String nodeId, String path, byte[] content) {
        Log.d(TAG, "Sending Message (" + nodeId + ")" + " - " + path + (content!=null ? ": " + new String(content) : ""));

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        client.blockingConnect(10000, TimeUnit.MILLISECONDS);
        Wearable.MessageApi.sendMessage(client, nodeId, path, content);
        client.disconnect();
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "Received Message - " + messageEvent.getPath() + (messageEvent.getData()!=null ? ": " + new String(messageEvent.getData()) : ""));

        String event = messageEvent.getPath();
        if(event.equals(WearMessages.LIST_DEVICES)){
            List<WearDeviceInfo> devices = DeviceFromToken.getWearDevices();
            Gson gson = new Gson();
            reply(messageEvent.getSourceNodeId(), WearMessages.LIST_DEVICES_OK, gson.toJson(devices).getBytes());
        }else if(event.equals(WearMessages.GET_DEVICE_API)){
            WearDeviceInfo device = WearDeviceInfo.fromJson(messageEvent.getData());
            Call<JsonObject> call = ThingerAPI.getInstance().deviceAPI("Bearer " + device.getToken(), device.getUser(), device.getDevice());
            try {
                Response<JsonObject> response = call.execute();
                if (response.isSuccessful()) {
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_API_OK, response.body().toString().getBytes());
                }else{
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_API_ERROR, null);
                }
            } catch (IOException e) {
                reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_API_ERROR, null);
            }
        }else if(event.equals(WearMessages.GET_RESOURCE_API)){
            WearDeviceResource resource = WearDeviceResource.fromJson(messageEvent.getData());
            WearDeviceInfo device = resource.getDeviceInfo();
            Call<JsonObject> call = ThingerAPI.getInstance().resourceAPI("Bearer " + device.getToken(), device.getUser(), device.getDevice(), resource.getResource());
            try {
                Response<JsonObject> response = call.execute();
                if (response.isSuccessful()) {
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_RESOURCE_API_OK, response.body().toString().getBytes());
                }else{
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_RESOURCE_API_ERROR, null);
                }
            } catch (IOException e) {
                reply(messageEvent.getSourceNodeId(), WearMessages.GET_RESOURCE_API_ERROR, null);
            }
        }else if(event.equals(WearMessages.POST_DEVICE_RESOURCE)){
            WearDeviceResource resource = WearDeviceResource.fromJson(messageEvent.getData());
            WearDeviceInfo device = resource.getDeviceInfo();
            Call<JsonElement> call = ThingerAPI.getInstance().postResource("Bearer " + device.getToken(), device.getUser(), device.getDevice(), resource.getResource(), resource.getJsonElement());
            try {
                Response<JsonElement> response = call.execute();
                if (response.isSuccessful()) {
                    reply(messageEvent.getSourceNodeId(), WearMessages.POST_DEVICE_RESOURCE_OK, response.body().toString().getBytes());
                }else{
                    reply(messageEvent.getSourceNodeId(), WearMessages.POST_DEVICE_RESOURCE_ERROR, null);
                }
            } catch (IOException e) {
                reply(messageEvent.getSourceNodeId(), WearMessages.POST_DEVICE_RESOURCE_ERROR, null);
            }
        }else if(event.equals(WearMessages.GET_DEVICE_RESOURCE)){
            WearDeviceResource resource = WearDeviceResource.fromJson(messageEvent.getData());
            WearDeviceInfo device = resource.getDeviceInfo();
            Call<JsonElement> call = ThingerAPI.getInstance().getResource("Bearer " + device.getToken(), device.getUser(), device.getDevice(), resource.getResource());
            try {
                Response<JsonElement> response = call.execute();
                if (response.isSuccessful()) {
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_RESOURCE_OK, response.body().toString().getBytes());
                }else{
                    reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_RESOURCE_ERROR, null);
                }
            } catch (IOException e) {
                reply(messageEvent.getSourceNodeId(), WearMessages.GET_DEVICE_RESOURCE_ERROR, null);
            }
        }
    }
}