package com.dali.admin.livestreaming.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.base.BaseActivity;
import com.dali.admin.livestreaming.http.AsyncHttp;
import com.dali.admin.livestreaming.http.request.CreateLiveRequest;
import com.dali.admin.livestreaming.http.request.RequestComm;
import com.dali.admin.livestreaming.http.response.CreateLiveResp;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.logic.IMLogin;
import com.dali.admin.livestreaming.logic.ImUserInfoMgr;
import com.dali.admin.livestreaming.mvp.presenter.PusherPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IPusherView;
import com.dali.admin.livestreaming.utils.AsimpleCache.ACache;
import com.dali.admin.livestreaming.utils.Constants;
import com.dali.admin.livestreaming.utils.HWSupportList;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class LivePublisherActivity extends BaseActivity implements IPusherView, View.OnClickListener {

    private static final String TAG = LivePublisherActivity.class.getSimpleName();
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();
//    private TXLivePusher mTXLivePusher;

    private boolean mPasuing = false;

    private String mPushUrl;
    private String mLiveId;
    private String mUserId;
    private String mTitle;
    private String mCoverPicUrl;
    private String mHeadPicUrl;
    private String mNickName;
    private String mLocation;
    private boolean mIsRecord;

    private PusherPresenter mPusherPresenter;
    private String mRoomId;

    private View btnSettingView;
    private int[] mSettingLocation = new int[2];
    private TXAudioPlayer mAudioPlayer;

    @Override
    protected void setBeforeLayout() {
        super.setBeforeLayout();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    @Override
    protected void setActionBar() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.disableLog(false);
        }
        //创建群组
        createGroup();
    }

    private void createGroup() {
        //检测登录状态，在登录的前提下创建群组，并回调登录监听，成功则将群组号传到推流中
        checkLoginState(new IMLogin.IMLoginListener() {

            @Override
            public void onSuccess() {
                IMLogin.getInstace().removeIMLoginListener();
                TIMGroupManager.getInstance().createAVChatroomGroup("cniaow_live", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.e(TAG, "create av group failed. code: " + code + " errmsg: " + msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        Log.e(TAG, "create av group succ,groupId: " + roomId);
                        mRoomId = roomId;
                        //加入群组并进行推流
                        onJoinGroupResult(0, roomId);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                IMLogin.getInstace().removeIMLoginListener();
            }
        });
    }

    //加入群组并进行推流
    private void onJoinGroupResult(int code, String msg) {
        if (0 == code) {
            mRoomId = msg;
            //推流回调
            mPusherPresenter.getPusherUrl(mUserId, mRoomId, mTitle, mCoverPicUrl, mNickName, mHeadPicUrl, mLocation, mIsRecord);
        } else if (Constants.NO_LOGIN_CACHE == code) {
            Log.e(TAG, "on join group failed " + msg);
        } else {
            Log.e(TAG, "on join group failed " + msg);
        }
    }

    //检测登陆状态
    private void checkLoginState(IMLogin.IMLoginListener imLoginListener) {
        IMLogin imLogin = IMLogin.getInstace();
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            imLogin.setIMLoginListener(imLoginListener);
            if (imLogin.checkCacheAndLogin()) {
                imLoginListener.onSuccess();
            } else {
                imLoginListener.onFailure(1, "login error");
            }
        } else {
            if (null != imLoginListener) {
                imLoginListener.onSuccess();
            }
        }
    }

    public void getPushUrl(final String userId, final String groupId, final String title, final String coverPic, final String location) {
        final CreateLiveRequest request = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, 0);
        Log.e("imLogin", "liveUrl:" + request.getUrl());
        AsyncHttp.instance().postJson(request, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.getStatus() == RequestComm.SUCCESS) {
                    CreateLiveResp resp = (CreateLiveResp) response.getData();
                    if (resp != null) {
                        mPushUrl = resp.getPushUrl();
                        if (!TextUtils.isEmpty(mPushUrl)) {
                            startPublish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {

            }
        });
    }

    public static void invoke(Activity activity, String roomTitle, String location, boolean isRecord, int bitrateType) {
        Intent intent = new Intent(activity, LivePublisherActivity.class);
        intent.putExtra(Constants.ROOM_TITLE,
                TextUtils.isEmpty(roomTitle) ? ImUserInfoMgr.getInstance().getNickname() : roomTitle);
        intent.putExtra(Constants.USER_ID, ACache.get(activity).getAsString("user_id"));
        intent.putExtra(Constants.USER_NICK, ACache.get(activity).getAsString("nickname"));
        intent.putExtra(Constants.USER_HEADPIC, ACache.get(activity).getAsString("head_pic_small"));
        intent.putExtra(Constants.COVER_PIC, ACache.get(activity).getAsString("head_pic"));
        intent.putExtra(Constants.USER_LOC, location);
        intent.putExtra(Constants.IS_RECORD, isRecord);
        intent.putExtra(Constants.BITRATE, bitrateType);
        activity.startActivity(intent);
    }

    //获取数据
    private void getDataFormIntent() {
        mUserId = getIntent().getStringExtra(Constants.USER_ID);
        mTitle = getIntent().getStringExtra(Constants.ROOM_TITLE);
        mCoverPicUrl = getIntent().getStringExtra(Constants.COVER_PIC);
        mHeadPicUrl = getIntent().getStringExtra(Constants.USER_HEADPIC);
        mNickName = getIntent().getStringExtra(Constants.USER_NICK);
        mLocation = getIntent().getStringExtra(Constants.USER_LOC);
        mIsRecord = getIntent().getBooleanExtra(Constants.IS_RECORD, false);
    }

    @Override
    protected void initView() {

        getDataFormIntent();

        mTXCloudVideoView = obtainView(R.id.video_view);
        btnSettingView = obtainView(R.id.btn_setting);

        mTXCloudVideoView.setVisibility(View.VISIBLE);
        mPusherPresenter = new PusherPresenter(this);

//        getPushUrl(ACache.get(this).getAsString("user_id"), "群主", "直播标题", "", "不显示地理位置");
//        mPusherPresenter.getPusherUrl(mUserId,"cniaow",mTitle,mCoverPicUrl,mLocation,false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_publisher;
    }


    private void startPublish() {
//        if (mTXLivePusher == null) {
//            mTXLivePusher = new TXLivePusher(this);
//            mTXPushConfig.setAutoAdjustBitrate(false);
//            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
//            mTXPushConfig.setVideoBitrate(1000);
//            mTXPushConfig.setHardwareAcceleration(true);
//            //切后台推流图片
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
//            mTXPushConfig.setPauseImg(bitmap);
//            mTXLivePusher.setConfig(mTXPushConfig);
//        }
//        mTXCloudVideoView.setVisibility(View.VISIBLE);
//        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
//        mTXLivePusher.setPushListener(null);
//        mTXLivePusher.startPusher(mPushUrl);

        mTXPushConfig.setAutoAdjustBitrate(false);
        mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
        mTXPushConfig.setVideoBitrate(1000);
        mTXPushConfig.setVideoFPS(20);

        //根据手机类型设置硬编码，判断是否支持手机硬编码
        Log.e(TAG,"MANUFACTURER:"+Build.MANUFACTURER+" MODEL:"+Build.MODEL);
        if (HWSupportList.isHWVideoEncodeSupport()){
            mTXPushConfig.setHardwareAcceleration(true);
            Log.e(TAG,"startPublish:手机设置硬编码成功！");
        }else {
            Log.e(TAG,"startPublish:手机不支持硬编码！");
        }

        //设置水印
        mTXPushConfig.setWatermark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
        //切后台推流图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
        mTXPushConfig.setPauseImg(bitmap);
        mPusherPresenter.startPusher(mTXCloudVideoView, mTXPushConfig, mPushUrl);
    }

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
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;

//            if (mTXLivePusher != null) {
//                mTXLivePusher.resumePusher();
//                mTXLivePusher.startCameraPreview(mTXCloudVideoView);
//                mTXLivePusher.resumeBGM();
//            }
            mPusherPresenter.resumePusher();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
//        if (mTXLivePusher != null) {
//            mTXLivePusher.pauseBGM();
//        }

        mPusherPresenter.pausePusher();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPasuing = true;
//        if (mTXLivePusher != null) {
//            mTXLivePusher.stopCameraPreview(false);
//            mTXLivePusher.pausePusher();
//        }
        mPusherPresenter.stopPusher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXCloudVideoView.onDestroy();
        mPusherPresenter.stopPusher();
//        TXRtmpApi.setRtmpDataListener(null);
    }

    private void stopPublish() {
        mPusherPresenter.stopPusher();
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onGetPushUrl(String pushUrl, int errorCode) {
        mPushUrl = pushUrl;

        if (errorCode == 0) {
            //start push
            startPublish();
        } else {
            Log.e(TAG, "push url is empty");
            //push url error
            finish();
        }
    }

    @Override
    public FragmentManager getFragmentMgr() {
        return getFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //直播退出
            case R.id.btn_close:
                stopPublish();
                finish();
                break;
            //直播设置，跳出popwindow
            case R.id.btn_setting:
                mPusherPresenter.showSettingPopupWindow(btnSettingView, mSettingLocation);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mSettingLocation[0] == 0 && mSettingLocation[1] == 0) {
            btnSettingView.getLocationOnScreen(mSettingLocation);
        }
    }
}
