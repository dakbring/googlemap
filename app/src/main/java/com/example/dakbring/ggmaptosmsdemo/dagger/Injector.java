package com.example.dakbring.ggmaptosmsdemo.dagger;

import dagger.ObjectGraph;

public final class Injector {

  public static final String INJECTION_ERROR = "You forget add this class to injection list!";

  private static ObjectGraph objectGraph = null;

  public static void init(final Object rootModule) {

    if (objectGraph == null) {
      objectGraph = ObjectGraph.create(rootModule);
    } else {
      objectGraph = objectGraph.plus(rootModule);
    }

    // Inject statics
    objectGraph.injectStatics();

  }

  public static void init(final Object rootModule, final Object target) {
    init(rootModule);
    inject(target);
  }

  public static void inject(final Object target) {
    objectGraph.inject(target);
  }

  public static <T> T resolve(Class<T> type) {
    return objectGraph.get(type);
  }
}