package com.tony.facebook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataHelper {
	private static DataHelper instance;
	private JSONObject data;
	private List<FacebookData> likeArray;
	private List<FacebookData> inviteArray;
	private FacebookData shareData;

	private DataHelper() {
	}

	public static DataHelper getInstance() {
		if (instance == null)
			instance = new DataHelper();
		return instance;
	}

	public void initData(JSONObject data) {
		this.data = data;
	}

	public List<FacebookData> getLikeArray() {
		if (this.data == null)
			return null;
		if (likeArray == null) {
			likeArray = new ArrayList<FacebookData>();
			JSONArray o = this.data.optJSONArray("1");
			for (int i = 0; i < o.length(); i++) {
				likeArray.add(new FacebookData(o.optJSONObject(i)));
			}
		}
		return likeArray;
	}

	public FacebookData getShareData() {
		if (this.data == null)
			return null;
		if (shareData == null) {
			shareData = new FacebookData(this.data.optJSONArray("2")
					.optJSONObject(0));
		}
		return shareData;
	}

	public List<FacebookData> getInviteArray() {
		if (this.data == null)
			return null;
		if (inviteArray == null) {
			inviteArray = new ArrayList<FacebookData>();
			JSONArray o = this.data.optJSONArray("3");
			if (o == null) {
				return null;
			}
			for (int i = 0; i < o.length(); i++) {
				inviteArray.add(new FacebookData(o.optJSONObject(i)));
			}
		}
		return inviteArray;
	}

	public void reset() {
		this.data = null;
		this.inviteArray = null;
		this.likeArray = null;
		this.shareData = null;
	}
}
