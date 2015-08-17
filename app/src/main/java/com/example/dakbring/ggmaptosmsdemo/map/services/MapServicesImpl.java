package com.example.dakbring.ggmaptosmsdemo.map.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.dakbring.ggmaptosmsdemo.gson.GsonRequest;
import com.example.dakbring.ggmaptosmsdemo.gson.Network;
import com.example.dakbring.ggmaptosmsdemo.map.data.Route;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MapServicesImpl implements MapServices {

    private Network mNetword;

    public Document getRoutingDocument(Context context, LatLng start, LatLng end, String mode) throws Exception {


        Document doc = null;
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


//        try {
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
////            String json = convertStreamToString(in);
//            doc = builder.parse(in);
//        } finally {
//            urlConnection.disconnect();
//        }
        return doc;
    }

    private String convertStreamToString(final InputStream input) throws Exception {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            final StringBuffer sBuf = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sBuf.append(line);
            }
            return sBuf.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
