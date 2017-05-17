package com.gamater.sdk.facebook;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.gamater.sdk.facebook.FbFriendPickerView.FbFriendPickerViewListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class FbFriendPickerDialog extends Dialog {
	private Activity mActivity;
	private FbFriendPickerView mView;
	private FbFriendPickerViewListener mListener;
	private ArrayList<FbInviteFriend> mItems;

	public FbFriendPickerDialog(Activity activity,
			ArrayList<FbInviteFriend> items, FbFriendPickerViewListener listener) {
		super(activity, android.R.style.Theme_Black_NoTitleBar);
		mActivity = activity;
		mListener = listener;
		mItems = items;
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

		mView = new FbFriendPickerView(mActivity, mItems);
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
		mView.setFbFriendPickerViewListener(new FbFriendPickerViewListener() {
			@Override
			public void onCancel() {
				dismiss();
				if (mListener != null)
					mListener.onCancel();
			}

			@Override
			public void onPick(String[] id, String[] name) {
				dismiss();
				if (mListener != null)
					mListener.onPick(id, name);
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
