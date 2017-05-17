package com.gamater.sdk.facebook;

import com.tony.viewinterface.WebInterface;

public interface FbShareCallback extends WebInterface {
	public void onShareFinish(Exception e, String postId);

	public void onShareCancel();
}
