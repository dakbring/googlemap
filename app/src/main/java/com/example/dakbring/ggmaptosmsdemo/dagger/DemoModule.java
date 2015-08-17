package com.example.dakbring.ggmaptosmsdemo.dagger;


import com.example.dakbring.ggmaptosmsdemo.DemoApplication;
import com.example.dakbring.ggmaptosmsdemo.MainActivity;
import com.example.dakbring.ggmaptosmsdemo.deeplinking.DeepLinkActivity;
import com.example.dakbring.ggmaptosmsdemo.gson.Network;
import com.example.dakbring.ggmaptosmsdemo.map.GoogleMapActivity;
import com.example.dakbring.ggmaptosmsdemo.map.services.MapServices;
import com.example.dakbring.ggmaptosmsdemo.map.services.MapServicesImpl;
import com.example.dakbring.ggmaptosmsdemo.map.services.RxMapController;
import com.example.dakbring.ggmaptosmsdemo.map.services.RxMapControllerImpl;
import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation;

import android.content.Context;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true,
        injects = {

                DemoApplication.class,

                //Activity
                MainActivity.class,
                GoogleMapActivity.class,
                DeepLinkActivity.class,

                //Others
                CurrentLocation.class,
                RxMapControllerImpl.class
        }
)
public class DemoModule {

    @Singleton
    @Provides
    Context provideAppContext() {
        return DemoApplication.getInstance().getApplicationContext();
    }

    @Singleton
    @Provides
    CurrentLocation provideCurrentLocation() {
        return new CurrentLocation();
    }

    @Singleton
    @Provides
    LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Singleton
    @Provides
    Network provideNetwork(Context context){return Network.getInstance(context);}

    @Singleton
    @Provides
    MapServices provideMapServices() {
        return new MapServicesImpl();
    }

    @Singleton
    @Provides
    RxMapController provideRxMapController() {
        return new RxMapControllerImpl();
    }
}
