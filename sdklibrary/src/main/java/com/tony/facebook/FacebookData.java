package com.tony.facebook;

import org.json.JSONObject;

public class FacebookData {
	int type;
	String image;
	String name;
	int number;
	String key;
	String description;
	String detail;
	String url;
	int sort;
	int has_been_reached;
	int has_number;
	int has_send_gifts;
	String id;

	public FacebookData(JSONObject o) {
		type = o.optInt("type");
		image = o.optString("image");
		name = o.optString("name");
		number = o.optInt("number");
		key = o.optString("key");
		description = o.optString("description");
		detail = o.optString("detail");
		sort = o.optInt("sort");
		has_been_reached = o.optInt("has_been_reached");
		has_number = o.optInt("has_number");
		url = o.optString("url");
		has_send_gifts = o.optInt("has_send_gifts");
		id = o.optString("id");
	}
}
