package com.gamater.sdk.common;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.facebook.FacebookSdk;
import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.define.SPUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.LogUtil;

public class ConfigUtil {
	public static final String LOGIN_FACEBOOK = "facebook_login";
	public static final String LOGIN_GOOGLE = "google_login";
	public static final String LOGIN_TWITTER = "twitter_login";
	public static final String LOGIN_INSTAGRAM = "instagram_login";
	public static final String LOGIN_RC = "rc_login";
	public static final String LOGIN_WX = "wx_login";

	public static final String MENU_ACCOUNT = "menu_account";
	public static final String MENU_HOMEPAGE = "menu_homepage";
	public static final String MENU_SERVICES = "menu_service";
	public static final String MENU_FACEBOOK = "menu_facebook";

	public static final String LOGIN_CLOSE = "login_close";

	public static void setConfig(String configName, boolean enable) {
		try {
			SPUtil.setConfigSettings(GamaterSDK.getInstance().getActivity(), configName, enable ? 1 : -1);
		} catch (Exception e) {
		}
	}

	public static boolean getConfigEnable(Context ctx, String configName) {
		int enable = SPUtil.getConfigSettings(ctx, configName);
		if (enable != 0) {
			return enable == 1 ? true : false;
		}
		Resources res = ctx.getResources();
		try {
			boolean b = res.getBoolean(res.getIdentifier("enable_" + configName, "bool", ctx.getPackageName()));
			return b;
		} catch (Exception e) {
			LogUtil.printHTTP(configName);
			e.printStackTrace();
			return false;
		}
	}

	public static boolean getConfigEnable(String configName) {
		if (GamaterSDK.getInstance() == null)
			return false;
		Context c = GamaterSDK.getInstance().getActivity();
		if (c == null)
			return false;
		return getConfigEnable(c, configName);
	}

	public static int getViewVisible(String configName) {
		if (configName.endsWith(MENU_ACCOUNT)) {
			MobUser user = MobUserManager.getInstance().getCurrentUser();
			if (user == null) {
				return View.GONE;
			}
			return getConfigEnable(configName) ? View.VISIBLE : View.GONE;
		} else if (configName.endsWith(MENU_FACEBOOK)) {
			String fbUrl = MobUserManager.getInstance().getFbUrl();
			return getConfigEnable(configName) && fbUrl != null
					&& fbUrl.length() > 0 ? View.VISIBLE : View.GONE;
		} else if (configName.endsWith(MENU_HOMEPAGE)) {
			String gwUrl = MobUserManager.getInstance().getGwUrl();
			LogUtil.printHTTP(gwUrl);
			LogUtil.printHTTP("MENU_HOMEPAGE == " + getConfigEnable(configName) + " , " + (gwUrl != null) + " , "
					+ (gwUrl.length() > 0) + "," + (getConfigEnable(configName) && gwUrl != null && gwUrl.length() > 0));

			return getConfigEnable(configName) && gwUrl != null && gwUrl.length() > 0 ? View.VISIBLE : View.GONE;
		}
		return getConfigEnable(configName) ? View.VISIBLE : View.GONE;
	}

	private static int thirdLoginCount = -1;

	public static int getThirdLoginCount() {
		if (thirdLoginCount > 0)
			return thirdLoginCount;
		thirdLoginCount = 0;
		if (ConfigUtil.getConfigEnable(LOGIN_FACEBOOK))
			thirdLoginCount++;
		if (ConfigUtil.getConfigEnable(LOGIN_GOOGLE))
			thirdLoginCount++;
		if (ConfigUtil.getConfigEnable(LOGIN_INSTAGRAM))
			thirdLoginCount++;
		if (ConfigUtil.getConfigEnable(LOGIN_TWITTER))
			thirdLoginCount++;
		if (ConfigUtil.getConfigEnable(LOGIN_RC))
			thirdLoginCount++;
		return thirdLoginCount;
	}

	public static int[] getMenuOrigin() {
		Context c = GamaterSDK.getInstance().getActivity();
		Resources res = c.getResources();
		try {
			return res.getIntArray(res.getIdentifier("sdk_menu_origin", "array", c.getPackageName()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int[] getScrollAdOrigin() {
		Context c = GamaterSDK.getInstance().getActivity();
		Resources res = c.getResources();
		try {
			return res.getIntArray(res.getIdentifier("sdk_scroll_ad_origin", "array", c.getPackageName()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void initConfigWithConfigJson(JSONObject configJson) {
		if (configJson != null) {
			String facebookid = configJson.optString(MobUserManager.CONFIG_KEY_FB);
			setFacebookId(facebookid);

			// String twitterid = configJson.optString(MobUserManager.CONFIG_KEY_TWITTER_ID);
			// String twitterkey = configJson.optString(MobUserManager.CONFIG_KEY_TWITTER_KEY);
			// setTwitterIdNKey(twitterid, twitterkey);

			// String wechatid = configJson.optString(MobUserManager.CONFIG_KEY_WX_ID);
			// String wechatkey = configJson.optString(MobUserManager.CONFIG_KEY_WX_KEY);
			// setWeChatIdNKey(wechatid, wechatkey);
		} else {
			setConfig(LOGIN_FACEBOOK, false);
		}
	}

	public static void setFacebookId(String id) {
		if (id != null && id.length() > 0) {
			setConfig(LOGIN_FACEBOOK, true);
			FacebookSdk.setApplicationId(id);
		} else {
			setConfig(LOGIN_FACEBOOK, false);
		}
	}

	// public static void setTwitterIdNKey(String key, String secret) {
	// if (key != null && key.length() > 0 && secret != null && secret.length() > 0) {
	// setConfig(LOGIN_TWITTER, true);
	// CONST.TWITTER_KEY = key;
	// CONST.TWITTER_SECRET = secret;
	// } else {
	// setConfig(LOGIN_TWITTER, false);
	// }
	// }
	//
	// public static void setWeChatIdNKey(String id, String key) {
	// if (id != null && id.length() > 0 && key != null && key.length() > 0) {
	// setConfig(LOGIN_WX, true);
	// WeChatHelper.WX_APP_ID = id;
	// WeChatHelper.WX_APP_SECRET = key;
	// } else {
	// setConfig(LOGIN_WX, false);
	// }
	// }
}
