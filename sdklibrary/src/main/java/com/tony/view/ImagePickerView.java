package com.tony.view;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;

import com.gamater.sdk.imagepicker.adapter.ImageFolder;
import com.gamater.sdk.imagepicker.adapter.ImagePicker;
import com.gamater.sdk.imagepicker.adapter.ImagePickerAdapter;
import com.gamater.sdk.imagepicker.adapter.ImagePickerAdapter.MultipleSelectListener;
import com.gamater.sdk.imagepicker.util.ImagePickerScrollListener;
import com.gamater.util.ResourceUtil;

public class ImagePickerView extends FrameLayout implements OnClickListener {
	private Activity mContext;

	private GridView mGridView;
	private ArrayList<ImagePicker> mItems;
	private ImagePickerAdapter mAdapter;
	private Button mDoneBtn;
	private Button mFolderBtn;
	private ListView mListView;
	private boolean mListViewAnimating;
	private ArrayList<ImageFolder> mFolderItems;
	private View mTouchPanel;
	private boolean mShowImageFirstTime = true;
	private int maxSelectCount;
	private boolean isMultiple;

	public void setMultiple(boolean isMultiMode) {
		this.isMultiple = isMultiMode;
	}

	public void setMaxSelectCount(int count) {
		this.maxSelectCount = count;
	}

	public ImagePickerView(Activity context) {
		this(context, 0);
	}

	public ImagePickerView(Activity context, int maxPickCount) {
		super(context);
		this.mContext = context;
		this.isMultiple = maxPickCount > 0;
		this.maxSelectCount = maxPickCount;
		init();
	}

	public void setActivity(Activity activity) {
		this.mContext = activity;
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(ResourceUtil.getLayoutId("vsgm_tony_imagepicker"), this);

		mDoneBtn = (Button) findViewById(ResourceUtil.getId("done_btn"));
		mDoneBtn.setOnClickListener(this);
		mFolderBtn = (Button) findViewById(ResourceUtil.getId("folder"));
		mFolderBtn.setClickable(false);
		findViewById(ResourceUtil.getId("folderPanel")).setOnClickListener(this);
		findViewById(ResourceUtil.getId("back_btn")).setOnClickListener(this);

		initImageGrid();
		executeGetImagesTask(null, false);
	}

	private void initImageGrid() {
		mGridView = (GridView) findViewById(ResourceUtil.getId("gridView"));
		mGridView.setOnScrollListener(new ImagePickerScrollListener());
		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mGridView.setNumColumns(4);
		} else {
			mGridView.setNumColumns(8);
		}
		mItems = new ArrayList<>();
		mAdapter = new ImagePickerAdapter(mContext, mItems);
		mGridView.setAdapter(mAdapter);
		mListView = (ListView) findViewById(ResourceUtil.getId("listView"));
		mFolderItems = new ArrayList<>();
		mTouchPanel = findViewById(ResourceUtil.getId("touchPanel"));
		mTouchPanel.setOnClickListener(this);

