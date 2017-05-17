package com.tony.floatmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.gamater.account.MobUserManager;
import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.dialog.util.DialogUtil;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.ResourceUtil;
import com.tony.sdkview.SdkViewOpenHelper;
import com.tony.view.FloatMenuHorizontalScrollView;
import com.tony.viewinterface.BaseOnClickListener;
import com.vsgm.sdk.SDKActivity;

public class FloatMenu extends FrameLayout {
	private Activity mActivity;
	private FloatMenuClipBgView mainView;

	private int isAccountAvaiable;
	private int isHomePageAvaiable;
	private int isServiceAvaiable;
	private int isFBAvaiable;

	private FloatMenuHorizontalScrollView menuScroll;
	private FloatMenuTextView fbView;
	private FloatMenuTextView serviceView;
	private FloatMenuTextView accountView;
	private FloatMenuTextView homePageView;

	private BaseOnClickListener onClickListener = new BaseOnClickListener() {
		@Override
		public void onBaseClick(View v) {
			SDKMenuManager.getInstance(null).hideMenuView();
			if (v.getId() == ResourceUtil.getId("tvAccount")) {
				SDKMenuManager.getInstance(null).openAccountDialog(0);
			} else if (v.getId() == ResourceUtil.getId("tvHomePage")) {
//				 EventTracker.btnClickEvent(2);
//                     if (user.getType().equalsIgnoreCase("0")) {
//                     dialog();
//                     return;
//				 }
				SdkViewOpenHelper.openHomeWebDialog();
			} else if (v.getId() == ResourceUtil.getId("tvFB")) {
				SdkViewOpenHelper.openFBWebDialog();
			} else if (v.getId() == ResourceUtil.getId("tvService")) {
//                EventTracker.btnClickEvent(4);
				SdkViewOpenHelper.openServiceDialog();
			}
		}
	};

	public void setServiceNum(int num) {
		if (num > 0) {
			serviceView.setNum(num);
		} else {
			serviceView.setNum(0);
		}
	}

	public int getNoteNum() {
		return accountView.getNum() + homePageView.getNum() + fbView.getNum() + serviceView.getNum();
	}

	public boolean isRedPoint() {
		return accountView.isRedPointShowing() || homePageView.isRedPointShowing() || fbView.isRedPointShowing() || serviceView.isRedPointShowing();
	}

