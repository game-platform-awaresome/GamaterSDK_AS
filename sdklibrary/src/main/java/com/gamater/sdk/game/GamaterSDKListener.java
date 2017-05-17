package com.gamater.sdk.game;

import com.gamater.account.po.MobUser;
import com.gamater.util.LogUtil;

public abstract class GamaterSDKListener {
	public abstract void didLoginSuccess(MobUser user);

	public abstract void didLogout();

	public void didCancel() {
		LogUtil.printHTTP("user cancel login...");
	}
}
