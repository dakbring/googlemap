package com.example.dakbring.ggmaptosmsdemo.map;

import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.dakbring.ggmaptosmsdemo.BaseActivity;
import com.example.dakbring.ggmaptosmsdemo.R;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;


public class GoogleMapActivity extends BaseActivity {

  private GoogleMap mGoogleMap;
  private LatLng mLatLng;

  @Override
  protected boolean isHandleUriData() {
    return true;
  }

  @Override
  protected void onHandleUriData(Uri uri) {
    super.onHandleUriData(uri);
    try {
      mLatLng = new LatLng(
          Double.valueOf(uri.getQueryParameter(CurrentLocation.LATITUDE)),
          Double.valueOf(uri.getQueryParameter(CurrentLocation.LONGITUDE))
      );
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), "Cannot load location", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_maps);

    mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

    mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
      @Override
      public void onMapLoaded() {
        if (mLatLng != null) {
          CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 16);
          mGoogleMap.animateCamera(cameraUpdate);
          mGoogleMap.addMarker(new MarkerOptions().position(mLatLng).title("Your current location"));
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

  }
}