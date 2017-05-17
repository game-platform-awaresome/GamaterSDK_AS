package com.tony.view;

import com.gamater.util.ResourceUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class PaySelectRadioButton extends RadioButton {

	public PaySelectRadioButton(Context context) {
		super(context);
	}

	public PaySelectRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PaySelectRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public int dip2px(float dpValue) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@SuppressLint("NewApi")
	public PaySelectRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		if (left != null) {
			left.setBounds(0, 0, dip2px(40), dip2px(40));
		}
		if (top != null) {
			top.setBounds(0, 0, dip2px(40), dip2px(40));
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}

	private boolean isNew = false;
	private Drawable drawableNew;

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawableWidth = 0;
				drawableWidth = drawableLeft.getBounds().right - drawableLeft.getBounds().left;
				int drawablePadding = (int) (getWidth() / 3 - (textWidth + drawableWidth) / 2);
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				canvas.translate(getWidth() / 2 - bodyWidth + textWidth / 2, 0);
				setCompoundDrawablePadding(drawablePadding);
			}
		}
		super.onDraw(canvas);
		Rect rec = canvas.getClipBounds();
		if (isNew) {
			Drawable drawable = getDrawableNew();
			drawable.setBounds(rec.right - dip2px(28), rec.top, rec.right, rec.top + dip2px(28));
			drawable.draw(canvas);
		}
		if (isChecked()) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.parseColor("#8FC31F"));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth((float) dip2px(6));
			canvas.drawRect(rec, paint);

			Path path = new Path();
			path.moveTo(rec.right, rec.bottom);
			path.lineTo(rec.right - dip2px(32), rec.bottom);
			path.lineTo(rec.right, rec.bottom - dip2px(28));
			path.close();
			paint.setStyle(Paint.Style.FILL);
			canvas.drawPath(path, paint);

			Path pathL = new Path();
			pathL.moveTo(rec.right - dip2px(16), rec.bottom - dip2px(9));
			pathL.lineTo(rec.right - dip2px(12), rec.bottom - dip2px(5));
			pathL.lineTo(rec.right - dip2px(4), rec.bottom - dip2px(13));
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth((float) dip2px(2));
			canvas.drawPath(pathL, paint);
		}
	}

	private Drawable getDrawableNew() {
		if (drawableNew == null) {
			drawableNew = getResources().getDrawable(ResourceUtil.getDrawableId("vsgm_tony_icon_pay_new"));
		}
		return drawableNew;
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		postInvalidate();
	}

}
