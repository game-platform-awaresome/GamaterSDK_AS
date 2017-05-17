package com.gamater.common;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.util.Base64;

import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;
import com.gamater.define.DeviceInfo;
import com.gamater.define.PackageUtil;
import com.gamater.define.ParameterKey;
import com.gamater.define.SPUtil;

public class AppListCollecter {

	private AppListCollecter() {
	}

	public static void init(Activity activity) {
		init(activity, 30000, 604800000L);
	}

	public static void init(Activity activity, int delaySecond, long period) {
		try {
			sendInstallApp(activity, delaySecond, period);
		} catch (Exception e) {
		}
	}

	private static void sendInstallApp(final Activity activity, final int delay, final long period) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						long lastTime = SPUtil.getLastSendAppsTime(activity);
						final long currentTime = System.currentTimeMillis();
						if (lastTime != 0 && currentTime - lastTime < period) {
							return;
						}
						JSONArray appList = PackageUtil.getThirdAppInfo(activity);
						String host = Config.getLoginHost();
						MVHttpRequest request = new MVHttpRequest("post", host, WebAPI.APPS_LIST);
						DeviceInfo info = DeviceInfo.getInstance(activity);
						request.initHeader(info);
						String content = Base64.encodeToString(appList.toString().getBytes(), Base64.DEFAULT).replaceAll("\n", "");
						request.addPostValue(ParameterKey.CONTENT, content);
						String time = currentTime / 1000 + "";
						request.addPostValue(ParameterKey.TIME, time);
						request.addPostValue(ParameterKey.FLAG,MD5.crypt(appList.toString() + info.getIMEI() + WebAPI.LOGIN_KEY + time));
						request.setHttpEventListener(new HttpEventListener() {
							@Override
							public void requestDidSuccess(HttpRequest httpRequest, String result) {
								try {
									JSONObject o = new JSONObject(result);
									if (1 == o.optInt("status")) {
										SPUtil.setLastSendAppsTime(activity, System.currentTimeMillis());
									}
								} catch (Exception e) {
								}
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
				}, delay);
			}
		});
	}
}
