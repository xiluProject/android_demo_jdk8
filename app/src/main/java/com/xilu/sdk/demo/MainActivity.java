package com.xilu.sdk.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xilu.sdk.config.ADXiluInitConfig;
import com.xilu.sdk.config.CustomDeviceInfoController;
import com.xilu.sdk.demo.activity.ad.banner.BannerAdActivity;
import com.xilu.sdk.demo.activity.ad.draw.DrawVodActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeExpressActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeExpressListActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeSelfRenderActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeSelfRenderListActivity;
import com.xilu.sdk.demo.activity.ad.fullscreen.FullScreenVodAdActivity;
import com.xilu.sdk.demo.activity.ad.interstitial.InterstitialAdActivity;
import com.xilu.sdk.demo.activity.ad.reward.RewardVodAdActivity;
import com.xilu.sdk.demo.activity.ad.splash.SplashAdSettingActivity;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.util.SPUtil;
import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.listener.ADXiluInitListener;

/**
 * Created by zhangqinglou on 2025/4/12.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button initBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        initBtn = findViewById(R.id.init_btn);
        initBtn.setOnClickListener(this);

        findViewById(R.id.btnSplashAd).setOnClickListener(this);
        findViewById(R.id.btnBannerAd).setOnClickListener(this);
        findViewById(R.id.btnNativeExpressLayout).setOnClickListener(this);
        findViewById(R.id.btnNativeSelfRenderLayout).setOnClickListener(this);
        findViewById(R.id.btnNativeExpressList).setOnClickListener(this);
        findViewById(R.id.btnNativeSelfRenderList).setOnClickListener(this);
        findViewById(R.id.btnRewardVodAd).setOnClickListener(this);
        findViewById(R.id.btnFullScreenAd).setOnClickListener(this);
        findViewById(R.id.btnInterstitialAd).setOnClickListener(this);
        //initAd();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.init_btn){
            initAd();
            return;
        }
        int id = v.getId();
        if (id == R.id.btnSplashAd) {
            startActivity(SplashAdSettingActivity.class);
        } else if (id == R.id.btnBannerAd) {
            startActivity(BannerAdActivity.class);
        } else if (id == R.id.btnNativeExpressLayout) {
            startActivity(NativeExpressActivity.class);
        } else if (id == R.id.btnNativeSelfRenderLayout) {
            startActivity(NativeSelfRenderActivity.class);
        } else if (id == R.id.btnNativeExpressList) {
            startActivity(NativeExpressListActivity.class);
        } else if (id == R.id.btnNativeSelfRenderList) {
            startActivity(NativeSelfRenderListActivity.class);
        } else if (id == R.id.btnRewardVodAd) {
            startActivity(RewardVodAdActivity.class);
        } else if (id == R.id.btnInterstitialAd) {
            startActivity(InterstitialAdActivity.class);
        } else if (id == R.id.btnFullScreenAd) {
            startActivity(FullScreenVodAdActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.toolbar_setting:
//                startActivity(SettingActivity.class);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
        return super.onOptionsItemSelected(item);
    }


    private void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    /**
     * 初始化广告SDK并且跳转开屏界面
     */
    public void initAd() {
        initBtn.setText("正在初始化...");
        initBtn.setEnabled(false);
        // 初始化ADXilu广告SDK
        ADXiluSdk.getInstance().init(AppApplication.application, new ADXiluInitConfig.Builder()
                        // 设置APPID
                        .appId(ADXiluDemoConstant.APP_ID)
                        // 是否开启Debug，开启会有详细的日志信息打印，如果用上ADXiluToastUtil工具还会弹出toast提示。
                        // TODO 注意上线后请置为false
                        .debug(true)
                        //【慎改】是否同意隐私政策，将禁用一切设备信息读起严重影响收益
                        .agreePrivacyStrategy(false)
                        // 是否可获取定位数据
                        .isCanUseLocation(false)
                        // 是否可获取设备信息
                        .isCanUsePhoneState(false)
                        // 是否可读取设备安装列表
                        .isCanReadInstallList(false)
                        // 是否可读取设备外部读写权限
                        .isCanUseReadWriteExternal(false)
                        // 是否可读取WIFI信息
                        .isCanUseWifiState(false)
                        // 是否可使用OAID
                        .isCanUseOaid(false)
                        // 是否过滤第三方平台的问题广告（例如: 已知某个广告平台在某些机型的Banner广告可能存在问题，如果开启过滤，则在该机型将不再去获取该平台的Banner广告）
                        .filterThirdQuestion(true)
                        // 是否允许多进程
                        .setMultiprocess(true)
                        // 是否允许使用传感器
                        .isCanUseSensor(false)
                        .setCustomDeviceInfoController(new CustomDeviceInfoController() {
                        })
                        .build(),
                new ADXiluInitListener() {
                    @Override
                    public void onSuccess() {
                        // 初始化成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBtn.setText("SDK初始化完成");
                                initBtn.setEnabled(true);
                                Toast.makeText(MainActivity.this, "SDK初始化成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        // 初始化失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBtn.setText("SDK初始化失败,点击重试");
                                initBtn.setEnabled(true);
                                Toast.makeText(MainActivity.this, "SDK初始化失败: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}

