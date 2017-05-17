package com.gamater.dialog;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.dialog.SdkGameDialog.ViewType;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.ResourceUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class SdkAccountDialog extends Dialog implements View.OnClickListener {
	private Activity mActivity;
	private String gwUrl;
	private MobUserManager mobUserManager;

	public SdkAccountDialog(Activity a) {
		super(a, ResourceUtil.getStyleId("vsgm_tony_hide_dialog"));
		this.mActivity = a;
		setCancelable(false);
	}

	protected void onCreate(android.os.Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_account_dialog"), null);
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		TextView accountText = (TextView) findViewById(ResourceUtil.getId("login_account_text"));
		StringBuffer sb = new StringBuffer();
		String sightseer = getContext().getResources()
				.getString(ResourceUtil.getStringId("vsgm_tony_common_sightseer"));
		MobUser user = MobUserManager.getInstance().getCurrentUser();
		if (user.getType().equalsIgnoreCase("0")) {
			sb.append(sightseer);
			sb.append("@");
			sb.append(user.getUserid());
			findViewById(ResourceUtil.getId("btn_account_dialog_change_psw")).setVisibility(View.GONE);
		} else {
			if (user.getEmail() == null || user.getEmail().length() <= 0) {
				sb.append(user.getUserid());
			} else {
				sb.append(user.getEmail());
			}
			findViewById(ResourceUtil.getId("btn_account_dialog_update_account")).setVisibility(View.GONE);
		}
		// accountText.setText(ResourceUtil.getStringId("vsgm_current_login_account")
		// + sb.toString());
		accountText.setText(sb.toString());

		findViewById(ResourceUtil.getId("btn_account_dialog_close")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("btn_account_dialog_update_account")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("btn_account_dialog_change_psw")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("btn_account_dialog_account_info")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("btn_account_dialog_check_payment")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("btn_account_dialog_change_account")).setOnClickListener(this);
		mobUserManager = MobUserManager.getInstance();
		gwUrl = mobUserManager.getGwUrl();
		if (TextUtils.isEmpty(gwUrl)) {
			gwUrl = "http://www." + Config.gmHost + "/";
		}
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (v.getId() == ResourceUtil.getId("btn_account_dialog_close")) {
		} else if (v.getId() == ResourceUtil.getId("btn_account_dialog_update_account")) {
			new SdkGameDialog(GamaterSDK.getInstance().getActivity(), ViewType.UpdateAccount).show();
		} else if (v.getId() == ResourceUtil.getId("btn_account_dialog_change_psw")) {
			new SdkGameDialog(GamaterSDK.getInstance().getActivity(), ViewType.ChangePassword).show();
		} else if (v.getId() == ResourceUtil.getId("btn_account_dialog_account_info")) {
			Intent intent = new Intent(mActivity, MVMainActivity.class);
			intent.putExtra("URL", gwUrl + "account/profile");
			intent.putExtra(MVMainActivity.WIN_TYPE, WinType.Web.toString());
			intent.putExtra("requestToken", true);
			mActivity.startActivity(intent);
		} else if (v.getId() == ResourceUtil.getId("btn_account_dialog_check_payment")) {
			Intent intent = new Intent(mActivity, MVMainActivity.class);
			// intent.putExtra("URL", mobUserManager.getGwUrl() +
			// "/account/recharge-record");
			String url = mobUserManager.getConfigJson(mActivity).optString("recharge_record");
			if (TextUtils.isEmpty(url)) {
				url = gwUrl + "account/recharge-record";
			}
			intent.putExtra("URL", url);
			intent.putExtra(MVMainActivity.WIN_TYPE, WinType.Web.toString());
			intent.putExtra("requestToken", true);
			mActivity.startActivity(intent);
		} else if (v.getId() == ResourceUtil.getId("btn_account_dialog_change_account")) {
			GamaterSDK.getInstance().logout();
		}
	}
}
