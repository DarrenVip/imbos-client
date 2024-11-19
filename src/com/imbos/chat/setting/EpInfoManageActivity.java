package com.imbos.chat.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.EditTextActivity;
import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.member.DicActivity;
import com.imbos.chat.member.RegionActivity;
import com.imbos.chat.sync.DataSyncListener;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SimpleSyncTask;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class EpInfoManageActivity extends SkinActivity {

	private DataSyncListener syncListener;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_info_manage_activity);

		setTitle(R.string.EP_INFORMESSION_MANAGE);
		
		findViewById(R.id.menu_ep_shortname).setOnClickListener(this);    //企业简称
		findViewById(R.id.menu_ep_industry).setOnClickListener(this);    //所属行业
		findViewById(R.id.menu_ep_area).setOnClickListener(this);    //所在地区
		findViewById(R.id.menu_ep_address).setOnClickListener(this);    //公司地址
		findViewById(R.id.menu_ep_zip).setOnClickListener(this);    //邮编
		findViewById(R.id.menu_ep_intro).setOnClickListener(this);    //企业简介
		findViewById(R.id.menu_ep_legal_person).setOnClickListener(this);    //法定代表人
		findViewById(R.id.menu_ep_orgcode).setOnClickListener(this);    //组织机构代码
		findViewById(R.id.menu_ep_operate_type).setOnClickListener(this);    //经营类型
		
		findViewById(R.id.menu_ep_shortname).setTag("ABBREVIATION");    //企业简称
		findViewById(R.id.menu_ep_industry).setTag("INDUSTRY");    //所属行业
		findViewById(R.id.menu_ep_area).setTag("CITY");    //所在地区
		findViewById(R.id.menu_ep_address).setTag("CONTACT_ADD");    //公司地址
		findViewById(R.id.menu_ep_zip).setTag("POSTCODE");    //邮编
		findViewById(R.id.menu_ep_intro).setTag("SUMMARY");    //企业简介
		findViewById(R.id.menu_ep_legal_person).setTag("REPRESENTATIVE");    //法定代表人
		findViewById(R.id.menu_ep_orgcode).setTag("ORGCODE");    //组织机构代码
		findViewById(R.id.menu_ep_operate_type).setTag("SERVICE_FIELD");    //经营类型
		
		syncListener = new DataSyncListener(this) {
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
		};
		
		btnNext.setVisibility(View.INVISIBLE);
		queryEPInfo();
	}
	
	private String getFieldValue(int id) {
		return ((TextView) findViewById(id).findViewById(R.id.txt_value)).getText().toString();
	}

	private String getFieldText(int id) {
		return ((TextView) findViewById(id).findViewById(R.id.txt_name)).getText().toString();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.menu_ep_shortname:    //企业简称
			case R.id.menu_ep_address:    //公司地址
			case R.id.menu_ep_zip:    //邮编
			case R.id.menu_ep_legal_person:    //法定代表人
			case R.id.menu_ep_orgcode:    //组织机构代码
				startActivityForResult(new Intent(this, EditTextActivity.class)
					.putExtra(Intents.EXTRA_TITLE, getFieldText(id))
					.putExtra(Intents.EXTRA_TEXT, getFieldValue(id)), id);
				break;
			case R.id.menu_ep_intro:    //企业简介
				startActivityForResult(new Intent(this, EditTextActivity.class)
				.putExtra(Intents.EXTRA_TITLE, getFieldText(id))
				.putExtra(Intents.EXTRA_TEXT, getFieldValue(id))
				.putExtra(Intents.EXTRA_SINGE,false),id);
				
				break;
			case R.id.menu_ep_area:    //所在地区
				startActivity(new Intent(view.getContext(), RegionActivity.class)
				.putExtra(Intents.EXTRA_MENU_ID, view.getId()));
				break;
			case R.id.menu_ep_industry:    //所属行业
			case R.id.menu_ep_operate_type:    //经营类型
				DicActivity.srcActivity = this.getClass();
				startActivityForResult(new Intent(this, DicActivity.class)
					.putExtra(Intents.EXTRA_MENU_ID, id)
					.putExtra(Intents.EXTRA_TITLE, getFieldText(id)), id);
				break;
			default:
				break;
		}
		super.onClick(view);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		int itemId = intent.getIntExtra(Intents.EXTRA_MENU_ID, 0);
		String text = intent.getStringExtra(Intents.EXTRA_TEXT);
		String id = intent.getStringExtra(Intents.EXTRA_ID);
		ArrayList<String> ids = intent.getStringArrayListExtra(Intents.EXTRA_ID);
		
		String filedValue = null;
		String filedName = null;
		switch (itemId) {
			case R.id.menu_ep_industry:    //所属行业
				filedValue = id;
				filedName = findViewById(itemId).getTag().toString();
				syncFiledValue(filedName, filedValue);
				break;
			case R.id.menu_ep_area:    //所在地区
				filedValue = text;
				filedName = findViewById(itemId).getTag().toString();
				syncFiledValue(filedName, filedValue);
				break;
			case R.id.menu_ep_operate_type:    //经营类型:
				filedValue = fromArray(ids);
				filedName = findViewById(itemId).getTag().toString();
				syncFiledValue(filedName, filedValue);
				break;
			default:
				break;
		}
	}
	
	private String fromArray(ArrayList<String> ids) {
		StringBuffer strIds = new StringBuffer();
		Iterator<String> iter = ids.iterator();
		while(iter.hasNext()) {
			strIds.append(iter.next()).append(",");
		}
		return strIds.toString();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=RESULT_OK)
			return;
		switch (requestCode) {
			case R.id.menu_ep_shortname:    //企业简称
			case R.id.menu_ep_address:    //公司地址
			case R.id.menu_ep_zip:    //邮编
			case R.id.menu_ep_intro:    //企业简介
			case R.id.menu_ep_legal_person:    //法定代表人
			case R.id.menu_ep_orgcode:    //组织机构代码
				String filedValue = data.getStringExtra(Intents.EXTRA_TEXT);	
				String filedName = findViewById(requestCode).getTag().toString();	
				syncFiledValue(filedName, filedValue);
			break;
			default:
			break;
		}
	}
	
	public void syncFiledValue(String filedName, String filedValue) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("fieldname", filedName);
		args.put("value", filedValue);

		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				showToast(R.string.GENERAL_DATA_SAVE_SUCCESS);
				queryEPInfo();
			}
		}).setMethodName(Constants.MENTHOD.EMEMBER_EDITEIM).setArgs(args));
	}
	
	public void queryEPInfo() {
		SimpleSyncTask task = new SimpleSyncTask().setSyncListener(syncListener)
		.setMethodName(Constants.MENTHOD.EMEMBER_FINDEIM);
		new Thread(task).start();
	}
	
	@SuppressWarnings("unchecked")
	private void setChildViewValue(HashMap<String, ?> response) {
		List<?> ds = (List<?>) response.get("ds");
		List<?> list = (List<?>) ds.get(0);
		ArrayList<String> data = (ArrayList<String>) list.get(1);
		
		((TextView) findViewById(R.id.menu_ep_shortname).findViewById(R.id.txt_value)).setText(data.get(0));    //企业简称
		((TextView) findViewById(R.id.menu_ep_industry).findViewById(R.id.txt_value)).setText(DbManager.getDicName(data.get(1)));    //所属行业
		((TextView) findViewById(R.id.menu_ep_area).findViewById(R.id.txt_value)).setText(data.get(2) + data.get(3));    //所在地区
		((TextView) findViewById(R.id.menu_ep_address).findViewById(R.id.txt_value)).setText(data.get(4));    //公司地址
		((TextView) findViewById(R.id.menu_ep_zip).findViewById(R.id.txt_value)).setText(data.get(5));    //邮编
		((TextView) findViewById(R.id.menu_ep_intro).findViewById(R.id.txt_value)).setText(data.get(6));    //企业简介

		((TextView) findViewById(R.id.menu_ep_legal_person).findViewById(R.id.txt_value)).setText(data.get(7));    //法定代表人
		((TextView) findViewById(R.id.menu_ep_orgcode).findViewById(R.id.txt_value)).setText(data.get(8));    //组织机构代码
		((TextView) findViewById(R.id.menu_ep_operate_type).findViewById(R.id.txt_value)).setText(DbManager.getDicName(data.get(9)));    //经营类型
	}
}