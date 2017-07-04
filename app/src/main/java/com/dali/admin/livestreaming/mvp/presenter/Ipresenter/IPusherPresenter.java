package com.dali.admin.livestreaming.mvp.presenter.Ipresenter;

import com.dali.admin.livestreaming.mvp.view.Iview.BaseView;

/**
 * 推流
 * Created by dali on 2017/5/7.
 */

public abstract class IPusherPresenter implements BasePresenter {
    protected BaseView mBaseView;

    public IPusherPresenter(BaseView baseView) {
        mBaseView = baseView;
    }

    /**
     * 获取推流地址
     * @param userId 用户Id
     * @param groupId 群组Id
     * @param title 直播房间标题
     * @param coverPic 直播封面图
     * @param location 位置
     */
    public abstract void getPusherUrl(final String userId, final String groupId, final String title, final String coverPic, final String location,final boolean isRecord);

}
