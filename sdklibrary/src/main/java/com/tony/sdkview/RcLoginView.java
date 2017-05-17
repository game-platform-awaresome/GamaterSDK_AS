package com.tony.sdkview;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.common.http.HttpRequest;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.view.SdkTipsTextView;
import com.tony.viewinterface.BaseOnClickListener;

public class RcLoginView extends BaseLinearLayout {

	public RcLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public RcLoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RcLoginView(Context context) {
		super(context);
	}

	private EditText accountEdit;
	private EditText passwdEdit;

	@Override
	public void initView() {
		accountEdit = (EditText) findViewById(findId("account_edit"));
		accountEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean isFocus) {
				if (isShown() && !isFocus) {
					String str = accountEdit.getText().toString().trim();
					checkAccount(str);
				}
			}
		});
		passwdEdit = (EditText) findViewById(findId("passwd_edit"));
		passwdEdit.setTypeface(Typeface.DEFAULT);
		final SdkTipsTextView btnLogin = (SdkTipsTextView) findViewById(ResourceUtil.getId("btnLogin"));
		btnLogin.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				String email = accountEdit.getText().toString().trim();
				if (!checkAccount(email)) {
					return;
				}
				String passwd = passwdEdit.getText().toString().trim();
				if (!checkPasswd(passwd)) {
					return;
				}
				requestApi(SdkHttpRequest.rcLoginRequest(email, passwd));
			}
		});
	}

	private boolean checkPasswd(String passwd) {
		if (passwd == null || passwd.length() == 0) {
			showError(passwdEdit, passwdEdit.getHint().toString());
			return false;
		}
		if (passwd.length() < 6) {
			showError(passwdEdit, findStringId("vsgm_tony_common_passwd_length_err"));
			return false;
		}
		return true;
	}

	private boolean checkAccount(String account) {
		if (account == null || account.length() == 0) {
			showError(accountEdit, accountEdit.getHint().toString());
			return false;
		}
		// else if (account.length() < 6) {
		// showError(accountEdit,
		// findStringId("vsgm_tony_rc_account_length_err"));
		// return false;
		// }
		return true;
	}

	public static RcLoginView createView(Context ctx) {
		if (ctx == null)
			return null;
		RcLoginView view = (RcLoginView) LayoutInflater.from(ctx).inflate(findLayoutId("vsgm_tony_sdk_view_rc_login"),
				null);
		view.initView();
		return view;
	}

	@Override
	public String getViewTitle() {
		return getContext().getString(ResourceUtil.getStringId("vsgm_tony_rc_login"));
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		super.requestDidSuccess(httpRequest, result);
		MobUser user = new MobUser(result);
		if (user.getStatus() == 1) {
			try {
				JSONObject obj = new JSONObject(result);
				MobUserManager mobUserManager = MobUserManager.getInstance();
				mobUserManager.saveAccount(user.getUserid(), obj.toString());
				mobUserManager.setCurrentUser(user);
				// OnLineHelper.getInstance(getContext()).start();
				SdkDialogViewManager.dialogDismiss();
				GamaterSDK.getInstance().resumeGmae(null);
				GamaterSDK.getInstance().showNoticeDialog();
				mobUserManager.setIsLoginIng(false);
				loginSuccessCallback(user, 3, "rc");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
