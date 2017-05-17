package com.gamater.define;

import java.io.Serializable;

public class PaymentParam implements Serializable {
	private static final long serialVersionUID = 1L;

	private String serverId;
	private String serverName = "";
	private String account = "";
	private String roleId;
	private String roleName = "";
	private String extra = "";
	private String sku;
	private float amount;
	private String currency = ""; // TWD THB HKD SGD USD

	private String productDescription;

	private int isCoinsCharge; // 0 == false, 1 主动，2 不够钱

	public boolean isCoinsCharge() {
		return isCoinsCharge != 0;
	}

	public int getCoinsCharge() {
		return isCoinsCharge;
	}

	public void setCoinsCharge(int isCoinsCharge) {
		this.isCoinsCharge = isCoinsCharge;
	}

	public PaymentParam(String serverId, String serverName, String account,
			String roleId, String roleName, String sku,
			String productDescription, float amount, String currency,
			String extra) {
		this.serverId = serverId;
		this.account = account;
		this.roleId = roleId;
		this.extra = extra;
		this.sku = sku;
		this.amount = amount;
		this.serverName = serverName;
		this.roleName = roleName;
		this.productDescription = productDescription;
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public PaymentParam copyParam() {
		PaymentParam p = new PaymentParam(serverId, serverName, account,
				roleId, roleName, sku, productDescription, amount, currency,
				extra);
		p.isCoinsCharge = isCoinsCharge;
		return p;
	}
}
