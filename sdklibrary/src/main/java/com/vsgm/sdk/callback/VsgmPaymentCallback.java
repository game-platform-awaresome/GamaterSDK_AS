package com.vsgm.sdk.callback;

import com.gamater.define.GPOrder;
import com.gamater.payment.GamaterIABListener;

public abstract class VsgmPaymentCallback implements GamaterIABListener {
	@Override
	public final void onPayType(int type) {
	}

	@Override
	public final void paymentSuccess(GPOrder order) {
		paymentSuccess();
	}

	public abstract void paymentSuccess();

	@Override
	public abstract void otherPaymentFinish();

	@Override
	public abstract void paymentFailed(String result);

	@Override
	public abstract void paymentStart(String productId);

	@Override
	public abstract void setupHelperFailed();
}
