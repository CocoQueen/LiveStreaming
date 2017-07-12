package com.dali.admin.livestreaming.mvp.view.Iview;

import android.app.FragmentManager;

/**
 * 推流View接口
 * Created by dali on 2017/5/7.
 */

public interface IPusherView extends BaseView {
    /**
     * 获取推流地址
     * @param pushUrl
     * @param errorCode 0表示成功 1表示失败
     */
    void onGetPushUrl(String pushUrl,int errorCode);

    FragmentManager getFragmentMgr();
}
