package com.imbos.chat.find;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.NormalAdapter;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.member.DicActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.util.JSONUtil;
import com.imbos.chat.util.StringUtil;

/**
 * This is an androidpn client demo application.
 * 
 * @author
 */
public class FindSubjectActivity extends SkinActivity {

	private EditText editSearch;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	
	private ListView listView;
	private NormalAdapter adapter;
	ArrayList<ArrayList<String>> dataList;
	private String currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_subject_activity);

		setTitle(R.string.FIND_SUBJECT);

		editSearch = (EditText) findViewById(R.id.edit_search);

		findViewById(R.id.menu_subject_financing).setOnClickListener(this);
		findViewById(R.id.menu_subject_industry).setOnClickListener(this);
		findViewById(R.id.menu_subject_assets).setOnClickListener(this);
		findViewById(R.id.menu_subject_amount).setOnClickListener(this);
		findViewById(R.id.btn_green).setOnClickListener(this);
		btnNext.setVisibility(View.INVISIBLE);

		searchCond.put("financingway", null);
		searchCond.put("industryclassification", null);
		searchCond.put("assetvalue", null);
		searchCond.put("financingamount", null);
		
		listView = (ListView) findViewById(R.id.mlist);
		dataList = DbManager.getQueryHistroy("project");
		adapter = new NormalAdapter(this, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menu_subject_financing:
		case R.id.menu_subject_industry:
		case R.id.menu_subject_assets:
		case R.id.menu_subject_amount:
			DicActivity.srcActivity = this.getClass();
			startActivity(new Intent(view.getContext(), DicActivity.class).putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.btn_green:
			querySubject();
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

		switch (itemId) {
		case R.id.menu_subject_financing:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("financingway", ids);
			break;
		case R.id.menu_subject_industry:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("industryclassification", ids);
			break;
		case R.id.menu_subject_assets:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("assetvalue", id);
		case R.id.menu_subject_amount:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("financingamount", id);
			break;
		default:
			break;
		}
		super.onNewIntent(intent);
	}

	@SuppressWarnings("unchecked")
	public void querySubject() {
		searchCond.put("key", editSearch.getText().toString());
		searchCond.put("page", String.valueOf(Constants.FIRSTPAGE));
		
		String financingway = ((TextView) findViewById(R.id.menu_subject_financing).findViewById(R.id.txt2)).getText().toString();
		String industry = ((TextView) findViewById(R.id.menu_subject_industry).findViewById(R.id.txt2)).getText().toString();
		String assetvalue = ((TextView) findViewById(R.id.menu_subject_assets).findViewById(R.id.txt2)).getText().toString();
		String amount = ((TextView) findViewById(R.id.menu_subject_amount).findViewById(R.id.txt2)).getText().toString();
		String key = editSearch.getText().toString();
		if(StringUtil.isEmpty(financingway) && StringUtil.isEmpty(industry) && StringUtil.isEmpty(assetvalue)
				 && StringUtil.isEmpty(amount) && StringUtil.isEmpty(key)) {
			
		} else {
			Object[] args = new Object[6];
			args[0] = DateUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			JSONStringer stringer = new JSONStringer();
			try {
				stringer.object().key("financingway").value(financingway)
						.key("industryclassification").value(industry)
						.key("assetvalue").value(assetvalue)
						.key("financingamount").value(amount)
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[1] = stringer.toString();
			
			stringer = new JSONStringer();
			try {
				stringer.object().key("financingway")
						.value(new JSONArray(searchCond.get("financingway") != null ? (ArrayList<String>)searchCond.get("financingway") : new ArrayList<String>()))
						.key("industryclassification")
						.value(new JSONArray(searchCond.get("industryclassification") != null ? (ArrayList<String>)searchCond.get("industryclassification") : new ArrayList<String>()))
						.key("assetvalue").value(searchCond.get("assetvalue") != null ? searchCond.get("assetvalue") : "")
						.key("financingamount").value(searchCond.get("financingamount") != null ? searchCond.get("financingamount") : "")
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[2] = stringer.toString();
			args[3] = "project";
			args[4] = System.currentTimeMillis();
			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtil.isEmpty(financingway) ? "" : financingway + "、");
			buffer.append(StringUtil.isEmpty(industry) ? "" : industry + "、");
			buffer.append(StringUtil.isEmpty(assetvalue) ? "" : assetvalue + "、");
			buffer.append(StringUtil.isEmpty(amount) ? "" : amount + "、");
			buffer.append(StringUtil.isEmpty(key) ? "" : key);
			args[5] = buffer.toString();
			
			DbManager.insertQueryHistroy(args);
			DbManager.delUnnecessaryQueryHistroy("project", 3);
			
			dataList.clear();
			dataList.addAll(DbManager.getQueryHistroy("project"));
			adapter.notifyDataSetChanged();
		}
		
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				DbManager.delFind_subject();
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				startActivity(new Intent(FindSubjectActivity.this, FindSubjectResultActivity.class).putExtra(Intents.EXTRA_DATA,
						(Serializable) searchCond).putExtra("currentTime", currentTime));

			}
		}).setMethodName(Constants.MENTHOD.FIND_SUBJECT).setArgs(searchCond));
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String query_condition = dataList.get(arg2).get(1);
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(query_condition);
				((TextView) findViewById(R.id.menu_subject_financing).findViewById(R.id.txt2)).setText(jsonObj.getString("financingway"));
				((TextView) findViewById(R.id.menu_subject_industry).findViewById(R.id.txt2)).setText(jsonObj.getString("industryclassification"));
				((TextView) findViewById(R.id.menu_subject_assets).findViewById(R.id.txt2)).setText(jsonObj.getString("assetvalue"));
				((TextView) findViewById(R.id.menu_subject_amount).findViewById(R.id.txt2)).setText(jsonObj.getString("financingamount"));
				editSearch.setText(jsonObj.getString("key"));
				
				String query_id = dataList.get(arg2).get(2);
				jsonObj = new JSONObject(query_id);
				searchCond.put("financingway", jsonObj.getJSONArray("financingway").length() < 1 ? null : JSONUtil.decode(jsonObj.getJSONArray("financingway").toString()));
				searchCond.put("industryclassification", jsonObj.getJSONArray("industryclassification").length() < 1 ? null : JSONUtil.decode(jsonObj.getJSONArray("industryclassification").toString()));
				searchCond.put("assetvalue", jsonObj.getString("assetvalue").length() < 1 ? null : jsonObj.getString("assetvalue"));
				searchCond.put("financingamount", jsonObj.getString("financingamount").length() < 1 ? null : jsonObj.getString("financingamount"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}