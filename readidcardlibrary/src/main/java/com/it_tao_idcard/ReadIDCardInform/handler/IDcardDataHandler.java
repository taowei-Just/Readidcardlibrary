package com.it_tao_idcard.ReadIDCardInform.handler;


import android.content.Context;
import android.os.Message;

import com.it_tao_idcard.ReadIDCardInform.base.BaseHandler;
import com.it_tao_idcard.ReadIDCardInform.callback.IdcardReadCallback;

import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;
import com.it_tao_idcard.ReadIDCardInform.utils.LogUtil;

/**
 * Created by Administrator on 2017-09-07.
 */

public class IDcardDataHandler extends BaseHandler implements IdcardReadCallback {


    // 读取身份信息失败

    public final static int READ_IDCARD_COUNTOUT_TAG = 0x18;

    //成功
    public final static int READ_IDCARD_SUCCESS_TAG = 0x19;

    //状态异常
    public final static int READ_IDCARD_THREAD_ERROR_TAG = 0x20;

    //超时
    public final static int READ_IDCARD_TIME_OUT_TAG = 0x21;

    IdcardReadCallback callback;
    String tag = "IDcard_infomation";

    public IDcardDataHandler(Context context, IdcardReadCallback callback) {
        super(context);
        this.callback = callback;

    }

    public void setCallback(IdcardReadCallback callback) {
        this.callback = callback;
    }

    public void handleMessage(Message msg, int what) {
        Context activity = activityWeakReference.get();
        if (activity != null) {
            switch (msg.what) {

                case READ_IDCARD_COUNTOUT_TAG:
                    // 读取身份信息失败
//                    ToastTools.showShort(activity, "读取身份证失败，请重新操作！");

                    LogUtil.e(tag, "read_IDCard_faild");
                    onReadFailed();
                    break;
                case READ_IDCARD_THREAD_ERROR_TAG:
                    // 读卡器状态异常
//                    ToastTools.showShort(activity, "身份证读卡器异常，请联系维护人员！");
                    LogUtil.e(tag, "read_IDCard_ Exception");
                    onReadSataeexception();
                    break;
                case READ_IDCARD_TIME_OUT_TAG:
                    // 获取身份信息超时
                    LogUtil.e(tag, "read_IDCard_TimeOut");
                    onReadTimeout();
                    break;

                case READ_IDCARD_SUCCESS_TAG:
                    // 读取身份信息成功
                    // AudioTools.playMediaAodio(Gloable.audioPath
                    // + "/VerifySuccess.mp3");

                    LogUtil.e(tag, "read_IDCard_successful");

                    IDcardInfo information = (IDcardInfo) msg.obj;
                    onReadSucces(information);

            }
        }
    }


    @Override
    public void onReadSucces(IDcardInfo information) {
        if (callback != null)
            callback.onReadSucces(information);
    }

    @Override
    public void onReadSataeexception() {
        if (callback != null)
            callback.onReadSataeexception();
    }

    @Override
    public void onReadFailed() {
        if (callback != null)
            callback.onReadFailed();
    }

    @Override
    public void onReadTimeout() {
        if (callback != null)
            callback.onReadTimeout();

    }
}
