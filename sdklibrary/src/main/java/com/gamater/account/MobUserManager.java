package com.gamater.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.gamater.account.http.APIs;
import com.gamater.account.http.Keys;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.account.po.ThirdType;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.WebAPI;
import com.gamater.define.DeviceInfo;
import com.gamater.define.SPUtil;
import com.gamater.payment.GamaterIAB;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.AppUtil;
import com.gamater.util.Encryption;
import com.gamater.util.FileDataUtil;
import com.gamater.util.LogUtil;
import com.kochava.android.tracker.Feature;
import com.tony.floatmenu.SDKMenuManager;
import com.tony.sdkview.SdkViewOpenHelper;

public class MobUserManager {
	// private static final String GAME_KEY = "^&*(YUI";
	private static final String ACCOUNT_LIST = "account_list";
	private static final String ACCOUNT_ORDER_LIST = "account_order_list";

	public static final String CONFIG_KEY_RSA = "rsa";
	public static final String CONFIG_KEY_FB = "FBid";
	public static final String CONFIG_KEY_TWITTER_ID = "twitter-key";
	public static final String CONFIG_KEY_TWITTER_KEY = "twitter-secret";

	// public static final String CONFIG_KEY_WX_ID = "wechat_id";
	// public static final String CONFIG_KEY_WX_KEY = "wechat_key";

	private static MobUserManager m_instance;
	private DeviceInfo deviceInfo;
	private MobUser currentUser;

	// private boolean isShowLog = false;
	// private boolean isDevMode = true;

	private HttpEventListener userManagerListener;
	private SharedPreferences accountSP;
	private SharedPreferences cacheAccountSP;
	private SharedPreferences accountOrderSP;

	private String gwUrl = "";
	private String serviceUrl = "";
	private String giftUrl = "";
	private String fbUrl = "";
	private String serviceHost = "";
	private String fb_copywriter = "";

	private int statType = 0;

	private JSONObject noticeContent;
	private JSONObject configJson;
	private boolean isHadShowNotice = false;

	private int rankType = 0;

	private boolean isRankDataUpload = false;

	private boolean isLoginIng = false;

	public void setStatType(int statType) {
		this.statType = statType;
	}

	public int getStatType() {
		return statType;
	}

	public void setIsLoginIng(boolean is) {
		isLoginIng = is;
	}

	public boolean isLoginIng() {
		return isLoginIng;
	}

	public void setRankDataUpload(boolean isRankDataUpload) {
		this.isRankDataUpload = isRankDataUpload;
	}

	public boolean isRankDataUpload() {
		return isRankDataUpload;
	}

	public int getRankType() {
		return rankType;
	}

	public void setRankType(int rankType) {
		this.rankType = rankType;
	}

	public String getFb_copywriter() {
		return fb_copywriter;
	}

	public void setFb_copywriter(String fb_copywriter) {
		this.fb_copywriter = fb_copywriter;
	}


	public boolean isHadShowNotice() {
		return isHadShowNotice;
	}

	public void setHadShowNotice(boolean isHadShowNotice) {
		this.isHadShowNotice = isHadShowNotice;
	}

	public MobUser getCurrentUser() {
		return currentUser;
	}

	public String getCurrentUserId() {
		return accountOrderSP.getString("currentUser", null);
	}

	public String getServiceHost() {
		return serviceHost;
	}

