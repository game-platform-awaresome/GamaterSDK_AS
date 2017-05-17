package com.tony.facebook;

import java.util.HashMap;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class FacebookViewAdapter extends PagerAdapter {
	private Context context;
	private HashMap<Integer, View> viewMap = new HashMap<Integer, View>();

	public FacebookViewAdapter(Context ctx) {
		this.context = ctx;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (viewMap.containsKey(0)) {
			((FbLikeView) viewMap.get(0)).notifyDataSetChanged();
		}
		if (viewMap.containsKey(1)) {
			((FbShareView) viewMap.get(1)).notifyDataSetChanged();
		}
		if (viewMap.containsKey(2)) {
			((FbInviteView) viewMap.get(2)).notifyDataSetChanged();
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = null;
		if (viewMap.containsKey(position)) {
			view = viewMap.get(position);
		} else {
			switch (position) {
			case 0:
				view = FbLikeView.createView(context);
				break;
			case 1:
				view = FbShareView.createView(context);
				break;
			case 2:
				view = FbInviteView.createView(context);
				break;
			default:
				break;
			}
			viewMap.put(position, view);
		}
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewMap.get(position));
	}
}
