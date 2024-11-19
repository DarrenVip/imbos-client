package com.imbos.chat.view.refreshView;

import android.view.View;

/**
 * @author wanxianze@gmail.com 2012-6-1
 */
public interface EmptyViewMethodAccessor {

	/**
	 * Calls upto AdapterView.setEmptyView()
	 * 
	 * @param View
	 *            to set as Empty View
	 */
	public void setEmptyViewInternal(View emptyView);

	/**
	 * Should call PullToRefreshBase.setEmptyView() which will then
	 * automatically call through to setEmptyViewInternal()
	 * 
	 * @param View
	 *            to set as Empty View
	 */
	public void setEmptyView(View emptyView);

}