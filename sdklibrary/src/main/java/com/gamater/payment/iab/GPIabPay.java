package com.gamater.payment.iab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.vending.util.IabHelper;
import com.android.vending.util.IabHelper.OnConsumeFinishedListener;
import com.android.vending.util.IabHelper.OnConsumeMultiFinishedListener;
import com.android.vending.util.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.util.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.util.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.util.IabResult;
import com.android.vending.util.Inventory;
import com.android.vending.util.Purchase;
import com.gamater.account.EventTracker;
import com.gamater.define.DeviceInfo;
import com.gamater.define.GPOrder;
import com.gamater.define.PaymentParam;
import com.gamater.define.SPUtil;
import com.gamater.payment.AcGameIAB;
import com.gamater.payment.PaymentHttpRequest;
import com.gamater.payment.PaymentHttpRequest.OrderIdCallback;
import com.gamater.util.LogUtil;

public class GPIabPay {
	private static GPIabPay m_instance;
	private Activity activity;
	private boolean isActivityNew;
	// private String base64EncodedPublicKey;
	private IabHelper helper;
	private boolean iabSetupOK;
	private List<String> skus;

	public synchronized static GPIabPay init(Activity a) {
		if (m_instance == null) {
			DeviceInfo.getInstance(a);
			m_instance = new GPIabPay(a);
		}
		if (m_instance.activity != a) {
			m_instance.activity = a;
			// if (m_instance.base64EncodedPublicKey != null) {
			m_instance.setupIabHelper(true);
			// }
		}
		return m_instance;
	}

	public synchronized static GPIabPay init(Activity a, List<String> skus) {
		if (m_instance == null) {
			DeviceInfo.getInstance(a);
			m_instance = new GPIabPay(a);
		}

		if (m_instance.activity != a || m_instance.skus == null
				|| m_instance.skus.size() == 0) {
			m_instance.activity = a;
			m_instance.skus = skus;
			// if (m_instance.base64EncodedPublicKey != null) {
			m_instance.setupIabHelper(true);
			// }
		}

		return m_instance;
	}

	public synchronized static GPIabPay getInstance() {
		return m_instance;
	}

	private GPIabPay(Activity a) {
		this.activity = a;
	}

	public boolean isIabSetupOK() {
		return iabSetupOK;
	}

	// String base64EncodedPublicKey = "";

