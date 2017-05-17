package com.gamater.sdk.imagepicker;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.tony.view.ImagePickerView;
import com.tony.view.ImagePickerView.ImagePickerViewListener;

public class ImagePickerDialog extends Dialog {
	private Activity mActivity;
	private ImagePickerView mView;
	private ImagePickerViewListener mListener;
	private int maxCount;

	public ImagePickerDialog(Activity activity, int maxSelect,
			ImagePickerViewListener listener) {
		super(activity, android.R.style.Theme_Black_NoTitleBar);
		mActivity = activity;
		mListener = listener;
		this.maxCount = maxSelect;
	}

	public ImagePickerDialog(Activity activity, ImagePickerViewListener listener) {
		this(activity, 0, listener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window dialogWindow = getWindow();
		dialogWindow.setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		setCancelable(false);
		setupView();
	}

	private void setupView() {
		mView = ImagePickerHelper.getInstance().getImagePickerView(mActivity,
				maxCount);
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = mActivity.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mActivity
				.getResources().getDisplayMetrics().heightPixels - sbar);
		addContentView(mView, lp);
		mView.setFocusable(true);
		mView.requestFocus();
		mView.setImagePickerListener(new ImagePickerViewListener() {
			@Override
			public void onCancel() {
				dismiss();
				if (mListener != null)
					mListener.onCancel();
			}

			@Override
			public void onPick(String... path) {
				dismiss();
				if (mListener != null)
					mListener.onPick(path);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			mView.onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
