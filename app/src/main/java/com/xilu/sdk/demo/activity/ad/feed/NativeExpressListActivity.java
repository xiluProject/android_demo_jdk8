package com.xilu.sdk.demo.activity.ad.feed;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.demo.activity.ad.adapter.NativeAdAdapter;
import com.xilu.sdk.demo.activity.ad.widget.MySmartRefreshLayout;
import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.ad.ADXiluNativeAd;
import com.xilu.sdk.ad.data.ADXiluNativeAdInfo;
import com.xilu.sdk.ad.entity.ADXiluAdSize;
import com.xilu.sdk.ad.entity.ADXiluExtraParams;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluNativeAdListener;
import com.xilu.sdk.util.ADXiluDisplayUtil;

import java.util.ArrayList;
import java.util.List;


public class NativeExpressListActivity extends AppCompatActivity implements OnRefreshLoadMoreListener {
    private MySmartRefreshLayout refreshLayout;
    private NativeAdAdapter nativeAdAdapter;
    private ADXiluNativeAd nativeAd;
    private List<Object> tempDataList = new ArrayList<>();
    private int refreshType;
    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);
        initView();
        initListener();
        initAd();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.refreshLayout);

        nativeAdAdapter = new NativeAdAdapter();
        recyclerView.setAdapter(nativeAdAdapter);
    }

    private void initListener() {
        refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    private void initAd() {
        int widthPixels = getResources().getDisplayMetrics().widthPixels - ADXiluDisplayUtil.dp2px(20);
        // 创建信息流广告实例
        nativeAd = new ADXiluNativeAd(this);

        // 创建额外参数实例
        ADXiluExtraParams extraParams = new ADXiluExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前仅头条平台需要，没有接入头条可不设置)，单位为px，高度如果小于等于0则高度自适应
                .adSize(new ADXiluAdSize(widthPixels, 0))
                // 设置广告视图中MediaView的预期宽高(目前仅Inmobi平台需要,Inmobi的MediaView高度为自适应，没有接入Inmobi平台可不设置)，单位为px
                .nativeAdMediaViewSize(new ADXiluAdSize((int) (widthPixels - 24 * getResources().getDisplayMetrics().density)))
//                .nativeStyle(adNativeStyle)
                // 设置信息流广告适配播放是否静音，默认静音，目前广点通、百度、汇量、快手、Admobile支持修改
                .nativeAdPlayWithMute(ADXiluDemoConstant.NATIVE_AD_PLAY_WITH_MUTE)
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数，如果有接入头条、Inmobi平台，如果包含这些平台该参数必须设置
        nativeAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置
        nativeAd.setOnlySupportPlatform(ADXiluDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        nativeAd.setListener(new ADXiluNativeAdListener() {
            @Override
            public void onRenderFailed(ADXiluNativeAdInfo adInfo, ADXiluError error) {
                Log.d(ADXiluDemoConstant.TAG, "onRenderFailed: " + error.toString());
                // 广告渲染失败，释放和移除adInfo
                nativeAdAdapter.removeData(adInfo);
            }

            @Override
            public void onAdReceive(List<ADXiluNativeAdInfo> adInfoList) {
                Log.d(ADXiluDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                for (int i = 0; i < adInfoList.size(); i++) {
                    Log.d(
                            ADXiluDemoConstant.TAG,
                            "platform : " + adInfoList.get(i).getPlatform() +
                                    " price : " +  adInfoList.get(i).getECPM() +
                                    " getEcpmPrecision : " +  adInfoList.get(i).getEcpmPrecision()
                    );

                    int index = i * 5;
                    ADXiluNativeAdInfo nativeAdInfo = adInfoList.get(i);
                    if (index >= tempDataList.size()) {
                        tempDataList.add(nativeAdInfo);
                    } else {
                        tempDataList.add(index, nativeAdInfo);
                    }
                    Log.d(ADXiluDemoConstant.TAG, "onAdReceive hash code: " + adInfoList.get(i).hashCode());
                }
                nativeAdAdapter.addData(tempDataList);
                refreshLayout.finish(refreshType, true, false);
            }

            @Override
            public void onAdExpose(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdExpose: " + adInfo.hashCode());
                setTvMessage("onAdExpose..." + adInfo.toString());
            }

            @Override
            public void onAdClick(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClick: " + adInfo.hashCode());
                setTvMessage("onAdClick..." + adInfo.toString());
            }

            @Override
            public void onAdClose(ADXiluNativeAdInfo adInfo) {
                Log.d(ADXiluDemoConstant.TAG, "onAdClose: " + adInfo.hashCode());
                // 广告被关闭，释放和移除adInfo
                nativeAdAdapter.removeData(adInfo);
                setTvMessage("onAdClose..." + adInfo.toString());
            }

            @Override
            public void onAdFailed(ADXiluError error) {
                if (error != null) {
                    Log.d(ADXiluDemoConstant.TAG, "onAdFailed: " + error.toString());
                }
                nativeAdAdapter.addData(tempDataList);
                refreshLayout.finish(refreshType, false, false);
                setTvMessage("onAdFailed...");
            }
        });

        // 触发刷新
        refreshLayout.autoRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshType = MySmartRefreshLayout.TYPE_LOAD_MORE;
        loadData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshType = MySmartRefreshLayout.TYPE_FRESH;
        nativeAdAdapter.clearData();
        loadData();
    }

    /**
     * 加载数据和广告
     */
    private void loadData() {
        tempDataList.clear();
        mockNormalDataRequest();
        // 信息流广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        nativeAd.setSceneId(ADXiluDemoConstant.NATIVE_AD_SCENE_ID);
        // 请求广告数据，参数一广告位ID，参数二请求数量[1,3]
        nativeAd.loadAd(ADXiluDemoConstant.NATIVE_AD_POS_ID2, ADXiluDemoConstant.NATIVE_AD_COUNT);
    }

    /**
     * 模拟普通数据请求
     */
    private void mockNormalDataRequest() {
        for (int i = 0; i < 20; i++) {
            tempDataList.add("模拟的普通数据 : " + (nativeAdAdapter == null ? 0 : nativeAdAdapter.getItemCount() + i));
        }
    }

    public void setTvMessage(String msg) {
        stringBuffer.append(msg);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initAd();
    }
}
