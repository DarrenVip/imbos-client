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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
public class FindGsActivity extends SkinActivity {

	private EditText editSearch;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	
	private ListView listView;
	private NormalAdapter adapter;
	ArrayList<ArrayList<String>> dataList;
	private String currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_gs_activity);

		setTitle(R.string.FIND_PRODUCTSERVICE);

		editSearch = (EditText) findViewById(R.id.edit_search);
		
		findViewById(R.id.menu_gs_industry).setOnClickListener(this);
		findViewById(R.id.menu_gs_classification).setOnClickListener(this);
		findViewById(R.id.menu_gs_area).setOnClickListener(this);
		findViewById(R.id.btn_green).setOnClickListener(this);
		btnNext.setVisibility(View.INVISIBLE);

		searchCond.put("industryinvolved", null);
		searchCond.put("productclassification", null);
		searchCond.put("region", null);
		
		listView = (ListView) findViewById(R.id.mlist);
		dataList = DbManager.getQueryHistroy("projects");
		adapter = new NormalAdapter(this, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menu_gs_industry:
		case R.id.menu_gs_classification:
		case R.id.menu_gs_area:
			DicActivity.srcActivity = this.getClass();
			startActivity(new Intent(view.getContext(), DicActivity.class).putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.btn_green:
			queryGs();
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
		case R.id.menu_gs_industry:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("industryinvolved", ids);
			break;
		case R.id.menu_gs_classification:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("productclassification", id);
			break;
		case R.id.menu_gs_area:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("region", id);
		default:
			break;
		}
		super.onNewIntent(intent);
	}

	@SuppressWarnings("unchecked")
	public void queryGs() {
		searchCond.put("key", editSearch.getText().toString());
		searchCond.put("page", String.valueOf(Constants.FIRSTPAGE));
		
		String industryinvolved = ((TextView) findViewById(R.id.menu_gs_industry).findViewById(R.id.txt2)).getText().toString();
		String productclassification = ((TextView) findViewById(R.id.menu_gs_classification).findViewById(R.id.txt2)).getText().toString();
		String region = ((TextView) findViewById(R.id.menu_gs_area).findViewById(R.id.txt2)).getText().toString();
		String key = editSearch.getText().toString();
		if(StringUtil.isEmpty(industryinvolved) && StringUtil.isEmpty(productclassification) && StringUtil.isEmpty(region)
				 && StringUtil.isEmpty(key)) {
			
		} else {
			Object[] args = new Object[6];
			args[0] = DateUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			JSONStringer stringer = new JSONStringer();
			try {
				stringer.object().key("industryinvolved").value(industryinvolved)
						.key("productclassification").value(productclassification)
						.key("region").value(region)
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[1] = stringer.toString();
			
			stringer = new JSONStringer();
			try {
				stringer.object().key("industryinvolved")
						.value(new JSONArray(searchCond.get("industryinvolved") != null ? (ArrayList<String>)searchCond.get("industryinvolved") : new ArrayList<String>()))
						.key("productclassification").value(searchCond.get("productclassification") != null ? searchCond.get("productclassification") : "")
						.key("region").value(searchCond.get("region") != null ? searchCond.get("region") : "")
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[2] = stringer.toString();
			args[3] = "projects";
			args[4] = System.currentTimeMillis();
			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtil.isEmpty(industryinvolved) ? "" : industryinvolved + "、");
			buffer.append(StringUtil.isEmpty(productclassification) ? "" : productclassification + "、");
			buffer.append(StringUtil.isEmpty(region) ? "" : region + "、");
			buffer.append(StringUtil.isEmpty(key) ? "" : key);
			args[5] = buffer.toString();
			
			DbManager.insertQueryHistroy(args);
			DbManager.delUnnecessaryQueryHistroy("projects", 3);
			
			dataList.clear();
			dataList.addAll(DbManager.getQueryHistroy("projects"));
			adapter.notifyDataSetChanged();
		}
		
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				DbManager.delFind_gs();
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				startActivity(new Intent(FindGsActivity.this, FindGsResultActivity.class).putExtra(Intents.EXTRA_DATA,
						(Serializable) searchCond).putExtra("currentTime", currentTime));
			}
		}).setMethodName(Constants.MENTHOD.FIND_GS).setArgs(searchCond));
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String query_condition = dataList.get(arg2).get(1);
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(query_condition);
				((TextView) findViewById(R.id.menu_gs_industry).findViewById(R.id.txt2)).setText(jsonObj.getString("industryinvolved"));
				((TextView) findViewById(R.id.menu_gs_classification).findViewById(R.id.txt2)).setText(jsonObj.getString("productclassification"));
				((TextView) findViewById(R.id.menu_gs_area).findViewById(R.id.txt2)).setText(jsonObj.getString("region"));
				editSearch.setText(jsonObj.getString("key"));
				
				String query_id = dataList.get(arg2).get(2);
				jsonObj = new JSONObject(query_id);
				searchCond.put("industryinvolved", jsonObj.getJSONArray("industryinvolved").length() < 1 ? null : JSONUtil.decode(jsonObj.getJSONArray("industryinvolved").toString()));
				searchCond.put("productclassification", jsonObj.getString("productclassification").length() < 1 ? null : jsonObj.getString("productclassification"));
				searchCond.put("region", jsonObj.getString("region").length() < 1 ? null : jsonObj.getString("region"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}