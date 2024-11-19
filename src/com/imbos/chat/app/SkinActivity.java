package com.imbos.chat.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imbos.chat.R;
import com.imbos.chat.base.BaseActivity;
import com.imbos.chat.util.AndroidUtils;
import com.imbos.chat.util.DebugUtil;

/**
 * 
 * @author wanxianze@gmail.com 2012-6-1
 */
public class SkinActivity extends BaseActivity{
	protected String TAG = SkinActivity.class.getSimpleName();
	
	protected TextView txtTitle;
	protected Button btnNext;
	protected Button btnBack;

	protected final int DIALOG_EXIT = 0x21234;
	protected final int DIALOG_MSG = 0x21235;
	protected final int DIALOG_PROGRESS = 0x11;
	
	protected final int RESULT_EMPTY = 10;

	protected final static int MSG_FAILED = 0x1;
	protected final static int MSG_SUCCESSS = 0x2;
	protected final static int MSG_TOAST = 0x3;

	public final static int MSG_DIALOG_SHOW = 260;
	public final static int MSG_DIALOG_DISMISS = 261;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getDecorView().setBackgroundResource(R.color.activity_backgrond); 
		this.getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
								| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		TAG = this.getClass().getSimpleName();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intents.ACTION_DATA);
		this.registerReceiver(receiver, filter);
		
		DebugUtil.trace(TAG);
	}
	
	protected BroadcastReceiver receiver  = new  BroadcastReceiver() {
		
		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getStringExtra(Intents.EXTRA_SOURCE_ACTION);
			intent.setAction(action);
			SkinActivity.this.onReceive(ctx, intent);
		}
	};
	
	protected void onReceive(Context ctx, Intent intent){
		
	}
	protected void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

	}

	protected void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	public void goActivity(Class<?> activity) {
		Intent intent = new Intent();
		intent.setClass(this, activity);
		startActivity(intent);
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		txtTitle = (TextView) findViewById(R.id.title_txt);
		btnBack = (Button) findViewById(R.id.title_btn_back);

		if (btnBack != null) {
			btnBack.setOnClickListener(this);
		}
		btnNext = (Button) findViewById(R.id.title_btn_next);
		if (btnNext != null)
			btnNext.setOnClickListener(this);
		
	}

	@Override
	public void setTitle(int titleId) {
		if (txtTitle != null)
			txtTitle.setText(titleId);
		super.setTitle(titleId);
	}

	@Override
	public void setTitle(CharSequence text) {
		if (txtTitle != null)
			txtTitle.setText(text);
		super.setTitle(text);
	}

	@Override
	public void onClick(View view) {
		if (AndroidUtils.isFastDoubleClick())
			return;

		switch (view.getId()) {
		case R.id.title_btn_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}



	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Log.d(TAG, "onCreateDialog(id,args):" + id);
		String msg = args != null && args.containsKey("msg") ? args
				.getString("msg") : "";
		switch (id) {
		case DIALOG_MSG:
			return new AlertDialog.Builder(this).setTitle(R.string.GENERAL_TIP)
					.setMessage(msg)
					.setPositiveButton(R.string.GENERAL_CONFIRM, null).create();

		default:
			return super.onCreateDialog(id, args);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		Log.d(TAG, "onPrepareDialog:" + id);
		if (id == DIALOG_MSG && dialog != null)
			((AlertDialog) dialog).setMessage(args.getString("msg"));
		else
			super.onPrepareDialog(id, dialog, args);
	}

	public void setButtonIcon(Button btn, int resId) {
		btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,resId);
	}

	public void setButton(Button btn, int icoId, int strId) {
		btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,icoId);
		btn.setText(strId);
	}

	@Override
	protected android.app.Dialog onCreateDialog(int id) {
		switch (id) {
//		case DIALOG_EXIT:
//			return new AlertDialog.Builder(this)
//			.setTitle(R.string.GENERAL_TIP)
//			.setMessage(R.string.GENERAL_CONFIRM_BACK)
//			.setPositiveButton(R.string.GENERAL_YES, new OnClickListener(){
//				public void onClick(DialogInterface dialog,int arg1)
//				{
//					setResult(Activity.RESULT_CANCELED);
//					SkinActivity.super.onBackPressed();
//				}
//			})
//			.setNegativeButton(R.string.GENERAL_NO, null)
//			.create();
		case DIALOG_PROGRESS:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("正在处理,请稍后.");
			return dialog;
		default:
			return super.onCreateDialog(id);
		}
		
	};

	@Override
	protected void onStart() {
		super.onStart();
		DebugUtil.trace(TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DebugUtil.trace(TAG);
	}

	@Override
	protected void onPause() {
		super.onPause();
		DebugUtil.trace(TAG);
	}

	@Override
	protected void onStop() {
		super.onStop();
		DebugUtil.trace(TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		DebugUtil.trace(TAG);
	}

	@Override
	public void finish() {
		if (handler != null)
			handler.removeCallbacksAndMessages(this);
		super.finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
