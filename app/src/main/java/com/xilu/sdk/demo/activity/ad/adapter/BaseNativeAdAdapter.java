package com.xilu.sdk.demo.activity.ad.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.xilu.sdk.ad.data.ADXiluNativeAdInfo;

import java.util.List;

public abstract class BaseNativeAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public abstract void removeData(ADXiluNativeAdInfo adInfo);

    public abstract void clearData();

    public abstract void addData(List<Object> datas);
}
