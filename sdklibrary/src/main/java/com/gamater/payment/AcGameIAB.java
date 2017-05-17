package com.gamater.payment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.gamater.account.EventTracker;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;
import com.gamater.define.AcGameIABUtil;
import com.gamater.define.DeviceInfo;
import com.gamater.define.GPOrder;
import com.gamater.define.ParameterKey;
import com.gamater.define.PaymentParam;
import com.gamater.define.SPUtil;
import com.gamater.dialog.util.DialogUtil;
import com.gamater.payment.iab.GPIabPay;
import com.gamater.payment.web.WebPay;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.pay.other.OtherPayInterface;

public class AcGameIAB implements HttpEventListener {
	static final String TAG = "AcGameIAB";

	private Context context;

	private static AcGameIAB m_instance = null;

	private Activity currentActivity;

	private GamaterIABListener acGameIABListener;

	private OtherPayInterface otherPay;

	public GamaterIABListener getAcGameIABListener() {
		return acGameIABListener;
	}

	public void setAcGameIABListener(GamaterIABListener listener) {
		this.acGameIABListener = listener;
	}

	public boolean isEnableHttpLog() {
		return Config.isShowLog;
	}

	public void setEnableHttpLog(boolean enableHttpLog) {
		Config.isShowLog = enableHttpLog;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public synchronized static AcGameIAB getInstance(Activity activity,
			List<String> skus, boolean isShowLog) {

		if (m_instance == null) {
			m_instance = new AcGameIAB(activity, skus, isShowLog);
		}

		return m_instance;
	}

	public synchronized static AcGameIAB getInstance(Activity activity,
			boolean isShowLog) {
		return getInstance(activity, null, isShowLog);
	}

	public synchronized static AcGameIAB getInstance() {
		return m_instance;
	}

	private AcGameIAB(Activity activity, List<String> skus, boolean isShowLog) {
		this.context = activity.getApplicationContext();
		this.currentActivity = activity;
		setEnableHttpLog(isShowLog);
		DeviceInfo.getInstance(activity);
		if (skus != null) {
			init(skus);
		}
	}

	public void init(List<String> skus) {
		GPIabPay.init(currentActivity, skus);
	}

	public void checkIabSetup() {
		if (!isIabSetupOK())
			setupIabHelper(true);
	}

	public void setupIabHelper(boolean enableDebugLogging) {
		try {
			GPIabPay.getInstance().setupIabHelper(enableDebugLogging);
		} catch (Exception e) {
		}
	}

	public void paymentGpIab(PaymentParam param) {
		if (isIabSetupOK()) {
			GPIabPay.init(currentActivity).payment(param);
		} else if (acGameIABListener != null) {
			LogUtil.printHTTP("GooglePlayIAB setupHelperFailed");
			acGameIABListener.setupHelperFailed();
		}

	}

	public void paymentGpIab(Activity activity, PaymentParam param) {
		if (isIabSetupOK() && activity != null) {
			GPIabPay.init(activity).payment(param);
		} else {
			paymentGpIab(param);
		}
	}

	public void paymentOther(PaymentParam param) {
		WebPay.init(currentActivity).payment(param);
	}

	public void paymentOther(Activity activity, PaymentParam param) {
		if (activity != null)

			WebPay.init(activity).payment(param);
		else
			paymentOther(param);
	}

	public void payment(Activity activity, PaymentParam param) {
		this.currentActivity = activity;
		payment(param);
	}

	public void payment(final PaymentParam param) {
		LogUtil.printLog(("payment type : " + Config.payType) + "  PaymentParam: "
				+ (param == null ? "param is null" : (param.getSku() == null ? "productId is null" : param.getSku())));
		this.currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (Config.payType == 1) {
					paymentGpIab(param);
				} else if (Config.payType == 2) {
					AlertDialog.Builder builder = DialogUtil.showDialog(currentActivity, Config.gmTitle,
							currentActivity.getString(ResourceUtil.getStringId("paytype_select_msg")));
					builder.setNegativeButton(currentActivity.getString(ResourceUtil.getStringId("paytype_gp")),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									paymentGpIab(param);
								}
							});
					builder.setPositiveButton(currentActivity.getString(ResourceUtil.getStringId("paytype_other")),
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									paymentOther(param);
								}
							});
					if (otherPay != null) {
						builder.setNeutralButton(otherPay.payName(), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								otherPay.pay(currentActivity, null, param);
							}
						});
					}
					builder.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							if (acGameIABListener != null)
								acGameIABListener.paymentFailed("user cancel");
						}
					});
					builder.show();
				}
			}
		});
	}

	/**
	 * 
	 * @param profession
	 *            职业
	 * @param serverId
	 *            服务器Id
	 * @param serverName
	 *            服务器名
	 * @param roleId
	 *            角色Id
	 * @param rolesName
	 *            角色名
	 * @param roleLevel
	 *            角色等级
	 */
	public void roleReport(String account, String profession, String serverId,
			String serverName, String roleId, String rolesName, int roleLevel) {
		String host = Config.getLoginHost();
		MVHttpRequest request = new MVHttpRequest("post", host,
				WebAPI.ROLE_REPORT);
		request.initHeader(DeviceInfo.getInstance(null));
		request.addPostValue(ParameterKey.POST_KEY_PROFESSION, profession);
		request.addPostValue(ParameterKey.POST_KEY_SERVER_ID, serverId);
		request.addPostValue(ParameterKey.POST_KEY_SERVER_NAME, serverName);
		request.addPostValue(ParameterKey.POST_KEY_ROLE_ID, roleId);
		request.addPostValue(ParameterKey.POST_KEY_ROLE_NAME, rolesName);
		request.addPostValue(ParameterKey.POST_KEY_LEVEL, roleLevel);
		request.addPostValue(ParameterKey.POST_KEY_ACCOUNT_ID, account);
		String time = System.currentTimeMillis() / 1000 + "";
		request.addPostValue(ParameterKey.TIME, time);
		request.addPostValue(ParameterKey.FLAG, MD5.crypt(roleId + serverId
				+ account + WebAPI.LOGIN_KEY + time));
		request.setHttpEventListener(this);
		request.asyncStart();
	}

	/**
	 * 验证购买
	 * 
	 * @param order
	 *            购买订单资料
	 */
	public void paymentValidate(GPOrder order) {
		EventTracker.payEvent(1003, order.getOrderId());
		String host = Config.getPayHost();
		MVHttpRequest request = new MVHttpRequest("post", host, WebAPI.PAY_VALIDATE);
		DeviceInfo di = DeviceInfo.getInstance(context);
		request.initHeader(di);
		AcGameIABUtil.initPaymentValidate(request, order);
		request.setHttpEventListener(this);
		request.asyncStart();
	}

	/**
	 * 检查是否有验证失败的订单
	 */
	public void checkFailedOrder() {

		JSONArray orders = SPUtil.getOrders(context);

		for (int i = 0; i < orders.length(); i++) {
			try {
				JSONObject jsonOrder = (JSONObject) orders.get(i);
				GPOrder order = new GPOrder(jsonOrder);
				LogUtil.print("checkFailedOrder", "checkFailedOrder: " + jsonOrder.toString());
				LogUtil.print("checkFailedOrder",
						"PayToken: " + order.getPayToken() + "//GoogleOrderId: " + order.getGoogleOrderId());
//				if (!order.getPayToken().isEmpty() && !order.getGoogleOrderId().isEmpty()) {
//					paymentValidate(order);
//				}
				if (!order.getPayToken().isEmpty()) {
					paymentValidate(order);
				}
			} catch (JSONException e) {
			}
		}

	}


	@Override
	public void requestDidSuccess(HttpRequest request, String result) {
		String function = request.getFuncation();
		if (WebAPI.PAY_VALIDATE.equalsIgnoreCase(function)) {
			if (request instanceof MVHttpRequest) {
				try {
					JSONObject o = new JSONObject(result);
					int code = o.optInt("code");
					LogUtil.print("checkFailedOrder", "checkFailedOrder code: " + code);
					if (1 == code || -10 == code || -11 == code) {
						MVHttpRequest httpRequest = (MVHttpRequest) request;
						GPOrder order = httpRequest.getOrder();
						SPUtil.removeOrder(order.toJSON(), context);
					} 
//					else {
//						((Activity) context).runOnUiThread(new Runnable() {
//							public void run() {
//								DialogUtil.showDialog(((Activity) context), "支付异常", "发货失败, 请关闭游戏重新进入游戏， 如果还没收到物品， 请联系客服");
//							}
//
//						});
////						Toast.makeText(context, "发货失败, 请关闭游戏重新进入游戏， 如果还没收到物品， 请联系客服", Toast.LENGTH_LONG).show();
//					}

				} catch (JSONException e) {

				}
			}
		} else if (WebAPI.ROLE_REPORT.equalsIgnoreCase(function)) {
			try {
				JSONObject o = new JSONObject(result);
				if (1 == o.optInt("status")) {
					if (acGameIABListener != null) {
						Config.payType = o.optJSONObject("data").optInt("type");
						acGameIABListener.onPayType(Config.payType);
					}
				} else {
					defaultPayTypeReturn();
				}
			} catch (JSONException e) {
				defaultPayTypeReturn();
			}
		}
	}

	@Override
	public void requestDidStart(HttpRequest request) {

	}

	@Override
	public void requestDidFailed(HttpRequest request) {
//		Log.e("initPaymentValidate", "requestDidFailed: " + request.getTag().toString());
		String function = request.getFuncation();
		if (function.equalsIgnoreCase(WebAPI.ROLE_REPORT)) {
			defaultPayTypeReturn();
		}

	}

	private void defaultPayTypeReturn() {
		if (acGameIABListener != null) {
			acGameIABListener.onPayType(1);
		}
	}

	private boolean isIabSetupOK() {
		try {
			return GPIabPay.getInstance().isIabSetupOK();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean handlerResult(int requestCode, int resultCode, Intent data) {
		return GPIabPay.getInstance().handlerResult(requestCode, resultCode, data);
	}

	public void destroy() {
		if (otherPay != null) {
			otherPay.onDestroy();
		}
	}
}