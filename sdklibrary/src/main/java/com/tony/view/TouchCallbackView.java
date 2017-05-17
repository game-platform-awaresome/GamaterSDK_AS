package com.tony.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchCallbackView extends View {

	public TouchCallbackView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TouchCallbackView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchCallbackView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (callback != null)
			callback.onTouch(event);
		return false;
	}

	private TouchCallback callback;

	public void setTouchCallback(TouchCallback c) {
		this.callback = c;
	}

	public interface TouchCallback {
		void onTouch(MotionEvent event);
	}
}
