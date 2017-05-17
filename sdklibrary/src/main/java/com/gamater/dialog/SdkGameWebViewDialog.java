package com.gamater.dialog;

import java.util.Locale;

import com.gamater.account.MobUserManager;
import com.gamater.common.Config;
import com.gamater.util.ResourceUtil;
import com.tony.circularprogress.CircularProgressBar;
import com.tony.viewinterface.BaseOnClickListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

//@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class SdkGameWebViewDialog extends Dialog {

	public static enum WebViewType {
		Policy, Facebook, Other
	}

	private WebViewType type;
	private View rootView;
	private WebView webView;
	private View progressView;
	private boolean isProgressLoading;
	private String url;
	private ImageView logoImage;

	public SdkGameWebViewDialog(Context context, WebViewType type) {
		super(context, ResourceUtil.getStyleId("vsgm_tony_dialog_full"));
		this.type = type;
	}

	public SdkGameWebViewDialog(Context context, String url) {
		this(context, WebViewType.Other);
		this.url = url;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = getLayoutInflater().inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_view_web"), null);
		webView = (WebView) rootView.findViewById(ResourceUtil.getId("webview"));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				hideLoadingView();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showLoadingView();
			}

		});
		setContentView(rootView);

		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(200);
		rootView.startAnimation(animation);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		rootView.findViewById(ResourceUtil.getId("btnBack")).setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				onBackPressed();
			}
		});
		logoImage = (ImageView) findViewById(ResourceUtil.getId("vsgm_tony_center_logo"));
		if (Config.isOkgameLogo) {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_okgame"));
		} else if (Config.isGmLogo) {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_gamemy"));
		} else {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo"));
		}
		progressView = rootView.findViewById(ResourceUtil.getId("progress_view"));
		loadUrl();
	}

	private void loadUrl() {
		if (this.type == WebViewType.Policy) {
			findViewById(ResourceUtil.getId("btnMenu")).setVisibility(View.GONE);
			String lan = Locale.getDefault().getLanguage();
			String country = Locale.getDefault().getCountry();
			String fileName = "privacy_policy";
			if (lan.startsWith("zh")) {
				if (country.toUpperCase().startsWith("CN")) {
					fileName += "_zh_CN";
				} else {
					fileName += "_zh_TW";
				}
			} else if (lan.startsWith("th")) {
				fileName += "_th";
			} else if (lan.startsWith("vi")) {
				fileName += "_vi";
			} else {
				fileName += "_en";
			}
			url = "file:///android_asset/" + fileName + ".htm";
			webView.loadUrl(url);
		} else if (this.type == WebViewType.Facebook) {
			findViewById(ResourceUtil.getId("btnMenu")).setVisibility(View.GONE);
			url = MobUserManager.getInstance().getFbUrl();
			webView.loadUrl(url);
		} else if (this.type == WebViewType.Other) {
			webView.loadUrl(url);
		}
	}

	public void hideMenu() {
		if (rootView != null) {
			rootView.findViewById(ResourceUtil.getId("btnMenu")).setVisibility(View.GONE);
			rootView.findViewById(ResourceUtil.getId("btnBack")).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
		}
	}

	public void showLoadingView() {
		progressView.setVisibility(View.VISIBLE);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(200);
		progressView.startAnimation(animation);
		CircularProgressBar p = (CircularProgressBar) rootView.findViewById(ResourceUtil.getId("progress_bar"));
		p.start();
		isProgressLoading = true;
	}

	public void hideLoadingView() {
		AlphaAnimation animation = new AlphaAnimation(1, 0);
		animation.setDuration(200);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				progressView.setVisibility(View.GONE);
			}
		});
		progressView.startAnimation(animation);
		CircularProgressBar p = (CircularProgressBar) rootView.findViewById(ResourceUtil.getId("progress_bar"));
		p.stop();
		isProgressLoading = false;
	}

	@Override
	public void dismiss() {
		AlphaAnimation aa = new AlphaAnimation(1, 0.3f);
		aa.setDuration(100);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				SdkGameWebViewDialog.super.dismiss();
			}
		});
		rootView.startAnimation(aa);
	}

	@Override
	public void onBackPressed() {
		if (isProgressLoading) {
			hideLoadingView();
			return;
		}
		if (webView.canGoBack()) {
			webView.goBack();
			return;
		}
		super.onBackPressed();
	}
}
