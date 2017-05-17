package com.tony.sdkview;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.http.SdkHttpRequest;
import com.gamater.account.po.MobUser;
import com.gamater.account.po.ThirdType;
import com.gamater.common.http.HttpRequest;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.view.SdkTipsTextView;
import com.tony.viewinterface.BaseOnClickListener;

public class NormalLoginView extends BaseLinearLayout {
	private boolean canQuickLogin;
	private MobUser user;
	private int passwdErrorCount = 0;

	public NormalLoginView() {
		super(GamaterSDK.getInstance().getActivity());
	}

	public NormalLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public NormalLoginView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NormalLoginView(Context context) {
		super(context);
	}

	private EditText accountEdit;
	private EditText passwdEdit;

	@Override
	public void initView() {
		accountEdit = (EditText) findViewById(findId("account_edit"));
		accountEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				String str = accountEdit.getText().toString().trim();
				if (isShown() && !hasFocus) {
					checkAccount(str);
				}
			}
		});
		accountEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = accountEdit.getText().toString().trim();
				if (canQuickLogin && !user.getEmail().equals(str)) {
					canQuickLogin = false;
					passwdEdit.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		passwdEdit = (EditText) findViewById(findId("passwd_edit"));
		passwdEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && canQuickLogin) {
					canQuickLogin = false;
					passwdEdit.setText("");
				}
			}
		});
		passwdEdit.setTypeface(Typeface.DEFAULT);
		final SdkTipsTextView btnLogin = (SdkTipsTextView) findViewById(ResourceUtil.getId("btnLogin"));
		btnLogin.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				if (canQuickLogin && user != null) {
					requestApi(SdkHttpRequest.eLoginRequest(user.getUserid(), user.getToken()));
					return;
				}
				String email = accountEdit.getText().toString().trim();
				if (!checkAccount(email)) {
					return;
				}
				String passwd = passwdEdit.getText().toString().trim();
				if (!checkPasswd(passwd)) {
					return;
				}
				// 发送登录请求
				requestApi(SdkHttpRequest.normalLoginRequest(email, passwd));
			}
		});
		// 注册一个
		findViewById(findId("btnLoginViewRegister")).setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				startView(RegisterView.createView(getContext()));
			}
		});
		// 忘记密码
		findViewById(findId("btnLoginViewForgetPw")).setVisibility(View.INVISIBLE);
		findViewById(findId("btnLoginViewForgetPw")).setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				startView(ForgetPasswdView.createView(getContext()));
			}
		});
		// 游客试玩
		findViewById(findId("btnQuickLogin")).setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				requestApi(SdkHttpRequest.quickLoginRequest());
			}
		});
		MobUserManager mum = MobUserManager.getInstance();
		List<MobUser> list = mum.accountList();
		if (list != null && list.size() > 0) {
			for (MobUser u : list) {
				if (!u.getType().equalsIgnoreCase("0")) {
					if (u.getEmail() != null && u.getEmail().length() > 0) {
						user = u;
						accountEdit.setText(user.getEmail());
						passwdEdit.setText("******");
						canQuickLogin = true;
						break;
					}
				}
			}
		}

		ImageView[] btn = new ImageView[2];
		btn[0] = (ImageView) findViewById(findId("btnLoginViewThirdLogin1"));
		btn[0].setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				thirdLogin((ThirdType) v.getTag());
			}
		});
		btn[1] = (ImageView) findViewById(findId("btnLoginViewThirdLogin2"));
		btn[1].setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				thirdLogin((ThirdType) v.getTag());
			}
		});
		ImageView otherBtn = (ImageView) findViewById(findId("btnLoginViewOtherLogin"));
		// int count = ConfigUtil.getThirdLoginCount();
		int count = 2;
		if (count > 2) {
			otherBtn.setOnClickListener(new BaseOnClickListener() {
				@Override
				public void onBaseClick(View v) {
					startView(ThirdLoginView.createView(getContext()));
				}
			});
		} else {
			otherBtn.setVisibility(View.GONE);
			if (count == 1) {
				btn[1].setVisibility(View.GONE);
			} else if (count == 0) {
				btn[0].setVisibility(View.GONE);
				btn[1].setVisibility(View.GONE);
				return;
			}
		}
		int index = 0;
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_FACEBOOK)) {
			setThirdBtn(btn[index], ThirdType.facebook);
			index++;
		}
		if (ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_GOOGLE)) {
			setThirdBtn(btn[index], ThirdType.google);
			index++;
		}

		if (index < 2 && ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_INSTAGRAM)) {
			setThirdBtn(btn[index], ThirdType.instagram);
			index++;
		}
		if (index < 2 && ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_TWITTER)) {
			setThirdBtn(btn[index], ThirdType.twitter);
			index++;
		}
		if (index < 2 && ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_RC)) {
			setThirdBtn(btn[index], ThirdType.rc);
			index++;
		}
	}

	private void setThirdBtn(ImageView v, ThirdType type) {
		v.setTag(type);
		if (type == ThirdType.facebook) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_fb_selector"));
		} else if (type == ThirdType.google) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_google_selector"));
		} else if (type == ThirdType.instagram) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_camera_selector"));
		} else if (type == ThirdType.twitter) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_twitter_selector"));
		} else if (type == ThirdType.rc) {
			v.setImageResource(findDrawableId("vsgm_tony_btn_rc_selector"));
		}
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
			showError(accountEdit, getContext().getString(ResourceUtil.getStringId("vsgm_input_account")));
			return false;
		} else if (!checkEmail(account)) {
			showError(accountEdit, findStringId("okgame_email_format_error"));
			return false;
		}
		return true;
	}

	public static NormalLoginView createView(Context ctx) {
		if (ctx == null)
			return null;
		NormalLoginView view = (NormalLoginView) LayoutInflater.from(ctx)
				.inflate(findLayoutId("vsgm_tony_sdk_view_login"), null);
		// 初始化界面元素和事件
		view.initView();
		return view;
	}

	@Override
	public boolean interceptOnBackEvent() {
		return true;
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		super.requestDidSuccess(httpRequest, result);
		String funcation = httpRequest.getFuncation();
		if (funcation.equals(APIs.WEB_API_THIRD_LOGIN))
			return;
		try {
			JSONObject obj = new JSONObject(result);
			MobUser user = new MobUser(result);
			if (user.getStatus() == 1) {
				String email = accountEdit.getText().toString().trim();
				JSONObject data = obj.getJSONObject("data");
				if (!funcation.equals(APIs.WEB_API_FREE_LOGIN) && email.length() > 0) {
					data.put("email", email);
					user.setEmail(email);
				}
				obj.put("data", data);
				MobUserManager mobUserManager = MobUserManager.getInstance();
				mobUserManager.saveAccount(user.getUserid(), obj.toString());
				mobUserManager.setCurrentUser(user);
//				OnLineHelper.getInstance(getContext()).start();
				GamaterSDK.getInstance().resumeGmae(null);
				GamaterSDK.getInstance().showNoticeDialog();
				mobUserManager.setIsLoginIng(false);
				loginSuccessCallback(user, funcation.equals(APIs.WEB_API_FREE_LOGIN) ? 1 : 2, null);
				getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						SdkDialogViewManager.dialogDismiss();
					}
				}, 300);
			} else if (obj.optInt("errorCode") == 102) {
				passwdErrorCount = passwdErrorCount + 1;
				if (passwdErrorCount > 1) {
					findViewById(findId("btnLoginViewForgetPw")).setVisibility(View.VISIBLE);
				}
			} else if (canQuickLogin) {
				canQuickLogin = false;
				passwdEdit.setText("");
				showError(passwdEdit, passwdEdit.getHint().toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isMenuEnable() {
		return true;
	}
}
