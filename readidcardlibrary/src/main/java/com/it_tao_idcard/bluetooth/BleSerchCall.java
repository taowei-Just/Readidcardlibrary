package com.it_tao_idcard.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Tao on 2018/7/24 0024.
 */

public interface BleSerchCall {
    void onSechDevice(BluetoothDevice bleDevide);
    void onBoundDevice(BluetoothDevice bleDevide);
}
