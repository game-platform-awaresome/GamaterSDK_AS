package com.gamater.sdk.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gamater.common.Config;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.util.DensityUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

@SuppressLint("SetJavaScriptEnabled")
public class NoticeDialog extends Dialog {
	private static final float[] DIMENSIONS_LANDSCAPE = { 380, 320 };
	private static final float[] DIMENSIONS_PORTRAIT = { 320, 480 };
	private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

	private Activity mActivity;
	private OKgameDialogProcess mProcess;
	private View mContent;
	private WebView mWebView;
	private ImageButton mCloseBtn;
	private String mContentString;
	private boolean isShowClose;

	public NoticeDialog(Activity activity, String content) {
		// super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		super(activity);
		this.mActivity = activity;
		this.mContentString = content;
	}

	public NoticeDialog(Activity activity) {
		this(activity, null);
	}

	public void show(String url) {
		try {
			show();
			mWebView.loadUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void show(String data, String mimeType, String encoding) {
		try {
			show();
			mContentString = data;
			// mWebView.loadData(data, mimeType, encoding);
			mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isShowClose() {
		return isShowClose;
	}

	public void setShowClose(boolean isShowClose) {
		this.isShowClose = isShowClose;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProcess = new OKgameDialogProcess(mActivity, ResourceUtil.getLayoutId("vsgm_tony_process"));
		mProcess.setCancelable(false);

		mContent = LayoutInflater.from(mActivity).inflate(ResourceUtil.getLayoutId("vsgm_tony_webnotice"), null);
		setUpWebView();
		setUpCloseBtn();

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;

		addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
				(int) (dimensions[1] * scale + 0.5f)));
		setCancelable(false);
	}

	private void setUpCloseBtn() {
		mContent.findViewById(ResourceUtil.getId("back_btn")).setVisibility(View.GONE);
		mContent.findViewById(ResourceUtil.getId("vsgm_tony_pay_process")).setVisibility(View.GONE);
		if (isShowClose) {
			mContent.findViewById(ResourceUtil.getId("close_btn")).setOnClickListener(new BaseOnClickListener() {
				@Override
				public void onBaseClick(View v) {
					NoticeDialog.this.dismiss();
				}
			});
		} else {
			mContent.findViewById(ResourceUtil.getId("close_btn")).setVisibility(View.GONE);
		}
		ImageView logo = (ImageView) mContent.findViewById(ResourceUtil.getId("vsgm_tony_center_logo"));
		if (Config.isOkgameLogo) {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_okgame"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
			}
		} else if (Config.isGmLogo) {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_gamemy"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
			}
		} else {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
			}
		}
	}

	private void setUpWebView() {
		mWebView = (WebView) mContent.findViewById(ResourceUtil.getId("vsgm_tony_pay_webview"));
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setBuiltInZoomControls(false);

		if (mContentString != null)
			mWebView.loadData(mContentString, "text/html; charset=UTF-8", null);
		mWebView.setLayoutParams(FILL);
//		mWebView.setBackgroundColor((Color.parseColor("#3F3A58")));//
//		mWebView.getBackground().setAlpha(2); // 设置填充透明度 范围：0-255 添加透明度其实也没有效果 空指针xml中设置
		mWebView.setWebViewClient(new CustomWebViewClient());
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// if (!NoticeDialog.this.isShowing())
			// return true;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			mActivity.startActivity(intent);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (NoticeDialog.this.isShowing()) {
				super.onPageStarted(view, url, favicon);
				mProcess.show();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProcess.dismiss();
		}
	}
}
