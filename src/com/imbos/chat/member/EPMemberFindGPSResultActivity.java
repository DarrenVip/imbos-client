package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.app.SkinActivity;
import com.imbos.chat.db.DbManager;
import com.imbos.chat.gps.LocationHelper;
import com.imbos.chat.gps.LocationHelper.BdLocationListener;
import com.imbos.chat.image.AsyncImageLoader;
import com.imbos.chat.sync.SimpleSyncListener;
import com.imbos.chat.sync.SyncService;
import com.imbos.chat.sync.SyncTask;
import com.imbos.chat.util.Constants;
import com.imbos.chat.util.DateUtil;
import com.imbos.chat.view.refreshView.PullToRefreshBase.OnRefreshListener;
import com.imbos.chat.view.refreshView.PullToRefreshListView;
import com.imbos.chat.view.urlimagehelper.UrlImageViewHelper;

/**
 * @author yeqing357@gmail.com
 * @date 2013-4-25 下午4:40:03
 * @version V1.0
 */
public class EPMemberFindGPSResultActivity extends SkinActivity implements OnScrollListener {

	private HashMap<String, Object> searchCond = new HashMap<String, Object>();

	private int currentPage = Constants.FIRSTPAGE;
	protected int lastItem = 0;
	private boolean noMoreData = false;

	protected LinearLayout loadingLayout;
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private ArrayList<ArrayList<String>> datalist;
	private EMemberAdapter ememberAdapter;
	private Button btnConfirm;
	private TextView textView = null;
	private String currentTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_gps_result_list);
		
		currentTime = getIntent().getStringExtra("currentTime");
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
		btnConfirm.setText("未获取到地理位置信息，点击获取");
		
		loadingLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.loading_item, null);
		btnNext.setVisibility(View.INVISIBLE);
		setTitle(R.string.CIRCLE_FIND_CP_MEMBER_LIST);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				currentPage = Constants.FIRSTPAGE;
				queryEMember();
			}
		});
		
		actualListView = mPullRefreshListView.getRefreshableView();

		View vEmpty = LayoutInflater.from(this).inflate(R.layout.empty, null);
		mPullRefreshListView.setEmptyView(vEmpty);
		textView = (TextView) mPullRefreshListView.getHeaderLayout().findViewById(R.id.pull_date);
		
		datalist = new ArrayList<ArrayList<String>>();
		ememberAdapter = new EMemberAdapter(datalist, this);

		actualListView.setOnScrollListener(this);
		actualListView.setAdapter(ememberAdapter);
		actualListView.setOnItemClickListener(itemClickListener);
		actualListView.setDivider(new ColorDrawable(Color.GRAY));
		actualListView.setDividerHeight(1);
		
		onClick(btnConfirm);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:
			 new LocationHelper(this, locationListener).start();
			 break;
		case R.id.title_btn_back:
			finish();
			break;
		default:
			super.onClick(view);
		}
	}
	
	private BdLocationListener locationListener = new BdLocationListener() {
		@Override
		public void needTakePicture(boolean take) {

		}

		@Override
		public void locationListener(double longitude, double latitude, String addr) {
			if (Double.isNaN(longitude) || Double.isNaN(latitude)) {

			} else {
				btnConfirm.setText(addr);
				btnConfirm.setTag(longitude + "," + latitude);
				searchCond.put("longitude", String.valueOf(longitude));
				searchCond.put("latitude", String.valueOf(latitude));
				queryEMember();
			}
		}
	};

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View converView, int postion, long id) {
			Intent intent = new Intent(EPMemberFindGPSResultActivity.this, EPMemberDetailViewPagerActivity.class);
			intent.putExtra("eid", datalist.get(postion - 1).get(0));
			startActivity(intent);
		};
	};

	public void queryEMember() {
		searchCond.put("page", String.valueOf(currentPage));
		searchCond.put("servicefield", null);
		searchCond.put("industry", null);
		searchCond.put("scope", null);

		SyncService.start(new SyncTask().setSyncListener(new SimpleSyncListener(this) {
			@Override
			public void onFailed() {
				super.onFailed();
			}
			
			@Override
			public void onBefore() {
				currentTime = DateUtil.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
				if(currentPage == Constants.FIRSTPAGE) {
					DbManager.delEnterprise_FindID();
				}
				super.onBefore();
			}

			@Override
			public void onSuccess() {
				super.onSuccess();
				loadEMember();
			}
		}).setMethodName(Constants.MENTHOD.ENTERPRISE_NEARENTERPRISE).setArgs(searchCond));
	}

	public void loadEMember() {
		updateLastTime();
		actualListView.removeFooterView(loadingLayout);
		resetLoadingLayout();
		mPullRefreshListView.onRefreshComplete();
		
		//TODO
		ArrayList<ArrayList<String>> temp = DbManager.getEnterprise_FindID(currentTime);
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
		ememberAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		loadingLayout.removeAllViews();
		super.onDestroy();
	}
	
	class EMemberAdapter extends BaseAdapter {
		private ArrayList<ArrayList<String>> mdataList;
		private Context mcontext;
		private ArrayList<String> item;

		public EMemberAdapter(ArrayList<ArrayList<String>> dataList, Context context) {
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
				convertView = lai.inflate(R.layout.emember_find_item, null);
				vh.name = ((TextView) convertView.findViewById(R.id.name));
				vh.industry = ((TextView) convertView.findViewById(R.id.industry));
				vh.logo = ((ImageView) convertView.findViewById(R.id.logo));
				vh.vip = ((ImageView) convertView.findViewById(R.id.vip));
				vh.distance = ((TextView) convertView.findViewById(R.id.distance));
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			item = getItem(position);
			vh.name.setText(item.get(1));
			vh.industry.setText(item.get(2));
			UrlImageViewHelper.setUrlDrawable(vh.logo, AsyncImageLoader.builderUrl(item.get(3)), R.drawable.eplogo_default);
			if("1".equals(item.get(4))) {
				vh.vip.setBackgroundResource(R.drawable.ico_vip);
			} else {
				vh.vip.setBackgroundResource(R.drawable.ico_vip);
			}
			vh.distance.setText(item.get(9));
			vh.distance.setVisibility(View.VISIBLE);
			return convertView;
		}

		class ViewHolder {
			ImageView logo;
			TextView name;
			ImageView vip;
			TextView industry;
			TextView distance;
		}
	}
	
	@Override 
    public void onScroll(AbsListView view, int firstVisibleItem, 
            int visibleItemCount, int totalItemCount) { 
        lastItem = firstVisibleItem + visibleItemCount - 1;
    } 
 
    @Override 
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
        if(lastItem == ememberAdapter.getCount()  && scrollState == SCROLL_STATE_IDLE){  
            getMoreData();
        } 
    }

	private void getMoreData() {
		if (!noMoreData) {
			actualListView.addFooterView(loadingLayout);
			loadingLayout.setOnClickListener(loadingClickListener);
			final int position = ememberAdapter.getCount() + actualListView.getHeaderViewsCount()
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
			queryEMember();

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
