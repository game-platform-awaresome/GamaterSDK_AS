package com.tony.facebook;

import java.util.List;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.share.widget.LikeView;
import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;
import com.vsgm.sdk.SDKActivity;

public class FbLikeView extends LinearLayout implements HttpEventListener {

	public FbLikeView(Context context) {
		super(context);
	}

	public FbLikeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FbLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private ListView rewardList;
	private RewardListAdapter listAdapter;
	private LikeView likeView;

	public void initView() {
		FacebookSdk.setIsDebugEnabled(true);
		FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
		FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
		FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);

		rewardList = (ListView) findViewById(ResourceUtil
				.getId("like_reward_list"));
		listAdapter = new RewardListAdapter();
		rewardList.setAdapter(listAdapter);

		likeView = (LikeView) findViewById(ResourceUtil.getId("btn_facebook_like_like"));
		likeView.setLikeViewStyle(LikeView.Style.BUTTON);
		FacebookHelper.getInstance().setLikeView(likeView);
		likeView.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				SdkFacebookDialog.checkFbLogin(new Runnable() {
					@Override
					public void run() {
						// new SdkGameWebViewDialog(getContext(),WebViewType.Facebook).show();
						Intent intent = new Intent(getContext(),Config.isGmLogo ? SDKActivity.class: MVMainActivity.class);
						intent.putExtra("URL", MobUserManager.getInstance().getFbUrl());
						intent.putExtra(MVMainActivity.WIN_TYPE, WinType.Web.toString());
						getContext().startActivity(intent);
					}
				});
			}
		});

		TextView descBtn = (TextView) findViewById(ResourceUtil
				.getId("btn_facebook_like_desc"));
		descBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		descBtn.getPaint().setAntiAlias(true);
		descBtn.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				if (listAdapter.getCount() == 0)
					return;
				new ActivitiesDescriptionDialog(getContext(), listAdapter
						.getItem(0).detail).show();

			}
		});
	}

	private OKgameDialogProcess processDialog;

	private void reportData(FacebookData data) {
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
		request.addPostValue("type", "1");
		if (data != null)
			request.addPostValue("id", data.id);
		request.addPostValue("data", "" + System.currentTimeMillis() / 1000);
		request.setHttpEventListener(this);
		request.setTag(data);
		request.asyncStart();
	}

	public static FbLikeView createView(Context ctx) {
		if (ctx == null)
			return null;
		FbLikeView view = (FbLikeView) LayoutInflater.from(ctx).inflate(
				ResourceUtil.getLayoutId("vsgm_tony_sdk_facebook_like"), null);
		view.initView();
		return view;
	}

	public void notifyDataSetChanged() {
		if (listAdapter != null)
			listAdapter.notifyDataSetChanged();
	}

	class RewardListAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public RewardListAdapter() {
			inflater = LayoutInflater.from(getContext());
		}

		@Override
		public int getCount() {
			List<FacebookData> data = DataHelper.getInstance().getLikeArray();
			if (data == null)
				return 0;
			else
				return data.size();
		}

		@Override
		public FacebookData getItem(int position) {
			List<FacebookData> data = DataHelper.getInstance().getLikeArray();
			if (data == null)
				return null;
			else
				return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(ResourceUtil
						.getLayoutId("vsgm_tony_sdk_facebook_like_list_item"),
						null, false);
			}
			if (convertView.getTag() == null) {
				convertView.setTag(new ViewHolder(convertView));
			}
			boolean isFacebookLogin = AccessToken.getCurrentAccessToken() != null;

			ViewHolder holder = getHolder(convertView);
			FacebookData item = getItem(position);
			holder.name.setText(item.name);
			holder.description.setText(item.description);
			holder.rewardBtn.setVisibility(View.GONE);
			holder.finishImage.setText("");
			holder.resultText.setText("");
			if (isFacebookLogin) {
				if (item.has_send_gifts == 1) {
					holder.finishImage.setBackgroundResource(ResourceUtil
							.getDrawableId("vsgm_tony_icon_like_succeed"));
					holder.finishImage.setText(ResourceUtil
							.getStringId("vsgm_tony_gift_reached"));
				} else if (item.has_been_reached == 1) {
					holder.finishImage.setBackgroundDrawable(null);
					holder.rewardBtn.setVisibility(View.VISIBLE);
					holder.rewardBtn.setTag(position);
					holder.rewardBtn.setOnClickListener(new BaseOnClickListener() {
						@Override
								public void onBaseClick(View v) {
							int p = (Integer) v.getTag();
							reportData(getItem(p));
						}
					});
				} else {
					holder.finishImage.setBackgroundDrawable(null);
					String text = item.has_number + "/" + item.number;
					SpannableString str = new SpannableString(text);
					str.setSpan(
							new ForegroundColorSpan(Color.parseColor("#fa4f2d")),
							0, ("" + item.has_number).length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.resultText.setText(str);
				}
			} else {
				holder.finishImage.setBackgroundResource(ResourceUtil
						.getDrawableId("vsgm_tony_icon_like_unsucceed"));
				holder.finishImage.setText(ResourceUtil
						.getStringId("vsgm_tony_gift_unreached"));
			}

			return convertView;
		}

		ViewHolder getHolder(View v) {
			ViewHolder holder;
			if (v.getTag() == null) {
				holder = new ViewHolder(v);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			return holder;
		}

		class ViewHolder {
			public ViewHolder(View v) {
				name = (TextView) v.findViewById(ResourceUtil
						.getId("like_count_text"));
				description = (TextView) v.findViewById(ResourceUtil
						.getId("like_reward_text"));
				finishImage = (TextView) v.findViewById(ResourceUtil
						.getId("like_result_image_text"));
				rewardBtn = (TextView) v.findViewById(ResourceUtil
						.getId("like_reward_btn"));
				resultText = (TextView) v.findViewById(ResourceUtil
						.getId("like_result_text"));
			}

			TextView name;
			TextView description;
			TextView finishImage;
			TextView resultText;
			TextView rewardBtn;
		}
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		if (processDialog.isShowing())
			processDialog.dismiss();
		try {
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 1) {
				FacebookData fd = (FacebookData) httpRequest.getTag();
				fd.has_send_gifts = 1;
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
