package com.dali.admin.livestreaming.mvp.presenter.Ipresenter;

import com.dali.admin.livestreaming.mvp.view.Iview.IIMChatView;

/**
 * IM聊天管理
 * Created by dali on 2017/7/15.
 */

public abstract class IIMChatPresenter implements BasePresenter{
    protected IIMChatView mIMChatView;

    public IIMChatPresenter(IIMChatView baseView) {
        mIMChatView = baseView;
    }

    /**
     * 创建群
     */
    public abstract void createGroup();

    /**
     * 解散群
     */
    public abstract void deleteGroup();


    /**
     * 加入群
     *
     * @param roomId
     */
    public abstract void joinGroup(final String roomId);

    /**
     * 退出群
     *
     * @param roomId
     */
    public abstract void quitGroup(final String roomId);

    /**
     * 发送消息
     * @param msg
     */
    public abstract void sendTextMsg(final String msg);

}
