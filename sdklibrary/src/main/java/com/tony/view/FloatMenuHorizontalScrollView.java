package com.tony.view;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

public class FloatMenuHorizontalScrollView extends HorizontalScrollView {

	private OverScroller mScroller;
	private boolean isInTouch;
	public FloatMenuHorizontalScrollView(Context context) {
		super(context);
		setScroller(context);
	}

	public FloatMenuHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScroller(context);
	}

	public FloatMenuHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setScroller(context);
	}

	@SuppressLint("NewApi")
	public FloatMenuHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setScroller(context);
	}

	private void setScroller(Context context) {
		mScroller = new OverScroller(context);
		try {
			Field field = getClass().getDeclaredField("mScroller");
			field.setAccessible(true);
			field.set(this, mScroller);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void checkScrollStop() {
		boolean mIsBeingDragged = false;
		try {
			Field field = getClass().getDeclaredField("mIsBeingDragged");
			field.setAccessible(true);
			mIsBeingDragged = field.getBoolean(this);
		} catch (Exception e) {
		}
		if (mScroller.isFinished() && !mIsBeingDragged && !isInTouch) {
			if (listener != null && mScroller.isFinished()) {
				listener.onFinish();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = super.onTouchEvent(ev);
		final int actionMasked = ev.getActionMasked();
		switch (actionMasked) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			isInTouch = true;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			isInTouch = false;
			checkScrollStop();
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		checkScrollStop();
	}

	private ScrollFinishListener listener;

	public void setScrollFinishListener(ScrollFinishListener listener) {
		this.listener = listener;
	}

	public interface ScrollFinishListener {
		void onFinish();
	}
}
