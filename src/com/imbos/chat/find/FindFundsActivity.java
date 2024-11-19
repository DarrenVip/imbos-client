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
 * @author wanxianze@gmail.com 2012-6-1
 */
public class FindFundsActivity extends SkinActivity {

	private EditText editSearch;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	
	private ListView listView;
	private NormalAdapter adapter;
	ArrayList<ArrayList<String>> dataList;
	private String currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_funds_activity);

		setTitle(R.string.FIND_FUNDS);

		editSearch = (EditText) findViewById(R.id.edit_search);

		findViewById(R.id.menu_funds_investment).setOnClickListener(this);
		findViewById(R.id.menu_funds_strength).setOnClickListener(this);
		findViewById(R.id.menu_funds_field).setOnClickListener(this);
		findViewById(R.id.menu_funds_cost).setOnClickListener(this);
		findViewById(R.id.btn_green).setOnClickListener(this);
		btnNext.setVisibility(View.INVISIBLE);

		searchCond.put("investmentway", null);
		searchCond.put("financialstrength", null);
		searchCond.put("investmentfield", null);
		searchCond.put("costing", null);
		
		listView = (ListView) findViewById(R.id.mlist);
		dataList = DbManager.getQueryHistroy("funds");
		adapter = new NormalAdapter(this, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menu_funds_investment:
		case R.id.menu_funds_strength:
		case R.id.menu_funds_field:
		case R.id.menu_funds_cost:
			DicActivity.srcActivity = this.getClass();
			startActivity(new Intent(view.getContext(), DicActivity.class).putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.btn_green:
			queryFunds();
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
		case R.id.menu_funds_investment:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("investmentway", ids);
			break;
		case R.id.menu_funds_strength:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("financialstrength", id);
			break;
		case R.id.menu_funds_field:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("investmentfield", ids);
			break;
		case R.id.menu_funds_cost:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("costing", ids);
			break;
		default:
			break;
		}
		super.onNewIntent(intent);
	}

	@SuppressWarnings("unchecked")
	public void queryFunds() {
		searchCond.put("key", editSearch.getText().toString());
		searchCond.put("page", String.valueOf(Constants.FIRSTPAGE));
		
		String investmentway = ((TextView) findViewById(R.id.menu_funds_investment).findViewById(R.id.txt2)).getText().toString();
		String financialstrength = ((TextView) findViewById(R.id.menu_funds_strength).findViewById(R.id.txt2)).getText().toString();
		String investmentfield = ((TextView) findViewById(R.id.menu_funds_field).findViewById(R.id.txt2)).getText().toString();
		String costing = ((TextView) findViewById(R.id.menu_funds_cost).findViewById(R.id.txt2)).getText().toString();
		String key = editSearch.getText().toString();
		if(StringUtil.isEmpty(investmentway) && StringUtil.isEmpty(financialstrength) && StringUtil.isEmpty(investmentfield)
				 && StringUtil.isEmpty(key) && StringUtil.isEmpty(costing)) {
			
		} else {
			Object[] args = new Object[6];
			args[0] = DateUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			JSONStringer stringer = new JSONStringer();
			try {
				stringer.object().key("investmentway").value(investmentway)
						.key("financialstrength").value(financialstrength)
						.key("investmentfield").value(investmentfield)
						.key("costing").value(costing)
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[1] = stringer.toString();
			
			stringer = new JSONStringer();
			try {
				stringer.object().key("investmentway")
						.value(new JSONArray(searchCond.get("investmentway") != null ? (ArrayList<String>)searchCond.get("investmentway") : new ArrayList<String>()))
						.key("investmentfield")
						.value(new JSONArray(searchCond.get("investmentfield") != null ? (ArrayList<String>)searchCond.get("investmentfield") : new ArrayList<String>()))
						.key("financialstrength").value(searchCond.get("financialstrength") != null ? searchCond.get("financialstrength") : "")
						.key("costing").value(searchCond.get("costing") != null ? searchCond.get("costing") : "")
						.key("key").value(key).endObject();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			args[2] = stringer.toString();
			args[3] = "funds";
			args[4] = System.currentTimeMillis();
			StringBuffer buffer = new StringBuffer();
			buffer.append(StringUtil.isEmpty(investmentway) ? "" : investmentway + "、");
			buffer.append(StringUtil.isEmpty(financialstrength) ? "" : financialstrength + "、");
			buffer.append(StringUtil.isEmpty(investmentfield) ? "" : investmentfield + "、");
			buffer.append(StringUtil.isEmpty(costing) ? "" : costing + "、");
			buffer.append(StringUtil.isEmpty(key) ? "" : key);
			args[5] = buffer.toString();
			
			DbManager.insertQueryHistroy(args);
			DbManager.delUnnecessaryQueryHistroy("funds", 3);
			
			dataList.clear();
			dataList.addAll(DbManager.getQueryHistroy("funds"));
			adapter.notifyDataSetChanged();
		}
		
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				DbManager.delFind_funds();
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				startActivity(new Intent(FindFundsActivity.this, FindFundsResultActivity.class).putExtra(Intents.EXTRA_DATA,
						(Serializable) searchCond).putExtra("currentTime", currentTime));

			}
		}).setMethodName(Constants.MENTHOD.FIND_FUNDS).setArgs(searchCond));
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			String query_condition = dataList.get(arg2).get(1);
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(query_condition);
				((TextView) findViewById(R.id.menu_funds_investment).findViewById(R.id.txt2)).setText(jsonObj.getString("investmentway"));
				((TextView) findViewById(R.id.menu_funds_strength).findViewById(R.id.txt2)).setText(jsonObj.getString("financialstrength"));
				((TextView) findViewById(R.id.menu_funds_field).findViewById(R.id.txt2)).setText(jsonObj.getString("investmentfield"));
				((TextView) findViewById(R.id.menu_funds_cost).findViewById(R.id.txt2)).setText(jsonObj.getString("costing"));
				editSearch.setText(jsonObj.getString("key"));
				
				String query_id = dataList.get(arg2).get(2);
				jsonObj = new JSONObject(query_id);
				searchCond.put("investmentway", jsonObj.getJSONArray("investmentway").length() < 1 ? null : JSONUtil.decode(jsonObj.getJSONArray("investmentway").toString()));
				searchCond.put("investmentfield", jsonObj.getJSONArray("investmentfield").length() < 1 ? null : JSONUtil.decode(jsonObj.getJSONArray("investmentfield").toString()));
				searchCond.put("financialstrength", jsonObj.getString("financialstrength").length() < 1 ? null : jsonObj.getString("financialstrength"));
				searchCond.put("costing", jsonObj.getString("costing").length() < 1 ? null : jsonObj.getString("costing"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}