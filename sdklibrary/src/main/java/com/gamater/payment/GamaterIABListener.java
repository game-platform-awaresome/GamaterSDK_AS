package com.gamater.payment;

import com.gamater.define.GPOrder;

public interface GamaterIABListener {
	void setupHelperFailed();

	void paymentStart(String sku);

	void paymentFailed(String result);

	void paymentSuccess(GPOrder order);

	void otherPaymentFinish();

	void onPayType(int type);
}