package com.gamater.sdk.imagepicker.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.gamater.util.ResourceUtil;
import com.squareup.picasso.Picasso;
import com.tony.viewinterface.BaseOnClickListener;

public class ImagePickerAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private ArrayList<ImagePicker> mItems;
	private boolean mIsMultiplePick; // 是否多选
	private int mMaxSelectCount; // 最大选择张数
	private Context mContext;
	private LinkedHashMap<Long, String> mSelectedData;

	private MultipleSelectListener multipleSelectListener;

	public interface MultipleSelectListener {
		void onSelectCountChange(int max, int current);
	}

	public void setMultipleSelectListener(
			MultipleSelectListener multipleSelectListener) {
		this.multipleSelectListener = multipleSelectListener;
	}

	public ImagePickerAdapter(Context context, ArrayList<ImagePicker> items) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mItems = items;
		mSelectedData = new LinkedHashMap<Long, String>();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public ImagePicker getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		mIsMultiplePick = isMultiplePick;
	}

	public void setMaxSelectCount(int maxSelectCount) {
		mMaxSelectCount = maxSelectCount;
	}

	public void setSelectedData(LinkedHashMap<Long, String> selectedData) {
		mSelectedData = selectedData;
	}

	public LinkedHashMap<Long, String> getSelectedData() {
		return mSelectedData;
	}

	public int getSelectedCount() {
		return mSelectedData.size();
	}

	public long[] getSelectedImageIds() {
		long[] ids = new long[mSelectedData.size()];
		int i = 0;
		for (Object data : mSelectedData.entrySet()) {
			Map.Entry entry = (Map.Entry) data;
			ids[i] = (Long) entry.getKey();
			i++;
		}
		return ids;
	}

	public String[] getSelectedImagePaths() {
		String[] paths = new String[mSelectedData.size()];
		int i = 0;
		for (Object data : mSelectedData.entrySet()) {
			Map.Entry entry = (Map.Entry) data;
			paths[i] = (String) entry.getValue();
			i++;
		}
		return paths;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					ResourceUtil.getLayoutId("vsgm_tony_item_image_picker"),
					null);
		}
		ImagePicker item = mItems.get(position);
		Holder holder = getHolder(convertView, position);

		Picasso.with(mContext)
				.load(new File(item.getPath()))
				.centerCrop()
				.resize(500, 500)
				.placeholder(
						ResourceUtil
								.getDrawableId("vsgm_tony_imagepicker_default_bg"))
				.into(holder.image);
		if (mIsMultiplePick) {
			holder.select.setSelected(item.isSelected());
			convertView.setOnClickListener(new BaseOnClickListener() {
				@Override
				public void onBaseClick(View v) {
					Holder holder = (Holder) v.getTag();
					if (mSelectedData.size() >= mMaxSelectCount
							&& !holder.select.isSelected()) {
						Toast.makeText(
								mContext,
								mContext.getString(
										ResourceUtil
												.getStringId("vsgm_tony_max_pick_count"),
										mMaxSelectCount), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					ImagePicker item = getItem(holder.position);
					if (holder.select.isSelected()) {
						mSelectedData.remove(item.getId());
					} else {
						mSelectedData.put(item.getId(), item.getPath());
					}

					item.setSelected(!item.isSelected());
					holder.select.setSelected(!holder.select.isSelected());
					if (multipleSelectListener != null) {
						multipleSelectListener.onSelectCountChange(
								mMaxSelectCount, getSelectedCount());
					}
				}
			});
		} else {
			holder.select.setVisibility(View.GONE);
		}

		return convertView;
	}

	private Holder getHolder(View view, int position) {
		Holder holder = (Holder) view.getTag();
		if (holder == null) {
			holder = new Holder(view, position);
			view.setTag(holder);
		}
		holder.position = position;
		return holder;
	}

	private class Holder {
		private ImageView image;
		private ImageView select;
		private int position;

		public Holder(View view, int pos) {
			image = (ImageView) view.findViewById(ResourceUtil
					.getId("imageView"));
			select = (ImageView) view.findViewById(ResourceUtil
					.getId("selectCheckBox"));
			position = pos;
		}
	}

}
