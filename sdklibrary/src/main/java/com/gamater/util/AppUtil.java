package com.gamater.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.WindowManager;

import com.gamater.common.Config;

public class AppUtil {

	static BroadcastReceiver mBatteryInfoReceiver;

	/**
	 * 获取屏幕分辨率
	 * 
	 * @return
	 */
	public static Point getDisplayScreenResolution(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		android.view.Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(dm);

		int screen_h = 0, screen_w = 0;
		screen_w = dm.widthPixels;
		screen_h = dm.heightPixels;
		return new Point(screen_w, screen_h);
	}

	/**
	 * 单位换算
	 * 
	 * @param fileSize
	 * @return
	 */
	public static String getSizeStr(long fileSize) {
		if (fileSize <= 0) {
			return "0M";
		}
		float result = fileSize;
		String suffix = "M";
		result = result / 1024 / 1024;
		return String.format("%.1f", result) + suffix;
	}

	/**
	 * 初始化一个空{@link Menu}
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Menu newInstanceMenu(Context context) {
		try {
			Class menuBuilder = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Constructor constructor = menuBuilder.getConstructor(Context.class);
			return (Menu) constructor.newInstance(context);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把Bitmap转化成byte的数据流； 还是通过Bitmap.compress()函数转化
	 * 
	 * @param photo
	 * @return
	 */
	public static byte[] getBitmap2Bytes(Bitmap photo) {
		if (photo == null)
			return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 卸载Android应用程序
	 * 
	 * @param packageName
	 */
	public static boolean uninstallApk(String packageName, Context context) {
		if (packageName == null)
			return false;

		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);

		return true;
	}

	public static String getDeviceID(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * 将数字大小转换成“MB"、“KB”、"GB"格式
	 * 
	 * @param size
	 * @return
	 */
	public static String getSize(long size) {
		if (size < 0)
			return null;

		String result = null;
		if (size > 1024 * 1024 * 1024) {
			float f = (float) size / (1024 * 1024 * 1024);
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			else
				result = s;
			return result + "GB";
		} else if (size > 1024 * 1024) {
			float f = (float) size / (1024 * 1024);
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			else
				result = s;
			return result + "MB";
		} else if (size > 1024) {
			float f = (float) size / 1024;
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			else
				result = s;
			return result + "KB";
		} else if (size < 1024) {
			return String.valueOf(size) + "B";
		} else
			return null;
	}

	/**
	 * judge whether <code>str</code> is equal one of <code>strArray</code>.
	 * 
	 * @param str
	 * @param strArray
	 * @return if include, return index of strArray. Else return -1
	 */
	public static int AincludeB(String str, String[] strArray) {
		if (str == null || strArray == null)
			return -1;
		for (int i = 0; i < strArray.length; i++) {
			if (str.equals(strArray[i]))
				return i;
		}
		return -1;
	}

	/**
	 * 将数字大小转换成“MB"、“KB”、"GB"格式
	 * 
	 * @param size
	 * @return
	 */
	public static String getSize(int size) {
		if (size < 0)
			return null;

		String result = null;
		if (size > 1024 * 1024 * 1024) {
			float f = (float) size / (1024 * 1024 * 1024);
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			return result + "GB";
		} else if (size > 1024 * 1024) {
			float f = (float) size / (1024 * 1024);
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			return result + "MB";
		} else if (size > 1024) {
			float f = (float) size / 1024;
			String s = String.valueOf(f);
			if (s.length() - s.indexOf(".") > 2)
				result = s.substring(0, s.indexOf(".") + 3);
			return result + "KB";
		} else if (size < 1024) {
			return String.valueOf(size) + "B";
		} else
			return null;
	}

	/**
	 * 如果是文件夹就删除文件下所有文件，然后删除文件夹，如果是文件就直接删除文件
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public static long del(String filepath) {

		if (filepath == null)
			return -1;

		long total = 0;
		try {

			File f = new File(filepath);// 定义文件路径
			if (!f.exists())
				return -1;
			if (f.isDirectory()) {// 目录
				int i = f.listFiles().length;
				if (i > 0) {
					File delFile[] = f.listFiles();
					for (int j = 0; j < i; j++) {
						if (delFile[j].isDirectory()) {
							// 递归调用del方法并取得子目录路径
							total = total + del(delFile[j].getAbsolutePath());
						}
						total += delFile[j].length();
						delFile[j].delete();// 删除文件
					}
				}
				f.delete();
			} else
				total += f.length();
			if (f.exists())
				f.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	/**
	 * 通过网址打开一个链接
	 * 
	 * @param url
	 *            网址
	 */
	public static void openNetUrl(String url, Context context) {
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
	}

	/**
	 * 判断手机号码是否格式正确
	 * 
	 * @return boolean
	 */
	public static boolean matcherPhoneNum(String telNum) {
		// 匹配11数字，并且13-19开头
		String regex = "^1[3-9]\\d{9}$";
		Pattern pt = Pattern.compile(regex);
		Matcher mc = pt.matcher(telNum);
		return mc.matches();
	}

	/**
	 * 判断密码格式正确
	 * 
	 * @return boolean
	 */
	public static boolean matcherPassword(String psd) {
		// (6-16位字母或数字)
		String regex = "^[a-zA-Z0-9]{6,16}$";
		Pattern pt = Pattern.compile(regex);
		Matcher mc = pt.matcher(psd);
		return mc.matches();
	}

	/**
	 * 判断注册账号格式正确
	 * 
	 * @return boolean
	 */
	public static boolean matcherAccount(String account) {
		// （4-20位字符）
		String regex = "[\\u4e00-\\u9fa5a-zA-Z0-9\\-]{4,20}";
		Pattern pt = Pattern.compile(regex);
		Matcher mc = pt.matcher(account);
		return mc.matches();
	}

	/**
	 * 判断邮箱格式正确
	 * 
	 * @return boolean
	 */
	public static boolean matcherEmail(String email) {
		String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pt = Pattern.compile(regex);
		Matcher mc = pt.matcher(email);
		return mc.matches();
	}

	/**
	 * 根据手机分辨率将dp转为px单位
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机分辨率将px转为dp单位
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 对外分享内容
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void shareText(Context context, String title, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, text);

		List<ResolveInfo> ris = getShareTargets(context);
		// LogUtil.d("aaa", ris.size() + "");
		if (ris != null && ris.size() > 0) {
			context.startActivity(Intent.createChooser(intent, title));
		}
	}

	/**
	 * 手机里具有"分享"功能的所有应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> getShareTargets(Context context) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		PackageManager pm = context.getPackageManager();
		mApps = pm.queryIntentActivities(intent,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		return mApps;
	}

	/**
	 * 获取当前日期字符串（格式yyyy-MM-dd）
	 */
	public static String getTodayDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		return date;
	}

