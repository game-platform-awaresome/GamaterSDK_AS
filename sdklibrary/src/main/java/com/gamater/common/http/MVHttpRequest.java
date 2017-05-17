package com.gamater.common.http;

import android.content.Context;
import android.os.Build;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.define.DeviceInfo;
import com.gamater.define.GPOrder;
import com.gamater.define.PaymentParam;
import com.gamater.util.AppUtil;

public class MVHttpRequest extends HttpRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5526348261374086910L;

	private PaymentParam param;
	private GPOrder order;

	private int requestCode;

	public GPOrder getOrder() {
		return order;
	}

	public void setOrder(GPOrder order) {
		this.order = order;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public PaymentParam getParam() {
		return param;
	}

	public void setParam(PaymentParam param) {
		this.param = param;
	}

	public MVHttpRequest(String method, String url, String function) {
		super(method, url, function);
	}

	public void initHeader(DeviceInfo info) {
		addHeader(HeadersName.MODEL, Build.MODEL);
		addHeader(HeadersName.IMEI, info.getIMEI());
		addHeader(HeadersName.GID, "");
		addHeader(HeadersName.MAC, info.getMacAddress());
		addHeader(HeadersName.ANDROID_ID, info.getAndroidId());
		addHeader(HeadersName.APP_VERSION_CODE, info.getAppVersionCode());
		addHeader(HeadersName.APP_VERSION_NAME, info.getappVersionName());
		addHeader(HeadersName.SYS_VERSION_CODE, Build.VERSION.SDK_INT + "");
		addHeader(HeadersName.SYS_VERSION_NAME, Build.VERSION.RELEASE);
		addHeader(HeadersName.SCREEN_SIZE, info.getDisplaySize());
		addHeader(HeadersName.LANGUAGE, info.getSystemLanguage());
		addHeader(HeadersName.NET_TYPE, info.getNetType());
		addHeader(HeadersName.CAMPAIGN, info.getCampaign());
		addHeader(HeadersName.PACKAGE, info.getPackageName());
		addHeader(HeadersName.PHONE_NUMBER, info.getPhoneNumber());
		addHeader(HeadersName.COUNTRY_CODE, info.getCountryCode());
		addHeader(HeadersName.CUSTOMER_ID, info.getCustomerId());
		Context c = DeviceInfo.getInstance(null).getContext();
		addHeader(HeadersName.PHONE_MNC, AppUtil.getMNC(c));
		addHeader(HeadersName.PLATFORM, "android");
		addHeader(HeadersName.RELEASE_PLATFORM, AppUtil.getReleasePlatform());
		addHeader(HeadersName.CLIENT_ID, com.gamater.common.Config.clientId);
		MobUserManager userManager = MobUserManager.getInstance();
		if (userManager != null && userManager.getCurrentUser() != null) {
			MobUser user = userManager.getCurrentUser();
			addHeader(HeadersName.USER_ID, user.getUserid());
			addHeader(HeadersName.ROLE_ID, user.getRoleId());
			addHeader(HeadersName.ROLE_NAME, user.getRoleName());
			addHeader(HeadersName.SERVER_ID, user.getServerId());
			addHeader(HeadersName.SERVER_NAME, user.getServerName());
			addHeader(HeadersName.TOKEN, user.getToken());
			addHeader(HeadersName.LOGIN_TYPE, user.getLoginType() + "");
		} else {
			addHeader(HeadersName.USER_ID, "0");
		}
	}
}
