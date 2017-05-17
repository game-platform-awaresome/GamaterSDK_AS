package com.gamater.sdk.facebook;

import com.tony.viewinterface.WebInterface;

public interface FbInviteCallback extends WebInterface {
	public void onInviteFinish(String friendIds);

	public void onInviteFinish(String[] friendNames);

	public void onCancel();
}
