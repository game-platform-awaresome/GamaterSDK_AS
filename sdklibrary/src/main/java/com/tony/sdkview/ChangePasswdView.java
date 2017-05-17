package com.tony.sdkview;

import org.json.JSONException;
import org.json.JSONObject;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.common.http.HttpRequest;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswdView extends BaseLinearLayout {

	public ChangePasswdView() {
		super(GamaterSDK.getInstance().getActivity());
	}

	public ChangePasswdView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ChangePasswdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChangePasswdView(Context context) {
		super(context);
	}

	private EditText oldPasswdEdit;
	private EditText newPasswdEdit;
	private EditText commitPasswdEdit;

	@Override
	public void initView() {
		oldPasswdEdit = (EditText) findViewById(findId("old_passwd_edit"));
		oldPasswdEdit.setTypeface(Typeface.DEFAULT);
		newPasswdEdit = (EditText) findViewById(findId("new_passwd_edit"));
		newPasswdEdit.setTypeface(Typeface.DEFAULT);
		commitPasswdEdit = (EditText) findViewById(findId("commit_passwd_edit"));
		commitPasswdEdit.setTypeface(Typeface.DEFAULT);
		TextView btnChangePw = (TextView) findViewById(ResourceUtil
				.getId("btnChangePw"));
		btnChangePw.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				String oldP = oldPasswdEdit.getText().toString().trim();
				int oldCheck = checkPasswd(oldP);
				if (oldCheck < 1) {
					showError(
							oldPasswdEdit,
							oldCheck == 0 ? oldPasswdEdit.getHint().toString()
									: getResources()
											.getString(
													findStringId("vsgm_tony_common_passwd_length_err")));
					return;
				}
				String newP = newPasswdEdit.getText().toString().trim();
				int newCheck = checkPasswd(newP);
				if (newCheck < 1) {
					showError(
							newPasswdEdit,
							newCheck == 0 ? newPasswdEdit.getHint().toString()
									: getResources()
											.getString(
													findStringId("vsgm_tony_common_passwd_length_err")));
					return;
				}
				String commitP = commitPasswdEdit.getText().toString().trim();
				int commitCheck = checkPasswd(commitP);
				if (commitCheck < 1) {
					showError(
							commitPasswdEdit,
							commitCheck == 0 ? commitPasswdEdit.getHint()
									.toString()
									: getResources()
											.getString(
													findStringId("vsgm_tony_common_passwd_length_err")));
					return;
				}
				if (!newP.equals(commitP)) {
					showError(commitPasswdEdit,
							findStringId("vsgm_tony_change_dlg_disagree"));
					return;
				}
				requestApi(SdkHttpRequest.changePasswd(oldP, newP));
			}
		});
	}

	private int checkPasswd(String passwd) {
		if (passwd == null || passwd.length() == 0) {
			return 0;
		}
		if (passwd.length() < 6) {
			return -1;
		}
		return 1;
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		super.requestDidSuccess(httpRequest, result);
		MobUser user = new MobUser(result);
		if (user.getStatus() == 1) {
			MobUserManager um = MobUserManager.getInstance();
			MobUser currentUser = um.getCurrentUser();
			if (currentUser != null) {
				try {
					JSONObject obj = new JSONObject(result);
					JSONObject data = obj.getJSONObject("data");
					data.put("email", currentUser.getEmail());
					obj.put("data", data);

					um.saveAccount(user.getUserid(), obj.toString());
					currentUser.setToken(user.getToken());
					um.setCurrentUser(currentUser);
				} catch (JSONException e) {
				}
			} else {
				um.saveAccount(user.getUserid(), result);
				um.setCurrentUser(user);
			}

			oldPasswdEdit.setText("");
			newPasswdEdit.setText("");
			commitPasswdEdit.setText("");

			showError(findStringId("vsgm_tony_change_dlg_success"));
		}
	}

	public static ChangePasswdView createView(Context ctx) {
		if (ctx == null)
			return null;
		ChangePasswdView view = (ChangePasswdView) LayoutInflater
				.from(ctx)
				.inflate(
						ResourceUtil
								.getLayoutId("vsgm_tony_sdk_view_change_pw"),
						null);
		view.initView();
		return view;
	}

	@Override
	public String getViewTitle() {
		return getContext().getString(
				ResourceUtil.getStringId("vsgm_tony_account_changepassword"));
	}
}
