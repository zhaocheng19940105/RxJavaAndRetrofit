package com.alaske.demo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.alaske.demo.api.ApiManager;
import com.alaske.demo.config.EnvConfig;

/**
 * Author: zhaocheng
 * Date: 2016-04-12
 * Time: 17:45
 * Name:App
 * Introduction:
 */
public class App extends Application {

    private static final String TAG_LOG="App_DEBUG";

    private static App app;

    private ApiManager apiManager = null;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        apiManager = new ApiManager();
        initEnv();
    }

    private void initEnv() {
        String env = "";
        try {
            getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString("APP_ENV");
        } catch (PackageManager.NameNotFoundException e) {

        }
        if (!TextUtils.isEmpty(env)&&TextUtils.equals("TEST",env)){
            Log.d(TAG_LOG,"env test");
            EnvConfig.envMode= EnvConfig.EnvMode.TEST;
        }
    }

    public static <T> T apiService(Class<T> clz) {
        return getApp().apiManager.getService(clz);
    }
}
