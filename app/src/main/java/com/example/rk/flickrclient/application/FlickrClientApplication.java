package com.example.rk.flickrclient.application;

import android.app.Application;
import android.util.Log;

/**
 * Created by RK on 10/20/2016.
 */
public class FlickrClientApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = FlickrClientApplication.class.getSimpleName();
    private static FlickrClientApplication instance;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        instance = this;
    }


    public static FlickrClientApplication getInstance() {
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, " fatal crash occured");
        if (defaultUncaughtExceptionHandler != null) {
            defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }

    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
