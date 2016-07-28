# Thinger.io Android Client

Android source code for the [Thinger.io](https://thinger.io) Internet of Things Platform. With wear support.

## Overview

The current version of the Android APP does not require any kind of login to interact with your IoT devices. Simply scan your device token as a QR code and your phone will be able to interact with your device for reading sensor values, changing led or relays states, and so on.

<p align="center">
<img src="http://docs.thinger.io/arduino/assets/phone.png">
</p>

Download at Google Play: [Thinger.io Android Client](https://play.google.com/store/apps/details?id=io.thinger.thinger)

## Wear Support 
The Android APP supports Android Wear devices for controlling and reading values from your sensors and actuators! The current version is still under testing and therefore it can still have some errors. At this moment the Wear version allows reading values (not composed values at this moment), execute resources, and control boolean resources. It is so cool to turn on and off things from the SmartWatch! To get the devices available in the SmartWatch, it is not required to do anything special. As usually, just scan the QR code from the platform with the mobile phone.

<p align="center">
<img src="http://discoursefiles.s3-eu-west-1.amazonaws.com/original/1X/2faffe71abf48fd47281e32a91b676b15f6d2f05.png" width="240">
<img src="http://discoursefiles.s3-eu-west-1.amazonaws.com/original/1X/c413cff037d5f9a2941bc6a723b45259ef693196.png" width="240">
<img src="http://discoursefiles.s3-eu-west-1.amazonaws.com/original/1X/d120460c2c50f8ac9853fa1e5927548e2d1cb424.png" width="240">
</p>>

## Features under development

Some of the planned featues for the Thinger.io Android App are the following:

* Writing device tokens to NFC Tags and reading them back without using QR.
* Improve Android Wear support.
* Improve device discovery screen.
* Add settings.
* Session login for accounts and all the underlying stuff like listing devices, showing connections, dashboards?, etc.

## Contributors

Any help is always welcome! If you feel confident about some feature to implement, or want to fix a bug, or improve the existing code, please, submit a pull request! :)

## License

<img align="right" src="http://opensource.org/trademarks/opensource/OSI-Approved-License-100x137.png">

The class is licensed under the [MIT License](http://opensource.org/licenses/MIT):

Copyright &copy; 2015 [THINGER LTD](http://thinger.io)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
