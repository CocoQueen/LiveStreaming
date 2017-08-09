package com.dali.admin.livestreaming.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dali.admin.livestreaming.LiveApplication;
import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.model.GiftWithUerInfo;
import com.dali.admin.livestreaming.model.LiveInfo;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.dali.admin.livestreaming.mvp.presenter.IMChatPresenter;
import com.dali.admin.livestreaming.mvp.presenter.LivePlayerPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IIMChatView;
import com.dali.admin.livestreaming.mvp.view.Iview.ILivePlayerView;
import com.dali.admin.livestreaming.ui.customviews.EndDetailFragment;
import com.dali.admin.livestreaming.ui.customviews.InputTextMsgDialog;
import com.dali.admin.livestreaming.utils.AsimpleCache.ACache;
import com.dali.admin.livestreaming.utils.Constants;
import com.dali.admin.livestreaming.utils.OtherUtils;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 直播播放
 */
public class LivePlayerActivity extends IMBaseActivity implements ILivePlayerView, IIMChatView, View.OnClickListener, InputTextMsgDialog.OnTextSendListener {

    private LivePlayerPresenter mLivePlayerPresenter;
    private IMChatPresenter mIMChatPresenter;

    private String mPlayUrl;
    private String TAG = LivePlayerActivity.class.getSimpleName();
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayerConfig;
    private LiveInfo mLiveInfo;
    private int mJoinCount = 0;

    //主播信息
    private ImageView ivHeadIcon;
    private ImageView ivRecordBall;
    private TextView tvPuserName;
    private TextView tvMemberCount;
    private int mMemberCount = 0; //实时人数
    private int mTotalCount = 0; //总观总人数
    private int mPraiseCount = 0;
    private long mLiveStartTime = 0;

    //背景
    private ImageView ivLiveBg;
    private boolean mOfficialMsgSended = false;
    private InputTextMsgDialog mInputTextMsgDialog;

