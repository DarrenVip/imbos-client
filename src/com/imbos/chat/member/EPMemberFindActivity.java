package com.imbos.chat.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.member.DicActivity;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;

/**
 * This is an androidpn client demo application.
 * 
 * @author
 */
public class EPMemberFindActivity extends SkinActivity {

	private EditText editSearch;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	private String currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.emember_find_activity);

		setTitle(R.string.M_ADD_EMEMBER);

		editSearch = (EditText) findViewById(R.id.edit_search);
		
		findViewById(R.id.menu_emember_industry).setOnClickListener(this);
		findViewById(R.id.menu_emember_area).setOnClickListener(this);
		findViewById(R.id.menu_emember_range).setOnClickListener(this);
		findViewById(R.id.btn_green).setOnClickListener(this);
		btnNext.setVisibility(View.INVISIBLE);

		searchCond.put("industry", null);
		searchCond.put("area", null);
		searchCond.put("servicefield", null);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menu_emember_industry:
		case R.id.menu_emember_area:
		case R.id.menu_emember_range:
			DicActivity.srcActivity = this.getClass();
			startActivity(new Intent(view.getContext(), DicActivity.class)
				.putExtra(Intents.EXTRA_MENU_ID, view.getId()));
			break;
		case R.id.btn_green:
			queryEMember();
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
		case R.id.menu_emember_industry:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("industry", ids);
			break;
		case R.id.menu_emember_area:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("area", id);
			break;
		case R.id.menu_emember_range:
			((TextView) findViewById(itemId).findViewById(R.id.txt2)).setText(text);
			searchCond.put("servicefield", id);
		default:
			break;
		}
		super.onNewIntent(intent);
	}

	public void queryEMember() {
		searchCond.put("key", editSearch.getText().toString());
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				DbManager.delEnterprise_FindID();
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				startActivity(new Intent(EPMemberFindActivity.this, EPMemberFindResultActivity.class).putExtra("querycondition", true)
						.putExtra(Intents.EXTRA_DATA, (Serializable) searchCond).putExtra("currentTime", currentTime));
			}
		}).setMethodName(Constants.MENTHOD.ENTERPRISE_FINDEIDFRIEND).setArgs(searchCond));
	}
}