package com.tony.viewinterface;

import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public interface BaseSdkView {
	LayoutParams getSdkViewLayoutParams();

	Animation getStartAnimation(AnimationListener listener);

	Animation getEndAnimation(AnimationListener listener);

	boolean doViewGoBack();

	void initView();

	String getViewTitle();

	boolean interceptOnBackEvent();

	boolean isMenuEnable();
}
