package com.gamater.account.http;

public class APIs {
	/**
	 * 账号登录
	 */
	public static final String WEB_API_LOGIN = "/login/elogin";
	/**
	 * 免注册登录
	 */
	public static final String WEB_API_FREE_LOGIN = "/login/freeRegister";
	/**
	 * 注册
	 */
	public static final String WEB_API_REGISTER = "/login/register";
	/**
	 * 第三方登录
	 */
	public static final String WEB_API_THIRD_LOGIN = "/login/third_login";
	/**
	 * 升级账号
	 */
	public static final String WEB_API_UPGRADE_ACCOUNT = "/login/upgradeAccount";
	/**
	 * 修改密码
	 */
	public static final String WEB_API_CHANGE_PASSWORD = "/login/changePassword";
	/**
	 * 找回密码
	 */
	public static final String WEB_API_FORGET_PASSWORD = "/user/forgetPassword";
	/**
	 * 获取官网地址
	 */
	public static final String GET_WEB_URL = "/api/getUrl";
	/**
	 * 获取动态加密值
	 */
	public static final String GET_DYNAMIC_TOKEN = "/api/getDynamicToken";

	/**
	 * 按钮点击统计
	 */
	public static final String STATISTICE_BUTTON = "/statistice/floatButton";

	public static final String RESPONSE_TIME = "/statistice/requestTime";

	public static final String UPLOAD_IMAGE = "/upload/image";

	public static final String COMBAT_POWER = "/user/role";

	public static final String FOLLOW_CREATE = "/user/role_follow_create";

	public static final String FOLLOW_CANCEL = "/user/role_follow_cancel";

	public static final String MESSAGE = "/message/activity";

	public static final String RANK_FIGHTING = "/rank/fighting";

	public static final String RANK_MONEY = "/rank/money";

	public static final String ALL_FOLLOW = "/follow/all";

	public static final String FOLLOW_RANK = "/follow/rank";

	public static final String FOLLOW_TOPIC = "/follow/topic";

	public static final String PUBLISH_TOPIC = "/topic/publish";

	public static final String REPLY_TOPIC = "/topic/reply";

	public static final String TOPIC_IMAGE_UPLOAD = "/user/upload";

	public static final String USER_PHOTO_UPLOAD = "/user/avatar";

	public static final String SYSTEM_MSG = "/message/system";

	public static final String MESSAGE_DELETE = "/message/delete";

	public static final String MESSAGE_SET_READ = "/message/activity_set_read";

	public static final String RANK_NOTE = "/message/rank";

	public static final String FB_ACTIVITY = "/activity/index";

	public static final String FB_REPORT = "/activity/reporting";
}
