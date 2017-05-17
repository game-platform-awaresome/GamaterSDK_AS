package com.tony.circularprogress;

import android.view.animation.Interpolator;

class Options {
	final Interpolator angleInterpolator;
	final Interpolator sweepInterpolator;
	final float borderWidth;
	final int[] colors;
	final float sweepSpeed;
	final float rotationSpeed;
	final int minSweepAngle;
	final int maxSweepAngle;
	final int style;

	public Options(Interpolator angleInterpolator,
			Interpolator sweepInterpolator, float borderWidth, int[] colors,
			float sweepSpeed, float rotationSpeed, int minSweepAngle,
			int maxSweepAngle, int style) {
		this.angleInterpolator = angleInterpolator;
		this.sweepInterpolator = sweepInterpolator;
		this.borderWidth = borderWidth;
		this.colors = colors;
		this.sweepSpeed = sweepSpeed;
		this.rotationSpeed = rotationSpeed;
		this.minSweepAngle = minSweepAngle;
		this.maxSweepAngle = maxSweepAngle;
		this.style = style;
	}

}
