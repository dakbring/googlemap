package com.example.dakbring.ggmaptosmsdemo.map.services;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

public interface MapServices {

    String MODE_DRIVING = "driving";
    String MODE_WALKING = "walking";

    Document getRoutingDocument(Context context, LatLng start, LatLng end, String mode) throws Exception;
}
