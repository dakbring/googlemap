package com.example.dakbring.ggmaptosmsdemo.gson;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.dakbring.ggmaptosmsdemo.gson.reader.Reader;
import com.example.dakbring.ggmaptosmsdemo.gson.reader.ReaderManager;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
	private static final String TAG = "GsonRequest";

	private final Class<T> mClazz;
	private final Map<String, String> mHeaders;
	private final Listener<T> mListener;
	private ErrorListener mErrorListener;
	private byte[] mBody;
	private String mBodyContentType;
	private String mId;

	public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener,
			ErrorListener errorListener, byte[] body, String contentType) {
		super(method, url, errorListener);
		this.mClazz = clazz;
		this.mHeaders = headers;
		this.mListener = listener;
		this.mErrorListener = errorListener;
		this.mBody = body;
		this.mBodyContentType = contentType;
	}

	public GsonRequest(String id, int method, String url, Class<T> clazz, Map<String, String> headers,
			Listener<T> listener, ErrorListener errorListener, byte[] body, String contentType) {
		super(method, url, errorListener);
		this.mId = id;
		this.mClazz = clazz;
		this.mHeaders = headers;
		this.mListener = listener;
		this.mErrorListener = errorListener;
		this.mBody = body;
		this.mBodyContentType = contentType;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return this.mBody != null ? this.mBody : super.getBody();
	}

	@Override
	public String getBodyContentType() {
		return mBodyContentType != null ? this.mBodyContentType : super.getBodyContentType();
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			Reader<T> reader = ReaderManager.getInstance().getReader(ReaderManager.JSON);
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			T t = reader.parse(json, mClazz);
			return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		if (mListener != null) {
			if (mListener instanceof AbsResponseListener) {
				((AbsResponseListener) mListener).setId(mId);
			}
			mListener.onResponse(response);
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		if (mErrorListener != null) {
			if (mErrorListener instanceof AbsErrorListener) {
				((AbsErrorListener) mErrorListener).setId(mId);
			}
			mErrorListener.onErrorResponse(error);
		}
	}

	public void setUrl(String url) {
		try {
			if (getClass().getSuperclass() == Request.class) {
				Field field = getClass().getSuperclass().getDeclaredField("mUrl");
				field.setAccessible(true);
				field.set(this, url);
			} else if (getClass().getSuperclass().getSuperclass() == Request.class) {
				Field field = getClass().getSuperclass().getSuperclass().getDeclaredField("mUrl");
				field.setAccessible(true);
				field.set(this, url);
			}
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "setUrl", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "setUrl", e);
		}
	}

	public void setId(String id) {
		mId = id;
	}

	public String getId() {
		return mId;
	}
}