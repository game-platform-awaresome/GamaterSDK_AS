package com.gamater.sdk.common;

import com.gamater.util.DensityUtil;
import com.tony.view.TipsLayout;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;

public class TipsLayoutHelper {

	public static TipsLayout showTipsLayout(View view) {
		return showTipsLayout(view, 0);
	}

	public static TipsLayout showTipsLayout(View view, int heightDp) {
		ViewParent parent = view.getParent();
		ViewGroup parentView = null;
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			parentView = (ViewGroup) view;
		} else if (parent != null && parent instanceof ViewGroup) {
			parentView = (ViewGroup) parent;
		} else {
			parentView = (ViewGroup) view.getRootView();
		}
		if (parentView != null) {
			ViewGroup.LayoutParams rlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightDp == 0
					? ViewGroup.LayoutParams.MATCH_PARENT : DensityUtil.dip2px(view.getContext(), heightDp));
			TipsLayout tipsView = new TipsLayout(view.getContext());

			parentView.addView(tipsView, rlp);
			return tipsView;
		}
		return null;
	}
}
