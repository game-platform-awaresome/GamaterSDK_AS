package com.gamater.account;

import org.json.JSONException;

import com.android.vending.util.Purchase;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.common.http.MD5;
import com.gamater.common.http.WebAPI;

public class EventTracker {
	public static void btnClickEvent(int event) {
		btnClickEvent(event + "");
	}

	private static void btnClickEvent(String event) {
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		String userId = user == null ? "" : user.getUserid();
		String time = System.currentTimeMillis() / 1000 + "";
		SdkHttpRequest request = SdkHttpRequest.statisticeBtn();
		request.addPostValue("btn", event);
		request.addPostValue("userid", userId);
		request.addPostValue("time", time);
		request.addPostValue("flag", MD5.crypt(event + userId + WebAPI.LOGIN_KEY + time));
		request.asyncStart();
	}

	public static void payEvent(int event, Purchase purchase) {

		try {
			payEvent(event, purchase.toJson().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void payEvent(int event, String purchase) {
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		String userId = user == null ? "" : user.getUserid();
		String time = System.currentTimeMillis() / 1000 + "";
		SdkHttpRequest request = SdkHttpRequest.statisticeBtn();
		request.addPostValue("btn", event);
		request.addPostValue("purchase", purchase);
		request.addPostValue("userid", userId);
		request.addPostValue("time", time);
		request.addPostValue("flag", MD5.crypt(event + userId + WebAPI.LOGIN_KEY + time));
		request.asyncStart();
	}
}