		if (!isMultiple) {
			mDoneBtn.setVisibility(View.GONE);
		} else {
			mAdapter.setMultiplePick(true);
			mAdapter.setMaxSelectCount(maxSelectCount);
			mAdapter.setMultipleSelectListener(new MultipleSelectListener() {
				@Override
				public void onSelectCountChange(int max, int current) {
					mDoneBtn.setText(mContext.getString(ResourceUtil.getStringId("vsgm_tony_finish")) + (current == 0 ? "" : ("(" + current + "/" + max + ")")));
				}
			});
		}

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImagePicker item = mAdapter.getItem(position);
				if (mListener != null)
					mListener.onPick(item.getPath());
			}
		});

	}

	private void closeFolderPanel(final String bucketId, final boolean changeData) {
		mListView.setVisibility(View.GONE);
		mTouchPanel.setVisibility(View.GONE);
		if (changeData) {
			executeGetImagesTask(bucketId, false);
		}
	}

	private void executeGetImagesTask(final String bucketId, final boolean isFolderData) {
		if (isFolderData) {
			mFolderItems.clear();
		} else {
			mItems.clear();
		}
		new Thread() {
			public void run() {
				ArrayList array = null;
				if (isFolderData) {
					array = getFolderImages();
				} else {
					array = getImages(bucketId);
				}
				final ArrayList result = array;
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (isFolderData) {
							mFolderItems.addAll(result);
						} else {
							if (result.isEmpty()) {
								findViewById(ResourceUtil.getId("bottomBar")).setVisibility(View.GONE);
							}
							mItems.addAll(result);
							mAdapter.notifyDataSetChanged();
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									mGridView.setSelection(0);
								}
							}, 0);
						}
					}
				});
			}
		}.start();
	}

	private ArrayList<ImagePicker> getImages(String bucketId) {
		ArrayList<ImagePicker> lists = new ArrayList<>();
		String[] columns;
		String orderBy = MediaStore.Images.Media._ID;
		String selection;
		Cursor cursor;
		if (bucketId != null) {
			columns = new String[] { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID };
			selection = MediaStore.Images.Media.BUCKET_ID + "=?";
			cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, new String[] { bucketId }, orderBy);
		} else {
			columns = new String[] { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
				selection = MediaStore.Images.Media.WIDTH + ">? and " + MediaStore.Images.Media.HEIGHT + ">?";
			} else {
				selection = null;
			}
			cursor = mContext.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					selection,
					selection == null ? null : new String[] { "140", "140" },
					orderBy);
		}

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				ImagePicker item = new ImagePicker();
				int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
				int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
				item.setId(cursor.getLong(idColumn));
				item.setPath(cursor.getString(dataColumn));
				File file = new File(item.getPath());
				if (file != null && file.exists() && file.length() > 0) {
					lists.add(item);
				}
			}
			cursor.close();
		}

		// show newest photo at beginning of the list
		Collections.reverse(lists);

		setSelectState(lists);
		return lists;
	}

	private void clearAllSelected(ArrayList<ImagePicker> items) {
		for (ImagePicker item : items) {
			item.setSelected(false);
		}
	}

	private void setSelectState(ArrayList<ImagePicker> items) {
		if (mShowImageFirstTime || items.size() == 0) {
			mShowImageFirstTime = false;
			return;
		}

		clearAllSelected(items);
		if (mAdapter.getSelectedCount() == 0) {
			return;
		}

		long[] ids = mAdapter.getSelectedImageIds();
		Arrays.sort(ids);
		long[] tempIds = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			tempIds[i] = ids[ids.length - 1 - i];
		}

		long id;
		// if (items.get(0).getViewType() == 1) {
		// id = items.get(1).getId();
		// } else {
		id = items.get(0).getId();
		// }
		boolean b = false;
		int i = 0;
		int j = 0;
		if (id < tempIds[0]) {
			for (; i < tempIds.length; i++) {
				if (tempIds[i] <= id) {
					break;
				}
			}
		}
		for (; i < tempIds.length; i++) {
			if (b) {
				j++;
			}
			b = true;
			for (; j < items.size(); j++) {
				ImagePicker item = items.get(j);
				if (item.getId() == tempIds[i]) {
					item.setSelected(true);
					break;
				}
			}
		}
	}

	private ArrayList<ImageFolder> getFolderImages() {
		ArrayList<ImageFolder> lists = new ArrayList<ImageFolder>();
		String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID };
		String orderBy = MediaStore.Images.Media._ID;
		String selection;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			selection = MediaStore.Images.Media.WIDTH + ">? and " + MediaStore.Images.Media.HEIGHT + ">?";
		} else {
			selection = null;
		}
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
				selection,
				selection == null ? null : new String[] { "140", "140" },
				orderBy);
		if (cursor != null && cursor.getCount() > 0) {
			ArrayList<String> bucketIds = new ArrayList<String>();
			cursor.moveToLast();
			ImageFolder item = new ImageFolder();
			item.setCount(null);
			item.setSelected(true);
			int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			item.setImgPath(cursor.getString(dataColumn));
			int folderIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
			String bucketId = cursor.getString(folderIdColumn);
			if (!bucketIds.contains(bucketId)) {
				bucketIds.add(bucketId);
				lists.add(item);
			}
			while (cursor.moveToPrevious()) {
				folderIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
				bucketId = cursor.getString(folderIdColumn);
				if (!bucketIds.contains(bucketId)) {
					bucketIds.add(bucketId);
				}
			}
			cursor.close();

			for (int i = 0; i < bucketIds.size(); i++) {
				String[] projection2 = { MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA };
				String orderBy2 = MediaStore.Images.Media.BUCKET_ID;
				String selection2 = "bucket_id=" + bucketIds.get(i);
				Cursor cursor2 = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2, selection2, null, orderBy2);
				if (cursor2 != null && cursor2.getCount() > 0) {
					item = new ImageFolder();
					item.setCount(String.valueOf(cursor2.getCount()));
					cursor2.moveToLast();
					folderIdColumn = cursor2.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
					item.setBucketId(cursor2.getString(folderIdColumn));
					int folderColumn = cursor2.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
					item.setName(cursor2.getString(folderColumn));
					int dataColumn2 = cursor2.getColumnIndex(MediaStore.Images.Media.DATA);
					item.setImgPath(cursor2.getString(dataColumn2));
					lists.add(item);
					cursor2.close();
				}
			}
		}
		return lists;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		if (mListView.getVisibility() == View.VISIBLE) {
			closeFolderPanel(null, false);
			return;
		}
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

	@Override
	public void onClick(View v) {
		if (v.getId() == ResourceUtil.getId("folderPanel")) {
			if (mListViewAnimating) {
				return;
			}
			if (mListView.getVisibility() == View.GONE) {
				mListView.setVisibility(View.VISIBLE);
				mTouchPanel.setVisibility(View.VISIBLE);
			} else {
				closeFolderPanel(null, false);
			}
		} else if (v.getId() == ResourceUtil.getId("touchPanel")) {
			if (mListViewAnimating) {
				return;
			}
			closeFolderPanel(null, false);
		} else if (v.getId() == ResourceUtil.getId("back_btn")) {
			onBackPressed();
		} else if (v.getId() == ResourceUtil.getId("done_btn")) {
			String[] paths = mAdapter.getSelectedImagePaths();
			if (mListener != null && paths != null && paths.length > 0)
				mListener.onPick(paths);
		}
	}

	private ImagePickerViewListener mListener;

	public void setImagePickerListener(ImagePickerViewListener listener) {
		this.mListener = listener;
	}

	public interface ImagePickerViewListener {
		public void onCancel();

		public void onPick(String... path);

	}
}
