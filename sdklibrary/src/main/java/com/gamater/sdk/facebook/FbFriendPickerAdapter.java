package com.gamater.sdk.facebook;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamater.util.ResourceUtil;
import com.squareup.picasso.Picasso;
import com.tony.viewinterface.BaseOnClickListener;

public class FbFriendPickerAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private ArrayList<FbInviteFriend> mItems;
	private Context mContext;
	private LinkedHashMap<String, String> mSelectedData;

	public FbFriendPickerAdapter(Context context,
			ArrayList<FbInviteFriend> items) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mItems = items;
		mSelectedData = new LinkedHashMap<String, String>();
		for (int i = 0; i < mItems.size(); i++) {
			mSelectedData.put(mItems.get(i).id, mItems.get(i).name);
		}
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public FbInviteFriend getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setSelectedData(LinkedHashMap<String, String> selectedData) {
		mSelectedData = selectedData;
	}

	public LinkedHashMap<String, String> getSelectedData() {
		return mSelectedData;
	}

	public int getSelectedCount() {
		return mSelectedData.size();
	}

	public String[] getSelectedFriendIds() {
		String[] ids = new String[mSelectedData.size()];
		int i = 0;
		for (Object data : mSelectedData.entrySet()) {
			Map.Entry entry = (Map.Entry) data;
			ids[i] = (String) entry.getKey();
			i++;
		}
		return ids;
	}

	public String[] getSelectedFriendNames() {
		String[] names = new String[mSelectedData.size()];
		int i = 0;
		for (Object data : mSelectedData.entrySet()) {
			Map.Entry entry = (Map.Entry) data;
			names[i] = (String) entry.getValue();
			i++;
		}
		return names;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					ResourceUtil.getLayoutId("vsgm_tony_item_image_picker"),
					null);
		}
		FbInviteFriend item = mItems.get(position);
		Holder holder = getHolder(convertView, position);

		Picasso.with(mContext)
				.load(item.picUrl)
				.resize(500, 500)
				.placeholder(
						ResourceUtil
								.getDrawableId("vsgm_tony_imagepicker_default_bg"))
				.into(holder.image);
		holder.select.setSelected(item.isSelected);
		holder.name.setText(item.name);
		convertView.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				Holder holder = (Holder) v.getTag();
				FbInviteFriend item = getItem(holder.position);
				if (holder.select.isSelected()) {
					mSelectedData.remove(item.id);
				} else {
					mSelectedData.put(item.id, item.id);
				}

				item.isSelected = !item.isSelected;
				holder.select.setSelected(!holder.select.isSelected());
			}
		});
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
		private TextView name;
		private int position;

		public Holder(View view, int pos) {
			image = (ImageView) view.findViewById(ResourceUtil
					.getId("imageView"));
			select = (ImageView) view.findViewById(ResourceUtil
					.getId("selectCheckBox"));
			name = (TextView) view.findViewById(ResourceUtil.getId("name"));
			name.setVisibility(View.VISIBLE);
			name.setBackgroundColor(0x50000000);
			position = pos;
		}
	}

}
