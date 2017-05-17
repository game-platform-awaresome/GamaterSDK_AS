package com.tony.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationValue extends Animation {
	private AnimationCallback mCallback;

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		if (mCallback != null) {
			mCallback.animationUpdate(interpolatedTime);
		}
		super.applyTransformation(interpolatedTime, t);
	}

	public void setAnimationCallback(AnimationCallback animationCallback) {
		mCallback = animationCallback;
	}

	public interface AnimationCallback {
		public void animationUpdate(float interpolatedTime);
	}
}
