package com.example.dakbring.ggmaptosmsdemo.map.services;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import rx.Observable;

public interface RxMapController {

    Observable<Document> getRoutingDocument(Context context, LatLng start, LatLng end, String mode);
}
