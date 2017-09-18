package com.gamater.payment.web.view;

import java.lang.reflect.Method;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gamater.common.Config;
import com.gamater.common.http.MD5;
import com.gamater.common.http.WebAPI;
import com.gamater.define.PaymentParam;
import com.gamater.util.DensityUtil;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class PayView extends FrameLayout {
	public static boolean isWebPayShowing = false;
	private Activity mActivity;
	private String mOrderId;
	private PaymentParam mPaymentParam;

	private WebView mWebView;

	// private OKgameDialogProcess process;
	private View process;

	public PayView(Activity activity, String orderId, PaymentParam param) {
		super(activity);
		this.mActivity = activity;
		this.mOrderId = orderId;
		this.mPaymentParam = param;
		init();
		startPayment();
	}

	private void startPayment() {
		isWebPayShowing = true;
		try {
			Method m = Activity.class.getDeclaredMethod("onPause", new Class[0]);
			m.setAccessible(true);
			m.invoke(mActivity, new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url = Config.getWebPayHost();
		url += "/payment-api/pay?";
		url += "order=" + mOrderId + "&";
		url += "account=" + mPaymentParam.getAccount() + "&";
		url += "currency=" + mPaymentParam.getCurrency() + "&";
		url += "description=" + mPaymentParam.getProductDescription() + "&";
		url += "package=" + mActivity.getPackageName() + "&";
		url += "server=" + mPaymentParam.getServerId() + "&";
		url += "serverName=" + mPaymentParam.getServerName() + "&";
		url += "role=" + mPaymentParam.getRoleId() + "&";
		url += "roleName=" + mPaymentParam.getRoleName() + "&";
		url += "amount=" + mPaymentParam.getAmount() + "&";
		url += "clientId=" + Config.clientId + "&";
		url += "signature="
				+ MD5.crypt(mPaymentParam.getAccount()
						+ mPaymentParam.getAmount()
						+ mPaymentParam.getCurrency()
						+ mPaymentParam.getProductDescription() + mOrderId
						+ mActivity.getPackageName()
						+ mPaymentParam.getRoleId()
						+ mPaymentParam.getRoleName()
						+ mPaymentParam.getServerId()
						+ mPaymentParam.getServerName()
						// + ")&*(jyuij6789IJHN|}\"");
						+ WebAPI.WEB_PAY_KEY);
		url += "&ReleasePlatform=" + Config.gmTitle.toLowerCase();
		LogUtil.printLog("startPayment url: " + url);
		mWebView.loadUrl(url);
	}

	private WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("sms:")) {
				Uri query_string = Uri.parse(url);
				String query_scheme = query_string.getScheme();
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, query_string);
					String[] body = url.split("\\?body=");
					if (query_scheme.equalsIgnoreCase("sms") && body.length > 1) {
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse(body[0]));
						intent.putExtra("sms_body", URLDecoder.decode(body[1]));
					}
					mActivity.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			} else if (url.startsWith("http:") || url.startsWith("https:")) {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// if (!process.isShowing())
			// process.show();
			process.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// if (process.isShowing())
			// process.dismiss();
			process.setVisibility(View.GONE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}
	};

	private BaseOnClickListener onClickListener = new BaseOnClickListener() {
		@Override
		public void onBaseClick(View v) {
			if (v.getId() == ResourceUtil.getId("back_btn")) {
				onBackPressed();
			} else if (v.getId() == ResourceUtil.getId("reload_btn")) {
				mWebView.reload();
			} else if (v.getId() == ResourceUtil.getId("close_btn")) {
				close();
			}
		}
	};

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_webpay"), this);
		ImageView logo = (ImageView) findViewById(ResourceUtil.getId("vsgm_tony_center_logo"));
		if (Config.isOkgameLogo) {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_okgame"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Config.isGmLogo) {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_gamemy"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			int padding = DensityUtil.dip2px(mActivity, 8);
			try {
				logo.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo"));
				logo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		process = findViewById(ResourceUtil.getId("vsgm_tony_pay_process"));
		process.setOnClickListener(onClickListener);
		mWebView = (WebView) findViewById(ResourceUtil.getId("vsgm_tony_pay_webview"));
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(false);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result) {
				return true;
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
				return false;
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});

		findViewById(ResourceUtil.getId("back_btn")).setOnClickListener(onClickListener);
		// findViewById(ResourceUtil.getId("reload_btn")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("close_btn")).setOnClickListener(onClickListener);

		// process = new OKgameDialogProcess(mActivity,
		// findLayoutId("vsgm_tony_process);
		// process.setCancelable(false);
	}

	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			close();
		}
	}

	private void close() {
		isWebPayShowing = false;
		mWebView.destroy();
		ViewGroup parent = ((ViewGroup) getParent());
		if (parent != null)
			parent.removeView(this);
		if (mListener != null)
			mListener.onClose();
		try {
			Method m = Activity.class.getDeclaredMethod("onResume", new Class[0]);
			m.setAccessible(true);
			m.invoke(mActivity, new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private ViewCloseListener mListener;

	public void setViewCloseListener(ViewCloseListener listener) {
		this.mListener = listener;
	}

	public interface ViewCloseListener {
		public void onClose();
	}
}
