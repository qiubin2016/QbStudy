package com.qb.toolbox.demo01.objectBox;

import android.content.Context;
import android.util.Log;

import com.qb.toolbox.BuildConfig;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();

        if (BuildConfig.DEBUG) {
            Log.d(ObjectBoxApp.TAG, String.format("Using ObjectBox %s (%s)",
                    BoxStore.getVersion(), BoxStore.getVersionNative()));
            new AndroidObjectBrowser(boxStore).start(context.getApplicationContext());
        }
    }

    public static BoxStore get() {
        return boxStore;
    }
}
