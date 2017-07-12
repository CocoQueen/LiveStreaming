package com.dali.admin.livestreaming.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.http.AsyncHttp;
import com.dali.admin.livestreaming.http.request.CreateLiveRequest;
import com.dali.admin.livestreaming.http.request.RequestComm;
import com.dali.admin.livestreaming.http.response.CreateLiveResp;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.mvp.presenter.Ipresenter.IPusherPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IPusherView;
import com.dali.admin.livestreaming.ui.customviews.BeautyDialogFragment;
import com.dali.admin.livestreaming.ui.customviews.FilterDialogFragment;
import com.dali.admin.livestreaming.utils.OtherUtils;
import com.dali.admin.livestreaming.utils.UIUtils;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 推流
 * Created by dali on 2017/5/7.
 */

public class PusherPresenter extends IPusherPresenter implements BeautyDialogFragment.SeekBarCallback,FilterDialogFragment.FilterCallback{

    private IPusherView mPusherView;
    private TXLivePusher mTXLivePusher;
    private TXCloudVideoView mTXCloudVideoView;
    private String mPushUrl;

    private PopupWindow mPopupWindow;
    private boolean mFlashOn = false;
    private BeautyDialogFragment mBeautyDialogFragment;
    private FilterDialogFragment mFilterDialogFragment;

    private int mLocX;
    private int mLocY;
    private boolean isBeauty = false;
    private int mBeautyLevel;
    private int mWhiteLevel;

    public PusherPresenter(IPusherView pusherView) {
        super(pusherView);
        this.mPusherView = pusherView;
        mBeautyDialogFragment = new BeautyDialogFragment();
        mBeautyDialogFragment.setSeekBarListener(this);

        mFilterDialogFragment = new FilterDialogFragment();
        mFilterDialogFragment.setFilterCallback(this);
    }


    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    public void getPushUrl(final String userId, final String groupId, final String title, final String coverPic, final String location, boolean isRecord) {
        final CreateLiveRequest request = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, isRecord ? 1 : 0);
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
                        if (!TextUtils.isEmpty(resp.getPushUrl())) {
                            System.out.println("LivePublishActivity:" + resp.getPushUrl());
                            mPusherView.onGetPushUrl(resp.getPushUrl(), 0);
                        } else {
                            mPusherView.onGetPushUrl(null, 1);
                        }
                    } else {
                        mPusherView.onGetPushUrl(null, 1);
                    }
                } else {
                    mPusherView.showMsg(response.getMsg());
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mPusherView.onGetPushUrl(null, 1);
            }
        });
    }

    @Override
    public void getPusherUrl(String userId, String groupId, String title, String coverPic, String nickName, String headPic, String location, boolean isRecord) {
        Log.e("getPusherUrl",userId+","+groupId+","+title+","+coverPic+","+location+","+isRecord);
        final CreateLiveRequest request = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, isRecord ? 1 : 0);
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
                            mPusherView.onGetPushUrl(mPushUrl, 0);
                        } else {
                            mPusherView.onGetPushUrl(mPushUrl, 1);
                        }
                    } else {
                        mPusherView.onGetPushUrl(mPushUrl, 1);
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mPusherView.onGetPushUrl(mPushUrl, 1);
            }
        });
    }

    @Override
    public void startPusher(TXCloudVideoView videoView, TXLivePushConfig pusherConfig, String pushUrl) {
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(mPusherView.getContext());
            mTXLivePusher.setConfig(pusherConfig);
        }
        mTXCloudVideoView = videoView;
        mPushUrl = pushUrl;
        mTXCloudVideoView.setVisibility(View.VISIBLE);
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.startPusher(pushUrl);
    }

    @Override
    public void stopPusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
    }

    @Override
    public void pausePusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumePusher() {
        if (mTXLivePusher != null) {
            mTXLivePusher.resumePusher();
            mTXLivePusher.startCameraPreview(mTXCloudVideoView);
            mTXLivePusher.resumeBGM();
        }
    }

    //设置PopupWindow
    @Override
    public void showSettingPopupWindow(final View anchorView, int[] location) {
        if(mPopupWindow==null){

            View contentView = LayoutInflater.from(mPusherView.getContext()).inflate(R.layout.live_host_setting,null);
            contentView.findViewById(R.id.ll_live_setting_flash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTXLivePusher.turnOnFlashLight(!mFlashOn);
                    mFlashOn = !mFlashOn;
                    mPopupWindow.dismiss();
                }
            });

            contentView.findViewById(R.id.ll_live_setting_changecamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mTXLivePusher.switchCamera();
                }
            });

            contentView.findViewById(R.id.ll_live_setting_beauty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    //beautyLevel:0-9,默认为0，不开启美颜
                    //whiteLevel 0-3,默认为0，不开启美白

//                    if (isBeauty) {
//                        mTXLivePusher.setBeautyFilter(0, 0);
//                        isBeauty = !isBeauty;
//                    } else {
//                        if (!mTXLivePusher.setBeautyFilter(7, 3)) {
//                            Toast.makeText(mPusherView.getContext(), R.string.beauty_disenable, Toast.LENGTH_SHORT);
//                        } else {
//                            isBeauty = !isBeauty;
//                        }
//                    }
                    mBeautyDialogFragment.show(mPusherView.getFragmentMgr(), "");

                }
            });

            contentView.findViewById(R.id.ll_live_setting_filter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    mFilterDialogFragment.show(mPusherView.getFragmentMgr(), "");
                }
            });

            mPopupWindow = new PopupWindow(contentView, UIUtils.formatDipToPx(mPusherView.getContext(),100), UIUtils.formatDipToPx(mPusherView.getContext(),170));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //PopupWindow显示位置计算
            mLocX = location[0] - (mPopupWindow.getWidth() - anchorView.getWidth()) / 2;
            mLocY = location[1] - (mPopupWindow.getHeight());
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    anchorView.setBackgroundResource(R.drawable.icon_setting_up);
                }
            });
        }
        mPopupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, mLocX, mLocY);
    }

    @Override
    public void onProgressChanged(int progress, int state) {
        switch (state){
            case BeautyDialogFragment.STATE_BEAUTY:
                mBeautyLevel = OtherUtils.filtNumber(9, 100, progress);
                break;
            case BeautyDialogFragment.STATE_WHITE:
                mWhiteLevel = OtherUtils.filtNumber(3, 100, progress);
                break;
        }
        mTXLivePusher.setBeautyFilter(mBeautyLevel,mWhiteLevel);
    }

    @Override
    public void setFilter(Bitmap filterBitmap) {
        mTXLivePusher.setFilter(filterBitmap);
    }
}