	public enum NetworkConnectedState {
		wifi, mobile, disconnect, other
	}

	/**
	 * 更新网络连接状态，是3g、wifi还是未连接
	 */
	public static NetworkConnectedState getNetworkState(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		// ------------------------- 未连接
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return NetworkConnectedState.disconnect;
		}

		// ------------------------- wifi连接
		State state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED) {
			return NetworkConnectedState.wifi;
		}

		// ------------------------- mobile连接
		state = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED) {
			return NetworkConnectedState.mobile;
		}

		return NetworkConnectedState.other;

	};

	/**
	 * 获取手机串号(IMEI)
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei == null) {
			return "";
		} else {
			return imei;
		}
	}

	/**
	 * 获取用户识别码（IMSI）
	 * 
	 * @param context
	 * @return
	 */
	public static String getSubscriberId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tel = tm.getSubscriberId();
		return TextUtils.isEmpty(tel) ? "" : tel;
	}

	/**
	 * 获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * 获取手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取运营商<br>
	 * 其中46000、46002和46007标识中国移动，46001标识中国联通，46003标识中国电信
	 * 
	 * @param context
	 * @return
	 */
	public static String getMNC(Context context) {
		String providersName = "";
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
			providersName = telephonyManager.getSimOperator();
			providersName = providersName == null ? "" : providersName;
		}
		return providersName;
	}

	/**
	 * 读取RAW文件内容
	 * 
	 * @param context
	 * @param resid
	 * @param encoding
	 * @return
	 */
	public static String getRawFileContent(Context context, int resid,
			String encoding) {
		InputStream is = context.getResources().openRawResource(resid);
		if (is != null) {
			ByteArrayBuffer bab = new ByteArrayBuffer(1024);
			int read;
			try {
				while ((read = is.read()) != -1) {
					bab.append(read);
				}
				return EncodingUtils.getString(bab.toByteArray(), encoding);
			} catch (UnsupportedEncodingException e) {
			} catch (IOException e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return "";
	}

	/**
	 * 获取系统版本，如1.5,2.1
	 * 
	 * @return　SDK版本号
	 */
	public static String getSysVersionName() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取SDK版本号
	 * 
	 * @return
	 */
	public static int getSdkInt() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取包名
	 * 
	 * @return
	 */
	public static String getPackageName(Context context) {
		final String packageName = context.getPackageName();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			return info.packageName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	/**
	 * 获取版本名称
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		final String packageName = context.getPackageName();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public static int getVersionCode(Context context) {
		final String packageName = context.getPackageName();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	/**
	 * MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiMacAddr(Context context) {
		String macAddr = "";
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			macAddr = wifi.getConnectionInfo().getMacAddress();
			if (macAddr == null) {
				macAddr = "";
			}
		} catch (Exception e) {
		}
		return macAddr;
	}

	/**
	 * 屏幕宽高
	 * 
	 * @param context
	 * @return
	 */
	public static int[] getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;

		return new int[] { screenWidth, screenHeight };
	}

	/**
	 * 屏幕宽高，字符串形式
	 * 
	 * @param context
	 * @return
	 */
	public static String getScreenSizeStr(Context context) {
		int[] screenSize = getScreenSize(context);
		return screenSize[0] + "*" + screenSize[1];
	}

	/**
	 * 创建快捷方式
	 * 
	 * @param context
	 * @param shortCutName
	 * @param iconId
	 * @param presentIntent
	 */
	public static void createShortcut(Context context, String shortCutName,
			int iconId, Intent presentIntent) {
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra("duplicate", false);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context, iconId));
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, presentIntent);

		context.sendBroadcast(shortcutIntent);
	}

	/**
	 * 获取安装包信息
	 * 
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageArchiveInfo(filePath, 0);
			return pi;
		} else {
			return null;
		}
	}

	/**
	 * 获取应用名称
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getAppName(Context context, String packageName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	public static String getLanguage(Context c) {
		if (c != null)
			return c.getResources().getConfiguration().locale.getLanguage();
		return null;
	}

	/**
	 * 获取Mac 地址
	 * 
	 * @return
	 */
	public String getMacAddress(Context c) {
		WifiManager wifi = (WifiManager) c
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String mac = info.getMacAddress();
		if (mac == null) {
			return "";
		}
		mac = mac.replaceAll(":", "");

		return mac.toLowerCase();
	}

	public static String getStringMetaData(Context context, String key) {
		Bundle metaData = getMetaData(context);
		String strVal = metaData != null ? metaData.getString(key) : null;
		if (strVal == null || "".equals(strVal)) {
			throw new IllegalArgumentException("please define " + key
					+ " in your AndroidManifest.xml");
		}
		return strVal != null ? strVal : "";
	}

	private static Bundle getMetaData(Context context) {
		if (context == null) {
			return null;
		}

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 128);
			if (appInfo != null)
				return appInfo.metaData;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean checkApkInstall(Context context, String packageName) {
		boolean bInstall = false;
		if (packageName == null || "".equals(packageName))
			return bInstall;
		try {
			ApplicationInfo info = null;
			info = context.getPackageManager().getApplicationInfo(packageName,
					0);
			if (info != null) {
				bInstall = true;
			}
			return bInstall;

		} catch (NameNotFoundException e) {
			return bInstall;
		}
	}

	public static String getGooglePlayPackageName() {
		String GooglePlayPackageName = "com.android.vending";
		return GooglePlayPackageName;
	}

	public static String getAndroidId(Context context) {
		String androidId = android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		return androidId;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String getTimeLineString(Resources res, Long time) {
		if (time == null || time == 0) {
			return "";
		}
		long margin = System.currentTimeMillis() / 1000 - time;
		if (0 <= margin && margin < 1 * 60 * 60) {
			if (margin < 60)
				margin = 60;
			return res.getString(ResourceUtil.getStringId("vsgm_tony_min"),
					margin / 60);
		} else if (1 * 60 * 60 <= margin && margin < 24 * 60 * 60) {
			return res.getString(ResourceUtil.getStringId("vsgm_tony_hour"),
					margin / 3600);
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(new Date(time * 1000));
		}
	}

	public static String getSimpleNumberString(int num) {
		return getSimpleNumberString((long) num);
	}

	public static String getSimpleNumberString(long num) {
		if (num / 10000 == 0) {
			return num + "";
		}
		return num / 1000 + "K";
	}

	public static String getReleasePlatform() {
		String releasePlatform = "sgmala";
		if (Config.isGmLogo) {
			if (Config.gmTitle.toLowerCase().contains("mix")) {
				releasePlatform = "Mix";
			} else {
				releasePlatform = "vn";
			}
		}
		return releasePlatform;
	}

	// 获取 meta-data 的值
	public static int GetMetaDataInt(Context context, String name) {
		int Value = -1;
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Value = appInfo.metaData.getInt(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return Value;
	}

	public static String GetMetaDataString(Context context, String name) {
		String s = "";
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			s = appInfo.metaData.getString(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	}
}
