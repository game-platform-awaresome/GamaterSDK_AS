package com.tony.sdkview;

import static com.gamater.dialog.SdkDialogViewManager.hideLoadingView;
import static com.gamater.dialog.SdkDialogViewManager.showLoadingView;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.account.po.MobUser;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.dialog.SdkDialogViewManager;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.ResourceUtil;
import com.tony.view.SdkTipsTextView;
import com.tony.viewinterface.BaseSdkView;

public class BaseRelativeLayout extends RelativeLayout implements BaseSdkView,
		HttpEventListener {
	public BaseRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseRelativeLayout(Context context) {
		super(context);
	}

	public LayoutParams getSdkViewLayoutParams() {
		return new LayoutParams(dip2px(320), LayoutParams.WRAP_CONTENT);
		// return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public Animation getStartAnimation(AnimationListener listener) {
		AnimationSet set = new AnimationSet(true);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1);
		aa.setDuration(100);
		ScaleAnimation sa = new ScaleAnimation(1.1f, 1f, 1.1f, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(100);
		set.addAnimation(aa);
		set.addAnimation(sa);
		if (listener != null)
			set.setAnimationListener(listener);
		return set;
	}

	@Override
	public Animation getEndAnimation(AnimationListener listener) {
		AnimationSet set = new AnimationSet(true);
		AlphaAnimation aa = new AlphaAnimation(1, 0.5f);
		aa.setDuration(100);
		ScaleAnimation sa = new ScaleAnimation(1, 1.1f, 1, 1.1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(100);
		set.addAnimation(aa);
		set.addAnimation(sa);
		if (listener != null)
			set.setAnimationListener(listener);
		return set;
	}

	@Override
	public boolean doViewGoBack() {
		if (getChildCount() > 1) {
			View view = getChildAt(getChildCount() - 1);
			if (view instanceof BaseSdkView) {
				return ((BaseSdkView) view).doViewGoBack();
			}
			return false;
		}
		return false;
	}

	@Override
	public void initView() {

	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	protected void startView(View newView) {
		SdkDialogViewManager.doAddView(newView);
	}

	@Override
	public String getViewTitle() {
		return null;
	}

	@Override
	public boolean interceptOnBackEvent() {
		return false;
	}

	@Override
	public boolean isMenuEnable() {
		return false;
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		showLoadingView();
	}

	protected static int findStringId(String stringName) {
		return ResourceUtil.getStringId(stringName);
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		hideLoadingView();
		showError(findStringId("vsgm_tony_err_unknown"));
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		try {
			hideLoadingView();
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");
			if (status == 0) {
				int errorCode = obj.getInt("errorCode");
				switch (errorCode) {
				case 100:
					showError(findStringId("vsgm_tony_err_100"));
					break;
				case 101:
					showError(findStringId("vsgm_tony_err_101"));
					break;
				case 102:
					showError(findStringId("vsgm_tony_err_102"));
					break;
				case 103:
					showError(findStringId("vsgm_tony_err_103"));
					break;
				case 104:
					showError(findStringId("vsgm_tony_err_104"));
					break;
				case 105:
					showError(findStringId("vsgm_tony_err_105"));
					break;
				case 107:
					showError(findStringId("vsgm_tony_err_107"));
					break;
				case 108:
					showError(findStringId("vsgm_tony_err_108"));
					break;
				case 109:
					showError(findStringId("vsgm_tony_err_109"));
					break;
				case 211:
					showError(findStringId("vsgm_tony_err_211"));
					break;
				default:
					showError(findStringId("vsgm_tony_err_unknown"));
					break;
				}
			} else {
				String funcation = httpRequest.getFuncation();
				if (funcation == APIs.WEB_API_THIRD_LOGIN) {
					MobUser user = new MobUser(result);
					if (user.getStatus() == 1) {
						MobUserManager mobUserManager = MobUserManager
								.getInstance();
						mobUserManager.saveAccount(user.getUserid(),
								obj.toString());
						mobUserManager.setCurrentUser(user);
						SdkDialogViewManager.dialogDismiss();
//						OnLineHelper.getInstance(getContext()).start();
						GamaterSDK.getInstance().resumeGmae(null);
						GamaterSDK.getInstance().showNoticeDialog();
						mobUserManager.setIsLoginIng(false);
						loginSuccessCallback(user, 3, httpRequest.getTag()
								.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SdkTipsTextView errorView;

	protected void showError(String errorStr) {
		if (errorView == null) {
			errorView = (SdkTipsTextView) findViewWithTag(SdkTipsTextView.TIP_VIEW_TAG);
		}
		if (errorView != null) {
			errorView.showError(errorStr);
		} else {
			showToast(errorStr);
		}
	}

	protected void showToast(int stringId) {
		Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(String str) {
		Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
	}

	protected void showError(int errorStrRes) {
		String error = getContext().getResources().getString(errorStrRes);
		showError(error);
	}

	protected void loginSuccessCallback(MobUser user, int type, String typeName) {
		if (GamaterSDK.getInstance().getAcGameSDKListener() != null && user != null) {
			GamaterSDK.getInstance().getAcGameSDKListener().didLoginSuccess(user);
		}
	}

	protected void requestApi(HttpRequest request) {
		if (request == null)
			return;
		request.setHttpEventListener(this);
		request.asyncStart();
	}
}
