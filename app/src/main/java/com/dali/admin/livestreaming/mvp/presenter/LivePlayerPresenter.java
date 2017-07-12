package com.dali.admin.livestreaming.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.dali.admin.livestreaming.mvp.presenter.Ipresenter.ILivePlayerPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.ILivePlayerView;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 播放功能Presenter
 * Created by dali on 2017/7/11.
 */

public class LivePlayerPresenter extends ILivePlayerPresenter implements ITXLivePlayListener {

    private static final String TAG = LivePlayerPresenter.class.getSimpleName();
    private ILivePlayerView mILivePlayerView;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mTXLivePlayer;

    public LivePlayerPresenter(ILivePlayerView baseView) {
        super(baseView);
        this.mILivePlayerView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig) {
        mTXCloudVideoView = cloudVideoView;
        mTXLivePlayer = new TXLivePlayer(mILivePlayerView.getContext());
        //硬编码
        mTXLivePlayer.enableHardwareDecode(true);
        mTXLivePlayer.setPlayerView(cloudVideoView);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(livePlayConfig);
    }

    //硬编码
    public void enableHardwareDecode(boolean decode) {
        mTXLivePlayer.enableHardwareDecode(false);
    }

    @Override
    public void playerPause() {
        mTXLivePlayer.pause();
    }

    @Override
    public void playerResume() {
        mTXLivePlayer.resume();
    }

    @Override
    public void startPlay(String playUrl, int playType) {
        //flv,rtmp,hls
        mTXLivePlayer.startPlay(playUrl, playType);
    }

    @Override
    public void stopPlay(boolean isClearLastImg) {
        //isClearLastImg:是否清除最后一帧信息
        mTXLivePlayer.stopPlay(isClearLastImg);
        mTXCloudVideoView.onDestroy();
    }

//    @Override
//    public void doLike(String userId, String liveId, String hostId, String groupId) {
//        LiveLikeRequest req = new LiveLikeRequest(1000, userId, liveId, hostId, groupId);
//        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
//            @Override
//            public void onStart(int requestId) {
//
//            }
//
//            @Override
//            public void onSuccess(int requestId, Response response) {
//                if (response.getStatus() == RequestComm.SUCCESS) {
//                    mILivePlayerView.doLikeResult(0);
//                } else {
//                    mILivePlayerView.doLikeResult(1);
//                }
//                Log.i(TAG, "onSuccess: doLike");
//            }
//
//            @Override
//            public void onFailure(int requestId, int httpStatus, Throwable error) {
//                mILivePlayerView.doLikeResult(1);
//                Log.i(TAG, "onFailure: doLike");
//            }
//        });
//    }

//    @Override
//    public void enterGroup(String userId, String liveId, String hostId, String groupId) {
//        EnterGroupRequest req = new EnterGroupRequest(RequestComm.enterlive, userId, liveId, hostId, groupId);
//        AsyncHttp.instance().postJson(req, null);
//    }
//
//    @Override
//    public void quitGroup(String userId, String liveId, String hostId, String groupId) {
//        QuitGroupRequest quiteRequest = new QuitGroupRequest(RequestComm.QuitGroup, userId, liveId, hostId, groupId);
//        AsyncHttp.instance().postJson(quiteRequest, null);
//    }
//
//    @Override
//    public void groupMember(String userId, String liveId, String hostId, String groupId, int pageIndex, int pageSize) {
//        final GroupMemberRequest req = new GroupMemberRequest(RequestComm.memberList, userId, liveId, hostId, groupId, 1, 20);
//        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
//            @Override
//            public void onStart(int requestId) {
//
//            }
//
//            @Override
//            public void onSuccess(int requestId, Response response) {
//                if (response != null && response.getStatus() == RequestComm.SUCCESS) {
//                    ResList resList = (ResList) response.getData();
//                    if (resList != null && resList.getItems() != null) {
//                        mILivePlayerView.onGroupMembersResult(0,resList.getTotalCount(),(ArrayList<SimpleUserInfo>)resList.getItems());
//                    }else {
//                        mILivePlayerView.onGroupMembersResult(1,0,null);
//                    }
//                }else {
//                    mILivePlayerView.onGroupMembersResult(1,0,null);
//                }
//            }
//
//            @Override
//            public void onFailure(int requestId, int httpStatus, Throwable error) {
//                mILivePlayerView.onGroupMembersResult(1,0,null);
//            }
//        });
//    }

    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        Log.i(TAG, "onPlayEvent: event = " + event);
        mILivePlayerView.onPlayEvent(event,bundle);
    }

    @Override
    public void onNetStatus(Bundle bundle) {
        Log.i(TAG, "onNetStatus: bundle = " + bundle.getString(TXLiveConstants.NET_STATUS_CPU_USAGE));
        mILivePlayerView.onNetStatus(bundle);
    }
}
