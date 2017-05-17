package com.tony.view;

import com.gamater.util.AppUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class SdkTipsTextView extends TextView {
	public final static String TIP_VIEW_TAG = "122454124";

	public SdkTipsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SdkTipsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SdkTipsTextView(Context context) {
		super(context);
	}

	private CharSequence originString;
	private String error;
	private int errorAlpha;
	private int alphaSpeed = 20;

	private Runnable refreshRunnable = new Runnable() {
		@Override
		public void run() {
			postInvalidate();
		}
	};

	private Runnable hideRunnable = new Runnable() {
		@Override
		public void run() {
			alphaSpeed = -20;
			postInvalidate();
		}
	};

	@Override
	public void setText(CharSequence text, BufferType type) {
		originString = text;
		setTag(TIP_VIEW_TAG);
		super.setText(text, type);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (error != null) {
			errorAlpha += alphaSpeed;
			if (errorAlpha > 255) {
				errorAlpha = 255;
				alphaSpeed = 0;
				postDelayed(hideRunnable, 1500);
			} else if (errorAlpha < 0) {
				errorAlpha = 0;
			}

			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.argb(errorAlpha, 255, 0, 0));
			RectF r = new RectF();
			r.left = 0;
			r.top = 0;
			r.right = getWidth();
			r.bottom = getHeight();
			int corner = AppUtil.dip2px(getContext(), 2);
			canvas.drawRoundRect(r, corner, corner, paint);

			Rect targetRect = new Rect(0, 0, getWidth(), getHeight());
			paint.setStrokeWidth(3);
			float trySize = getTextSize();
			paint.setTextSize(trySize);
			while (paint.measureText(error) > getWidth() - 20) {
				trySize -= 1;
				paint.setTextSize(trySize);
			}
			paint.setColor(Color.argb(errorAlpha, 255, 255, 255));
			paint.setStyle(Paint.Style.FILL);
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(error, targetRect.centerX(), baseline, paint);

			if (errorAlpha == 255 && alphaSpeed == 0) {
				return;
			}
			if (errorAlpha < 255 && errorAlpha > 0) {
				postDelayed(refreshRunnable, 20);
			} else if (errorAlpha == 255) {

			} else {
				error = null;
				errorAlpha = 0;
				alphaSpeed = 20;
				setClickable(true);
				super.setText(originString, BufferType.NORMAL);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (error != null) {
			if (removeCallbacks(hideRunnable)) {
				postDelayed(new Runnable() {
					@Override
					public void run() {
						hideError();
					}
				}, 100);
			}
		}
		return super.onTouchEvent(event);
	}

	public void hideError() {
		error = null;
		errorAlpha = 0;
		alphaSpeed = 20;
		postInvalidate();
		setClickable(true);
		super.setText(originString, BufferType.NORMAL);
	}

	public void showError(String error) {
		this.error = error;
		super.setText("", BufferType.NORMAL);
		setClickable(false);
		postDelayed(refreshRunnable, 20);
	}
}
