package com.gamater.common.http;

public interface WebAPI {
	public static final String LOGIN_KEY = "^&*(YUI"; // 登录key
	public static final String PAY_KEY = "game8&&(#joy"; // Google支付加密key
	public static final String WEB_PAY_KEY = ")&*(jyuij6789IJHN|}\"";// Web支付加密key
	public static final String PUSH_KEY = "&X(1PDKCw.eJX6*y"; // 暂时没有用到
	// public static final String LOGIN_KEY = "fab870efc0e07440be513e46a214f7b03c951c11";
	// public static final String PAY_KEY = "fab870efc0e07440be513e46a214f7b03c951c11";
	// public static final String WEB_PAY_KEY = "fab870efc0e07440be513e46a214f7b03c951c11";
	public static final String GET_ORDER_ID = "/order/getId";
	public static final String PAY_VALIDATE = "/googlePlay/pay";
	public static final String PAY_TYPE = "/user/getPayPage";

	public static final String CRASH_UPLOAD = "/user/crash";
	public static final String ROLE_REPORT = "/tracking/getServer";
	public static final String APPS_LIST = "/tracking/getInstallList";
}
