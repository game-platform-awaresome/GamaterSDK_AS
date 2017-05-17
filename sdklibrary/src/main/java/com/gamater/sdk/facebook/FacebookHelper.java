package com.gamater.sdk.facebook;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;
import com.facebook.share.widget.ShareDialog.Mode;
import com.gamater.common.Config;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.LogUtil;
import com.vsgm.sdk.SDKActivity;

public class FacebookHelper {
	private static FacebookHelper mInstance;

	private CallbackManager mCallbackManager;

	private CallbackManager mLoginCallbackManager;

	private FbInviteCallback inviteCallback;

	private LikeView likeView;

	private String ids;
	private String[] names;
	private String fnames;

	public void callInviteCallback() {
		if (inviteCallback != null) {
			inviteCallback.onInviteFinish(ids);
			inviteCallback.onInviteFinish(names);
		}
	}

	public void setInviteNames(String[] name) {
		this.names = name;
	}

	public void setInviteIds(String ids) {
		this.ids = ids;
	}

	public String getInviteIds() {
		return this.ids;
	}

	public void setInviteFnames(String fnames) {
		this.fnames = fnames;
	}

	public String getInviteFnames() {
		return this.fnames;
	}

	public FbInviteCallback getInviteCallback() {
		return inviteCallback;
	}

	public void setInviteCallback(FbInviteCallback inviteCallback) {
		this.inviteCallback = inviteCallback;
	}

	public void setLikeView(LikeView likeView) {
		this.likeView = likeView;
	}

	public LikeView getLikeView() {
		return this.likeView;
	}

	private FacebookHelper() {
	}

	public CallbackManager newLoginCallbackManager() {
		mLoginCallbackManager = CallbackManager.Factory.create();
		return mLoginCallbackManager;
	}

	public CallbackManager getLoginCallbackManager() {
		return mLoginCallbackManager;
	}

	public static FacebookHelper getInstance() {
		if (mInstance == null) {
			mInstance = new FacebookHelper();
		}
		return mInstance;
	}

	public CallbackManager newCallbackManager() {
		mCallbackManager = CallbackManager.Factory.create();
		return mCallbackManager;
	}

	public CallbackManager getCallbackManager() {
		return mCallbackManager;
	}

	public void getFacebookFriends(final FbFriendsCallback callback) {
		GraphRequest
				.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
					@Override
					public void onCompleted(JSONArray objects, GraphResponse response) {
						if (callback != null)
							callback.onGetFriends(objects);
					}
				}).executeAsync();
	}

	public void share2Fb(Activity activity, String shareDataStr) {
		try {
			JSONObject o = new JSONObject(shareDataStr);
			share2Fb(activity, o);
		} catch (Exception e) {
		}
	}

	private FbShareCallback mShareCallback;

	public void share2Fb(Activity activity, JSONObject shareData) {
		String linkUrl = shareData.optString("link");
		String caption = shareData.optString("caption");
		String description = shareData.optString("description");
		String pictureURL = shareData.optString("picture");
		LogUtil.printLog("share2Fb linkUrl:" + linkUrl + " /caption:" + caption + " /description:" + description
				+ " /pictureURL:" + pictureURL);
		try {
			ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(linkUrl))
					.setContentDescription(description).setContentTitle(caption)
					.setImageUrl((pictureURL != null && pictureURL.length() > 0) ? Uri.parse(pictureURL) : null)
					.build();
			ShareDialog dialog = new ShareDialog(activity);
			mCallbackManager = CallbackManager.Factory.create();
			dialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
				@Override
				public void onSuccess(Sharer.Result result) {
					if (mShareCallback != null)
						mShareCallback.onShareFinish(null, result.getPostId());
				}

				@Override
				public void onError(FacebookException e) {
					if (mShareCallback != null)
						mShareCallback.onShareFinish(e, null);
				}

				@Override
				public void onCancel() {
					if (mShareCallback != null)
						mShareCallback.onShareCancel();
				}
			});
			dialog.show(content, Mode.FEED);
		} catch (Exception e) {
		}
	}

	public void share2Fb(Context ctx, String linkUrl, String caption, String description, String pictureURL,
			final FbShareCallback callback) {
		mShareCallback = callback;
		JSONObject o = new JSONObject();
		try {
			o.put("link", "" + linkUrl);
			o.put("caption", "" + caption);
			o.put("description", "" + description);
			o.put("picture", "" + pictureURL);
		} catch (Exception e) {
		}
		Intent i = new Intent(ctx, Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
		i.putExtra(MVMainActivity.WIN_TYPE, WinType.FBShare.toString());
		i.putExtra("shareData", o.toString());
		ctx.startActivity(i);
	}

	public void fbLogin(FacebookCallback<LoginResult> callback) {
		LoginManager.getInstance().registerCallback(FacebookHelper.getInstance().newCallbackManager(), callback);
		Intent intent = new Intent(GamaterSDK.getInstance().getActivity(),
				Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
		intent.putExtra(MVMainActivity.WIN_TYPE, WinType.FbLogin.toString());
		GamaterSDK.getInstance().getActivity().startActivity(intent);
	}
}
