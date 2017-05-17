package com.tony.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BaseSdkBgLinearLayout extends LinearLayout {

	public BaseSdkBgLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setWillNotDraw(false);
	}

	public BaseSdkBgLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}

	public BaseSdkBgLinearLayout(Context context) {
		super(context);
		setWillNotDraw(false);
	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		int widht = getWidth();
//		int height = getHeight();
//		int padding = dip2px(12);
//		int minLineLength = dip2px(42);
//		paint.setColor(Color.parseColor("#50000000"));
//		canvas.drawRect(padding, padding, widht - padding, height - padding,
//				paint);
//		paint.setColor(Color.parseColor("#B0B0B0"));
//		float[] pts = { 0, padding, widht,
//				padding, // 上横线
//				0, height - padding, widht,
//				height - padding, // 下横线
//				padding, 0, padding,
//				minLineLength,// 左上竖线
//				padding, height - minLineLength, padding,
//				height,// 左下竖线
//				widht - padding, 0, widht - padding,
//				minLineLength, // 右上竖线
//				widht - padding, height - minLineLength, widht - padding,
//				height // 右下竖线
//		};
//		paint.setStrokeWidth((float) dip2px(1)); // 线宽
//		canvas.drawLines(pts, paint);
	}

}
