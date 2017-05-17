package com.gamater.payment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.util.ResourceUtil;

public class PaymentHttpRequest extends MVHttpRequest implements HttpEventListener {
	private static final long serialVersionUID = -5526348261374086910L;

	private OKgameDialogProcess process;

	private Activity activity;

	private int mDialogLayout = ResourceUtil.getLayoutId("vsgm_tony_process");

	public PaymentHttpRequest(Activity activity) {
		super("post", Config.getPayHost(), WebAPI.GET_ORDER_ID);
		this.activity = activity;
	}

	public void initParams(String serverId, String roleId, String accountId, String extra, String amount) {
		String time = System.currentTimeMillis() / 1000 + "";
		this.addPostValue("serverId", serverId);
		this.addPostValue("account", accountId);
		this.addPostValue("roleId", roleId);
		this.addPostValue("extra", extra);
		this.addPostValue("time", time + "");
		this.addPostValue("amount", amount);
		this.addPostValue("flag", MD5.crypt(serverId + accountId + roleId + WebAPI.PAY_KEY + time + ""));
	}

	private OrderIdCallback mCallback;

	public void asyncStart(OrderIdCallback callback) {
		this.mCallback = callback;
		process = new OKgameDialogProcess(activity, mDialogLayout);
		process.setCancelable(false);
		setHttpEventListener(this);
		super.asyncStart();
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		String orderId = null;
		try {
			JSONObject obj = new JSONObject(result);
			if (1 == obj.optInt("code")) {
				orderId = obj.optString("orderId");
			}
		} catch (JSONException e) {
		}
		if (process != null && process.isShowing())
			process.dismiss();
		if (mCallback != null) {
			mCallback.onCallback(orderId);
		}
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		if (process != null && !process.isShowing())
			process.show();
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		if (process != null && process.isShowing())
			process.dismiss();
		if (mCallback != null) {
			mCallback.onCallback(null);
		}
	}

	public interface OrderIdCallback {
		public void onCallback(String orderId);
	}
}
