package com.tony.viewinterface;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseOnClickListener implements OnClickListener {

	@Override
	public final void onClick(View v) {
		onBaseClick(v);
	}

	protected String getViewClickTag(View v) {
		return v.getTag() + "";
	}

	public abstract void onBaseClick(View v);
}
