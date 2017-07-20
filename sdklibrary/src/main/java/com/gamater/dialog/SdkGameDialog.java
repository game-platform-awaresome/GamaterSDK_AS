package com.gamater.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamater.account.MobUserManager;
import com.gamater.common.Config;
import com.gamater.util.ResourceUtil;
import com.tony.circularprogress.CircularProgressBar;
import com.tony.floatmenu.SDKMenuManager;
import com.tony.sdkview.BaseRelativeLayout;
import com.tony.sdkview.ChangePasswdView;
import com.tony.sdkview.NormalLoginView;
import com.tony.sdkview.SdkSettingView;
import com.tony.sdkview.UpdateAccountView;
import com.tony.viewinterface.BaseOnClickListener;
import com.tony.viewinterface.BaseSdkView;

@SuppressLint({ "NewApi", "ResourceAsColor" })
public class SdkGameDialog extends Dialog {
	public enum ViewType {
		Login, UpdateAccount, ChangePassword
	}

	private BaseRelativeLayout rootView;
	private RelativeLayout containerView;
	private View progressView;
	private boolean isProgressLoading;
	private ImageView logoImage;
	private ImageView backImage;
	private ImageView menuImage;
	private TextView viewTitle;
	private ViewType type;
	private Activity mActivity;

	public SdkGameDialog(Activity activity, ViewType type) {
		// super(activity, ResourceUtil.getStyleId("vsgm_tony_dialog_full"));
		super(activity, ResourceUtil.getStyleId("vsgm_tony_dialog"));
		setOwnerActivity(activity);
		this.type = type;
		this.mActivity = activity;
	}

