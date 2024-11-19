package com.imbos.chat.setting;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.util.Constants;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class EpUserManageActivity extends SkinActivity {

	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	private Button btn_clearpwd;
	private String uid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_user_manage_activity);

		setTitle(getIntent().getStringExtra(Intents.EXTRA_TITLE));
		btnNext.setVisibility(View.INVISIBLE);
		uid = getIntent().getStringExtra(Intents.EXTRA_UID);
		
		btn_clearpwd = (Button) findViewById(R.id.btn_clearpwd);
		btn_clearpwd.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clearpwd:
			clearUserPwd();
			break;
		default:
			break;
		}
		super.onClick(view);
	}
	
	public void clearUserPwd() {
		searchCond.put("uid", uid);
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(new DataSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				super.onBefore();
			}

			@Override
			public void onSuccess(HashMap<String, ?> response) {
				super.onSuccess(response);
				showToast("清空密码成功");
			}
		}).setMethodName(Constants.MENTHOD.EMEMBER_EDITENTERPRISEUSER).setArgs(searchCond);
		new Thread(task).start();
	}
}