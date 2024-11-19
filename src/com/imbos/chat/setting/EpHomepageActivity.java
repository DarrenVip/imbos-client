package com.imbos.chat.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.EditTextActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.find.FindFundsResultActivity;
import com.imbos.chat.find.FindGsResultActivity;
import com.imbos.chat.find.FindSubjectResultActivity;
import com.imbos.chat.member.EPMemberDetailViewPagerActivity;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.StringUtil;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class EpHomepageActivity extends SkinActivity {
	private long flagTimeMillis = System.currentTimeMillis();
	private long flagTimeMillis_ = System.currentTimeMillis();
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_homepage_activity);

		setTitle(R.string.EP_HOMEPAGE);
		
		findViewById(R.id.menu_ep_card).setOnClickListener(this);   //企业名片
		findViewById(R.id.menu_ep_info_manage).setOnClickListener(this);   //企业信息维护
		findViewById(R.id.menu_ep_singleuser_manage).setOnClickListener(this);    //企业个人用户维护
		findViewById(R.id.menu_ep_staff_manage).setOnClickListener(this);    //企业员工管理
		findViewById(R.id.menu_ep_vip).setOnClickListener(this);   //企业VIP
		findViewById(R.id.menu_ep_approve_manage).setOnClickListener(this);    //企业认证管理
		findViewById(R.id.menu_ep_change_pwd).setOnClickListener(this);    //企业认证管理
		findViewById(R.id.menu_ep_dynamic).setOnClickListener(this);    //企业动态
		
		findViewById(R.id.menu_ep_money_manage).setOnClickListener(this);    //资金管理
		findViewById(R.id.menu_ep_project_manage).setOnClickListener(this);    //项目管理
		findViewById(R.id.menu_ep_service).setOnClickListener(this);    //商品/服务
		
		btnNext.setVisibility(View.INVISIBLE);
		btnBack.setText("注销");
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn_back:
			long currTimeMillis = System.currentTimeMillis();
			if ((currTimeMillis - flagTimeMillis_) < 3 * 1000) {
				((ChatApp)this.getApplication()).logout();
				super.onBackPressed();
			} else {
				showToast("再按一次退出");
			}
			flagTimeMillis_ = currTimeMillis;
			break;
		case R.id.menu_ep_card:
			startActivity(new Intent(view.getContext(), EPMemberDetailViewPagerActivity.class).putExtra("eid", ChatApp.getConfig().saasUid));
			break;
		case R.id.menu_ep_info_manage:
			startActivity(new Intent(view.getContext(), EpInfoManageActivity.class));
			break;
		case R.id.menu_ep_approve_manage:
			startActivity(new Intent(view.getContext(), EpApproveManageActivity.class));
			break;
		case R.id.menu_ep_staff_manage:
			startActivity(new Intent(view.getContext(), EpUserListActivity.class));
			break;
		case R.id.menu_ep_dynamic:
			startActivityForResult(new Intent(this, EditTextActivity.class)
			.putExtra(Intents.EXTRA_TITLE, getFieldText(view.getId()))
			.putExtra(Intents.EXTRA_TEXT, getFieldValue(view.getId())), view.getId());
			break;
		case R.id.menu_ep_change_pwd:
			String num = ChatApp.getConfig().saasLoginame;
			if (!StringUtil.isEmpty(num)) {
				startActivity(new Intent(this, ChangePwdActivity.class).putExtra(Intents.EXTRA_ID, num));
			}
			break;
		case R.id.menu_ep_money_manage:
			startActivity(new Intent(view.getContext(), FindFundsResultActivity.class).putExtra("fromSetting", true));
			break;
		case R.id.menu_ep_project_manage:
			startActivity(new Intent(view.getContext(), FindSubjectResultActivity.class).putExtra("fromSetting", true));
			break;
		case R.id.menu_ep_service:
			startActivity(new Intent(view.getContext(), FindGsResultActivity.class).putExtra("fromSetting", true));
			break;
		default:
			break;
		}
		super.onClick(view);
	}
	
	private String getFieldValue(int id) {
		return ((TextView) findViewById(id).findViewById(R.id.txt_value)).getText().toString();
	}

	private String getFieldText(int id) {
		return ((TextView) findViewById(id).findViewById(R.id.txt_name)).getText().toString();
	}
	
	private void setFieldText(int id, String value) {
		((TextView) findViewById(id).findViewById(R.id.txt_value)).setText(value);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=RESULT_OK)
			return;
		switch (requestCode) {
			case R.id.menu_ep_dynamic:    //组织机构代码
				String filedValue = data.getStringExtra(Intents.EXTRA_TEXT);	
				syncFiledValue(R.id.menu_ep_dynamic, filedValue);
			break;
			default:
			break;
		}
	}
	
	public void syncFiledValue(final int id, final String filedValue) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("doing", filedValue);
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
				setFieldText(R.id.menu_ep_dynamic, filedValue);
			}
		}).setMethodName("enterprisesetting_editDoing").setArgs(args));
	}

	public void queryEPMainInfo() {
		new SimpleSyncTask().setSyncListener(new DataSyncListener(this) {
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
//				setChildViewValue(response);
			}
		}).setMethodName(Constants.MENTHOD.EMEMBER_FINDEIM).run();
	}
	
	@SuppressWarnings("unchecked")
	private void setChildViewValue(HashMap<String, ?> response) {
		List<?> ds = (List<?>) response.get("ds");
		List<?> list = (List<?>) ds.get(0);
		ArrayList<String> data = (ArrayList<String>) list.get(1);
		
		((TextView) findViewById(R.id.menu_ep_shortname).findViewById(R.id.txt2)).setText(data.get(0));    //企业简称
		((TextView) findViewById(R.id.menu_ep_industry).findViewById(R.id.txt2)).setText(data.get(1));    //所属行业
		((TextView) findViewById(R.id.menu_ep_area).findViewById(R.id.txt2)).setText(data.get(2) + data.get(3));    //所在地区
		((TextView) findViewById(R.id.menu_ep_address).findViewById(R.id.txt2)).setText(data.get(4));    //公司地址
		((TextView) findViewById(R.id.menu_ep_zip).findViewById(R.id.txt2)).setText(data.get(5));    //邮编
		((TextView) findViewById(R.id.menu_ep_intro).findViewById(R.id.txt2)).setText(data.get(6));    //企业简介

		((TextView) findViewById(R.id.menu_ep_legal_person).findViewById(R.id.txt2)).setText(data.get(7));    //法定代表人
		((TextView) findViewById(R.id.menu_ep_orgcode).findViewById(R.id.txt2)).setText(data.get(8));    //组织机构代码
		((TextView) findViewById(R.id.menu_ep_operate_type).findViewById(R.id.txt2)).setText(data.get(9));    //经营类型
	}
	
	@Override
	public void onBackPressed() {
		long currTimeMillis = System.currentTimeMillis();
		// 3秒退出
		if ((currTimeMillis - flagTimeMillis) < 3 * 1000) {
			super.onBackPressed();
		} else {
			showToast("再按一次退出");
		}
		flagTimeMillis = currTimeMillis;
	}
}