    @Override
    protected void setActionBar() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        String headPic = ACache.get(this).getAsString("head_pic");
        if (!TextUtils.isEmpty(headPic)) {
            OtherUtils.blurBgPic(this, ivLiveBg, ACache.get(this).getAsString("head_pic"), R.drawable.bg);
        }
    }

    @Override
    protected void initView() {
        //获取Intent传递到数据
        getDataFormIntent();
        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView = obtainView(R.id.video_view);
        mTXPlayerConfig = new TXLivePlayConfig();
        mLivePlayerPresenter = new LivePlayerPresenter(this);
        mIMChatPresenter = new IMChatPresenter(this);

        //主播信息
        tvPuserName = obtainView(R.id.tv_broadcasting_time);
        tvPuserName.setText(OtherUtils.getLimitString(mLiveInfo.getUserInfo().getNickname(), 10));
        ivRecordBall = obtainView(R.id.iv_record_ball);
        ivRecordBall.setVisibility(View.GONE);
        ivHeadIcon = obtainView(R.id.iv_head_icon);
        ivHeadIcon.setOnClickListener(this);
        OtherUtils.showPicWithUrl(this, ivHeadIcon, mLiveInfo.getUserInfo().getNickname(), R.drawable.default_head);

        ivLiveBg = obtainView(R.id.iv_live_bg);
        tvMemberCount = obtainView(R.id.tv_member_counts);

        mMemberCount++;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));

        //配置相关
        mTXPlayerConfig.setConnectRetryCount(3);//重试次数
        mTXPlayerConfig.setConnectRetryInterval(3);//重试间隔
        //极速模式
        mTXPlayerConfig.setAutoAdjustCacheTime(true);
        mTXPlayerConfig.setMinAutoAdjustCacheTime(1);
        mTXPlayerConfig.setMaxAutoAdjustCacheTime(1);

        mLivePlayerPresenter.initPlayerView(mTXCloudVideoView, mTXPlayerConfig);

        if (mPlayUrl != null) {
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        } else {
            showToast("player url is null");
        }

        //进入群组
        mIMChatPresenter.joinGroup(mLiveInfo.getGroupId());

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    public static void invoke(Activity activity, LiveInfo info) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.LIVE_INFO, info);
        activity.startActivityForResult(intent, Constants.LIVE_PLAYER_REQUEST_CODE);
    }

    private void getDataFormIntent() {
        mLiveInfo = (LiveInfo) getIntent().getSerializableExtra(Constants.LIVE_INFO);
        mPlayUrl = mLiveInfo.getPlayUrl();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停
        mLivePlayerPresenter.playerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //继续
        mLivePlayerPresenter.playerResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivePlayerPresenter.stopPlay(true);// true代表清除最后一帧画面
        mTXCloudVideoView.onDestroy();
        mIMChatPresenter.quitGroup(mLiveInfo.getGroupId());
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
    public void onPlayEvent(int event, Bundle bundle) {
        //播放相关事件
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            mLiveStartTime = System.currentTimeMillis();
            ivLiveBg.setVisibility(View.GONE);
            //可以上传播放状态
            if (!mOfficialMsgSended) {
//                refreshMsg("", getString(R.string.live_system_name), getString(R.string.live_system_notify), Constants.AVIMCMD_TEXT_TYPE);
                mOfficialMsgSended = true;
            }
        }

        if (event < 0) {
            if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                showComfirmDialog("请检查网络", true);
            }
        }
        if (event == TXLiveConstants.PLAY_WARNING_HW_ACCELERATION_FAIL) {
            mLivePlayerPresenter.enableHardwareDecode(false);
            stopPlay();
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        }

        Log.i(TAG, "onPlayEvent: event =" + event + " event description = " + bundle.getString(TXLiveConstants.EVT_DESCRIPTION));

    }

    @Override
    public void onNetStatus(Bundle bundle) {
        //播放信息及状态
        Log.i(TAG, "net status, " +
                "CPU:" + bundle.getString(TXLiveConstants.NET_STATUS_CPU_USAGE) +
                ", RES:" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) +
                "*" + bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT) +
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
        if (retCode == 0 && totalCount > 0) {
            mTotalCount += totalCount;
            mMemberCount += totalCount;
            tvMemberCount.setText("" + mMemberCount);
//            if (membersList != null) {
//                for (SimpleUserInfo userInfo : membersList) {
//                    mAvatarListAdapter.addItem(userInfo);
//                }
//         }
        } else {
            Log.e(TAG, "onGroupMembersResult failed");
        }
    }

    @Override
    public void onJoinGroupResult(int code, String msg) {
        if (code != 0) {
            mLivePlayerPresenter.enterGroup(ACache.get(this).getAsString("user_id"),
                    mLiveInfo.getLiveId(), mLiveInfo.getUserInfo().getUserId(), mLiveInfo.getGroupId());
            if (Constants.ERROR_GROUP_NOT_EXIT == code) {
                showErrorAndQuit(Constants.ERROR_MSG_GROUP_NOT_EXIT);
            } else if (Constants.ERROR_QALSDK_NOT_INIT == code) {
                mJoinCount++;
                ((LiveApplication) getApplication()).initSDK();
                if (mJoinCount > 1) {
                    showErrorAndQuit(Constants.ERROR_MSG_JOIN_GROUP_FAILED);
                } else {
                    mIMChatPresenter.joinGroup(mLiveInfo.getGroupId());
                }
            } else {
                showErrorAndQuit(Constants.ERROR_MSG_JOIN_GROUP_FAILED + code);
            }
        } else {
            // 进入房间成功  获取 成员数据
            mLivePlayerPresenter.groupMember(ACache.get(this).getAsString("user_id"), mLiveInfo.getLiveId(),
                    mLiveInfo.getUserInfo().getUserId(), mLiveInfo.getGroupId(), 1, 20);
        }
    }

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
    public void handleEnterLiveMsg(SimpleUserInfo simpleUserInfo) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                showComfirmDialog(getString(R.string.msg_stop_watch), false);
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
                    stopPlay();
                    showEndDetail();
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
            stopPlay();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEndDetail() {
        long second = 0;
        if (mLiveStartTime != 0) {
            second = (System.currentTimeMillis() - mLiveStartTime) / 1000;
        }
        EndDetailFragment.invoke(getFragmentManager(), second, mPraiseCount, mTotalCount);
    }

    private void stopPlay() {
        mLivePlayerPresenter.quitGroup(ACache.get(this).getAsString("user_id"),
                mLiveInfo.getLiveId(), mLiveInfo.getUserInfo().getUserId(), mLiveInfo.getGroupId());
        mLivePlayerPresenter.stopPlay(true);
        mTXCloudVideoView.onDestroy();
        mIMChatPresenter.quitGroup(mLiveInfo.getGroupId());
        ACache.get(this).put(mLiveInfo.getLiveId() + "_first_praise", "0");
    }

    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        mIMChatPresenter.sendTextMsg(msg);
        Log.i(TAG,"msg="+msg);
//        refreshMsg(ACache.get(this).getAsString("user_id"), "我:", msg, Constants.AVIMCMD_TEXT_TYPE);
    }
}
