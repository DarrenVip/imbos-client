package com.imbos.chat.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.util.Constants;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class EpApproveManageActivity extends SkinActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_approve_manage_activity);

		setTitle(R.string.EP_APPROVE_MANAGE);
		
		findViewById(R.id.menu_ep_approve_state).setOnClickListener(this);   //认证状态
		findViewById(R.id.menu_ep_license).setOnClickListener(this);   //营业执照
		findViewById(R.id.menu_ep_legal_person).setOnClickListener(this);   //法定代表人
		findViewById(R.id.menu_ep_orgcode).setOnClickListener(this);   //组织机构代码
		findViewById(R.id.menu_ep_operate_type).setOnClickListener(this);   //经营类型
		
		btnNext.setVisibility(View.INVISIBLE);
		queryEpApprove();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		default:
			break;
		}
		super.onClick(view);
	}

	public void queryEpApprove() {
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
				setChildViewValue(response);
			}
		}).setMethodName(Constants.MENTHOD.EMEMBER_FINDVIPEXPLAIN);
		new Thread(task).run();
	}
	
	@SuppressWarnings("unchecked")
	private void setChildViewValue(HashMap<String, ?> response) {
		List<?> ds = (List<?>) response.get("ds");
		List<?> list = (List<?>) ds.get(0);
		ArrayList<String> data = (ArrayList<String>) list.get(1);
		
		((TextView) findViewById(R.id.menu_ep_approve_state).findViewById(R.id.txt2)).setText("1".equals(data.get(0)) ? "已认证" : "未认证");
		((TextView) findViewById(R.id.menu_ep_license).findViewById(R.id.txt2)).setText(data.get(1));
		((TextView) findViewById(R.id.menu_ep_legal_person).findViewById(R.id.txt2)).setText(data.get(2));
		((TextView) findViewById(R.id.menu_ep_orgcode).findViewById(R.id.txt2)).setText(data.get(3));
		((TextView) findViewById(R.id.menu_ep_operate_type).findViewById(R.id.txt2)).setText(DbManager.getDicName(data.get(4)));
	}
}