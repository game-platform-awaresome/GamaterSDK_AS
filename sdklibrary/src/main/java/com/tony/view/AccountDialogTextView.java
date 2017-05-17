package com.tony.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AccountDialogTextView extends TextView {
	public AccountDialogTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AccountDialogTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AccountDialogTextView(Context context) {
		super(context);
	}

	private Drawable drawableTop;

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		if (top != null) {
			drawableTop = top;
			return;
		}
	}

	private void resetTextSize() {
		if (getText() != null && getWidth() > 0) {
			Paint paint = new Paint();
			paint.set(getPaint());
			float trySize = getTextSize();
			paint.setTextSize(trySize);
			while (paint.measureText(getText().toString()) > getWidth() - 8) {
				trySize -= 1;
				paint.setTextSize(trySize);
			}
			setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
		}
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		resetTextSize();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (drawableTop != null) {
			int size = (int) (getHeight() * 0.40);
			drawableTop.setBounds(0, 0, size, size);
			super.setCompoundDrawables(null, drawableTop, null, null);
		}
		resetTextSize();
	}

}
