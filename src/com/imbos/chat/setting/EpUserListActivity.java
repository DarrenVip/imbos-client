package com.imbos.chat.setting;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;


/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class EpUserListActivity extends SkinActivity {

	private ListView listView;
	private SQLiteCursor cursor;
	private SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_user_list);
		btnNext.setVisibility(View.INVISIBLE);
		setTitle(R.string.EP_STAFF_MANAGE);

		listView = (ListView) findViewById(android.R.id.list);
		View vEmpty = LayoutInflater.from(this).inflate(R.layout.empty, null);
		listView.setEmptyView(vEmpty);

		listView.setOnItemClickListener(itemClickListener);
		cursor = DbManager.queryMembers(Constants.MENTHOD.EMEMBER_FINDENTERPRISEUSER);// _id,NAME,HEAD,STAR,REAL,DOING
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.member_item, cursor, new String[] { "NAME", "DOING" }, new int[] {
				R.id.txt_name, R.id.txt_remark });
		listView.setAdapter(cursorAdapter);
		
		queryMembers();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}

	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int postion, long id) {
			String name = ((TextView) converView.findViewById(R.id.txt_name)).getText().toString();
			startActivity(new Intent(adapterView.getContext(), EpUserManageActivity.class).putExtra(Intents.EXTRA_TITLE,
					String.valueOf(name)).putExtra(Intents.EXTRA_UID, String.valueOf(id)));
		};
	};

	public void queryMembers() {
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			@Override
			public void onBefore() {
				super.onBefore();
				DbManager.delTable("MEMBER_DETAIL", Constants.MENTHOD.EMEMBER_FINDENTERPRISEUSER);
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				loadMembers();
			}
		}).setMethodName(Constants.MENTHOD.EMEMBER_FINDENTERPRISEUSER));
	}

	public void loadMembers() {
		if (cursor != null)
			cursor.close();
		cursor = DbManager.queryMembers(Constants.MENTHOD.EMEMBER_FINDENTERPRISEUSER);

		cursorAdapter.changeCursor(cursor);
		cursorAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}
}
