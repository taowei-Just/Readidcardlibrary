 
package com.sdt;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;

import com.it_tao_idcard.ReadIDCardInform.utils.LogUtil;
import com.it_tao_idcard.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static android.R.id.message;

public class Sdtusbapi extends  Sdt {

   public Common common = new Common();
    int debug = 1;
    UsbDeviceConnection mDeviceConnection;
    UsbEndpoint epOut;
    UsbEndpoint epIn;
    final String FILE_NAME = "/file1.txt";
    RandomAccessFile raf;
    File targetFile;
    Context instance;
    private String TAG = getClass().getName();

    public Sdtusbapi( ) {

    }

    public Sdtusbapi(Context instance) throws Exception {
        int ret = this.initUSB(instance);
        this.instance = instance;
        if(this.debug == 1) {
            this.writefile("inintUSB ret=" + ret);
        }

        if(ret != this.common.SUCCESS) {
            Exception e = new Exception();
            if(ret == this.common.ENOUSBRIGHT) {
                e.initCause(new Exception());
                this.writefile("error common.ENOUSBRIGHT");
            } else {
                e.initCause((Throwable)null);
                this.writefile("error null");
            }

            throw e;
        }
    }

    int initUSB(Context instance) {
        this.openfile();
        UsbDevice mUsbDevice = null;
        UsbManager manager = (UsbManager)instance.getSystemService(Context.USB_SERVICE);
       
        if(manager == null) {
            this.writefile("manager == null");
            return this.common.EUSBMANAGER;
        } else {
            if(this.debug == 1) {
                this.writefile("usb dev：" + manager.toString());
            }

            HashMap deviceList = manager.getDeviceList();
            if(this.debug == 1) {
                this.writefile("usb dev：" + String.valueOf(deviceList.size()));
            }

            Iterator deviceIterator = deviceList.values().iterator();
            ArrayList USBDeviceList = new ArrayList();

            while(deviceIterator.hasNext()) {
                UsbDevice device = (UsbDevice)deviceIterator.next();
                USBDeviceList.add(String.valueOf(device.getVendorId()));
                USBDeviceList.add(String.valueOf(device.getProductId()));
                if(device.getVendorId() == 1024 && device.getProductId() == '썚') {
                    LogUtil.e(TAG, " device.getProductId()  " +device.getProductId());
                    mUsbDevice = device;
                    if(this.debug == 1) {
                        this.writefile("zhangmeng:find device!");
                    }
                }
            }

            int ret = this.findIntfAndEpt(manager, mUsbDevice);
            return ret;
        }
    }


    /**
     *
     * @param pucSendData       发送数据
     * @param uiSendLen         发送数据长度
     * @param RecvData          返回数据
     * @param puiRecvLen        返回数据长度
     * @return
     */
    int usbsendrecv(byte[] pucSendData, int uiSendLen, byte[] RecvData, int[] puiRecvLen) {
        byte iFD = 0;
        Boolean bRet = null;
        byte ucCheck = 0;
        byte[] ucRealSendData = new byte[4096];
        byte[] pucBufRecv = new byte[4096];
        int[] iOffset = new int[1];
        if(4091 < uiSendLen) {
            return -1;
        } else {
            int iRet;
            if(-1 == iFD) {
                iRet = this.common.ENOOPEN;
                return iRet;
            } else {
                int iLen = (pucSendData[0] << 8) + pucSendData[1]; // 03

                LogUtil.e(TAG , " len  "  + iLen);
                ucRealSendData[0] = ucRealSendData[1] = ucRealSendData[2] = -86; //AA
                ucRealSendData[3] = -106; // 96
                ucRealSendData[4] = 105;  //69
                // 校验位
                for(int iIter = 0; iIter < iLen + 1; ++iIter) {
                    ucCheck ^= pucSendData[iIter];
                }
//                LogUtil.e(TAG , " check  "  + Integer.toHexString(ucCheck));
                int uiSizeSend;
                for(uiSizeSend = 0; uiSizeSend < iLen + 2; ++uiSizeSend) {
                    //6-9
                    ucRealSendData[uiSizeSend + 5] = pucSendData[uiSizeSend];
//                    LogUtil.e(TAG , " data  " + Integer.toHexString(pucSendData[uiSizeSend]));
                }
                ucRealSendData[iLen + 6] = ucCheck;
                uiSizeSend = iLen + 2 + 5;
                boolean uiSizeRecv = false;
                LogUtil.e(TAG , " uiSizeSend  " +uiSizeSend);

//                this.writefile(" 发送数据 " + StringUtils.byte2hex( ucRealSendData));
//                LogUtil.e(TAG , " 发送数据   data" );

                iRet = this.mDeviceConnection.bulkTransfer(this.epOut, ucRealSendData, uiSizeSend, 2000);

                this.writefile("before uiSizeRecv error iRet=" + iRet);

                int dataLenth = this.mDeviceConnection.bulkTransfer(this.epIn, pucBufRecv, pucBufRecv.length, 5000);

                 //  this.writefile(" 接收数据 " + StringUtils.byte2hex( pucBufRecv));

                return prepareReceiveData(RecvData, puiRecvLen, pucBufRecv, iOffset, dataLenth);
            }
        }
    }

