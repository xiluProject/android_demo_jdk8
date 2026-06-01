package com.xilu.sdk.demo.activity.ad.interstitial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.ad.IADXiluInterstitialAd;
import com.xilu.sdk.ad.data.ADXiluInterstitialAdInfo;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluInterstitialAdListener;
import com.xilu.sdk.util.ADXiluToastUtil;

/**
 * @author ciba
 * @description 插屏广告示例
 * @date 2020/3/27
 */
public class InterstitialAdActivity extends AppCompatActivity implements View.OnClickListener {
    private IADXiluInterstitialAd interstitialAd;
    private ADXiluInterstitialAdInfo interstitialAdInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_vod);

        initListener();
        initAd();
    }

    private void initListener() {
        Button btnLoadAd = findViewById(R.id.btnLoadAd);
        Button btnShowAd = findViewById(R.id.btnShowAd);

        btnLoadAd.setText("获取插屏广告");
        btnShowAd.setText("展示插屏广告");

        btnLoadAd.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);

    }

    private void initAd() {
        interstitialAd = com.xilu.sdk.ad.factory.AdFactoryManager.createInterstitialAd(this);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        // 注：仅debug模式为true时生效。
        interstitialAd.setOnlySupportPlatform(ADXiluDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
        // 创建额外参数实例
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                // 设置视频类广告是否静音
                .setVideoWithMute(ADXiluDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE)
                .build();
        interstitialAd.setLocalExtraParams(extraParams);
        // 设置插屏广告监听
        interstitialAd.setListener(new ADXiluInterstitialAdListener() {

            @Override
            public void onAdReceive(ADXiluInterstitialAdInfo interstitialAdInfo) {
                // TODO 插屏广告对象一次成功拉取的广告数据只允许展示一次
                InterstitialAdActivity.this.interstitialAdInfo = interstitialAdInfo;
                ADXiluToastUtil.show(getApplicationContext(), "插屏广告获取成功");
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onAdReady(ADXiluInterstitialAdInfo interstitialAdInfo) {
                // 部分渠道不会回调该方法，请在onAdReceive做广告展示处理
                Log.d(ADXiluDemoConstant.TAG, "onAdReady...");
            }

            @Override
            public void onAdExpose(ADXiluInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADXiluInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADXiluInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                // ADXiluToastUtil.show(getApplicationContext(), "广告获取失败");
                if (error != null) {
                    String failedJson = error.toString();
                    Log.d(ADXiluDemoConstant.TAG, "onAdFailed----->" + failedJson);
                    ADXiluToastUtil.show(getApplicationContext(), "广告获取失败 : " + error.getError());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLoadAd) {
            loadAd();
        } else if (id == R.id.btnShowAd) {
            if (interstitialAdInfo == null) {
                Toast.makeText(this, "无可用广告", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!interstitialAdInfo.isReady()) {
                Toast.makeText(this, "广告未准备好", Toast.LENGTH_SHORT).show();
                return;
            }
            if (interstitialAdInfo.hasExpired()) {
                Toast.makeText(this, "广告已失效", Toast.LENGTH_SHORT).show();
                return;
            }
            interstitialAdInfo.showInterstitial(this);
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        if (interstitialAdInfo != null) {
            interstitialAdInfo.release();
            interstitialAdInfo = null;
        }
        // 插屏广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        interstitialAd.setSceneId(ADXiluDemoConstant.INTERSTITIAL_AD_SCENE_ID);
        // 加载插屏广告，参数为广告位ID
        interstitialAd.loadAd(ADXiluDemoConstant.INTERSTITIAL_AD_POS_ID);
    }

}
