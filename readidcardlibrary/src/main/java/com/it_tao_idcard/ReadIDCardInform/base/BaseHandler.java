package com.it_tao_idcard.ReadIDCardInform.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.lang.ref.WeakReference;

 

/**
 * handler包装类
 */
public abstract class BaseHandler extends Handler {

	
	String TAG = "BaseHandler";
	protected WeakReference<Context> activityWeakReference;
	protected WeakReference<Fragment> fragmentWeakReference;

	private BaseHandler() {
	}// 构造私有化,让调用者必须传递一个Activity 或者 Fragment的实例

	public BaseHandler(Context		 context) {
		this.activityWeakReference = new WeakReference<Context>(context);
	}

	public BaseHandler(Fragment fragment) {
		this.fragmentWeakReference = new WeakReference<Fragment>(fragment);
	}

	@Override
	public void handleMessage(Message msg) {
		if (activityWeakReference == null || activityWeakReference.get() == null 	|| activityWeakReference.get().isRestricted()) {
			// 确认Activity是否不可用
			Log.i(TAG,"Context is gone");
//			handleMessage(msg, What.ACTIVITY_GONE);
		}  else   {
			handleMessage(msg, msg.what);
		}
		
		if (fragmentWeakReference == null
				|| fragmentWeakReference.get() == null
				|| fragmentWeakReference.get().isRemoving()) {
			// 确认判断Fragment不可用
			Log.i(TAG,"Fragment is gone");
//			handleMessage(msg, What.ACTIVITY_GONE);
		} else   {
//			handleMessage(msg, msg.what);
		}
	}

	/**
	 * 抽象方法用户实现,用来处理具体的业务逻辑
	 * 
	 * @param msg
	 * @param what
	 *   
	 */
	public abstract void handleMessage(Message msg, int what);

}
