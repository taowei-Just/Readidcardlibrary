package com.it_tao_idcard.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.identity.Communication;
import com.identity.Shell;
import com.identity.Util;
import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;
import com.it_tao_idcard.utils.LogUtil;
import com.it_tao_idcard.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import sdses.id2piclib.wltToBmpInterface;

import static android.R.attr.name;

/**
 * Created by Tao on 2018/7/27 0027.
 */

public class BleAquireCard extends Thread {

    int reConnectCount = 3;
    BluetoothSocket bluetoothSocket;
    String TAG = getClass().getSimpleName();
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean bStop;
    private boolean bRet;
    private byte[] cardInfo;
    private String data;
    Context context;
    ReadInfoCall infoCall;
    boolean isLongTime  =false ;
    private BluetoothHelper.connectCall connectCall;

    public BleAquireCard(Context context, BluetoothSocket bleSocket) {
        this.context = context;
        bluetoothSocket = bleSocket;
    }

    public BleAquireCard(Context context, BluetoothSocket bluetoothSocket, ReadInfoCall infoCall) {
        this(context, bluetoothSocket);
        this.infoCall = infoCall;
    }

    @Override
    public void run() {
        try {
            if (connect()) {
                try {
                    inputStream = bluetoothSocket.getInputStream();
                    outputStream = bluetoothSocket.getOutputStream();
                    while (!isInterrupted()) {
                        long start = System.currentTimeMillis();
                        readStatue();
                        LogUtil.e(TAG, " 耗时：" + (System.currentTimeMillis() - start));
                        Thread.sleep(300);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeConn();
                    LogUtil.e(TAG, " IOException " + e.toString());
                    this.interrupted();
                    if (connectCall!=null)
                        connectCall.onConnectFiled();
                }
            } else {
                closeConn();
                this.interrupted();
                LogUtil.e(TAG, "连接 失败 ");
                if (connectCall!=null)
                    connectCall.onConnectFiled();
            }
        } catch (Exception e) {
            closeConn();
            e.printStackTrace();
            LogUtil.e(TAG, " Exception " + e.toString());
        }
    }

    private void closeConn() {
        try {
            bluetoothSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readStatue() throws IOException {
        if (!findCard())
            return;
        LogUtil.e(TAG, "找卡成功 ：");
        if (!selectCard())
            return;
        LogUtil.e(TAG, "选卡成功 ：");
        //读卡
            preaseCardInfo(readCard());
    }

    private  byte[] readCard() throws IOException {
        byte[] sendByte;
        String string;
        byte[] data;
        sendByte = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x30, (byte) 0x10, (byte) 0x23, (byte) 0x00};
        outputStream.write(sendByte);
        string = StringUtils.bytesToHexString(sendByte);
        LogUtil.e(TAG, "发送读卡指令：" + string);
        data = new byte[4094];
        receiver(data, 2321, 4 * 1000);
        string = StringUtils.bytesToHexString(data);
        LogUtil.e(TAG, "收到回复：" + string);
        if (data[9] != 144 && data[9] != -112) {
            return null;
        }
            LogUtil.e(TAG, "读到身份证信息啦 ：");
          return data;
    }

    private boolean selectCard() throws IOException {
        byte[] sendByte;
        String string;
        byte[] data;//选卡
        sendByte = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x02, (byte) 0x21, (byte) 0x00};
        outputStream.write(sendByte);
        string = StringUtils.bytesToHexString(sendByte);
        LogUtil.e(TAG, "发送选卡指令：" + string);
        data = new byte[4094];
        receiver(data, 19, 1000);
        string = StringUtils.bytesToHexString(data);
        LogUtil.e(TAG, "收到回复：" + string);

        if (data[9] != 144 && data[9] != -112)
            return false;
        return true;
    }

    private boolean findCard() throws IOException {
        byte[] sendByte = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x22, (byte) 0x00};
        outputStream.write(sendByte);
        String string = StringUtils.bytesToHexString(sendByte);
        LogUtil.e(TAG, "发送找卡指令：" + string);
        byte[] data = new byte[4094];
        receiver(data, 15, 1000);
        string = StringUtils.bytesToHexString(data);
        LogUtil.e(TAG, "收到回复：" + string);
        if (data[9] != 159 && data[9] != -97)
            return false;
        return true;
    }

