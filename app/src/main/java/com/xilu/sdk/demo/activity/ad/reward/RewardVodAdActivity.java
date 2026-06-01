package com.xilu.sdk.demo.activity.ad.reward;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.demo.util.SPUtil;
import com.xilu.sdk.ad.IADXiluRewardVodAd;
import com.xilu.sdk.ad.data.ADXiluRewardVodAdInfo;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluRewardVodAdListener;
import com.xilu.sdk.util.ADXiluToastUtil;

/**
 * @author ciba
 * @description 激励视频广告示例
 * @date 2020/3/27
 */
public class RewardVodAdActivity extends AppCompatActivity implements View.OnClickListener {
    private IADXiluRewardVodAd rewardVodAd;
    private ADXiluRewardVodAdInfo rewardVodAdInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_vod);

        initListener();
        initAd();
    }

    private void initListener() {
        findViewById(R.id.btnLoadAd).setOnClickListener(this);
        findViewById(R.id.btnShowAd).setOnClickListener(this);
    }

    private void initAd() {
        boolean issensor = SPUtil.getBoolean(this, "sensor");
        // 创建激励视频广告实例
        rewardVodAd = com.xilu.sdk.ad.factory.AdFactoryManager.createRewardVodAd(this);

//        ADXiluRewardExtra rewardExtra = new ADXiluRewardExtra("用户id");
//        // 设置激励视频服务端验证的自定义信息
//        rewardExtra.setCustomData("设置激励视频服务端验证的自定义信息");
//        // 设置激励视频服务端激励名称
//        rewardExtra.setRewardName("激励名称");
//        // 设置激励视频服务端激励数量
//        rewardExtra.setRewardAmount(1);
//        rewardVodAd.setLocalExtraParams(new ADXiluExtraParams.Builder().rewardExtra(rewardExtra).build());

        // 创建额外参数实例
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
//                .rewardExtra(rewardExtra)
                // 设置视频类广告是否静音
                .setVideoWithMute(ADXiluDemoConstant.REWARD_AD_PLAY_WITH_MUTE)
                .setAdShakeDisable(issensor)
                .build();

        rewardVodAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        // 注：仅debug模式为true时生效。
        rewardVodAd.setOnlySupportPlatform(ADXiluDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
        // 设置激励视频广告监听
        rewardVodAd.setListener(new ADXiluRewardVodAdListener() {

            @Override
            public void onAdReceive(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                // TODO 激励视频广告对象一次成功拉取的广告数据只允许展示一次
                RewardVodAdActivity.this.rewardVodAdInfo = rewardVodAdInfo;
                ADXiluToastUtil.show(getApplicationContext(), "激励视频广告获取成功");
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onVideoCache(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                // 部分渠道不会回调该方法，请在onAdReceive做广告展示处理
                Log.d(ADXiluDemoConstant.TAG, "onVideoCache...");
            }

            @Override
            public void onVideoComplete(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onVideoComplete...");
            }

            @Override
            public void onVideoError(ADXiluRewardVodAdInfo rewardVodAdInfo, ADXiluError error) {
                Log.d(ADXiluDemoConstant.TAG, "onVideoError..." + error.toString());
            }

            @Override
            public void onReward(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onReward...");
            }

            @Override
            public void onAdExpose(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADXiluRewardVodAdInfo rewardVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADXiluRewardVodAdInfo rewardVodAdInfo) {
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
            if (rewardVodAdInfo == null) {
                Toast.makeText(this, "无可用广告", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!rewardVodAdInfo.isReady()) {
                Toast.makeText(this, "广告未准备好", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rewardVodAdInfo.hasExpired()) {
                Toast.makeText(this, "广告已失效", Toast.LENGTH_SHORT).show();
                return;
            }
            rewardVodAdInfo.showRewardVod(this);
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        if (rewardVodAdInfo != null) {
            rewardVodAdInfo.release();
            rewardVodAdInfo = null;
        }

        // 激励广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rewardVodAd.setSceneId(ADXiluDemoConstant.REWARD_VOD_AD_SCENE_ID);
        // 加载激励视频广告，参数为广告位ID
        rewardVodAd.loadAd(ADXiluDemoConstant.REWARD_VOD_AD_POS_ID);
    }

}
