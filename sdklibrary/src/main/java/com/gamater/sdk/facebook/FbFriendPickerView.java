package com.gamater.sdk.facebook;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.gamater.sdk.imagepicker.util.ImagePickerScrollListener;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class FbFriendPickerView extends FrameLayout {
	private Activity mContext;

	private GridView mGridView;
	private ArrayList<FbInviteFriend> mItems;
	private FbFriendPickerAdapter mAdapter;
	private Button mDoneBtn;

	public FbFriendPickerView(Activity context, ArrayList<FbInviteFriend> list) {
		super(context);
		this.mContext = context;
		this.mItems = list;
		init();
	}

	public void setActivity(Activity activity) {
		this.mContext = activity;
	}

	private BaseOnClickListener onClickListener = new BaseOnClickListener() {
		@Override
		public void onBaseClick(View v) {
			if (v.getId() == ResourceUtil.getId("back_btn")) {
				onBackPressed();
			} else if (v.getId() == ResourceUtil.getId("done_btn")) {
				String[] id = mAdapter.getSelectedFriendIds();
				String[] name = mAdapter.getSelectedFriendNames();
				if (mListener != null && id != null && name != null)
					mListener.onPick(id, name);
			}
		}
	};

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_imagepicker"),
				this);

		mDoneBtn = (Button) findViewById(ResourceUtil.getId("done_btn"));
		mDoneBtn.setOnClickListener(onClickListener);
		findViewById(ResourceUtil.getId("folderPanel")).setOnClickListener(
				onClickListener);
		findViewById(ResourceUtil.getId("back_btn")).setOnClickListener(
				onClickListener);

		initImageGrid();
	}

	private void initImageGrid() {
		mGridView = (GridView) findViewById(ResourceUtil.getId("gridView"));
		TextView title = (TextView) findViewById(ResourceUtil
				.getId("picker_title"));
		title.setText(ResourceUtil.getStringId("vsgm_tony_pick_friend"));
		mGridView.setOnScrollListener(new ImagePickerScrollListener());
		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mGridView.setNumColumns(4);
		} else {
			mGridView.setNumColumns(8);
		}
		mAdapter = new FbFriendPickerAdapter(mContext, mItems);
		mGridView.setAdapter(mAdapter);

		// mGridView.setOnItemClickListener(new
		// AdapterView.OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// ImagePicker item = mAdapter.getItem(position);
		// if (mListener != null)
		// mListener.onPick(item.getPath());
		// }
		// });

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		onDestory();
		if (mListener != null)
			mListener.onCancel();
	}

	public void removeFromParent() {
		ViewGroup parent = ((ViewGroup) getParent());
		if (parent != null)
			parent.removeView(this);
	}

	public void onDestory() {
		removeFromParent();
		try {
			Method m = Activity.class.getDeclaredMethod("onResume", new Class[0]);
			m.setAccessible(true);
			m.invoke(mContext, new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private FbFriendPickerViewListener mListener;

	public void setFbFriendPickerViewListener(
			FbFriendPickerViewListener listener) {
		this.mListener = listener;
	}

	public interface FbFriendPickerViewListener {
		public void onCancel();

		public void onPick(String[] id, String[] name);

	}
}
