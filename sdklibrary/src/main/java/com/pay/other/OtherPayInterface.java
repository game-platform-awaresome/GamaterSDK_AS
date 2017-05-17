package com.pay.other;

import com.gamater.define.PaymentParam;

import android.app.Activity;

public interface OtherPayInterface {
	void init(Activity activity);

	String payName();

	String payTag();

	void pay(Activity activity, String orderId, PaymentParam param);

	void onDestroy();
}
