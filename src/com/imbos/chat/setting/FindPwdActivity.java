package com.imbos.chat.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncSqliteHandler;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;
/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class FindPwdActivity extends SkinActivity{
	
	private static final int MSG_TIME_PICK = 0;
	private EditText editPhone;
	private EditText editValicode;
	private EditText editNewPassword;
	private EditText editCfmPassword;
	private Button btnValidCode;
	protected int timerCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_pwd);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.FIND_PWD_TITLE);
			
		editPhone = (EditText) findViewById(R.id.edit_phone);
		editNewPassword = (EditText) findViewById(R.id.edit_newPassword);
		editCfmPassword = (EditText) findViewById(R.id.edit_cfmPassword);
		editValicode = (EditText) findViewById(R.id.edit_valicode);
		btnValidCode = (Button) findViewById(R.id.btn_valicode);
		btnValidCode.setOnClickListener(this);
		findViewById(R.id.btn_green).setOnClickListener(this);
	}
	

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_green:
			doFindPwd();
			break;
		case R.id.btn_valicode:
			doValicode();
			break;
		default:
			super.onClick(view);
		}
	}
	
	public void doFindPwd(){
		
		
		if(StringUtil.isEmpty(editPhone.getText())){
			editPhone.setError(getString(R.string.FIND_PWD_PHONE_EMPLY));
			return;
		}else if(StringUtil.isEmpty(editValicode.getText())){
			editValicode.setError(getString(R.string.FIND_PWD_PHONE_VALICODE));
			return;
		}else if(StringUtil.isEmpty(editNewPassword.getText())){
			editNewPassword.setError(getString(R.string.FIND_PWD_PHONE_VALICODE));
			return;
		}
		
		Map<String,String> args = new HashMap<String, String>();
		args.put("phone",editPhone.getText().toString());
		args.put("code",editValicode.getText().toString());
		args.put("password", editNewPassword.getText().toString());
		
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onFailed() {
						super.onFailed();
						if(dialog.isShowing())
							dialog.dismiss();
						if(StringUtil.isEmpty(tips))
							showToast(R.string.CHANGE_PWD_MSG_FAIL);
						else
							showToast(this.tips);
					}
					@Override
					public void onSuccess() {
						super.onSuccess();
						if(StringUtil.isEmpty(tips))
							showToast(R.string.CHANGE_PWD_MSG_FINISH);
						else
							showToast(this.tips);
						
						finish();
					}
				})
				.setMethodName(Constants.MENTHOD.COMMON_EDIT_NEWPWD)
				.setArgs(args)
				.setSyncHandler(null));
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
	public void doValicode(){
		
	
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("phone", editPhone.getText().toString());
		
		
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
		}).setMethodName(Constants.MENTHOD.COMMON_PWD_VALICODE)
		.setSyncHandler(null)
		.setArgs(args));
	}
}
