package com.xilu.sdk.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.util.SPUtil;
import com.xilu.sdk.demo.util.UIUtils;
import com.xilu.sdk.ad.ADXiluSplashAd;
import com.xilu.sdk.ad.data.ADXiluAdInfo;
import com.xilu.sdk.ad.entity.ADXiluAdSize;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluSplashAdListener;
import com.xilu.sdk.listener.ADXiluInitListener;

/**
 * App启动页 - 开屏广告
 * 完全参考 SplashAdActivity 的实现
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ADXiluSplashAd splashAd;
    private TextView tvSkip;
    private FrameLayout flContainer;
    private RelativeLayout rlLogoContainer;

    /**
     * 广告位
     */
    private String posId;
    /**
     * 展示样式
     */
    private int splashType = ADXiluDemoConstant.HALF_SCREEN;
    /**
     * 加载类型
     */
    private int loadType = ADXiluDemoConstant.LOAD_AND_SHOW;
    /**
     * logo高度
     */
    private int logoHeightPx;
    /**
     * 是否沉浸状态栏
     */
    private boolean isImmersive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 先设置内容视图，Window背景保持启动图（避免白屏）
        setContentView(R.layout.activity_splash);

        initData();
        setFullScreen();
        initView();
        if(ADXiluSdk.getInstance().isInit()) {
            initAd();
        }else{
            ADXiluSdk.getInstance().setmInitListener(new ADXiluInitListener() {
                @Override
                public void onSuccess() {
                    initAd();
                }

                @Override
                public void onFailed(String s) {

                }
            });
        }
    }

    /**
     * 初始化页面数据
     */
    private void initData() {
        posId = ADXiluDemoConstant.SPLASH_AD_POS_ID;
        splashType = ADXiluDemoConstant.HALF_SCREEN;
        loadType = ADXiluDemoConstant.LOAD_AND_SHOW;
        logoHeightPx = dpToPx(150); // logo区域高度150dp
    }

    private void setFullScreen() {
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        flContainer = findViewById(R.id.flSplashContainer);
        tvSkip = findViewById(R.id.tvSkip);
        rlLogoContainer = findViewById(R.id.rlLogoContainer);

        if (splashType == ADXiluDemoConstant.IMMERSIVE_AND_FULLSCREEN) {
            isImmersive = true;
            rlLogoContainer.setVisibility(View.GONE);
        } else if (splashType == ADXiluDemoConstant.FULL_SCREEN) {
            isImmersive = false;
            rlLogoContainer.setVisibility(View.GONE);
        } else if (splashType == ADXiluDemoConstant.HALF_SCREEN) {
            isImmersive = false;
            ViewGroup.LayoutParams layoutParams = rlLogoContainer.getLayoutParams();
            layoutParams.height = logoHeightPx;
            rlLogoContainer.setLayoutParams(layoutParams);
            rlLogoContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化广告
     */
    private void initAd() {
        // 创建开屏广告实例
        splashAd = new ADXiluSplashAd(this);

        int widthPixels = UIUtils.getScreenWidthInPx(this);
        int heightPixels = UIUtils.getScreenHeightInPx(this);

        boolean issensor = SPUtil.getBoolean(this, "sensor");

        // 创建额外参数实例
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                .adSize(new ADXiluAdSize(widthPixels, heightPixels - logoHeightPx))
                .setAdShakeDisable(issensor)
                .build();

        splashAd.setLocalExtraParams(extraParams);
        splashAd.setImmersive(isImmersive);

        if (ADXiluDemoConstant.SPLASH_AD_CUSTOM_SKIP_VIEW) {
            // 设置自定义跳过按钮
            splashAd.setSkipView(tvSkip, 5000);
        }

        // 设置仅支持的广告平台
        splashAd.setOnlySupportPlatform(ADXiluDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);

        // 设置开屏广告监听
        splashAd.setListener(new ADXiluSplashAdListener() {

            @Override
            public void onADTick(long millisUntilFinished) {
                Log.d(TAG, "广告剩余倒计时时长回调：" + millisUntilFinished);
                tvSkip.setText(millisUntilFinished + "s自动跳转");
            }

            @Override
            public void onReward(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告奖励回调... ");
            }

            @Override
            public void onAdSkip(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告跳过回调... ");
            }

            @Override
            public void onAdReceive(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告获取成功回调... ");
                // 广告获取成功后，清除Window背景，让广告内容显示出来
                getWindow().setBackgroundDrawable(null);
                if (loadType == ADXiluDemoConstant.LOAD_AND_SHOW) {
                    showAd();
                }
            }

            @Override
            public void onAdExpose(ADXiluAdInfo adInfo) {
                if (ADXiluDemoConstant.SPLASH_AD_CUSTOM_SKIP_VIEW) {
                    tvSkip.setAlpha(1f);
                }
                Log.d(TAG, "广告展示回调");
            }

            @Override
            public void onAdClick(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告点击回调");
            }

            @Override
            public void onAdClose(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告关闭回调，跳转主界面");
                jumpMain();
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                if (error != null) {
                    Log.d(TAG, "onAdFailed----->" + error.toString());
                }
                // 广告失败也清除Window背景
                getWindow().setBackgroundDrawable(null);
                jumpMain();
            }
        });

        if (loadType == ADXiluDemoConstant.LOAD_AND_SHOW) {
            loadAd();
        }
    }

    private void loadAd() {
        if (splashAd != null) {
            // 仅加载开屏广告
            splashAd.loadOnly(ADXiluDemoConstant.SPLASH_AD_POS_ID);
        }
    }

    /**
     * 展示广告
     */
    private void showAd() {
        if (splashAd != null && flContainer != null) {
            splashAd.showSplash(flContainer);
        }
    }

    /**
     * 跳转到主界面
     */
    private void jumpMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * dp转px
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    @Override
    public void onBackPressed() {
        // 取消返回事件，增加开屏曝光率
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放广告资源
        if (splashAd != null) {
            splashAd.release();
            splashAd = null;
        }
    }
}
