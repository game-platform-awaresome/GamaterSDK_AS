package com.gamater.sdk.game;

import java.util.Arrays;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.LikeView;
import com.gamater.account.MobUserManager;
import com.gamater.common.Config;
import com.gamater.common.GoogleGameLoginHelper;
import com.gamater.common.ThirdLoginHelper;
import com.gamater.payment.GamaterIAB;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.sdk.fragments.MVBaseFragment;
import com.gamater.sdk.fragments.MVWebFragment;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.gamater.sdk.permission.PermissionManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MVMainActivity extends FragmentActivity {
	public static final String WIN_TYPE = "winType";
	private GamaterSDK GamaterSDK;
	public View rootView;
	private MobUserManager mobUserManager;
	private boolean isCanGoBack;

	private String winType;
//	private String webUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (Config.isShowLog) {
			LogUtil.printHTTP("MVMainActivity onCreate");
		}
		isCanGoBack = ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_CLOSE);
		try {
			GamaterIAB.getInstance().checkIabSetup();
			if (!FacebookSdk.isInitialized()) {
				JSONObject config = MobUserManager.getInstance().getConfigJson(this);
				ConfigUtil.initConfigWithConfigJson(config);
			}
			FacebookSdk.sdkInitialize(getApplicationContext());

			Intent intent = getIntent();
			winType = intent.getStringExtra(WIN_TYPE);
			if (!WinType.Services.toString().equalsIgnoreCase(winType) && !WinType.Web.toString().equalsIgnoreCase(winType)) {
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			setContentView(ResourceUtil.getLayoutId("vsgm_tony_activity_base"));

			rootView = findViewById(ResourceUtil.getId("content"));
			GamaterSDK = GamaterSDK.getInstance();

			mobUserManager = MobUserManager.getInstance();
			mobUserManager.setShowLog(GamaterSDK.isShowLog());

			if (mobUserManager.getServiceHost().equalsIgnoreCase("") || mobUserManager.getConfigJson(this) == null) {
				mobUserManager.requestUrls();
			}

			Fragment f;
			if (winType != null) {
				if (winType.equalsIgnoreCase(WinType.FacebookLike.toString())) {
					LikeView likeView = FacebookHelper.getInstance().getLikeView();
					if (likeView == null) {
						finish();
						return;
					}
					likeView.setActivity(this);
					FacebookHelper.getInstance().newCallbackManager();
					likeView.toggleLike();
					return;
				} else if (winType.equalsIgnoreCase(WinType.Web.toString())) {
//					webUrl = intent.getStringExtra("URL");
					f = new MVWebFragment();
				} else if (winType.equalsIgnoreCase(WinType.FBShare.toString())) {
					String shareDataStr = getIntent().getStringExtra("shareData");
					FacebookHelper.getInstance().share2Fb(this, shareDataStr);
					return;
				} else if (winType.equalsIgnoreCase(WinType.FbLogin.toString())) {
					LoginManager.getInstance()
							.setLoginBehavior(LoginBehavior.SSO_WITH_FALLBACK)
							.logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
					return;
				} else if (winType.equalsIgnoreCase(WinType.GoogleGame.toString())) {
					if (!GoogleGameLoginHelper.getInstance().startResolutionForResult(this, 6)) {
						finish();
					}
					return;
				} else if (winType.equalsIgnoreCase(WinType.FbInviteFriend.toString())) {
					GameRequestDialog requestDialog = new GameRequestDialog(this);
					requestDialog.registerCallback(FacebookHelper.getInstance().newCallbackManager(),
							new FacebookCallback<GameRequestDialog.Result>() {
								public void onSuccess(GameRequestDialog.Result result) {
									FacebookHelper.getInstance().callInviteCallback();
									Toast.makeText(getBaseContext(), ResourceUtil.getStringId("vsgm_tony_invited"), Toast.LENGTH_SHORT).show();
								}

								public void onCancel() {
								}

								public void onError(FacebookException error) {
									Toast.makeText(getBaseContext(), ResourceUtil.getStringId("vsgm_tony_invited_failed"), Toast.LENGTH_SHORT).show();
								}
							});
					String ids = getIntent().getStringExtra("friend_ids");
					String names = getIntent().getStringExtra("friend_names");
					FacebookHelper.getInstance().setInviteIds(ids);
					FacebookHelper.getInstance().setInviteFnames(names);
					String msg = mobUserManager.getFb_copywriter();
					if (msg == null || msg.length() == 0) {
						msg = getString(ResourceUtil.getStringId("vsgm_tony_friend_invite"));
					}
					GameRequestContent content = new GameRequestContent.Builder().setMessage(msg).setTo(ids).build();
					requestDialog.show(content);
					return;
				} else if (winType.equalsIgnoreCase(WinType.Permission.toString())) {
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					if (savedInstanceState == null) {
						PermissionManager.getInstance(this).checkRequestPermissionRationale(this);
					}
					return;
				} else if (winType.equalsIgnoreCase(WinType.GoogleLogin.toString())) {
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					ThirdLoginHelper.getInstance().googleLoginInNewActivity(this, 9);
					return;
				} else {
					finish();
					return;
				}
			} else {
				finish();
				return;
			}
			registerReceiver();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(ResourceUtil.getId("content"), f);
			ft.commit();

		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		PermissionManager.getInstance(this).checkRequestPermissionRationale(
				this);
	}

	@SuppressLint("Override")
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		LogUtil.printHTTP("onRequestPermissionsResult");
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		PermissionManager.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
		finish();
	}

	private LoginFinishReceiver mLoginReceiver;

	private void registerReceiver() {
		if (mLoginReceiver == null)
			mLoginReceiver = new LoginFinishReceiver();
		IntentFilter filter = new IntentFilter("com.gamater.LoginFinish");
		registerReceiver(mLoginReceiver, filter);
	}

	private void unreisgerReceiver() {
		if (mLoginReceiver != null)
			unregisterReceiver(mLoginReceiver);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (FacebookSdk.isInitialized() && FacebookSdk.getApplicationId() != null) {
			AppEventsLogger.activateApp(this, FacebookSdk.getApplicationId());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (FacebookHelper.getInstance().getCallbackManager() != null) {
			if (FacebookHelper.getInstance().getCallbackManager().onActivityResult(requestCode, resultCode, data)) {
				finish();
			}
		}
		if (requestCode == 9) {
			ThirdLoginHelper.getInstance().googleLoginResult(resultCode, data);
			finish();
			return;
		}
		if (requestCode == 6) {
			if (resultCode == Activity.RESULT_OK) {
				GoogleGameLoginHelper.getInstance().checkGoogleConnection();
			}
			LogUtil.printHTTP("resultCode == " + resultCode);
			finish();
			return;
		}
		if (getSupportFragmentManager() == null)
			return;
		if (getSupportFragmentManager().getFragments() == null)
			return;
		int size = getSupportFragmentManager().getFragments().size();
		while (size > 0) {
			Fragment f = getSupportFragmentManager().getFragments().get(--size);
			if (f != null && f instanceof MVBaseFragment) {
				f.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (FacebookSdk.isInitialized() && FacebookSdk.getApplicationId() != null) {
			AppEventsLogger.deactivateApp(this);
		}
	}

	@Override
	public void onDestroy() {
		try {
			unreisgerReceiver();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		if (paramMotionEvent.getAction() == 0) {
			View localView = getCurrentFocus();
			if (shouldHandleEvent(paramMotionEvent)) {
				InputMethodManager localInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if ((localView != null) && (localInputMethodManager != null)) {
					localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(paramMotionEvent);
		}
		if (getWindow().superDispatchTouchEvent(paramMotionEvent)) {
			return true;
		}
		return onTouchEvent(paramMotionEvent);
	}

	public boolean shouldHandleEvent(MotionEvent paramMotionEvent) {
		try {
			if (getSupportFragmentManager().getFragments().size() > 0) {
				for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
					MVBaseFragment f = (MVBaseFragment) getSupportFragmentManager().getFragments()
							.get(getSupportFragmentManager().getFragments().size() - 1 - i);
					if (f == null)
						continue;
					return f.shouldHandleEvent(paramMotionEvent);
				}
			}
		} catch (Exception localException) {
            localException.printStackTrace();
		}
		return false;
	}

	public class LoginFinishReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			MVMainActivity.this.finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
			int size = getSupportFragmentManager().getFragments().size();
			while (size > 0) {
				Fragment fragment = getSupportFragmentManager().getFragments().get(--size);
				if (fragment instanceof MVBaseFragment) {
					if (((MVBaseFragment) fragment).onBackPressed()) {
						return;
					}
				}
			}
		}
		if (isCanGoBack) {
			super.onBackPressed();
		}
	}
}
