package com.yxc.websocket.base;

import android.app.Application;
import android.content.Context;


public class BaseApplication extends Application {
    private static BaseApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static Context getAppContext() {
        return mApp;
    }
}