	@SuppressLint("NewApi")
	public FloatMenu(Activity activity) {
		super(activity);
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		this.mActivity = activity;
		init(activity);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(ResourceUtil.getLayoutId("vsgm_tony_float_menu"), this);
		mainView = (FloatMenuClipBgView) findViewById(ResourceUtil.getId("menu_main_view"));
		if (Config.isOkgameLogo) {
			try {
				mainView.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_okgame_float_menu_bg"));
			} catch (Exception e) {
                e.printStackTrace();
			}
		}

		menuScroll = (FloatMenuHorizontalScrollView) findViewById(ResourceUtil.getId("menu_scroll"));
		accountView = (FloatMenuTextView) findViewById(ResourceUtil.getId("tvAccount"));
		accountView.setOnClickListener(onClickListener);
		accountView.setVisibility(isAccountAvaiable == ConfigUtil.getViewVisible(ConfigUtil.MENU_ACCOUNT) ? View.VISIBLE: View.GONE);
		homePageView = (FloatMenuTextView) findViewById(ResourceUtil.getId("tvHomePage"));
		homePageView.setOnClickListener(onClickListener);
		homePageView.setVisibility(isHomePageAvaiable == ConfigUtil.getViewVisible(ConfigUtil.MENU_HOMEPAGE) ? View.VISIBLE: View.GONE);
		fbView = (FloatMenuTextView) findViewById(ResourceUtil.getId("tvFB"));
		fbView.setOnClickListener(onClickListener);
		fbView.setVisibility(isFBAvaiable == ConfigUtil.getViewVisible(ConfigUtil.MENU_FACEBOOK) ? View.VISIBLE: View.GONE);
		serviceView = (FloatMenuTextView) findViewById(ResourceUtil.getId("tvService"));
		serviceView.setOnClickListener(onClickListener);
		serviceView.setVisibility(isServiceAvaiable == ConfigUtil.getViewVisible(ConfigUtil.MENU_SERVICES) ? View.VISIBLE: View.GONE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public int getMenuContentWidth() {
		if (menuScroll == null) {
			return getMeasuredWidth();
		}
		return menuScroll.getMeasuredWidth();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if ((w != oldw) || (h != oldh)) {
			makePath(w, h, h / 2);
		}
	}

	private Path makePath(int width, int height, int radius) {
		if (p == null)
			p = new Path();
		p.reset();
		p.addRoundRect(new RectF(0.0F, 0.0F, width, height), radius, radius, Path.Direction.CW);
		return p;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mainView != null && mainView.getClipPath() != null) {
			mainView.setClipPath(makePath(menuScroll.getMeasuredWidth(), getHeight(), getHeight() / 2 + 1));
			Path p = mainView.getClipPath();
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.parseColor("#CC000000"));
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			paint.setAntiAlias(true);
			canvas.drawPath(p, paint);
		}
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private Path p;

	public void setItemVisible(int visible) {
		if (isAccountAvaiable == View.VISIBLE) {
			findViewById(ResourceUtil.getId("tvAccount")).setVisibility(visible);
		}
		if (isHomePageAvaiable == View.VISIBLE) {
			findViewById(ResourceUtil.getId("tvHomePage")).setVisibility(visible);
		}
		if (isServiceAvaiable == View.VISIBLE) {
			findViewById(ResourceUtil.getId("tvService")).setVisibility(visible);
		}
		if (isFBAvaiable == View.VISIBLE) {
			findViewById(ResourceUtil.getId("tvFB")).setVisibility(visible);
		}
		updateMenuIcon(visible);
	}

	public void setDirection(boolean isLeft) {
		invalidate();
	}

	public void updateMenuIcon(int visible) {
		MobUserManager mobUserManager = MobUserManager.getInstance();
		MobUser user = mobUserManager.getCurrentUser();
		isFBAvaiable = ConfigUtil.getViewVisible(ConfigUtil.MENU_FACEBOOK);
		isAccountAvaiable = ConfigUtil.getViewVisible(ConfigUtil.MENU_ACCOUNT);
		isHomePageAvaiable = ConfigUtil.getViewVisible(ConfigUtil.MENU_HOMEPAGE);
		if (isAccountAvaiable == View.VISIBLE) {
			View view = findViewById(ResourceUtil.getId("tvAccount"));
			if (user != null) {
				if (visible != 99)
					view.setVisibility(visible);
			} else {
				view.setVisibility(View.GONE);
			}
		} else {
			findViewById(ResourceUtil.getId("tvAccount")).setVisibility(View.GONE);
		}
		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		invalidate();
	}

	public void showAnim(boolean isLeft) {
		menuScroll.fullScroll(View.FOCUS_LEFT);
		setDirection(isLeft);
		setItemVisible(View.INVISIBLE);
		setItemVisible(View.VISIBLE);
		float pivotX = isLeft ? 0.1f : 0.9f, pivotY = 0.5f;
		ScaleAnimation a1 = new ScaleAnimation(0f, 1.1f, 0f, 1f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
		a1.setDuration(150);
		final ScaleAnimation a2 = new ScaleAnimation(1.1f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
		a2.setDuration(100);
		a1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				clearAnimation();
				startAnimation(a2);
			}
		});
		startAnimation(a1);
	}

	public void hideAnim(boolean isLeft) {
		setItemVisible(View.INVISIBLE);
		float pivotX = isLeft ? 0.1f : 0.9f, pivotY = 0.5f;
		ScaleAnimation a1 = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, pivotX,
				Animation.RELATIVE_TO_SELF, pivotY);
		a1.setDuration(150);
		a1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				clearAnimation();
			}
		});
		startAnimation(a1);
	}

	private void dialog() {
		Resources res = mActivity.getResources();
		String done = res.getString(ResourceUtil.getStringId("vsgm_tony_btn_done"));
		String cancel = res.getString(ResourceUtil.getStringId("vsgm_tony_btn_cancel"));
		String title = Config.gmTitle;
		String msg = res.getString(ResourceUtil.getStringId("vsgm_tony_float_dialog_upgrade_account"));

		AlertDialog.Builder builder = DialogUtil.showDialog(mActivity, title, msg);
		builder.setNegativeButton(cancel, null);
		builder.setPositiveButton(done, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(mActivity, Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
				intent.putExtra(MVMainActivity.WIN_TYPE, WinType.UpgradeAccount.toString());
				mActivity.startActivity(intent);
			}
		});

		builder.show();
	}

}
