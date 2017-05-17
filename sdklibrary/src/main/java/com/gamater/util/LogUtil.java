package com.gamater.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gamater.common.Config;
import com.gamater.sdk.game.GamaterSDK;

public class LogUtil {
	private static final int JSON_INDENT = 4;

	public static void printHTTP(String msg) {
		if (Config.isShowLog) {
			print(Config.gmTitle + "_HTTP_LOG_" + GamaterSDK.LOG_TAG, msg,
					false);
		}
	}

	public static void printLog(String msg) {
		if (Config.isShowLog) {
			Log.d(Config.gmTitle + "_HTTP_LOG_" + GamaterSDK.LOG_TAG, msg);
		}
	}

	public static void printPush(String msg) {
		if (Config.isShowLog) {
			print(Config.gmTitle + "_HTTP_LOG_PUSH_" + GamaterSDK.LOG_TAG,
					msg, true);
		}
	}

	public static void printPushSend(String msg) {
		if (Config.isShowLog) {
			print(Config.gmTitle + "_HTTP_LOG_PUSHSEND_" + GamaterSDK.LOG_TAG,
					msg, true);
		}
	}

	public static void print(String tag, String msg) {
		print(tag, msg, false);
	}

	public static synchronized void print(String tag, String msg,
			boolean errorLevel) {
		if (msg == null) {
			return;
		}
		try {
			if (msg.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(msg);
				String message = jsonObject.toString(JSON_INDENT);
				String[] lines = message.split(System
						.getProperty("line.separator"));
				log(tag,
						"┌─────────────────────────────────────────────────────────────────",
						errorLevel);
				for (String line : lines) {
					log(tag, "│" + " " + line, errorLevel);
				}
				log(tag,
						"└─────────────────────────────────────────────────────────────────",
						errorLevel);
				writeFile(context, "sdk_log", logCache.toString());
				logCache.delete(0, logCache.length());
				return;
			}
			if (msg.startsWith("[")) {
				JSONArray jsonArray = new JSONArray(msg);
				String message = jsonArray.toString(JSON_INDENT);
				String[] lines = message.split(System
						.getProperty("line.separator"));
				log(tag,
						"┌─────────────────────────────────────────────────────────────────",
						errorLevel);
				for (String line : lines) {
					log(tag, "│" + " " + line, errorLevel);
				}
				log(tag,
						"└─────────────────────────────────────────────────────────────────",
						errorLevel);
			} else {
				log(tag,
						"┌─────────────────────────────────────────────────────────────────",
						errorLevel);
				log(tag, "│" + " " + msg, errorLevel);
				log(tag,
						"└─────────────────────────────────────────────────────────────────",
						errorLevel);
			}
			writeFile(context, "sdk_log", logCache.toString());
			logCache.delete(0, logCache.length());
		} catch (JSONException e) {
			Log.e(tag, e.getCause().getMessage() + "\n" + msg);
		}
	}

	private static void log(String tag, String log, boolean errorLevel) {
		if (errorLevel) {
			Log.e(tag, log);
			addLogCache(log);
		} else {
			Log.w(tag, log);
		}
	}

	public static void printFile(Context ctx, String file, String log) {
		printHTTP(log);
		writeFile(ctx, file, log + "\n\n\n");
	}

	private static StringBuffer logCache = new StringBuffer();
	private static Context context;

	public static void setContext(Context ctx) {
		context = ctx.getApplicationContext();
	}

	private static void addLogCache(String log) {
		logCache.append(log).append("\n");
	}

	private static void writeFile(Context ctx, String name, String log) {
		if (ctx == null)
			return;
		if (log.length() > 0) {
			try {
				String filePath = Environment.getExternalStorageDirectory()
						+ "/" + name + ".txt";
				File f = new File(filePath);
				if (!f.exists()) {
					f.createNewFile();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(f, true), 2048);
				writer.write(log);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("HTTP_PUSH", e.getMessage());
			}
		}
	}
}
