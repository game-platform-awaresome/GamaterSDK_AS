package com.vsgm.permission;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.gamater.common.Config;
import com.gamater.sdk.common.WinType;
import com.gamater.sdk.game.MVMainActivity;
import com.gamater.util.LogUtil;
import com.vsgm.sdk.SDKActivity;

public class PermissionManager {
	private static final int REQUEST_CODE_PERMISSION = 0x38;
	private static PermissionManager instance;
	private Context context;
    private Activity activity;
	private String[] permissions;
    private PermissionCallback callback;
	private final List<String> mDeniedPermissions = new LinkedList<String>();
	private final Set<String> manifestPermissions = new HashSet<String>(1);

	private PermissionManager(Context context) {
        this.context = context;
        getManifestPermissions();
    }

	public static PermissionManager getInstance(Context context) {
		if (instance == null) {
			if (context != null) {
				instance = new PermissionManager(
						context.getApplicationContext());
			}
		}
		return instance;
	}

    private synchronized void getManifestPermissions() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
            		context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String perm : permissions) {
                    manifestPermissions.add(perm);
                }
            }
        }
    }

	public synchronized void request(String[] permissions,
			PermissionCallback listener) {
		this.callback = listener;
		this.permissions = permissions;
        checkSelfPermission();
    }

	public synchronized void requestAll(PermissionCallback listener) {
		getManifestPermissions();
		request(manifestPermissions.toArray(new String[manifestPermissions
				.size()]), listener);
	}

    private synchronized void checkSelfPermission() {
        mDeniedPermissions.clear();
		if (Build.VERSION.SDK_INT < 23) {
			callback.onGranted();
            onDestroy();
            return;
        }
        for (String permission : permissions) {
			if (manifestPermissions.contains(permission)) {
				int checkSelfPermission = PermissionUtil.checkSelfPermission(
						context, permission);
                if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                    mDeniedPermissions.add(permission);
                }
            }
        }
        if (mDeniedPermissions.isEmpty()) {
			callback.onGranted();
            onDestroy();
            return;
        }
        startAcpActivity();
    }

    private synchronized void startAcpActivity() {
		Intent intent = new Intent(context,
				Config.isGmLogo ? SDKActivity.class : MVMainActivity.class);
		intent.putExtra(MVMainActivity.WIN_TYPE, WinType.Permission.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
    }

	public synchronized void checkRequestPermissionRationale(Activity activity) {
		this.activity = activity;
        boolean shouldShowRational = false;
        for (String permission : mDeniedPermissions) {
			shouldShowRational = shouldShowRational
					|| PermissionUtil.shouldShowRequestPermissionRationale(
							activity, permission);
        }
        String[] permissions = mDeniedPermissions.toArray(new String[mDeniedPermissions.size()]);
		requestPermissions(permissions);
    }

    private synchronized void requestPermissions(String[] permissions) {
		PermissionUtil.requestPermissions(activity, permissions,
				REQUEST_CODE_PERMISSION);
    }

	public synchronized void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
			LinkedList<String> grantedPermissions = new LinkedList<String>();
			LinkedList<String> deniedPermissions = new LinkedList<String>();
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				LogUtil.printHTTP(permission + " " + grantResults[i]);
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
					grantedPermissions.add(permission);
				else
					deniedPermissions.add(permission);
			}
			if (!grantedPermissions.isEmpty() && deniedPermissions.isEmpty()) {
				callback.onGranted();
				onDestroy();
			}
			break;
        }
    }

    private void onDestroy() {
		if (activity != null) {
			activity.finish();
			activity = null;
		}
    }

    synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (callback == null) {
            onDestroy();
            return;
        }
        checkSelfPermission();
    }
}
