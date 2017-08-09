package com.dali.admin.livestreaming.mvp.presenter.Ipresenter;

import com.dali.admin.livestreaming.mvp.view.Iview.ILivePlayerView;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 直播播放Presenter
 * Created by dali on 2017/7/11.
 */

public abstract class ILivePlayerPresenter implements BasePresenter {

    protected ILivePlayerView mBaseView;

    public ILivePlayerPresenter(ILivePlayerView baseView) {
        mBaseView = baseView;
    }

    /**
     * 初始化播放器
     *
     * @param cloudVideoView
     * @param livePlayConfig
     */
    public abstract void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig);

    /**
     * pause
     */
    public abstract void playerPause();

    /**
     * Resume
     */
    public abstract void playerResume();

    /**
     * 开始播放
     * @param playUrl
     * @param playType
     */
    public abstract void startPlay(String playUrl,
                                   int playType);

    /**
     * stop
     * @param isClearLastImg
     */
    public abstract void stopPlay(boolean isClearLastImg);

    /**
     * 点赞接口
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void doLike(String userId, String liveId, String hostId, String groupId);

    /**
     * 进入直播群
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void enterGroup(String userId, String liveId, String hostId, String groupId);

    /**
     * 退出直播群
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void quitGroup(String userId, String liveId, String hostId, String groupId);

    /**
     * 当前观看直播的用户列表，限制50个人
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     * @param pageIndex
     * @param pageSize
     */
    public abstract void groupMember(String userId, String liveId, String hostId, String groupId, int pageIndex, int pageSize);

}
