package com.gamater.define;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.gamater.common.Config;
import com.gamater.common.http.MD5;
import com.gamater.common.http.WebAPI;
import com.gamater.util.LogUtil;

public class SPUtil {
	public static final String DATA_SAVE_NAME = "VsgmSdkData";
	public static final String JSON_DATA = "VsgmJSONData";
	private static final String ORDER_KEY = "google_orders";
	private static final String CAMPAIGN_KEY = "campaign";
	private static final String IS_SEND_INSTALL = "isSendInstall";
	private static final String LAST_SEND_APPS_TIME = "lastSendAppsTime";
	private static final String LAST_GET_BACK_TIME = "lastGetBackTime_";
	private static final String CONFIG_SETTINGS = "configSettings_";
	private static final String PNF_USER_MSG = "page_not_found_message";
	private static final String RESPONSE_TIME = "response_time";
	public static final String PURCHASE_KEY = "iab_purchase";
	public static final String CONFIG_JSON_STRING = "config_json_string";
	public static final String JSON_DATA_STRING = "json_data_string";

	public static void setConfigSettings(Context ctx, String configName,
			int enable) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		sp.edit().putInt(CONFIG_SETTINGS + configName, enable).commit();
	}

	public static int getConfigSettings(Context ctx, String configName) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		return sp.getInt(CONFIG_SETTINGS + configName, 0);
	}

	public static void setConfigJsonString(Context ctx, String configStr) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().putString(CONFIG_JSON_STRING, configStr).commit();
	}

	public static String getConfigJsonString(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(CONFIG_JSON_STRING, null);
	}

	public static void setJSONData(Context ctx, String jsonData) {
		SharedPreferences sp = ctx.getSharedPreferences(JSON_DATA, 0);
		sp.edit().putString(JSON_DATA_STRING, jsonData).commit();
	}

	public static String getJSONData(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(JSON_DATA, 0);
		return sp.getString(JSON_DATA_STRING, null);
	}

	public static void addResponseTime(Context ctx, String function,
			Exception e, String result, long responseTime) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);

		String error = "";
		if (e != null) {
			error = e.getMessage();
		}
		JSONObject o = new JSONObject();
		try {
			o.put("func", "" + function);
			o.put("error", "" + error);
			o.put("result", "" + result);
			o.put("responseTime", "" + responseTime);
		} catch (JSONException e1) {
		}

		String key = "" + System.currentTimeMillis();

		LogUtil.printHTTP("CrashHandler add key : " + key);
		String str = sp.getString(RESPONSE_TIME, "");
		str += key + "&&";

		sp.edit().putString(RESPONSE_TIME, str).commit();
		sp.edit().putString(RESPONSE_TIME + "_" + key, o.toString()).commit();
	}

	public static String getResponseTime(Context ctx, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(RESPONSE_TIME + "_" + key, "");
	}

	public static String getResponseTimeKey(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(RESPONSE_TIME, "");
	}

	public static void removeResponseTime(Context ctx, String key) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		String str = sp.getString(RESPONSE_TIME, "");
		str = str.replace(key + "&&", "");
		sp.edit().putString(RESPONSE_TIME, str).commit();
		sp.edit().remove(RESPONSE_TIME + "_" + key).commit();
	}

	public static void addPNFMessage(Context ctx, String msg) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		msg = sp.getString(PNF_USER_MSG, "") + msg + "\n";
		sp.edit().putString(PNF_USER_MSG, msg).commit();
	}

	public static String getPNFMessage(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(PNF_USER_MSG, "");
	}

	public static void removePNFMessage(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().remove(PNF_USER_MSG).commit();
	}

	public static void setLastGetBackTime(Context ctx, String email, long time) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().putLong(LAST_GET_BACK_TIME + email, time).commit();
	}

	public static long getLastGetBackTime(Context ctx, String email) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getLong(LAST_GET_BACK_TIME + email, 0);
	}

	public static void setLastSendAppsTime(Context ctx, long time) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().putLong(LAST_SEND_APPS_TIME, time).commit();
	}

	public static long getLastSendAppsTime(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getLong(LAST_SEND_APPS_TIME, 0);
	}

	public static void saveCampaign(Context ctx, String campaign) {
		if (campaign == null || campaign.length() == 0)
			return;
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().putString(CAMPAIGN_KEY, campaign).commit();
	}

	public static String getCampaign(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(CAMPAIGN_KEY, null);
	}

	public static void saveSystemMsgRead(Context ctx, int msgId) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		sp.edit().putBoolean("SystemMessage" + msgId, true).commit();
	}

	public static boolean getSystemMsgRead(Context ctx, int msgId) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getBoolean("SystemMessage" + msgId, false);
	}

	public static void setSendInstall(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		sp.edit().putBoolean(IS_SEND_INSTALL, true).commit();
	}

	public static boolean isSendInstall(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getBoolean(IS_SEND_INSTALL, false);
	}

	/**
	 * 保存Order数据
	 * 
	 * @param data
	 */
	public static void saveOrder(JSONObject orderJson, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		String orders = sp.getString(ORDER_KEY, null);

		JSONArray orderList = new JSONArray();

		if (orders == null) {
			sp.edit().putString(ORDER_KEY, orderList.toString());
			orders = orderList.toString();
		}
		try {
			JSONArray array = new JSONArray(orders);
			for (int i = 0; i < array.length(); i++) {
				JSONObject order = (JSONObject) array.get(i);
				if (order.get(ParameterKey.ORDER_ID).equals(orderJson.get(ParameterKey.ORDER_ID))) {
					Field valuesField = JSONArray.class.getDeclaredField("values");
					valuesField.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<Object> values = (List<Object>) valuesField.get(array);
					values.remove(i);
					break;
				}
			}
			long time = System.currentTimeMillis() / 1000;
			orderJson.put("time", time);
			orderJson.put("flag",
					MD5.crypt(orderJson.optString(ParameterKey.ORDER_ID)
							+ orderJson.optString("sku") + WebAPI.PAY_KEY
							+ time));
			array.put(orderJson);
			sp.edit().putString(ORDER_KEY, array.toString()).commit();

			orders = sp.getString(ORDER_KEY, null);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void removeOrder(JSONObject orderJson, Context ctx) {
		try {
			List<Object> list = new ArrayList<Object>();
			SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
			String orders = sp.getString(ORDER_KEY, null);

			if (orders == null) {
				return;
			}

			JSONArray array = new JSONArray(orders);

			for (int i = 0; i < array.length(); i++) {
				list.add(array.get(i));

				JSONObject json = (JSONObject) array.get(i);
				String orderId = orderJson.getString(ParameterKey.ORDER_ID);

				if (orderId.equals(json.getString(ParameterKey.ORDER_ID))) {
					list.remove(i);
				}
			}

			array = new JSONArray(list);

			sp.edit().putString(ORDER_KEY, array.toString()).commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得Order列表
	 * 
	 * @return
	 */
	public static JSONArray getOrders(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		String orders = sp.getString(ORDER_KEY, null);

		JSONArray orderList = new JSONArray();

		if (orders == null) {
			return orderList;
		}

		try {
			orderList = new JSONArray(orders);
		} catch (JSONException e1) {
			orderList = new JSONArray();
		}

		return orderList;
	}

	public static JSONArray getOrdersLimit50(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		String orders = sp.getString(ORDER_KEY, null);

		JSONArray orderList = new JSONArray();

		if (orders == null) {
			return orderList;
		}

		try {
			orderList = new JSONArray(orders);
			List<JSONObject> list = new ArrayList<JSONObject>();
			JSONObject jsonObj = null;
			for (int i = 0; i < orderList.length(); i++) {
				jsonObj = orderList.optJSONObject(i);
				list.add(jsonObj);
			}
			Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject json1, JSONObject json2) {
					long date1 = json1.optLong("time");
					long date2 = json2.optLong("time");
					if (date1 > date2) {
						return 1;
					} else if (date1 < date2) {
						return -1;
					}
					return 0;
				}
			};
			Collections.sort(list, comparator);
		} catch (JSONException e1) {
			e1.printStackTrace();
			orderList = new JSONArray();
		}

		return orderList;
	}

	public static void setUserPhotoUrl(Context ctx, String userId,
		String roleId, String serverId, String url) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"+ Config.isTestMode, 0);
		sp.edit().putString(userId + roleId + serverId, url).commit();
	}

	public static String getUserPhotoUrl(Context ctx, String userId,
			String roleId, String serverId) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		return sp.getString(userId + roleId + serverId, null);
	}

	public static int[] getFloatMenuOrigin(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		int x = sp.getInt("FloatMenuOriginX", -1);
		int y = sp.getInt("FloatMenuOriginY", -1);
		if (x < 0 || y < 0)
			return null;
		return new int[] { x, y };
	}

	public static void setFloatMenuOrigin(Context ctx, int x, int y) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		sp.edit().putInt("FloatMenuOriginX", x).putInt("FloatMenuOriginY", y).commit();
	}

	public static void setScrollAdMenuOrigin(Context ctx, int x, int y) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_" + Config.isTestMode, 0);
		sp.edit().putInt("ScrollAdMenuOriginX", x).putInt("ScrollAdMenuOriginY", y).commit();
	}

	public static int[] getScrollAdMenuOrigin(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DATA_SAVE_NAME + "_"
				+ Config.isTestMode, 0);
		int x = sp.getInt("ScrollAdMenuOriginX", -1);
		int y = sp.getInt("ScrollAdMenuOriginY", -1);
		if (x < 0 || y < 0)
			return null;
		return new int[] { x, y };
	}

}
