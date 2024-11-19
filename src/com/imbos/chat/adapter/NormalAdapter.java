package com.imbos.chat.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.util.DateUtil;

public class NormalAdapter extends BaseAdapter {
	private ArrayList<ArrayList<String>> dataList;
	private Context mcontext;

	public NormalAdapter(Context context, ArrayList<ArrayList<String>> dataList) {
		this.mcontext = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public ArrayList<String> getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mcontext).inflate(R.layout.query_histroy_item, null);
		ArrayList<String> item = getItem(position);
		
		((TextView) convertView.findViewById(R.id.txt1)).setText(DateUtil.formatSource2Target(item.get(0), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
		((TextView) convertView.findViewById(R.id.txt2)).setText(item.get(4));
		
		if(getCount() > 1) {
			if (position == 0)
				convertView.setBackgroundResource(R.drawable.preference_first_item);
			else if (position == getCount() - 1)
				convertView.setBackgroundResource(R.drawable.preference_last_item);
			else
				convertView.setBackgroundResource(R.drawable.preference_item);
		} else {
			convertView.setBackgroundResource(R.drawable.preference_item);
		}
		return convertView;
	}

}
