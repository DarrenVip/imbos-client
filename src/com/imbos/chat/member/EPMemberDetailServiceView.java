package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.find.NormalDetailActivity;
import com.imbos.chat.model.NormalDetail;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;


/**
 * 
 * @author
 */
public class EPMemberDetailServiceView {
	private View mview;
	private Context mcontext;
	private ListView listView;
	
	private boolean isloaded = false;
	private String eid;
	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	private String currentTime;
	private AllAdapter adapter;
	
	ArrayList<ArrayList<String>> fundlist;
	ArrayList<ArrayList<String>> projectlist;
	ArrayList<ArrayList<String>> gslist;
	ArrayList<Object> allList;
	
	public EPMemberDetailServiceView(Context mcontext, String eid) {
		this.mcontext = mcontext;
		this.eid = eid;
		initView();
	}
	
	private void initView() {
		mview = LayoutInflater.from(mcontext).inflate(R.layout.ep_gs_list, null);
		initChildView();
	}
	
	private void initChildView() {
		listView = (ListView) mview.findViewById(R.id.list);
		allList = new ArrayList<Object>();
		adapter = new AllAdapter(allList, mcontext);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}
	
	private void setChildViewValue() {
		fundlist = DbManager.getFind_fundsList(currentTime);
		projectlist = DbManager.getFind_subjectList(currentTime);
		gslist = DbManager.getFind_gs(currentTime);
		
		allList.add("资金");
		allList.addAll(fundlist);
		allList.add("项目");
		allList.addAll(projectlist);
		allList.add("产品/服务");
		allList.addAll(gslist);
		adapter.notifyDataSetChanged();
	}
	