	public SdkGameDialog(Activity activity) {
		this(activity, ViewType.Login);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SdkDialogViewManager.getManager().init(this);
		MobUserManager mobUserManager = MobUserManager.getInstance();
		if (mobUserManager.getServiceHost().equalsIgnoreCase("") || mobUserManager.getConfigJson(getContext()) == null) {
			mobUserManager.requestUrls();
		}
		LinearLayout myLayout = new LinearLayout(mActivity);
		myLayout.setOrientation(LinearLayout.VERTICAL);

		rootView = (BaseRelativeLayout) getLayoutInflater().inflate(ResourceUtil.getLayoutId("vsgm_tony_sdk_view_base"), null);
//		((RelativeLayout) rootView.findViewById(ResourceUtil.getId("top_bar"))).setBackgroundResource(ResourceUtil.getDrawableId("title_bg"));
//		SDKMenuManager.getInstance(mActivity).initParentView(rootView);
//		SDKMenuManager.getInstance(mActivity).popupMenu();
		containerView = (RelativeLayout) rootView.findViewById(ResourceUtil.getId("container_view"));

		setContentView(rootView);

		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(200);
		rootView.startAnimation(animation);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		backImage = (ImageView) findViewById(ResourceUtil.getId("btnLoginViewBack"));
		backImage.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				onBackPressed();
			}
		});
		menuImage = (ImageView) findViewById(ResourceUtil.getId("btnLoginViewMenu"));
		menuImage.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				SdkDialogViewManager.doAddView(SdkSettingView.createView(getContext()));
			}
		});
		progressView = findViewById(ResourceUtil.getId("progress_view"));
		logoImage = (ImageView) findViewById(ResourceUtil.getId("vsgm_tony_center_logo"));
		if (Config.isOkgameLogo) {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_okgame"));
		} else if (Config.isGmLogo) {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo_gamemy"));
		} else {
			logoImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_logo"));
		}
		viewTitle = (TextView) findViewById(ResourceUtil.getId("vsgm_tony_title"));

		BaseSdkView sdkView = null;
		if (type == ViewType.UpdateAccount) {
			sdkView = UpdateAccountView.createView(getContext());
		} else if (type == ViewType.ChangePassword) {
			sdkView = ChangePasswdView.createView(getContext());
		} else {
			sdkView = NormalLoginView.createView(getContext());
		}
		containerView.addView((View) sdkView, sdkView.getSdkViewLayoutParams());
		updateView(sdkView);

		if (showAfterCallback != null) {
			showAfterCallback.run();
		}

	}

	private Runnable showAfterCallback;
	public void showWithCallback(Runnable callback) {
		showAfterCallback = callback;
		show();
	}

	public void showLoadingView() {
		progressView.setVisibility(View.VISIBLE);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(200);
		progressView.startAnimation(animation);
		CircularProgressBar p = (CircularProgressBar) rootView.findViewById(ResourceUtil.getId("progress_bar"));
		p.start();
		isProgressLoading = true;
	}

	public void hideLoadingView() {
		progressView.postDelayed(new Runnable() {
			@Override
			public void run() {
				AlphaAnimation animation = new AlphaAnimation(1, 0);
				animation.setDuration(200);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						progressView.setVisibility(View.GONE);
					}
				});
				progressView.startAnimation(animation);
				CircularProgressBar p = (CircularProgressBar) rootView.findViewById(ResourceUtil.getId("progress_bar"));
				p.stop();
				isProgressLoading = false;
			}
		}, 500);
	}

	public void addBaseSdkView(View view) {
		if (containerView != null)
			SdkDialogViewManager.addBaseSdkView(containerView, view);
	}

	public void updateView(BaseSdkView v) {
		updateViewTitle(v.getViewTitle());
		updateViewBack(v.interceptOnBackEvent());
		updateViewMenu(v.isMenuEnable());
	}

	private void updateViewMenu(boolean isMenuEnable) {
		if (isMenuEnable) {
			menuImage.setVisibility(View.VISIBLE);
		} else {
			menuImage.setVisibility(View.GONE);
		}
	}

	private void updateViewBack(boolean intercept) {
		if (intercept) {
			backImage.setVisibility(View.GONE);
		} else {
			backImage.setVisibility(View.VISIBLE);
		}
	}

	private void updateViewTitle(String title) {
		if (title == null) {
			viewTitle.setVisibility(View.GONE);
			logoImage.setVisibility(View.VISIBLE);
		} else {
			viewTitle.setText(title);
			viewTitle.setVisibility(View.VISIBLE);
			logoImage.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		SdkDialogViewManager.destory();
	}

	@Override
	public void dismiss() {
		SDKMenuManager.getInstance(mActivity).resetIconAlphaCallback();
		SDKMenuManager.getInstance(mActivity).initParentView(null);
		AlphaAnimation aa = new AlphaAnimation(1, 0.3f);
		aa.setDuration(100);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				SdkGameDialog.super.dismiss();
			}
		});
		rootView.startAnimation(aa);
	}

	private float touchY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			touchY = ev.getY();
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (Math.abs(ev.getY() - touchY) > 5)
				return super.dispatchTouchEvent(ev);
			View localView = getCurrentFocus();
			if (shouldHandleEvent(ev)) {
				InputMethodManager localInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if ((localView != null) && (localInputMethodManager != null)) {
					localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	protected void hideSoftInput(Context context) {
		try {
			InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (getCurrentFocus() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
		}
	}

	public boolean shouldHandleEvent(MotionEvent ev) {
		if (isTouchEditText((ViewGroup) this.rootView, ev)) {
			return false;
		}
		return true;
	}

	private boolean isTouchEditText(ViewGroup paramViewGroup, MotionEvent paramMotionEvent) {
		try {
			for (int i = 0; i < paramViewGroup.getChildCount(); i++) {
				View localView = paramViewGroup.getChildAt(i);
				if ((localView instanceof ViewGroup)) {
					if (isTouchEditText((ViewGroup) localView, paramMotionEvent))
						return true;
				} else if ((localView instanceof EditText)) {
					int[] arrayOfInt = new int[2];
					localView.getLocationInWindow(arrayOfInt);
					int j = arrayOfInt[0];
					int k = arrayOfInt[1];
					int m = k + localView.getHeight();
					int n = j + localView.getWidth();
					if ((paramMotionEvent.getX() > j) && (paramMotionEvent.getX() < n) && (paramMotionEvent.getY() > k)
							&& (paramMotionEvent.getY() < m)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (((BaseSdkView) containerView.getChildAt(containerView.getChildCount() - 1)).interceptOnBackEvent() || isProgressLoading)
			return;
		if (SdkDialogViewManager.doViewBackPressed(containerView)) {
			return;
		}
		super.onBackPressed();
	}
}
