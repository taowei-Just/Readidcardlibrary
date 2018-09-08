package com.hdos.usbdevice;

import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.ArrayList;

public class ReceiverHelper {

	
	public static  ArrayList<BroadcastReceiver> list  = new ArrayList<BroadcastReceiver>();
	
	public static BroadcastReceiver removeExtra(Context context){
		if (list!=null &&list.size()>1){
		for (int i=0 ;i<list.size() ;i++) {
			 
			if(1<list.size()){
				context.unregisterReceiver(list.get(0));
				list.remove(0);
			}
			
		}
		
		return list.get(0);
		
		}else {
			return null ;
		}
	}
	
	public static void removeAll(Context context){
	 
		if (list!=null &&list.size()>0){
			
			for (BroadcastReceiver br : list) {
				
				if (br!=null)
				{
					context.unregisterReceiver(br);
					 
					list.remove(br);
					
				}
				
			}
			
		}
		
	}
	
	
}
