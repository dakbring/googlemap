package com.example.dakbring.ggmaptosmsdemo;

import com.example.dakbring.ggmaptosmsdemo.dagger.Injector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends FragmentActivity {

  private CompositeSubscription mCompositeSubscription;

  protected boolean isHandleDeepLinking(){
    return false;
  }

  protected boolean isHandleUriData(){
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.inject(this);
    handleDeepLinkingData(getIntent());
  }

  private void handleDeepLinkingData(Intent intent) {
    if(intent != null) {
      Uri data = intent.getData();
      if(data != null) {
        if (isHandleDeepLinking()) {
          onHandleDeepLinkingData(data);
        }
        if (isHandleUriData()) {
          onHandleUriData(data);
        }
      }
    }
  }

  protected void onHandleDeepLinkingData(Uri uri) {}

  protected void onHandleUriData(Uri uri) {}

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleDeepLinkingData(intent);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mCompositeSubscription != null) {
      mCompositeSubscription.unsubscribe();
      mCompositeSubscription.clear();
      mCompositeSubscription = null;
    }
  }

  public synchronized Subscription handleSubscription(Subscription subscription) {
    if (!isFinishing()) {
      if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
        mCompositeSubscription = new CompositeSubscription();
      }
      mCompositeSubscription.add(subscription);
      return subscription;
    }
    return null;
  }
}
