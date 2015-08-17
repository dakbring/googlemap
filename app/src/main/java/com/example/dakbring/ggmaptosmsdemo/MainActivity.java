package com.example.dakbring.ggmaptosmsdemo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.dakbring.ggmaptosmsdemo.dialogs.DialogManager;
import com.example.dakbring.ggmaptosmsdemo.dialogs.SmsDialog;
import com.example.dakbring.ggmaptosmsdemo.gson.GsonRequest;
import com.example.dakbring.ggmaptosmsdemo.gson.Network;
import com.example.dakbring.ggmaptosmsdemo.map.data.Route;
import com.example.dakbring.ggmaptosmsdemo.map.services.MapServices;
import com.example.dakbring.ggmaptosmsdemo.map.services.MapUtils;
import com.example.dakbring.ggmaptosmsdemo.map.services.RxMapController;
import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation;
import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation.ProviderStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.w3c.dom.Document;


import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;


public class MainActivity extends BaseActivity implements CurrentLocation.OnReceivedLocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    CurrentLocation mCurrentLocation;

    @Inject
    RxMapController mMapController;

    @Inject
    Network mNetwork;

    @Bind(R.id.loading_group)
    LinearLayout mLoadingGroup;

    @Bind(R.id.btn_group)
    LinearLayout mBtnGroup;

    private Subject<Location, Location> mLocationSubject;

    private Location mLocation;
    private LatLng mLatLng;
    private Marker mMarker;
    private PolylineOptions mPolyline;
    private DialogManager mDialogManager;
    private GoogleMap mGoogleMap;

    private LatLng mHomeLatLng = new LatLng(10.772412, 106.694049);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDialogManager = DialogManager.getInstance(this);

        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mHomeLatLng, 16);
                mGoogleMap.animateCamera(cameraUpdate);
                mGoogleMap.addMarker(new MarkerOptions().position(mHomeLatLng).title("Your home location"));
            }
        });

//        LatLng here = new LatLng(10.775556, 106.666080);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationSubject != null) {
            mLocationSubject.onCompleted();
            mLocationSubject = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribe();
        if (mLocation != null) {
            showLoadingProgress(false);
            mLocation = null;
        }
    }

    @OnClick({R.id.btn_getLocation, R.id.btn_sendMsg})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getLocation:
                if (mMarker != null) {
                    mMarker.remove();
                }
                startGettingLocation();
                break;
            case R.id.btn_sendMsg:
                submitLocation(mLocation);
        }
    }

    private void subscribe() {
        if (mLocationSubject == null) {
            mLocationSubject = PublishSubject.create();
        }
        handleSubscription(mLocationSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        showLoadingProgress(false);
                        loadCurrentLocationOnMap();
                        try{
                            drawDirection(mLatLng, mHomeLatLng, MapServices.MODE_DRIVING);
                        }catch (Exception e){
                            Log.e(TAG, e.toString());
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showLoadingProgress(false);
                        Toast.makeText(MainActivity.this, "Cannot load your location, please try again", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void drawDirectionRoute(LatLng location1, LatLng location2) {
        handleSubscription(mMapController.getRoutingDocument(this, location1, location2, MapServices.MODE_DRIVING)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Document>() {
                    @Override
                    public void call(Document document) {
                        if (document != null) {
                            ArrayList<LatLng> directionPoint = MapUtils.getDirection(document);
                            PolylineOptions rectLine = new PolylineOptions()
                                    .width(8)
                                    .color(Color.GRAY);

                            for (int i = 0; i < directionPoint.size(); i++) {
                                rectLine.add(directionPoint.get(i));
                            }
                            mGoogleMap.addPolyline(rectLine);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, throwable.toString());
                    }
                }));
    }

    private void drawDirection(LatLng start, LatLng end, String mode)throws Exception{
        URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&language=vi&sensor=false&units=metric&mode=driving");

        mGetRouteRequest.setUrl(url.toString());
        mNetwork.addToRequestQueue(mGetRouteRequest);
    }

    private void startGettingLocation() {
        ProviderStatus status = mCurrentLocation.getProviderStatus();
        switch (status) {
            case GPS_DISABLE:
            case NETWORK_DISABLE:
                showLoadingProgress(false);
                mDialogManager.showEnableProviderDialog(status);
                break;
            case READY:
                showLoadingProgress(true);
                mCurrentLocation.getLocation(this);
                break;
        }
    }

    @Override
    public void gotLocation(Location location) {
        mLocation = location;
        if (mLocationSubject != null) {
            mLocationSubject.onNext(location);
        }
    }

    private void submitLocation(Location location) {
        Bundle bundle = new Bundle();
        if (location != null) {
            bundle.putDouble(CurrentLocation.LATITUDE, location.getLatitude());
            bundle.putDouble(CurrentLocation.LONGITUDE, location.getLongitude());
            bundle.putBoolean(SmsDialog.LOADING_STATE, true);
        } else {
            bundle.putBoolean(SmsDialog.LOADING_STATE, false);
        }
        SmsDialog.newInstance(bundle).show(getFragmentManager(), null);
    }

    private void showLoadingProgress(boolean show) {
        if (show) {
            mBtnGroup.setVisibility(View.GONE);
            mLoadingGroup.setVisibility(View.VISIBLE);
        } else {
            mBtnGroup.setVisibility(View.VISIBLE);
            mLoadingGroup.setVisibility(View.GONE);
        }
    }

    private void loadCurrentLocationOnMap() {
        if (mLatLng != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 16);
            mGoogleMap.animateCamera(cameraUpdate);
            mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mLatLng).title("Your current location"));
        }
    }

    private GsonRequest mGetRouteRequest = new GsonRequest(Request.Method.POST, "", Route[].class,
            null, new Response.Listener<Route[]>() {
        @Override
        public void onResponse(Route[] routes) {
            for(int i=0; i<routes.length; i++) {
                ArrayList<LatLng> directionPoint = MapUtils.getJSONDirection(routes[i]);
                PolylineOptions rectLine = new PolylineOptions()
                        .width(8)
                        .color(Color.GRAY);

                for (int j = 0; j < directionPoint.size(); j++) {
                    rectLine.add(directionPoint.get(j));
                }
                mGoogleMap.addPolyline(rectLine);
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            //handle error
        }
    }, null, null);
}
