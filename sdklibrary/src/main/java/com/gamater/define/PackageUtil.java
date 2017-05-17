package com.gamater.define;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

public class PackageUtil {
	public static List<ApplicationInfo> getInstallAppInfo(Context context) {
		PackageManager mypm = context.getPackageManager();
		List<ApplicationInfo> appInfoList = mypm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(appInfoList,
				new ApplicationInfo.DisplayNameComparator(mypm));

		return appInfoList;
	}

	public static JSONArray getThirdAppInfo(Context context) {
		List<ApplicationInfo> appList = getInstallAppInfo(context);
		JSONArray array = new JSONArray();
		for (ApplicationInfo app : appList) {
			// if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
			// array.put(app.packageName);
			// } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)
			// != 0) {
			array.put("" + app.packageName);
			// }
		}
		return array;
	}

	public static boolean isHasPackage(Context c, String packageName) {
		if (null == c || null == packageName)
			return false;

		boolean bHas = true;
		try {
			c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
		} catch (Exception e) {
			bHas = false;
		}
		return bHas;
	}

	public static Intent getLaunchIntentForPackage(Context context, String pkgname) {
		Intent intent = null;
		try {
			PackageManager pm = context.getPackageManager();
			intent = pm.getLaunchIntentForPackage(pkgname);
			intent.putExtra("key_from", context.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return intent;
	}

	public static void openApp(Context context, String packageName) {
		Intent intent = getLaunchIntentForPackage(context, packageName);
		if (intent != null)
			startActivity(context, intent);
	}

	// 必须都使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
	public static boolean startActivity(Context context, Intent intent) {
		boolean bResult = true;
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			bResult = false;
		} catch (SecurityException e) {
			bResult = false;
		} catch (NullPointerException e) {
			bResult = false;
		} catch (IllegalStateException e) {
			bResult = false;
		}
		return bResult;
	}

	public static void go2GooglePlay(Context context, String url) {
		if (context != null && !TextUtils.isEmpty(url)) {
			if (isGooglePlayUrl(url)) {
				openGooglePlayByUrl(url, context);
			} else {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				startActivity(context, intent);
			}

		}
	}

	/*
	 * 优先GP跳转，如果GP没有的话就让用户去选择跳转的方式
	 */
	public static void openGooglePlayByUrl(String url, Context context) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		boolean gpSuccess = false;
		if (isGPAvailable(context)) {
			gpSuccess = startGooglePlayByUrl(url, context);
		}
		if (!gpSuccess) {
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(context, it);
		}
	}

	public static boolean startGooglePlayByUrl(String url, Context context) {
		if (url == null || url.length() == 0) {
			return false;
		}

		try {
			Intent it = new Intent(Intent.ACTION_VIEW);
			it.setPackage("com.android.vending");
			if (!(context instanceof Activity)) {
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			} else {
				it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			}

			it.setData(Uri.parse(url));

			return startActivity(context, it);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isGPAvailable(Context ctx) {
		if (!isHasPackage(ctx, "com.android.vending")) {
			return false;
		}

		PackageInfo gsfInfo = getPackageInfo(ctx, "com.google.android.gsf");
		if (null == gsfInfo) {
			return false;
		}

		return true;
	}

	public static PackageInfo getPackageInfo(Context c, String packageName) {
		if (null == c || null == packageName)
			return null;

		PackageInfo info = null;
		try {
			info = c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
		} catch (Exception e) {
			return null;
		}
		return info;
	}

	public static boolean isGooglePlayUrl(String url) {
		if (TextUtils.isEmpty(url))
			return false;
		if (url.startsWith("https://play.google.com") || url.startsWith("http://play.google.com") || url.startsWith("market:")) {
			return true;
		}
		return false;
	}

}
