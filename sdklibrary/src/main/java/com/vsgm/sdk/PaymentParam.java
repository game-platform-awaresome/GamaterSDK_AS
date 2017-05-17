package com.vsgm.sdk;

public class PaymentParam extends com.gamater.define.PaymentParam {
	private static final long serialVersionUID = 1L;

	public PaymentParam(String serverId, String serverName, String account,
			String roleId, String roleName, String sku,
			String productDescription, float amount, String currency,
			String extra) {
		super(serverId, serverName, account, roleId, roleName, sku,
				productDescription, amount, currency, extra);
	}

	@Override
	public String getCurrency() {
		return super.getCurrency();
	}

	@Override
	public void setCurrency(String currency) {
		super.setCurrency(currency);
	}

	@Override
	public float getAmount() {
		return super.getAmount();
	}

	@Override
	public void setAmount(float amount) {
		super.setAmount(amount);
	}

	@Override
	public String getServerId() {
		return super.getServerId();
	}

	@Override
	public void setServerId(String serverId) {
		super.setServerId(serverId);
	}

	@Override
	public String getAccount() {
		return super.getAccount();
	}

	@Override
	public void setAccount(String account) {
		super.setAccount(account);
	}

	@Override
	public String getRoleId() {
		return super.getRoleId();
	}

	@Override
	public void setRoleId(String roleId) {
		super.setRoleId(roleId);
	}

	@Override
	public String getExtra() {
		return super.getExtra();
	}

	@Override
	public void setExtra(String extra) {
		super.setExtra(extra);
	}

	@Override
	public String getSku() {
		return super.getSku();
	}

	@Override
	public void setSku(String sku) {
		super.setSku(sku);
	}

	@Override
	public String getServerName() {
		return super.getServerName();
	}

	@Override
	public void setServerName(String serverName) {
		super.setServerName(serverName);
	}

	@Override
	public String getRoleName() {
		return super.getRoleName();
	}

	@Override
	public void setRoleName(String roleName) {
		super.setRoleName(roleName);
	}

	@Override
	public String getProductDescription() {
		return super.getProductDescription();
	}

	@Override
	public void setProductDescription(String productDescription) {
		super.setProductDescription(productDescription);
	}

}
