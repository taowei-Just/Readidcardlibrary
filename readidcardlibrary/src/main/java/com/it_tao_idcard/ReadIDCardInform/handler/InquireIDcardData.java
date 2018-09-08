package com.it_tao_idcard.ReadIDCardInform.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.it_tao_idcard.ReadIDCardInform.helper.IDCardSacnHelper;
import com.it_tao_idcard.ReadIDCardInform.helper.IDcardAquireHelper;
import com.it_tao_idcard.ReadIDCardInform.info.IDcardInfo;
import com.it_tao_idcard.ReadIDCardInform.utils.LogUtil;

public class InquireIDcardData extends Thread {

    private String TAG = "InquireIDcardData";

    private Context activity;
    private Handler handler;
    private long retryTime = 5000;
    //超时时间
    private long overtime = 60 * 1000;
    private long onesetime = 5 * 100;
    //尝试读取5次
    private int retryCount = 1;
    //读卡器状态
    private boolean cardState = false;
    private IDcardAquireHelper mCardHelper;
    private long startTime;
    private IDcardInfo information;
    boolean isLongTime = false;
    private IDCardSacnHelper.Mode mode;

    public void setLongTime(boolean longTime) {
        isLongTime = longTime;
    }

    public InquireIDcardData(Context activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    long laststart;

    public void run() {
        LogUtil.e(TAG, " run： ");

        mCardHelper = new IDcardAquireHelper(activity.getApplicationContext(), mode == null ? IDCardSacnHelper.Mode.USB : mode);
        startTime = System.currentTimeMillis();
        laststart = startTime;
        try {
            acqureState();
            while (!isInterrupted()) {
                readCardTimeout();
                if (cardState) {
                    try {
                        LogUtil.e(TAG, " 正在读取身份信息： ");
                        information = mCardHelper.acquireCardInformation();
                    } catch (Exception e) {
                        e.toString();
                        LogUtil.e(TAG, " 读取身份证异常： " + e.toString());
                        information = null;

                    }
                    if (information != null) {

                        // 身份信息读取成功
                        LogUtil.e(TAG, "身份信息读取成功 ");
                        Message message = handler.obtainMessage();
                        message.what = IDcardDataHandler.READ_IDCARD_SUCCESS_TAG;
                        message.obj = information;
                        handler.sendMessage(message);

                        if (!isLongTime)
                            stopRead();
                    } else {
                        // 身份信息读取失败
                        acqureState();
                        handler.sendEmptyMessage(IDcardDataHandler.READ_IDCARD_COUNTOUT_TAG);
                    }
                } else {
                    acqureState();
                    // 读卡器状态异常
//                    LogUtil.e(TAG, " 读卡器状态异常  ");
                    handler.sendEmptyMessage(IDcardDataHandler.READ_IDCARD_THREAD_ERROR_TAG);
                }
                InquireIDcardData.sleep(onesetime);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LogUtil.e(TAG, "  Exception  " + e.toString());

            if ((System.currentTimeMillis() - startTime) > overtime) {
                handler.sendEmptyMessage(IDcardDataHandler.READ_IDCARD_TIME_OUT_TAG);
            }
        } finally {

            LogUtil.e(TAG, "   结束查询  ");
            try {
                mCardHelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRead() throws  Exception{
            this.interrupt();
    }

    private void acqureState() {
        LogUtil.e(TAG, " acqureState ");
        try {
            cardState = mCardHelper.acquireCardState();
        } catch (Exception e) {
            e.printStackTrace();
            cardState = false;
            LogUtil.e(TAG, "thread name : " + InquireIDcardData.this.getName() + " thread id : " + InquireIDcardData.this.getId() + "\n");
            // 错误时间超时
        } finally {
            //重置 gpio
            if ((System.currentTimeMillis() - laststart) > retryTime && !cardState) {
                laststart = System.currentTimeMillis();
            }
        }

    }

    public void readCardTimeout() {

        // 获取身份识别器 失败超时
        // 读取身份信息超时

        if (noOutTime)
            return;

        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis - startTime > overtime) {
            // 超时
            handler.sendEmptyMessage(IDcardDataHandler.READ_IDCARD_TIME_OUT_TAG);
            LogUtil.e("", "读取超时 ！ " + overtime);
            try {
                stopRead();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * @return 重复查询次数
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * @return 设置 重复查询次数
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * @return 读取信息超时时间
     */
    public long getOvertime() {
        return overtime;
    }

    /**
     * @return 设置读取信息超时时间
     */
    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }


    boolean noOutTime = false;

    public void noOuttime(boolean b) {
        this.noOutTime = b;
    }

    public void setMode(IDCardSacnHelper.Mode mode) {
        this.mode = mode;
    }
}