	public JSONObject getConfigJson(Context context) {
		if (configJson == null) {
			String cacheStr = SPUtil.getConfigJsonString(context);
			if (cacheStr != null && cacheStr.length() > 0) {
				try {
					configJson = new JSONObject(Encryption.decryptDES(cacheStr));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return configJson;
	}

	public JSONObject getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(JSONObject notice) {
		this.noticeContent = notice;
	}

	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
		ConfigUtil.setConfig(ConfigUtil.MENU_SERVICES, serviceHost != null && serviceHost.length() > 0);
	}

	private Runnable updateAccountRunnable = new Runnable() {
		@Override
		public void run() {
			if (GamaterSDK.getInstance() == null || GamaterSDK.getInstance().getActivity() == null
					|| MobUserManager.getInstance().getCurrentUser() == null
					|| !"0".equals(MobUserManager.getInstance().getCurrentUser().getType())
					|| GamaterSDK.getInstance().getActivity().isFinishing()
					|| !SDKMenuManager.getInstance(null).isVisible())
				return;
			try {
				new SdkViewOpenHelper().openAction(GamaterSDK.getInstance().getActivity(),
						SdkViewOpenHelper.ACTION_UPDATE_ACCOUNT, null, null, 0);
			} catch (Exception e) {
			}
		}
	};

	public void setCurrentUser(MobUser currentUser) {
		Handler h = GamaterSDK.getInstance().getHandler();
		if (this.currentUser == null && currentUser != null) {
			this.currentUser = currentUser;
			if (currentUser != null && "0".equals(currentUser.getType())) {
				h.removeCallbacks(updateAccountRunnable);
				// 游客登录延迟300秒弹出升级账号
				h.postDelayed(updateAccountRunnable, 300000);
			}
		} else {
			if (currentUser == null) {
				h.removeCallbacks(updateAccountRunnable);
			}
			this.currentUser = currentUser;
		}
		if (currentUser != null) {
			setCacheUser(currentUser);
		}
	}

	public void setCacheUser(MobUser user) {
		cacheAccountSP.edit().putString("cacheUser", user.toCacheJson()).commit();
	}

	public MobUser getCacheUser() {
		return new MobUser().toCacheUser(cacheAccountSP.getString("cacheUser", ""));
	}

	public String getGwUrl() {
		return gwUrl;
	}

	public void setGwUrl(String gwUrl) {
		this.gwUrl = gwUrl;
		ConfigUtil.setConfig(ConfigUtil.MENU_HOMEPAGE, gwUrl != null && gwUrl.length() > 0);
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getGiftUrl() {
		return giftUrl;
	}

	public void setGiftUrl(String giftUrl) {
		this.giftUrl = giftUrl;
	}

	public String getFbUrl() {
		return fbUrl;
	}

	public void setFbUrl(String fbUrl) {
		this.fbUrl = fbUrl;
		ConfigUtil.setConfig(ConfigUtil.MENU_FACEBOOK, fbUrl != null && fbUrl.length() > 0);
	}

	public synchronized static MobUserManager initUserManager(Context ctx, boolean isShowLog) {
		if (m_instance == null)
			m_instance = new MobUserManager(ctx, isShowLog);
		return m_instance;
	}

	public synchronized static MobUserManager getInstance() {
		return m_instance;
	}

	private MobUserManager(Context ctx, boolean isShowLog) {
		this.deviceInfo = DeviceInfo.getInstance(ctx);
		Config.isShowLog = isShowLog;

		cacheAccountSP = ctx.getSharedPreferences("cache_account", Context.MODE_PRIVATE);
		accountSP = ctx.getSharedPreferences(ACCOUNT_LIST, Context.MODE_PRIVATE);
		accountOrderSP = ctx.getSharedPreferences(ACCOUNT_ORDER_LIST, Context.MODE_PRIVATE);

		if (getServiceHost().equalsIgnoreCase("")) {
			requestUrls();
		}

		
		if (getStatType() == 0) {

		} else if (getStatType() == 1) {

		} else if (getStatType() == 2) {

		} else {

		}

	}

	// private MobUserManager(Context ctx) {
	// deviceInfo = DeviceInfo.getInstance(ctx);
	// sharedPreferences = ctx.getSharedPreferences(ACCOUNT_LIST,
	// Context.MODE_PRIVATE);
	// }

	/**
	 * 保存一个登录账号
	 * 
	 * @param userid
	 * @param jsonStr
	 */
	public void saveAccount(String userid, String jsonStr) {
		accountSP.edit().putString(userid, jsonStr).commit();
		accountOrderSP.edit().putString("currentUser", userid).commit();
	}

	/**
	 * 移除一个登录账号
	 * 
	 * @param userid
	 */
	public void removeAccount(String userid) {
		Map<String, ?> list = accountSP.getAll();
		for (String key : list.keySet()) {
			if (key.equals(userid)) {
				accountSP.edit().remove(key).commit();
				accountList();
				break;
			}
		}
		if (userid.equals(getCurrentUserId())) {
			accountOrderSP.edit().remove("currentUser").commit();
		}
	}

	/**
	 * 获得登录过的账号
	 * 
	 * @return
	 */
	public List<MobUser> accountList() {
		Map<String, ?> list = accountSP.getAll();
		List<MobUser> userList = new ArrayList<MobUser>();
		String currentId = getCurrentUserId();
		if (currentId != null) {
			userList.add(userByUserId(currentId));
		}
		for (String key : list.keySet()) {
			MobUser user = new MobUser((String) list.get(key));
			if (!userList.contains(user))
				userList.add(user);
		}

		return userList;
	}

	/**
	 * 获得一个登录过的账号
	 * 
	 * @param userid
	 * @return
	 */
	public MobUser userByUserId(String userid) {
		Map<String, ?> list = accountSP.getAll();
		for (String key : list.keySet()) {
			MobUser user = new MobUser((String) list.get(key));
			if (user.getUserid().equalsIgnoreCase(userid)) {
				return user;
			}
		}
		return null;
	}

	public HttpEventListener getUserManagerListener() {
		return userManagerListener;
	}

	public void setUserManagerListener(HttpEventListener userManagerListener) {
		this.userManagerListener = userManagerListener;
	}

	public boolean isShowLog() {
		return Config.isShowLog;
	}

	public void setShowLog(boolean isShowLog) {
		Config.isShowLog = isShowLog;
	}

	/**
	 * 匿名登录
	 */
	public void anonymousLogin() {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_FREE_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(Build.MODEL);
			sb.append(deviceInfo.getIMEI());
			sb.append(deviceInfo.getAndroidId());

			String flag = MD5.crypt(sb.toString());

			sb = new StringBuffer();
			sb.append(flag);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			request.addPostValue(Keys.POST_KEY_FLAG, MD5.crypt(sb.toString()));

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}

	}

	/**
	 * 平台账号登录
	 * 
	 * @param email
	 *            用户名
	 * @param password
	 *            密码
	 */
	public void login(String email, String password) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);
			String pwd = MD5.crypt(password);
			request.addPostValue(Keys.POST_KEY_EMAIL, email);
			request.addPostValue(Keys.POST_KEY_PASSWORD, pwd);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(email);
			sb.append(pwd);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			request.addPostValue(Keys.POST_KEY_FLAG, MD5.crypt(sb.toString()));

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}

	}

	public void weixinLogin(String accessToken, String openid) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_THIRD_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			request.addPostValue(Keys.POST_KEY_THIRD_TYPE, ThirdType.wechat.toString());
			request.addPostValue(Keys.POST_KEY_THIRD_TOKEN, accessToken);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);
			request.addPostValue(Keys.POST_KEY_OPENID, openid);

			StringBuffer sb = new StringBuffer();
			sb.append(ThirdType.wechat.toString());
			sb.append(accessToken);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	/**
	 * RC登录
	 * 
	 * @param account
	 * @param password
	 */
	public void rcLogin(String account, String password) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_THIRD_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);
			String accessToken = UUID.randomUUID().toString();
			request.addPostValue(Keys.POST_KEY_THIRD_TYPE, ThirdType.rc.toString());
			request.addPostValue(Keys.POST_KEY_THIRD_TOKEN, accessToken);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);
			request.addPostValue(Keys.POST_KEY_EMAIL, account);
			request.addPostValue(Keys.POST_KEY_PASSWORD, password);

			StringBuffer sb = new StringBuffer();
			sb.append(ThirdType.rc.toString());
			sb.append(accessToken);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	/**
	 * 验证登录
	 * 
	 * @param userid
	 *            用户名
	 * @param token
	 *            密码
	 */
	public void elogin(String userid, String token) {

		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);
			request.addPostValue(Keys.POST_KEY_USERID, userid);
			request.addPostValue(Keys.POST_KEY_TOKEN, token);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(userid);
			sb.append(token);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			request.addPostValue(Keys.POST_KEY_FLAG, MD5.crypt(sb.toString()));

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}

	}

	/**
	 * 注册
	 * 
	 * @param email
	 * @param password
	 * @param phone
	 */
	public void register(String email, String password, String phone) {
		if (deviceInfo != null) {

			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_REGISTER);

			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			String pwd = MD5.crypt(password);

			request.addPostValue(Keys.POST_KEY_EMAIL, email);
			request.addPostValue(Keys.POST_KEY_PASSWORD, pwd);

			if (phone != null && phone.length() > 0) {
				request.addPostValue(Keys.POST_KEY_PHONE, phone);
			}

			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(email);
			sb.append(pwd);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());
			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	/**
	 * 第三方登录
	 * 
	 * @param type
	 * @param accessToken
	 */
	public void thirdLogin(ThirdType type, String accessToken) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_THIRD_LOGIN);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			request.addPostValue(Keys.POST_KEY_THIRD_TYPE, type.toString());
			request.addPostValue(Keys.POST_KEY_THIRD_TOKEN, accessToken);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(type.toString());
			sb.append(accessToken);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	/**
	 * 找回密码
	 * 
	 * @param email
	 */
	public void forgetPassword(String email) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_FORGET_PASSWORD);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			request.addPostValue(Keys.POST_KEY_EMAIL, email);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			request.addPostValue("ReleasePlatform", Config.gmTitle.toLowerCase());

			StringBuffer sb = new StringBuffer();
			sb.append(email);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	/**
	 * 修改密码
	 *
	 * @param passwd
	 * @param npasswd
	 */
	public void changePassword(String passwd, String npasswd) {
		if (currentUser == null) {
			return;
		}

		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_CHANGE_PASSWORD);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			request.addPostValue(Keys.POST_KEY_USERID, currentUser.getUserid());
			request.addPostValue(Keys.POST_KEY_TOKEN, currentUser.getToken());
			String npwd = MD5.crypt(npasswd);
			String pwd = MD5.crypt(passwd);
			request.addPostValue(Keys.POST_KEY_PASSWORD, pwd);
			request.addPostValue(Keys.POST_KEY_NEW_PASSWORD, npwd);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);

			StringBuffer sb = new StringBuffer();
			sb.append(currentUser.getUserid());
			sb.append(currentUser.getToken());
			sb.append(pwd);
			sb.append(npwd);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	private void initAppsFlyer() {
		try {
			AppsFlyerLib.setAppsFlyerKey("n9EW6sqgVTYsmSpfqyKqNL");
			AppsFlyerLib.setAppUserId(MD5.crypt(deviceInfo.getPackageName() + deviceInfo.getAndroidId() + deviceInfo.getIMEI()));
			AppsFlyerLib.sendTracking(GamaterSDK.getInstance().getActivity());
			LogUtil.print("Appsflyer","Appsflyer 初始化成功");
		} catch (Exception e) {
			LogUtil.print("Appsflyer", "Appsflyer 初始化异常，请检查jar是否正常引入");
		} catch (Error e) {
			LogUtil.print("Appsflyer", "Appsflyer 初始化异常，请检查jar是否正常引入");
		}
	}

	private void initKochava() {
		try {
			HashMap<String, Object> datamap = new HashMap<>();
			datamap.put(Feature.INPUTITEMS.KOCHAVA_APP_ID, AppUtil.GetMetaDataString(GamaterSDK.getInstance().getActivity(), "my_kochava_app_guid"));
			datamap.put(Feature.INPUTITEMS.REQUEST_ATTRIBUTION, true);
			Feature kTracker = new Feature(GamaterSDK.getInstance().getActivity(), datamap);
			kTracker.event("customer_user_id", MD5.crypt(deviceInfo.getPackageName() + deviceInfo.getAndroidId() + deviceInfo.getIMEI()));

			LogUtil.print("Kochava", "Kochava 初始化成功" + MD5.crypt(deviceInfo.getPackageName() + deviceInfo.getAndroidId() + deviceInfo.getIMEI()));
		} catch (Exception e) {
			LogUtil.print("Kochava", "Kochava 初始化异常，请检查jar是否正常引入");
		} catch (Error e) {
			LogUtil.print("Kochava", "Kochava 初始化异常，请检查jar是否正常引入");
		}
	}

	public void requestUrls() {
		SdkHttpRequest request = SdkHttpRequest.homepageURL();
		request.setHttpEventListener(new HttpEventListener() {
			@Override
			public void requestDidSuccess(HttpRequest httpRequest, String result) {
				try {
					Context ctx = GamaterSDK.getInstance().getActivity().getApplicationContext();
					JSONObject obj = new JSONObject(result);
					JSONObject urlObj = obj.getJSONObject("data");
					if (obj.getString("status").toString().equals("1")) {
						SPUtil.setJSONData(GamaterSDK.getInstance().getActivity(), urlObj.toString());
					}

					setGwUrl(urlObj.optString("gw"));
					setGiftUrl(urlObj.optString("packs"));
					setServiceUrl(urlObj.optString("customerService"));
					setFbUrl(urlObj.optString("facebook"));
					setNoticeContent(urlObj.optJSONObject("notice"));
					setServiceHost(urlObj.optString("vstarGameCs"));
					setFb_copywriter(urlObj.optString("fb_copywriter"));
					String configStr = urlObj.optString("config");

					if (configStr != null && configStr.length() > 0) {
						configJson = new JSONObject(Encryption.decryptDES(configStr));
						LogUtil.printHTTP(configJson.toString());
						Object o = configJson.opt("isTestMode");
//						LogUtil.printLog("Config.isTestMode = " + Config.isTestMode + Encryption.decryptDES(configStr));
						if (o != null) {
							try {
								if (o != null)
									Config.isTestMode = Integer.valueOf(o.toString());
//									LogUtil.printHTTP("Config.isTestMode = " + Config.isTestMode + o.toString());
							} catch (Exception e) {
							}
						}

						SPUtil.setConfigJsonString(GamaterSDK.getInstance().getActivity(), configStr);
						GamaterIAB.getInstance().checkIabSetup();
						ConfigUtil.initConfigWithConfigJson(configJson);
						String facebookid = configJson.optString(MobUserManager.CONFIG_KEY_FB);
						if (facebookid != null && facebookid.length() > 0) {
							FacebookSdk.setApplicationId(facebookid);
							FacebookSdk.sdkInitialize(GamaterIAB.getInstance().getContext());
							AppEventsLogger.deactivateApp(GamaterIAB.getInstance().getContext(), facebookid);
						}

						// 根据服务器下发的statType 选择使用哪一个统计
						setStatType(urlObj.optInt("statType"));
						if (getStatType() == 1) {
							initAppsFlyer();
						} else if (getStatType() == 2) {
							initKochava();
						} else if (getStatType() == 0) {
							initAppsFlyer();
							initKochava();
						} else {
						}

//						FileDataUtil.saveFileData(ctx, "is_test_mode", "" + Config.isTestMode);
						PreferenceManager.getDefaultSharedPreferences(GamaterSDK.getInstance().getActivity()).edit()
								.putString("sdk_config_str", configStr).commit();
						FileDataUtil.saveFileData(ctx, "is_test_mode", "" + Config.isTestMode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestDidStart(HttpRequest httpRequest) {
			}

			@Override
			public void requestDidFailed(HttpRequest httpRequest) {
				LogUtil.print("getUrl", "api/getUrl 访问失败");
				initAppsFlyer();
				initKochava();
			}
		});
		request.asyncStart();
	}

	/**
	 * 升级为平台账号
	 * 
	 * @param email
	 * @param userid
	 * @param token
	 * @param passwd
	 */
	public void upgradeAccount(String email, String userid, String token, String passwd) {
		if (deviceInfo != null) {
			SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.WEB_API_UPGRADE_ACCOUNT);
			long time = System.currentTimeMillis() / 1000;
			String timeStr = String.valueOf(time);

			request.addPostValue(Keys.POST_KEY_EMAIL, email);
			request.addPostValue(Keys.POST_KEY_TOKEN, token);
			request.addPostValue(Keys.POST_KEY_USERID, userid);
			request.addPostValue(Keys.POST_KEY_TIME, timeStr);
			String password = MD5.crypt(passwd);
			request.addPostValue(Keys.POST_KEY_PASSWORD, password);

			StringBuffer sb = new StringBuffer();
			sb.append(email);
			sb.append(password);
			sb.append(userid);
			sb.append(token);
			sb.append(WebAPI.LOGIN_KEY);
			sb.append(timeStr);

			String flag = MD5.crypt(sb.toString());

			request.addPostValue(Keys.POST_KEY_FLAG, flag);

			request.setHttpEventListener(userManagerListener);
			request.asyncStart();
		}
	}

	public void facebookLogin() {

	}

	public void twitterLogin() {

	}

	public void googlePlusLogin() {

	}

	public void destroy() {
		m_instance = null;
	}

}
