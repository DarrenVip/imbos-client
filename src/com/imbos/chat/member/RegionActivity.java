package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.imbos.chat.R;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.setting.EpInfoManageActivity;
import com.imbos.chat.util.StringUtil;

public class RegionActivity extends SkinActivity {

	private ListView listView;
	private SimpleAdapter adapter;
	private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
	private String parentId;
	private String text;
	private int menuId;
	private int step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_area);

		listView = (ListView) findViewById(android.R.id.list);

		parentId = getIntent().getStringExtra(Intents.EXTRA_ID);
		menuId = getIntent().getIntExtra(Intents.EXTRA_MENU_ID, 0);
		String title = getIntent().getStringExtra(Intents.EXTRA_TITLE);
		text = getIntent().getStringExtra(Intents.EXTRA_TEXT);
		step = getIntent().getIntExtra("step", 0);
		step++;

		if (StringUtil.isEmpty(title)) {

		} else {
			setTitle(title);
		}

		data.addAll(DbManager.queryDicItems("area", parentId));
		adapter = new SimpleAdapter(this, data, R.layout.list_text_item, new String[] { "NAME" }, new int[] { R.id.txt1 }) {
			@Override
			public long getItemId(int position) {
				Map<String, ?> item = (Map<String, ?>) getItem(position);
				return Long.parseLong(StringUtil.toString(item.get("ID")));
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				Map<String, ?> item = (Map<String, ?>) getItem(position);
				if ("1".equals(item.get("SUB"))) {
					convertView.findViewById(R.id.subMenu).setVisibility(View.INVISIBLE);
				} else {
					convertView.findViewById(R.id.subMenu).setVisibility(View.VISIBLE);
				}
				return convertView;
			}
		};
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(adapter);

		btnNext.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
			Map<String, ?> item = (Map<String, ?>) adapter.getItem(position);
			String name = StringUtil.toString(item.get("NAME"));
			if (step == 1)
				text = "";
			text = StringUtil.toString(text) + " " + name;
			if ("1".equals(item.get("SUB"))) {
				Class<?> mclass = null;
				switch (menuId) {
				case R.id.menu_ep_area:
					mclass = EpInfoManageActivity.class;
					break;
				default:
					mclass = MemberFindAdvActivity.class;
					break;
				}
				startActivity(new Intent(adapterView.getContext(), mclass).putExtra(Intents.EXTRA_ID, StringUtil.toString(id))
						.putExtra(Intents.EXTRA_TEXT, text).putExtra(Intents.EXTRA_MENU_ID, menuId)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
			} else {
				startActivity(new Intent(adapterView.getContext(), RegionActivity.class)
						.putExtra(Intents.EXTRA_ID, StringUtil.toString(id)).putExtra(Intents.EXTRA_MENU_ID, menuId)
						.putExtra(Intents.EXTRA_TITLE, StringUtil.toString(name)).putExtra(Intents.EXTRA_TEXT, text)
						.putExtra("step", step));
			}
		}
	};

}
