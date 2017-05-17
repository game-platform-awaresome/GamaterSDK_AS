package com.gamater.payment.web;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.gamater.define.PaymentParam;
import com.gamater.payment.AcGameIAB;
import com.gamater.payment.web.view.PayView;
import com.gamater.payment.web.view.PayView.ViewCloseListener;
import com.gamater.util.ResourceUtil;

public class WebPayDialog extends Dialog {
	private Activity mActivity;

	private String mOrderId;
	private PaymentParam mParam;

	private PayView mPayView;

	public WebPayDialog(Activity activity, String orderId, PaymentParam param) {
		// super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		super(activity, ResourceUtil.getStyleId("vsgm_tony_dialog_full"));
		mActivity = activity;
		mOrderId = orderId;
		mParam = param;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window dialogWindow = getWindow();
		dialogWindow.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setCancelable(false);
		setupView();
	}

	private void setupView() {
		mPayView = new PayView(mActivity, mOrderId, mParam);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mActivity
				.getResources().getDisplayMetrics().heightPixels);
		addContentView(mPayView, lp);
		mPayView.setFocusable(true);
		mPayView.requestFocus();
		mPayView.setViewCloseListener(new ViewCloseListener() {
			@Override
			public void onClose() {
				dismiss();
				if (AcGameIAB.getInstance().getAcGameIABListener() != null)
					AcGameIAB.getInstance().getAcGameIABListener()
							.otherPaymentFinish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			mPayView.onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}