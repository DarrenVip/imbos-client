package com.imbos.chat.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PageItemAdapter extends PagerAdapter {
	private List<View> mViewArray;

	public PageItemAdapter(List<View> mViewArray) {
		super();
		this.mViewArray = mViewArray;
	}

	@Override
	public int getCount() {
		return mViewArray.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {	
		container.addView(mViewArray.get(position));
		return mViewArray.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object view) {
		container.removeView(mViewArray.get(position));
	}

	@Override
	public boolean isViewFromObject(View view, Object xview) {
		return view == xview;
	}

	@Override
	public void finishUpdate(View view) {
	}

	@Override
	public void restoreState(Parcelable p, ClassLoader c) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View view) {

	}
}