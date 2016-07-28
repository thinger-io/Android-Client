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

package io.thinger.wearinterface;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;

public class WearDeviceResource implements Serializable {
    private WearDeviceInfo device;
    private String resource;
    private String value;
    private int type;

    public WearDeviceResource(WearDeviceInfo deviceInfo, String resource, int type) {
        this.device = deviceInfo;
        this.resource = resource;
        this.type = type;
    }

    public WearDeviceInfo getDeviceInfo() {
        return device;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDevice(WearDeviceInfo deviceInfo) {
        this.device = deviceInfo;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public byte[] serializeToJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json.getBytes();
    }

    public static WearDeviceResource fromJson(byte[] json){
        Gson gson = new Gson();
        WearDeviceResource resource = gson.fromJson(new String(json), WearDeviceResource.class);
        return resource;
    }

    public JsonElement getJsonElement() {
        if (value != null && !value.isEmpty()) {
            return new JsonParser().parse(value);
        } else {
            return null;
        }
    }

    public JsonElement getInput(){
        if(this.value==null || this.value.isEmpty()) return null;
        JsonObject value = getJsonElement().getAsJsonObject();
        if(value.has("in")){
            return value.get("in");
        }
        return null;
    }

    public JsonElement getOutput(){
        if(this.value==null || this.value.isEmpty()) return null;
        JsonObject value = getJsonElement().getAsJsonObject();
        if(value!=null && value.has("out")){
            return value.get("out");
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
