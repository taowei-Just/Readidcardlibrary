package com.it_tao_idcard.ReadIDCardInform.helper;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.hdos.usbdevice.publicSecurityIDCardLib;
import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;
import com.it_tao_idcard.ReadIDCardInform.utils.LogUtil;
import com.sdt.Api;
import com.sdt.Sdtapi;
import com.sdt.SerialSdtapi;

public class IDcardAquireHelper {
    private static publicSecurityIDCardLib iDCardDevice;
    private static Api sdtapi;
    private Context context;
    String tag = getClass().getSimpleName() ;

    private byte[] name = new byte[32];
    private byte[] sex = new byte[6];
    private byte[] birth = new byte[18];
    private byte[] nation = new byte[12];
    private byte[] address = new byte[72];
    private byte[] Department = new byte[32];
    private byte[] IDNo = new byte[38];
    private byte[] EffectDate = new byte[18];
    private byte[] ExpireDate = new byte[18];
    byte[] BmpFile = new byte[38556];
    byte[] FpMsg = new byte[1024];

    String pkName;
    IntentFilter mIntentFilter;
    IDCardSacnHelper.Mode mode  = IDCardSacnHelper.Mode.USB;

    public IDcardAquireHelper(Context context, IDCardSacnHelper.Mode mode) {
        this.context = context;
        this.mode = mode;
        pkName = context.getPackageName();
    }


    //获取读卡器状态
    public boolean acquireCardState() throws Exception {
        boolean isOk = false;
        if ( IDcardAquireHelper.iDCardDevice == null) {
            try {
                LogUtil.e(tag , " 初始化usb  acquireCardState  mode =" + mode);
                IDcardAquireHelper.iDCardDevice = new publicSecurityIDCardLib(context ,  mode ==null? IDCardSacnHelper.Mode.USB:mode);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(" publicSecurityIDCardLib  ", e.toString());
                try {
                    IDcardAquireHelper.iDCardDevice.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                IDcardAquireHelper.iDCardDevice = null;
                throw e;
            }
        }
        if (IDcardAquireHelper.iDCardDevice.sdtapi == null) {
            LogUtil.e(tag , " 初始化usb  sdtapi  mode =" + mode);

            try {
                if (mode == IDCardSacnHelper.Mode.USB){
                    LogUtil.e(tag, " usb mode");
                    sdtapi = new Sdtapi(context);
                }else if (mode == IDCardSacnHelper.Mode.Serial){
                    LogUtil.e(tag, " Serial mode");
                    sdtapi = new SerialSdtapi(context);
                }
            } catch (Exception d) {
                try {
                    IDcardAquireHelper.iDCardDevice.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                IDcardAquireHelper.iDCardDevice. sdtapi = null;
                d.printStackTrace();
                LogUtil.e(" Sdtapi ", d.toString());
                throw d;
            }
        }
        if (iDCardDevice != null && sdtapi != null) {
            iDCardDevice.setSdtapi(sdtapi);
        }
        try {
            LogUtil.e(tag , "  获取模块状态 ");
            int ret = iDCardDevice.getSAMStatus();
            String show;
            if (ret == 0x90) {
                show = "模块状态良好";
                isOk = true;
            } else {
                show = "模块状态错误:" + String.format("0x%02x", ret);
                try {
                    IDcardAquireHelper.iDCardDevice.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                IDcardAquireHelper.iDCardDevice.sdtapi = null;
                    IDcardAquireHelper.iDCardDevice=null;
                  LogUtil.e(tag, "此处 so 可能报错！");
            }
            LogUtil.e(tag , "   模块状态 " + show);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(tag, e.toString());

            try {
                IDcardAquireHelper.iDCardDevice.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            IDcardAquireHelper.iDCardDevice. sdtapi = null;
            IDcardAquireHelper.iDCardDevice=null;
            throw e;
        }
        return isOk;
    }


    //读基本信息
    public IDcardInfo acquireCardInformation() throws Exception {

        IDcardInfo mCardInfo = null;
        int ret;
        String show = "";
        long beginTime = System.currentTimeMillis();

        LogUtil.e(tag , "读取身份信息 acquireCardInformation"  );
        if (iDCardDevice == null)
            iDCardDevice = new publicSecurityIDCardLib(context ,  mode ==null? IDCardSacnHelper.Mode.USB:mode);

        ret = iDCardDevice.readBaseMsgToStr(pkName, BmpFile, name, sex, nation, birth, address, IDNo, Department,
                EffectDate, ExpireDate);
        long time = System.currentTimeMillis() - beginTime;

        int[] colors = iDCardDevice.convertByteToColor(BmpFile);
        Bitmap bm = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);

        if (ret == 0x90) {

            show = "读基本信息成功 :" + String.format("0x%02x", ret);

            try {
                mCardInfo = new IDcardInfo();
                mCardInfo.setBm(bm);
                mCardInfo.setName(new String(name, "Unicode"));

                mCardInfo.setSex(new String(sex, "Unicode"));
                mCardInfo.setNation(new String(nation, "Unicode") + "族");
                mCardInfo.setBirth(new String(birth, "Unicode"));
                mCardInfo.setAddress(new String(address, "Unicode"));
                mCardInfo.setIDNo(new String(IDNo, "Unicode"));
                mCardInfo.setDepartment(new String(Department, "Unicode"));
                mCardInfo.setEffectDate(new String(EffectDate, "Unicode"));
                mCardInfo.setExpireDate(new String(ExpireDate, "Unicode"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show = "读基本信息失败:" + String.format("0x%02x", ret);
        }
        LogUtil.e(tag ," 读取信息:" + show );
        return mCardInfo;
    }


    public void close() throws  Exception {
        IDcardAquireHelper.iDCardDevice.close();
    }
}
