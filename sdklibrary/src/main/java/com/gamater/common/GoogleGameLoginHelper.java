package com.gamater.common;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.widget.Toast;

import com.gamater.sdk.common.WinType;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.LogUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.vsgm.permission.PermissionCallback;
import com.vsgm.permission.PermissionManager;
import com.vsgm.sdk.SDKActivity;

/**
 * Google游戏登录相关操作类
 * 
 * @author Stephen
 * 
 */
public class GoogleGameLoginHelper {

	public static final int GOOGLE_SIGN_IN_CODE = 0x91;
	public static final int GOOGLE_GAME_ACHIEVE_CODE = 0x92;
	public static final int GOOGLE_GAME_LEADERBOARDS_CODE = 0x93;

	private static GoogleGameLoginHelper instance;

	private GoogleApiClient mGoogleGameApiClient;
	private ConnectionResult mConnectionResult;

	private GoogleGameLoginHelper() {
	}

	/**
	 * 创建单例
	 * 
	 * @return
	 */
	public static GoogleGameLoginHelper getInstance() {
		if (instance == null)
			instance = new GoogleGameLoginHelper();
		return instance;
	}

	public void loginGoogleGame(Activity activity,
			GoogleGameLoginCallback callback) {
		if (mGoogleGameApiClient == null || !mGoogleGameApiClient.isConnected()) {
			gameLoginCalback = callback;
			googleLogin(activity);
		}
	}

	/**
	 * 登录Google操作
	 */
	private void googleLogin(final Activity activity) {
		int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (available != ConnectionResult.SUCCESS) {
			Toast.makeText(activity, "Google Play Services is not available.", Toast.LENGTH_SHORT).show();
			if (gameLoginCalback != null) {
				gameLoginCalback.onFail();
			}
			return;
		}

		PermissionManager.getInstance(activity).request(
				new String[] { "android.permission.GET_ACCOUNTS" },
				new PermissionCallback() {
					@Override
					public void onGranted() {
						if (mConnectionResult == null || mGoogleGameApiClient == null) {
							initGoogleLogin(activity);
							mGoogleGameApiClient.connect();
							return;
						}
						try {
							mConnectionResult.startResolutionForResult(activity, 1);
						} catch (SendIntentException e) {
							mGoogleGameApiClient.connect();
						}
					}
					@Override
					public void onDenied(List<String> permissions) {
					}
				});
	}

	public interface GoogleGameLoginCallback {
		void onSuccess();

		void onFail();
	}

	private GoogleGameLoginCallback gameLoginCalback;

	/**
	 * 初始化Google登录
	 */
	private void initGoogleLogin(final Activity activity) {
		mGoogleGameApiClient = new GoogleApiClient.Builder(activity,
				new ConnectionCallbacks() {
					@Override
					public void onConnected(Bundle arg0) {
						if (gameLoginCalback != null) {
							gameLoginCalback.onSuccess();
						}
					}

					@Override
					public void onConnectionSuspended(int cause) {
						LogUtil.printHTTP("onConnectionSuspended() called. Trying to reconnect.");
						mGoogleGameApiClient.connect();
					}
				}, new OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(ConnectionResult result) {
						LogUtil.printHTTP("onConnectionFailed() called, result: " + result.getErrorCode());
						mConnectionResult = result;
						if (result.hasResolution()) {
							LogUtil.printHTTP("google onConnectionFailed hasResolution");
							Intent intent = new Intent(activity,
									Config.isGmLogo ? SDKActivity.class
											: MVMainActivity.class);
							intent.putExtra(MVMainActivity.WIN_TYPE,
									WinType.GoogleGame.toString());
							GamaterSDK.getInstance().getActivity()
									.startActivity(intent);
						} else {
							LogUtil.printHTTP("google onConnectionFailed");
							if (gameLoginCalback != null) {
								gameLoginCalback.onFail();
							}
						}
					}
				})
				// .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES).build();
	}

	public boolean startResolutionForResult(Activity activity, int requestCode) {
		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				LogUtil.printHTTP("google onConnectionFailed hasResolution");
				mConnectionResult.startResolutionForResult(activity, requestCode);
				return true;
			} catch (SendIntentException e) {
				e.printStackTrace();
				mGoogleGameApiClient.connect();
				return false;
			}
		}
		return false;
	}

	/**
	 * 启动成就版页面
	 * 
	 * @param activity
	 */
	public void startAchievementView(Activity activity) {
		activity.startActivityForResult(
				Games.Achievements.getAchievementsIntent(mGoogleGameApiClient), GOOGLE_GAME_ACHIEVE_CODE);

	}

	/**
	 * 启动排行榜页面
	 */
	public void startLeaderboards(Activity activity) {
		activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleGameApiClient), GOOGLE_GAME_LEADERBOARDS_CODE);
	}

	/**
	 * 增加玩家成就数量
	 * 
	 * @param achieveId
	 * @param incrementNum
	 */
	public void setAchievements(String achieveId, int incrementNum) {
		if (mGoogleGameApiClient != null && mGoogleGameApiClient.isConnected()) {
			Games.Achievements.unlockImmediate(mGoogleGameApiClient, achieveId);
			Games.Achievements.increment(mGoogleGameApiClient, achieveId, incrementNum);
		}

	}

	/**
	 * 提交玩家排行榜分数
	 * 
	 * @param leaderboardId
	 * @param score
	 */
	public void submitLeaderboardScore(String leaderboardId, int score) {
		if (mGoogleGameApiClient != null && mGoogleGameApiClient.isConnected()) {
			Games.Leaderboards.submitScore(mGoogleGameApiClient, leaderboardId, score);
		}
	}

	/**
	 * 返回是否已连上Google Service
	 * 
	 * @return
	 */
	public boolean isGoogleServiceConnected() {
		if (mGoogleGameApiClient != null && mGoogleGameApiClient.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 断开与Google Service连接
	 */
	public void logout() {
		if (mGoogleGameApiClient != null && mGoogleGameApiClient.isConnected()) {
			mGoogleGameApiClient.disconnect();
		}
		mGoogleGameApiClient = null;
	}

	public void checkGoogleConnection() {
		if (!mGoogleGameApiClient.isConnected() && !mGoogleGameApiClient.isConnecting()) {
			mGoogleGameApiClient.connect();
		}
	}
}
