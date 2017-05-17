package com.tony.sdkview;

import com.gamater.account.po.ThirdType;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ThirdLoginView extends BaseLinearLayout {

	public ThirdLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ThirdLoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ThirdLoginView(Context context) {
		super(context);
	}

	private BaseOnClickListener onClickListener = new BaseOnClickListener() {
		@Override
		public void onBaseClick(View v) {
			thirdLogin((ThirdType) v.getTag());
		}
	};

	@Override
	public void initView() {
		ImageView[] btn = new ImageView[5];
		btn[0] = (ImageView) findViewById(ResourceUtil.getId("btnThirdLogin1"));
		btn[0].setOnClickListener(onClickListener);
		btn[1] = (ImageView) findViewById(ResourceUtil.getId("btnThirdLogin2"));
		btn[1].setOnClickListener(onClickListener);
		btn[2] = (ImageView) findViewById(ResourceUtil.getId("btnThirdLogin3"));
		btn[2].setOnClickListener(onClickListener);
		btn[3] = (ImageView) findViewById(ResourceUtil.getId("btnThirdLogin4"));
		btn[3].setOnClickListener(onClickListener);
		btn[4] = (ImageView) findViewById(ResourceUtil.getId("btnThirdLogin5"));
		btn[4].setOnClickListener(onClickListener);

		int index = 0;
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_FACEBOOK)) {
			setThirdBtn(btn[index], ThirdType.facebook);
			index++;
		}
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_GOOGLE)) {
			setThirdBtn(btn[index], ThirdType.google);
			index++;
		}
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_INSTAGRAM)) {
			setThirdBtn(btn[index], ThirdType.instagram);
			index++;
		}
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_TWITTER)) {
			setThirdBtn(btn[index], ThirdType.twitter);
			index++;
		}
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_RC)) {
			setThirdBtn(btn[index], ThirdType.rc);
			index++;
		}
		for (int i = index; i < 5; i++) {
			btn[i].setVisibility(View.INVISIBLE);
		}
	}

	private void setThirdBtn(ImageView v, ThirdType type) {
		v.setTag(type);
		if (type == ThirdType.facebook) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_fb_selector"));
		} else if (type == ThirdType.google) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_google_selector"));
		} else if (type == ThirdType.instagram) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_camera_selector"));
		} else if (type == ThirdType.twitter) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_twitter_selector"));
		} else if (type == ThirdType.rc) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_rc_selector"));
		}
	}

	public static ThirdLoginView createView(Context ctx) {
		if (ctx == null)
			return null;
		ThirdLoginView view = (ThirdLoginView) LayoutInflater.from(ctx)
				.inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_view_third_login"), null);
		view.initView();
		return view;
	}

	@Override
	public String getViewTitle() {
		return getContext().getString(ResourceUtil.getStringId("vsgm_login_by_other_way"));
	}

}
