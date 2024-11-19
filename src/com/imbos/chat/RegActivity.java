package com.imbos.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncSqliteHandler;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

public class RegActivity extends SkinActivity{
	
	private Button btnReg;
	private Button btnValidCode;
	private EditText editNickname;
	private EditText editPassword;
	private EditText editCfmPassword;
	private EditText editPhone;
	private EditText editValicode;
	private RadioGroup radioGroup;
	
	private Object num;
	public static final int MSG_TIME_PICK=0x912;
	public int timerCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg);
		setTitle(R.string.GENERAL_REG);
		btnNext.setVisibility(View.INVISIBLE);
		
		btnReg = (Button) findViewById(R.id.btn_reg);
		btnReg.setOnClickListener(this);
		
		btnValidCode = (Button) findViewById(R.id.btn_valicode);
		btnValidCode.setOnClickListener(this);
		
		editNickname = (EditText) findViewById(R.id.edit_nickname);
		editPassword = (EditText) findViewById(R.id.edit_password);
		editCfmPassword = (EditText) findViewById(R.id.edit_cfmPassword);
		editPhone = (EditText) findViewById(R.id.edit_phone);
		editValicode = (EditText)findViewById(R.id.edit_phone);
		radioGroup = (RadioGroup) findViewById(R.id.rg_sex);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_reg:
				doRegister();
				break;
			case R.id.btn_valicode:
				doValicode();
				break;
			default:
				super.onClick(view);
		}
		
	}
	
	@Override
	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_TIME_PICK:
			timerCount--;
			if(timerCount>0){
				handler.sendEmptyMessageDelayed(MSG_TIME_PICK, 1000);
				btnValidCode.setText(getString(R.string.REG_GET_VALICODE)+"("+timerCount+")");
			}else{
				btnValidCode.setText(getString(R.string.REG_GET_VALICODE));
				btnValidCode.setEnabled(true);
			}
			break;

		default:
			super.handleMessage(msg);
		}
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
		.setTitle(R.string.GENERAL_TIP)
		.setMessage(Html.fromHtml(getString(R.string.REG_SUCCESS_TIP,num)))
		.setPositiveButton(R.string.GENERAL_CONFIRM, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				ChatApp.resetDbHelper();
				startActivity(new Intent(getApplication(), MainActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				finish();
			}
		})
		.setNegativeButton(R.string.GENERAL_CANCEL,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).create();
	}
	
	public void doValicode(){
		
	
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("mobile", editPhone.getText().toString());
		
		
		SyncService.start(new SyncTask()
		.setSyncListener(new SimpleSyncListener(this){
			@Override
			public void onFailed() {
				super.onFailed();
				if(StringUtil.isEmpty(tips)){
					showToast(R.string.SYNC_MSG_FAILED);
				}else{
					showToast(this.tips);
				}
			}
			
			@Override
			public void onSuccess() {
				super.onSuccess();
				showToast(R.string.REG_MSG_PHONE_SEND);
				List<?> ds = (List<?>) syncTask.getResponse().get("ds");
				Object validcode = SyncSqliteHandler.tableValue(ds,"code");
				editValicode.setTag(validcode);
				
				timerCount=60;
				btnValidCode.setEnabled(false);
				handler.sendEmptyMessage(MSG_TIME_PICK);
			}
		}).setMethodName(Constants.MENTHOD.COMMON_ADDCODE)
		.setSyncHandler(null)
		.setArgs(args));
	}
	
	public void doRegister(){
		
		
		String nickname = editNickname.getText().toString();
		String password = editPassword.getText().toString();
		String passwordCfm = editCfmPassword.getText().toString();
		String phone = editPhone.getText().toString();
		String sex = radioGroup.getCheckedRadioButtonId()==R.id.cb_boy?Constants.SEX.BOY:Constants.SEX.GIRL;
		String valicode = editValicode.getText().toString();
		Object serverValidcode = editValicode.getTag();
		if(StringUtil.isEmpty(nickname)){
			editNickname.setError(getString(R.string.REG_MSG_NICKNAME_EMPLY));
			return;
		}else if (StringUtil.isEmpty(password)){
			editPassword.setError(getString(R.string.REG_MSG_PASSWORD_EMPLY));
			return;
		}else if (StringUtil.isEmpty(passwordCfm)){
			editCfmPassword.setError(getString(R.string.REG_MSG_PASSWORD_EMPLY));
			return;
		}else if (!password.equals(passwordCfm)){
			editCfmPassword.setError(getString(R.string.REG_MSG_PASSWORD_EMPLY));
			return;
		}else if (!valicode.equals(serverValidcode)){
			editValicode.setError(getString(R.string.REG_MSG_PHONE_ERROR));
			return;
		}
		
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("name", nickname);
		args.put("pwd", password);
		args.put("phone",phone);
		args.put("sex", sex);

		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onFailed() {
						super.onFailed();
						if(StringUtil.isEmpty(tips)){
							showToast(R.string.SYNC_MSG_FAILED);
						}else{
							showToast(this.tips);
						}
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						
						List<?> ds = (List<?>) syncTask.getResponse().get("ds");
						num = SyncSqliteHandler.tableValue(ds,"num");
						
						showDialog(R.string.REG_SUCCESS_TIP);
					}
				}).setMethodName(Constants.MENTHOD.LOGIN_REGISTERED)
				.setArgs(args));
	}
}
