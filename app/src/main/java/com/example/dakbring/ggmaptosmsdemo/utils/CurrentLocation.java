package com.example.dakbring.ggmaptosmsdemo.utils;

import com.example.dakbring.ggmaptosmsdemo.dagger.Injector;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class CurrentLocation {

  public static final String LATITUDE = "latitude";

  public static final String LONGITUDE = "longitude";

  private static final long TIME_OUT = 20000;

  private final Handler mHandler;

  @Inject
  LocationManager mLocationManager;

  @Inject
  Context mContext;

  private OnReceivedLocationListener mOnReceivedLocationListener;

  private GetLastLocation mRunnable;

  public CurrentLocation() {
    Injector.inject(this);
    mHandler = new Handler();
    mRunnable = new GetLastLocation(this);
  }

  public ProviderStatus getProviderStatus() {

    boolean isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnable = (Connectivity.isConnectedWifi(mContext) || Connectivity.isConnectedMobile(mContext));

    if (!isGPSEnable) {
      return ProviderStatus.GPS_DISABLE;
    }
    if(!isNetworkEnable){
      return ProviderStatus.NETWORK_DISABLE;
    }
    return ProviderStatus.READY;
  }

  public void getLocation(OnReceivedLocationListener listener) {

    mOnReceivedLocationListener = listener;

    startGettingLocation(LocationManager.GPS_PROVIDER);
    startGettingLocation(LocationManager.NETWORK_PROVIDER);
  }

  private void startGettingLocation(String provider) {
    //exceptions will be thrown if provider is not permitted.
    boolean isEnable = false;
    try {
      isEnable = mLocationManager.isProviderEnabled(provider);
    } catch (Exception ex) {
    }

    //don't start listeners if no provider is enabled
    if (isEnable) {
      mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
      mHandler.postDelayed(mRunnable, TIME_OUT);
    }
  }

  private void onReceivedLocation(Location location) {
    mHandler.removeCallbacks(mRunnable);
    if (mOnReceivedLocationListener != null) {
      mOnReceivedLocationListener.gotLocation(location);
    }
    mLocationManager.removeUpdates(mLocationListener);
  }

  private LocationListenerAdapter mLocationListener = new LocationListenerAdapter() {
    @Override
    public void onLocationChanged(Location location) {
      onReceivedLocation(location);
    }
  };

  private void onTimeOut() {
    mLocationManager.removeUpdates(mLocationListener);
    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location == null) {
      location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    mOnReceivedLocationListener.gotLocation(location);
  }

  private class GetLastLocation implements Runnable {

    private WeakReference<CurrentLocation> mWeakReference;

    public GetLastLocation(CurrentLocation currentLocation) {
      mWeakReference = new WeakReference<>(currentLocation);
    }

    @Override
    public void run() {
      CurrentLocation currentLocation = mWeakReference.get();
      if (currentLocation != null) {
        currentLocation.onTimeOut();
      }
    }
  }

  public interface OnReceivedLocationListener {

    void gotLocation(Location location);
  }

  private class LocationListenerAdapter implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
  }

  public enum ProviderStatus {
    NETWORK_DISABLE,
    GPS_DISABLE,
    READY
  }
}
