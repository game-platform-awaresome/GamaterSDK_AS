package com.gamater.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;
import com.gamater.define.DeviceInfo;
import com.gamater.define.ParameterKey;
import com.gamater.define.SPUtil;

public class InstallReceiver extends BroadcastReceiver {
	private static final String INSTALL_API = "/tracking/install";

	@Override
	public void onReceive(Context ctx, Intent data) {
		Bundle extras = data.getExtras();
		String campaign = extras.getString("referrer");
		SPUtil.saveCampaign(ctx, campaign);
		sendInstall(ctx, null, 0);
	}

	public static void sendInstall(final Context ctx, Handler h, int delay) {
		if (delay == 0) {
			post(ctx);
		} else {
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					post(ctx);
				}
			}, delay);
		}
	}

	private static void post(final Context ctx) {
		if (SPUtil.isSendInstall(ctx)) {
			return;
		}
		String host = Config.getLoginHost();
		DeviceInfo info = DeviceInfo.getInstance(ctx);
		MVHttpRequest request = new MVHttpRequest("post", host, INSTALL_API);
		request.initHeader(info);
		String time = System.currentTimeMillis() / 1000 + "";
		request.addPostValue(ParameterKey.TIME, time);
		request.addPostValue(
				ParameterKey.FLAG,
				MD5.crypt(info.getCampaign() + Build.MODEL + info.getIMEI()
						+ info.getAndroidId() + info.getPackageName()
						+ WebAPI.LOGIN_KEY + time + ""));
		request.setHttpEventListener(new HttpEventListener() {
			@Override
			public void requestDidSuccess(HttpRequest httpRequest, String result) {
				SPUtil.setSendInstall(ctx);
			}

			@Override
			public void requestDidStart(HttpRequest httpRequest) {

			}

			@Override
			public void requestDidFailed(HttpRequest httpRequest) {

			}
		});
		request.asyncStart();
	}
}
