package com.xilu.sdk.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
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
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final long SPLASH_MAX_WAIT_TIME = 5000;

    private ADXiluSplashAd splashAd;
    private TextView tvSkip;
    private FrameLayout flContainer;
    private RelativeLayout rlLogoContainer;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeoutRunnable;
    private volatile boolean mHasJumped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 必须在setContentView之前设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);

        flContainer = findViewById(R.id.flSplashContainer);
        tvSkip = findViewById(R.id.tvSkip);
        rlLogoContainer = findViewById(R.id.rlLogoContainer);

        // 监听广告容器子view变化，SDK添加广告view时可能重置系统UI，重新隐藏状态栏
        flContainer.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                hideStatusBar();
            }
            @Override
            public void onChildViewRemoved(View parent, View child) {}
        });

        if (ADXiluSdk.getInstance().isInit()) {
            initAd();
        } else {
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

    private void initAd() {
        splashAd = new ADXiluSplashAd(this);
        startSplashTimeout();

        int logoHeightPx = (int) (150 * getResources().getDisplayMetrics().density + 0.5f);
        int widthPixels = UIUtils.getScreenWidthInPx(this);
        int heightPixels = UIUtils.getScreenHeightInPx(this);

        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                .adSize(new ADXiluAdSize(widthPixels, heightPixels - logoHeightPx))
                .setAdShakeDisable(SPUtil.getBoolean(this, "sensor"))
                .build();

        splashAd.setLocalExtraParams(extraParams);
        splashAd.setSkipView(tvSkip, 5000);
        splashAd.setOnlySupportPlatform(ADXiluDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);

        splashAd.setListener(new ADXiluSplashAdListener() {
            @Override
            public void onADTick(long millisUntilFinished) {
                tvSkip.setText(millisUntilFinished + "s自动跳转");
            }

            @Override
            public void onReward(ADXiluAdInfo adInfo) {}

            @Override
            public void onAdSkip(ADXiluAdInfo adInfo) {}

            @Override
            public void onAdReceive(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告获取成功");
                getWindow().setBackgroundDrawable(null);
                splashAd.showSplash(flContainer);
            }

            @Override
            public void onAdExpose(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告展示");
                removeSplashTimeout();
                tvSkip.setAlpha(1f);
                // SDK展示广告时可能重置系统UI，重新隐藏状态栏
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            @Override
            public void onAdClick(ADXiluAdInfo adInfo) {}

            @Override
            public void onAdClose(ADXiluAdInfo adInfo) {
                Log.d(TAG, "广告关闭");
                jumpMain();
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                Log.d(TAG, "广告加载失败: " + (error != null ? error.toString() : "unknown"));
                getWindow().setBackgroundDrawable(null);
                jumpMain();
            }
        });

        splashAd.loadOnly(ADXiluDemoConstant.SPLASH_AD_POS_ID);
    }

    private void hideStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideStatusBar();
        }
    }

    private void jumpMain() {
        if (mHasJumped || isFinishing() || isDestroyed()) {
            return;
        }
        mHasJumped = true;
        removeSplashTimeout();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void startSplashTimeout() {
        removeSplashTimeout();
        mTimeoutRunnable = () -> {
            Log.w(TAG, "开屏5秒未曝光，超时跳转");
            jumpMain();
        };
        mHandler.postDelayed(mTimeoutRunnable, SPLASH_MAX_WAIT_TIME);
    }

    private void removeSplashTimeout() {
        if (mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mTimeoutRunnable = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeSplashTimeout();
        if (splashAd != null) {
            splashAd.release();
            splashAd = null;
        }
    }
}
