package com.dali.admin.livestreaming.http.request;

import com.dali.admin.livestreaming.http.response.ResList;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 观众列表请求
 * Created by dali on 2017/7/11.
 */

public class GroupMemberRequest extends IRequest{

    public GroupMemberRequest(int requestId, String userId, String liveId, String hostId, String groupId,
                             int pageIndex, int pageSize) {
        mRequestId = requestId;
        mParams.put("action", "groupMember");
        mParams.put("userId", userId);
        mParams.put("groupId", groupId);
        mParams.put("liveId", liveId);
        mParams.put("hostId", hostId);
        mParams.put("pageIndex", pageIndex);
        mParams.put("pageSize", pageSize);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<SimpleUserInfo>>>() {
        }.getType();
    }
}
