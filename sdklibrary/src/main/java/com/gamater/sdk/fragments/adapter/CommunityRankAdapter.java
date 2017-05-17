package com.gamater.sdk.fragments.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gamater.util.ResourceUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommunityRankAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater layoutInflater;

	public class RankEntity {
		private int rank;
		private String roleName;
		private int roleLevel;
		private String serverName;
		private int rankCount;
		private int followNum;
		private boolean isFollow;
	}

	public CommunityRankAdapter(Context ctx) {
		this.context = ctx;
		this.layoutInflater = LayoutInflater.from(ctx);
		this.data = new ArrayList<RankEntity>();
	}

	private List<RankEntity> data;

	@Override
	public int getCount() {
		// return data.size();
		return 100;
	}

	@Override
	public RankEntity getItem(int position) {
		if (position < data.size())
			return data.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(
					ResourceUtil.getLayoutId("vsgm_tony_community_rank_item"),
					null);
			holder.rankIndex = (TextView) convertView.findViewById(ResourceUtil
					.getId("rank_index"));
			holder.rankIndexImg = (ImageView) convertView
					.findViewById(ResourceUtil.getId("rank_index_img"));
			holder.rolePhoto = (ImageView) convertView
					.findViewById(ResourceUtil.getId("role_photo"));
			holder.roleName = (TextView) convertView.findViewById(ResourceUtil
					.getId("role_name"));
			holder.rankCount = (TextView) convertView.findViewById(ResourceUtil
					.getId("rank_count"));
			holder.roleLevel = (TextView) convertView.findViewById(ResourceUtil
					.getId("role_level"));
			holder.serverName = (TextView) convertView
					.findViewById(ResourceUtil.getId("server_name"));
			holder.followBtn = (Button) convertView.findViewById(ResourceUtil
					.getId("follow_btn"));
			holder.followCount = (TextView) convertView
					.findViewById(ResourceUtil.getId("follow_count"));

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.rankIndex.setText(position + 1 + "");
		return convertView;
	}

	class ViewHolder {
		TextView rankIndex;
		ImageView rankIndexImg;
		ImageView rolePhoto;
		TextView roleName;
		TextView roleLevel;
		TextView serverName;
		TextView rankCount;
		TextView followCount;
		Button followBtn;
	}

}
