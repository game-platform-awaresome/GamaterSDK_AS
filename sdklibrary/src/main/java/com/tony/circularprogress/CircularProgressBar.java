package com.tony.circularprogress;

import com.gamater.util.AppUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CircularProgressBar extends ProgressBar {

	public CircularProgressBar(Context context) {
		this(context, null);
	}

	public CircularProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.style.Widget_ProgressBar);
	}

	public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final int color = Color.parseColor("#ff6e07");
		final float strokeWidth = AppUtil.dip2px(context, 4);
		final float sweepSpeed = 1f;
		final float rotationSpeed = 1f;

		final int minSweepAngle = 20;
		final int maxSweepAngle = 300;

		Drawable indeterminateDrawable;
		CircularProgressDrawable.Builder builder = new CircularProgressDrawable.Builder(
				context).sweepSpeed(sweepSpeed).rotationSpeed(rotationSpeed)
				.strokeWidth(strokeWidth).minSweepAngle(minSweepAngle)
				.maxSweepAngle(maxSweepAngle);

		builder.color(color);

		indeterminateDrawable = builder.build();
		setIndeterminateDrawable(indeterminateDrawable);
	}

	private CircularProgressDrawable checkIndeterminateDrawable() {
		Drawable ret = getIndeterminateDrawable();
		if (ret == null || !(ret instanceof CircularProgressDrawable))
			throw new RuntimeException(
					"The drawable is not a CircularProgressDrawable");
		return (CircularProgressDrawable) ret;
	}

	public void progressiveStop() {
		checkIndeterminateDrawable().progressiveStop();
	}

	public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
		checkIndeterminateDrawable().progressiveStop(listener);
	}

	public void start() {
		checkIndeterminateDrawable().start();
	}

	public void stop() {
		progressiveStop();
	}
}
