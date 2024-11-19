package com.imbos.chat.base;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class BaseActivity extends Activity implements OnClickListener{
	
	public static String TAG = BaseActivity.class.getSimpleName();
	static {
		//e4a28ae9-8858-49a5-8ca9-a3c7e14708bc;
	}
	protected Resources res = null;
	protected Handler handler = null;
	
	protected final int ACTION_ACTIVITY_GO = 0x06;
	protected final int ACTION_TOAST = 0x07;
	protected static final int MESSAGE_FINISH = 0x12;
	protected static final int MESSAGE_FAILED =0x11;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.progress_small));
		//progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_small));
		//progressDialog.setInverseBackgroundForced(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				BaseActivity.this.handleMessage(msg);

			}

		};
		res = this.getResources();

		TAG = this.getClass().getSimpleName();
	}
	
	protected void goActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case ACTION_ACTIVITY_GO:
			this.goActivity((Class<?>) msg.obj);
			break;
		case ACTION_TOAST:
			this.showToast(msg.obj == null ? "" : msg.obj.toString());
			break;
		default:
			break;
		}
	}
    protected void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart() {
		Log.d(TAG,"onStart");
		super.onStart();
	}
	@Override
	protected void onRestart() {
		Log.d(TAG,"onRestart");
		super.onRestart();
	}
	@Override
	protected void onResume() {
		Log.d(TAG,"onResume");
		super.onResume();
	}
	@Override
	protected void onStop() {
		Log.d(TAG,"onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG,"onDestroy");
		super.onDestroy();
	}

	public int getWidth() {
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		return display.widthPixels;
	}
	public int getHeight() {
		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		return display.heightPixels;
	}
    
    
}
