package com.gamater.payment;

import com.gamater.define.GPOrder;

public interface GamaterIABListener {
	public void setupHelperFailed();

	public void paymentStart(String sku);

	public void paymentFailed(String result);

	public void paymentSuccess(GPOrder order);

	public void otherPaymentFinish();

	public void onPayType(int type);
}