package com.xilu.sdk.demo.activity.ad.adapter.holder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.ad.data.ADXiluAdAppInfo;
import com.xilu.sdk.ad.data.ADXiluNativeAdInfo;
import com.xilu.sdk.ad.data.ADXiluNativeFeedAdInfo;
import com.xilu.sdk.ad.entity.ADXiluAppInfo;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluNativeVideoListener;
import com.xilu.sdk.util.ADXiluAdUtil;
import com.xilu.sdk.util.ADXiluViewUtil;

/**
 * 信息流原生广告BaseViewHolder
 */
public class NativeFeedAdViewHolder extends RecyclerView.ViewHolder {

    private final ImageView ivIcon;
    private final RelativeLayout rlAdContainer;
    private final ImageView ivAdTarget;
    private final TextView tvTitle;
    private final TextView tvDesc;
    private final ImageView ivClose;

    private final ImageView ivImage;
    private final FrameLayout flMediaContainer;

    public NativeFeedAdViewHolder(@NonNull ViewGroup viewGroup) {
        super(
                LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.item_native_ad_native_ad,
                        viewGroup,
                        false)
        );

        rlAdContainer = itemView.findViewById(R.id.rlAdContainer);
        ivIcon = itemView.findViewById(R.id.ivIcon);
        ivAdTarget = itemView.findViewById(R.id.ivAdTarget);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvDesc = itemView.findViewById(R.id.tvDesc);
        ivClose = itemView.findViewById(R.id.ivClose);

        ivImage = itemView.findViewById(R.id.ivImage);
        flMediaContainer = itemView.findViewById(R.id.flMediaContainer);
    }

    public void setData(ADXiluNativeFeedAdInfo nativeFeedAdInfo) {
        // 判断广告Info对象是否被释放（调用过ADXiluNativeAd的release()或ADXiluNativeAdInfo的release()会释放广告Info对象）
        // 释放后的广告Info对象不能再次使用
        if (!ADXiluAdUtil.adInfoIsRelease(nativeFeedAdInfo)) {
            setVideoListener(nativeFeedAdInfo);

            if (ivIcon != null) {
                // 广告icon
                Glide.with(ivIcon).load(nativeFeedAdInfo.getIconUrl()).into(ivIcon);
            }
            if (tvTitle != null) {
                // 广告标题
                tvTitle.setText(nativeFeedAdInfo.getTitle());
            }
            if (tvDesc != null) {
                // 广告详情
                tvDesc.setText(nativeFeedAdInfo.getDesc());
            }
            // 广告平台logo图标
            ivAdTarget.setImageResource(nativeFeedAdInfo.getPlatformIcon());
            // 注册关闭按钮，将关闭按钮点击事件交于SDK托管，以便于回调onAdClose
            nativeFeedAdInfo.registerCloseView(ivClose);

            // 下载类广告六要素
            if (nativeFeedAdInfo instanceof ADXiluAdAppInfo) {
                ADXiluAppInfo appInfo = ((ADXiluAdAppInfo) nativeFeedAdInfo).getAppInfo();
                if (appInfo != null) {
                    // 应用名
                    String name = appInfo.getName();
                    // 开发者
                    String developer = appInfo.getDeveloper();
                    // 版本号
                    String version = appInfo.getVersion();
                    // 隐私地址
                    String privacyUrl = appInfo.getPrivacyUrl();
                    // 权限地址
                    String permissionsUrl = appInfo.getPermissionsUrl();
                    // 功能介绍
                    String descriptionUrl = appInfo.getDescriptionUrl();
                    // 应用大小
                    long size = appInfo.getSize();
                    // icp备案号
                    String icp = appInfo.getIcp();
                }
            }

            if (nativeFeedAdInfo.isVideo()) {
                // 当前信息流原生广告，获取的是多媒体视图（可能是视频、或者图片之类的），mediaView不为空时强烈建议进行展示
                View mediaView = nativeFeedAdInfo.getMediaView(flMediaContainer);
                // 将广告视图添加到容器中的便捷方法，mediaView为空会移除flMediaContainer的所有子View
                ADXiluViewUtil.addAdViewToAdContainer(flMediaContainer, mediaView);
                flMediaContainer.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.GONE);
            } else {
                Glide.with(ivImage.getContext()).load(nativeFeedAdInfo.getImageUrl()).into(ivImage);
                flMediaContainer.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);
            }
            // 注册广告交互, 必须调用
            // 务必最后调用
            nativeFeedAdInfo.registerViewForInteraction(
                    rlAdContainer,
                    rlAdContainer
            );
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
