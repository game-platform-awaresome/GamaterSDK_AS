package com.gamater.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.gamater.account.http.APIs;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.define.DeviceInfo;
import com.gamater.define.ParameterKey;
import com.gamater.define.SPUtil;
import com.gamater.util.LogUtil;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler mInstance = new CrashHandler();

	private UncaughtExceptionHandler mDefaultHandler;

	private Context mContext;

	private boolean isWriteFile;

	public void setWriteFile(boolean isWriteFile) {
		this.isWriteFile = isWriteFile;
	}

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return mInstance;
	}

	public void setCrashHanler(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		reportCrash(obtainExceptionInfo(ex));
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private void reportCrash(String crashLog) {
		if (isWriteFile) {
			LogUtil.printFile(mContext, "push_crash_log", crashLog);
			return;
		}
		reportCrash(crashLog, null);
	}

	public void reportCrash(String crashLog, HttpEventListener listener) {
		String host = Config.getLoginHost();
		DeviceInfo info = DeviceInfo.getInstance(mContext);
		MVHttpRequest request = new MVHttpRequest("post", host, WebAPI.CRASH_UPLOAD);
		request.initHeader(info);
		String time = System.currentTimeMillis() / 1000 + "";
		request.addPostValue(ParameterKey.TIME, time);
		request.addPostValue("content", crashLog);
		request.addPostValue(ParameterKey.FLAG, MD5.crypt(crashLog + WebAPI.LOGIN_KEY + time));
		request.setHttpEventListener(listener);
		request.asyncStart();
	}

	public static String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();
		return mStringWriter.toString();
	}

	private static boolean isSending = false;

	public static void sendResponseTime(final Activity activity) {
		if (isSending)
			return;
		isSending = true;
		String msg = SPUtil.getResponseTimeKey(activity);
		if (msg != null && msg.length() > 0) {
			final String m[] = msg.split("&&");
			if (m.length > 5) {// 当日志条数大于5的时候，提交
				try {
					JSONArray array = new JSONArray();
					for (int i = 0; i < m.length; i++) {
						LogUtil.printHTTP("sendResponseTime : " + m[i]);
						JSONObject o = new JSONObject(SPUtil.getResponseTime(
								activity, m[i]));
						SPUtil.removeResponseTime(activity, m[i]);
						String func = o.optString("func", "");

						String error = o.optString("error", "");
						String result = o.optString("result", "");
						String responseTime = o.optString("responseTime", "");
						int type = (error == null || "".equals(error)) ? 1 : 0;
						String content = type == 0 ? error : result;
						JSONObject newJSONObject = new JSONObject();
						newJSONObject.put("url", func);
						newJSONObject.put("resTime", responseTime);
						newJSONObject.put("content", content);
						newJSONObject.put("type", type);
						array.put(newJSONObject);
						if (array.toString().length() > 1024 * 16)
							break;
					}

					/*
					 * LogUtil.printHTTP("CrashHandler send key : " + m[0]);
					 * JSONObject o = new
					 * JSONObject(SPUtil.getResponseTime(activity, m[0]));
					 * String func = o.getString("func"); String error =
					 * o.getString("error"); String result =
					 * o.getString("result"); String responseTime =
					 * o.getString("responseTime"); int type = (error == null ||
					 * "".equals(error)) ? 1 : 0;
					 */

					String host = Config.getLoginHost();
					DeviceInfo info = DeviceInfo.getInstance(activity);
					MVHttpRequest request = new MVHttpRequest("post", host,
							APIs.RESPONSE_TIME);
					request.initHeader(info);
					request.addPostValue("request", array.toString());
					/*
					 * request.addPostValue("request",
					 * "[{\"content\":\"4\",\"type\":\"4\",\"resTime\":\"4\",\"url\":\"4\"},{\"content\":\"4\",\"type\":\"4\",\"resTime\":\"4\",\"url\":\"4\"}]"
					 * );
					 */
					String time = System.currentTimeMillis() / 1000 + "";
					request.addPostValue(ParameterKey.TIME, time);
					/*
					 * String time = System.currentTimeMillis() / 1000 + "";
					 * request.addPostValue(ParameterKey.TIME, time);
					 * request.addPostValue("url", func);
					 * request.addPostValue("resTime", responseTime); String
					 * content = type == 0 ? error : result;
					 */

					// content = content.replaceAll("\r", "");
					// content = content.replaceAll("\n", "");
					//
					// content = Base64.encodeToString(content.getBytes(),
					// Base64.DEFAULT);

					/*
					 * 
					 * request.addPostValue("content", content);
					 * request.addPostValue("type", type);
					 */
					request.setHttpEventListener(new HttpEventListener() {

						@Override
						public void requestDidSuccess(HttpRequest httpRequest,
								String result) {
							// try {
							// JSONObject o = new JSONObject(result);
							// if (o.optInt("status", 0) == 1) {
							// }
							// } catch (Exception e) {
							// e.printStackTrace();
							// }
							sendResponseTime(activity);
							isSending = false;
						}

						@Override
						public void requestDidStart(HttpRequest httpRequest) {
						}

						@Override
						public void requestDidFailed(HttpRequest httpRequest) {
							isSending = false;
						}
					});
					request.asyncStart();
				} catch (Exception e) {
					e.printStackTrace();
					isSending = false;
				}
			} else {
				isSending = false;
			}
		} else {
			isSending = false;
		}
	}
}
