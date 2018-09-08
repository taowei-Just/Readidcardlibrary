package com.hdos.usbdevice;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class UsbHandler extends Handler {
    UsbHandler(publicSecurityIDCardLib parampublicSecurityIDCardLib) {


    }


    public UsbHandler() {
        // TODO Auto-generated constructor stub
    }

    public final void handleMessage(Message msg) {
        if (msg.what == 2) {
            Log.e("UsbHandler ", "USB外设已弹出");
        }
        if (msg.what == 3) {
            Log.e("UsbHandler", "USB设备无权限");

        }
    }


}

 