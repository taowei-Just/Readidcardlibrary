package com.hdos.usbdevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;

import com.it_tao_idcard.ReadIDCardInform.helper.IDCardSacnHelper;
import com.it_tao_idcard.utils.LogUtil;
import com.sdt.Api;
import com.sdt.Sdtapi;
import com.sdt.SerialSdtapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class publicSecurityIDCardLib {
    private Context context;
    public boolean findloop = true;
    public Api sdtapi;
    String tag = getClass().getSimpleName() ;
    static {
        System.loadLibrary("HdosIdentityCard");
    }
    private IDCardSacnHelper.Mode mode = IDCardSacnHelper.Mode.USB;
    public final native byte[] HdosIdUnpack(String var1, byte[] var2);
    public final native int hdos(int var1);

    public publicSecurityIDCardLib() {
    }

    public publicSecurityIDCardLib(IDCardSacnHelper.Mode mode) throws Exception {
        this.mode = mode;
        try {
            if (mode == IDCardSacnHelper.Mode.USB){
                sdtapi = new Sdtapi();
            }else if (mode == IDCardSacnHelper.Mode.Serial){
                sdtapi = new SerialSdtapi();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            LogUtil.e(tag, var2.toString());
        }
    }

    public publicSecurityIDCardLib(Context context , IDCardSacnHelper.Mode mode) {
        this.context = context;
        try {

            if (mode == IDCardSacnHelper.Mode.USB){
                LogUtil.e(tag, " usb mode");
                sdtapi = new Sdtapi(context);
            }else if (mode == IDCardSacnHelper.Mode.Serial){
                LogUtil.e(tag, " Serial mode");

                sdtapi = new SerialSdtapi(context);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            LogUtil.e("publicSecurityIDCardLib", var2.toString());
        }
        IntentFilter context1;
        (context1 = new IntentFilter()).addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
//    this.context.registerReceiver(this.receiver, context1);
    }

    public final int getSAMStatus() throws IOException {
        return this.sdtapi.SDT_GetSAMStatus();
    }

    public final int readBaseMsgToStr(String pkName, byte[] BmpFile, byte[] pName, byte[] pSex, byte[] pNation, byte[] pBirth, byte[] pAddress, byte[] pIDNo, byte[] pDepartment, byte[] pEffectDate, byte[] pExpireDate)  throws IOException{
        this.sdtapi.SDT_StartFindIDCard();
        this.sdtapi.SDT_SelectIDCard();
        return this.ReadBaseMsgToStr(pkName, BmpFile, pName, pSex, pNation, pBirth, pAddress, pIDNo, pDepartment, pEffectDate, pExpireDate);
    }

    public final int readBaseMsgToStr(String pkName, byte[] BmpFile, byte[] Fpmsg, byte[] pName, byte[] pSex, byte[] pNation, byte[] pBirth, byte[] pAddress, byte[] pIDNo, byte[] pDepartment, byte[] pEffectDate, byte[] pExpireDate)  throws IOException{
        this.sdtapi.SDT_StartFindIDCard();
        this.sdtapi.SDT_SelectIDCard();
        return this.ReadBaseMsgToStr(pkName, BmpFile, Fpmsg, pName, pSex, pNation, pBirth, pAddress, pIDNo, pDepartment, pEffectDate, pExpireDate);
    }

    public final int MessageTstr(byte[] data, String pkName,
                                 byte[] BmpFile,
                                 byte[] pName,
                                 byte[] pSex,
                                 byte[] pNation,
                                 byte[] pBirth,
                                 byte[] pAddress,
                                 byte[] pIDNo,
                                 byte[] pDepartment,
                                 byte[] pEffectDate,
                                 byte[] pExpireDate)  throws IOException{

        byte[] recvdata = new byte[3072];
        int[] RecvLen = new int[1];
        int[] iOffset = new int[1];

        int ret = sdtapi.usbapi.prepareReceiveData(recvdata, RecvLen, data, iOffset, data.length);


        int[]  puiCHMsgLen = new int[1];
        byte[] pucPHMsg = new byte[1024];

        int[] puiPHMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];

        puiCHMsgLen[0] = 0;
        pucPHMsg[0] = 0;

        if (ret != 144) {
            return ret;
        } else {

            if (RecvLen[0] - 5 > 0 && RecvLen[0] > 0 && recvdata[4] == -112) {
                puiCHMsgLen[0] = recvdata[5] * 256 + recvdata[6];
                puiPHMsgLen[0] = recvdata[7] * 256 + recvdata[8];
                if (puiCHMsgLen[0] > 256) {
                    puiCHMsgLen[0] = 256;
                }

                if (puiPHMsgLen[0] > 1024) {
                    puiPHMsgLen[0] = 1024;
                }

                int i;
                for (i = 0; i < puiCHMsgLen[0]; ++i) {
                    pucCHMsg[i] = recvdata[i + 9];
                }

                for (i = 0; i < puiPHMsgLen[0]; ++i) {
                    pucPHMsg[i] = recvdata[i + 9 + puiCHMsgLen[0]];
                }
            }

            int byte2int = sdtapi.byte2int(recvdata[4]);


            byte [] sexCode ;

            sexCode = pucPHMsg ;

            if (byte2int != 144 )
                return  byte2int ;


            byte[] zeroArray;
            Arrays.fill(zeroArray = new byte[1025], (byte) 0);
            System.arraycopy(sexCode, 0, zeroArray, 0, 1024);
            pkName = "/data/data/" + pkName + "/lib/libwlt2bmp1.so";
            byte[] dataBuff;
            // 处理图片
            if ((dataBuff = this.HdosIdUnpack(pkName, zeroArray)) != null) {
                byte var23;
                int index;
                for (index = 0; index < 19278; ++index) {
                    var23 = dataBuff[index];
                    dataBuff[index] = dataBuff['際' - index];
                    dataBuff['際' - index] = var23;

//                    LogUtil.e("際", "際" + '際');
                }
                index = 0;
                while (true) {
                    if (index >= 126) {
                        System.arraycopy(dataBuff, 0, BmpFile, 0, '障');
                        break;
                    }
                    for (int var16 = 0; var16 < 153; ++var16) {
                        var23 = dataBuff[var16 + index * 102 * 3];
                        dataBuff[var16 + index * 102 * 3] = dataBuff[305 - var16 + index * 102 * 3];
                        dataBuff[305 - var16 + index * 102 * 3] = var23;
                    }
                    ++index;
                }
            }
            try {
                (zeroArray = new byte[32])[0] = -1;
                zeroArray[1] = -2;
                System.arraycopy(pucCHMsg, 0, zeroArray, 2, 30);
                System.arraycopy(zeroArray, 0, pName, 0, 32);


                sexCode = new byte[2];
                // 性别
                System.arraycopy(pucCHMsg, 30, sexCode, 0, 2);
                if (sexCode[0] == 49) {
                    sexCode = "男".getBytes("Unicode");
                } else if (sexCode[0] == 50) {
                    sexCode = "女".getBytes("Unicode");
                } else {
                    sexCode = "其他".getBytes("Unicode");
                }
                // 性别
                System.arraycopy(sexCode, 0, pSex, 0, sexCode.length);
                nation(pNation, pucCHMsg);


                int nationCode;

                dataBuff = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(pucCHMsg, 36, dataBuff, 0, dataBuff.length);

                int var20;
                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                // 生日
                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pBirth, 0, dataBuff.length);
                (pName = new byte[72])[0] = -1;
                pName[1] = -2;
                // 名称
                System.arraycopy(pucCHMsg, 52, pName, 2, pName.length - 2);
                // 地址
                System.arraycopy(pName, 0, pAddress, 0, pName.length);
                dataBuff = new byte[36];
                BmpFile = new byte[18];
                System.arraycopy(pucCHMsg, 122, dataBuff, 0, dataBuff.length);

                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                // 身份证号码
                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pIDNo, 0, dataBuff.length);
                (pName = new byte[32])[0] = -1;
                pName[1] = -2;
                System.arraycopy(pucCHMsg, 158, pName, 2, pName.length - 2);

                // 有效期
                System.arraycopy(pName, 0, pDepartment, 0, pName.length);
                dataBuff = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(pucCHMsg, 188, dataBuff, 0, dataBuff.length);

                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pEffectDate, 0, dataBuff.length);
                pName = new byte[16];
                dataBuff = new byte[8];
                System.arraycopy(pucCHMsg, 204, pName, 0, pName.length);

                for (nationCode = 0; nationCode < dataBuff.length; ++nationCode) {
                    dataBuff[nationCode] = pName[nationCode * 2];
                }

                System.arraycopy(dataBuff = (new String(dataBuff)).getBytes("Unicode"), 0, pExpireDate, 0, dataBuff.length);
            } catch (Exception var17) {
                var17.printStackTrace();
            }

            return byte2int;
        }

    }

    public final int ReadBaseMsgToStr(String pkName,
                                      byte[] BmpFile,
                                      byte[] pName,
                                      byte[] pSex,
                                      byte[] pNation,
                                      byte[] pBirth,
                                      byte[] pAddress,
                                      byte[] pIDNo,
                                      byte[] pDepartment,
                                      byte[] pEffectDate,
                                      byte[] pExpireDate) throws IOException {


        LogUtil.e("ReadBaseMsgToStr", " 读取信息 ");

        int[] var12 = new int[1];
        byte[] pucCHMsg = new byte[256];

        int[] puiPHMsgLen = new int[1];
        byte[] sexCode = new byte[1024];

        int statueCode;
        if ((statueCode = this.sdtapi.SDT_ReadBaseMsg(pucCHMsg, var12, sexCode, puiPHMsgLen)) == 144) {

            byte[] zeroArray;
            Arrays.fill(zeroArray = new byte[1025], (byte) 0);
            System.arraycopy(sexCode, 0, zeroArray, 0, 1024);
            pkName = "/data/data/" + pkName + "/lib/libwlt2bmp1.so";
            byte[] dataBuff;
            // 处理图片
            if ((dataBuff = this.HdosIdUnpack(pkName, zeroArray)) != null) {
                byte var23;
                int index;
                for (index = 0; index < 19278; ++index) {
                    var23 = dataBuff[index];
                    dataBuff[index] = dataBuff['際' - index];
                    dataBuff['際' - index] = var23;

//                    LogUtil.e("際", "際" + '際');
                }
                index = 0;
                while (true) {
                    if (index >= 126) {
                        System.arraycopy(dataBuff, 0, BmpFile, 0, '障');
                        break;
                    }
                    for (int var16 = 0; var16 < 153; ++var16) {
                        var23 = dataBuff[var16 + index * 102 * 3];
                        dataBuff[var16 + index * 102 * 3] = dataBuff[305 - var16 + index * 102 * 3];
                        dataBuff[305 - var16 + index * 102 * 3] = var23;
                    }
                    ++index;
                }
            }
            try {
                (zeroArray = new byte[32])[0] = -1;
                zeroArray[1] = -2;
                System.arraycopy(pucCHMsg, 0, zeroArray, 2, 30);
                System.arraycopy(zeroArray, 0, pName, 0, 32);

                sexCode = new byte[2];
                // 性别
                System.arraycopy(pucCHMsg, 30, sexCode, 0, 2);
                if (sexCode[0] == 49) {
                    sexCode = "男".getBytes("Unicode");
                } else if (sexCode[0] == 50) {
                    sexCode = "女".getBytes("Unicode");
                } else {
                    sexCode = "其他".getBytes("Unicode");
                }
                // 性别
                System.arraycopy(sexCode, 0, pSex, 0, sexCode.length);
                nation(pNation, pucCHMsg);


                int nationCode;

                dataBuff = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(pucCHMsg, 36, dataBuff, 0, dataBuff.length);

                int var20;
                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                // 生日
                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pBirth, 0, dataBuff.length);
                (pName = new byte[72])[0] = -1;
                pName[1] = -2;
                // 名称
                System.arraycopy(pucCHMsg, 52, pName, 2, pName.length - 2);
                // 地址
                System.arraycopy(pName, 0, pAddress, 0, pName.length);
                dataBuff = new byte[36];
                BmpFile = new byte[18];
                System.arraycopy(pucCHMsg, 122, dataBuff, 0, dataBuff.length);

                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                // 身份证号码
                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pIDNo, 0, dataBuff.length);
                (pName = new byte[32])[0] = -1;
                pName[1] = -2;
                System.arraycopy(pucCHMsg, 158, pName, 2, pName.length - 2);

                // 有效期
                System.arraycopy(pName, 0, pDepartment, 0, pName.length);
                dataBuff = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(pucCHMsg, 188, dataBuff, 0, dataBuff.length);

                for (var20 = 0; var20 < BmpFile.length; ++var20) {
                    BmpFile[var20] = dataBuff[var20 * 2];
                }

                System.arraycopy(dataBuff = (new String(BmpFile)).getBytes("Unicode"), 0, pEffectDate, 0, dataBuff.length);
                pName = new byte[16];
                dataBuff = new byte[8];
                System.arraycopy(pucCHMsg, 204, pName, 0, pName.length);

                for (nationCode = 0; nationCode < dataBuff.length; ++nationCode) {
                    dataBuff[nationCode] = pName[nationCode * 2];
                }

                System.arraycopy(dataBuff = (new String(dataBuff)).getBytes("Unicode"), 0, pExpireDate, 0, dataBuff.length);
            } catch (Exception var17) {
                var17.printStackTrace();
            }
        }

        return statueCode;
    }

    private void nation(byte[] pNation, byte[] pucCHMsg) throws UnsupportedEncodingException {
        byte[] dataBuff;
        String pkName;
        byte[] var25 = new byte[4];
        // 民族
        dataBuff = new byte[2];
        System.arraycopy(pucCHMsg, 32, var25, 0, var25.length);

        int nationCode;
        for (nationCode = 0; nationCode < dataBuff.length; ++nationCode) {
            dataBuff[nationCode] = var25[nationCode * 2];
        }

        nationCode = Integer.parseInt(new String(dataBuff));
        pkName = matchNation(nationCode);
        // 籍贯
        System.arraycopy(dataBuff = pkName.getBytes("Unicode"), 0, pNation, 0, dataBuff.length);
    }

    private String matchNation(int nationCode) {
        String pkName = "汉";
        switch (nationCode) {
            case 1:
                pkName = "汉";
                break;
            case 2:
                pkName = "蒙古";
                break;
            case 3:
                pkName = "回";
                break;
            case 4:
                pkName = "藏";
                break;
            case 5:
                pkName = "维吾尔";
                break;
            case 6:
                pkName = "苗";
                break;
            case 7:
                pkName = "彝";
                break;
            case 8:
                pkName = "壮";
                break;
            case 9:
                pkName = "布依";
                break;
            case 10:
                pkName = "朝鲜";
                break;
            case 11:
                pkName = "满";
                break;
            case 12:
                pkName = "侗";
                break;
            case 13:
                pkName = "瑶";
                break;
            case 14:
                pkName = "白";
                break;
            case 15:
                pkName = "土家";
                break;
            case 16:
                pkName = "哈尼";
                break;
            case 17:
                pkName = "哈萨克";
                break;
            case 18:
                pkName = "傣";
                break;
            case 19:
                pkName = "黎";
                break;
            case 20:
                pkName = "傈僳";
                break;
            case 21:
                pkName = "佤";
                break;
            case 22:
                pkName = "畲";
                break;
            case 23:
                pkName = "高山";
                break;
            case 24:
                pkName = "拉祜";
                break;
            case 25:
                pkName = "水";
                break;
            case 26:
                pkName = "东乡";
                break;
            case 27:
                pkName = "纳西";
                break;
            case 28:
                pkName = "景颇";
                break;
            case 29:
                pkName = "柯尔克孜";
                break;
            case 30:
                pkName = "土";
                break;
            case 31:
                pkName = "达斡尔";
                break;
            case 32:
                pkName = "仫佬";
                break;
            case 33:
                pkName = "羌";
                break;
            case 34:
                pkName = "布朗";
                break;
            case 35:
                pkName = "撒拉";
                break;
            case 36:
                pkName = "毛南";
                break;
            case 37:
                pkName = "仡佬";
                break;
            case 38:
                pkName = "锡伯";
                break;
            case 39:
                pkName = "阿昌";
                break;
            case 40:
                pkName = "普米";
                break;
            case 41:
                pkName = "塔吉克";
                break;
            case 42:
                pkName = "怒";
                break;
            case 43:
                pkName = "乌孜别克";
                break;
            case 44:
                pkName = "俄罗斯";
                break;
            case 45:
                pkName = "鄂温克";
                break;
            case 46:
                pkName = "德昂";
                break;
            case 47:
                pkName = "保安";
                break;
            case 48:
                pkName = "裕固";
                break;
            case 49:
                pkName = "京";
                break;
            case 50:
                pkName = "塔塔尔";
                break;
            case 51:
                pkName = "独龙";
                break;
            case 52:
                pkName = "鄂伦春";
                break;
            case 53:
                pkName = "赫哲";
                break;
            case 54:
                pkName = "门巴";
                break;
            case 55:
                pkName = "珞巴";
                break;
            case 56:
                pkName = "基诺";
        }
        return pkName;
    }

    public final int ReadBaseMsgToStr(String pkName, byte[] BmpFile, byte[] pFPmsg, byte[] pName, byte[] pSex, byte[] pNation, byte[] pBirth, byte[] pAddress, byte[] pIDNo, byte[] pDepartment, byte[] pEffectDate, byte[] pExpireDate)  throws IOException{
        int[] var13 = new int[1];
        int[] var14 = new int[1];
        int[] var15 = new int[1];
        byte[] var16 = new byte[256];
        byte[] var17 = new byte[1024];
        byte[] var18 = new byte[1024];
        int var23;
        if ((var23 = this.sdtapi.SDT_ReadBaseFPMsg(var16, var13, var17, var14, var18, var15)) == 144) {
            byte[] var24;
            Arrays.fill(var24 = new byte[1025], (byte) 0);
            System.arraycopy(var17, 0, var24, 0, 1024);
            pkName = "/data/data/" + pkName + "/lib/libwlt2bmp1.so";
            byte[] var20;
            if ((var20 = this.HdosIdUnpack(pkName, var24)) != null) {
                byte var25;
                int var26;
                for (var26 = 0; var26 < 19278; ++var26) {
                    var25 = var20[var26];
                    var20[var26] = var20['際' - var26];
                    var20['際' - var26] = var25;
                }

                var26 = 0;

                while (true) {
                    if (var26 >= 126) {
                        System.arraycopy(var20, 0, BmpFile, 0, '障');
                        break;
                    }

                    for (int var28 = 0; var28 < 153; ++var28) {
                        var25 = var20[var28 + var26 * 102 * 3];
                        var20[var28 + var26 * 102 * 3] = var20[305 - var28 + var26 * 102 * 3];
                        var20[305 - var28 + var26 * 102 * 3] = var25;
                    }

                    ++var26;
                }
            }

            System.arraycopy(var18, 0, pFPmsg, 0, 1024);

            try {
                (var24 = new byte[32])[0] = -1;
                var24[1] = -2;
                System.arraycopy(var16, 0, var24, 2, 30);
                System.arraycopy(var24, 0, pName, 0, 32);
                byte[] var27 = new byte[2];
                System.arraycopy(var16, 30, var27, 0, 2);
                if (var27[0] == 49) {
                    var27 = "男".getBytes("Unicode");
                } else if (var27[0] == 50) {
                    var27 = "女".getBytes("Unicode");
                } else {
                    var27 = "其他".getBytes("Unicode");
                }

                System.arraycopy(var27, 0, pSex, 0, var27.length);
                var17 = new byte[4];
                var20 = new byte[2];
                System.arraycopy(var16, 32, var17, 0, var17.length);

                int var21;
                for (var21 = 0; var21 < var20.length; ++var21) {
                    var20[var21] = var17[var21 * 2];
                }

                var21 = Integer.parseInt(new String(var20));
                pkName = null;
                switch (var21) {
                    case 1:
                        pkName = "汉";
                        break;
                    case 2:
                        pkName = "蒙古";
                        break;
                    case 3:
                        pkName = "回";
                        break;
                    case 4:
                        pkName = "藏";
                        break;
                    case 5:
                        pkName = "维吾尔";
                        break;
                    case 6:
                        pkName = "苗";
                        break;
                    case 7:
                        pkName = "彝";
                        break;
                    case 8:
                        pkName = "壮";
                        break;
                    case 9:
                        pkName = "布依";
                        break;
                    case 10:
                        pkName = "朝鲜";
                        break;
                    case 11:
                        pkName = "满";
                        break;
                    case 12:
                        pkName = "侗";
                        break;
                    case 13:
                        pkName = "瑶";
                        break;
                    case 14:
                        pkName = "白";
                        break;
                    case 15:
                        pkName = "土家";
                        break;
                    case 16:
                        pkName = "哈尼";
                        break;
                    case 17:
                        pkName = "哈萨克";
                        break;
                    case 18:
                        pkName = "傣";
                        break;
                    case 19:
                        pkName = "黎";
                        break;
                    case 20:
                        pkName = "傈僳";
                        break;
                    case 21:
                        pkName = "佤";
                        break;
                    case 22:
                        pkName = "畲";
                        break;
                    case 23:
                        pkName = "高山";
                        break;
                    case 24:
                        pkName = "拉祜";
                        break;
                    case 25:
                        pkName = "水";
                        break;
                    case 26:
                        pkName = "东乡";
                        break;
                    case 27:
                        pkName = "纳西";
                        break;
                    case 28:
                        pkName = "景颇";
                        break;
                    case 29:
                        pkName = "柯尔克孜";
                        break;
                    case 30:
                        pkName = "土";
                        break;
                    case 31:
                        pkName = "达斡尔";
                        break;
                    case 32:
                        pkName = "仫佬";
                        break;
                    case 33:
                        pkName = "羌";
                        break;
                    case 34:
                        pkName = "布朗";
                        break;
                    case 35:
                        pkName = "撒拉";
                        break;
                    case 36:
                        pkName = "毛南";
                        break;
                    case 37:
                        pkName = "仡佬";
                        break;
                    case 38:
                        pkName = "锡伯";
                        break;
                    case 39:
                        pkName = "阿昌";
                        break;
                    case 40:
                        pkName = "普米";
                        break;
                    case 41:
                        pkName = "塔吉克";
                        break;
                    case 42:
                        pkName = "怒";
                        break;
                    case 43:
                        pkName = "乌孜别克";
                        break;
                    case 44:
                        pkName = "俄罗斯";
                        break;
                    case 45:
                        pkName = "鄂温克";
                        break;
                    case 46:
                        pkName = "德昂";
                        break;
                    case 47:
                        pkName = "保安";
                        break;
                    case 48:
                        pkName = "裕固";
                        break;
                    case 49:
                        pkName = "京";
                        break;
                    case 50:
                        pkName = "塔塔尔";
                        break;
                    case 51:
                        pkName = "独龙";
                        break;
                    case 52:
                        pkName = "鄂伦春";
                        break;
                    case 53:
                        pkName = "赫哲";
                        break;
                    case 54:
                        pkName = "门巴";
                        break;
                    case 55:
                        pkName = "珞巴";
                        break;
                    case 56:
                        pkName = "基诺";
                }

                System.arraycopy(var20 = pkName.getBytes("Unicode"), 0, pNation, 0, var20.length);
                var20 = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(var16, 36, var20, 0, var20.length);

                int var22;
                for (var22 = 0; var22 < BmpFile.length; ++var22) {
                    BmpFile[var22] = var20[var22 * 2];
                }

                System.arraycopy(var20 = (new String(BmpFile)).getBytes("Unicode"), 0, pBirth, 0, var20.length);
                (pFPmsg = new byte[72])[0] = -1;
                pFPmsg[1] = -2;
                System.arraycopy(var16, 52, pFPmsg, 2, pFPmsg.length - 2);
                System.arraycopy(pFPmsg, 0, pAddress, 0, pFPmsg.length);
                var20 = new byte[36];
                BmpFile = new byte[18];
                System.arraycopy(var16, 122, var20, 0, var20.length);

                for (var22 = 0; var22 < BmpFile.length; ++var22) {
                    BmpFile[var22] = var20[var22 * 2];
                }

                System.arraycopy(var20 = (new String(BmpFile)).getBytes("Unicode"), 0, pIDNo, 0, var20.length);
                (pFPmsg = new byte[32])[0] = -1;
                pFPmsg[1] = -2;
                System.arraycopy(var16, 158, pFPmsg, 2, pFPmsg.length - 2);
                System.arraycopy(pFPmsg, 0, pDepartment, 0, pFPmsg.length);
                var20 = new byte[16];
                BmpFile = new byte[8];
                System.arraycopy(var16, 188, var20, 0, var20.length);

                for (var22 = 0; var22 < BmpFile.length; ++var22) {
                    BmpFile[var22] = var20[var22 * 2];
                }

                System.arraycopy(var20 = (new String(BmpFile)).getBytes("Unicode"), 0, pEffectDate, 0, var20.length);
                pFPmsg = new byte[16];
                var20 = new byte[8];
                System.arraycopy(var16, 204, pFPmsg, 0, pFPmsg.length);

                for (var21 = 0; var21 < var20.length; ++var21) {
                    var20[var21] = pFPmsg[var21 * 2];
                }

                System.arraycopy(var20 = (new String(var20)).getBytes("Unicode"), 0, pExpireDate, 0, var20.length);
            } catch (Exception var19) {
                var19.printStackTrace();
            }
        }

        return var23;
    }

    public final int[] convertByteToColor(byte[] data) {
        int var2;
        if ((var2 = data.length) == 0) {
            return null;
        } else {
            byte var3 = 0;
            if (var2 % 3 != 0) {
                var3 = 1;
            }

            int[] var4 = new int[var2 / 3 + var3];
            int var5;
            if (var3 == 0) {
                for (var5 = 0; var5 < var4.length; ++var5) {
                    var4[var5] = data[var5 * 3] << 16 & 16711680 | data[var5 * 3 + 1] << 8 & '\uff00' | data[var5 * 3 + 2] & 255 | -16777216;
                }
            } else {
                for (var5 = 0; var5 < var4.length - 1; ++var5) {
                    var4[var5] = data[var5 * 3] << 16 & 16711680 | data[var5 * 3 + 1] << 8 & '\uff00' | data[var5 * 3 + 2] & 255 | -16777216;
                }

                var4[var4.length - 1] = -16777216;
            }

            return var4;
        }
    }

    public void setSdtapi(Api sdtapi) {

        this.sdtapi = sdtapi;
    }

    public void close() throws Exception {
        this.sdtapi.close();
    }
}

/* Location:           E:\NEWEclipseSpace\TobaccoSales\libs\publicSecurityIDCardLib.jar
 * Qualified Name:     com.hdos.usbdevice.publicSecurityIDCardLib
 * JD-Core Version:    0.6.2
 */