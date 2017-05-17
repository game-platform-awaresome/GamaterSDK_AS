package com.tony.facebook;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.sdk.facebook.FbShareCallback;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.squareup.picasso.Picasso;
import com.tony.viewinterface.BaseOnClickListener;

public class FbShareView extends LinearLayout implements FbShareCallback,
		HttpEventListener {
	public FbShareView(Context context) {
		super(context);
	}

	public FbShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FbShareView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private ImageView shareImage;
	private FacebookData facebookData;

	public void initView() {
		shareImage = (ImageView) findViewById(ResourceUtil.getId("image_share"));
		TextView descBtn = (TextView) findViewById(ResourceUtil.getId("btn_facebook_share_desc"));
		descBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		descBtn.getPaint().setAntiAlias(true);
		descBtn.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				if (facebookData == null)
					return;
				new ActivitiesDescriptionDialog(getContext(),
						facebookData.detail).show();
			}
		});
		findViewById(ResourceUtil.getId("btn_facebook_share_share")).setOnClickListener(
				new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						if (facebookData == null)
							return;
						SdkFacebookDialog.checkFbLogin(new Runnable() {
							@Override
							public void run() {
								GamaterSDK.getInstance().shareToFb(
										facebookData.url, facebookData.name,
										facebookData.description,
										facebookData.image, FbShareView.this);
							}
						});
					}
				});
		notifyDataSetChanged();
	}

	private OKgameDialogProcess processDialog;

	private void reportData() {
		processDialog = new OKgameDialogProcess(getContext(),
				ResourceUtil.getLayoutId("vsgm_tony_process"));
		processDialog.setCancelable(false);
		SdkHttpRequest request = SdkHttpRequest.postRequest(
				Config.getLoginHost(), APIs.FB_REPORT);
		request.addPostValue("language", DeviceInfo.getInstance(getContext())
				.getSystemLanguage());
		request.addPostValue("userid", MobUserManager.getInstance()
				.getCurrentUser().getUserid());
		request.addPostValue("roleId", MobUserManager.getInstance()
				.getCurrentUser().getRoleId());
		request.addPostValue("serverId", MobUserManager.getInstance()
				.getCurrentUser().getServerId());
		request.addPostValue("type", "2");
		request.addPostValue("id", facebookData.id);
		request.addPostValue("data", "" + System.currentTimeMillis() / 1000);
		request.setHttpEventListener(this);
		request.asyncStart();
	}

	public void notifyDataSetChanged() {
		facebookData = DataHelper.getInstance().getShareData();
		if (facebookData == null)
			return;
		Picasso.with(getContext()).load(facebookData.image).into(shareImage);
		if (facebookData.has_send_gifts == 1) {
			TextView t = (TextView) findViewById(ResourceUtil.getId("btn_facebook_share_share"));
			t.setText(ResourceUtil.getStringId("vsgm_tony_share_gift_send"));
		}
	}

	public static FbShareView createView(Context ctx) {
		if (ctx == null)
			return null;
		FbShareView view = (FbShareView) LayoutInflater.from(ctx).inflate(
				ResourceUtil.getLayoutId("vsgm_tony_sdk_facebook_share"), null);
		view.initView();
		return view;
	}

	@Override
	public void onShareFinish(Exception e, String postId) {
		if (e == null) {
			reportData();
		} else {
			e.printStackTrace();
		}
	}

	@Override
	public void onShareCancel() {
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		if (processDialog.isShowing())
			processDialog.dismiss();
		try {
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 1) {
				facebookData.has_send_gifts = 1;
				notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		if (!processDialog.isShowing())
			processDialog.show();
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		if (processDialog.isShowing())
			processDialog.dismiss();
	}
}
