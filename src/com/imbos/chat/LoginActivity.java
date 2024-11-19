package com.imbos.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.imbos.chat.app.ChatApp;
import com.imbos.chat.base.BaseActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.setting.Config;
import com.imbos.chat.setting.EpHomepageActivity;
import com.imbos.chat.setting.FindPwdActivity;
import com.imbos.chat.setting.SettingActivity;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SyncLoginHandler;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.FileUtil;
import com.imbos.chat.util.SharedUtils;
import com.imbos.chat.util.StringUtil;

public class LoginActivity extends BaseActivity{
	
	private static final String TAG = LoginActivity.class.getSimpleName();
	private Button btnLogin;
	private TextView txtReg;
	private EditText editUsername,editPassword;
	private SharedUtils sharedUtils;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		btnLogin= (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(clickListener);
		
		txtReg= (TextView) findViewById(R.id.txt_reg);
		txtReg.setOnClickListener(clickListener);
		
		findViewById(R.id.txt_pwd)
			.setOnClickListener(clickListener);
		
		editUsername = (EditText) findViewById(R.id.edit_username);
		editPassword = (EditText) findViewById(R.id.edit_password);
		
		String  fileDir = FileUtil.getFileDir(this);
		Log.d(TAG,"fileDir"+ fileDir);
		sharedUtils = SharedUtils.instance(this).putString(
				Constants.SHARED_FILE_DIR,fileDir);
		
		
		//editPassword.setText("1");	//just for test
		
		((TextView)findViewById(R.id.txt_version)).setText("v"+ChatApp.appVersion);
		
		
		
		
		doUpdate();
		
		if(sharedUtils.contains(Constants.XMPP_PASSWORD)){
			goDesktop();
		}
	};
	
	@Override
	protected void onStart() {
		editUsername.setText(ChatApp.getConfig().saasLoginame);
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.GENERAL_SET,R.string.GENERAL_SET, 1,R.string.GENERAL_SET);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.string.GENERAL_SET){
			goActivity(SettingActivity.class);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_login:
				doLogin();
//				createDebugData();
//				goActivity(MainActivity.class);
				break;
			case R.id.txt_reg:
				goActivity(RegActivity.class);
				break;
			case R.id.txt_pwd:
				goActivity(FindPwdActivity.class);
				break;
//			case R.id.btn_qrCode:
//				startActivity(new Intent(LoginActivity.this, NameCardActivity.class));
//				break;
			default:
				break;
			}
		}
	};
	
	public void doLogin(){
		final String username = editUsername.getText().toString();
		final String password = editPassword.getText().toString();
		final String imei = editPassword.getText().toString();
		
		HashMap<String,String> args = new HashMap<String, String>();
		args.put("u", username);
		args.put("p", password);
		args.put("i", imei);
		ChatApp.getConfig().saasLoginame = username;
		SyncService.start(new SyncTask()
				.setSyncListener(new SimpleSyncListener(this){
					@Override
					public void onFailed() {
						super.onFailed();
					}
					
					@Override
					public void onSuccess() {
						super.onSuccess();
						goDesktop();
					}
				}).setMethodName(Constants.MENTHOD.LOGN)
				.setSyncHandler(new SyncLoginHandler())
				.setArgs(args));
	}
	
	private void goDesktop(){
		String userType = sharedUtils.getString(Constants.SHARED_USER_TYPE, Constants.USER_TYPE.MB);
		if(Constants.USER_TYPE.EP.equals(userType)){
			startActivity(new Intent(getApplicationContext(), EpHomepageActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
		}else{
			((ChatApp)getApplication()).login();
			startActivity(new Intent(getApplicationContext(), MainActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
		}
	}
	private void doUpdate() {
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(new DataSyncListener(this,true) {
			@Override
			public void onFailed() {
				super.onFailed();
				//goActivity(MainActivity.class);
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				ChatApp.updateAppVersion = ((ArrayList<ArrayList<ArrayList<?>>>)response.get("ds")).get(0).get(1).get(0).toString();
				ChatApp.updateAppUrl =  ((ArrayList<ArrayList<ArrayList<?>>>)response.get("ds")).get(0).get(1).get(1).toString();
				//goActivity(MainActivity.class);
			}
		}).setMethodName(Constants.MENTHOD.AUTOUPDATE_GETVERSION);
		new Thread(task).start();
	}
	
	
}
