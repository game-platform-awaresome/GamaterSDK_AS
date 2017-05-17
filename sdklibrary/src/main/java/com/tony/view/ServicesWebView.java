package com.tony.view;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gamater.account.po.MobUser;
import com.gamater.common.Config;
import com.gamater.common.http.HeadersName;
import com.gamater.define.DeviceInfo;
import com.gamater.define.SPUtil;
import com.gamater.dialog.OKgameDialogProcess;
import com.gamater.dialog.util.DialogUtil;
import com.gamater.sdk.common.ServicesHelper;
import com.gamater.sdk.imagepicker.ImagePickerHelper;
import com.gamater.util.AppUtil;
import com.gamater.util.LogUtil;
import com.gamater.util.ResourceUtil;
import com.tony.floatmenu.SDKMenuManager;
import com.tony.view.ImagePickerView.ImagePickerViewListener;
import com.tony.viewinterface.WebInterface;

public class ServicesWebView extends WebView {
	private Activity mActivity;
	private ImagePickerViewListener mImagePickerListener;
	private boolean hasLoad = false;
	private boolean hasError = false;

	private int mMaxUploadSize = 1148576;

	public int getMaxUploadSize() {
		return mMaxUploadSize;
	}

	private OKgameDialogProcess mProcessDialog;

	public ServicesWebView(Activity activity) {
		super(activity);
		this.mActivity = activity;
		init();
	}

	public ServicesWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ServicesWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ServicesWebView(Context context) {
		super(context);
	}

	public void setActivity(Activity activity) {
		if (activity != null) {
			this.mActivity = activity;
			mProcessDialog = new OKgameDialogProcess(mActivity, ResourceUtil.getLayoutId("vsgm_tony_process"));
			mProcessDialog.setCancelable(true);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void init() {
		getSettings().setJavaScriptEnabled(true);
		getSettings().setDomStorageEnabled(true);
		getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
		String appCachePath = getContext().getCacheDir().getAbsolutePath();
		getSettings().setAppCachePath(appCachePath);
		getSettings().setAllowFileAccess(true);
		getSettings().setAppCacheEnabled(true);

		setVerticalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setHorizontalScrollbarOverlay(false);
		addJavascriptInterface(new SendImageInterface(), "vstarGameSendImage");
		addJavascriptInterface(new CloseInterface(), "vstarGameClose");
		addJavascriptInterface(new ReturnInterface(), "vstarGameUploadSize");
		addJavascriptInterface(new NoteInterface(), "vstarGameNote");
		addJavascriptInterface(new StopUploadInterface(), "vstarGameStopSendImage");
		addJavascriptInterface(new RefreshInterface(), "vstarGameRefresh");
		addJavascriptInterface(new SendMessageInterface(), "vstarGameSendMessage");
		addJavascriptInterface(new MsgCloseInterface(), "vstarGameMsgClose");
		addJavascriptInterface(new PageErrorInterface(), "vstarGameShowError");
		addJavascriptInterface(new UserDataInterface(), "vstarGameUserData");

		setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				hasError = false;
				showProcessDialog();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (!hasError) {
					hasLoad = true;
				}
				dismissProcessDialog();
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				hasError = true;
				super.onReceivedError(view, errorCode, description, failingUrl);
				loadErrorPage("");
			}
		});

