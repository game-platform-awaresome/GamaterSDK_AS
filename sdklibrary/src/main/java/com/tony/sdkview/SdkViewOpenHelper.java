package com.tony.sdkview;

import org.json.JSONException;
import org.json.JSONObject;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.define.SPUtil;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.dialog.SdkGameDialog;
import com.gamater.dialog.SdkGameWebViewDialog;
import com.gamater.dialog.SdkGameDialog.ViewType;
import com.gamater.dialog.SdkGameWebViewDialog.WebViewType;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.dialog.SdkServiceViewDialog;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.NetCheckUtil;
import com.gamater.util.ResourceUtil;
import com.vsgm.sdk.SDKActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class SdkViewOpenHelper {
	public static String ACTION_LOGIN = "viewLogin";
	public static String ACTION_REGISTER = "viewRegister";
	public static String ACTION_SETTING = "viewSetting";
	public static String ACTION_FORGET_PASSWD = "viewForgetPW";
	public static String ACTION_UPDATE_ACCOUNT = "viewUpdateAccount";

	public SdkViewOpenHelper() {
	}

	private boolean checkUserLogin() {
		if (MobUserManager.getInstance() == null)
			return false;
		if (MobUserManager.getInstance().getCurrentUser() != null) {
			return true;
		}
		return false;
	}

	private void newSdkDialogView(final View view) {
		GamaterSDK.getInstance().getHandler().post(new Runnable() {
			@Override
			public void run() {
				try {
					new SdkGameDialog(GamaterSDK.getInstance().getActivity())
							.showWithCallback(new Runnable() {
								@Override
								public void run() {
									SdkDialogViewManager.doAddView(view);
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void checkUrlConfig(Runnable runnable) {
		Activity activity = GamaterSDK.getInstance().getActivity();
		if (activity == null)
			return;
		if (NetCheckUtil.isNetworkStatus(activity)) {
			MobUserManager mobUserManager = MobUserManager.getInstance();
			String jsonData = SPUtil.getJSONData(activity);
			if (!TextUtils.isEmpty(jsonData)) {
				try {
					JSONObject jsonObject = new JSONObject(jsonData);
					if (!TextUtils.isEmpty(jsonObject.getString("facebook"))) {
						runnable.run();
					} else {
						if (mobUserManager.getServiceHost()
								.equalsIgnoreCase("")
								|| mobUserManager.getConfigJson(activity) == null) {
							mobUserManager.requestUrls();
						}
						Toast.makeText(activity,
								ResourceUtil.getStringId("vsgm_error_network"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				if (mobUserManager.getServiceHost().equalsIgnoreCase("")
						|| mobUserManager.getConfigJson(activity) == null) {
					mobUserManager.requestUrls();
				}
				Toast.makeText(activity,
						ResourceUtil.getStringId("vsgm_error_network"),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(activity,
					ResourceUtil.getStringId("vsgm_error_network"),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void openFBWebDialog() {
		checkUrlConfig(new Runnable() {
			@Override
			public void run() {
				new SdkGameWebViewDialog(GamaterSDK.getInstance()
						.getActivity(), WebViewType.Facebook).show();
			}
		});
	}

	public static void openHomeWebDialog() {
		checkUrlConfig(new Runnable() {
			@Override
			public void run() {
				Activity activity = GamaterSDK.getInstance().getActivity();
				Intent intent = new Intent(activity, Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
				intent.putExtra("URL", MobUserManager.getInstance().getGwUrl());
				intent.putExtra("requestToken", true);
				intent.putExtra(MVMainActivity.WIN_TYPE, WinType.Web.toString());
				activity.startActivity(intent);
			}
		});
	}

	public static void openServiceDialog() {
		checkUrlConfig(new Runnable() {
			@Override
			public void run() {
				new SdkServiceViewDialog(GamaterSDK.getInstance().getActivity()).show();
			}
		});
	}

	public boolean openAction(Context ctx, String action, String param, Object extra, int statisticsId) {
		if (ACTION_LOGIN.equals(action)) {
			if (checkUserLogin())
				return false;
			if (SdkDialogViewManager.isManagerReady()) {
				SdkDialogViewManager.doAddView(NormalLoginView.createView(ctx));
			} else {
				GamaterSDK.getInstance().popLoginView();
			}
			return true;
		} else if (ACTION_REGISTER.equals(action)) {
			if (checkUserLogin()) {
				return false;
			}
			if (SdkDialogViewManager.isManagerReady()) {
				SdkDialogViewManager.doAddView(RegisterView.createView(ctx));
			} else {
				newSdkDialogView(RegisterView.createView(ctx));
			}
			return true;
		} else if (ACTION_SETTING.equals(action)) {
			if (checkUserLogin())
				return false;
			if (SdkDialogViewManager.isManagerReady()) {
				SdkDialogViewManager.doAddView(SdkSettingView.createView(ctx));
			} else {
				newSdkDialogView(SdkSettingView.createView(ctx));
			}
			return true;
		} else if (ACTION_FORGET_PASSWD.equals(action)) {
			if (checkUserLogin())
				return false;
			if (SdkDialogViewManager.isManagerReady()) {
				SdkDialogViewManager.doAddView(ForgetPasswdView.createView(ctx));
			} else {
				newSdkDialogView(ForgetPasswdView.createView(ctx));
			}
			return true;
		} else if (ACTION_UPDATE_ACCOUNT.equals(action)) {
			if (!checkUserLogin())
				return false;
			MobUser user = MobUserManager.getInstance().getCurrentUser();
			if ("0".equals(user.getType())) {
				if (SdkDialogViewManager.isManagerReady()) {
					SdkDialogViewManager.doAddView(UpdateAccountView.createView(ctx));
				} else {
					new SdkGameDialog(GamaterSDK.getInstance().getActivity(), ViewType.UpdateAccount).show();
				}
			}
			return true;
		} else if (SdkGameDialog.ViewType.ChangePassword.toString().equals(action)) {
			if (!checkUserLogin())
				return false;
			if (SdkDialogViewManager.isManagerReady()) {
				SdkDialogViewManager.doAddView(ChangePasswdView.createView(ctx));
			} else {
				new SdkGameDialog(GamaterSDK.getInstance().getActivity(), ViewType.ChangePassword).show();
			}
			return true;
		}
		return false;
	}

}
