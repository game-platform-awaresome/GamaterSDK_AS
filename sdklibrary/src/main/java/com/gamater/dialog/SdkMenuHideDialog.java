package com.gamater.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class SdkMenuHideDialog extends Dialog {

	public SdkMenuHideDialog(Context context) {
		super(context);
	}

	public SdkMenuHideDialog(Context context, int theme) {
		super(context, theme);
	}

	public SdkMenuHideDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public static class Builder {
		private Context context;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;
		private OnCancelListener onCancelListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setPositiveButton(OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener listener) {
			this.onCancelListener = listener;
			return this;
		}

		public SdkMenuHideDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final SdkMenuHideDialog dialog = new SdkMenuHideDialog(context, ResourceUtil.getStyleId("vsgm_tony_hide_dialog"));
			View layout = inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_hide_sdkmenu_dialog"), null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			if (positiveButtonClickListener != null) {
				layout.findViewById(ResourceUtil.getId("btn_hide_sdkmenu_commit")).setOnClickListener(new BaseOnClickListener() {
					public void onBaseClick(View v) {
						positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
					}
				});
				layout.findViewById(ResourceUtil.getId("btn_hide_sdkmenu_cancel")).setOnClickListener(new BaseOnClickListener() {
					public void onBaseClick(View v) {
						negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
			if (onCancelListener != null) {
				dialog.setOnCancelListener(onCancelListener);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}

}
