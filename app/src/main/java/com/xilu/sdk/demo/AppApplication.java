package com.xilu.sdk.demo;

import android.app.Application;
import android.util.Log;

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
    private static final String TAG = "AppApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        // 在Application中初始化SDK（App启动时自动初始化）
        Log.d(TAG, "开始初始化SDK...");
        ADXiluSdk.getInstance().init(this, new ADXiluInitConfig.Builder()
                        .appId(ADXiluDemoConstant.APP_ID)
                        .debug(true)
                        // TODO 注意上线后请置为false
                        .agreePrivacyStrategy(false)
                        .isCanUseLocation(false)
                        .isCanUsePhoneState(false)
                        .isCanReadInstallList(false)
                        .isCanUseReadWriteExternal(false)
                        .isCanUseWifiState(false)
                        .isCanUseOaid(false)
                        .filterThirdQuestion(false)
                        .setMultiprocess(false)
                        .isCanUseSensor(false)
                        .setCustomDeviceInfoController(new CustomDeviceInfoController() {})
                        .build(),
                new ADXiluInitListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "SDK初始化成功");
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.e(TAG, "SDK初始化失败: " + error);
                    }
                });
    }
}
