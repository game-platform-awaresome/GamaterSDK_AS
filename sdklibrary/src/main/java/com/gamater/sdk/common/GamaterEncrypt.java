package com.gamater.sdk.common;

import android.content.Context;

/**
 * Created by hasoji on 15/11/17.
 */
public class GamaterEncrypt {
	public native static String encrypt(Context context, String[] params);

	static {
		System.loadLibrary("gamater-native-lib");
	}

	@Deprecated
	public static String crypt(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}
