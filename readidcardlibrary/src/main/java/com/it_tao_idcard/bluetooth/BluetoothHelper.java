package com.it_tao_idcard.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.it_tao_idcard.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;


/**
 * Created by Tao on 2018/7/24 0024.
 */

public class BluetoothHelper {

    String tag = getClass().getSimpleName();

    String[] blePermissionS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    Activity context;
    private BluetoothAdapter bluetoothAdapter;
    boolean permissionAble = false;
    BleSerchCall blecall;
    private BluetoothSocket bluetoothSocket;
    Handler handler = new Handler();
    private BluetoothReceiver bluetoothReceiver;
    private InputStream bluetoothinputStream;
    private OutputStream bluetoothoutputStream;
    private BleAquireCard bleAquireCard;
    ReadInfoCall infoCall;
    boolean isAuto = false;
    boolean isAutoConnect = true;
    private static BluetoothHelper bluetoothHelper;

    private BluetoothHelper(Activity context, BleSerchCall blecall, ReadInfoCall infoCall) {
        this(context, blecall);
        this.infoCall = infoCall;
    }

    public BluetoothHelper(Activity context, BleSerchCall blecall) {
        this(context);
        this.blecall = blecall;
    }

    public BluetoothHelper(Activity context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothHelper setInfoCall(ReadInfoCall infoCall) {
        this.infoCall = infoCall;
        return this;
    }

    public BluetoothHelper setBlecall(BleSerchCall blecall) {
        this.blecall = blecall;
        return this;
    }

    private void initBroad() {
        bluetoothReceiver = new BluetoothReceiver(this);
        IntentFilter filter = new IntentFilter();
        // 搜索结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 配对状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 连接状态发生改变
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // s搜索开始
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // 名称改变
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        //状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //状态改变
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 应该是重新搜索
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        context.registerReceiver(bluetoothReceiver, filter);
        serchBle();
    }

    public void serchBle() {
        if (permissionAble) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                Log.e(tag, "打开蓝牙 ");
            }
            if (bluetoothAdapter.isDiscovering()) {
                cancleSerchBle();
            }
            bluetoothAdapter.startDiscovery();
        } else {
            // 请求权限
            //  rxPermissions.isGranted()
        }
    }

    public void cancleSerchBle() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean isPermissionAble() {
        return permissionAble;
    }

    public void setPermissionAble(boolean permissionAble) {
        this.permissionAble = permissionAble;
    }

    int rquestcode;

    public void requestBlePermission(int rquestcode) {
        this.rquestcode = rquestcode;
        boolean ispermission = true;
        for (int i = 0; i < blePermissionS.length; ++i) {
            String permission = blePermissionS[i];
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                switch (i) {
                    case 0:
                    case 1:
                        // 位置权限
                        Toast.makeText(context, " 请允许相关位置权限 ", Toast.LENGTH_LONG).show();
                        break;

                    case 2:
                    case 3:
                        // 蓝牙权限
                        Toast.makeText(context, " 请允许相关蓝牙权限 ", Toast.LENGTH_LONG).show();
                        break;
                }
                ispermission = false;
            }
        }

        if (!ispermission)
            ActivityCompat.requestPermissions(context, blePermissionS, rquestcode);
        else
            permissionAble = true;
        scan();
    }

    public boolean onReBlePermission(int[] grantResults) {
        boolean isBle = true;
        if (grantResults == null || grantResults.length <= 0)
            return false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isBle = false;
                permissionAble = false;
                switch (i) {
                    case 0:
                    case 1:
                        // 位置权限
                        Toast.makeText(context, "  位置权限未打开 ", Toast.LENGTH_LONG).show();
                        break;

                    case 2:
                    case 3:
                        // 蓝牙权限
                        Toast.makeText(context, "  蓝牙权限权限未打开 ", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
        scan();
        return isBle;
    }

    private void scan() {
        if (permissionAble) {
            initBroad();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivityForResult(intent, 1);
        }
    }

    public void onSerch(BluetoothDevice bleDevide) {
        if (permissionAble)
            blecall.onSechDevice(bleDevide);
        else
            requestBlePermission(rquestcode);
    }

    public void BindBle(BluetoothDevice devices) {
        if (devices.getBondState() == BluetoothDevice.BOND_BONDED) {

            if (blecall != null)
                blecall.onBoundDevice(devices);
            Log.e(tag, "已经配对：");
            if (isAuto)
                connectSecket(devices);
            return;
        }
        if (devices.getBondState() == BluetoothDevice.BOND_BONDING) {
            Log.e(tag, "配对中 ：");
            return;
        }
        try {
            Method createBond = BluetoothDevice.class.getMethod("createBond");
            boolean invoke = (boolean) createBond.invoke(devices);
            Log.e(tag, "配对设备" + invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void onBond(BluetoothDevice bleDevice) {
    }

    public void bondComplete(BluetoothDevice bleDevice) {
        Log.e(tag, " 配对成功：" + bleDevice.getName());

        if (blecall != null)
            blecall.onBoundDevice(bleDevice);

        if (isAuto)
            connectSecket(bleDevice);
    }

    private void connectSecket(final BluetoothDevice bleDevice) {
        try {
//            if (bleAquireCard != null && !bleAquireCard.isInterrupted())
//                return;

            if (Build.VERSION.SDK_INT < 15) {
                bluetoothSocket = bleDevice.createRfcommSocketToServiceRecord(bleDevice.getUuids()[0].getUuid());
            } else {
                bluetoothSocket = bleDevice.createInsecureRfcommSocketToServiceRecord(bleDevice.getUuids()[0].getUuid());
            }
//            Toast.makeText(context, " 准备连接蓝牙 socket ：  " + bleDevice.getName() + " uuid " + bleDevice.getUuids()[0].getUuid(), Toast.LENGTH_LONG).show();

            if (bleAquireCard != null && !bleAquireCard.isInterrupted())
                bleAquireCard.interrupt();

            bleAquireCard = new BleAquireCard(context, bluetoothSocket, infoCall);
            bleAquireCard.setLongTime(isLongTime);
            bleAquireCard.connectCall(new connectCall() {
                @Override
                public void onConnectFiled() {
                    LogUtil.e(tag,"重新连接！");
                    if (isAutoConnect)
                        open(deviceName);
                }
            });
            bleAquireCard.start();
//            final BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(bluetoothAdapter.getName(), bleDevice.getUuids()[0].getUuid());
//            if (serverSocket != null) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            try {
//                                Log.e(tag, " 开始阻塞 "+bleDevice.getName() + " uuid "+bleDevice.getUuids()[0].getUuid() );
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context, " 开始阻塞 "+bleDevice.getName() + " uuid "+bleDevice.getUuids()[0].getUuid() , Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                                final BluetoothSocket accept = serverSocket.accept();
//                                Log.e(tag, " 有蓝牙socket 连接了： " + accept.getRemoteDevice().getName());
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(context, " 有蓝牙socket 连接了： " + accept.getRemoteDevice().getName(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).scan();
//            }
        } catch ( Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, "   socket 异常  ：  " + e.toString(), Toast.LENGTH_LONG).show();
            Log.e(tag, " 异常 : " + e.toString());
        }
    }

    public void close() {
        if (bluetoothReceiver != null)
            try {
                context.unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (bleAquireCard != null && !bleAquireCard.isInterrupted()) {
            bleAquireCard.interrupt();
        }
    }

    public void setAutoStart(boolean a) {
        isAuto = a;
    }

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public static void checkBleDevice(Context context) {
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

    String deviceName;

    public void open(String string) {
        if (TextUtils.isEmpty(string))
            return;
        deviceName = string;

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = iterator.next();
            if (device.getName().equals(string)) {
                connectSecket(device);
                return;
            }
        }
    }


    boolean isLongTime = false;

    public void setLongtime(boolean b) {
        if (bleAquireCard != null)
            bleAquireCard.setLongTime(b);
    }

    public static BluetoothHelper getInstance(Activity context, BleSerchCall blecall, ReadInfoCall infoCall) {
        if (bluetoothHelper == null)
            bluetoothHelper = new BluetoothHelper(context, blecall, infoCall);
        return bluetoothHelper;
    }

    interface connectCall {
        void onConnectFiled();
    }
}
