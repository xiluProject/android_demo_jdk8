package com.xilu.sdk.demo.activity.ad.adapter;

import com.xilu.sdk.ad.data.ADXiluDrawVodAdInfo;

public class DrawVodAdSampleData {
    private ADXiluDrawVodAdInfo drawVodAdInfo;
    private String normalData;

    public DrawVodAdSampleData(ADXiluDrawVodAdInfo drawVodAdInfo) {
        this.drawVodAdInfo = drawVodAdInfo;
    }

    public DrawVodAdSampleData(String normalData) {
        this.normalData = normalData;
    }

    public ADXiluDrawVodAdInfo getDrawVodAdInfo() {
        return drawVodAdInfo;
    }

    public void setDrawVodAdInfo(ADXiluDrawVodAdInfo drawVodAdInfo) {
        this.drawVodAdInfo = drawVodAdInfo;
    }

    public String getNormalData() {
        return normalData;
    }

    public void setNormalData(String normalData) {
        this.normalData = normalData;
    }
}
