package com.imbos.chat.sync;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.imbos.chat.R;
import com.imbos.chat.util.StringUtil;


/**
 * 
* 类功能说明
* 类修改者	修改日期
* 修改说明
* <p>Title: SimpleSyncListener.java</p>
* <p>Description:</p>
* <p>Copyright: Copyright (c) 2012</p>
* @author wanxianze@gmail.com
* @date 2013-1-14 上午9:59:06
* @version V1.0
 */
public class DataSyncListener implements SimpleSyncTask.OnSyncListener {
	private Context context;
	private Handler handler;
	protected AlertDialog dialog;
	protected final int MSG_PROGRESS = 0x11;
	protected Object tag;
	protected String message;
	protected HashMap<String, ?> response;
	
	/**
	 * 
	* <p>Title: 简单的同步监听</p>
	* <p>Description: </p>
	* @param ctx
	* @param background 是否后台执行，即不锁定界面
	 */
	public DataSyncListener(Context ctx, boolean background) {
		this.context = ctx;

		if (!background) {
			this.dialog = new ProgressDialog(ctx);
			this.dialog.setCanceledOnTouchOutside(false);
			this.dialog.setCancelable(false);
		}
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case SyncTask.STATUS_START:
					onBefore();
					break;
				case SyncTask.STATUS_FINISH:
					onSuccess(response);
					break;
				case SyncTask.STATUS_FAILED:
					onFailed();
					break;
				case MSG_PROGRESS:
					onProgress((long) msg.arg1, (long) msg.arg2, msg.obj == null ? "" : msg.obj.toString());
					break;
				}
			}
		};
	}

	public DataSyncListener(Context ctx) {
		this(ctx, false);
	}

	@Override
	public void onChange(SimpleSyncTask task, int state) {
		message = task.getErrorMessage();
		response = task.getResponse();
		handler.obtainMessage(state).sendToTarget();
	}

	@Override
	public void onProgress(int cur, int max, String msg) {
		Message message = handler.obtainMessage(MSG_PROGRESS);
		message.arg1 = cur;
		message.arg2 = max;
		message.obj = msg;
		message.sendToTarget();
	}

	public void onBefore() {
		if (dialog != null) {
			dialog.setMessage(context.getString(R.string.SYNC_MSG_SYNCING));
			dialog.show();
		}
	}

	public void onSuccess(HashMap<String, ?> response) {
		if (dialog != null) {
			dialog.setMessage(context.getString(R.string.SYNC_MSG_SUCCESS));
			if (dialog.isShowing())
				dialog.dismiss();
		}
	}

	public void onFailed() {
		if (dialog != null) {
			this.dialog.setCancelable(true);
			message = StringUtil.isEmpty(message) ? context.getString(R.string.SYNC_MSG_FAILED) : message;
			dialog.setMessage(message);
		}

	}

	public void onProgress(long cur, long max, String msg) {
		if (dialog != null)
			dialog.setMessage(msg);
	}

	public void closeDialog() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
	
}
