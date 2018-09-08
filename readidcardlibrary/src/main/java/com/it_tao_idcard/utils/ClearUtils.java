package com.it_tao_idcard.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ClearUtils {
	
	/** 
	* 清除应用缓存的用户数据，同时停止所有服务和Alarm定时task 
	* String cmd = "pm clear " + packageName; 
	* String cmd = "pm clear " + packageName  + " HERE"; 
	* Runtime.getRuntime().exec(cmd) 
	* @param packageName 
	* @return 
	*/  
	public static Process clearAppUserData(String packageName) {  
	Process p = execRuntimeProcess("pm clear " + packageName);  
	if (p == null) {  
	Log.e("","Clear app data packageName:" + packageName  
	+ ", FAILED !");  
	} else {  
		Log.v("","Clear app data packageName:" + packageName  
	+ ", SUCCESS !");  
	}  
	    return p;  
	}  
	
	
	public static Process execRuntimeProcess(String commond) {  
		Process p = null;  
		try {  
		p = Runtime.getRuntime().exec(commond);  
		} catch (IOException e) {  
			Log.e("","exec Runtime commond:" + commond + ", IOException" + e);  
		e.printStackTrace();  
		}  
		Log.v("","exec Runtime commond:" + commond + ", Process:" + p);  
		return p;  
		}  
	
	
	/**      * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)      *       * @param context      */   
	 public static void cleanInternalCache(Context context) {      
	 deleteFilesByDirectory(context.getCacheDir());     
	 }       
	 /**      * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)      *       * @param context      */    
	 public static void cleanDatabases(Context context) {         
	 deleteFilesByDirectory(new File("/data/data/"                 + context.getPackageName() + "/databases"));   
	 }       
	 /**      * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)      *       * @param context      */    
	 public static void cleanSharedPreference(Context context) {       
	 deleteFilesByDirectory(new File("/data/data/"                 + context.getPackageName() + "/shared_prefs"));     
	 }      
	 /**      * 按名字清除本应用数据库      *       * @param context      * @param dbName      */     
	 public static void cleanDatabaseByName(Context context, String dbName) {         context.deleteDatabase(dbName);     
	 }      
	 /**      * 清除/data/data/com.xxx.xxx/files下的内容      *       * @param context      */    
	 public static void cleanFiles(Context context) {        
	 deleteFilesByDirectory(context.getFilesDir());      
	 }       
	 /**      * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)      *       * @param context      */   
	 public static void cleanExternalCache(Context context) {      
	 if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) {        
	 deleteFilesByDirectory(context.getExternalCacheDir());        
	 }      
	 }     
	 /**      * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除      *       * @param filePath      */   
	 public static void cleanCustomCache(String filePath) {      
	 deleteFilesByDirectory(new File(filePath));     
	 }     
	 /**      * 清除本应用所有的数据      *       * @param context      * @param filepath      */   
	 public static void cleanApplicationData(Context context, String... filepath) {         
	 cleanInternalCache(context);        
	 cleanExternalCache(context);        
	 cleanDatabases(context);         
	 cleanSharedPreference(context);        
	 cleanFiles(context);          
	 for (String filePath : filepath) {         
	 cleanCustomCache(filePath);      
	 }     
	 }      
	 /**      * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理      *       * @param directory      */  
	 private static void deleteFilesByDirectory(File directory) {      
	 if (directory != null && directory.exists() && directory.isDirectory()) {       
	 for (File item : directory.listFiles()) {             
	 item.delete();             
	 }         
	 }     
	 }  

}
