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

package io.thinger.thinger.model;

import android.util.Base64;

import com.google.gson.Gson;

import java.util.List;

public class DeviceToken {
    private Long iat;
    private Long exp;
    private String usr;
    private String dev;
    private List<String> res;
    private String jwtToken;

    @Override
    public String toString() {
        return "DeviceTokens{" +
                "iat=" + iat +
                ", exp=" + exp +
                ", usr='" + usr + '\'' +
                ", dev='" + dev + '\'' +
                ", res=" + res +
                '}';
    }

    public String getAuthorizationHeader(){
        return "Bearer " + jwtToken;
    }

    public String getJwtToken(){
        return jwtToken;
    }

    public String getUser(){
        return usr;
    }

    public String getDevice(){
        return dev;
    }

    public List<String> getResources(){
        return res;
    }

    public static DeviceToken parse(String jwt){
        String[] parts = jwt.split("\\.");
        if(parts.length==3){
            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
            Gson gson = new Gson();
            DeviceToken deviceToken = gson.fromJson(payload, DeviceToken.class);
            deviceToken.jwtToken = jwt;
            return deviceToken;
        }
        return null;
    }
}
