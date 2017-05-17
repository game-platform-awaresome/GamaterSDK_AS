package com.tony.view;

import com.gamater.util.AppUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class WarningLinearLayout extends LinearLayout {
	public WarningLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public WarningLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WarningLinearLayout(Context context) {
		super(context);
	}

	private boolean shouldShowWarning;
	private int warningAlpha;
	private int alphaSpeed = 30;

	private Runnable refreshRunnable = new Runnable() {
		@Override
		public void run() {
			postInvalidate();
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (shouldShowWarning || warningAlpha > 0) {
			if (shouldShowWarning) {
				warningAlpha += alphaSpeed;
			} else {
				warningAlpha -= alphaSpeed;
			}
			if (warningAlpha > 255) {
				warningAlpha = 255;
				alphaSpeed = 0;
				postDelayed(new Runnable() {
					@Override
					public void run() {
						shouldShowWarning = false;
						alphaSpeed = 30;
						postInvalidate();
					}
				}, 1500);
				TranslateAnimation a = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.007f,
						Animation.RELATIVE_TO_SELF, 0.007f, Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				a.setDuration(50);
				a.setRepeatCount(6);
				a.setRepeatMode(Animation.REVERSE);
				startAnimation(a);
			}
			if (warningAlpha < 0)
				warningAlpha = 0;
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.argb(warningAlpha, 255, 0, 0));
			int stroke = AppUtil.dip2px(getContext(), 3);
			paint.setStrokeWidth(stroke);
			paint.setStyle(Paint.Style.STROKE);
			RectF r = new RectF();
			r.left = 0;
			r.top = 0;
			r.right = getWidth();
			r.bottom = getHeight();
			int corner = AppUtil.dip2px(getContext(), 2);
			canvas.drawRoundRect(r, corner, corner, paint);
			if (warningAlpha < 255 && warningAlpha > 0) {
				postDelayed(refreshRunnable, 20);
			}
		}
	}

	public void makeWarning() {
		shouldShowWarning = true;
		warningAlpha = 0;
		postInvalidate();
	}
}
