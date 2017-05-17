package com.tony.sdkview;

import com.gamater.util.ResourceUtil;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

public class SdkMessageView extends BaseLinearLayout {

	public SdkMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SdkMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SdkMessageView(Context context) {
		super(context);
	}

	@Override
	public void initView() {
		TextView msgText = (TextView) findViewById(findId("message_text"));
		msgText.setText(message);
	}

	public static SdkMessageView createView(Context ctx, String title, SpannableString message) {
		if (ctx == null)
			return null;
		SdkMessageView view = (SdkMessageView) LayoutInflater.from(ctx)
				.inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_view_sdk_message"), null);
		view.title = title;
		view.message = message;
		view.initView();
		return view;
	}

	private String title;

	private SpannableString message;

	@Override
	public String getViewTitle() {
		return title;
	}
}
