package com.example.dakbring.ggmaptosmsdemo.deeplinking;

import com.example.dakbring.ggmaptosmsdemo.BaseActivity;
import com.example.dakbring.ggmaptosmsdemo.R;
import com.example.dakbring.ggmaptosmsdemo.map.GoogleMapActivity;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class DeepLinkActivity extends BaseActivity {

  private static final Map<Integer, Class<? extends BaseActivity>> sSupportedPaths;

  static {
    sSupportedPaths = new HashMap<>();
    sSupportedPaths.put(R.string.deep_linking_map, GoogleMapActivity.class);
  }

  @Override
  protected boolean isHandleDeepLinking() {
    return true;
  }

  @Override
  protected void onHandleDeepLinkingData(Uri uri) {
    super.onHandleDeepLinkingData(uri);
    for (Map.Entry<Integer, Class<? extends BaseActivity>> entry : sSupportedPaths.entrySet()) {
      String path = getString(entry.getKey());
      if (uri.getPath().matches(path)) {
        Intent intent = new Intent(this, entry.getValue());
        intent.setData(uri);
        startActivity(intent);
        return;
      }
    }
  }
}