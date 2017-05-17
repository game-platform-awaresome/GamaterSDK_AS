package com.gamater.define;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;

import com.gamater.common.http.MD5;
import com.gamater.util.ResourceUtil;

/**
 * 獲取設備資料的類
 * 
 * @author tony
 */
public class DeviceInfo {
	private static DeviceInfo instance;

	private Context mContext;

	private int mScreenWidth;

	private int mScreenHeight;

	public static DeviceInfo getInstance(Context context) {
		if (instance == null) {
			instance = new DeviceInfo(context);
		}
		ResourceUtil.init(context);
		return instance;
	}

	private DeviceInfo(Context a) {
		this.mContext = a;
		try {
			initDisplaySize((Activity) a);
		} catch (Exception e) {
		}
	}

	private static String gid = "";

	public static String getGId() {
		return gid;
	}

	public Context getContext() {
		return mContext;
	}

	public String getAndroidId() {
		String androidId = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
		if (androidId != null) {
			return androidId;
		} else {
			return "";
		}
	}

	private String appVersionCode = "";

	public String getAppVersionCode() {
		if (appVersionCode.length() > 0)
			return appVersionCode;
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					mContext.getPackageName(), 0);
			appVersionCode = info.versionCode + "";
		} catch (Exception e) {
		}
		return appVersionCode;
	}

	private String appVersionName = "";

	public String getappVersionName() {
		if (appVersionName.length() > 0)
			return appVersionName;
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					mContext.getPackageName(), 0);
			appVersionName = info.versionName;
		} catch (Exception e) {
		}
		return appVersionName;
	}

	/**
	 * IMEI只有手機設備才有，其他設備（如平板）返回null
	 * 
	 * @return
	 */
	public String getIMEI() {
		try {
			TelephonyManager tm = (TelephonyManager) instance.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			return imei == null ? "" : imei;
		} catch (Exception e) {
		}
		return "";
	}

	public String getPhoneNumber() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) instance
					.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			String phone = telephonyManager.getLine1Number();
			return phone == null ? "" : phone;
		} catch (Exception e) {
		}
		return "";
	}

	public String getCountryCode() {
		TelephonyManager tm = (TelephonyManager) instance.getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkCountryIso();
	}

	public String getMacAddress() {
		return getLocalMacAddressFromBusybox();
	}

	public String getIP() {
		return getLocalIpAddress();
	}

	@SuppressWarnings("deprecation")
	private void initDisplaySize(Activity a) {
		if (a != null) {
			Display mDisplay = a.getWindowManager().getDefaultDisplay();
			mScreenWidth = mDisplay.getWidth();
			mScreenHeight = mDisplay.getHeight();
		}
	}

	public int getScreenWidth() {
		return mScreenWidth;
	}

	public int getScreenHeight() {
		return mScreenHeight;
	}

	public String getDisplaySize() {
		if (instance.mScreenWidth == 0)
			return "";
		return instance.mScreenWidth + "x" + instance.mScreenHeight;
	}

	public String getSystemLanguage() {
		if (instance.mContext != null)
			return instance.mContext.getResources().getConfiguration().locale
					.toString();
		return null;
	}

	private String getLocalMacAddressFromBusybox() {
		try {
			WifiManager wifi = (WifiManager) instance.getContext()
					.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = wifi.getConnectionInfo();

			return info.getMacAddress();
		} catch (Exception e) {
			return "";
		}
	}

	private String getLocalIpAddress() {
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

	public String getNetType() {
		String netType = "";
		try {
			ConnectivityManager connMgr = (ConnectivityManager) instance
					.getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			if (networkInfo == null) {
				return netType;
			}
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE) {
				// if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
				// {
				// netType = "CMNET";
				// } else {
				// netType = "CMWAP";
				// }
				final Cursor c = mContext.getContentResolver().query(
						Uri.parse("content://telephony/carriers/preferapn"),
						null, null, null, null);
				if (c != null) {
					c.moveToFirst();
					final String user = c.getString(c.getColumnIndex("user"));
					if (!TextUtils.isEmpty(user)) {
						// if (user.startsWith(CTWAP)) {
						// return TYPE_CT_WAP;
						// }
					}
				}
				c.close();
			} else if (nType == ConnectivityManager.TYPE_WIFI) {
				netType = "WIFI";
			}
		} catch (Exception e) {
		}
		return netType;
	}

	public String getPackageName() {
		return instance.getContext().getPackageName();
	}

	public String getCampaign() {
		String gpCampaign = SPUtil.getCampaign(instance.getContext());
		if (gpCampaign == null) {
			String appCampaign = getStringMetaData(instance.getContext(),
					"OKGAME_CAMPAIGN");
			if (appCampaign == null || appCampaign.length() == 0)
				appCampaign = "other";
			return appCampaign;
		}
		return gpCampaign;
	}
	


	public static String getStringMetaData(Context context, String key) {
		Bundle metaData = getMetaData(context);
		String strVal = metaData != null ? metaData.getString(key) : null;
		// if (strVal == null || "".equals(strVal)) {
		// }
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
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getCustomerId() {
		return MD5.crypt(getPackageName() + getAndroidId() + getIMEI());
	}
}
