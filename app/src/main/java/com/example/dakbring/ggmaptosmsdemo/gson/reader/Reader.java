package com.example.dakbring.ggmaptosmsdemo.gson.reader;

import java.io.InputStream;

public interface Reader<T> {
	T parse(InputStream is, Class<T> classOfT);

	T parse(String xml, Class<T> classOfT);

    String parse(T t);

    String parse(T t, Class<T> classOfT);
}