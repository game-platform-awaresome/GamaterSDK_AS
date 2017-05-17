package com.gamater.sdk.facebook;

import org.json.JSONArray;

import com.tony.viewinterface.WebInterface;

public interface FbFriendsCallback extends WebInterface {
	public void onGetFriends(JSONArray friends);
}
