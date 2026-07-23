package com.xilu.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    /**
     * 开屏页最大等待时长，单位毫秒
     */
    private static final long SPLASH_MAX_WAIT_TIME = 5000;

    private ADXiluSplashAd splashAd;
    private TextView tvSkip;
    private FrameLayout flContainer;
    private RelativeLayout rlLogoContainer;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeoutRunnable;
    /**
     * 是否已经跳转，防止重复跳转
     */
    private volatile boolean mHasJumped;
    /**
     * 广告收到、曝光、关闭时间戳，用于排查过早跳转
     */
    private long mAdReceiveTime;
    private long mAdExposeTime;
    private long mAdCloseTime;

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
                    Log.w(TAG, "SDK初始化失败，跳转主界面");
                    jumpMain();
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

        // 启动开屏超时兜底：无论广告成功/失败/异常，最多等待 SPLASH_MAX_WAIT_TIME 后强制跳转
        startSplashTimeout();

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
                mAdReceiveTime = System.currentTimeMillis();
                String msg = "广告获取成功 receiveTime=" + mAdReceiveTime;
                Log.d(TAG, msg);
                // 广告获取成功后，清除Window背景，让广告内容显示出来
                getWindow().setBackgroundDrawable(null);
                if (loadType == ADXiluDemoConstant.LOAD_AND_SHOW) {
                    showAd();
                }
            }

            @Override
            public void onAdExpose(ADXiluAdInfo adInfo) {
                mAdExposeTime = System.currentTimeMillis();
                // 广告已曝光，取消5秒全局兜底，交给SDK自己的倒计时/关闭回调决定何时跳转
                removeSplashTimeout();
                if (ADXiluDemoConstant.SPLASH_AD_CUSTOM_SKIP_VIEW) {
                    tvSkip.setAlpha(1f);
                }
                String msg = "广告展示 expose2receive=" + (mAdExposeTime - mAdReceiveTime) + "ms";
                Log.d(TAG, msg);
            }

            @Override
            public void onAdClick(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告点击回调");
            }

            @Override
            public void onAdClose(ADXiluAdInfo adInfo) {
                mAdCloseTime = System.currentTimeMillis();
                long duration = mAdExposeTime > 0 ? mAdCloseTime - mAdExposeTime : mAdCloseTime - mAdReceiveTime;
                String msg = "广告关闭 expose2close=" + duration + "ms";
                Log.d(TAG, msg);
                jumpMain();
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                if (error != null) {
                    Log.d(TAG, "onAdFailed----->" + error.toString());
                }
                // 广告失败也清除Window背景
                getWindow().setBackgroundDrawable(null);
                String msg = "广告加载失败，跳转";
                Log.d(TAG, msg);
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
        if (mHasJumped || isFinishing() || isDestroyed()) {
            return;
        }
        mHasJumped = true;
        removeSplashTimeout();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 启动开屏超时兜底
     */
    private void startSplashTimeout() {
        removeSplashTimeout();
        mTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                String msg = "开屏5秒未曝光，超时跳转";
                Log.w(TAG, msg);
                jumpMain();
            }
        };
        mHandler.postDelayed(mTimeoutRunnable, SPLASH_MAX_WAIT_TIME);
    }

    /**
     * 移除开屏超时兜底
     */
    private void removeSplashTimeout() {
        if (mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mTimeoutRunnable = null;
        }
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
        // 移除超时任务，避免内存泄漏和Activity销毁后仍跳转
        removeSplashTimeout();
        // 释放广告资源
        if (splashAd != null) {
            splashAd.release();
            splashAd = null;
        }
    }
}
