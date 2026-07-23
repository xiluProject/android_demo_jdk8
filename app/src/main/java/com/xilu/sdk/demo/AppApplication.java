package com.xilu.sdk.demo;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.config.ADXiluImageLoader;
import com.xilu.sdk.config.ADXiluInitConfig;
import com.xilu.sdk.config.CustomDeviceInfoController;
import com.xilu.sdk.core.BuildConfig;
import com.xilu.sdk.listener.ADImageLoaderCallback;
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
        try {
            initAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initAd() {
        // 初始化ADXilu广告SDK
        ADXiluSdk.getInstance().init(this, new ADXiluInitConfig.Builder()
                // 设置APPID
                .appId(ADXiluDemoConstant.APP_ID)
                //.slotId(ADXiluDemoConstant.SLOT_ID)
                // 是否开启Debug，开启会有详细的日志信息打印，如果用上ADXiluToastUtil工具还会弹出toast提示。
                // TODO 注意上线后请置为false
                .debug(true)
                //【慎改】是否同意隐私政策，将禁用一切设备信息读起严重影响收益
                .agreePrivacyStrategy(true)
                // 是否可获取定位数据
                .isCanUseLocation(true)
                // 是否可获取设备信息
                .isCanUsePhoneState(true)
                // 是否可读取设备安装列表
                .isCanReadInstallList(true)
                // 是否可读取设备外部读写权限
                .isCanUseReadWriteExternal(true)
                // 是否可读取WIFI信息
                .isCanUseWifiState(true)
                // 是否可使用OAID
                .isCanUseOaid(true)
                // 是否过滤第三方平台的问题广告（例如: 已知某个广告平台在某些机型的Banner广告可能存在问题，如果开启过滤，则在该机型将不再去获取该平台的Banner广告）
                .filterThirdQuestion(true)
                // 是否允许多进程
                .setMultiprocess(false)
                // 是否允许使用传感器
                .isCanUseSensor(true)
                // 设置微信AppId
                //.setWXAppId(ADXiluDemoConstant.WX_APP_ID)
                .setCustomDeviceInfoController(new CustomDeviceInfoController() {})
                // 设置图片加载器（使用Glide）
                .setImageLoader(new ADXiluImageLoader() {
                    @Override
                    public void loadImage(Context context, String url, ImageView imageView) {
                        Glide.with(context)
                                .load(url)
                                .into(imageView);
                    }

                    @Override
                    public void loadImage(Context context, String url, ImageView imageView, ADImageLoaderCallback callback) {
                        Glide.with(context)
                                .load(url)
                                .into(imageView);
                        // Glide没有直接的回调，假设加载成功
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                })
                .build(), new ADXiluInitListener() {
            @Override
            public void onSuccess() {
                // 初始化成功
                android.util.Log.d("AppApplication", "SDK初始化成功");
            }

            @Override
            public void onFailed(String error) {
                // 初始化失败
                android.util.Log.e("AppApplication", "SDK初始化失败: " + error);
            }
        });
    }


}
