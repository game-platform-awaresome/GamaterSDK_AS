package com.gamater.sdk.game;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.po.MobUser;
import com.gamater.common.AppListCollecter;
import com.gamater.common.Config;
import com.gamater.common.CrashHandler;
import com.gamater.common.GoogleGameLoginHelper;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.define.PaymentParam;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.dialog.SdkGameDialog;
import com.gamater.dialog.util.DialogUtil;
import com.gamater.payment.AcGameIAB;
import com.gamater.payment.GamaterIABListener;
import com.gamater.receiver.InstallReceiver;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.dialog.NoticeDialog;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.sdk.facebook.FbFriendPickerDialog;
import com.gamater.sdk.facebook.FbFriendPickerView.FbFriendPickerViewListener;
import com.gamater.sdk.facebook.FbFriendsCallback;
import com.gamater.sdk.facebook.FbInviteCallback;
import com.gamater.sdk.facebook.FbInviteFriend;
import com.gamater.sdk.facebook.FbShareCallback;
import com.gamater.util.FileDataUtil;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.tony.facebook.SdkFacebookDialog;
import com.tony.floatmenu.SDKMenuManager;
import com.vsgm.sdk.SDKActivity;

public class GamaterSDK {
	public final static String LOG_TAG = "1.2.2";
	private static GamaterSDK m_instance = null;
	private Activity activity;
	private Context context;
	private boolean isShowMenu = true;
	private GamaterSDKListener acgameSDKListener;
	private Handler mainHandler;

	public Context getContext() {
		if (context == null && activity != null)
			context = activity.getApplicationContext();
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static synchronized GamaterSDK initSDK(Activity activity, String clientId, boolean isShowLog) {

		if (m_instance == null) {
			try {
				m_instance = new GamaterSDK(activity, clientId, isShowLog);
//				SDKMenuManager.getInstance(activity).initParentView(null);
//				SDKMenuManager.getInstance(activity).popupMenu();
			} catch (Exception e) {
				Toast.makeText(activity, CrashHandler.obtainExceptionInfo(e), Toast.LENGTH_SHORT).show();

				e.printStackTrace();
			}
		} else {
			if (m_instance.activity != null
					&& m_instance.activity.getClass().getName().equals(activity.getClass().getName())
					&& m_instance.activity != activity) {
				m_instance.destroy();
				return initSDK(activity, clientId, isShowLog);
			}
			m_instance.setActivity(activity);
			m_instance.setConfig(isShowLog, clientId);
		}
		return m_instance;
	}

	public void setAdWords(String id, String tag, String money) {
		AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(), id, tag, money, false);
	}

	public void setConfig(boolean isShowLog, String clientId) {
		Config.isShowLog = isShowLog;
//		Config.isTestMode = Integer.parseInt(FileDataUtil.getFileData(activity, "isTestMode", "1"));
		Config.clientId = clientId;
		FileDataUtil.saveFileData(activity, "is_show_log", "" + Config.isShowLog);
		FileDataUtil.saveFileData(activity, "client_id", "" + Config.clientId);
	}

	public static synchronized GamaterSDK getInstance() {
		return m_instance;
	}

	private GamaterSDK(final Activity activity, final String clientId, boolean isShowLog) {
		this.activity = activity;
		DeviceInfo info = DeviceInfo.getInstance(activity);
		checkSdkCallMethod();
		setConfig(isShowLog, clientId);
		AcGameIAB.getInstance(activity, isShowLog);
		MobUserManager.initUserManager(activity, isShowLog);
		SDKMenuManager.getInstance(activity);
		AppListCollecter.init(activity);
		CrashHandler.getInstance().setCrashHanler(activity);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainHandler = new Handler();
				InstallReceiver.sendInstall(activity, mainHandler, 1000);
			}
		});