		if (mActivity != null) {
			mProcessDialog = new OKgameDialogProcess(mActivity, ResourceUtil.getLayoutId("vsgm_tony_process"));
			mProcessDialog.setCancelable(true);
		}
	}

	public void setImagePickerListener(ImagePickerViewListener l) {
		mImagePickerListener = l;
	}

	public void loadErrorPage(String errorCode) {
		loadUrl("");
		String titleStr = getResources().getString(ResourceUtil.getStringId("vsgm_tony_pnf_title_text"));
		String msgStr = getResources().getString(ResourceUtil.getStringId("vsgm_tony_pnf_msg_btn_text"));
		String hintStr = getResources().getString(ResourceUtil.getStringId("vsgm_tony_pnf_hint_text"));
		String closeStr = getResources().getString(ResourceUtil.getStringId("vsgm_tony_pnf_close_btn_text"));
		String refreshStr = getResources().getString(ResourceUtil.getStringId("vsgm_tony_pnf_refresh_btn_text"));
		loadUrl("file:///android_asset/page_not_found.html?system=android&title_text="
				+ titleStr
				+ errorCode
				+ "&msg_btn_text="
				+ msgStr
				+ "&hint_text="
				+ hintStr
				+ "&close_btn_text="
				+ closeStr
				+ "&refresh_btn_text=" + refreshStr);
	}

	public void sendImageBegin() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadUrl("javascript:sendImgBegin()");
			}
		});
	}

	public void onImageUploadPercent(final int percent) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadUrl("javascript:sendImgProcess(" + percent + ")");
			}
		});
	}

	public void onImageUploadFinish(final int ret, final String msg) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadUrl("javascript:sendImgFinish(" + ret + ", '" + msg + "')");
			}
		});
		ServicesHelper.getInstance().cleanUploadCache();
	}

	public class SendImageInterface implements WebInterface {
		@JavascriptInterface
		public void sendImage() { // 上传图片开始
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadUrl("javascript:getMaxUploadSize()");
				}
			});
			ImagePickerHelper.getInstance().invokeImagePicker(mActivity, mImagePickerListener, maxImagePickCount);
		}
	}

	private int maxImagePickCount;

	public void setMaxImagePickCount(int count) {
		this.maxImagePickCount = count;
	}

	public void showProcessDialog() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (isShown() && !mProcessDialog.isShowing())
						mProcessDialog.show();
				} catch (Exception e) {
				}
			}
		});
	}

	public void dismissProcessDialog() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (isShown() && mProcessDialog.isShowing())
						mProcessDialog.dismiss();
				} catch (Exception e) {
				}
			}
		});
	}

	public class PageErrorInterface implements WebInterface {
		@JavascriptInterface
		public void show(final String errorCode) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					loadErrorPage(errorCode);
				}
			});
		}
	}

	public class MsgCloseInterface implements WebInterface {
		@JavascriptInterface
		public void click() {
			if (mListener != null)
				mListener.onClose();
			ServicesHelper.getInstance().resetWebView();
		}
	}

	public class CloseInterface implements WebInterface {
		@JavascriptInterface
		public void Wclick() {
			if (mListener != null)
				mListener.onClose();
		}
	}

	public class RefreshInterface implements WebInterface {
		@JavascriptInterface
		public void click() {
			if (mListener != null)
				mListener.onRefresh();
		}
	}

	public class SendMessageInterface implements WebInterface {
		@JavascriptInterface
		public void click(final String msg) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SPUtil.addPNFMessage(mActivity, msg);
					ServicesHelper.getInstance().sendPageNotFoundMessage(mActivity);
					Builder b = DialogUtil.showDialog(mActivity, Config.gmTitle, ResourceUtil.getStringId("vsgm_tony_pnf_dialog_msg"));
					b.setCancelable(false).setPositiveButton(ResourceUtil.getStringId("vsgm_tony_btn_done"), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mListener != null)
								mListener.onClose();
							ServicesHelper.getInstance().resetWebView();
						}
					});
					b.show();
				}
			});
		}
	}

	public class NoteInterface implements WebInterface {
		@JavascriptInterface
		public void noteAndroidUi(final int num) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SDKMenuManager.getInstance(mActivity).setServiceNoteNum(num);
				}
			});
		}
	}

	public class ReturnInterface implements WebInterface {
		@JavascriptInterface
		public void uploadSize(int size) {
			mMaxUploadSize = size;
		}
	}

	public class StopUploadInterface implements WebInterface {
		@JavascriptInterface
		public void stopUpload() {
			ServicesHelper.getInstance().cancelUpload();
		}
	}

	public class UserDataInterface implements WebInterface {
		@JavascriptInterface
		public void get() {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					LogUtil.printHTTP("UserDataInterface.get()");
					loadUrl("javascript:sendUserData('" + getUserData() + "')");
					LogUtil.printHTTP(getUserData());
				}
			});
		}
	}

	private String getUserData() {
		JSONArray orders = SPUtil.getOrdersLimit50(getContext());
		JSONObject data = new JSONObject();
		try {
			data.put("games-paymentOrders", orders);
			DeviceInfo info = DeviceInfo.getInstance(getContext());
			data.put(HeadersName.MODEL, Build.MODEL);
			data.put(HeadersName.IMEI, info.getIMEI());
			data.put(HeadersName.MAC, info.getMacAddress());
			data.put(HeadersName.ANDROID_ID, info.getAndroidId());
			data.put(HeadersName.APP_VERSION_CODE, info.getAppVersionCode());
			data.put(HeadersName.APP_VERSION_NAME, info.getappVersionName());
			data.put(HeadersName.SYS_VERSION_CODE, Build.VERSION.SDK_INT + "");
			data.put(HeadersName.SYS_VERSION_NAME, Build.VERSION.RELEASE);
			data.put(HeadersName.SCREEN_SIZE, info.getDisplaySize());
			data.put(HeadersName.LANGUAGE, info.getSystemLanguage());
			data.put(HeadersName.NET_TYPE, info.getNetType());
			data.put(HeadersName.CAMPAIGN, info.getCampaign());
			data.put(HeadersName.PACKAGE, info.getPackageName());
			data.put(HeadersName.PHONE_NUMBER, info.getPhoneNumber());
			data.put(HeadersName.COUNTRY_CODE, info.getCountryCode());
			data.put(HeadersName.CUSTOMER_ID, info.getCustomerId());
			data.put(HeadersName.PHONE_MNC, AppUtil.getMNC(getContext()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data.toString();
	}

	private ServicesViewListener mListener;

	public void setServicesViewListener(ServicesViewListener listener) {
		this.mListener = listener;
	}

	public interface ServicesViewListener {
		public void onClose();

		public void onRefresh();
	}

	public void onOffLine() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadUrl("javascript:setOffLine()");
			}
		});
	}

	public void removeFromParent() {
		try {
			ViewGroup parent = ((ViewGroup) getParent());
			if (parent != null)
				parent.removeView(this);
		} catch (Exception e) {
		}
	}

	public void restart() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadUrl("javascript:localStorage.clear()");
				loadUrl("javascript:setOnLine()");
			}
		});
	}

	private MobUser currentUser = new MobUser();

	public boolean checkUser(MobUser user) {
		MobUser u = currentUser;
		currentUser = user;
		return u == user;
	}

	private void deleteFile(File f) {
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				deleteFile(file);
			}
		} else if (f.exists()) {
		} else {
		}
	}

	public boolean hasLoad() {
		return this.hasLoad;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection connection = super.onCreateInputConnection(outAttrs);
		if (connection == null)
			return super.onCreateInputConnection(outAttrs);
		return new MyCustomInputConnection(connection, this, false);
	}

	public class MyCustomInputConnection extends BaseInputConnection {
		private InputConnection mConnection;

		public MyCustomInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		public MyCustomInputConnection(InputConnection connection,
				View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
			mConnection = connection;
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			if (beforeLength == 1 && afterLength == 0) {
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			return mConnection.commitText(text, newCursorPosition);
		}

		@Override
		public Editable getEditable() {
			return super.getEditable();
		}

		@Override
		public boolean beginBatchEdit() {
			return mConnection.beginBatchEdit();
		}

		@Override
		public boolean endBatchEdit() {
			return mConnection.endBatchEdit();
		}

		@Override
		public boolean clearMetaKeyStates(int states) {
			return mConnection.clearMetaKeyStates(states);
		}

		@Override
		public boolean commitCompletion(CompletionInfo text) {
			return mConnection.commitCompletion(text);
		}

		@Override
		public boolean finishComposingText() {
			return mConnection.finishComposingText();
		}

		@Override
		public int getCursorCapsMode(int reqModes) {
			return mConnection.getCursorCapsMode(reqModes);
		}

		@Override
		public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
			return mConnection.getExtractedText(request, flags);
		}

		@Override
		public CharSequence getTextBeforeCursor(int length, int flags) {
			return mConnection.getTextBeforeCursor(length, flags);
		}

		@Override
		public CharSequence getTextAfterCursor(int length, int flags) {
			return mConnection.getTextAfterCursor(length, flags);
		}

		@Override
		public boolean performEditorAction(int actionCode) {
			return mConnection.performEditorAction(actionCode);
		}

		@Override
		public boolean performContextMenuAction(int id) {
			return mConnection.performContextMenuAction(id);
		}

		@Override
		public boolean performPrivateCommand(String action, Bundle data) {
			return mConnection.performPrivateCommand(action, data);
		}

		@Override
		public boolean setComposingText(CharSequence text, int newCursorPosition) {
			return mConnection.setComposingText(text, newCursorPosition);
		}

		@Override
		public boolean setSelection(int start, int end) {
			return mConnection.setSelection(start, end);
		}

		// @Override
		// public boolean sendKeyEvent(KeyEvent event) {
		// return mConnection.sendKeyEvent(event);
		// }

		@Override
		public boolean reportFullscreenMode(boolean enabled) {
			return mConnection.reportFullscreenMode(enabled);
		}
	}

}
