package com.gamater.sdk.imagepicker.util;

import android.widget.AbsListView;

public class ImagePickerScrollListener implements AbsListView.OnScrollListener {

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/*
		 * if (scrollState == SCROLL_STATE_IDLE) {
		 * GlideHelper.getRequestManager(view.getContext()).resumeRequests(); }
		 * else {
		 * GlideHelper.getRequestManager(view.getContext()).pauseRequests(); }
		 */
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// Do nothing.
	}
}