    private void preaseCardInfo(byte[] data) {
        if (data == null)
            return;
        byte[] txt = new byte[256];
        byte[] BMPDATA = new byte[1024];
        // 文字
        System.arraycopy(data, 16, txt, 0, 256);
        // 图片
        System.arraycopy(data, 272, BMPDATA, 0, 1024);
        try {
            int i =  GetCardTypeFlag(data);
            LogUtil.e(TAG, " 身份证版本 " + i);
            if (i != 0)
                return;
            String name = getName(txt);
            String gender = GetGender(txt);
            String national = GetNational(txt);
            String birthday = GetBirthday(txt);
            String address = GetAddress(txt);
            String card = GetIndentityCard(txt);
            String issued = GetIssued(txt);
            String startDate = GetStartDate(txt);
            String endDate = GetEndDate(txt);

            LogUtil.e(TAG, " 名称 " + name);
            LogUtil.e(TAG, " 性别 " + gender);
            LogUtil.e(TAG, " 民族 " + national);
            LogUtil.e(TAG, " 生日 " + birthday);
            LogUtil.e(TAG, " 地址 " + address);
            LogUtil.e(TAG, " 身份证号 " + card);
            LogUtil.e(TAG, " 签发机关 " + issued);
            LogUtil.e(TAG, " 起始有效期 " + startDate);
            LogUtil.e(TAG, " 终止有效期 " + endDate);

            Bitmap bitmip = getBitmip(BMPDATA);
            IDcardInfo cardInfo = new IDcardInfo();
            cardInfo.setName(name);
            cardInfo.setSex(gender);
            cardInfo.setNation(national);
            cardInfo.setBirth(birthday);
            cardInfo.setAddress(address);
            cardInfo.setIDNo(card);
            cardInfo.setDepartment(card);
            cardInfo.setEffectDate(startDate);
            cardInfo.setExpireDate(endDate);
            cardInfo.setBm(bitmip);
            if (infoCall!=null)
                infoCall.onReadSuccess(cardInfo);
            if (!isLongTime)
                this.interrupted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int GetCardTypeFlag(byte[] cardInfo) throws IOException {
        String typeFlag = "";
        if(cardInfo.length > 250) {
            byte[] type = new byte[2];
            Util.memcpy(type, 0, cardInfo, 248, 2);
            typeFlag = new String(type, "UTF-16LE");
            return typeFlag.endsWith("I")?1:0;
        } else {
            return -1;
        }
    }

    private Bitmap getBitmip(byte[] bmpdata) {
        byte[] bmpBuff = new byte[38862];
        int ret = GetPicByBuff(context.getPackageName(), bmpdata, bmpBuff);
        LogUtil.e(TAG, "ret " + ret);
        if (ret == 1)
            return BitmapFactory.decodeByteArray(bmpBuff, 0, 38862);
        else
            return null;
    }
    // 名称
    private String getName(byte[] txt) {
        byte[] name = new byte[30];
        System.arraycopy(txt, 0, name, 0, 30);
        try {
            return new String(name, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    // 性别
    private String GetGender(byte[] txt) {
        byte[] gender = new byte[2];
        System.arraycopy(txt, 30, gender, 0, 2);
        try {
            return Integer.valueOf(new String(gender, "UTF-16LE")) == 1 ? "男" : "女";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void receiver(byte[] data, int lenl, long time) throws IOException {
        long startTime = System.currentTimeMillis();
        byte[] recieveData = new byte[1];
        int len = 0;
        int totalLen = 0;
        while ((System.currentTimeMillis() - startTime) < time && totalLen < lenl) {
            if ((len = inputStream.available()) <= 0)
                continue;
            recieveData = new byte[len];
            inputStream.read(recieveData, 0, len);
            System.arraycopy(recieveData, 0, data, totalLen, len);
            totalLen += len;
        }
        LogUtil.e(TAG, " totalLen  " + totalLen);
    }

    public int read(byte[] buffer, int len, long timeOut) {
        int bytes = 0;
        int ilen = 0;
        if (inputStream == null) {
            return -1;
        } else {
            long start = System.currentTimeMillis();
            while (bytes != len && System.currentTimeMillis() - start < timeOut) {
                try {
                    if ((ilen = inputStream.available()) > 0 && len - bytes > 0) {
                        bytes += ilen;
                        byte[] buf = new byte[ilen];
                        inputStream.read(buf, 0, ilen);
                        System.arraycopy(buf, 0, buffer, bytes, ilen);
                    }
                } catch (IOException var9) {
                    var9.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException var8) {
                    var8.printStackTrace();
                }
            }
            String hexString = StringUtils.bytesToHexString(buffer);
            Log.e(TAG, "  读取数据 ：" + hexString);
            return bytes;
        }
    }

    int aleradyCount = 0;

    private boolean connect() {
        if (bluetoothSocket == null)
            return false;
        try {
            aleradyCount++;
            bluetoothSocket.connect();
            if (bluetoothSocket.isConnected()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (aleradyCount < reConnectCount) {
            return connect();
        } else {
            bluetoothSocket = null;
            return false;
        }
    }


    public String GetNational(byte[] data) throws UnsupportedEncodingException {
        byte[] national = new byte[4];
        Util.memcpy(national, 0, data, 32, 4);
        return this.GetNationalFromCode(new String(national, "UTF-16LE"));
    }

    public String GetBirthday(byte[] data) throws UnsupportedEncodingException {
        byte[] birthday = new byte[16];
        Util.memcpy(birthday, 0, data, 36, 16);
        String sBirthday = new String(birthday, "UTF-16LE");
        return String.format("%s年%s月%s日", new Object[]{sBirthday.substring(0, 4), sBirthday.substring(4, 6), sBirthday.substring(6, 8)});
    }

    public String GetAddress(byte[] data) throws UnsupportedEncodingException {
        byte[] address = new byte[70];
        Util.memcpy(address, 0, data, 52, 70);
        return (new String(address, "UTF-16LE")).trim();
    }

    public String GetIndentityCard(byte[] data) throws UnsupportedEncodingException {
        byte[] identity = new byte[36];
        Util.memcpy(identity, 0, data, 122, 36);
        return (new String(identity, "UTF-16LE")).trim();
    }

    public String GetIssued(byte[] data) throws UnsupportedEncodingException {
        byte[] issued = new byte[30];
        Util.memcpy(issued, 0, data, 158, 30);
        return (new String(issued, "UTF-16LE")).trim();
    }

    public String GetStartDate(byte[] data) throws UnsupportedEncodingException {
        byte[] start = new byte[16];
        Util.memcpy(start, 0, data, 188, 16);
        String sStart = new String(start, "UTF-16LE");
        return String.format("%s年%s月%s日", new Object[]{sStart.substring(0, 4), sStart.substring(4, 6), sStart.substring(6, 8)});
    }

    public String GetEndDate(byte[] data) throws UnsupportedEncodingException {
        byte[] end = new byte[16];
        Util.memcpy(end, 0, data, 204, 16);
        String sEnd = new String(end, "UTF-16LE");
        String mEnd = "";
        String strTem = sEnd.substring(0, 4);
        if (sEnd.trim().length() < 5) {
            mEnd = strTem;
        } else {
            mEnd = String.format("%s年%s月%s日", new Object[]{sEnd.substring(0, 4), sEnd.substring(4, 6), sEnd.substring(6, 8)});
        }

        Log.w("777", "mEnd is:" + mEnd);
        return mEnd;
    }

    public int GetPicByBuff(String packageName, byte[] wltBuff, byte[] bmpBuff) {
        Log.w("ComShell", "picUnpack start");
        String head = "424DCE970000000000003600000028000000660000007E00000001001800000000000000000000000000000000000000000000000000";
        byte[] tmpBmpBuff1 = new byte['韎'];
        byte[] tmpBmpBuff2 = new byte['韎'];
        byte[] headbuff = new byte[54];
        headbuff = Util.hexStringToBytes(head);
        int nRet = wltToBmpInterface.wltToBmp(packageName, wltBuff, tmpBmpBuff1);
        Log.w("ComShell", "picUnpack end " + nRet);
        if (nRet != 1) {
            nRet = wltToBmpInterface.wltToBmp(packageName, wltBuff, tmpBmpBuff1);
        }

        int hLen = head.length() / 2;
        System.arraycopy(headbuff, 0, bmpBuff, 0, hLen);

        int i;
        for (i = 0; i < 12954; ++i) {
            tmpBmpBuff2[i * 3 + 0] = tmpBmpBuff1[i * 3 + 2];
            tmpBmpBuff2[i * 3 + 1] = tmpBmpBuff1[i * 3 + 1];
            tmpBmpBuff2[i * 3 + 2] = tmpBmpBuff1[i * 3 + 0];
        }

        for (i = 0; i < 126; ++i) {
            System.arraycopy(tmpBmpBuff2, i * 306, bmpBuff, hLen + i * 308, 306);
            bmpBuff[hLen + i * 308 + 306 + 0] = 0;
            bmpBuff[hLen + i * 308 + 306 + 1] = 0;
        }

        Log.w("ComShell", "picUnpack nRet is:" + nRet);
        return nRet;
    }


    private String GetNationalFromCode(String nationalCode) {
        int n = Integer.valueOf(nationalCode).intValue();
        switch (n) {
            case 1:
                return "汉族";
            case 2:
                return "蒙古族";
            case 3:
                return "回族";
            case 4:
                return "藏族";
            case 5:
                return "维吾尔族";
            case 6:
                return "苗族";
            case 7:
                return "彝族";
            case 8:
                return "壮族";
            case 9:
                return "布依族";
            case 10:
                return "朝鲜族";
            case 11:
                return "满族";
            case 12:
                return "侗族";
            case 13:
                return "瑶族";
            case 14:
                return "白族";
            case 15:
                return "土家族";
            case 16:
                return "哈尼族";
            case 17:
                return "哈萨克族";
            case 18:
                return "傣族";
            case 19:
                return "黎族";
            case 20:
                return "傈僳族";
            case 21:
                return "佤族";
            case 22:
                return "畲族";
            case 23:
                return "高山族";
            case 24:
                return "拉祜族";
            case 25:
                return "水族";
            case 26:
                return "东乡族";
            case 27:
                return "纳西族";
            case 28:
                return "景颇族";
            case 29:
                return "柯尔克孜族";
            case 30:
                return "土族";
            case 31:
                return "达斡尔族";
            case 32:
                return "仫佬族";
            case 33:
                return "羌族";
            case 34:
                return "布朗族";
            case 35:
                return "撒拉族";
            case 36:
                return "毛难族";
            case 37:
                return "仡佬族";
            case 38:
                return "锡伯族";
            case 39:
                return "阿昌族";
            case 40:
                return "普米族";
            case 41:
                return "塔吉克族";
            case 42:
                return "怒族";
            case 43:
                return "乌孜别克族";
            case 44:
                return "俄罗斯族";
            case 45:
                return "鄂温克族";
            case 46:
                return "崩龙族";
            case 47:
                return "保安族";
            case 48:
                return "裕固族";
            case 49:
                return "京族";
            case 50:
                return "塔塔尔族";
            case 51:
                return "独龙族";
            case 52:
                return "鄂伦春族";
            case 53:
                return "赫哲族";
            case 54:
                return "门巴族";
            case 55:
                return "珞巴族";
            case 56:
                return "基诺族";
            default:
                return "其他";
        }
    }


    public void setLongTime( boolean b) {
        isLongTime =b;
    }

    public void connectCall(BluetoothHelper.connectCall connectCall) {
       this.connectCall =connectCall ;
    }
}
