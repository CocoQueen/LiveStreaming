package com.dali.admin.livestreaming.http.request;

import com.dali.admin.livestreaming.http.response.ResList;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.model.LiveInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 进入房间接口请求
 * Created by dali on 2017/7/11.
 */

public class EnterGroupRequest extends IRequest{

    public EnterGroupRequest(int requestId, String userId, String liveId, String hostId, String groupId) {
        mRequestId = requestId;
        mParams.put("action", "enterGroup");
        mParams.put("userId", userId);
        mParams.put("groupId", groupId);
        mParams.put("hostId", hostId);
        mParams.put("liveId", liveId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<LiveInfo>>>() {
        }.getType();
    }
}
