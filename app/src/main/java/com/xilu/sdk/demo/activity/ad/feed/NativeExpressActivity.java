package com.xilu.sdk.demo.activity.ad.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.ad.ADXiluNativeAd;
import com.xilu.sdk.ad.data.ADXiluNativeAdInfo;
import com.xilu.sdk.ad.data.ADXiluNativeExpressAdInfo;
import com.xilu.sdk.ad.entity.ADXiluAdSize;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluNativeAdListener;
import com.xilu.sdk.util.ADXiluAdUtil;
import com.xilu.sdk.util.ADXiluViewUtil;

import java.util.List;


/**
 * @author : 草莓
 * @date : 2021/11/01
 * @description : 将信息流模板广告放到RelativeLayout进行展示案例
 */
public class NativeExpressActivity extends AppCompatActivity {

    private Button btnLoadAndShowAd;
    private Button btnLoadAd;
    private Button btnShowAd;
    private RelativeLayout rlExpressAd;

    private ADXiluNativeAd mAd;
    private ADXiluNativeAdInfo adInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_native_express_ad);

        initView();
        initListener();

    }

    private void initView() {
        btnLoadAndShowAd = findViewById(R.id.btnLoadAndShowAd);
        btnLoadAd = findViewById(R.id.btnLoadAd);
        btnShowAd = findViewById(R.id.btnShowAd);
        rlExpressAd = findViewById(R.id.rlExpressAd);
    }

    private void initListener() {
        btnLoadAndShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAndShowAd();
            }
        });
        btnLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(false);
            }
        });

        btnShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd();
            }
        });
    }

    /**
     * 加载成功并展示
     */
    private void loadAndShowAd() {
        loadAd(true);
    }

    /**
     * 加载广告
     *
     * @param isLoadSuccessShows 是否加载信息流广告成功后立刻展示广告
     */
    private void loadAd(boolean isLoadSuccessShows) {
        releaseAd();

        // 模板广告容器宽度
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        // 创建信息流广告实例
        mAd = new ADXiluNativeAd(this);
        // 创建额外参数实例
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                // 设置整个广告视图预期宽高，单位为px，高度如果小于等于0则高度自适应
                .adSize(new ADXiluAdSize(widthPixels, 0))
                // 设置信息流广告适配播放是否静音，默认静音，目前优量汇、百度、汇量、快手、天目支持修改
                .nativeAdPlayWithMute(ADXiluDemoConstant.NATIVE_AD_PLAY_WITH_MUTE)
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数
        mAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        // 注：仅debug模式为true时生效。
        mAd.setOnlySupportPlatform(ADXiluDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        mAd.setListener(new ADXiluNativeAdListener() {
            @Override
            public void onRenderFailed(ADXiluNativeAdInfo adInfo, ADXiluError error) {
                Log.d(ADXiluDemoConstant.TAG, "onRenderFailed: " + error.toString());
            }

            @Override
            public void onAdReceive(List<ADXiluNativeAdInfo> adInfoList) {
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                if (adInfoList != null && adInfoList.size() > 0) {
                    Toast.makeText(NativeExpressActivity.this, "广告获取成功", Toast.LENGTH_SHORT).show();
                    adInfo = adInfoList.get(0);

                    // 是否立刻展示广告
                    if (isLoadSuccessShows) {
                        showAd();
                    }
                }
            }

            @Override
            public void onAdExpose(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose: " + adInfo.hashCode());
            }

            @Override
            public void onAdClick(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick: " + adInfo.hashCode());
            }

            @Override
            public void onAdClose(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClose: " + adInfo.hashCode());
                closeAd();
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                if (error != null) {
                    Log.d(ADXiluDemoConstant.TAG, "onAdFailed: " + error.toString());
                }
            }
        });

        mAd.loadAd(ADXiluDemoConstant.NATIVE_AD_POS_ID1, 1);
    }

    /**
     * 展示广告
     */
    private void showAd() {
        if (ADXiluAdUtil.adInfoIsRelease(adInfo)) {
            Toast.makeText(this, "广告已被释放", Toast.LENGTH_SHORT).show();
            Log.d(ADXiluDemoConstant.TAG, "广告已被释放");
            return;
        }
        if (adInfo == null) {
            Toast.makeText(this, "未获取到广告，请先请求广告", Toast.LENGTH_SHORT).show();
            Log.d(ADXiluDemoConstant.TAG, "未获取到广告，请先请求广告");
            return;
        }

        ADXiluNativeExpressAdInfo nativeExpressAdInfo;
        if (!adInfo.isNativeExpress()) {
            Toast.makeText(this, "当前请求到广告非信息流模板广告，请使用信息流模板广告位", Toast.LENGTH_SHORT).show();
            Log.d(ADXiluDemoConstant.TAG, "当前请求到广告非信息流模板广告，请使用信息流模板广告位");
            return;
        } else {
            // 将广告对象转换成模板广告
            nativeExpressAdInfo = (ADXiluNativeExpressAdInfo) adInfo;
        }

        // 当前是信息流模板广告，getNativeExpressAdView获取的是整个模板广告视图
        View nativeExpressAdView = nativeExpressAdInfo.getNativeExpressAdView(rlExpressAd);
        // 将广告视图添加到容器中的便捷方法
        ADXiluViewUtil.addAdViewToAdContainer(
                rlExpressAd,
                nativeExpressAdView,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        // 渲染广告视图, 必须调用, 因为是模板广告, 所以传入ViewGroup和响应点击的控件可能并没有用
        // 务必在最后调用
        nativeExpressAdInfo.render(rlExpressAd);

    }

    /**
     * 关闭广告
     */
    private void closeAd() {
        if (rlExpressAd != null) {
            rlExpressAd.removeAllViews();
        }
        releaseAd();
    }

    /**
     * 释放广告
     */
    private void releaseAd() {
        if (mAd != null) {
            mAd.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAd();
    }
}
