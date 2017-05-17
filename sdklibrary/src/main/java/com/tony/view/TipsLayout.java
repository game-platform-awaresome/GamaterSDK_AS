package com.tony.view;

import com.gamater.util.ResourceUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TipsLayout extends RelativeLayout {
	public static final int LOAD_ING = 0;
	public static final int LOAD_FAILED = 1;
	public static final int TYPE_CUSTOM_VIEW = 2;
	public static final int LOAD_FAILED_NET_ERROR = 3;
	public static final int LOAD_SUCCESS_NO_DATA = 4;

	private View mLoadingView;

	private View mLoadFaileView;

	private TextView mFailedTv;
	private Button mFailedBtn;

	private Context mcontext;

	public TipsLayout(Context context) {
		this(context, null);
		this.mcontext = context;
	}

	public TipsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mcontext = context;
	}

	public TipsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mcontext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		mLoadingView = inflater.inflate(
				ResourceUtil.getLayoutId("vsgm_tony_process"), null);
		// mLoadingView.setBackgroundColor(Color.WHITE);
		mLoadFaileView = inflater.inflate(
				ResourceUtil.getLayoutId("vsgm_tony_load_failed"), null);

		LayoutParams rlp = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);

		mFailedTv = (TextView) mLoadFaileView.findViewById(ResourceUtil
				.getId("tv_loading_wrong"));
		mFailedBtn = (Button) mLoadFaileView.findViewById(ResourceUtil
				.getId("btn_load_failed_refresh"));

		addView(mLoadingView, rlp);
		addView(mLoadFaileView, rlp);

	}

	/**
	 * 设置刷新按钮点击事件
	 * 
	 * @param listener
	 */
	public void setOnRefreshButtonClickListener(OnClickListener listener) {
		mFailedBtn.setOnClickListener(listener);
	}

	/**
	 * 显示提示界面
	 * 
	 * @param showType
	 *            显示类型，分为显示loading和显示刷新按钮
	 */
	public void show(int showType) {
		setVisibility(View.VISIBLE);
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setVisibility(View.GONE);
		}
		switch (showType) {
		case LOAD_ING:
			getChildAt(0).setVisibility(View.VISIBLE);
			break;
		case LOAD_FAILED:
			getChildAt(1).setVisibility(View.VISIBLE);
			mFailedTv.setVisibility(View.VISIBLE);
			mFailedBtn.setVisibility(View.VISIBLE);
			mFailedTv.setText(mcontext.getResources().getString(
					ResourceUtil.getStringId("vsgm_tony_load_wrong")));
			break;
		case TYPE_CUSTOM_VIEW:
			// 显示自定义页面
			if (getChildCount() == 3) {
				getChildAt(2).setVisibility(View.VISIBLE);
			}
			break;
		case LOAD_FAILED_NET_ERROR:
			getChildAt(1).setVisibility(View.VISIBLE);
			mFailedTv.setVisibility(View.VISIBLE);
			mFailedBtn.setVisibility(View.VISIBLE);
			mFailedTv.setText(mcontext.getResources().getString(
					ResourceUtil.getStringId("vsgm_tony_err_unknown")));
			break;
		case LOAD_SUCCESS_NO_DATA:
			// 加载完成，但是没有数据
			getChildAt(1).setVisibility(View.VISIBLE);
			mFailedTv.setVisibility(View.VISIBLE);
			mFailedBtn.setVisibility(View.GONE);
			mFailedTv.setText(mcontext.getResources().getString(
					ResourceUtil.getStringId("vsgm_tony_load_no_data")));
			break;
		}

		// if (mLoadingView != null
		// && mLoadingView.getVisibility() == View.VISIBLE) {
		// mLoadingView.findViewById(R.id.progress_loading_bar).setVisibility(
		// View.VISIBLE);
		// } else {
		// mLoadingView.findViewById(R.id.progress_loading_bar).setVisibility(
		// View.GONE);
		// }
	}

	/**
	 * 隐藏提示界面
	 */
	public void hide() {
		setVisibility(View.GONE);
	}

	/**
	 * 设置自定义提示View
	 * 
	 * @param customTipsView
	 */
	public void setCustomView(View customTipsView) {
		if (getChildCount() == 2) {
			LayoutParams rlp = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			rlp.addRule(RelativeLayout.CENTER_IN_PARENT);

			addView(customTipsView, rlp);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setLoadFailViewBackgroundColor(int color) {
		mLoadFaileView.setBackgroundColor(color);
	}

}
