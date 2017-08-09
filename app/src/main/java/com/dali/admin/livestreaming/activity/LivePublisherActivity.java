package com.dali.admin.livestreaming.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.adapter.UserAvatarListAdapter;
import com.dali.admin.livestreaming.logic.ImUserInfoMgr;
import com.dali.admin.livestreaming.model.GiftWithUerInfo;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.dali.admin.livestreaming.mvp.presenter.IMChatPresenter;
import com.dali.admin.livestreaming.mvp.presenter.PusherPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IIMChatView;
import com.dali.admin.livestreaming.mvp.view.Iview.IPusherView;
import com.dali.admin.livestreaming.ui.customviews.EndDetailFragment;
import com.dali.admin.livestreaming.ui.customviews.InputTextMsgDialog;
import com.dali.admin.livestreaming.utils.AsimpleCache.ACache;
import com.dali.admin.livestreaming.utils.Constants;
import com.dali.admin.livestreaming.utils.HWSupportList;
import com.dali.admin.livestreaming.utils.OtherUtils;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LivePublisherActivity extends IMBaseActivity implements IPusherView, View.OnClickListener, IIMChatView, InputTextMsgDialog.OnTextSendListener {

    private static final String TAG = LivePublisherActivity.class.getSimpleName();
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

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
    private String mRoomId;

    private PusherPresenter mPusherPresenter;
    private IMChatPresenter mIMChatPresenter;

    //主播相关信息，头像、观众数
    private ImageView ivHeadIcon;//主播头像
    private ImageView ivRecordBall;//小红点
    private TextView tvMemberCount;//观众人数
    private int mMemberCount = 0; //实时人数
    private int mTotalCount = 0; //总观众人数
    private int mPraiseCount = 0; //点赞人数
    //播放信息：时间、红点
    private long mSecond = 0;//秒数
    private TextView tvBroadcastTime;//直播时间
    private Timer mBroadcastTimer;//定时器
    private BroadcastTimerTask mBroadcastTimerTask;
    private ObjectAnimator mObjAnim;//小红点动画（渐隐渐现）

    //文本对话框
    private InputTextMsgDialog mInputTextMsgDialog;

    //观众列表
    private RecyclerView mUserAvatarList;
    private UserAvatarListAdapter mAvatarListAdapter;

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
        mIMChatPresenter.createGroup();
    }


    @Override
    public void onJoinGroupResult(int code, String msg) {
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

    //加入群组并进行推流
    @Override
    public void onGroupDeleteResult() {

    }

    @Override
    public void handleTextMsg(SimpleUserInfo userInfo, String text) {

    }

    @Override
    public void handlePraiseMsg(SimpleUserInfo userInfo) {

    }

    @Override
    public void handlePraiseFirstMsg(SimpleUserInfo userInfo) {

    }

    @Override
    public void handleExitLiveMsg(SimpleUserInfo userInfo) {
        Log.i(TAG, "handleExitLiveMsg: ");
        //更新观众列表，观众退出显示
        if (mMemberCount > 0)
            mMemberCount--;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));

    }

    @Override
    public void handleGift(GiftWithUerInfo giftWithUerInfo) {

    }

    @Override
    public void handleEnterLiveMsg(SimpleUserInfo userInfo) {
        Log.i(TAG, "handleEnterLiveMsg: ");
        //更新观众列表，观众进入显示
        mMemberCount++;
        mTotalCount++;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));
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

        //主播信息
        tvBroadcastTime = obtainView(R.id.tv_broadcasting_time);
        tvBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
        ivRecordBall = obtainView(R.id.iv_record_ball);
        ivHeadIcon = obtainView(R.id.iv_head_icon);
        ivHeadIcon.setOnClickListener(this);
        OtherUtils.showPicWithUrl(this, ivHeadIcon, ACache.get(this).getAsString("head_pic_small"), R.drawable.default_head);
        tvMemberCount = obtainView(R.id.tv_member_counts);
        tvMemberCount.setText("0");

        mTXCloudVideoView.setVisibility(View.VISIBLE);
        mPusherPresenter = new PusherPresenter(this);
        mIMChatPresenter = new IMChatPresenter(this);

        recordAnmination();

        //文本发送对话框
        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
    }

    private void recordAnmination() {
        mObjAnim = ObjectAnimator.ofFloat(ivRecordBall, "alpha", 1.0f, 0f, 1.0f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_publisher;
    }


    private void startPublish() {
        mTXPushConfig.setAutoAdjustBitrate(false);
        mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
        mTXPushConfig.setVideoBitrate(1000);
        mTXPushConfig.setVideoFPS(20);

        //根据手机类型设置硬编码，判断是否支持手机硬编码
        Log.e(TAG, "MANUFACTURER:" + Build.MANUFACTURER + " MODEL:" + Build.MODEL);
        if (HWSupportList.isHWVideoEncodeSupport()) {
            mTXPushConfig.setHardwareAcceleration(true);
            Log.e(TAG, "startPublish:手机设置硬编码成功！");
        } else {
            Log.e(TAG, "startPublish:手机不支持硬编码！");
        }

        //设置水印
        mTXPushConfig.setWatermark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
        //切后台推流图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
        mTXPushConfig.setPauseImg(bitmap);
        mPusherPresenter.startPusher(mTXCloudVideoView, mTXPushConfig, mPushUrl);

        if (mBroadcastTimer == null){
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask,1000,1000);
        }
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
            mPusherPresenter.resumePusher();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
        mPusherPresenter.pausePusher();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPasuing = true;
        mPusherPresenter.stopPusher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXCloudVideoView.onDestroy();
//        TXRtmpApi.setRtmpDataListener(null);

        stopPublish();

    }

    private void stopPublish() {
        stopRecordAnimation();
        if (mPusherPresenter != null) {
//            mPusherPresenter.changeLiveStatus(mUserId,PusherPresenter.LIVE_STATUS_OFFLINE);
            mPusherPresenter.stopPusher();
        }
        if (mIMChatPresenter != null) {
            mIMChatPresenter.deleteGroup();
        }
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
    }

    private void stopRecordAnimation() {
        if (mObjAnim != null) {
            mObjAnim.cancel();
            mObjAnim = null;
        }
        if (mBroadcastTimerTask != null) {
            mBroadcastTimerTask.cancel();
            mBroadcastTimer.cancel();
            mBroadcastTimerTask = null;
            mBroadcastTimer = null;
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
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;

        }
    }

    //显示评论输入框
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = display.getWidth();//设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mSettingLocation[0] == 0 && mSettingLocation[1] == 0) {
            btnSettingView.getLocationOnScreen(mSettingLocation);
        }
    }

    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        mIMChatPresenter.sendTextMsg(msg);
        Log.i(TAG,"msg="+msg);
    }

    private class BroadcastTimerTask extends TimerTask {
        @Override
        public void run() {
            mSecond++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvBroadcastTime.setText(OtherUtils.formattedTime(mSecond));
                }
            });
        }
    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(String msg, Boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    EndDetailFragment.invoke(getFragmentManager(), mSecond, mPraiseCount, mTotalCount);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    finish();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
