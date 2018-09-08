package com.it_tao_idcard.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tao on 2018/7/24 0024.
 */

public class BluetoothReceiver extends BroadcastReceiver {
    BluetoothHelper helper;
    String tag = getClass().getSimpleName();

    public BluetoothReceiver(BluetoothHelper helper) {
        this.helper = helper;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            // 搜索到设备
            BluetoothDevice bleDevide = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.e(tag, "搜索到设备 " + bleDevide.getName());
            helper.onSerch(bleDevide);
        } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            // 配对状态
            BluetoothDevice bleDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bleDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                Log.e(tag, "配对中");
                helper.onBond(bleDevice);
            } else if (bleDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.e(tag, "配对完成");
                helper.bondComplete(bleDevice);
            } else if (bleDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Log.e(tag, "取消配对");
            }
        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            Log.e(tag, "搜索完毕！");
        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            Log.e(tag, "开始搜索！");
        } else if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
            Log.e(tag, "搜索模式改变！");
        }
    }

}


