package com.gamater.dialog.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;

@SuppressLint("NewApi")
public class DialogUtil {
	public static AlertDialog.Builder showDialog(Activity activity, String title, String msg) {
		AlertDialog.Builder builder = null;
		if (Build.VERSION.SDK_INT >= 11) {
			builder = new AlertDialog.Builder(activity, 5);
		} else {
			builder = new AlertDialog.Builder(activity);
		}
		builder.setMessage(msg);
		builder.setTitle(title);
		return builder;
	}

	public static AlertDialog.Builder showDialog(Activity activity, int titleResId, int msgResId) {
		AlertDialog.Builder builder = null;
		if (Build.VERSION.SDK_INT >= 11) {
			builder = new AlertDialog.Builder(activity, 5);
		} else {
			builder = new AlertDialog.Builder(activity);
		}
		builder.setMessage(msgResId);
		builder.setTitle(titleResId);
		return builder;
	}

	public static AlertDialog.Builder showDialog(Activity activity, String title, int msgResId) {
		AlertDialog.Builder builder = null;
		if (Build.VERSION.SDK_INT >= 11) {
			builder = new AlertDialog.Builder(activity, 5);
		} else {
			builder = new AlertDialog.Builder(activity);
		}
		builder.setMessage(msgResId);
		builder.setTitle(title);
		return builder;
	}
}