	public void queryEMemberService() {
		searchCond.put("eid", eid);
		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this.mcontext) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				DbManager.delFind_funds();
				DbManager.delFind_gs();
				DbManager.delFind_subject();
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				isloaded = true;
				setChildViewValue();
			}
		}).setMethodName(Constants.MENTHOD.ENTERPRISE_SEARCHITEMS).setArgs(searchCond));
	}
	
	public void initData() {
		if(isloaded == false) {
			queryEMemberService();
		}
	}

	public View getMview() {
		return mview;
	}
	
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int position, long id) {
			if(position > 0 && position <= fundlist.size()) {				
				ArrayList<String> templist = (ArrayList<String>) allList.get(position);
				String title = templist.get(0);
				String intro = templist.get(4);
				String detail = (templist.get(1) == null ? "" : templist.get(1))
						+ "\n" + (templist.get(2) == null ? "" : templist.get(2))
						+ "\n" + (templist.get(3) == null ? "" : templist.get(3));
				String mainTitle = mcontext.getString(R.string.FIND_FUNDS_DETAIL);
				NormalDetail normal = new NormalDetail(title, detail, intro);
				normal.setMainTitle(mainTitle);
				((Activity) mcontext).startActivity(new Intent(adapterView.getContext(), NormalDetailActivity.class).putExtra("detail", normal));
			} else if(position > fundlist.size() + 1 && position <= fundlist.size() + projectlist.size() + 1) {
				ArrayList<String> templist = (ArrayList<String>) allList.get(position);
				String title = templist.get(0);
				String intro = templist.get(4);
				String detail = (templist.get(1) == null ? "" : templist.get(1))
						+ "\n" + (templist.get(2) == null ? "" : templist.get(2))
						+ "\n" + (templist.get(3) == null ? "" : templist.get(3));
				String mainTitle = mcontext.getString(R.string.FIND_SUBJECT_DETAIL);
				NormalDetail normal = new NormalDetail(title, detail, intro);
				normal.setMainTitle(mainTitle);
				((Activity) mcontext).startActivity(new Intent(adapterView.getContext(), NormalDetailActivity.class).putExtra("detail", normal));				
			} else if(position > fundlist.size() + projectlist.size() + 2) {				
				ArrayList<String> templist = (ArrayList<String>) allList.get(position);
				String title = templist.get(0);
				String intro = templist.get(5);
				String detail = (templist.get(1) == null ? "" : templist.get(1))
						+ "\n" + (templist.get(2) == null ? "" : templist.get(2))
						+ "\n" + (templist.get(3) == null ? "" : templist.get(3)) 
						+ "\n" + (templist.get(4) == null ? "" : templist.get(4)) ;
				String mainTitle = mcontext.getString(R.string.FIND_GS_DETAIL);
				NormalDetail normal = new NormalDetail(title, detail, intro);
				normal.setMainTitle(mainTitle);
				((Activity) mcontext).startActivity(new Intent(adapterView.getContext(), NormalDetailActivity.class).putExtra("detail", normal));
			}
		};
	};
	
	class AllAdapter extends BaseAdapter {
		private ArrayList<Object> mdataList;
		private Context mcontext;

		public AllAdapter(ArrayList<Object> dataList, Context context) {
			this.mdataList = dataList;
			this.mcontext = context;
		}

		@Override
		public int getCount() {
			return mdataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mdataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater lai = LayoutInflater.from(mcontext);
			if(position == 0) {
				convertView = lai.inflate(R.layout.ep_gs_item, null);
				String item = (String) getItem(position);
				TextView txt_category = ((TextView) convertView.findViewById(R.id.txt_category));
				txt_category.setText(item);
			} else if(position > 0 && position <= fundlist.size()) {
				convertView = lai.inflate(R.layout.find_funds_item, null);				
				TextView name = ((TextView) convertView.findViewById(R.id.name));
				TextView investment = ((TextView) convertView.findViewById(R.id.investment));
				TextView strength = ((TextView) convertView.findViewById(R.id.strength));
				TextView field = ((TextView) convertView.findViewById(R.id.field));
				TextView intro = ((TextView) convertView.findViewById(R.id.intro));
				
				ArrayList<String> item = (ArrayList<String>) getItem(position);
				name.setText(item.get(0));
				investment.setText(item.get(1));
				strength.setText(item.get(2));
				field.setText(item.get(3));
				intro.setText(item.get(4));
			} else if(position == fundlist.size() + 1) {
				convertView = lai.inflate(R.layout.ep_gs_item, null);
				String item = (String) getItem(position);
				TextView txt_category = ((TextView) convertView.findViewById(R.id.txt_category));
				txt_category.setText(item);
			} else if (position > fundlist.size() + 1 && position <= fundlist.size() + projectlist.size() + 1) {
				convertView = lai.inflate(R.layout.find_subject_item, null);
				TextView name = ((TextView) convertView.findViewById(R.id.name));
				TextView financing = ((TextView) convertView.findViewById(R.id.financing));
				TextView industry = ((TextView) convertView.findViewById(R.id.industry));
				TextView assets = ((TextView) convertView.findViewById(R.id.assets));
				TextView amount = ((TextView) convertView.findViewById(R.id.amount));
				TextView intro = ((TextView) convertView.findViewById(R.id.intro));

				ArrayList<String> item = (ArrayList<String>) getItem(position);
				name.setText(item.get(0));
				financing.setText(item.get(1));
				industry.setText(item.get(2));
				assets.setText(item.get(3));
				amount.setText(item.get(4));
				intro.setText(item.get(5));
			} else if(position == fundlist.size() + projectlist.size() + 2) {
				convertView = lai.inflate(R.layout.ep_gs_item, null);
				String item = (String) getItem(position);
				TextView txt_category = ((TextView) convertView.findViewById(R.id.txt_category));
				txt_category.setText(item);
			} else {
				convertView = lai.inflate(R.layout.find_gs_item, null);
				TextView name = ((TextView) convertView.findViewById(R.id.name));
				TextView industry = ((TextView) convertView.findViewById(R.id.industry));
				TextView classification = ((TextView) convertView.findViewById(R.id.classification));
				TextView area = ((TextView) convertView.findViewById(R.id.area));
				TextView intro = ((TextView) convertView.findViewById(R.id.intro));

				ArrayList<String> item = (ArrayList<String>) getItem(position);
				name.setText(item.get(0));
				industry.setText(item.get(1));
				classification.setText(item.get(2));
				area.setText(item.get(3));
				intro.setText(item.get(4));
			}
			return convertView;
		}
	}
}