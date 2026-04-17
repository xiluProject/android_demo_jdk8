package com.xilu.sdk.demo;

import android.app.Application;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.config.ADXiluInitConfig;
import com.xilu.sdk.config.CustomDeviceInfoController;
import com.xilu.sdk.core.BuildConfig;
import com.xilu.sdk.listener.ADXiluInitListener;

/**
 * Created by zhangqinglou on 2025/4/12.
 */
public class AppApplication extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

    }


}
