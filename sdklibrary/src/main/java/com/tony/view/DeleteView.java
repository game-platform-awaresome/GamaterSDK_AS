package com.tony.view;

import com.gamater.util.ResourceUtil;

import android.content.Context;
import android.widget.ImageView;

public class DeleteView extends ImageView {
	private boolean isDelete;

	public DeleteView(Context context) {
		super(context);
		setScaleType(ScaleType.CENTER_INSIDE);
		// int padding = DensityUtil.dip2px(context, 9);
		// setPadding(padding, padding, padding, padding);
		// setImageResource(ResourceUtil.getDrawableId("vsgm_tony_menu_delete_bg"));
		updateDeteleStatus(false);
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void updateDeteleStatus(boolean isReadyToDelete) {
		if (isReadyToDelete) {
			// setBackgroundColor(Color.argb(88, 255, 0, 0));
			setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_menu_delete_ready_bg"));
		} else {
			// setBackgroundColor(Color.argb(88, 100, 100, 100));
			setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_menu_delete_normal_bg"));
		}
		isDelete = isReadyToDelete;
	}
}
