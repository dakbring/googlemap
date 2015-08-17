package com.example.dakbring.ggmaptosmsdemo.map.services;


import android.content.Context;

import com.example.dakbring.ggmaptosmsdemo.dagger.Injector;
import com.google.android.gms.maps.model.LatLng;
import org.w3c.dom.Document;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

public class RxMapControllerImpl implements RxMapController{

    @Inject MapServices mMapServices;

    public RxMapControllerImpl(){
        Injector.inject(this);
    }

    @Override
    public Observable<Document> getRoutingDocument(final Context context, final LatLng start, final LatLng end, final String mode){
        return Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {
                try {
                    Document document = mMapServices.getRoutingDocument(context, start, end, mode);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(document);
                        subscriber.onCompleted();
                    }
                } catch (Exception e){
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onError(e);
                        subscriber.onCompleted();
                    }
                }
            }
        });
    }
}
