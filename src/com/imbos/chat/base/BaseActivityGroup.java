package com.imbos.chat.base;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imbos.chat.R;
/**
 * 
 * @author wanxianze@gmail.com
 * 2012-3-15
 */
public class BaseActivityGroup extends ActivityGroup implements OnClickListener{
	protected static final int DIALOG_EXIT = 0x11;
	protected String TAG = BaseActivityGroup.class.getSimpleName();
	protected TextView txtTitle;
	protected Button btnNext,btnBack;
	protected  final int DIALOG_PROCESS=0x12;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getDecorView().setBackgroundResource(R.color.activity_backgrond);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN  |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 	
	};
	@Override
	public void setTitle(int titleId) {
		if(txtTitle!=null){
			txtTitle.setText(titleId);
		}super.setTitle(titleId);
	}
	public void setButtonIcon(Button btn,int resId){
		btn.setCompoundDrawablesWithIntrinsicBounds(resId, 0,0,0);
	}
	public void setButton(Button btn,int icoId,int strId){
		btn.setCompoundDrawablesWithIntrinsicBounds(icoId, 0,0,0);
		btn.setText(strId);
	}
	protected void onReceive(Context ctx, Intent intent){
		
	}	
	protected void showToast(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
	protected void showToast(int resId){
		Toast.makeText(this,resId,Toast.LENGTH_SHORT).show();
	}
	public void goActivity (Class<?> activity) {
		Intent intent = new Intent();
		intent.setClass(this,activity);
		startActivity(intent);
	}
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROCESS:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("process...");
			return dialog;

		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onClick(View arg0) {
		
	}
}
