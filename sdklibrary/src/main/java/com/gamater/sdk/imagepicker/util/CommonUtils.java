package com.gamater.sdk.imagepicker.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

public class CommonUtils {

	/**
	 * 获取DisplayMetrics对象，以获取屏幕宽、高、密度等信息
	 */
	public static DisplayMetrics getScreenInfo(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * AsyncTask请调用此方法执行
	 */
	@SuppressLint("NewApi")
	public static <Params, Progress, Result> void executeAsyncTask(
			AsyncTask<Params, Progress, Result> task, Params... params) {
		// if (Build.VERSION.SDK_INT >= 11) {
		// task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		// } else {
		task.execute(params);
		// }
	}

	/**
	 * 扫描媒体库指定文件 如调用相机拍摄了一张照片后刷新此文件，使图库能显示出此图片
	 */
	public static void notifyScanFile(Context context, String fileName) {
		Uri data = Uri.parse("file://" + fileName);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				data));
	}

}
