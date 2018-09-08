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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Tao on 2018/7/21 0021.
 */

public abstract class Sdt {



    /**
     *
     * @param pucSendData       发送数据
     * @param uiSendLen         发送数据长度
     * @param RecvData          返回数据
     * @param puiRecvLen        返回数据长度
     * @return
     */
  abstract   int usbsendrecv(byte[] pucSendData, int uiSendLen, byte[] RecvData, int[] puiRecvLen) throws IOException;

    /**
     *     对数据 进行处理 取出数据内容
     * @param RecvData          接收取出后的数据
     * @param puiRecvLen        取出数据长度
     * @param pucBufRecv        原始数据
     * @param iOffset           数据偏移量 长度为1  int[] iOffset = new int[1];
     * @param dataLenth         原始数据长度
     * @return
     */

   abstract public int prepareReceiveData(byte[] RecvData, int[] puiRecvLen, byte[] pucBufRecv, int[] iOffset, int dataLenth) ;

    // 数据偏移

    /**
     *
     * @param dataBuffer   校验数据
     * @param iOffset     存放数据偏移位
     * @return          false 头部数据偏移量大于6
     */


  abstract public  boolean Usb_GetDataOffset(byte[] dataBuffer, int[] iOffset) ;
    public void writefile(String context) {}
    public int  init ( Context context) throws Exception {return 0;}

    public abstract void close() throws Exception;
}
