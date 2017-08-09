package com.dali.admin.livestreaming.mvp.view.Iview;

import com.dali.admin.livestreaming.model.GiftWithUerInfo;
import com.dali.admin.livestreaming.model.SimpleUserInfo;

/**
 * Created by dali on 2017/7/15.
 */

public interface IIMChatView extends BaseView{
    /**
     * 加入群组回调
     *
     * @param code 错误码，成功时返回0，失败时返回相应错误码
     * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
     */
    void onJoinGroupResult(int code, String msg);

    /**
     * 群组删除回调，在主播群组解散时被调用
     */
    void onGroupDeleteResult();

    void handleTextMsg(SimpleUserInfo userInfo, String text);

    /**
     * 点赞消息处理
     * @param userInfo
     */
    void handlePraiseMsg(SimpleUserInfo userInfo);

    /**
     * 点赞每一次消息
     * @param userInfo
     */
    void handlePraiseFirstMsg(SimpleUserInfo userInfo);

    /**
     * 观众离开消息处理
     * @param userInfo
     */
    void handleExitLiveMsg(SimpleUserInfo userInfo);

    /**
     * 直播结束
     * @param giftWithUerInfo
     */
    void handleGift(GiftWithUerInfo giftWithUerInfo);

    void handleEnterLiveMsg(SimpleUserInfo simpleUserInfo);
}
