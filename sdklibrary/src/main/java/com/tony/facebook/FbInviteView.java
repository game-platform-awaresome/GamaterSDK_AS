package com.tony.facebook;

import java.util.List;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.sdk.facebook.FacebookHelper;
import com.gamater.sdk.facebook.FbInviteCallback;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class FbInviteView extends LinearLayout implements HttpEventListener {

	public FbInviteView(Context context) {
		super(context);
	}

	public FbInviteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FbInviteView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private ListView rewardList;
	private RewardListAdapter listAdapter;

	public void initView() {
		rewardList = (ListView) findViewById(ResourceUtil.getId("invite_reward_list"));
		listAdapter = new RewardListAdapter();
		rewardList.setAdapter(listAdapter);

		TextView descBtn = (TextView) findViewById(ResourceUtil.getId("btn_facebook_invite_desc"));
		descBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		descBtn.getPaint().setAntiAlias(true);
		descBtn.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				if (listAdapter.getCount() == 0)
					return;
				new ActivitiesDescriptionDialog(getContext(), listAdapter.getItem(0).detail).show();
			}
		});
	}

	private OKgameDialogProcess processDialog;

	private void reportData(Object data) {
		processDialog = new OKgameDialogProcess(getContext(), ResourceUtil.getLayoutId("vsgm_tony_process"));
		processDialog.setCancelable(false);
		SdkHttpRequest request = SdkHttpRequest.postRequest(Config.getLoginHost(), APIs.FB_REPORT);
		request.addPostValue("language", DeviceInfo.getInstance(getContext()).getSystemLanguage());
		request.addPostValue("userid", MobUserManager.getInstance().getCurrentUser().getUserid());
		request.addPostValue("roleId", MobUserManager.getInstance().getCurrentUser().getRoleId());
		request.addPostValue("serverId", MobUserManager.getInstance().getCurrentUser().getServerId());
		request.addPostValue("type", "3");
		if (data != null) {
			if (data instanceof FacebookData) {
				FacebookData d = (FacebookData) data;
				request.addPostValue("id", d.id);
			} else {
				request.addPostValue("data", data.toString());
				request.addPostValue("fbname", FacebookHelper.getInstance().getInviteFnames());
			}
		} else {
		}
		request.setHttpEventListener(this);
		request.setTag(data);
		request.asyncStart();
	}

	private int currentInviteNumber;

	public static FbInviteView createView(Context ctx) {
		if (ctx == null)
			return null;
		FbInviteView view = (FbInviteView) LayoutInflater.from(ctx).inflate(
				ResourceUtil.getLayoutId("vsgm_tony_sdk_facebook_invite"), null);
		view.initView();
		return view;
	}

	private void doInvite() {
		SdkFacebookDialog.checkFbLogin(new Runnable() {
			@Override
			public void run() {
				GamaterSDK.getInstance().facebookFriendsInvite(new FbInviteCallback() {
					@Override
					public void onInviteFinish(String friendIds) {
						LogUtil.printLog("FaceBook InviteFinish friendIds: " + friendIds);
						reportData(friendIds);
					}

					@Override
					public void onInviteFinish(String[] friendNames) {
						LogUtil.printLog("FaceBook InviteFinish friendNames: " + friendNames.toString());
					}

					@Override
					public void onCancel() {

					}
				});
			}
		});
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
			List<FacebookData> data = DataHelper.getInstance().getInviteArray();
			if (data == null)
				return 0;
			else
				return data.size();
		}

		@Override
		public FacebookData getItem(int position) {
			List<FacebookData> data = DataHelper.getInstance().getInviteArray();
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
				convertView = inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_facebook_invite_list_item"),
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
			if (isFacebookLogin) {
				if (item.has_send_gifts == 1) {
					holder.finishImage.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_icon_like_succeed"));
					holder.finishImage.setText(ResourceUtil.getStringId("vsgm_tony_gift_reached"));
				} else {
					boolean isCanReward = item.has_been_reached == 1 || (currentInviteNumber >= item.number);
					if (isCanReward) {
						holder.finishImage.setBackgroundDrawable(null);
						holder.rewardBtn.setVisibility(View.VISIBLE);
						holder.rewardBtn.setTag(position);
						holder.rewardBtn.setText(ResourceUtil.getStringId("vsgm_tony_get_gifts"));
						holder.rewardBtn
								.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_facebook_reward_btn_bg"));
						holder.rewardBtn.setOnClickListener(new BaseOnClickListener() {
							@Override
									public void onBaseClick(View v) {
								reportData(getItem((Integer) v.getTag()));
							}
						});
					} else {
						holder.finishImage.setBackgroundDrawable(null);
						holder.rewardBtn.setVisibility(View.VISIBLE);
						holder.rewardBtn.setTag(position);
						int hasNumber = currentInviteNumber > 0 ? currentInviteNumber : item.has_number;
						String text = getResources().getString(ResourceUtil.getStringId("vsgm_tony_invite")) + " "
								+ hasNumber + "/" + item.number;
						holder.rewardBtn.setText(text);
						holder.rewardBtn
								.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_facebook_invite_btn_bg"));
						holder.rewardBtn.setOnClickListener(new BaseOnClickListener() {
							@Override
									public void onBaseClick(View v) {
								doInvite();
							}
						});
					}
				}
			} else {
				holder.finishImage.setBackgroundDrawable(null);
				holder.rewardBtn.setVisibility(View.VISIBLE);
				holder.rewardBtn.setTag(position);
				holder.rewardBtn.setText(ResourceUtil.getStringId("vsgm_tony_invite"));
				holder.rewardBtn.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_facebook_invite_btn_bg"));
				holder.rewardBtn.setOnClickListener(new BaseOnClickListener() {
					@Override
					public void onBaseClick(View v) {
						doInvite();
					}
				});
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
				name = (TextView) v.findViewById(ResourceUtil.getId("invite_count_text"));
				description = (TextView) v.findViewById(ResourceUtil.getId("invite_reward_text"));
				finishImage = (TextView) v.findViewById(ResourceUtil.getId("invite_result_image_text"));
				rewardBtn = (TextView) v.findViewById(ResourceUtil.getId("invite_reward_btn"));
			}

			TextView name;
			TextView description;
			TextView finishImage;
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
				if (httpRequest.getTag() instanceof FacebookData) {
					FacebookData fd = (FacebookData) httpRequest.getTag();
					fd.has_send_gifts = 1;
				}
				JSONObject number = obj.optJSONObject("data");
				currentInviteNumber = number.optInt("number", 0);
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
