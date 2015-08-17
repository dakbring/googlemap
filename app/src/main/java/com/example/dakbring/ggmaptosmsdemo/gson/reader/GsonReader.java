package com.example.dakbring.ggmaptosmsdemo.gson.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonReader<T> implements Reader<T> {
	private final Gson mGson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();

	@Override
	public T parse(InputStream is, Class<T> classOfT) {
		try {
			String xml = getStringFromInputStream(is);
			return parse(xml, classOfT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public T parse(String xml, Class<T> classOfT) {
		return mGson.fromJson(xml, classOfT);
	}

    @Override
    public String parse(T t, Class<T> classOfT) {
        return mGson.toJson(t, classOfT);
    }

    @Override
    public String parse(T t) {
        return mGson.toJson(t);
    }

    private String getStringFromInputStream(InputStream stream) throws IOException {
		int n = 0;
		char[] buffer = new char[1024 * 4];
		InputStreamReader reader = new InputStreamReader(stream, "UTF8");
		StringWriter writer = new StringWriter();
		while (-1 != (n = reader.read(buffer)))
			writer.write(buffer, 0, n);
		return writer.toString();
	}

	public class JsonDateDeserializer implements JsonDeserializer<Date> {
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String s = json.getAsJsonPrimitive().getAsString();
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
			DateTime dateTime = parser.parseDateTime(s);
			return dateTime.toDate();
		}
	}
}