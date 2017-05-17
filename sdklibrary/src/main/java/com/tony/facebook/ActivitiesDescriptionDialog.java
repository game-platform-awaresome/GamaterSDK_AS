package com.tony.facebook;

import com.gamater.util.ResourceUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ActivitiesDescriptionDialog extends Dialog {
	private String description;

	public ActivitiesDescriptionDialog(Context context, String desc) {
		super(context);
		this.description = desc;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = getLayoutInflater().inflate(
				ResourceUtil.getLayoutId("vsgm_tony_sdk_description"), null);
		setContentView(view);
		initView();
	}

	private void initView() {
		TextView descText = (TextView) findViewById(ResourceUtil
				.getId("desc_text"));
		descText.setText(description);
	}

}