//		try {
//
//			HashMap<String, Object> datamap = new HashMap<String, Object>();
//			datamap.put(Feature.INPUTITEMS.KOCHAVA_APP_ID, AppUtil.GetMetaDataString(activity, "my_kochava_app_guid"));
//			datamap.put(Feature.INPUTITEMS.REQUEST_ATTRIBUTION, true);
//			Feature kTracker = new Feature(activity, datamap);
//
//			// HashMap<String, String> identityLinkMap = new HashMap<String,
//			// String>();
//			// identityLinkMap.put("customer_user_id",
//			// MD5.crypt(info.getPackageName() + info.getAndroidId() +
//			// info.getIMEI()));
//			// datamap.put(Feature.INPUTITEMS.IDENTITY_LINK , identityLinkMap);
//
//			kTracker.event("customer_user_id", MD5.crypt(info.getPackageName() + info.getAndroidId() + info.getIMEI()));
//			// Kochava 初始化
//			// Feature kTracker = new Feature(this.activity,
//			// AppUtil.GetMetaDataString(this.activity,"my_kochava_app_guid"));
//
//		} catch (Exception e) {
//			alertMessage("Kochava 初始化异常，请检查jar是否正常引入", false);
//		} catch (Error e) {
//			alertMessage("Kochava 初始化异常，请检查jar是否正常引入", false);
//		}
//
//		try {
//			AppsFlyerLib.setAppsFlyerKey("n9EW6sqgVTYsmSpfqyKqNL");
//			AppsFlyerLib.setAppUserId(MD5.crypt(info.getPackageName() + info.getAndroidId() + info.getIMEI()));
//			AppsFlyerLib.sendTracking(this.activity);
//			
//			// AppsFlyerLib.getInstance().setImeiData(info.getIMEI());
//			// AppsFlyerLib.getInstance().setAndroidIdData(info.getAndroidId());
//			// AppsFlyerLib.getInstance().setCustomerUserId(MD5.crypt(info.getPackageName() + info.getAndroidId() + info.getIMEI()));
//			// AppsFlyerLib.getInstance().startTracking(activity.getApplication(), "n9EW6sqgVTYsmSpfqyKqNL");
//			
//		} catch (Exception e) {
//			alertMessage("Appsflyer 初始化异常，请检查jar是否正常引入", false);
//		} catch (Error e) {
//			alertMessage("Appsflyer 初始化异常，请检查jar是否正常引入", false);
//		}

		LogUtil.print(Config.gmTitle, "SDK version " + GamaterSDK.LOG_TAG + " start.");

	}

	private void checkSdkCallMethod() {
		StackTraceElement stack[] = new Throwable().getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			if (stack[i].getClassName().equals(Activity.class.getName()) && stack[i].getMethodName().equals("performCreate")) {
				return;
			}
		}
		alertMessage("SDK.initSDK必须在主Activity的onCreate中调用", false);
	}

	private void alertMessage(String msg, boolean cancel) {
		Resources res = activity.getResources();
		String title = Config.gmTitle;
		AlertDialog.Builder builder = DialogUtil.showDialog(activity, title, msg);
		if (cancel) {
			String done = res.getString(ResourceUtil.getStringId("vsgm_tony_btn_done"));
			builder.setPositiveButton(done, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		builder.setCancelable(false);
		builder.show();
	}

	public Handler getHandler() {
		return mainHandler;
	}

	public void initPayment(List<String> skus, GamaterIABListener listener) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		mIab.init(skus);
		mIab.setAcGameIABListener(listener);
	}

	protected void requestApi(HttpRequest request) {
		if (request == null)
			return;
		request.setHttpEventListener(new HttpEventListener() {

			@Override
			public void requestDidSuccess(HttpRequest httpRequest, String result) {

				String funcation = httpRequest.getFuncation();
				if (funcation.equals(APIs.WEB_API_THIRD_LOGIN))
					return;
				try {
					JSONObject obj = new JSONObject(result);
					MobUser user = new MobUser(result);
					if (user.getStatus() == 1) {
						MobUserManager mobUserManager = MobUserManager.getInstance();
						mobUserManager.saveAccount(user.getUserid(), obj.toString());
						mobUserManager.setCurrentUser(user);
						GamaterSDK.getInstance().resumeGmae(null);
						GamaterSDK.getInstance().showNoticeDialog();
						mobUserManager.setIsLoginIng(false);
						loginSuccessCallback(user, funcation.equals(APIs.WEB_API_FREE_LOGIN) ? 1 : 2, null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestDidStart(HttpRequest httpRequest) {
				// TODO Auto-generated method stub
			}

			@Override
			public void requestDidFailed(HttpRequest httpRequest) {
				// TODO Auto-generated method stub
			}
		});
		request.asyncStart();
	}

	private MobUser user;
	public void popLoginView() {
		if (activity.isFinishing())
			return;

//		 MobUserManager mum = MobUserManager.getInstance();
//		 List<MobUser> list = mum.accountList();
//		 if (list != null && list.size() > 0) {
//			for (MobUser u : list) {
//				if (!u.getType().equalsIgnoreCase("0")) {
//					if (u.getToken() != null) {
//						user = u;
//						break;
//					}
//				}
//			}
//		 }
//		 if (user != null) {
//			requestApi(SdkHttpRequest.eLoginRequest(user.getUserid(), user.getToken()));
//		 } else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						SdkGameDialog dialog = new SdkGameDialog(activity);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
//		}

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
	public void roleReport(String profession, String serverId, String serverName, String roleId, String rolesName, int roleLevel) {
		MobUserManager mobUserManager = MobUserManager.getInstance();
		if (mobUserManager.getServiceHost().equalsIgnoreCase("") || mobUserManager.getConfigJson(activity) == null) {
			mobUserManager.requestUrls();
		}
		MobUser user = mobUserManager.getCurrentUser();
		if (user == null)
			return;
		user.setRoleId(roleId);
		user.setRoleName(rolesName);
		user.setServerId(serverId);
		user.setServerName(serverName);
		user.setLevel(roleLevel);
		user.setProfession(profession);
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null)
			mIab.roleReport(user.getUserid(), profession, serverId, serverName, roleId, rolesName, roleLevel);
	}


	public void getFacebookFriends(FbFriendsCallback callback) {
		if (MobUserManager.getInstance().getCurrentUser() == null || !MobUserManager.getInstance().getCurrentUser().isFacebook())
			return;
		FacebookHelper.getInstance().getFacebookFriends(callback);
	}

	public void shareToFb(final String link, final String caption, final String description, final String pictureURL,
			final FbShareCallback callback) {
		if (!FacebookSdk.isInitialized() || FacebookSdk.getApplicationId() == null) {
			return;
		}
		if (AccessToken.getCurrentAccessToken() == null) {
			FacebookHelper.getInstance().fbLogin(new FacebookCallback<LoginResult>() {
				@Override
				public void onSuccess(LoginResult result) {
					FacebookHelper.getInstance().share2Fb(activity, link, caption, description, pictureURL, callback);
				}

				@Override
				public void onError(FacebookException error) {
				}

				@Override
				public void onCancel() {
				}
			});
		} else {
			FacebookHelper.getInstance().share2Fb(activity, link, caption, description, pictureURL, callback);
		}
	}

	public void destroy() {
		LogUtil.printLog("destroy game...");
		if (SDKMenuManager.getInstance(activity) != null) {
			SDKMenuManager.getInstance(activity).dismissMenu();
			SDKMenuManager.getInstance(activity).destory();
		}
		SdkDialogViewManager.dialogDismiss();
		SdkDialogViewManager.destory();
		MobUserManager.getInstance().destroy();
		AcGameIAB.getInstance().destroy();
		m_instance = null;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void resumeGmae(Activity a) {
		if (a != null) {
			a.sendBroadcast(new Intent("com.gamater.LoginFinish"));
		}
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (isShowMenu && user != null) {
			showPopupMenu();
		}
	}

	public void logout() {
		if (acgameSDKListener != null) {
			acgameSDKListener.didLogout();
		}
		SdkDialogViewManager.dialogDismiss();
		SdkDialogViewManager.destory();
		Config.payType = 1;
		MobUserManager.getInstance().setCurrentUser(null);
		MobUserManager.getInstance().setRankDataUpload(false);
		SDKMenuManager manager = SDKMenuManager.getInstance(activity);
		GoogleGameLoginHelper.getInstance().logout();
		if (manager != null) {
			manager.updateMenuStatus();
			manager.hideMenuView();
		}
	}

	public void showNoticeDialog() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								MobUserManager mobUserManager = MobUserManager.getInstance();
								if (!mobUserManager.isHadShowNotice()) {
									JSONObject data = mobUserManager.getNoticeContent();
									NoticeDialog dialog = new NoticeDialog(getActivity());
									if (data != null) {
										dialog.setShowClose("1".equals(data.optString("isShowClose")));
										dialog.show(data.optString("content"), "text/html; charset=UTF-8", null);

									}
									mobUserManager.setHadShowNotice(true);
								}
							} catch (Exception e) {
							}
						}
					}, 1000);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void paymentDefault(PaymentParam param) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null) {
			mIab.payment(param);
		} else {
			LogUtil.printHTTP("payment not init");
		}
	}

	public void paymentDefault(Activity a, PaymentParam param) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null) {
			mIab.payment(a, param);
		} else {
			LogUtil.printHTTP("payment not init");
		}
	}

	public void paymentOther(Activity activity, PaymentParam param) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null)
			mIab.paymentOther(activity, param);
	}

	public void paymentOther(PaymentParam param) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null)
			mIab.paymentOther(param);
	}

	public void showPopupMenu() {
		SDKMenuManager.getInstance(activity).popupMenu();
		ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
		View decorChild = decor.getChildAt(0);
		if (isMenuView(decorChild)) {
			return;
		}
		for (int i = 1; i < decor.getChildCount(); i++) {
			View c = decor.getChildAt(i);
			if (isMenuView(c)) {
				decor.bringChildToFront(c);
			}
		}
	}

	private boolean isMenuView(View v) {
		if (v.getTag() != null && SDKMenuManager.MENU_TAG.equals(v.getTag().toString())) {
			return true;
		}
		return false;
	}

	public GamaterSDKListener getAcGameSDKListener() {
		return acgameSDKListener;
	}

	public void setGamaterSDKListener(GamaterSDKListener gamaterSDKListener) {
		this.acgameSDKListener = gamaterSDKListener;
	}

	public boolean isShowLog() {
		return Config.isShowLog;
	}

	public void setShowLog(boolean isShowLog) {
		Config.isShowLog = isShowLog;
	}

	public boolean isShowMenu() {
		return isShowMenu;
	}

	public void setShowMenu(boolean isShowMenu) {
		this.isShowMenu = isShowMenu;
	}

	public void onResume() {
		LogUtil.printLog("onResume begin ...");
		SDKMenuManager.getInstance(activity).onResume();
		checkFailedOrder();
	}

	public void onPause() {
		SDKMenuManager.getInstance(activity).onPause();
	}

	private void checkFailedOrder() {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab == null) {
			mIab = AcGameIAB.getInstance(activity, Config.isShowLog);
		}
		mIab.checkFailedOrder();
		CrashHandler.sendResponseTime(activity);
	}

	public boolean handlerResult(int requestCode, int resultCode, Intent data) {
		AcGameIAB mIab = AcGameIAB.getInstance();
		if (mIab != null)
			return mIab.handlerResult(requestCode, resultCode, data);
		return false;
	}

	public void facebookFriendsInvite(FbInviteCallback callback) {
		FacebookHelper.getInstance().setInviteCallback(callback);
		facebookFriendsInvite();
	}

	public void facebookFriendsInvite() {
		if (!FacebookSdk.isInitialized() || FacebookSdk.getApplicationId() == null) {
			return;
		}
		if (AccessToken.getCurrentAccessToken() == null) {
			FacebookHelper.getInstance().fbLogin(new FacebookCallback<LoginResult>() {
				@Override
				public void onSuccess(LoginResult result) {
					getFacebookInvitable();
				}

				@Override
				public void onError(FacebookException error) {
				}

				@Override
				public void onCancel() {
				}
			});
		} else {
			getFacebookInvitable();
		}
	}

	private void getFacebookInvitable() {
		final OKgameDialogProcess p = new OKgameDialogProcess(activity);
		p.setCanCancel(false);
		new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/invitable_friends", null, HttpMethod.GET, new GraphRequest.Callback() {
			public void onCompleted(GraphResponse response) {
				p.dismiss();
				if (response.getError() == null && response.getJSONObject() != null) {
					JSONArray array = response.getJSONObject().optJSONArray("data");
					LogUtil.printLog("FbInviteFriend response: " +  response.getJSONObject().toString());
					ArrayList<FbInviteFriend> data = new ArrayList<FbInviteFriend>();
					for (int i = 0; i < array.length(); i++) {
						FbInviteFriend f = new FbInviteFriend();
						JSONObject o = array.optJSONObject(i);
						if (o != null) {
							f.id = o.optString("id");
							f.name = o.optString("name");
							f.picUrl = o.optJSONObject("picture").optJSONObject("data").optString("url");
						}
						data.add(f);
					}
					FbFriendPickerDialog d = new FbFriendPickerDialog(activity, data, new FbFriendPickerViewListener() {
						@Override
						public void onPick(String[] id, String[] name) {
							String ids = "";
							String names = "";
							for (int i = 0; i < id.length; i++) {
								ids += id[i];
								names += name[i];
								if (i < id.length - 1){
									ids += ",";
									names += ",";
								}
							}
							if (ids.length() > 0) {
								FacebookHelper.getInstance().setInviteNames(name);
								Intent intent = new Intent(activity, Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
								intent.putExtra(MVMainActivity.WIN_TYPE, WinType.FbInviteFriend.toString());
								intent.putExtra("friend_ids", ids);
								intent.putExtra("friend_names", names);
								activity.startActivity(intent);
							}
						}

						@Override
						public void onCancel() {

						}
					});
					d.show();
				}
			}
		}).executeAsync();
		p.show();
	}

	public boolean isFacebookLogin() {
		if (!FacebookSdk.isInitialized() || FacebookSdk.getApplicationId() == null) {
			return false;
		}
		if (AccessToken.getCurrentAccessToken() == null) {
			return false;
		}
		return !AccessToken.getCurrentAccessToken().isExpired();
	}

	public void showFacebook() {
		if (MobUserManager.getInstance().getCurrentUser() == null) {
			LogUtil.printHTTP("showFacebook without login");
			return;
		}
		if (MobUserManager.getInstance().getCurrentUser().getRoleId().length() == 0) {
			LogUtil.printHTTP("showFacebook without role");
			return;
		}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new SdkFacebookDialog(activity).show();
			}
		});
	}

	protected void loginSuccessCallback(MobUser user, int type, String typeName) {
		SDKMenuManager.getInstance(null).hideMenuView();
		if (GamaterSDK.getInstance().getAcGameSDKListener() != null && user != null) {
			SDKMenuManager.getInstance(null).updateMenuViewLoginToday();
			GamaterSDK.getInstance().getAcGameSDKListener().didLoginSuccess(user);
		}
	}

}
