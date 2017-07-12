package com.dali.admin.livestreaming.mvp.view.Iview;

import android.os.Bundle;

import com.dali.admin.livestreaming.model.SimpleUserInfo;

import java.util.ArrayList;

/**
 * 直播播放View
 * Created by dali on 2017/7/11.
 */

public interface ILivePlayerView extends BaseView{
    void onPlayEvent(int i, Bundle bundle);

    void onNetStatus(Bundle bundle);

    void doLikeResult(int result);

    /**
     * 获取观众列表结果
     *
     * @param retCode
     * @param totalCount
     * @param membersList
     */
    void onGroupMembersResult(int retCode, int totalCount, ArrayList<SimpleUserInfo> membersList);
}
