package com.gamater.sdk.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.WebAPI;
import com.gamater.define.DeviceInfo;
import com.gamater.define.ParameterKey;
import com.gamater.util.DensityUtil;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface", "NewApi" })
public class MVWebFragment extends MVBaseFragment {

	private View btnBack;
	private View btnClose;
	private View progressBar;
	private WebView webView;
	private String mUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_web"), container, false);
		ImageView logo = (ImageView) rootView.findViewById(findId("vsgm_tony_center_logo"));
		if (Config.isOkgameLogo) {
			int padding = DensityUtil.dip2px(getActivity(), 4);
			try {
				logo.setImageResource(findDrawableId("vsgm_tony_logo_okgame"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
			}
		} else if (Config.isGmLogo) {
			try {
				logo.setImageResource(findDrawableId("vsgm_tony_logo_gamemy"));
			} catch (Exception e) {
			}
		} else {
			try {
				logo.setImageResource(findDrawableId("vsgm_tony_logo"));
			} catch (Exception e) {
			}
		}
		webView = (WebView) rootView.findViewById(ResourceUtil.getId("webView"));
		webView.getSettings().setJavaScriptEnabled(true);
		// DOM存储API是否可用
		webView.getSettings().setDomStorageEnabled(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.addJavascriptInterface(new CopyInterface(), "okgame");
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webView.setWebViewClient(wvc);
		// webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings webSettings = webView.getSettings();
		// 支持缩放
		webSettings.setBuiltInZoomControls(true);
		// 将图片调整到适合webview大小
		webSettings.setUseWideViewPort(true);
		// 缩放至屏幕大小
		// webSettings.setLoadWithOverviewMode(true);
		Intent intent = getActivity().getIntent();
		mUrl = intent.getStringExtra("URL");
		LogUtil.printHTTP("mUrl: " + mUrl);
		boolean requestToken = intent.getBooleanExtra("requestToken", false);
		btnBack = rootView.findViewById(ResourceUtil.getId("btnBack"));
		btnClose = rootView.findViewById(ResourceUtil.getId("btnClose"));
		btnBack.setOnClickListener(onClickListener);
		btnClose.setOnClickListener(onClickListener);
		btnClose.setVisibility(View.VISIBLE);

		progressBar = rootView.findViewById(ResourceUtil.getId("loading_process_dialog_progressBar"));

		if (MobUserManager.getInstance().getCurrentUser() == null || !requestToken || Config.isTestMode == 0) {
			webView.loadUrl(mUrl);
		} else {
			updateToken();
		}

		return rootView;
	}

	private BaseOnClickListener onClickListener = new BaseOnClickListener() {
		@Override
		public void onBaseClick(View v) {
			if (v.equals(btnBack)) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					getActivity().finish();
				}
			} else if (v.equals(btnClose)) {
				getActivity().finish();
			}
		}
	};

	private void updateToken() {
		progressBar.setVisibility(View.VISIBLE);
		final DeviceInfo info = DeviceInfo.getInstance(getActivity());
		SdkHttpRequest request = SdkHttpRequest.dynamicToken();
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		long time = System.currentTimeMillis() / 1000;
		String userId = user.getUserid();
		String token = user.getToken();
		// request.initHeader(info);
		request.addPostValue(ParameterKey.USER_ID, userId);
		request.addPostValue(ParameterKey.TOKEN, token);
		// request.addPostValue(ParameterKey.TIME, time + "");
		// request.addPostValue(ParameterKey.FLAG, MD5.crypt(userId + token + WebAPI.LOGIN_KEY + time));
		request.setHttpEventListener(new HttpEventListener() {
			@Override
			public void requestDidSuccess(HttpRequest httpRequest, String result) {
				try {
					JSONObject o = new JSONObject(result);
					if (o.optInt("status", 0) != 1) {
						requestDidFailed(httpRequest);
						return;
					} else {
						try {
							JSONObject data = o.getJSONObject("data");
							String token = data.optString("dynameicToken");
							String userid = data.optString("userid");
							String time = data.optString("time");
							String imei = info.getIMEI();
							String flag = MD5.crypt(token + userid + imei + WebAPI.LOGIN_KEY + time);
							mUrl += "?dynameicToken=" + token + "&userid=" + userid + "&time=" + time + "&imei=" + imei + "&flag=" + flag;
							// Toast.makeText(getContext(), mUrl,Toast.LENGTH_LONG).show();
							webView.loadUrl(mUrl);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestDidStart(HttpRequest httpRequest) {

			}

			@Override
			public void requestDidFailed(HttpRequest httpRequest) {
				showToast(ResourceUtil.getStringId("vsgm_tony_err_unknown"));
				getActivity().finish();
			}
		});
		request.asyncStart();
	}

	WebViewClient wvc = new WebViewClient() {

		@Override
		public void onPageFinished(WebView view, String url) {
			progressBar.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			progressBar.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);
		}

	};

	public class CopyInterface {
		@JavascriptInterface
		@SuppressLint("NewApi")
		public void copy2Android(String str) {
			ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(str.trim());
			showToast("copy success\n" + str);
		}
	}
}
