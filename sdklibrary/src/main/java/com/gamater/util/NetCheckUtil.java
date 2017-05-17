package com.gamater.util;

import com.gamater.account.MobUserManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 
 * 描述:网络链接检测类
 * 
 * @author cjl
 * @since 2014-8-15 下午12:06:10
 */

public class NetCheckUtil extends BroadcastReceiver {

	// wifi为0,3g为1
	public final static int WIFI = 0;
	public final static int G3 = 1;

	// 检查网络连接的是wifi还是3G
	public static int checkNetworkInfo(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // mobile
																													// 3G
																													// Data
																													// Network
		try {
			// 平板上没有3G报错
			State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

			if (wifi == State.CONNECTED) {
				return WIFI;
			} else {
				return G3;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return WIFI;
	}

	/**
	 * 判断是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkStatus(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isConnected = true;

		try {
			// 平板上没有3G报错
			State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (!(mobile == State.CONNECTED || wifi == State.CONNECTED)) {
				isConnected = false;
			}
		} catch (Exception e) {
		}
		return isConnected;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		if (activeInfo != null) {
			MobUserManager mobUserManager = MobUserManager.getInstance();
			if (mobUserManager.getServiceHost().equalsIgnoreCase("") || mobUserManager.getConfigJson(context) == null) {
				mobUserManager.requestUrls();
			}
		}
	} // 如果无网络连接activeInfo为null

}
