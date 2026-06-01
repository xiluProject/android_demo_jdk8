package com.xilu.sdk.demo.activity.ad.fullscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.ad.IADXiluFullScreenVodAd;
import com.xilu.sdk.ad.data.ADXiluFullScreenVodAdInfo;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluFullScreenVodAdListener;
import com.xilu.sdk.util.ADXiluToastUtil;

/**
 * @author ciba
 * @description 全屏视频广告示例
 * @date 2020/3/27
 */
public class FullScreenVodAdActivity extends AppCompatActivity implements View.OnClickListener {
    private IADXiluFullScreenVodAd fullScreenVodAd;
    private ADXiluFullScreenVodAdInfo fullScreenVodAdInfo;

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

        btnLoadAd.setText("获取全屏视频广告");
        btnShowAd.setText("展示全屏视频广告");

        btnLoadAd.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);
    }

    private void initAd() {
        fullScreenVodAd = com.xilu.sdk.ad.factory.AdFactoryManager.createFullScreenVodAd(this);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        // 注：仅debug模式为true时生效。
        fullScreenVodAd.setOnlySupportPlatform(ADXiluDemoConstant.FULL_SCREEN_VOD_AD_ONLY_SUPPORT_PLATFORM);
        // 设置全屏视频监听
        fullScreenVodAd.setListener(new ADXiluFullScreenVodAdListener() {

            @Override
            public void onAdReceive(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
                // TODO 全屏视频广告对象一次成功拉取的广告数据只允许展示一次
                FullScreenVodAdActivity.this.fullScreenVodAdInfo = fullScreenVodAdInfo;
                ADXiluToastUtil.show(getApplicationContext(), "全屏视频广告获取成功");
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onVideoCache(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
                // 部分渠道不会回调该方法，请在onAdReceive做广告展示处理
                Log.d(ADXiluDemoConstant.TAG, "onVideoCache...");
            }

            @Override
            public void onVideoComplete(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onVideoComplete...");
            }

            @Override
            public void onVideoError(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo, ADXiluError error) {
                Log.d(ADXiluDemoConstant.TAG, "onVideoError..." + error.toString());
            }

            @Override
            public void onAdExpose(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(ADXiluFullScreenVodAdInfo fullScreenVodAdInfo) {
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
            if (fullScreenVodAdInfo == null) {
                Toast.makeText(this, "无可用广告", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!fullScreenVodAdInfo.isReady()) {
                Toast.makeText(this, "广告未准备好", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fullScreenVodAdInfo.hasExpired()) {
                Toast.makeText(this, "广告已失效", Toast.LENGTH_SHORT).show();
                return;
            }
            fullScreenVodAdInfo.showFullScreenVod(this);
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        if (fullScreenVodAdInfo != null) {
            fullScreenVodAdInfo.release();
            fullScreenVodAdInfo = null;
        }
        fullScreenVodAd.loadAd(ADXiluDemoConstant.FULL_SCREEN_VOD_AD_POS_ID);
    }

}
