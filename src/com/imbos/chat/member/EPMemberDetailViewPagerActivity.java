package com.imbos.chat.member;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.imbos.chat.R;
import com.imbos.chat.adapter.PageItemAdapter;
import com.imbos.chat.app.SkinActivity;

/**
 * This is an androidpn client demo application.
 * 
 * @author
 */
public class EPMemberDetailViewPagerActivity extends SkinActivity {

	private ViewPager mViewPager;
	private List<View> mViewArray;
	private PageItemAdapter adapter;
	
	private TextView tv_homepage; // 主页
	private TextView tv_aboutus; // 关于我们
	private TextView tv_service; // 服务
	
	private ArrayList<TextView> rbList;
	private int currentItem = 0;
	private String eid;
	
	private EPMemberDetailHomepageView homePageView;
	private EPMemberDetailAboutUsView aboutUsPageView;
	private EPMemberDetailServiceView serviceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.emember_datail_viewpager);
		setTitle(R.string.EM_DETAIL_TITLE);
		btnNext.setVisibility(View.INVISIBLE);
		
		eid = getIntent().getStringExtra("eid");
		initTextView();
		initPageView();

		mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
		adapter = new PageItemAdapter(mViewArray);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new NewsPageChangeListener());
		mViewPager.setCurrentItem(currentItem);
		changeCheckStatus(currentItem);
		
		homePageView.initData();
	}

	private class NewsPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int key) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			changeCheckStatus(arg0);
			switch (arg0) {
			case 0:
				homePageView.initData();
				break;
			case 1:
				aboutUsPageView.initData();
				break;
			case 2:
				serviceView.initData();
				break;
			default:
				break;
			}
		}
	}
	
	private void changeCheckStatus(int checkedId) {
		int length = rbList.size();
		TextView temp = null;
		for (int i = 0; i < length; i++) {
			temp = rbList.get(i);
			if (temp.getId() == checkedId) {
				temp.setTextColor(Color.BLACK);
				temp.setBackgroundResource(R.drawable.title_check_item_bg_common);
			} else {
				temp.setTextColor(Color.BLACK);
				temp.setBackgroundResource(R.drawable.title_bg_common);
			}
		}
	}

	private void initTextView() {
		rbList = new ArrayList<TextView>();
		tv_homepage = (TextView) findViewById(R.id.tv_homepage);
		tv_homepage.setId(0);
		tv_homepage.getPaint().setFakeBoldText(true);

		tv_aboutus = (TextView) findViewById(R.id.tv_aboutus);
		tv_aboutus.setId(1);
		tv_aboutus.getPaint().setFakeBoldText(true);

		tv_service = (TextView) findViewById(R.id.tv_service);
		tv_service.setId(2);
		tv_service.getPaint().setFakeBoldText(true);

		tv_homepage.setOnClickListener(this);
		tv_aboutus.setOnClickListener(this);
		tv_service.setOnClickListener(this);

		rbList.add(tv_homepage);
		rbList.add(tv_aboutus);
		rbList.add(tv_service);
	}

	private void initPageView() {
		homePageView = new EPMemberDetailHomepageView(this, eid);
		aboutUsPageView = new EPMemberDetailAboutUsView(this, eid);
		serviceView = new EPMemberDetailServiceView(this, eid);

		mViewArray = new ArrayList<View>();
		mViewArray.add(homePageView.getMview());
		mViewArray.add(aboutUsPageView.getMview());
		mViewArray.add(serviceView.getMview());
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case 0:
			mViewPager.setCurrentItem(0);
			break;
		case 1:
			mViewPager.setCurrentItem(1);
			break;
		case 2:
			mViewPager.setCurrentItem(2);
			break;
		default:
			break;
		}
		super.onClick(view);
	}
}