	public void setupIabHelper(boolean enableDebugLogging) {
		// this.base64EncodedPublicKey = base64EncodedPublicKey;
		if (helper != null) {
			helper.dispose();
		}
		helper = new IabHelper(activity);
		helper.enableDebugLogging(enableDebugLogging);
		helper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
						AcGameIAB.getInstance().getAcGameIABListener().setupHelperFailed();
					}
					return;
				}
				if (helper == null) {
					return;
				}
				iabSetupOK = true;
				helper.queryInventoryAsync(mGotInventoryListener);
			}

		});
	}



	QueryInventoryFinishedListener mGotInventoryListener = new QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {
			if (helper == null)
				return;
			if (result == null || result.isFailure()) {
				return;
			}
			// 查询未消费的定单
//			List<Purchase> list = new ArrayList<Purchase>();
//			if (skus != null) {
//				 for (int i = 0; i < skus.size(); i++) {
//				 String sku = skus.get(i);
//				 Purchase purchase = inv.getPurchase(sku);
//					 if (purchase != null) {
//						 list.add(purchase);
//					 }
//				 }
//			} else {
//				 LogUtil.printHTTP("skus null......");
//			}
			List<Purchase> list = inv.getAllPurchases();
			list.addAll(getAllPurchases());
			if (list.size() > 0) {
				consumeAllPurchase(list);
			}
		}
	};

	OnIabPurchaseFinishedListener mPurchaseFinishedListener = new OnIabPurchaseFinishedListener() {

		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (helper == null) {
				if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
					AcGameIAB.getInstance().getAcGameIABListener().paymentFailed(result.getMessage());
				}
				return;
			}
			if (result.isFailure()) {
				if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
					AcGameIAB.getInstance().getAcGameIABListener().paymentFailed(result.getMessage());
				}
				return;
			}
			consumePurchase(purchase);
		}
	};

	OnConsumeFinishedListener mConsumeFinishedListener = new OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (helper == null) {
				return;
			}
			EventTracker.payEvent(1002, purchase);
			if (result.isSuccess()) {
				removePurchase(purchase);
				String orderId = purchase.getDeveloperPayload();
				JSONArray orders = SPUtil.getOrders(activity);

				for (int i = 0; i < orders.length(); i++) {
					try {
						JSONObject jsonOrder = (JSONObject) orders.get(i);
						GPOrder order = new GPOrder(jsonOrder);
						String oid = order.getOrderId();
						if (oid.equalsIgnoreCase(orderId)) {
							order.setPayToken(purchase.getToken());
							order.setGoogleOrderId(purchase.getOrderId());
							SPUtil.saveOrder(order.toJSON(), activity);
							AcGameIAB.getInstance().paymentValidate(order);

							if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
								AcGameIAB.getInstance().getAcGameIABListener().paymentSuccess(order);
							}
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
					AcGameIAB.getInstance().getAcGameIABListener().paymentFailed(result.getMessage());
				}
			}
		}
	};

	public IabHelper getHelper() {
		return helper;
	}

	public void consumePurchase(Purchase purchase) {
		EventTracker.payEvent(1001, purchase);
		savePurchase(purchase);
		if (helper != null)
			helper.consumeAsync(purchase, mConsumeFinishedListener);
	}

	public void consumeAllPurchase(List<Purchase> list) {
		if (helper != null)
			helper.consumeAsync(list, new OnConsumeMultiFinishedListener() {
				@Override
				public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
					for (int i = 0; i < purchases.size(); i++) {
						EventTracker.payEvent(1001, purchases.get(i));
						mConsumeFinishedListener.onConsumeFinished(purchases.get(i), results.get(i));
						LogUtil.print("consumeAllPurchase", "消费所有订单 " + i);
					}
				}
			});
	}

	public void savePurchase(Purchase purchase) {
		savePurchase(activity, purchase);
	}

	public void removePurchase(Purchase purchase) {
		removePurchase(activity, purchase);
	}

	public List<Purchase> getAllPurchases() {
		return getAllPurchases(activity);
	}

	public boolean handlerResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1234 && helper != null) {
			return helper.handleActivityResult(requestCode, resultCode, data);
		}

		return false;
	}

	public List<String> getSkus() {
		return skus;
	}

	public void setSkus(List<String> skus) {
		this.skus = skus;
	}

	// public String getBase64EncodedPublicKey() {
	// return base64EncodedPublicKey;
	// }

	public void payment(final PaymentParam param) {
		if (isIabSetupOK()) {
			PaymentHttpRequest request = new PaymentHttpRequest(activity);

			DeviceInfo di = DeviceInfo.getInstance(activity);
			request.initHeader(di);

			request.initParams(param.getServerId(), param.getRoleId(), param.getAccount(), param.getExtra(), (param.getAmount() + ""));
			LogUtil.printHTTP("request orderId begin...");
			request.asyncStart(new OrderIdCallback() {
				@Override
				public void onCallback(String orderId) {
					LogUtil.printHTTP("request orderId success : " + orderId);
					if (orderId != null) {
						try {
							GPOrder order = new GPOrder();
							order.setOrderId(orderId);
							order.setAccount(param.getAccount());
							order.setAmount(param.getAmount() + "");
							order.setRoleId(param.getRoleId());
							order.setServerId(param.getServerId());
							order.setSku(param.getSku());
							order.setExtra(param.getExtra());
							SPUtil.saveOrder(order.toJSON(), activity);
							if (helper != null) {
								if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
									AcGameIAB.getInstance().getAcGameIABListener().paymentStart(param.getSku());
								}
								helper.launchPurchaseFlow(activity, param.getSku(), 1234, mPurchaseFinishedListener, orderId);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		} else if (AcGameIAB.getInstance().getAcGameIABListener() != null) {
			AcGameIAB.getInstance().getAcGameIABListener().setupHelperFailed();
		}
	}

	public static void savePurchase(Context ctx, Purchase purchase) {
		SharedPreferences sp = ctx.getSharedPreferences(SPUtil.DATA_SAVE_NAME, 0);
		String purchasesStr = sp.getString(SPUtil.PURCHASE_KEY, null);
		try {
			JSONObject object;
			if (purchasesStr == null) {
				object = new JSONObject();
			} else {
				object = new JSONObject(purchasesStr);
			}
			if (!object.has(purchase.getDeveloperPayload())) {
				object.put(purchase.getDeveloperPayload(), purchase.toJson());
				sp.edit().putString(SPUtil.PURCHASE_KEY, object.toString()).commit();
			}
		} catch (JSONException e) {
		}
	}

	public static void removePurchase(Context ctx, Purchase purchase) {
		SharedPreferences sp = ctx.getSharedPreferences(SPUtil.DATA_SAVE_NAME, 0);
		String purchasesStr = sp.getString(SPUtil.PURCHASE_KEY, null);
		try {
			JSONObject object;
			if (purchasesStr == null) {
				return;
			}
			object = new JSONObject(purchasesStr);
			if (object.has(purchase.getDeveloperPayload())) {
				object.remove(purchase.getDeveloperPayload());
				sp.edit().putString(SPUtil.PURCHASE_KEY, object.toString()).commit();
			}
		} catch (JSONException e) {
		}
	}

	public static List<Purchase> getAllPurchases(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(SPUtil.DATA_SAVE_NAME, 0);
		String purchasesStr = sp.getString(SPUtil.PURCHASE_KEY, null);
		List<Purchase> result = new ArrayList<Purchase>();
		try {
			JSONObject object;
			if (purchasesStr == null) {
				return result;
			}
			object = new JSONObject(purchasesStr);
			Iterator i = object.keys();
			while (i.hasNext()) {
				result.add(new Purchase().initWithJson(object.getJSONObject(i.next().toString())));
			}
		} catch (Exception e) {
		} catch (Error e) {
		}
		return result;
	}
}
