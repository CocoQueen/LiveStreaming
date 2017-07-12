package com.dali.admin.livestreaming.http.request;

import com.dali.admin.livestreaming.http.response.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 直播喜欢请求
 */
public class LiveLikeRequest extends IRequest {

    public LiveLikeRequest(int requestId, String userId, String liveId, String hostId, String groupId) {
        mRequestId = requestId;
        mParams.put("action", "liveLike");
        mParams.put("userId", userId);
        mParams.put("liveId", liveId);
        mParams.put("hostId", hostId);
        mParams.put("groupId", groupId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response>() {}.getType();
    }
}