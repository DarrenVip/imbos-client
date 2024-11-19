package com.imbos.chat.find;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.ChatApp;
import com.imbos.chat.app.Intents;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.model.NormalDetail;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.view.refreshView.PullToRefreshBase.OnRefreshListener;
import com.imbos.chat.view.refreshView.PullToRefreshListView;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public class FindFundsResultActivity extends SkinActivity implements OnScrollListener {

	private HashMap<String, Object> searchCond = new HashMap<String, Object>();
	
	private int currentPage = Constants.FIRSTPAGE;
	protected int lastItem = 0;
	private boolean noMoreData = false;

	protected LinearLayout loadingLayout;
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private ArrayList<ArrayList<String>> datalist;
	private FundsAdapter fundsAdapter;

	private TextView textView = null;
	private boolean fromSetting;
	private String currentTime;
	private SimpleSyncListener syncListener;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normal_result_list);

		currentTime = getIntent().getStringExtra("currentTime");
		fromSetting = getIntent().getBooleanExtra("fromSetting", false);
		searchCond = (HashMap<String, Object>) getIntent().getSerializableExtra(Intents.EXTRA_DATA);

		btnNext.setVisibility(View.INVISIBLE);
		loadingLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.loading_item, null);
		setTitle(R.string.FIND_FUNDS_LIST);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				currentPage = Constants.FIRSTPAGE;
				queryFunds();
			}
		});
		
		actualListView = mPullRefreshListView.getRefreshableView();

		View vEmpty = LayoutInflater.from(this).inflate(R.layout.empty, null);
		mPullRefreshListView.setEmptyView(vEmpty);
		textView = (TextView) mPullRefreshListView.getHeaderLayout().findViewById(R.id.pull_date);
		if(fromSetting) {
			datalist = new ArrayList<ArrayList<String>>();
		} else {
			datalist = DbManager.getFind_fundsList(currentTime);
			if(datalist.size() < Constants.PERPAGECOUNT) {
				noMoreData = true;
			}
		}
		fundsAdapter = new FundsAdapter(datalist, this);

		actualListView.setAdapter(fundsAdapter);
		actualListView.setOnScrollListener(this);
		actualListView.setOnItemClickListener(itemClickListener);
		actualListView.setDivider(new ColorDrawable(0x00ffffff));
		actualListView.setDividerHeight(1);
		
		syncListener = new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				if(currentPage == Constants.FIRSTPAGE) {
					DbManager.delFind_funds();
				}
				super.onBefore();
			}
			
			@Override
			public void onSuccess() {
				super.onSuccess();
				loadFunds();
			}
		};
		
		if(fromSetting) {
			queryFunds();
		}
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
			ArrayList<String> templist = datalist.get(postion - 1);
			String title = templist.get(0);
			String intro = templist.get(4);
			String mainTitle = getString(R.string.FIND_FUNDS_DETAIL);
			String detail = (templist.get(1) == null ? "" : templist.get(1))
					+ "\n" + (templist.get(2) == null ? "" : templist.get(2))
					+ "\n" + (templist.get(3) == null ? "" : templist.get(3));
			NormalDetail normal = new NormalDetail(title, detail, intro);
			normal.setMainTitle(mainTitle);
			startActivity(new Intent(adapterView.getContext(), NormalDetailActivity.class).putExtra("detail", normal));
		};
	};

	public void queryFunds() {
		if(fromSetting) {
			searchCond = new HashMap<String, Object>();
			searchCond.put("eid", ChatApp.getConfig().saasUid);
		}
		searchCond.put("page", String.valueOf(currentPage));
		SyncService.start(new SyncTask().setSyncListener(syncListener)
				.setMethodName(fromSetting ? Constants.MENTHOD.EMEMBER_FINDFUND : Constants.MENTHOD.FIND_FUNDS).setArgs(searchCond));
	}

	public void loadFunds() {
		updateLastTime();
		actualListView.removeFooterView(loadingLayout);
		resetLoadingLayout();
		mPullRefreshListView.onRefreshComplete();
		
		ArrayList<ArrayList<String>> temp = DbManager.getFind_fundsList(currentTime);
		if(temp == null) {
			temp = new ArrayList<ArrayList<String>>();
		}
		if(temp.size() < Constants.PERPAGECOUNT) {
			noMoreData = true;
		}
		if(currentPage == Constants.FIRSTPAGE) {
			datalist.clear();
		}
		datalist.addAll(temp);
		fundsAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		loadingLayout.removeAllViews();
		super.onDestroy();
	}
	
	class FundsAdapter extends BaseAdapter {
		private ArrayList<ArrayList<String>> mdataList;
		private Context mcontext;

		public FundsAdapter(ArrayList<ArrayList<String>> dataList, Context context) {
			this.mdataList = dataList;
			this.mcontext = context;
		}

		@Override
		public int getCount() {
			return mdataList.size();
		}

		@Override
		public ArrayList<String> getItem(int position) {
			return mdataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater lai = LayoutInflater.from(mcontext);
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = lai.inflate(R.layout.find_funds_item, null);
				vh.name = ((TextView) convertView.findViewById(R.id.name));
				vh.investment = ((TextView) convertView.findViewById(R.id.investment));
				vh.strength = ((TextView) convertView.findViewById(R.id.strength));
				vh.field = ((TextView) convertView.findViewById(R.id.field));
				vh.intro = ((TextView) convertView.findViewById(R.id.intro));
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ArrayList<String> item = getItem(position);
			vh.name.setText(item.get(0));
			vh.investment.setText(item.get(1));
			vh.strength.setText(item.get(2));
			vh.field.setText(item.get(3));
			vh.intro.setText(item.get(4));
			return convertView;
		}

		class ViewHolder {
			TextView name; // 名称
			TextView investment; // 投资方式
			TextView strength; // 实力
			TextView field; // 领域
			TextView intro; // 简介
		}
	}
	
	@Override 
    public void onScroll(AbsListView view, int firstVisibleItem, 
            int visibleItemCount, int totalItemCount) { 
        lastItem = firstVisibleItem + visibleItemCount - 1;
    } 
 
    @Override 
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
        Log.i(TAG, "scrollState="+scrollState); 
        if(lastItem == fundsAdapter.getCount()  && scrollState == SCROLL_STATE_IDLE){  
            Log.i(TAG, "拉到最底部"); 
            getMoreData();
        } 
    }

	private void getMoreData() {
		if (!noMoreData) {
			actualListView.addFooterView(loadingLayout);
			loadingLayout.setOnClickListener(loadingClickListener);
			final int position = fundsAdapter.getCount() + actualListView.getHeaderViewsCount()
					+ actualListView.getFooterViewsCount();
			actualListView.post(new Runnable() {
				@Override
				public void run() {
					actualListView.smoothScrollToPosition(position + 1, position);
				}
			});
		}
	}

	private View.OnClickListener loadingClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			currentPage++;
			queryFunds();

			loadingLayout.setOnClickListener(null);
			loadingLayout.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			loadingLayout.findViewById(R.id.text_show).setVisibility(View.VISIBLE);
			loadingLayout.findViewById(R.id.text_addmore).setVisibility(View.GONE);
		}
	};

	private void resetLoadingLayout() {
		loadingLayout.findViewById(R.id.progressBar).setVisibility(View.GONE);
		loadingLayout.findViewById(R.id.text_show).setVisibility(View.GONE);
		loadingLayout.findViewById(R.id.text_addmore).setVisibility(View.VISIBLE);
	}
	
	private void updateLastTime() {
		textView.setText("最后更新:" + DateUtil.formatDate2Str(new Date(), "HH:mm"));
	}
}