    /**
     *     对数据 进行处理 取出数据内容
     * @param RecvData          接收取出后的数据
     * @param puiRecvLen        取出数据长度
     * @param pucBufRecv        原始数据
     * @param iOffset           数据偏移量 长度为1  int[] iOffset = new int[1];
     * @param dataLenth         原始数据长度
     * @return
     */

    public int prepareReceiveData(byte[] RecvData, int[] puiRecvLen, byte[] pucBufRecv, int[] iOffset, int dataLenth) {
        Boolean bRet;
        int iRet;
        int iLen;
        if(5 <= dataLenth && 4096 > dataLenth) {

            bRet = Boolean.valueOf(this.Usb_GetDataOffset(pucBufRecv, iOffset));

            if(!bRet.booleanValue()) {
                iRet = this.common.EDATAFORMAT;
                this.writefile("iRet = EDATAFORMAT =" + bRet + "iOffset= " + iOffset);
                return iRet;
            } else {
                //
                iLen = (pucBufRecv[iOffset[0] + 4] << 8) + pucBufRecv[iOffset[0] + 5];

                if(4089 < iLen) {
                    iRet = this.common.EDATALEN;
                    this.writefile("iRet = EDATALEN = " + iLen);
                    return iRet;
                } else {
                    byte[] tempData = new byte[4096];

                    int i;
                    for(i = 0; i < pucBufRecv.length - iOffset[0] - 4; ++i) {
                        tempData[i] = pucBufRecv[i + iOffset[0] + 4];
                    }

                    bRet = Boolean.valueOf(Usb_CheckChkSum(iLen + 2, tempData));

                    if(!bRet.booleanValue()) {
                        iRet = this.common.EPCCRC;
                        this.writefile("iRet = EPCCRC");
                        return iRet;
                    } else {
                        for(i = 0; i < iLen + 1; ++i) {
                            RecvData[i] = pucBufRecv[i + iOffset[0] + 4];
                        }

                        puiRecvLen[0] = iLen + 1;
                        this.writefile("stdapi.puiRecvLen =" + (iLen + 1));
                        return this.common.SUCCESS;
                    }
                }
            }
        } else {
            iRet = this.common.EDATALEN;
            this.writefile("uiSizeRecv error =" + dataLenth);
            return iRet;
        }
    }


    // 数据偏移

    /**
     *
     * @param dataBuffer   校验数据
     * @param iOffset     存放数据偏移位
     * @return          false 头部数据偏移量大于6
     */


    public boolean Usb_GetDataOffset(byte[] dataBuffer, int[] iOffset) {
        iOffset[0] = 0;
        int iIter;
        for(iIter = 0; iIter < 7 && (dataBuffer[iIter + 0] != -86 || dataBuffer[iIter + 1] != -86 || dataBuffer[iIter + 2] != -106 || dataBuffer[iIter + 3] != 105); ++iIter) {
        }
        if(7 <= iIter) {
            return false;
        } else {
            iOffset[0] = iIter;
            return true;
        }
    }

