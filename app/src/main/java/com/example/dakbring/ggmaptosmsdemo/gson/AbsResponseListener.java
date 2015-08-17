package com.example.dakbring.ggmaptosmsdemo.gson;

import com.android.volley.Response;

public abstract class AbsResponseListener<T> implements Response.Listener<T> {

	private String mId;
	private String mName;

	public void setId(String id) {
		mId = id;
	}

	public String getId() {
		return mId;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}
}
