package com.tony.sdkview;

import static com.gamater.dialog.SdkDialogViewManager.hideLoadingView;
import static com.gamater.dialog.SdkDialogViewManager.showLoadingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.account.po.ThirdType;
import com.gamater.common.Config;
import com.gamater.common.ThirdLoginHelper;
import com.gamater.common.ThirdLoginHelper.GoogleLoginCallback;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.SPUtil;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.payment.GamaterIAB;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.Encryption;
import com.gamater.util.LogUtil;
import com.gamater.util.NetCheckUtil;
import com.gamater.util.ResourceUtil;
import com.tony.floatmenu.SDKMenuManager;
import com.tony.view.SdkTipsTextView;
import com.tony.view.WarningLinearLayout;
import com.tony.viewinterface.BaseSdkView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BaseLinearLayout extends LinearLayout implements BaseSdkView, HttpEventListener {

	public BaseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseLinearLayout(Context context) {
		super(context);
	}

	public LayoutParams getSdkViewLayoutParams() {
		return new LayoutParams(dip2px(320), LayoutParams.WRAP_CONTENT);
//		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public Animation getStartAnimation(AnimationListener listener) {
		AnimationSet set = new AnimationSet(true);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1);
		aa.setDuration(100);
		ScaleAnimation sa = new ScaleAnimation(1.1f, 1f, 1.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(100);
		set.addAnimation(aa);
		set.addAnimation(sa);
		if (listener != null)
			set.setAnimationListener(listener);
		return set;
	}

	@Override
	public Animation getEndAnimation(AnimationListener listener) {
		AnimationSet set = new AnimationSet(true);
		AlphaAnimation aa = new AlphaAnimation(1, 0.5f);
		aa.setDuration(100);
		ScaleAnimation sa = new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(100);
		set.addAnimation(aa);
		set.addAnimation(sa);
		if (listener != null)
			set.setAnimationListener(listener);
		return set;
	}

	@Override
	public boolean doViewGoBack() {
		if (getChildCount() > 1) {
			View view = getChildAt(getChildCount() - 1);
			if (view instanceof BaseSdkView) {
				return ((BaseSdkView) view).doViewGoBack();
			}
			return false;
		}
		return false;
	}

	@Override
	public void initView() {

	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	protected void startView(View newView) {
		SdkDialogViewManager.doAddView(newView);
	}

	protected void hideSoftInput() {
		SdkDialogViewManager.hideSoftInput(getContext());
	}

	@Override
	public String getViewTitle() {
		return null;
	}

	@Override
	public boolean interceptOnBackEvent() {
		return false;
	}

	private SdkTipsTextView errorView;

	protected void showError(String errorStr) {
		if (errorView == null) {
			errorView = (SdkTipsTextView) findViewWithTag(SdkTipsTextView.TIP_VIEW_TAG);
		}
		if (errorView != null) {
			errorView.showError(errorStr);
		} else {
			showToast(errorStr);
		}
	}

	protected void showToast(int stringId) {
		Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(String str) {
		Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
	}

	protected void showError(int errorStrRes) {
		String error = getContext().getResources().getString(errorStrRes);
		showError(error);
	}

	protected void makeEditTextWarning(EditText et) {
		if (et == null)
			return;
		if (et.getParent() instanceof WarningLinearLayout) {
			((WarningLinearLayout) et.getParent()).makeWarning();
		}
	}

	public boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = ".*@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	protected static int findLayoutId(String layoutName) {
		return ResourceUtil.getLayoutId(layoutName);
	}

	protected static int findId(String name) {
		return ResourceUtil.getId(name);
	}

	protected static int findStringId(String stringName) {
		return ResourceUtil.getStringId(stringName);
	}

	protected static int findDrawableId(String drawableName) {
		return ResourceUtil.getDrawableId(drawableName);
	}

	protected void requestApi(HttpRequest request) {
		if (request == null)
			return;
		request.setHttpEventListener(this);
		request.asyncStart();
	}

	protected void thirdLogin(ThirdType type) {
		if (NetCheckUtil.isNetworkStatus(getContext())) {
			if (type == ThirdType.facebook) {
				if (NetCheckUtil.isNetworkStatus(getContext())) {
					String configString = SPUtil.getConfigJsonString(getContext());
					if (!TextUtils.isEmpty(configString)) {
						JSONObject configJson;
						try {
							configJson = new JSONObject(Encryption.decryptDES(configString));
							ConfigUtil.initConfigWithConfigJson(configJson);
							String facebookid = configJson.optString(MobUserManager.CONFIG_KEY_FB);
							if (facebookid != null && facebookid.length() > 0) {
								FacebookSdk.setApplicationId(facebookid);
								FacebookSdk.sdkInitialize(GamaterIAB.getInstance().getContext());
								AppEventsLogger.deactivateApp(GamaterIAB.getInstance().getContext(), facebookid);
							}
							facebookLogin();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getContext(), ResourceUtil.getStringId("vsgm_error_network"), Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getContext(), ResourceUtil.getStringId("vsgm_error_network"), Toast.LENGTH_SHORT)
							.show();
				}
			} else if (type == ThirdType.google) {
				googleLogin();
			} else if (type == ThirdType.instagram) {
				instagramLogin();
			} else if (type == ThirdType.twitter) {
				twitterLogin();
			} else if (type == ThirdType.rc) {
				startView(RcLoginView.createView(getContext()));
			}
		} else {
			showError(ResourceUtil.getStringId("vsgm_error_network"));
		}
	}

	protected void facebookLogin() {
		if (!FacebookSdk.isInitialized() || FacebookSdk.getApplicationId() == null
				|| FacebookSdk.getApplicationId().length() == 0)
			return;
		ThirdLoginHelper.getInstance().fbLogin(new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				if (Config.isShowLog) {
					LogUtil.printHTTP("Facebook Login onSuccess(LoginResult loginResult)");
				}
				requestApi(
						SdkHttpRequest.thirdLoginRequest(ThirdType.facebook, loginResult.getAccessToken().getToken()));
				Profile.fetchProfileForCurrentAccessToken();
			}

			@Override
			public void onCancel() {
				if (Config.isShowLog) {
					LogUtil.printHTTP("Facebook Login onCancel");
				}
			}

			@Override
			public void onError(FacebookException exception) {
				exception.printStackTrace();
				if (Config.isShowLog) {
					LogUtil.printHTTP("Facebook Login onError");
				}
			}
		});
	}

	/**
	 * 登录Google账号
	 */
	protected void googleLogin() {
		ThirdLoginHelper.getInstance().gpLogin(getContext(),
				new GoogleLoginCallback() {
					@Override
					public void onSuccess(String token, String email) {
						if (Config.isShowLog) {
							LogUtil.printHTTP("Google Login OnSuccess");
						}
						requestApi(SdkHttpRequest.thirdLoginRequest(ThirdType.google, token));
					}

					@Override
					public void onFail() {
						if (Config.isShowLog) {
							LogUtil.printHTTP("Google Login onFail");
						}
					}

					@Override
					public void onFail(int available) {

					}
				});
	}

	protected void instagramLogin() {
		// InstagramApp mApp = new InstagramApp(
		// SdkDialogViewManager.getOwnerActivity());
		// mApp.setListener(new OAuthAuthenticationListener() {
		// @Override
		// public void onSuccess() {
		// InstagramSession session = new InstagramSession(
		// SdkDialogViewManager.getOwnerActivity());
		// String accessToken = session.getAccessToken();
		// requestApi(SdkHttpRequest.thirdLoginRequest(
		// ThirdType.instagram, accessToken));
		// }
		//
		// @Override
		// public void onFail(String error) {
		//
		// }
		// });
		// if (mApp.hasAccessToken()) {
		// instagramLogin();
		// } else {
		// mApp.authorize();
		// }
	}

	protected void twitterLogin() {
		// TwitterLoginDialog d = new TwitterLoginDialog(
		// SdkDialogViewManager.getOwnerActivity());
		// d.setTwitterOAuthListener(new TwitterOAuthListener() {
		// @Override
		// public void onComplete(String token, String secret) {
		// requestApi(SdkHttpRequest.thirdLoginRequest(ThirdType.twitter,
		// token));
		// }
		// });
		// d.show();
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		showLoadingView();
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		hideLoadingView();
		showError(findStringId("vsgm_tony_err_unknown"));
	}

	@Override
	public boolean isMenuEnable() {
		return false;
	}

	public void showError(EditText et, String e) {
		showError(e);
		makeEditTextWarning(et);
	}

	public void showError(EditText et, int eRes) {
		showError(eRes);
		makeEditTextWarning(et);
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		try {
			hideLoadingView();
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 0) {
				int errorCode = obj.getInt("errorCode");
				switch (errorCode) {
				case 100:
					showError(findStringId("vsgm_tony_err_100"));
					break;
				case 101:
					showError(findStringId("vsgm_tony_err_101"));
					break;
				case 102:
					showError(findStringId("vsgm_tony_err_102"));
					break;
				case 103:
					showError(findStringId("vsgm_tony_err_103"));
					break;
				case 104:
					showError(findStringId("vsgm_tony_err_104"));
					break;
				case 105:
					showError(findStringId("vsgm_tony_err_105"));
					break;
				case 107:
					showError(findStringId("vsgm_tony_err_107"));
					break;
				case 108:
					showError(findStringId("vsgm_tony_err_108"));
					break;
				case 109:
					showError(findStringId("vsgm_tony_err_109"));
					break;
				case 211:
					showError(findStringId("vsgm_tony_err_211"));
					break;
				default:
					showError(findStringId("vsgm_tony_err_unknown"));
					break;
				}
			} else {
				String funcation = httpRequest.getFuncation();
				if (funcation == APIs.WEB_API_THIRD_LOGIN) {
					MobUser user = new MobUser(result);
					if (user.getStatus() == 1) {
						MobUserManager mobUserManager = MobUserManager.getInstance();
						mobUserManager.saveAccount(user.getUserid(), obj.toString());
						mobUserManager.setCurrentUser(user);
//						OnLineHelper.getInstance(getContext()).start();
						GamaterSDK.getInstance().resumeGmae(null);
						GamaterSDK.getInstance().showNoticeDialog();
						mobUserManager.setIsLoginIng(false);
						loginSuccessCallback(user, 3, httpRequest.getTag().toString());
						getHandler().postDelayed(new Runnable() {
							@Override
							public void run() {
								SdkDialogViewManager.dialogDismiss();
							}
						}, 300);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setLoginToday(Context ctx) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		Editor editor = ctx.getSharedPreferences("sdk_login_today", 0).edit();
		editor.putBoolean(date, true).commit();
	}

	private static boolean isLoginToday(Context ctx) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		return ctx.getSharedPreferences("sdk_login_today", 0).getBoolean(date, false);
	}

	protected void loginSuccessCallback(MobUser user, int type, String typeName) {
		SDKMenuManager.getInstance(null).hideMenuView();
		if (!isLoginToday(getContext())) {
			SDKMenuManager.getInstance(null).updateMenuViewLoginToday();
		}
		if (GamaterSDK.getInstance().getAcGameSDKListener() != null && user != null) {
			GamaterSDK.getInstance().getAcGameSDKListener().didLoginSuccess(user);
		}
		setLoginToday(getContext());
	}
}
