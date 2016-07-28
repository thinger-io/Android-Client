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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.thinger.thinger.database.DBDeviceToken;
import io.thinger.wearinterface.WearDeviceInfo;

public class DeviceFromToken extends Device {
    private DBDeviceToken databaseToken;
    private DeviceToken deviceToken;

    @Override
    public void remove() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        databaseToken.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public String getDeviceId() {
        return deviceToken.getDevice();
    }

    @Override
    public String getDeviceOwner() {
        return deviceToken.getUser();
    }

    public String getJWTToken(){
        return deviceToken.getJwtToken();
    }

    public static List<Device> getDeviceTokens(){
        List<Device> devices = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DBDeviceToken> deviceTokens = realm.where(DBDeviceToken.class).findAll();
        for(DBDeviceToken token : deviceTokens){
            DeviceFromToken deviceFromToken = new DeviceFromToken();
            deviceFromToken.databaseToken = token;
            deviceFromToken.deviceToken = DeviceToken.parse(token.getToken());
            devices.add(deviceFromToken);
        }
        return devices;
    }


    public static List<WearDeviceInfo> getWearDevices(){
        List<WearDeviceInfo> devices = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DBDeviceToken> deviceTokens = realm.where(DBDeviceToken.class).findAll();
        for(DBDeviceToken token : deviceTokens){
            DeviceToken deviceToken = DeviceToken.parse(token.getToken());
            devices.add(new WearDeviceInfo(deviceToken.getUser(), deviceToken.getDevice(), deviceToken.getJwtToken()));
        }
        return devices;
    }

    public static Device createDeviceFromToken(DeviceToken deviceToken){
        DeviceFromToken deviceFromToken = new DeviceFromToken();
        deviceFromToken.databaseToken = saveDeviceToken(deviceToken);
        deviceFromToken.deviceToken = deviceToken;
        return deviceFromToken;
    }

    public static DBDeviceToken saveDeviceToken(DeviceToken deviceToken){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DBDeviceToken dbToken = realm.createObject(DBDeviceToken.class);
        dbToken.setToken(deviceToken.getJwtToken());
        realm.commitTransaction();
        return dbToken;
    }
}
