package com.vsgm.sdk.callback;

import org.json.JSONArray;

public interface VsgmFbFriendsCallback extends
		com.gamater.sdk.facebook.FbFriendsCallback {
	@Override
	public void onGetFriends(JSONArray friends);
}
