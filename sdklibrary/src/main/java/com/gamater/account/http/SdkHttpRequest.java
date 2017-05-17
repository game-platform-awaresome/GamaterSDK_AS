package com.gamater.account.http;

import java.util.Hashtable;
import java.util.UUID;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.account.po.ThirdType;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.define.DeviceInfo;

public class SdkHttpRequest extends MVHttpRequest {
	private static final long serialVersionUID = -6633987773270030316L;

	public SdkHttpRequest(String method, String url, String function) {
		super(method, url, function);
	}

	@Override
	public void asyncStart() {
		initHeader(DeviceInfo.getInstance(null));
		super.asyncStart();
	}

	public static HttpRequest rcLoginRequest(String account, String passwd) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		String accessToken = UUID.randomUUID().toString();
		params.put(Keys.POST_KEY_THIRD_TYPE, ThirdType.rc.toString());
		params.put(Keys.POST_KEY_THIRD_TOKEN, accessToken);
		params.put(Keys.POST_KEY_EMAIL, account);
		params.put(Keys.POST_KEY_PASSWORD, passwd);
		HttpRequest r = postRequest(Config.getLoginHost(), APIs.WEB_API_THIRD_LOGIN, params);
		r.setTag("rc");
		return r;
	}

	public static SdkHttpRequest forgetPasswd(String email) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(Keys.POST_KEY_EMAIL, email);
		params.put("ReleasePlatform", Config.gmTitle.toLowerCase());
		return postRequest(Config.getLoginHost(), APIs.WEB_API_FORGET_PASSWORD, params);
	}

	public static SdkHttpRequest registerRequest(String email, String passwd) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		String pwd = MD5.crypt(passwd);
		params.put(Keys.POST_KEY_EMAIL, email);
		params.put(Keys.POST_KEY_PASSWORD, pwd);
		return postRequest(Config.getLoginHost(), APIs.WEB_API_REGISTER, params);
	}

	public static SdkHttpRequest normalLoginRequest(String email, String passwd) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		String pwd = MD5.crypt(passwd);
		params.put(Keys.POST_KEY_EMAIL, email);
		params.put(Keys.POST_KEY_PASSWORD, pwd);
		return postRequest(Config.getLoginHost(), APIs.WEB_API_LOGIN, params);
	}

	public static SdkHttpRequest quickLoginRequest() {
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.WEB_API_FREE_LOGIN);
		return request;
	}

	public static HttpRequest eLoginRequest(String userid, String token) {
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.WEB_API_LOGIN);
		request.addPostValue(Keys.POST_KEY_USERID, userid);
		request.addPostValue(Keys.POST_KEY_TOKEN, token);
		return request;
	}

	public static HttpRequest updateAccount(String email, String passwd) {
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (user == null)
			return null;
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.WEB_API_UPGRADE_ACCOUNT);
		request.addPostValue(Keys.POST_KEY_EMAIL, email);
		String password = MD5.crypt(passwd);
		request.addPostValue(Keys.POST_KEY_PASSWORD, password);
		request.addPostValue(Keys.POST_KEY_TOKEN, user.getToken());
		request.addPostValue(Keys.POST_KEY_USERID, user.getUserid());
		return request;
	}

	public static HttpRequest changePasswd(String passwd, String npasswd) {
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (user == null)
			return null;
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.WEB_API_CHANGE_PASSWORD);
		request.addPostValue(Keys.POST_KEY_USERID, user.getUserid());
		request.addPostValue(Keys.POST_KEY_TOKEN, user.getToken());
		String npwd = MD5.crypt(npasswd);
		String pwd = MD5.crypt(passwd);
		request.addPostValue(Keys.POST_KEY_PASSWORD, pwd);
		request.addPostValue(Keys.POST_KEY_NEW_PASSWORD, npwd);
		return request;
	}

	public static SdkHttpRequest thirdLoginRequest(ThirdType type, String token) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put(Keys.POST_KEY_THIRD_TYPE, type.toString());
		params.put(Keys.POST_KEY_THIRD_TOKEN, token);
		SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_THIRD_LOGIN, params);
		request.setTag(type.toString());
		return request;
	}

	public static SdkHttpRequest homepageURL() {
		String host = Config.getGWHost();
		SdkHttpRequest request = SdkHttpRequest.postRequest(host, APIs.GET_WEB_URL);
		return request;
	}

	public static SdkHttpRequest dynamicToken() {
		String host = Config.getGWHost();
		SdkHttpRequest request = SdkHttpRequest.postRequest(host, APIs.GET_DYNAMIC_TOKEN);
		return request;
	}

	public static SdkHttpRequest statisticeBtn() {
		String host = Config.getLoginHost();
		SdkHttpRequest request = SdkHttpRequest.postRequest(host, APIs.STATISTICE_BUTTON);
		return request;
	}

	public static SdkHttpRequest postRequest(String host, String method, Hashtable<String, String> params) {
		SdkHttpRequest request = new SdkHttpRequest("post", host, method);
		if (params != null) {
			for (String key : params.keySet()) {
				request.addPostValue(key, params.get(key));
			}
		}
		return request;
	}

	public static SdkHttpRequest postRequest(String host, String method) {
		return postRequest(host, method, null);
	}

	private boolean isShowLoadFail = false;

	public void setShowLoadFail(boolean isShowLoadFail) {
		this.isShowLoadFail = isShowLoadFail;
	}

	public boolean isShowLoadFail() {
		return isShowLoadFail;
	}
}