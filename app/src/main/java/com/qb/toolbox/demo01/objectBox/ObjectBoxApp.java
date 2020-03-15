package com.qb.toolbox.demo01.objectBox;

import android.app.Application;

public class ObjectBoxApp extends Application {

    public static final String TAG = ObjectBoxApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
