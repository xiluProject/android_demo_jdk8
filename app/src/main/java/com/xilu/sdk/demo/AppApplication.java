package com.xilu.sdk.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.config.ADXiluInitConfig;
import com.xilu.sdk.config.CustomDeviceInfoController;
import com.xilu.sdk.core.plugin.CoreApplicationHelper;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.listener.ADXiluInitListener;


public class AppApplication extends Application {
    public static Application application;

    @Override
    protected void attachBaseContext(Context base) {
        CoreApplicationHelper.attachBaseContext(base, this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initAd();
    }

    public void initAd() {
        try {
            ADXiluSdk.getInstance().init(this,
                    new ADXiluInitConfig.Builder()
                            .appId(ADXiluDemoConstant.APP_ID)
                            .slotId(ADXiluDemoConstant.SLOT_ID)
                            .debug(true)
                            .agreePrivacyStrategy(true)
                            .isCanUseLocation(true)
                            .isCanUsePhoneState(true)
                            .isCanReadInstallList(true)
                            .isCanUseReadWriteExternal(true)
                            .isCanUseWifiState(true)
                            .isCanUseOaid(true)
                            .filterThirdQuestion(true)
                            .setMultiprocess(false)
                            .isCanUseSensor(true)
                            .setWXAppId(ADXiluDemoConstant.WX_APP_ID)
                            .setCustomDeviceInfoController(new CustomDeviceInfoController() {
                            })
                            .build(),
                    new ADXiluInitListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("AppApplication", "SDK初始化成功");
                        }

                        @Override
                        public void onFailed(String error) {
                            Log.e("AppApplication", "SDK初始化失败: " + error);
                        }

                        @Override
                        public void onPluginLoadComplete() {
                            Log.d("AppApplication", "插件加载完成，所有热更新插件已成功加载");
                        }
                    });
        } catch (Exception e) {
            Log.e("AppApplication", "初始化SDK失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ADXiluSdk.getInstance().shutdown();
    }
}
