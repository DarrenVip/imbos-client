package com.imbos.chat;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.find.FindFundsActivity;
import com.imbos.chat.find.FindGsActivity;
import com.imbos.chat.find.FindSubjectActivity;

/**
 * 发现界面
 * 
 * @author xianze
 * 
 */
public class FindActivity extends SkinActivity {

	private ListView listView;
	private ArrayAdapter<int[]> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find);
		setTitle(R.string.MENU_FIND);
		btnBack.setVisibility(View.INVISIBLE);
		listView = (ListView) findViewById(R.id.mlist);

		arrayAdapter = new ArrayAdapter<int[]>(this, R.layout.normal_item, R.id.txt1, new ArrayList<int[]>()) {
			public View getView(int position, View convertView, android.view.ViewGroup parent) {

				convertView = super.getView(position, convertView, parent);
				int[] item = getItem(position);

				((TextView) convertView.findViewById(R.id.txt1)).setText(item[1]);

				if (item[2] > 0)
					((TextView) convertView.findViewById(R.id.txt2)).setText(item[2]);
				else
					((TextView) convertView.findViewById(R.id.txt2)).setText(null);

				if (position == 0)
					convertView.setBackgroundResource(R.drawable.preference_first_item);
				else if (position == getCount() - 1)
					convertView.setBackgroundResource(R.drawable.preference_last_item);
				else
					convertView.setBackgroundResource(R.drawable.preference_item);

				return convertView;
			};
		};

		// [iconId,nameId,tipId]
		arrayAdapter.add(new int[] { 0, R.string.F_FUND, -1 });
		arrayAdapter.add(new int[] { 0, R.string.F_PROJECT, -1 });
		arrayAdapter.add(new int[] { 0, R.string.F_SD, R.string.CIRCLE_NEW_TIP });
		arrayAdapter.add(new int[] { 0, R.string.F_TALENT, R.string.CIRCLE_NEW_TIP });
		arrayAdapter.add(new int[] { 0, R.string.F_PRODUCT, -1 });
		arrayAdapter.add(new int[] { 0, R.string.F_BUSINESS, R.string.CIRCLE_NEW_TIP });

		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(itemClickListener);
		btnNext.setVisibility(View.INVISIBLE);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			switch (arg2) {
			case 0:
				goActivity(FindFundsActivity.class);
				break;
			case 1:
				goActivity(FindSubjectActivity.class);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				goActivity(FindGsActivity.class);
				break;
			case 5:
				break;
			default:
				break;
			}
		}
	};
	
}
