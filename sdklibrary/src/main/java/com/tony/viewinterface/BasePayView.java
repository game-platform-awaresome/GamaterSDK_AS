package com.tony.viewinterface;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public interface BasePayView {
	Animation getStartAnimation(AnimationListener listener);

	Animation getEndAnimation(AnimationListener listener);

	boolean doViewGoBack();

	void onCreate();

	void onReturn();

	void onDestory();

	CharSequence getViewTitle();

	boolean interceptOnBackEvent();

	boolean isShowBackBtn();

	Object getClassTag();

	RelativeLayout.LayoutParams getSdkViewLayoutParams();

	boolean isCoinsChargeView();
}
