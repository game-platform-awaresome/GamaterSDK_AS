package com.tony.facebook;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.util.ResourceUtil;
import com.tony.view.CustomViewPager;
import com.tony.viewinterface.BaseOnClickListener;

public class SdkFacebookDialog extends Dialog implements HttpEventListener {
	private static SdkFacebookDialog dialog;
	private CustomViewPager viewPager;
	private FacebookViewAdapter pagerAdapter;

	private OKgameDialogProcess processDialog;

	public SdkFacebookDialog(Activity activity) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		setOwnerActivity(activity);
		dialog = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = getLayoutInflater().inflate(
				ResourceUtil.getLayoutId("vsgm_tony_sdk_facebook"), null);
		setContentView(view);
		initView();
		requestData();
	}

	private void requestData() {
		processDialog = new OKgameDialogProcess(getOwnerActivity(),
				ResourceUtil.getLayoutId("vsgm_tony_process"));
		processDialog.setCancelable(true);
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.FB_ACTIVITY);
		request.addPostValue("language", DeviceInfo.getInstance(getContext())
				.getSystemLanguage());
		request.addPostValue("userid", MobUserManager.getInstance()
				.getCurrentUser().getUserid());
		request.addPostValue("roleId", MobUserManager.getInstance()
				.getCurrentUser().getRoleId());
		request.addPostValue("serverId", MobUserManager.getInstance()
				.getCurrentUser().getServerId());
		request.setHttpEventListener(this);
		request.asyncStart();
	}

	private void initView() {
		findViewById(ResourceUtil.getId("btn_close")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						dismiss();
					}
				});
		viewPager = (CustomViewPager) findViewById(ResourceUtil.getId("view_pager"));
		viewPager.setSwipeEnabled(false);
		pagerAdapter = new FacebookViewAdapter(getContext());
		viewPager.setAdapter(pagerAdapter);
		pagerAdapter.notifyDataSetChanged();
		RadioGroup fbGroup = (RadioGroup) findViewById(ResourceUtil.getId("fb_radio_group"));
		fbGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == ResourceUtil.getId("radio_like")) {
					viewPager.setCurrentItem(0, false);
				} else if (checkedId == ResourceUtil.getId("radio_share")) {
					viewPager.setCurrentItem(1, false);
				} else {
					viewPager.setCurrentItem(2, false);
				}
			}
		});
	}

	@Override
	public void dismiss() {
		super.dismiss();
		dialog = null;
		DataHelper.getInstance().reset();
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		if (processDialog.isShowing())
			processDialog.dismiss();
		try {
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 1) {
				JSONObject data = obj.optJSONObject("data");
				DataHelper.getInstance().initData(data);
				pagerAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void checkFbLogin(final Runnable r) {
		if (!FacebookSdk.isInitialized()
				|| FacebookSdk.getApplicationId() == null) {
			return;
		}
		if (AccessToken.getCurrentAccessToken() == null) {
			FacebookHelper.getInstance().fbLogin(
					new FacebookCallback<LoginResult>() {
						@Override
						public void onSuccess(LoginResult result) {
							if (dialog != null) {
								dialog.pagerAdapter.notifyDataSetChanged();
							}
							r.run();
						}

						@Override
						public void onError(FacebookException error) {
						}

						@Override
						public void onCancel() {
						}
					});
		} else {
			r.run();
		}
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		if (!processDialog.isShowing())
			processDialog.show();
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		if (processDialog.isShowing())
			processDialog.dismiss();
	}
}
