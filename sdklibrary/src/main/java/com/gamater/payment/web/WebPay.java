package com.gamater.payment.web;

import android.app.Activity;

import com.gamater.define.DeviceInfo;
import com.gamater.define.PaymentParam;
import com.gamater.payment.PaymentHttpRequest;
import com.gamater.payment.PaymentHttpRequest.OrderIdCallback;

public class WebPay {
	private static WebPay m_instance = null;
	private Activity activity;

	public synchronized static WebPay init(Activity a) {

		if (m_instance == null) {
			m_instance = new WebPay(a);
		}
		m_instance.activity = a;
		return m_instance;
	}

	public synchronized static WebPay getInstance() {
		return m_instance;
	}

	private WebPay(Activity a) {
		this.activity = a;
	}

	public void payment(final PaymentParam param) {
		if (activity == null) {
			return;
		}

		PaymentHttpRequest request = new PaymentHttpRequest(activity);

		DeviceInfo di = DeviceInfo.getInstance(activity);
		request.initHeader(di);

		request.initParams(param.getServerId(), param.getRoleId(), param.getAccount(), param.getExtra(), (param.getAmount() + ""));

		request.asyncStart(new OrderIdCallback() {
			@Override
			public void onCallback(String orderId) {
				if (orderId != null) {
					new WebPayDialog(activity, orderId, param).show();
				}
			}
		});
	}
}
