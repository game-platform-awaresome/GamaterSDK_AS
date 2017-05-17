package com.gamater.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gamater.util.ResourceUtil;
import com.tony.circularprogress.CircularProgressBar;

public class OKgameDialogProcess extends Dialog {
	private CircularProgressBar progressBar;
	private boolean isCanCancel = true;

	public void setCanCancel(boolean b) {
		this.isCanCancel = b;
	}

	public OKgameDialogProcess(Context context) {
		this(context, ResourceUtil.getLayoutId("vsgm_tony_process"));
	}

	public OKgameDialogProcess(Context context, int layout) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layout);
		Window window = getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		View v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		progressBar = (CircularProgressBar) findViewById(ResourceUtil
				.getId("progress_bar"));
		progressBar.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isCanCancel && event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			dismiss();
			progressBar.stop();
			if (mCancelListener != null)
				mCancelListener.onCancel(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnCancelListener mCancelListener;

	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		super.setOnCancelListener(listener);
		this.mCancelListener = listener;
	}
}
