package com.dali.admin.livestreaming.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.base.BaseActivity;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.dali.admin.livestreaming.mvp.presenter.LivePlayerPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.ILivePlayerView;
import com.dali.admin.livestreaming.utils.Constants;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;

/**
 * 直播播放
 */
public class LivePlayerActivity extends BaseActivity implements ILivePlayerView {

    private LivePlayerPresenter mLivePlayerPresenter;
//    private LiveInfo mLiveInfo;
    private String mPlayUrl;
    private String TAG = LivePlayerActivity.class.getSimpleName();
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayerConfig;

    @Override
    protected void setActionBar() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView = obtainView(R.id.video_view);
        mTXPlayerConfig = new TXLivePlayConfig();

        mLivePlayerPresenter = new LivePlayerPresenter(this);
        mTXPlayerConfig.setConnectRetryCount(3);
        mTXPlayerConfig.setConnectRetryInterval(3);
        mLivePlayerPresenter.initPlayerView(mTXCloudVideoView, mTXPlayerConfig);

//        mTXCloudVideoView = obtainView(R.id.video_view);
//        //创建player对象
//        mLivePlayer = new TXLivePlayer(this);
//        mLivePlayer.enableHardwareDecode(true);
//        //关键player对象与界面view
//        mTXCloudVideoView.setVisibility(View.VISIBLE);
//        mLivePlayer.setPlayerView(mTXCloudVideoView);
//
        mPlayUrl = getIntent().getStringExtra(Constants.PLAY_URL);
        if (mPlayUrl !=null) {
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        }else {
            showToast("player url is null");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    public static void invoke(Activity activity, String playerUrl) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.PLAY_URL, playerUrl);
        activity.startActivityForResult(intent, Constants.LIVE_PLAYER_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停
//        mLivePlayer.pause();
        mLivePlayerPresenter.playerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //继续
//        mLivePlayer.resume();
        mLivePlayerPresenter.playerResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLivePlayer.stopPlay(true); // true代表清除最后一帧画面
//        mTXCloudVideoView.onDestroy();
        mLivePlayerPresenter.stopPlay(true);
    }

    //停止播放
//    private void stopPlay() {
//        mLivePlayerPresenter.quitGroup(ACache.get(this).getAsString("user_id"),
//                mLiveInfo.getLiveId(), mLiveInfo.getUserInfo().getUserId(), mLiveInfo.getGroupId());
//        mLivePlayerPresenter.stopPlay(true);
//        mTXCloudVideoView.onDestroy();
//        ACache.get(this).put(mLiveInfo.getLiveId() + "_first_praise", "0");
//    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void showMsg(int msgId) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {
        //播放信息及状态
        Log.i(TAG, "net status, CPU:" + bundle.getString(TXLiveConstants.NET_STATUS_CPU_USAGE) +
                ", RES:" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT) +
                ", SPD:" + bundle.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps" +
                ", FPS:" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS) +
                ", ARA:" + bundle.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps" +
                ", VRA:" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps");
    }

    @Override
    public void doLikeResult(int result) {

    }

    @Override
    public void onGroupMembersResult(int retCode, int totalCount, ArrayList<SimpleUserInfo> membersList) {

    }
}
