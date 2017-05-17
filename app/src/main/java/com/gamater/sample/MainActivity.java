package com.gamater.sample;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.gamater.account.po.MobUser;
import com.gamater.define.GPOrder;
import com.gamater.define.PaymentParam;
import com.gamater.payment.GamaterIABListener;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.game.GamaterSDKListener;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends Activity implements OnClickListener {
	private GamaterSDK gamaterSDK;
	private FirebaseAnalytics mFirebaseAnalytics;
	private MobUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		boolean isShowLog = true; // 是否允许SDK输出日志
		gamaterSDK = GamaterSDK.initSDK(this, "1023", isShowLog); // 游戏ID 有我方提供
		gamaterSDK.setGamaterSDKListener(new GamaterSDKListener() {
			@Override
			public void didLogout() {
				Log.e("MainActivity", "didLogout: 切换账号后游戏需要切换到登录界面: ");
			}

			@Override
			public void didLoginSuccess(final MobUser arg0) {
				user = arg0;
				Log.e("use", "getEmail" + user.getEmail() + "//getUserid:" + user.getUserid() + "//getToken" + user.getToken() + "//getType:" + user.getType());
				// 上传服务器角色名接口(登录成功,选择区服之后,进入游戏之前调用)
				roleReport();

				Bundle bundle = new Bundle();
				bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg0.getUserid());
				bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, user.getEmail());
				bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
				mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
			}
		});

		// 获取 key hash 示例代码
		try {
			PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("MainActivity:", "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

		gamaterSDK.initPayment(null, new GamaterIABListener() {
			@Override
			public void setupHelperFailed() {
				Log.e("MainActivity", "setupHelperFailed");
			}

			@Override
			public void paymentStart(String arg0) {
				Log.e("MainActivity", "paymentStart");
			}

			@Override
			public void paymentFailed(String arg0) {
				Log.e("MainActivity", "paymentFailed");
			}

			@Override
			public void otherPaymentFinish() {
				Log.e("MainActivity", "otherPaymentFinish");
			}

			@Override
			public void paymentSuccess(GPOrder order) {
				Log.e("MainActivity", "paymentSuccess");
			}

			@Override
			public void onPayType(int type) {
				// Log.e("MainActivity", "onPayType");
			}
		});
		initEditText(R.id.server_id);
		initEditText(R.id.server_name);
		initEditText(R.id.role_id);
		initEditText(R.id.role_name);
		initEditText(R.id.role_level);
		findViewById(R.id.btnToLogin).setOnClickListener(this);
		findViewById(R.id.btnRoleReport).setOnClickListener(this);
		findViewById(R.id.btnDefaultPay).setOnClickListener(this);
		findViewById(R.id.btnShowFacebook).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		gamaterSDK.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		gamaterSDK.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gamaterSDK.handlerResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gamaterSDK.destroy();
	}

	private PaymentParam getPaymentParam() {
		String serverId = "1";
		String serverName = "serverName";
		String account = user.getUserid();
		String roleId = "1";
		String roleName = "roleName";
		String productId = "gamater_test";
		String productDescription = "10元宝";
		float amount = 1.99f;
		String currency = "USD";
		String extra = "extra";
		return new PaymentParam(serverId, serverName, account, roleId, roleName, productId, productDescription, amount, currency, extra);
	}

	private void paymentDefault() {
		if (user == null) {
			Toast.makeText(this, "请先登录.....", Toast.LENGTH_LONG).show();
			return;
		}
		gamaterSDK.paymentDefault(this, getPaymentParam());
	}

	private void roleReport() {
		// 游戏角色职业，没有可传空字符串
		String roleProfession = "武士";
		String serverId = getEditText(R.id.server_id);
		serverId = serverId.length() == 0 ? "1" : serverId;
		String serverName = getEditText(R.id.server_name);
		serverName = serverName.length() == 0 ? "游戏一区" : serverName;
		String roleId = getEditText(R.id.role_id);
		roleId = roleId.length() == 0 ? "1" : roleId;
		String roleName = getEditText(R.id.role_name);
		roleName = roleName.length() == 0 ? "no name" : roleName;
		String roleLevel = getEditText(R.id.role_level);
		roleLevel = roleLevel.length() == 0 ? "1" : roleLevel;
		gamaterSDK.roleReport(roleProfession, serverId, serverName, roleId, roleName, Integer.valueOf(roleLevel));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnToLogin:
			gamaterSDK.popLoginView();
			break;
		case R.id.btnDefaultPay:
			paymentDefault();
			break;
		case R.id.btnRoleReport:
			// 上传服务器角色名（构造支付参数 ）接口(登录成功,选择区服之后,进入游戏时调用)
			roleReport();
			break;
		case R.id.btnShowFacebook:
			// Facebook集成功能（游戏主界面加UI）（Facebook官网点赞、分享、邀请好友送礼包）
			GamaterSDK.getInstance().showFacebook();

			// contentURL，要分享的链接、
			// contentTitle，表示链接中的内容的标题、
			// 内容的contentDescription，通常为 2-4个句子
			// imageURL，将在帖子中显示的缩略图的网址http://img.gamater.com/sgmala/images/20161208/5849029d28cf4.png
//			GamaterSDK.getInstance().shareToFb("http://wx.moreu.cn/mf/share.html?cdk=8jzgcxneejd5t", "玩全职法师，领分享好礼",
//					"领取《全职法师》分享礼包，体验最意想不到的冒险MMO8jzgcxneejd5t", "", new FbShareCallback() {
//						@Override
//						public void onShareFinish(Exception e, String postId) {
//							if (e == null) {
//								// 分享完成.....
//								Log.e("FB", "shareToFb 分享完成.....");
//							} else {
//								e.printStackTrace();
//								Log.e("FB", "shareToFb 分享错误....." + e.getMessage());
//							}
//						}
//
//						@Override
//						public void onShareCancel() {
//							// TODO Auto-generated method stub
//							Log.e("FB", "shareToFb 分享取消.....");
//						}
//					});

			break;
		default:
			break;
		}
	}

	public void initEditText(int id) {
		SharedPreferences sp = getSharedPreferences("EDIT_TEXT_DATA", 0);
		((EditText) findViewById(id)).setText(sp.getString("EDIT_TEXT_DATA_" + id, ""));
	}

	public void saveEditText() {
		String serverId = getEditText(R.id.server_id);
		String serverName = getEditText(R.id.server_name);
		String roleId = getEditText(R.id.role_id);
		String roleName = getEditText(R.id.role_name);
		String roleLevel = getEditText(R.id.role_level);
		SharedPreferences sp = getSharedPreferences("EDIT_TEXT_DATA", 0);
		Editor editor = sp.edit();
		editor.putString("EDIT_TEXT_DATA_" + R.id.server_id, serverId);
		editor.putString("EDIT_TEXT_DATA_" + R.id.server_name, serverName);
		editor.putString("EDIT_TEXT_DATA_" + R.id.role_id, roleId);
		editor.putString("EDIT_TEXT_DATA_" + R.id.role_name, roleName);
		editor.putString("EDIT_TEXT_DATA_" + R.id.role_level, roleLevel);
		editor.commit();
	}

	public String getEditText(int editTextId) {
		return ((EditText) findViewById(editTextId)).getText().toString().trim();
	}

}
