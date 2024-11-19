package com.imbos.chat.setting;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

public class ChangePwdActivity extends SkinActivity{
	
	private EditText editText;
	private EditText editPassword;
	private EditText editNewPassword;
	private EditText editCfmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pwd);
		
		btnNext.setVisibility(View.INVISIBLE);
		
		setTitle(R.string.ACCOUNT_MODF_PWD);
			

		
		editText = (EditText) findViewById(R.id.edit_num);
		editPassword = (EditText) findViewById(R.id.edit_password);
		editNewPassword = (EditText) findViewById(R.id.edit_newPassword);
		editCfmPassword = (EditText) findViewById(R.id.edit_cfmPassword);
		
		editText.setText(getIntent().getStringExtra(Intents.EXTRA_ID));
		
		findViewById(R.id.btn_green).setOnClickListener(this);
	}
	

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_green:
			syncChangePwd();
			break;
		default:
			super.onClick(view);
		}
	}
	
	public void syncChangePwd(){
		
		
		if(StringUtil.isEmpty(editPassword.getText())){
			editPassword.setError("旧密码不能为空");
			return;
		}
		else if(StringUtil.isEmpty(editNewPassword.getText())){
			editNewPassword.setError("新密码不能为空");
			return;
		}
		else if(!editCfmPassword.getText().toString().equals(editNewPassword.getText().toString())){
			editCfmPassword.setError("两次输入的新密码不一致");
			return;
		}
		
		
		
		Map<String,String> args = new HashMap<String, String>();
		args.put("oldpassword",editPassword.getText().toString());
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
				.setMethodName(Constants.MENTHOD.USER_EDIT_PWD)
				.setArgs(args)
				.setSyncHandler(null));
	}
	
	
}
