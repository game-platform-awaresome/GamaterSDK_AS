package com.tony.floatmenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.gamater.define.SPUtil;
import com.gamater.dialog.SdkAccountDialog;
import com.gamater.dialog.SdkMenuHideDialog;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.util.DensityUtil;
import com.tony.view.DeleteView;

@SuppressWarnings("deprecation")
public class SDKMenuManager {
	public static final String MENU_TAG = "AcGameMenu";
	private static SDKMenuManager instance;
	private Activity mActivity;
	private int mScreenWidth;
	private int mScreenHeight;

	private FloatIcon mFloatIcon;
	private AbsoluteLayout.LayoutParams mIconParams;

	private DeleteView mDeleteView;
	private View deleteBgView;
	private AbsoluteLayout.LayoutParams mDeleteParams;

	private FloatMenu mFloatMenu;
	private AbsoluteLayout.LayoutParams mMenuParams;
	private int mMenuWidth, mMenuHeight;

	private AbsoluteLayout mContainer;

	private SensorManager sensorManager;

	private boolean isFloatHideEnable = true;

	public SDKMenuManager() {
	}

	private SDKMenuManager(Activity activity) {
		this.mActivity = activity;
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;

		mContainer = new AbsoluteLayout(mActivity);
		mContainer.setTag(MENU_TAG);
		mContainer.setBackgroundColor(Color.argb(0, 0, 0, 0));
	}

	public int getHeight() {
		if (mContainer != null) {
			int height = mContainer.getMeasuredHeight();
			return height == 0 ? mScreenHeight : height;
		}
		return mScreenHeight;
	}

	public void initParentView(ViewGroup parent) {
		ViewGroup decor = parent;
		if (decor == null) {
			decor = (ViewGroup) mActivity.getWindow().getDecorView();
		}
		if (mContainer.getParent() != null) {
			if (mContainer.getParent() == parent) {
				return;
			}
			try {
				removeView(mContainer);
			} catch (Exception e) {
			}
		}
		decor.addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		if (mFloatIcon != null && !mFloatIcon.isMenuShowing()) {
			mFloatIcon.startViewAlpha();
		}
		updateMenuStatus();
	}

	public void resetIconAlphaCallback() {
		if (mFloatIcon != null) {
			mFloatIcon.removeAlphaCallback();
		}
	}

	public static SDKMenuManager getInstance(Activity activity) {
		if (activity != null) {
			if (instance == null) {
				instance = new SDKMenuManager(activity);
			}
			if (activity != instance.mActivity) {
				instance.dismissMenu();
				instance.mActivity = activity;
			}
		}
		return instance;
	}

