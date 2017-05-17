package com.gamater.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gamater.util.ResourceUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

/**
 * Created by tony on 13-6-19.
 */
public abstract class MyEndlessAdapter<T> extends BaseAdapter {
	public List<T> mItems;
	public int bottomResource;
	public Context mContext;
	public LayoutInflater mLayoutInflater;
	private boolean isLoadingMore;
	private boolean isLoadMore;
	public Class<T[]> clazz;

	public MyEndlessAdapter(Context context, int bottomResource,
			Class<T[]> clazz) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		this.bottomResource = bottomResource;
		this.clazz = clazz;
		mItems = new ArrayList<T>();
	}

	public MyEndlessAdapter(Context context, Class<T[]> clazz) {
		this(context, ResourceUtil.getLayoutId("vsgm_tony_bottom_loadmore"),
				clazz);
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}

	public void addItem(T item) {
		mItems.add(item);
	}

	@Override
	public final int getCount() {
		if (isLoadMore())
			return mItems.size() + 1;
		return mItems.size();
	}

	@Override
	public T getItem(int i) {
		if (i < mItems.size())
			return mItems.get(i);
		return null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public final int getViewTypeCount() {
		if (isLoadMore())
			return getNormalViewTypeCount() + 1;
		return getNormalViewTypeCount();
	}

	@Override
	public final int getItemViewType(int position) {
		if (isLoadMore() && position == mItems.size()) {
			return AdapterView.ITEM_VIEW_TYPE_IGNORE;// 适配器禁止条项的视图再利用
		}
		return getNormalViewType(position);
	}

	@Override
	public final View getView(int i, View view, ViewGroup viewGroup) {
		if (isLoadMore() && i == mItems.size()) {
			view = mLayoutInflater.inflate(bottomResource, viewGroup, false);
			if (!isLoadingMore) {
				isLoadingMore = true;
				loadMoreData();
			}
			return view;
		} else {
			return getNormalView(i, view, viewGroup);
		}
	}

	public boolean isLoadMore() {
		return isLoadMore;
	}

	public void setLoadMore(boolean isLoadMore) {
		this.isLoadMore = isLoadMore;
		this.isLoadingMore = false;
	}

	public int getNormalViewTypeCount() {
		return 1;
	}

	public int getNormalViewType(int position) {
		return super.getItemViewType(position);
	}

	public abstract View getNormalView(int position, View view,
			ViewGroup viewGroup);

	public abstract void loadMoreData();
}
