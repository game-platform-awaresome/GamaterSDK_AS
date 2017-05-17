package com.gamater.common;

import org.json.JSONObject;

import android.content.Context;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.define.DeviceInfo;
import com.gamater.util.LogUtil;

public class OnLineHelper {
	private static OnLineHelper instance;
	private Context context;
	private Thread onLineThread;
	private boolean flag;

	private OnLineHelper(Context ctx) {
		context = ctx.getApplicationContext();
	}

	public static OnLineHelper getInstance(Context context) {
		if (instance == null)
			instance = new OnLineHelper(context);
		return instance;
	}

	public void start() {
		if (onLineThread == null) {
			onLineThread = new Thread() {
				@Override
				public void run() {
					while (flag) {
						OnLineHelper.this.run();
					}
				}
			};
			flag = true;
			onLineThread.start();
		}
	}

	public void stop() {
		flag = false;
		onLineThread = null;
	}

	private void run() {
		String onLineResult = getOnLineRequest().startOutOfSelfThread();
		if (onLineResult != null) {
			try {
				LogUtil.printHTTP(onLineResult);
				JSONObject data = new JSONObject(onLineResult);
				int status = data.optInt("status");
				if (status == 1) {
					int interval = data.optInt("interval", 30);
					Thread.sleep(interval * 1000);
				} else {
					Thread.sleep(30000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private HttpRequest getOnLineRequest() {
		DeviceInfo info = DeviceInfo.getInstance(context);
		String time = System.currentTimeMillis() / 1000 + "";
		MVHttpRequest request = new MVHttpRequest("post", Config.getOnLineHost(), "");
		request.setContext(context);
		request.addPostValue("type", 6);
		request.addPostValue("client_id", Config.clientId);
		request.addPostValue("time", time);
		request.addPostValue("customer_id", info.getCustomerId());
		MobUserManager userManager = MobUserManager.getInstance();
		if (userManager != null && userManager.getCurrentUser() != null) {
			MobUser user = userManager.getCurrentUser();
			request.addPostValue("user_id", userManager.getCurrentUser().getUserid());
			request.addPostValue("server_id", user.getServerId());
			request.addPostValue("server_name", user.getServerName());
			request.addPostValue("role_id", user.getRoleId());
			request.addPostValue("role_name", user.getRoleName());
			request.addPostValue("career", user.getProfession());
			request.addPostValue("level", user.getLevel());
			request.addPostValue("power", user.getPower());
			request.addPostValue("gold", user.getGold());
		} else {
			request.addPostValue("user_id", "0");
			request.addPostValue("server_id", "0");
			request.addPostValue("server_name", "");
			request.addPostValue("role_id", "0");
			request.addPostValue("role_name", "");
			request.addPostValue("career", "");
			request.addPostValue("level", "0");
			request.addPostValue("power", "0");
			request.addPostValue("gold", "0");
		}
		request.initHeader(info);
		return request;
	}
}
