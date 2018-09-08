package com.it_tao_idcard.ReadIDCardInform.helper;

import android.content.Context;

import com.it_tao_idcard.ReadIDCardInform.callback.IdcardReadCallback;
import com.it_tao_idcard.ReadIDCardInform.handler.IDcardDataHandler;
import com.it_tao_idcard.ReadIDCardInform.handler.InquireIDcardData;
import com.it_tao_idcard.ReadIDCardInform.utils.LogUtil;

/**
 * Created by Administrator on 2017-09-06.
 */

public class IDCardSacnHelper {

    private InquireIDcardData inquireIDcardThread;
    private IDcardDataHandler iDcardDataHandler;
    private static IDCardSacnHelper idCardSacnHelper;
    private Context context;

    static String TAG = "IDCardSacnHelper";
    private boolean aBoolean;
    private long outTime;
    private IdcardReadCallback callback;

    private Mode mode = Mode.USB;

    public enum Mode {
        USB, Serial
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (inquireIDcardThread != null)
            inquireIDcardThread.setMode(mode);
    }

    public static IDCardSacnHelper getIDCardSacnHelperInstalse(Context context, IdcardReadCallback callback) {
        LogUtil.e(TAG, "getIDCardSacnHelperInstalse " + String.valueOf(null == idCardSacnHelper));
        if (null == idCardSacnHelper) {
            idCardSacnHelper = new IDCardSacnHelper(context, callback);
        }
        idCardSacnHelper.setCallback(callback);
        return idCardSacnHelper;
    }

    public static IDCardSacnHelper getIDCardSacnHelperInstalse(Context context, long readTime, IdcardReadCallback callback) {

        if (null == idCardSacnHelper) {
            idCardSacnHelper = new IDCardSacnHelper(context, callback);
        }
        idCardSacnHelper.setReadOutTimne(readTime);
        idCardSacnHelper.setCallback(callback);
        return idCardSacnHelper;
    }

    private IDCardSacnHelper(Context context, IdcardReadCallback callback) {
        this.context = context;
        iDcardDataHandler = new IDcardDataHandler(context, callback);
        inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
    }

    private IDCardSacnHelper(Context context, long readTime, IdcardReadCallback callback) {
        this.context = context;
        iDcardDataHandler = new IDcardDataHandler(context, callback);
        inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
        inquireIDcardThread.setOvertime(readTime);

    }


    public void setReadOutTimne(long time) {

        outTime = time;
        if (inquireIDcardThread != null)
            inquireIDcardThread.setOvertime(time);
    }

    public void setCallback(IdcardReadCallback callback) {

        this.callback = callback;
        if (iDcardDataHandler != null)
            iDcardDataHandler.setCallback(callback);
    }

    public void setLongTime(boolean longTime) {
        if (inquireIDcardThread != null)
            inquireIDcardThread.setLongTime(longTime);
    }

    public void read(Mode mode) {

        if (mode != null)
            setMode(mode);

        if (inquireIDcardThread != null) {
            if (inquireIDcardThread.isInterrupted())
                inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
            inquireIDcardThread.noOuttime(aBoolean);
            inquireIDcardThread.setOvertime(outTime);
            try {
                inquireIDcardThread.start();
            } catch (Exception e) {

            }
        } else {
            inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
            inquireIDcardThread.noOuttime(aBoolean);
            inquireIDcardThread.setOvertime(outTime);
            try {
                inquireIDcardThread.start();
            } catch (Exception e) {

            }
        }
    }

    public void read() {
        if (inquireIDcardThread != null) {
            if (!inquireIDcardThread.isInterrupted())
                inquireIDcardThread.interrupt();
            inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
            inquireIDcardThread.noOuttime(aBoolean);
            inquireIDcardThread.setOvertime(outTime);
            try {
                inquireIDcardThread.start();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "" + e.toString());
            }
        } else {
            inquireIDcardThread = new InquireIDcardData(context, iDcardDataHandler);
            inquireIDcardThread.noOuttime(aBoolean);
            inquireIDcardThread.setOvertime(outTime);
            try {
                inquireIDcardThread.start();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "" + e.toString());

            }
        }
    }


    public void release() {
        if (inquireIDcardThread != null && !inquireIDcardThread.isInterrupted()) {
            inquireIDcardThread.interrupt();
        }
        inquireIDcardThread = null;
    }

    public void noOuttime(boolean b) {
        aBoolean = b;
        if (inquireIDcardThread != null) {
            inquireIDcardThread.noOuttime(b);

        }


    }


}
