package com.gamater.common.http;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeMap;

import org.apache.http.ParseException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gamater.account.http.APIs;
import com.gamater.account.http.Keys;
import com.gamater.common.Config;
import com.gamater.common.CrashHandler;
import com.gamater.define.SPUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.LogUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.vsgm.sdk.VsgmEncrypt;

/**
 * 
 * @author Robert Lo
 * 
 */
public class HttpRequest extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4024085954193344648L;
	private static final int REQUEST_START = 1;
	private static final int REQUEST_SUCCESS = 2;
	private static final int REQUEST_FAIL = 0;
	private static final String RESPONSE_OBJ = "responseObj";
	private static final String KEY_METHOD = "method";
	private static final String SELF_OBJ = "selfObj";

	private String url;
	private HttpEventListener httpEventListener;
	private String funcation;
	private String method;
	private OkHttpClient httpClient;
	private Context context;
	private String flagKey = "flag";

	public HttpRequest setFlagKey(String key) {
		flagKey = key;
		return this;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private Call mCall;

	private Object tag;

	public HttpRequest setTag(Object tag) {
		this.tag = tag;
		return this;
	}

	public Object getTag() {
		return this.tag;
	}

	public String getFuncation() {
		return funcation;
	}

	public void setFuncation(String funcation) {
		this.funcation = funcation;
	}

	private Request.Builder builder = new Request.Builder();

	public HttpEventListener getHttpEventListener() {
		return httpEventListener;
	}

	public void setHttpEventListener(HttpEventListener httpEventListener) {
		this.httpEventListener = httpEventListener;
	}

	/**
	 * 取消请求
	 */
	public void cancelRequest() {
		// this.httpClient.getConnectionManager().shutdown();
		if (mCall != null) {
			httpClient.cancel(mCall);
		}
		httpEventListener = null;
	}

	/**
	 * 结构
	 * 
	 * @param url
	 *            请求的根地址
	 * @param method
	 *            方法
	 */
	public HttpRequest(String method, String url, String function) {
		try {
			this.url = url;
			this.funcation = function;
			this.method = method;

			StringBuffer sb = new StringBuffer(url);
			if (funcation != null && funcation.length() > 0) {
				sb.append(funcation);
			}

			this.url = sb.toString();
			this.httpClient = getHttpClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OkHttpClient getHttpClient() {
		return MVHttpClient.getHttpClient();
	}

	/**
	 * 添加一个Post值
	 * 
	 * @param key
	 *            参数名
	 * @param value
	 *            参数值
	 */
	// private FormEncodingBuilder postBodyBuilder = new FormEncodingBuilder();
	private TreeMap<String, String> postData = new TreeMap<String, String>(
			new Comparator<String>() {
				public int compare(String key1, String key2) {
					return key1.compareTo(key2);
				}
			});

	public void addPostValue(String key, String value) {
		if (key != null && key.length() > 0)
			postData.put(key, "" + value);
		// postBodyBuilder.addEncoded(key, "" + value);
	}

	public void addPostValue(String key, int value) {
		if (key != null && key.length() > 0)
			postData.put(key, "" + value);
		// postBodyBuilder.addEncoded(key, "" + value);
	}

	/**
	 * 开始异步请求
	 */
	public void asyncStart() {
		isRunning = true;
		start();
	}

	public void addHeader(String name, String value) {
		try {
			builder.addHeader(name, value == null ? "" : value);
			LogUtil.printLog(name + ":" + (value == null ? "" : value));
		} catch (Exception e) {
			Log.e("ErrorHeader", name + " : " + value);
		}
	}

	private boolean isRunning;

	public boolean isRunning() {
		return isRunning;
	}

	public String startOutOfSelfThread() {
		builder.header("User-Agent", Config.gmTitle.toLowerCase() + "_sdk");
		long startTime = System.currentTimeMillis();
		String result = null;
		try {
			Response rsp = null;
			if (method.equalsIgnoreCase("GET")) {

				mCall = httpClient.newCall(builder.url(url).build());
				rsp = mCall.execute();
				if (rsp.isSuccessful()) {
					result = rsp.body().string();
					handlerRequestSuccess(startTime, result);
				} else {
					long responseTime = System.currentTimeMillis() - startTime;
					handlerRequestFail(url, null, "" + rsp.code(), responseTime);
				}
			} else if (method.equalsIgnoreCase("POST")) {
				if (Config.isShowLog) {
					LogUtil.printHTTP((funcation == null || funcation.length() == 0) ? url : funcation);
					LogUtil.printHTTP(url);
				}
				mCall = httpClient.newCall(builder.url(url).post(getHttpEntity()).build());
				rsp = mCall.execute();
				if (rsp.isSuccessful()) {
					result = rsp.body().string();
					handlerRequestSuccess(startTime, result);
				} else {
					long responseTime = System.currentTimeMillis() - startTime;
					handlerRequestFail(url, null, "" + rsp.code(), responseTime);
				}
			}
		} catch (Exception e) {
			// tryIpConnect();
			e.printStackTrace();
			long time = System.currentTimeMillis() - startTime;
			handlerRequestFail(url, e, "", time);
		}
		return result;
	}

	@Override
	public void run() {
		Message startMessage = new Message();
		Bundle bundle1 = new Bundle();

		startMessage.what = REQUEST_START;
		startMessage.obj = httpEventListener;
		bundle1.putString(KEY_METHOD, this.funcation);
		bundle1.putSerializable(SELF_OBJ, this);
		startMessage.setData(bundle1);

		handler.sendMessage(startMessage);

		startOutOfSelfThread();
		isRunning = false;
	}

	public RequestBody getHttpEntity() throws Exception {
		if (context == null)
			context = GamaterSDK.getInstance().getActivity();
		addPostValue("platform", "android");
		addPostValue("client_id", Config.clientId);
		addPostValue("package", context.getPackageName());
		addPostValue(Keys.POST_KEY_TIME, System.currentTimeMillis() / 1000 + "");
		JSONObject data = new JSONObject();
		makeSignature(context, postData, flagKey, data);
		RequestBody body = RequestBody.create(null, data.toString());
		return body;
	}

	public static String makeSignature(Context ctx, Hashtable<String, String> data, String signatureKey) {
		TreeMap<String, String> postData = new TreeMap<String, String>(
				new Comparator<String>() {
					public int compare(String key1, String key2) {
						return key1.compareTo(key2);
					}
				});
		postData.putAll(data);
		return makeSignature(ctx, postData, signatureKey, null);
	}

	private static String makeSignature(Context ctx, TreeMap<String, String> data, String signatureKey, JSONObject json) {
		try {
			int size = data.containsKey(signatureKey) ? data.size() - 1 : data.size();
			if (json == null)
				json = new JSONObject();
			if (size > 0) {
				int index = 0;
				String[] params = new String[size];
				for (String key : data.keySet()) {
					if (!key.equals(signatureKey)) {
						params[index] = data.get(key).trim();
						json.put(key, "" + params[index]);
						index++;
					}
				}
				String result = VsgmEncrypt.encrypt(ctx, params);
				json.put(signatureKey, result);
				LogUtil.printHTTP(json.toString());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private void handlerRequestSuccess(long startTime, String result) {
		long responseTime = System.currentTimeMillis() - startTime;

		try {
			Message successMsg = new Message();
			Bundle successBundle = new Bundle();
			successBundle.putString(KEY_METHOD, funcation);
			successBundle.putString(RESPONSE_OBJ, result);
			successBundle.putSerializable(SELF_OBJ, (Serializable) this);

			successMsg.what = REQUEST_SUCCESS;
			successMsg.obj = httpEventListener;
			successMsg.setData(successBundle);
			handler.sendMessage(successMsg);

			if (Config.isShowLog) {
				formatOutput(result);
			}

			// reportResponseTime(url, null, result, responseTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void handlerRequestFail(String url, Exception e, String code, long time) {
		Message failedMsg = new Message();
		Bundle failedBundle = new Bundle();
		failedBundle.putString(KEY_METHOD, funcation);
		failedBundle.putSerializable(SELF_OBJ, this);
		failedMsg.what = REQUEST_FAIL;
		failedMsg.obj = httpEventListener;
		failedMsg.setData(failedBundle);
		handler.sendMessage(failedMsg);
		reportResponseTime(url, e, code, time);
	}

	private void formatOutput(String json) {
		LogUtil.printHTTP(json);
	}

	private static final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			HttpEventListener httpEventListener = (HttpEventListener) message.obj;

			if (httpEventListener != null) {
				Bundle data = message.getData();
				HttpRequest selfObj = (HttpRequest) data.getSerializable(SELF_OBJ);

				switch (message.what) {
				case REQUEST_START:
					httpEventListener.requestDidStart(selfObj);
					break;
				case REQUEST_SUCCESS:
					String obj = (String) data.get(RESPONSE_OBJ);
					httpEventListener.requestDidSuccess(selfObj, obj);
					break;
				case REQUEST_FAIL:
					httpEventListener.requestDidFailed(selfObj);
					break;
				default:
					break;
				}
			}
		}
	};

	private static void reportResponseTime(String function, Exception e, String result, long rspTime) {
		if (function.contains(APIs.RESPONSE_TIME) || function.contains(WebAPI.CRASH_UPLOAD) || function.length() == 0)
			return;
		try {
			SPUtil.addResponseTime(GamaterSDK.getInstance().getActivity(), function, e, result, rspTime);
			CrashHandler.sendResponseTime(GamaterSDK.getInstance().getActivity());
		} catch (Exception ex) {
		}
	}

	public interface HttpEventListener {
		public void requestDidSuccess(HttpRequest httpRequest, String result);

		public void requestDidStart(HttpRequest httpRequest);

		public void requestDidFailed(HttpRequest httpRequest);
	}

}
