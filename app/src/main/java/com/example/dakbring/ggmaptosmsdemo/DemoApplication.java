package com.example.dakbring.ggmaptosmsdemo;

import com.example.dakbring.ggmaptosmsdemo.dagger.DemoModule;
import com.example.dakbring.ggmaptosmsdemo.dagger.Injector;

import android.app.Application;

public class DemoApplication extends Application{
  private static DemoApplication sInstance;

  public static DemoApplication getInstance(){
    return sInstance;
  }

  public DemoApplication(){
    sInstance = this;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    initObjectGraph();
  }

  private void initObjectGraph() {
    Injector.init(getRootModule(), this);
  }

  protected Object getRootModule(){
    return new DemoModule();
  }
}
