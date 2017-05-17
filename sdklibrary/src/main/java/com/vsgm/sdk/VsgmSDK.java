package com.vsgm.sdk;

import java.util.List;

import com.gamater.common.Config;
import com.gamater.dialog.SdkGameDialog;
import com.gamater.sdk.game.GamaterSDK;
import com.vsgm.sdk.callback.VsgmFbFriendsCallback;
import com.vsgm.sdk.callback.VsgmFbShareCallback;
import com.vsgm.sdk.callback.VsgmPaymentCallback;
import com.vsgm.sdk.callback.VsgmUserCallback;

import android.app.Activity;
import android.content.Intent;

public class VsgmSDK {
	private static VsgmSDK m_instance = null;
	private static GamaterSDK sdkInstance = null;

	public static synchronized VsgmSDK initSDK(Activity activity,
			String clientId, boolean isShowLog) {

		if (m_instance == null) {
			m_instance = new VsgmSDK(activity, clientId, isShowLog);
		} else {
			m_instance.setActivity(activity);
			Config.isShowLog = isShowLog;
			Config.clientId = clientId;
		}
		return m_instance;

	}

	public static synchronized VsgmSDK getInstance() {
		return m_instance;
	}

	private VsgmSDK(Activity activity, String clientId, boolean isShowLog) {
		sdkInstance = GamaterSDK
				.initSDK(activity, clientId, isShowLog);
	}

	public void initPayment(List<String> skus, VsgmPaymentCallback listener) {
		sdkInstance.initPayment(skus, listener);
	}

	public void popLoginView() {
		if (sdkInstance.getActivity().isFinishing())
			return;
		sdkInstance.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					new SdkGameDialog(sdkInstance.getActivity()).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 
	 * @param profession
	 *            职业
	 * @param serverId
	 *            服务器Id
	 * @param serverName
	 *            服务器名
	 * @param roleId
	 *            角色Id
	 * @param rolesName
	 *            角色名
	 * @param roleLevel
	 *            角色等级
	 */
	public void roleReport(String profession, String serverId,
			String serverName, String roleId, String rolesName, int roleLevel) {
		sdkInstance.roleReport(profession, serverId, serverName, roleId,
				rolesName, roleLevel);
	}

	public void getFacebookFriends(VsgmFbFriendsCallback callback) {
		sdkInstance.getFacebookFriends(callback);
	}

	public void shareToFb(String link, String caption, String description,
			String pictureURL, VsgmFbShareCallback callback) {
		sdkInstance.shareToFb(link, caption, description, pictureURL, callback);
	}

	public void destroy() {
		sdkInstance.destroy();
		sdkInstance = null;
		m_instance = null;
	}

	public Activity getActivity() {
		return sdkInstance.getActivity();
	}

	public void setActivity(Activity activity) {
		sdkInstance.setActivity(activity);
	}

	public void resumeGmae(Activity a) {
		sdkInstance.resumeGmae(a);
	}

	public void logout() {
		sdkInstance.logout();
	}

	public void showNoticeDialog() {
		sdkInstance.showNoticeDialog();
	}

	public void paymentDefault(PaymentParam param) {
		sdkInstance.paymentDefault(param);
	}

	public void paymentDefault(Activity a, PaymentParam param) {
		sdkInstance.paymentDefault(a, param);
	}

	public void paymentOther(Activity activity, PaymentParam param) {
		sdkInstance.paymentOther(activity, param);
	}

	public void paymentOther(PaymentParam param) {
		sdkInstance.paymentOther(param);
	}

	public void showPopupMenu() {
		sdkInstance.showPopupMenu();
	}

	public VsgmUserCallback getVsgmUserCallback() {
		return (VsgmUserCallback) sdkInstance.getAcGameSDKListener();
	}

	public void setVsgmUserCallback(VsgmUserCallback callback) {
		sdkInstance.setGamaterSDKListener(callback);
	}

	public boolean isShowLog() {
		return Config.isShowLog;
	}

	public void setShowLog(boolean isShowLog) {
		Config.isShowLog = isShowLog;
	}

	public boolean isShowMenu() {
		return sdkInstance.isShowMenu();
	}

	public void setShowMenu(boolean isShowMenu) {
		sdkInstance.setShowMenu(isShowMenu);
	}

	public void onResume() {
		sdkInstance.onResume();
	}

	public void onPause() {
		sdkInstance.onPause();
	}

	public boolean handlerResult(int requestCode, int resultCode, Intent data) {
		return sdkInstance.handlerResult(requestCode, resultCode, data);
	}

	public void facebookFriendsInvite() {
		sdkInstance.facebookFriendsInvite();
	}

	public void showFacebook() {
		sdkInstance.showFacebook();
	}
}
