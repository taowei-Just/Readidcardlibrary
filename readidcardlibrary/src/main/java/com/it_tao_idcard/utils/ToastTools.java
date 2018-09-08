package com.it_tao_idcard.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/7/5.
 */

public class ToastTools {
    
    
    
    public static  void showLong(Context context ,String string){

        Toast.makeText(context, string,Toast.LENGTH_LONG).show();
        
    }
     
    public static  void showShort(Context context ,String string){

        Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
        
        
        
    }
    
    
    
}
