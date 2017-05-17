package com.tony.sdkview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class SdkSettingView extends BaseLinearLayout {

	public SdkSettingView() {
		super(GamaterSDK.getInstance().getActivity());
	}

	public SdkSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SdkSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SdkSettingView(Context context) {
		super(context);
	}

	@Override
	public void initView() {
		findViewById(ResourceUtil.getId("other_login")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						startView(ThirdLoginView.createView(getContext()));
					}
				});
		findViewById(ResourceUtil.getId("update_account")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						startView(UpdateAccountView.createView(getContext()));
					}
				});
		findViewById(ResourceUtil.getId("change_passwd")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						startView(ChangePasswdView.createView(getContext()));
					}
				});
		findViewById(ResourceUtil.getId("forget_passwd")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						startView(ForgetPasswdView.createView(getContext()));
					}
				});
		MobUser user = MobUserManager.getInstance().getCurrentUser();
	}

	public static SdkSettingView createView(Context ctx) {
		if (ctx == null)
			return null;
		SdkSettingView view = (SdkSettingView) LayoutInflater.from(ctx)
				.inflate(
						ResourceUtil.getLayoutId("vsgm_tony_sdk_view_setting"),
						null);
		view.initView();
		return view;
	}

	@Override
	public String getViewTitle() {
		return getContext().getString(ResourceUtil.getStringId("vsgm_setting"));
	}

}
