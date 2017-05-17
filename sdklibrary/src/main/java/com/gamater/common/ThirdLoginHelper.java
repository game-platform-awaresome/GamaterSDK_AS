package com.gamater.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.AppUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vsgm.sdk.SDKActivity;

public class ThirdLoginHelper {
	private static ThirdLoginHelper instance;
	private GoogleLoginCallback googleLoginCallback;
	private GoogleApiClient mGoogleApiClient;

	private ThirdLoginHelper() {
	}

	public static ThirdLoginHelper getInstance() {
		if (instance == null)
			instance = new ThirdLoginHelper();
		return instance;
	}

	public void fbLogin(FacebookCallback<LoginResult> callback) {
		FacebookHelper.getInstance().fbLogin(callback);
	}

	public GoogleLoginCallback getGoogleLoginCallback() {
		return googleLoginCallback;
	}

	/**
	 * google game登录
	 * 
	 * @param callback
	 */
	@SuppressWarnings("deprecation")
	public void gpLogin(Context ctx, GoogleLoginCallback callback) {
		int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
		if (available != ConnectionResult.SUCCESS) {
			Toast.makeText(ctx, "Google Play Services is not available.", Toast.LENGTH_SHORT).show();
			if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
				GoogleLoginCallback cl = ThirdLoginHelper.getInstance().getGoogleLoginCallback();
				if (cl != null) {
					cl.onFail(available);
					GooglePlayServicesUtil.getErrorDialog(available, SdkDialogViewManager.getOwnerActivity(), 0,
							new OnCancelListener() {
								@Override
								public void onCancel(DialogInterface arg0) {

								}
							}).show();
				}
				return;
			}
			GoogleLoginCallback cl = ThirdLoginHelper.getInstance().getGoogleLoginCallback();
			if (cl != null) {
				cl.onFail();
			}
			return;
		}
		googleLoginCallback = callback;

//		String googleClientId = ctx.getResources().getString(ResourceUtil.getStringId("vsgm_google_client_id"));
		String googleClientId = AppUtil.GetMetaDataString(GamaterSDK.getInstance().getContext(), "gamater_google_client_id");
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(googleClientId)
				.requestEmail().build();
		mGoogleApiClient = new GoogleApiClient.Builder(ctx).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
		Intent intent = new Intent(ctx, Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
		intent.putExtra(MVMainActivity.WIN_TYPE, WinType.GoogleLogin.toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	public void googleLoginInNewActivity(Activity activity, int requestCode) {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		activity.startActivityForResult(signInIntent, requestCode);
	}

	public void googleLoginResult(int resultCode, Intent data) {
		GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
		if (resultCode == Activity.RESULT_OK && result.isSuccess()) {
			GoogleSignInAccount acct = result.getSignInAccount();
			if (googleLoginCallback != null) {
				googleLoginCallback.onSuccess(acct.getIdToken(), acct.getEmail());
			}
		} else {
			if (googleLoginCallback != null) {
				googleLoginCallback.onFail();
			}
		}
	}

	public static interface GoogleLoginCallback {
		void onFail();

		void onFail(int available);

		void onSuccess(String token, String email);
	}
}
