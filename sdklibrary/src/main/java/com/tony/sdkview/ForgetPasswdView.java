package com.tony.sdkview;

import org.json.JSONException;
import org.json.JSONObject;

import com.gamater.account.http.SdkHttpRequest;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.define.SPUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ForgetPasswdView extends BaseLinearLayout {

	public ForgetPasswdView() {
		super(GamaterSDK.getInstance().getActivity());
	}

	public ForgetPasswdView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ForgetPasswdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ForgetPasswdView(Context context) {
		super(context);
	}

	private EditText accountEdit;

	@Override
	public void initView() {
		TextView mesText = (TextView) findViewById(ResourceUtil.getId("text_mes"));
		mesText.setText(String.format(getResources().getString(ResourceUtil.getStringId("vsgm_find_pwd_message")),
				Config.gmTitle));
		accountEdit = (EditText) findViewById(ResourceUtil.getId("account_edit"));
		final TextView btnForgetPw = (TextView) findViewById(ResourceUtil.getId("btnForgetPw"));
		btnForgetPw.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				String account = accountEdit.getText().toString().trim();
				if (account == null || account.length() == 0) {
					showError(accountEdit, accountEdit.getHint().toString());
					return;
				} else if (!checkEmail(account)) {
					showError(accountEdit, findStringId("okgame_email_format_error"));
					return;
				}
				long lastTime = SPUtil.getLastGetBackTime(getContext(), account);
				if (lastTime > 0) {
					if (System.currentTimeMillis() - lastTime < (1000 * 60 * 60)) {
						showError(findStringId("okgame_forget_pw_tips"));
						return;
					}
				}
				JSONObject data = new JSONObject();
				try {
					data.put("id", 2);
					data.put("email", account);
				} catch (JSONException e) {
				}
				hideSoftInput();
				requestApi(SdkHttpRequest.forgetPasswd(account));
			}
		});
		JSONObject data = new JSONObject();
		try {
			data.put("id", 1);
		} catch (JSONException e) {
		}
	}

	public static ForgetPasswdView createView(Context ctx) {
		if (ctx == null)
			return null;
		ForgetPasswdView view = (ForgetPasswdView) LayoutInflater.from(ctx)
				.inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_view_forget_pw"), null);
		view.initView();
		return view;
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		super.requestDidSuccess(httpRequest, result);
		try {
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 1) {
				String email = accountEdit.getText().toString();
				SPUtil.setLastGetBackTime(getContext(), email, System.currentTimeMillis());
				SpannableString s = new SpannableString(getContext()
						.getString(ResourceUtil.getStringId("vsgm_to_email"), accountEdit.getText().toString()));
				int index = s.toString().indexOf(email);
				s.setSpan(new ForegroundColorSpan(Color.GREEN), index, index + email.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				startView(SdkMessageView.createView(getContext(), null, s));
				accountEdit.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getViewTitle() {
		return getContext().getString(ResourceUtil.getStringId("vsgm_find_pwd"));
	}

}
