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

import java.io.Serializable;

public class WearDeviceInfo implements Serializable {
    private String user;
    private String device;
    private String token;

    public WearDeviceInfo(String user, String device, String token) {
        this.user = user;
        this.device = device;
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public byte[] serializeToJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json.getBytes();
    }

    public static WearDeviceInfo fromJson(byte[] json){
        Gson gson = new Gson();
        WearDeviceInfo device = gson.fromJson(new String(json), WearDeviceInfo.class);
        return device;
    }
}
