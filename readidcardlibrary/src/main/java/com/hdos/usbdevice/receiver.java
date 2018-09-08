package com.hdos.usbdevice;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Message;


final class receiver extends BroadcastReceiver {
	receiver(publicSecurityIDCardLib parampublicSecurityIDCardLib) {
		
		ReceiverHelper.list.add(this);
		
	}

	public final void onReceive(Context paramContext, Intent intent)
	  {
		Message msg ;
		
		UsbDevice device;
		String name;
		
	    String action = intent.getAction();
	    if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action))
	    {
	    	name = (device = (UsbDevice)intent.getParcelableExtra("device")).getDeviceName();
	      if ((device != null)  )
	      {
	        (msg = new Message()).what = 2;
	        msg.obj = "USB设备拔出 ";
	        new UsbHandler().sendMessage(msg);
	      }
	    }
	    else
	    {
	     
	      if ("com.android.USB_PERMISSION".equals(action))
	      {
	        (msg = new Message()).what = 3;
	        msg.obj = "USB设备无权限";
	        new UsbHandler().sendMessage(msg);
	      }
	    }
	  }
	 

	}

	 

	 
 

/*
 * Location: E:\NEWEclipseSpace\TobaccoSales\libs\publicSecurityIDCardLib.jar
 * Qualified Name: com.hdos.usbdevice.usbHandler JD-Core Version: 0.6.2
 */