	public void openAccountDialog(final int from) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mFloatIcon != null)
					mFloatIcon.setMenuShowing(false);
				new SdkAccountDialog(mActivity).show();
			}
		});
	}

	private void createIconView() {
		if (mFloatIcon != null)
			return;
		mFloatIcon = new FloatIcon(mActivity, mMenuHeight);
		int[] originCache = SPUtil.getFloatMenuOrigin(mActivity);
		int x, y;
		if (originCache == null) {
			int[] origin = ConfigUtil.getMenuOrigin();
			if (origin != null) {
				if (origin[0] != 0 && origin[0] != 100)
					origin[0] = 0;
				if (origin[1] < 0 || origin[1] > 100)
					origin[1] = 0;
			} else {
				origin = new int[] { 0, 0 };
			}
			x = (mScreenWidth - mMenuHeight) * origin[0] / 100;
			y = (getHeight() - mMenuHeight) * origin[1] / 100;
		} else {
			x = originCache[0];
			y = originCache[1];
		}
		mIconParams = new AbsoluteLayout.LayoutParams(mMenuHeight, mMenuHeight, x, y);
		mFloatIcon.setParams(mIconParams);
		mFloatIcon.checkParams();
		mIconParams = mFloatIcon.getParams();
		mContainer.addView(mFloatIcon, mIconParams);
		setViewPosition(mFloatIcon, mIconParams.x, mIconParams.y);
		mFloatIcon.invalidate();
		mFloatIcon.startViewAlpha();
	}

	private void removeView(View v) {
		ViewGroup parent = ((ViewGroup) v.getParent());
		if (parent != null)
			parent.removeView(v);
	}

	public void setViewPosition(View v, int x, int y) {
		if (x < 0)
			x = 0;
		if (x > mScreenWidth - v.getMeasuredWidth())
			x = mScreenWidth - v.getMeasuredWidth();
		if (y < 0)
			y = 0;
		if (y > getHeight() - v.getMeasuredHeight())
			y = getHeight() - v.getMeasuredHeight();
		v.layout(x, y, x + v.getMeasuredWidth(), y + v.getMeasuredHeight());
		v.invalidate();
	}

	public void removeIconView() {
		if (mFloatIcon != null) {
			removeView(mFloatIcon);
			mFloatIcon = null;
		}
	}

	private void createMenuView() {
		if (mFloatMenu != null)
			return;
		mFloatMenu = new FloatMenu(mActivity);
		mFloatMenu.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		mMenuWidth = mFloatMenu.getMenuContentWidth();
		mMenuHeight = mFloatMenu.getMeasuredHeight();
		if (mMenuParams == null) {
			mMenuParams = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
		}
		mContainer.addView(mFloatMenu, mMenuParams);
		mFloatMenu.setVisibility(View.INVISIBLE);
	}

	public void removeMenuView() {
		if (mFloatMenu != null) {
			removeView(mFloatMenu);
			mFloatMenu = null;
		}
	}

	public void setServiceNoteNum(int num) {
		if (mFloatMenu != null)
			mFloatMenu.setServiceNum(num);
	}

	public void createDeleteView() {
		if (!isFloatHideEnable)
			return;
		if (mDeleteView != null)
			return;
		mDeleteView = new DeleteView(mActivity);
		int width = (int) (DensityUtil.dip2px(mActivity, 39));
		int height = (int) (DensityUtil.dip2px(mActivity, 39));
		if (mDeleteParams == null) {
			mDeleteParams = new AbsoluteLayout.LayoutParams(width, height, (mScreenWidth - width) / 2,
					getHeight() - height - (int) (DensityUtil.dip2px(mActivity, 35)));
		}
		mContainer.addView(mDeleteView, mDeleteParams);
		mDeleteView.setVisibility(View.INVISIBLE);

		deleteBgView = new View(mActivity);
		GradientDrawable bgDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { 0x66000000,
				0xBB000000, 0xF0000000 });
		deleteBgView.setBackgroundDrawable(bgDrawable);
		AbsoluteLayout.LayoutParams deleteBgParams = new AbsoluteLayout.LayoutParams(mScreenWidth,
				(int) (DensityUtil.dip2px(mActivity, 35)), 0, getHeight() - (int) (DensityUtil.dip2px(mActivity, 35)));

		mContainer.addView(deleteBgView, deleteBgParams);
		deleteBgView.setVisibility(View.INVISIBLE);
	}

	public void removeDeleteView() {
		if (mDeleteView != null) {
			removeView(mDeleteView);
			removeView(deleteBgView);
			mDeleteView = null;
			deleteBgView = null;
		}
	}

	public void updateIconView() {
		if (mFloatIcon != null) {
			mFloatIcon.setNoteNumVisible(true);
			mFloatIcon.setMenuShowing(false);
			mFloatIcon.invalidate();
			if (tempX >= 0) {
				mIconParams.x = tempX;
				tempX = -1;
				setViewPosition(mFloatIcon, mIconParams.x, mIconParams.y);
			}
		}
		if (mFloatMenu != null) {
			mFloatMenu.setVisibility(View.INVISIBLE);
		}
		if (mContainer != null)
			mContainer.invalidate();
	}

	public void updateDeteleView() {
		if (isFloatHideEnable && mDeleteView != null) {
			mDeleteView.setVisibility(View.VISIBLE);
			deleteBgView.setVisibility(View.VISIBLE);
			mDeleteView.updateDeteleStatus(isReadyToDelete());
		}
	}

	public void hideMenuView() {
		mFloatMenu.setItemVisible(View.VISIBLE);
		mFloatMenu.setVisibility(View.INVISIBLE);
		mFloatIcon.setNoteNumVisible(true);
		mFloatIcon.setMenuShowing(false);
		mFloatIcon.startViewAlpha();
	}

	private int tempX = -1;

	public void updateMenuStatus() {
		if (mFloatIcon != null && mFloatMenu != null) {
			mFloatMenu.updateMenuIcon(99);
		}
	}

	private Handler handler = new Handler();
	private Runnable loginTodayRunnable = new Runnable() {
		@Override
		public void run() {
			updateMenuView();
		}
	};

	public void updateMenuViewLoginToday() {
		if (mFloatMenu == null)
			return;
		mFloatIcon.setTouchable(true);
		mFloatIcon.postDelayed(new Runnable() {
			@Override
			public void run() {
				updateMenuView();
			}
		}, 500);

		if (loginTodayRunnable != null) {
			handler.postDelayed(loginTodayRunnable, 5000);
		}
	}

	public void updateMenuView() {
		if (mFloatMenu == null)
			return;
		if (mFloatMenu.getVisibility() == View.VISIBLE) {
			mFloatMenu.hideAnim(mFloatIcon.isLeft());
			hideMenuView();
			if (tempX >= 0) {
				mIconParams.x = tempX;
				tempX = -1;
				setViewPosition(mFloatIcon, mIconParams.x, mIconParams.y);
			}
		} else {
			mFloatMenu.updateMenuIcon(View.INVISIBLE);
			mFloatMenu.setVisibility(View.VISIBLE);
			mFloatIcon.setNoteNumVisible(false);
			if (mFloatIcon.isLeft()) {
				if (mIconParams.x + mFloatIcon.getMeasuredWidth() + mFloatMenu.getMenuContentWidth() > mScreenWidth) {
					tempX = mIconParams.x;
					mIconParams.x = mScreenWidth - mFloatIcon.getMeasuredWidth() - mFloatMenu.getMenuContentWidth();
				}
				mMenuParams.x = mIconParams.x + mFloatIcon.getMeasuredWidth();
			} else {
				if (mIconParams.x - mFloatMenu.getMenuContentWidth() < 0) {
					tempX = mIconParams.x;
					mIconParams.x = mFloatMenu.getMenuContentWidth();
				}
				mMenuParams.x = mIconParams.x - mFloatMenu.getMenuContentWidth();
			}
			mMenuParams.y = mIconParams.y;
			mFloatMenu.setVisibility(View.VISIBLE);
			mFloatIcon.setMenuShowing(true);
			setViewPosition(mFloatIcon, mIconParams.x, mIconParams.y);
			setViewPosition(mFloatMenu, mMenuParams.x, mMenuParams.y);
			mFloatMenu.showAnim(mFloatIcon.isLeft());
		}
	}

	public boolean hideDeleteView() {
		if (!isFloatHideEnable || mDeleteView == null)
			return false;
		mDeleteView.setVisibility(View.INVISIBLE);
		deleteBgView.setVisibility(View.INVISIBLE);
		if (mDeleteView.isDelete()) {
			mFloatIcon.resetPosition();
			SdkMenuHideDialog dialog = new SdkMenuHideDialog.Builder(mActivity)
					.setNegativeButton(new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mFloatIcon.setVisibility(View.VISIBLE);
							if (mDeleteView != null)
								mDeleteView.updateDeteleStatus(false);
							mFloatIcon.startViewAlpha();
						}
					}).setPositiveButton(new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mFloatIcon.resetViewAlpha();
							mFloatIcon.setVisibility(View.GONE);
							mFloatIcon.setTouchable(false);
							isViewShowing = false;
							if (mDeleteView != null)
								mDeleteView.updateDeteleStatus(false);
						}
					}).setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							mFloatIcon.setVisibility(View.VISIBLE);
							if (mDeleteView != null)
								mDeleteView.updateDeteleStatus(false);
							mFloatIcon.startViewAlpha();
						}
					}).create();
			dialog.show();
			mFloatIcon.setVisibility(View.INVISIBLE);
			return true;
		} else {
			return false;
		}
	}

	private boolean isReadyToDelete() {
		if ((mIconParams.x + mIconParams.width) < mDeleteParams.x)
			return false;
		if ((mIconParams.x) > (mDeleteParams.x + mDeleteParams.width))
			return false;
		if ((mIconParams.y + mIconParams.height) < mDeleteParams.y)
			return false;
		return true;
	}

	public void popupMenu() {
		createMenuView();
		createDeleteView();
		createIconView();
	}

	public void dismissMenu() {
		removeIconView();
		removeMenuView();
		removeDeleteView();
	}

	public void destory() {
		instance = null;
	}

	public void showMenuView() {
		mFloatIcon.setNoteNumVisible(false);
		mFloatMenu.setVisibility(View.VISIBLE);
		mFloatIcon.setMenuShowing(true);
		mFloatIcon.resetViewAlpha();
	}

	public boolean isVisible() {
		return instance != null && mFloatIcon != null && mFloatIcon.getVisibility() == View.VISIBLE;
	}

	public void onResume() {
		if (FacebookSdk.isInitialized() && FacebookSdk.getApplicationId() != null) {
			AppEventsLogger.activateApp(mActivity, FacebookSdk.getApplicationId());
		}
	}

	public void onPause() {
		if (FacebookSdk.isInitialized() && FacebookSdk.getApplicationId() != null) {
			AppEventsLogger.deactivateApp(mActivity, FacebookSdk.getApplicationId());
		}
	}

	@SuppressWarnings("unused")
	private boolean isViewShowing = true;

	public void setFloatIconEnable(boolean enable) {
		if (mFloatIcon != null) {
			mFloatIcon.setTouchable(enable);
		}
	}

	@SuppressWarnings("unused")
	private void hideAnim() {
		mFloatIcon.resetViewAlpha();
		mFloatIcon.setTouchable(false);
		AlphaAnimation a = new AlphaAnimation(1f, 0f);
		a.setDuration(300);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mFloatIcon.setVisibility(View.GONE);
			}
		});
		mFloatIcon.startAnimation(a);
	}

	@SuppressWarnings("unused")
	private void showAnim() {
		mFloatIcon.resetViewAlpha();
		AnimationSet as = new AnimationSet(true);
		AlphaAnimation a = new AlphaAnimation(0f, 1f);
		a.setDuration(300);
		a.setFillAfter(true);
		as.addAnimation(a);
		ScaleAnimation a1 = new ScaleAnimation(0f, 1.5f, 0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		a1.setDuration(150);
		ScaleAnimation a2 = new ScaleAnimation(1.5f, 0.8f, 1.5f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		a2.setDuration(150);
		a2.setStartOffset(150);
		ScaleAnimation a3 = new ScaleAnimation(0.8f, 1f, 0.8f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		a3.setDuration(150);
		a3.setFillAfter(true);
		a3.setStartOffset(300);
		as.addAnimation(a1);
		as.addAnimation(a2);
		as.addAnimation(a3);
		as.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mFloatIcon.setVisibility(View.VISIBLE);
				mFloatIcon.resetViewAlpha();
				mFloatIcon.startViewAlpha();
				mFloatIcon.setTouchable(true);
			}
		});
		mFloatIcon.startAnimation(as);
	}
}
