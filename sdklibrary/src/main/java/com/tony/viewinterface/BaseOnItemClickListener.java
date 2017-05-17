package com.tony.viewinterface;

import com.gamater.util.ResourceUtil;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BaseOnItemClickListener extends BaseOnClickListener
		implements OnItemClickListener {

	@Override
	public void onBaseClick(View v) {

	}

	@Override
	protected String getViewClickTag(View v) {
		return v.getTag(ResourceUtil.getId("tag_item_click")) + "";
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		onClick(view);
		onBaseItemClick(adapter, view, position, id);
	}

	public abstract void onBaseItemClick(AdapterView<?> adapter, View view,
			int position, long id);
}
