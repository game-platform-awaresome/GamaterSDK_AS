package com.vsgm.sdk.callback;

public interface VsgmFbShareCallback extends
		com.gamater.sdk.facebook.FbShareCallback {
	@Override
	public void onShareCancel();

	@Override
	public void onShareFinish(Exception e, String postId);
}
