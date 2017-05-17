package com.gamater.sdk.imagepicker;

import com.tony.view.ImagePickerView;
import com.tony.view.ImagePickerView.ImagePickerViewListener;
import com.vsgm.sdk.PermissionUtil;

import android.Manifest;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

public class ImagePickerHelper {
	private static ImagePickerHelper mInstance;
	private ImagePickerView mImagePickerView;

	private ImagePickerHelper() {
	}

	public static ImagePickerHelper getInstance() {
		if (mInstance == null) {
			mInstance = new ImagePickerHelper();
		}
		return mInstance;
	}

	public ImagePickerView getImagePickerView(Activity activity) {
		return getImagePickerView(activity, 0);
	}

	public ImagePickerView getImagePickerView(Activity activity, int maxCount) {
		mImagePickerView = new ImagePickerView(activity, maxCount);
		return mImagePickerView;
	}

	public void invokeImagePicker(final Activity activity, final ImagePickerViewListener l) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ImagePickerDialog d = new ImagePickerDialog(activity, l);
				d.show();
			}
		});
	}

	public void invokeImagePicker(final Activity activity, final ImagePickerViewListener l, final int maxPickCount) {
		if (PermissionUtil.checkPermission(activity,
				Manifest.permission.READ_EXTERNAL_STORAGE)) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ImagePickerDialog d = new ImagePickerDialog(activity,
							maxPickCount, l);
					d.show();
				}
			});
		} else {
			PermissionUtil.requestAppPermissions(activity);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					invokeImagePicker(activity, l, maxPickCount);
				}
			}, 1000);
		}
	}
}