    // 抑或运算
    static boolean Usb_CheckChkSum(int uiDataLen, byte[] pucRecvData) {
        byte ucCheck = 0;

        for(int iIter = 0; iIter < uiDataLen - 1; ++iIter) {
            ucCheck ^= pucRecvData[iIter];
        }
        return ucCheck == pucRecvData[uiDataLen - 1];
    }

    private void openfile() {
        if(this.debug == 1) {
            File sdCardDir = Environment.getExternalStorageDirectory();

            try {
                File f = new File(sdCardDir.getCanonicalPath() + "/file.txt");
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
                this.setTargetFile(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                
                this.setFile(new RandomAccessFile(this.targetFile, "rw"));
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            this.writefile("in open file()");
        }

    }

    public void writefile(String context) {
        if(this.debug == 1 && Environment.getExternalStorageState().equals("mounted")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
           LogUtil.e(TAG ,  "指令输出 ： " + context);
            try {
                FileOutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + FILE_NAME, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write((" \n " + sdf.format(new Date()) + " \n " + context));
                outputStreamWriter.flush();
                outputStreamWriter.close();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void closefile() {
        if(this.debug == 1 && this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    private int findIntfAndEpt(final UsbManager manager, final UsbDevice mUsbDevice) {
        UsbInterface mInterface = null;
        if(mUsbDevice == null) {
            this.writefile("zhangmeng:no device found");
            return this.common.EUSBDEVICENOFOUND;
        } else {
            byte connection = 0;
            if(connection < mUsbDevice.getInterfaceCount()) {
                UsbInterface intf = mUsbDevice.getInterface(connection);
                mInterface = intf;
            }

            if(mInterface != null) {
                UsbDeviceConnection connection1 = null;
                if(manager.hasPermission(mUsbDevice)) {
                    connection1 = manager.openDevice(mUsbDevice);
                    if(connection1 == null) {
                        return this.common.EUSBCONNECTION;
                    } else {
                        if(connection1.claimInterface(mInterface, true)) {
                            this.mDeviceConnection = connection1;
                            this.getEndpoint(this.mDeviceConnection, mInterface);
                        } else {
                            connection1.close();
                            return this.common.EUSBCONNECTION;
                        }

                        return this.common.SUCCESS;
                    }
                } else {
                    this.writefile("zhangmeng:no rights");
                    (new Thread() {
                        public void run() {
                            try {

                                Context var10000 = Sdtusbapi.this.instance;
                                Sdtusbapi.this.common.getClass();
                                PendingIntent pi = PendingIntent.getBroadcast(var10000, 0, new Intent("com.android.USB_PERMISSION"), 0);
                                manager.requestPermission(mUsbDevice, pi);
                            }catch (Exception e){
                                
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    return this.common.ENOUSBRIGHT;
                }
            } else {
                this.writefile("zhangmeng:no interface");
                return this.common.ENOUSBINTERFACE;
            }
        }
    }

    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if(intf.getEndpoint(1) != null) {
            this.epOut = intf.getEndpoint(1);
        }

        if(intf.getEndpoint(0) != null) {
            this.epIn = intf.getEndpoint(0);
        }

    }

    private void setFile(RandomAccessFile raf) {
        this.raf = raf;
    }

    private void setTargetFile(File f) {
        this.targetFile = f;
    }
    
    public int  init ( Context context) throws Exception {
        this.instance = context ;
        int ret = initUSB(context);
        

        if(ret != this.common.SUCCESS) {
            Exception e = new Exception();
            if(ret == this.common.ENOUSBRIGHT) {
                e.initCause(new Exception());
                this.writefile("error common.ENOUSBRIGHT");
            } else {
                e.initCause((Throwable)null);
                this.writefile("error null");
            }
            throw e;
        }
        
        return ret ;
    }

    @Override
    public void close() {
        this.mDeviceConnection.close();
    }
}
