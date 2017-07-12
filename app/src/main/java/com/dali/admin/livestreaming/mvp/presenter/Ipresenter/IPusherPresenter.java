package com.dali.admin.livestreaming.mvp.presenter.Ipresenter;

import android.view.View;

import com.dali.admin.livestreaming.mvp.view.Iview.BaseView;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;

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
    public abstract void getPusherUrl(String userId, String groupId, String title, String coverPic, String nickName, String headPic, String location, boolean isRecord);

    /**
     * 启动推流
     * @param videoView 推流显示界面
     * @param pusherConfig 推流配置
     * @param pushUrl 推流url
     */
    public abstract void startPusher(TXCloudVideoView videoView, TXLivePushConfig pusherConfig, String pushUrl);

    /**
     * 停止推流
     */
    public abstract void stopPusher();

    /**
     * 暂停推流
     */
    public abstract void pausePusher();

    /**
     * 继续推流
     */
    public abstract void resumePusher();


    /**
     * 设置PopupWindow弹出
     * @param targetView 弹出的view
     */
    public abstract void showSettingPopupWindow(View targetView,int[] location);
}
