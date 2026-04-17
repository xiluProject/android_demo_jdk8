package com.xilu.sdk.demo.activity.ad.adapter.holder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xilu.sdk.demo.R;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.ad.data.ADXiluNativeAdInfo;
import com.xilu.sdk.ad.data.ADXiluNativeExpressAdInfo;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluNativeVideoListener;
import com.xilu.sdk.util.ADXiluAdUtil;
import com.xilu.sdk.util.ADXiluViewUtil;

/**
 * 信息流模板广告ViewHolder
 */
public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

    public NativeExpressAdViewHolder(@NonNull ViewGroup viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_native_ad_express_ad, viewGroup, false));
    }

    public void setData(ADXiluNativeExpressAdInfo nativeExpressAdInfo) {
        // 判断广告Info对象是否被释放（调用过ADXiluNativeAd的release()或ADADXiluNativeAdInfo的release()会释放广告Info对象）
        // 释放后的广告Info对象不能再次使用
        if (!ADXiluAdUtil.adInfoIsRelease(nativeExpressAdInfo)) {
            setVideoListener(nativeExpressAdInfo);
            // 当前是信息流模板广告，getNativeExpressAdView获取的是整个模板广告视图
            View nativeExpressAdView = nativeExpressAdInfo.getNativeExpressAdView((ViewGroup) itemView);
            // 将广告视图添加到容器中的便捷方法
            ADXiluViewUtil.addAdViewToAdContainer(
                    (ViewGroup) itemView,
                    nativeExpressAdView,
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            );

            // 渲染广告视图, 必须调用, 因为是模板广告, 所以传入ViewGroup和响应点击的控件可能并没有用
            // 务必在最后调用
            nativeExpressAdInfo.render((ViewGroup) itemView);
        }
    }

    /**
     * 设置视频监听，无需求可不设置，视频监听回调因平台差异会有所不一，如：某些平台可能没有完成回调等
     */
    private static void setVideoListener(ADXiluNativeAdInfo nativeAdInfo) {
        if (nativeAdInfo.isVideo()) {
            // 设置视频监听，监听回调因三方平台SDK差异有所差异，无需要可不设置
            nativeAdInfo.setVideoListener(new ADXiluNativeVideoListener() {
                @Override
                public void onVideoLoad(ADXiluNativeAdInfo adInfo) {
                    Log.d(ADXiluDemoConstant.TAG, "onVideoLoad.... ");
                }

                @Override
                public void onVideoStart(ADXiluNativeAdInfo adInfo) {
                    Log.d(ADXiluDemoConstant.TAG, "onVideoStart.... ");
                }

                @Override
                public void onVideoPause(ADXiluNativeAdInfo adInfo) {
                    Log.d(ADXiluDemoConstant.TAG, "onVideoPause.... ");
                }

                @Override
                public void onVideoComplete(ADXiluNativeAdInfo adInfo) {
                    Log.d(ADXiluDemoConstant.TAG, "onVideoComplete.... ");
                }

                @Override
                public void onVideoError(ADXiluNativeAdInfo adInfo, ADXiluError error) {
                    Log.d(ADXiluDemoConstant.TAG, "onVideoError.... " + error.toString());
                }
            });
        }
    }
}
