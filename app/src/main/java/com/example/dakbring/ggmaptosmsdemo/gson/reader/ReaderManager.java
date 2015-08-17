package com.example.dakbring.ggmaptosmsdemo.gson.reader;

public class ReaderManager {
	public static final int JSON = 2;

	private static ReaderManager mInstance;

	private ReaderManager() {
	}

	public static ReaderManager getInstance() {
		if (mInstance == null) {
			mInstance = new ReaderManager();
		}
		return mInstance;
	}

	public Reader getReader(int type) {
		if (type == JSON) {
			return new GsonReader();
		}
		return null;
	}

}