package com.gamater.common;

public class Config {
	// 0 test、 2 pre 、 其它正式
	public static int isTestMode = 1;

	public static int payType = 1;

	public static boolean isShowLog = true;

	public static String clientId = "";

	public static final boolean isOkgameLogo = false;

	public static final boolean isGmLogo = false;
	public static final String gmTitle = "Gamater";
	public static final String gmHost = "gamater.com";

	public static final String HTTP_LOGIN = "http://login." + gmHost;
	public static final String HTTP_LOGIN_TEST = "http://test.login." + gmHost;
	public static final String HTTP_LOGIN_PRE = "http://pre.login." + gmHost;

	public static final String HTTP_PAY = "http://pay." + gmHost;
	public static final String HTTP_PAY_TEST = "http://test.pay." + gmHost;
	public static final String HTTP_PAY_PRE = "http://pre.pay." + gmHost;

	private static final String HTTP_GW = "http://gw." + gmHost;
	private static final String HTTP_GW_TEST = "http://test.gw." + gmHost;
	private static final String HTTP_GW_PRE = "http://pre.gw." + gmHost;

	public static final String HTTP_WEB_PAY = "http://epay." + gmHost;
	public static final String HTTP_WEB_PAY_TEST = "http://test.epay." + gmHost;
	public static final String HTTP_WEB_PAY_PRE = "http://pre.epay." + gmHost;

	private static final String HTTP_ONLINE = "http://online." + gmHost;
	private static final String HTTP_ONLINE_TEST = "http://test.online." + gmHost;
	private static final String HTTP_ONLINE_PRE = "http://pre.online." + gmHost;

	public static String getLoginHost() {
		if (isTestMode == 0)
			return HTTP_LOGIN_TEST;
		if (isTestMode == 2)
			return HTTP_LOGIN_PRE;
		return HTTP_LOGIN;
	}

	public static String getGWHost() {
		if (isTestMode == 0)
			return HTTP_GW_TEST;
		if (isTestMode == 2)
			return HTTP_GW_PRE;

		return HTTP_GW;

	}

	public static String getPayHost() {
		if (isTestMode == 0)
			return HTTP_PAY_TEST;
		if (isTestMode == 2)
			return HTTP_PAY_PRE;
		return HTTP_PAY;
	}

	public static String getWebPayHost() {
		if (isTestMode == 0)
			return HTTP_WEB_PAY_TEST;
		if (isTestMode == 2)
			return HTTP_WEB_PAY_PRE;
		return HTTP_WEB_PAY;
	}


	public static String getOnLineHost() {
		if (isTestMode == 0)
			return HTTP_ONLINE_TEST;
		if (isTestMode == 2)
			return HTTP_ONLINE_PRE;
		return HTTP_ONLINE;
	}

}
