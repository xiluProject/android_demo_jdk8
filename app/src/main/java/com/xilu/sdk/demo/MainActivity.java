package com.xilu.sdk.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xilu.sdk.ADXiluSdk;
import com.xilu.sdk.ad.utils.ADXiluLogUtil;
import com.xilu.sdk.demo.activity.ad.banner.BannerAdActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeExpressActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeExpressListActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeSelfRenderActivity;
import com.xilu.sdk.demo.activity.ad.feed.NativeSelfRenderListActivity;
import com.xilu.sdk.demo.activity.ad.fullscreen.FullScreenVodAdActivity;
import com.xilu.sdk.demo.activity.ad.interstitial.InterstitialAdActivity;
import com.xilu.sdk.demo.activity.ad.reward.RewardVodAdActivity;
import com.xilu.sdk.demo.activity.ad.splash.SplashAdSettingActivity;
import com.xilu.sdk.demo.util.SPUtil;


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

        if (!ADXiluSdk.getInstance().isInit()) {
            ADXiluLogUtil.d("MainActivity", "SDK未初始化，手动触发初始化");
            initBtn.performClick();
        } else {
            initBtn.setText("SDK初始化已完成");
        }
        if(ADXiluSdk.getInstance().isPluginLoaded()){
            ADXiluLogUtil.d("MainActivity", "所有插件已经加载完成");
        }

        findViewById(R.id.btnSplashAd).setOnClickListener(this);
        findViewById(R.id.btnBannerAd).setOnClickListener(this);
        findViewById(R.id.btnNativeExpressLayout).setOnClickListener(this);
        findViewById(R.id.btnNativeSelfRenderLayout).setOnClickListener(this);
        findViewById(R.id.btnNativeExpressList).setOnClickListener(this);
        findViewById(R.id.btnNativeSelfRenderList).setOnClickListener(this);
        findViewById(R.id.btnInterstitialAd).setOnClickListener(this);
        findViewById(R.id.btnRewardVodAd).setOnClickListener(this);
        findViewById(R.id.btnFullScreenAd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.init_btn){
            initAd();
            return;
        }
        if (!ADXiluSdk.getInstance().isInit()){
            Toast.makeText(this, "初始化未完成，请稍等", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    public void initAd() {
        if (ADXiluSdk.getInstance().isInit()) {
            initBtn.setText("SDK初始化已完成");
            initBtn.setEnabled(true);
        } else {
            initBtn.setText("点击初始化sdk");
            initBtn.setEnabled(false);
        }
    }
}

