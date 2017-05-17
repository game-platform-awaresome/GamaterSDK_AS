package com.gamater.define;

import com.gamater.common.http.MD5;
import com.gamater.common.http.MVHttpRequest;
import com.gamater.common.http.WebAPI;

public class AcGameIABUtil {

	public static void initRequestOrderIdParam(MVHttpRequest request, PaymentParam param) {
		long time = System.currentTimeMillis() / 1000;

		request.setParam(param);

		String serverId = param.getServerId();
		String account = param.getAccount();
		String roleId = param.getRoleId();

		request.addPostValue("serverId", serverId);
		request.addPostValue("account", account);
		request.addPostValue("roleId", roleId);
		request.addPostValue("extra", param.getExtra());

		request.addPostValue("time", time + "");
		request.addPostValue("flag", MD5.crypt(serverId + account + roleId + WebAPI.PAY_KEY + time + ""));
	}

	public static void initPaymentValidate(MVHttpRequest request, GPOrder order) {
		long time = System.currentTimeMillis() / 1000;

		request.setOrder(order);
		String orderId = order.getOrderId();
		request.addPostValue(ParameterKey.ORDER_ID, orderId);
		String productId = order.getSku();
		request.addPostValue(ParameterKey.PRODUCT_ID, productId);
		request.addPostValue(ParameterKey.PRODUCT_TYPE, order.getPayType());
		request.addPostValue(ParameterKey.SERVER_ID, order.getServerId());
		request.addPostValue(ParameterKey.ACCOUNT, order.getAccount());
		request.addPostValue(ParameterKey.ROLE_ID, order.getRoleId());
		request.addPostValue(ParameterKey.TOKEN, order.getPayToken());
		request.addPostValue(ParameterKey.GP_ORDER_ID, order.getGoogleOrderId());
		request.addPostValue(ParameterKey.AMOUNT, order.getAmount());
		request.addPostValue(ParameterKey.TIME, time + "");
		request.addPostValue(ParameterKey.FLAG, MD5.crypt(orderId + productId + WebAPI.PAY_KEY + time));
	}

}
