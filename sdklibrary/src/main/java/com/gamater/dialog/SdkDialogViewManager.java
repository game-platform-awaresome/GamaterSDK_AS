package com.gamater.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.tony.viewinterface.BaseSdkView;

public class SdkDialogViewManager {
	private static SdkDialogViewManager instance;
	private SdkGameDialog gameDialog;

	private SdkDialogViewManager() {
	}

	public static SdkDialogViewManager getManager() {
		if (instance == null)
			instance = new SdkDialogViewManager();
		return instance;
	}

	public static void hideSoftInput(Context ctx) {
		if (instance != null && instance.gameDialog != null) {
			instance.gameDialog.hideSoftInput(ctx);
		}
	}

	public static boolean isManagerReady() {
		if (instance != null && instance.gameDialog != null) {
			return true;
		}
		return false;
	}

	public void init(SdkGameDialog gameDialog) {
		if (this.gameDialog == null) {
			this.gameDialog = gameDialog;
		}
	}

	public static void destory() {
		if (instance != null) {
			instance.gameDialog = null;
			instance = null;
		}
	}

	public static Activity getOwnerActivity() {
		if (instance != null && instance.gameDialog != null)
			return instance.gameDialog.getOwnerActivity();
		return null;
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void doBackPressed() {
		if (instance != null && instance.gameDialog != null)
			instance.gameDialog.onBackPressed();
	}

	public static void showLoadingView() {
		if (instance != null && instance.gameDialog != null)
			instance.gameDialog.showLoadingView();
	}

	public static void hideLoadingView() {
		if (instance != null && instance.gameDialog != null)
			instance.gameDialog.hideLoadingView();
	}

	public static void dialogDismiss() {
		if (instance != null && instance.gameDialog != null)
			instance.gameDialog.dismiss();
	}

	public static boolean doAddView(View baseSdkView) {
		if (instance != null && instance.gameDialog != null
				&& baseSdkView instanceof BaseSdkView) {
			instance.gameDialog.addBaseSdkView(baseSdkView);
			instance.gameDialog.updateView((BaseSdkView) baseSdkView);
			return true;
		}
		return false;
	}

	public static void addBaseSdkView(final ViewGroup parentView,
			final View subView) {
		if (parentView.getChildCount() > 0) {
			final View oldView = parentView.getChildAt(parentView
					.getChildCount() - 1);
			if (oldView.getClass().getName()
					.equals(subView.getClass().getName())) {
				return;
			}
			long duration = showEndAnimation(
					parentView.getChildAt(parentView.getChildCount() - 1), null);
			parentView.postDelayed(new Runnable() {
				@Override
				public void run() {
					parentView.addView(subView,
							((BaseSdkView) subView).getSdkViewLayoutParams());
					showStartAnimation(subView, null);
					oldView.setVisibility(View.GONE);
				}
			}, duration / 2);
		}
	}

	public static boolean doViewBackPressed(final ViewGroup parent) {
		if (parent == null) {
			return false;
		} else if (parent instanceof BaseSdkView
				&& ((BaseSdkView) parent).doViewGoBack()) {
			return true;
		} else if (parent.getChildCount() > 1) {
			final View firstView = parent
					.getChildAt(parent.getChildCount() - 1);
			final View secondView = parent
					.getChildAt(parent.getChildCount() - 2);
			long duration = showEndAnimation(
					parent.getChildAt(parent.getChildCount() - 1), null);
			parent.postDelayed(new Runnable() {
				@Override
				public void run() {
					secondView.setVisibility(View.VISIBLE);
					showStartAnimation(secondView, null);
					firstView.setVisibility(View.GONE);
					parent.removeView(firstView);
					instance.gameDialog.updateView((BaseSdkView) secondView);
				}
			}, duration / 2);
			return true;
		}
		return false;
	}

	private static void showStartAnimation(View view, AnimationListener listener) {
		try {
			view.startAnimation(((BaseSdkView) view)
					.getStartAnimation(listener));
		} catch (Exception e) {
		}
	}

	private static long showEndAnimation(View view, AnimationListener listener) {
		try {
			Animation animation = ((BaseSdkView) view)
					.getEndAnimation(listener);
			view.startAnimation(animation);
			return animation.getDuration();
		} catch (Exception e) {
		}
		return 0;
	}
}
