package com.xilu.sdk.demo.activity.ad.banner;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.demo.util.SPUtil;
import com.xilu.sdk.ad.IADXiluBannerAd;
import com.xilu.sdk.ad.data.ADXiluAdInfo;
import com.xilu.sdk.ad.entity.ADXiluAdSize;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluBannerAdListener;
import com.xilu.sdk.util.ADXiluToastUtil;


/**
 * @author ciba
 * @description Banner广告示例
 * @date 2020/3/26
 */
public class BannerAdActivity extends AppCompatActivity {

    private FrameLayout flContainer;
    private IADXiluBannerAd bannerAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        flContainer = findViewById(R.id.flContainer);
        loadBannerAd();
    }

    private void loadBannerAd() {
        boolean issensor = SPUtil.getBoolean(this, "sensor");
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                .adSize(new ADXiluAdSize(600, 300))
                .setAdShakeDisable(issensor)
                .build();

        bannerAd = com.xilu.sdk.ad.factory.AdFactoryManager.createBannerAd(this, flContainer);
        // 设置自刷新时间范围为30～120秒，⚠️注意！！！如果设置了自刷新，初始化ADXiluSDK时传入的content一定为Application的Content
        bannerAd.setAutoRefreshInterval(ADXiluDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        // 注：仅debug模式为true时生效。
        bannerAd.setOnlySupportPlatform(ADXiluDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
        // 如果横幅容器不是全屏可以设置额外参数
        bannerAd.setLocalExtraParams(extraParams);
        // 设置Banner广告监听
        bannerAd.setListener(new ADXiluBannerAdListener() {
            @Override
            public void onAdReceive(ADXiluAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onAdExpose(ADXiluAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADXiluAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADXiluAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClose...");
                if (flContainer != null) {
                    flContainer.removeAllViews();
                    // flContainer.setVisibility(View.GONE);
                }

                if (bannerAd != null) {
                    bannerAd.release();
                }
                if (adInfo != null) {
                    adInfo.release();
                }
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                if (error != null) {
                    String failedJson = error.toString();
                    Log.d(ADXiluDemoConstant.TAG, "onAdFailed----->" + failedJson);
                    ADXiluToastUtil.show(getApplicationContext(), "广告获取失败 : " + error.getError());
                }
                finish();
            }
        });
        // banner广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        bannerAd.setSceneId(ADXiluDemoConstant.BANNER_AD_SCENE_ID);
        // 加载Banner广告，参数为广告位ID，同一个ADXiluBannerAd只有一次loadAd有效
        bannerAd.loadAd(ADXiluDemoConstant.BANNER_AD_POS_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerAd != null) {
            bannerAd.release();
            bannerAd = null;
        }
    }
}
