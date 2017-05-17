package com.vsgm.sdk.callback;

import com.gamater.account.po.MobUser;
import com.gamater.sdk.game.GamaterSDKListener;
import com.vsgm.sdk.VsgmUser;

public abstract class VsgmUserCallback extends GamaterSDKListener {
	@Override
	public void didLoginSuccess(MobUser user) {
		didLoginSuccess(new VsgmUser(user));
	}

	public abstract void didLoginSuccess(VsgmUser user);

	@Override
	public abstract void didLogout();
}
