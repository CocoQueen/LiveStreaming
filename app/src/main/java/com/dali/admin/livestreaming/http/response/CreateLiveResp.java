package com.dali.admin.livestreaming.http.response;

import com.dali.admin.livestreaming.http.IDontObfuscate;

/**
 * @description: 创建直播返回
 */
public class CreateLiveResp  extends IDontObfuscate {
    private String pushUrl;
    private String liveId;

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveIdl(String liveId) {
        this.liveId = liveId;
    }
}
