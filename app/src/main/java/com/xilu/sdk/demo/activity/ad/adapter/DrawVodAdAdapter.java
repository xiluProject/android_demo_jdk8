package com.xilu.sdk.demo.activity.ad.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xilu.sdk.demo.constant.ADXiluDemoConstant;
import com.xilu.sdk.demo.R;
import com.xilu.sdk.ad.data.ADXiluDrawVodAdInfo;
import com.xilu.sdk.ad.entity.DrawVodAdSampleData;
import com.xilu.sdk.ad.error.ADXiluError;
import com.xilu.sdk.ad.listener.ADXiluDrawVodVideoListener;
import com.xilu.sdk.util.ADXiluAdUtil;
import com.xilu.sdk.util.ADXiluViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ciba
 * @description Draw视频信息流广告
 * @date 2020/4/1
 */
public class DrawVodAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 普通数据类型
     */
    private static final int ITEM_VIEW_TYPE_NORMAL_DATA = 0;
    /**
     * 信息流模板广告类型
     */
    private static final int ITEM_VIEW_TYPE_DRAW_VOD_AD = 1;
    private final Context context;

    private List<DrawVodAdSampleData> dataList = new ArrayList<>();

    public DrawVodAdAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemViewType) {
        switch (itemViewType) {
            case ITEM_VIEW_TYPE_DRAW_VOD_AD:
                return new DrawVodAdViewHolder(viewGroup);
            default:
                return new NormalDataViewHolder(viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DrawVodAdSampleData drawVodAdSampleData = dataList.get(position);
        if (viewHolder instanceof NormalDataViewHolder) {
            ((NormalDataViewHolder) viewHolder).setData(context, drawVodAdSampleData.getNormalData(), position);
        } else if (viewHolder instanceof DrawVodAdViewHolder) {
            ((DrawVodAdViewHolder) viewHolder).setData(drawVodAdSampleData.getDrawVodAdInfo());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        DrawVodAdSampleData drawVodAdSampleData = dataList.get(position);
        if (drawVodAdSampleData.getDrawVodAdInfo() == null) {
            return ITEM_VIEW_TYPE_NORMAL_DATA;
        } else {
            return ITEM_VIEW_TYPE_DRAW_VOD_AD;
        }
    }

    /**
     * 移除广告所在的对象，一般模板广告有可能会渲染失败
     */
    public void removeData(ADXiluDrawVodAdInfo drawVodAdInfo) {
        for (int i = 0; i < dataList.size(); i++) {
            DrawVodAdSampleData drawVodAdSampleData = dataList.get(i);
            if (drawVodAdSampleData.getDrawVodAdInfo() == drawVodAdInfo) {
                // 释放广告Info
                drawVodAdInfo.release();
                // 从数据源中移除
                dataList.remove(drawVodAdSampleData);
                // 通知适配器刷新
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 刷新数据
     */
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<DrawVodAdSampleData> drawVodAdSampleDataList) {
        int startPosition = dataList.size();
        dataList.addAll(drawVodAdSampleDataList);
        if (startPosition <= 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(startPosition + 1, dataList.size() - startPosition);
        }
    }

    /**
     * 普通数据ViewHolder
     */
    private static class NormalDataViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvPage;
        private final TextView tvTitle;

        public NormalDataViewHolder(@NonNull ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_draw_vod_ad_normal_data, viewGroup, false));
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPage = itemView.findViewById(R.id.tvPage);
        }

        public void setData(Context context, String normalData, int position) {
            tvTitle.setText("Normal Data position : " + position);
            tvPage.setText(normalData);
        }
    }

    /**
     * Draw视频信息流广告ViewHolder
     */
    private static class DrawVodAdViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlAdContainer;

        public DrawVodAdViewHolder(@NonNull ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_draw_vod_ad, viewGroup, false));
            rlAdContainer = itemView.findViewById(R.id.rlAdContainer);
        }

        public void setData(ADXiluDrawVodAdInfo drawVodAdInfo) {
            // 判断广告Info对象是否被释放（调用过ADXiluDrawVodAd的release()或ADXiluDrawVodAdInfo的release()会释放广告Info对象）
            // 释放后的广告Info对象不能再次使用
            if (!ADXiluAdUtil.adInfoIsRelease(drawVodAdInfo)) {
                // 设置视频监听，监听回调因三方平台SDK差异有所差异，无需要可不设置
                drawVodAdInfo.setVideoListener(new ADXiluDrawVodVideoListener() {
                    @Override
                    public void onVideoLoad(ADXiluDrawVodAdInfo drawVodAdInfo) {
                        Log.d(ADXiluDemoConstant.TAG, "onVideoLoad.... ");
                    }

                    @Override
                    public void onVideoStart(ADXiluDrawVodAdInfo drawVodAdInfo) {
                        Log.d(ADXiluDemoConstant.TAG, "onVideoStart.... ");
                    }

                    @Override
                    public void onVideoPause(ADXiluDrawVodAdInfo drawVodAdInfo) {
                        Log.d(ADXiluDemoConstant.TAG, "onVideoPause.... ");
                    }

                    @Override
                    public void onVideoComplete(ADXiluDrawVodAdInfo drawVodAdInfo) {
                        Log.d(ADXiluDemoConstant.TAG, "onVideoComplete.... ");
                    }

                    @Override
                    public void onVideoError(ADXiluDrawVodAdInfo drawVodAdInfo, ADXiluError error) {
                        Log.d(ADXiluDemoConstant.TAG, "onVideoError.... " + error.toString());
                    }
                });
                // 当前是Draw视频信息流模板广告，getMediaView获取的是模板广告视图
                View mediaView = drawVodAdInfo.getMediaView(rlAdContainer);
                // 将广告视图添加到容器中的便捷方法
                ADXiluViewUtil.addAdViewToAdContainer(rlAdContainer, mediaView);
                // 注册或者渲染广告视图, 必须调用
                drawVodAdInfo.render(rlAdContainer);
            }
        }
    }
}
