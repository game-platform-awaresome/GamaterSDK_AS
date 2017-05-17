package com.vsgm.sdk;

import com.gamater.account.po.MobUser;

public class VsgmUser extends MobUser {
	private static final long serialVersionUID = 1L;

	public VsgmUser(String json) {
		super(json);
	}

	public VsgmUser(MobUser user) {
		setEmail(user.getEmail());
		setFacebook(user.isFacebook());
		setFbNickName(user.getFbNickName());
		setFbUserId(user.getFbUserId());
		setPasswd(user.getPasswd());
		setStatus(user.getStatus());
		setToken(user.getToken());
		setType(user.getType());
		setUserid(user.getUserid());
	}

	@Override
	public String getEmail() {
		return super.getEmail();
	}

	@Override
	public String getFbNickName() {
		return super.getFbNickName();
	}

	@Override
	public boolean isFacebook() {
		return super.isFacebook();
	}

	@Override
	public String getFbUserId() {
		return super.getFbUserId();
	}

	@Override
	public String getPasswd() {
		return super.getPasswd();
	}

	@Override
	public int getStatus() {
		return super.getStatus();
	}

	@Override
	public String getToken() {
		return super.getToken();
	}

	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public String getUserid() {
		return super.getUserid();
	}

}
