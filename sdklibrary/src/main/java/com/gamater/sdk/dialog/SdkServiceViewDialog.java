package com.gamater.sdk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.common.http.MD5;
import com.gamater.define.DeviceInfo;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.sdk.common.ServicesHelper;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.tony.floatmenu.SDKMenuManager;
import com.tony.view.ImagePickerView.ImagePickerViewListener;
import com.tony.view.ServicesWebView;
import com.tony.view.ServicesWebView.ServicesViewListener;

public class SdkServiceViewDialog extends Dialog {
	private ServicesWebView servicesWebView;

	public SdkServiceViewDialog(Activity activity) {
		super(activity, ResourceUtil.getStyleId("vsgm_tony_dialog_full"));
		setOwnerActivity(activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		servicesWebView = ServicesHelper.getInstance().getServicesWebView(GamaterSDK.getInstance().getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		servicesWebView.setLayoutParams(params);
		setContentView(servicesWebView);
		SDKMenuManager.getInstance(SdkDialogViewManager.getOwnerActivity()).setServiceNoteNum(0);
		servicesWebView.setImagePickerListener(new ImagePickerViewListener() {
			@Override
			public void onPick(String... path) {
				ServicesHelper.getInstance().scaleImageNupload(path[0],
						servicesWebView.getMaxUploadSize());
			}

			@Override
			public void onCancel() {
			}
		});

		servicesWebView.setServicesViewListener(new ServicesViewListener() {
			@Override
			public void onClose() {
				dismiss();
			}

			@Override
			public void onRefresh() {
				SdkDialogViewManager.getOwnerActivity().runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								// if (Config.isTestMode) {// loadUrl("xx");
								loadUrl();
								// } else {
								// updateToken();
								// }
							}
						});
			}
		});

		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (!servicesWebView.checkUser(user) || !servicesWebView.hasLoad()) {
			// if (Config.isTestMode) {
			// loadUrl("xx");
			loadUrl();
			// } else {
			// updateToken();
			// }
		} else if (servicesWebView.hasLoad()) {
			servicesWebView.restart();
		}
	}

	private void loadUrl() {
		String packageName = DeviceInfo.getInstance(null).getPackageName();
		int systemOs = 1;
		String time = System.currentTimeMillis() / 1000 + "";
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (user == null) {
			user = MobUserManager.getInstance().getCacheUser();
		}
		String userid = user == null ? "" : user.getUserid();
		String token = user == null ? "" : (user.getToken() == null ? "xx" : user.getToken());
		String flag = MD5.crypt(userid + packageName + token + time + systemOs);
		String host = MobUserManager.getInstance().getServiceHost();

		String url = host + "/api/login?user_id=" + userid + "&token=" + token + "&package=" + packageName + "&time=" + time
				+ "&os=" + systemOs + "&sign=" + flag + "&customer_id=" + DeviceInfo.getInstance(getContext()).getCustomerId()
				+ "&logo=" + Config.gmTitle.toLowerCase();
		if (user != null) {
			url += ("&role_id=" + user.getRoleId() + "&role_name=" + user.getRoleName() + "&server_id="
					+ user.getServerId() + "&server_name=" + user.getServerName())
					+ "&career="
					+ user.getProfession()
					+ "&level="
					+ user.getLevel()
					+ "&power="
					+ user.getPower()
					+ "&gold=" + user.getGold() + "&client_id=" + Config.clientId;
		}
		LogUtil.printHTTP(url);
		servicesWebView.loadUrl(url);
	}

	// private void loadUrl(String token) {
//		String packageName = DeviceInfo.getInstance(null).getPackageName();
//		int systemOs = 1;
//		String time = System.currentTimeMillis() / 1000 + "";
//		MobUser user = MobUserManager.getInstance().getCurrentUser();
//		if (user == null) {
//			user = MobUserManager.getInstance().getCacheUser();
//		}
//		String userid = user == null ? "" : user.getUserid();
//		String flag = MD5.crypt(userid + packageName + token + time + systemOs);
//		String host = MobUserManager.getInstance().getServiceHost();
//		String url = host + "/api/login?user_id=" + userid + "&token=" + token
//				+ "&package=" + packageName + "&time=" + time + "&os="
//				+ systemOs + "&sign=" + flag + "&customer_id="
//				+ DeviceInfo.getInstance(getContext()).getCustomerId()
//				+ "&logo=" + Config.gmTitle.toLowerCase();
//		if (user != null) {
//			url += ("&role_id=" + user.getRoleId() + "&role_name="
//					+ user.getRoleName() + "&server_id=" + user.getServerId()
//					+ "&server_name=" + user.getServerName())
//					+ "&career="
//					+ user.getProfession()
//					+ "&level="
//					+ user.getLevel()
//					+ "&power="
//					+ user.getPower()
//					+ "&gold="
//					+ user.getGold() + "&client_id=" + Config.clientId;
//		}
//		servicesWebView.loadUrl(url);
//	}

	// private void updateToken() {
	// servicesWebView.showProcessDialog();
	// final DeviceInfo info = DeviceInfo.getInstance(getOwnerActivity());
	// SdkHttpRequest request = SdkHttpRequest.dynamicToken();
	// MobUser user = MobUserManager.getInstance().getCurrentUser();
	// long time = System.currentTimeMillis() / 1000;
	// String userId = user.getUserid();
	// String token = user.getToken();
	// request.initHeader(info);
	// request.addPostValue(ParameterKey.USER_ID, userId);
	// request.addPostValue(ParameterKey.TOKEN, token);
	// request.addPostValue(ParameterKey.TIME, time + "");
	// request.addPostValue(ParameterKey.FLAG,
	// MD5.crypt(userId + token + WebAPI.LOGIN_KEY + time));
	// request.setHttpEventListener(new HttpEventListener() {
	// @Override
	// public void requestDidSuccess(HttpRequest httpRequest, String result) {
	// try {
	// JSONObject o = new JSONObject(result);
	// if (o.optInt("status", 0) != 1) {
	// requestDidFailed(httpRequest);
	// return;
	// } else {
	// try {
	// JSONObject data = o.getJSONObject("data");
	// String token = data.optString("dynameicToken");
	// loadUrl(token);
	// } catch (JSONException e) {
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void requestDidStart(HttpRequest httpRequest) {
	//
	// }
	//
	// @Override
	// public void requestDidFailed(HttpRequest httpRequest) {
	// Toast.makeText(getContext(),
	// ResourceUtil.getStringId("vsgm_tony_err_unknown"),
	// Toast.LENGTH_SHORT).show();
	// servicesWebView.loadErrorPage("");
	// }
	// });
	// request.asyncStart();
	// }

	@Override
	public void dismiss() {
		// AlphaAnimation aa = new AlphaAnimation(1, 0.3f);
		// aa.setDuration(100);
		// aa.setAnimationListener(new AnimationListener() {
		// @Override
		// public void onAnimationStart(Animation animation) {
		//
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		//
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// SdkServiceViewDialog.super.dismiss();
		// }
		// });
		super.dismiss();
		// servicesWebView.startAnimation(aa);
		servicesWebView.onOffLine();
	}

}
