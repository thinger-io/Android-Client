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

public class WearMessages {
    public static String LIST_DEVICES = "/list_devices";
    public static String LIST_DEVICES_OK = "/list_devices/ok";
    public static String LIST_DEVICES_ERROR = "/list_devices/error";

    public static String GET_DEVICE_API = "/get_device_api";
    public static String GET_DEVICE_API_OK = "/get_device_api/ok";
    public static String GET_DEVICE_API_ERROR = "/get_device_api/error";

    public static String GET_RESOURCE_API = "/get_resource_api";
    public static String GET_RESOURCE_API_OK = "/get_resource_api/ok";
    public static String GET_RESOURCE_API_ERROR = "/get_resource_api/error";

    public static String POST_DEVICE_RESOURCE = "/post_device_resource";
    public static String POST_DEVICE_RESOURCE_OK = "/post_device_resource/ok";
    public static String POST_DEVICE_RESOURCE_ERROR = "/post_device_resource/error";

    public static String GET_DEVICE_RESOURCE = "/get_device_resource";
    public static String GET_DEVICE_RESOURCE_OK = "/get_device_resource/ok";
    public static String GET_DEVICE_RESOURCE_ERROR = "/get_device_resource/error";
}
