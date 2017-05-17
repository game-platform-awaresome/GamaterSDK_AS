package com.vsgm.permission;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
	static int checkSelfPermission(Context context, String permission) {
		return ContextCompat.checkSelfPermission(context, permission);
	}

	/**
	 * 向系统请求权限
	 *
	 * @param activity
	 * @param permissions
	 * @param requestCode
	 */
	static void requestPermissions(Activity activity, String[] permissions,
			int requestCode) {
		ActivityCompat.requestPermissions(activity, permissions, requestCode);
	}

	/**
	 * 检查权限是否存在拒绝不再提示
	 *
	 * @param activity
	 * @param permission
	 * @return
	 */
	static boolean shouldShowRequestPermissionRationale(Activity activity,
			String permission) {
		boolean rationale = ActivityCompat
				.shouldShowRequestPermissionRationale(activity, permission);
		return rationale;
	}
}
