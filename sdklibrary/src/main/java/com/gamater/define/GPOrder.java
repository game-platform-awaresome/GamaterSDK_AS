package com.gamater.define;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GPOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5141165152153046036L;

	private static final String ACCOUNT = "account";
	private static final String SKU = "sku";
	private static final String PAY_TOKEN = "payToken";
	private static final String ORDER_ID = "orderId";
	private static final String ROLE_ID = "roleId";
	private static final String AMOUNT = "amount";
	private static final String PAY_TYPE = "payType";
	private static final String SERVER_ID = "serverId";
	private static final String GOOGLE_ORDER_ID = "googleOrderId";
	private static final String EXTRA = "extra";

	private String account;
	private String sku;
	private String payToken;
	private String orderId;
	private String roleId;
	private String amount;
	private String payType;
	private String serverId;
	private String extra;

	private String googleOrderId;

	public GPOrder() {
		this.account = "";
		this.sku = "";
		this.payToken = "";
		this.orderId = "";
		this.amount = "";
		this.payType = "inapp";
		this.serverId = "";
		this.googleOrderId = "";
		this.roleId = "";
		this.extra = "";
	}

	public GPOrder(JSONObject obj) {
		try {
			this.account = (String) obj.get(ACCOUNT);
			this.sku = (String) obj.get(SKU);
			this.payToken = (String) obj.get(PAY_TOKEN);
			this.orderId = (String) obj.get(ORDER_ID);
			this.amount = (String) obj.get(AMOUNT);
			this.payType = (String) obj.get(PAY_TYPE);
			this.serverId = (String) obj.get(SERVER_ID);
			this.googleOrderId = (String) obj.get(GOOGLE_ORDER_ID);
			this.roleId = (String) obj.get(ROLE_ID);
			this.extra = (String) obj.getString(EXTRA);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getPayToken() {
		return payToken;
	}

	public void setPayToken(String payToken) {
		this.payToken = payToken;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getGoogleOrderId() {
		return googleOrderId;
	}

	public void setGoogleOrderId(String googleOrderId) {
		this.googleOrderId = googleOrderId;
		Log.e("initPaymentValidate", googleOrderId);
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public JSONObject toJSON() {

		JSONObject obj = new JSONObject();

		try {
			obj.put(ACCOUNT, "" + account);
			obj.put(SKU, "" + sku);
			obj.put(PAY_TOKEN, "" + payToken);
			obj.put(ORDER_ID, "" + orderId);
			obj.put(ROLE_ID, "" + roleId);
			obj.put(AMOUNT, "" + amount);
			obj.put(PAY_TYPE, "" + payType);
			obj.put(SERVER_ID, "" + serverId);
			obj.put(GOOGLE_ORDER_ID, "" + googleOrderId);
			obj.put(EXTRA, "" + extra);
		} catch (JSONException e) {
		}

		return obj;
	}

}
