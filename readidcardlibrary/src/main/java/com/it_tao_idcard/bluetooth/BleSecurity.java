package com.it_tao_idcard.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tao on 2018/7/30 0030.
 */

public class BleSecurity {


    private  static  void openGps(final Activity activity , int flag) {
        //跳转到gps设置页
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity. startActivityForResult(intent, flag);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, " 请打开位置服务!", Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public  static  void checkBleDevice(Context context) {
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // 开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
            }
        } else {
            Log.i("blueTooth", "该手机不支持蓝牙");
        }
    }

    // gps是否可用
    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }


}
