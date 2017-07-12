package com.dali.admin.livestreaming.http.request;

import com.dali.admin.livestreaming.http.response.ResList;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.model.LiveInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 退出直播请求
 * Created by dali on 2017/7/11.
 */

public class QuitGroupRequest extends IRequest{
    /**
     * 退出群组
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     */
    public QuitGroupRequest(int requestId, String userId, String liveId, String hostId, String groupId) {
        mRequestId = requestId;
        mParams.put("action", "QuitGroup");
        mParams.put("userId", userId);
        mParams.put("hostId", hostId);
        mParams.put("liveId", liveId);
        mParams.put("groupId", groupId);
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
