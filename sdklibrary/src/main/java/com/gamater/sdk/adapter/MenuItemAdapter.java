package com.gamater.sdk.adapter;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuItemAdapter extends BaseAdapter {
	private final Context context;
	private final Menu menu;

	public MenuItemAdapter(final Context context, final Menu menu) {
		this.context = context;
		this.menu = menu;
	}

	@Override
	public int getCount() {
		return this.menu.size();
	}

	@Override
	public Object getItem(final int position) {
		return this.menu.getItem(position);
	}

	@Override
	public long getItemId(final int position) {
		return this.menu.getItem(position).getItemId();
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final MenuItem item = this.menu.getItem(position);
		final View view;
		final ViewHolder holder;

		if (convertView == null) {
			view = View.inflate(this.context, 0, null);
			holder = new ViewHolder((ImageView) view.findViewById(0),
					(TextView) view.findViewById(0));

			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		holder.icon.setImageDrawable(item.getIcon());
		holder.title.setText(item.getTitle());

		return view;
	}

	private final class ViewHolder {
		public ImageView icon;
		public TextView title;

		public ViewHolder(final ImageView icon, final TextView title) {
			this.icon = icon;
			this.title = title;
		}
	}
}
