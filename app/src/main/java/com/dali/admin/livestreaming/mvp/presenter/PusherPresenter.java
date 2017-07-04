package com.dali.admin.livestreaming.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.dali.admin.livestreaming.http.AsyncHttp;
import com.dali.admin.livestreaming.http.request.CreateLiveRequest;
import com.dali.admin.livestreaming.http.request.RequestComm;
import com.dali.admin.livestreaming.http.response.CreateLiveResp;
import com.dali.admin.livestreaming.http.response.Response;
import com.dali.admin.livestreaming.mvp.presenter.Ipresenter.IPusherPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IPusherView;

/**
 * 推流
 * Created by dali on 2017/5/7.
 */

public class PusherPresenter extends IPusherPresenter {

    private IPusherView mPusherView;

    public PusherPresenter(IPusherView pusherView) {
        super(pusherView);
        this.mPusherView = pusherView;
    }


    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    public void getPushUrl(final String userId, final String groupId, final String title, final String coverPic, final String location, boolean isRecord) {
        final CreateLiveRequest request = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, isRecord?1:0);
        Log.e("imLogin", "liveUrl:" + request.getUrl());
        AsyncHttp.instance().postJson(request, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.getStatus() == RequestComm.SUCCESS){
                    CreateLiveResp resp = (CreateLiveResp) response.getData();
                    if (resp != null){
                        if (!TextUtils.isEmpty(resp.getPushUrl())){
                            System.out.println("LivePublishActivity:"+resp.getPushUrl());
                            mPusherView.onGetPushUrl(resp.getPushUrl(),0);
                        }else {
                            mPusherView.onGetPushUrl(null,1);
                        }
                    }else {
                        mPusherView.onGetPushUrl(null,1);
                    }
                }else {
                    mPusherView.showMsg(response.getMsg());
                }
            }
            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mPusherView.onGetPushUrl(null,1);
            }
        });
    }

    @Override
    public void getPusherUrl(final String userId, final String groupId, final String title, final String coverPic, final String location,boolean isRecord) {
        final CreateLiveRequest request = new CreateLiveRequest(RequestComm.createLive, userId, groupId, title, coverPic, location, isRecord?1:0);
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
                            mPusherView.onGetPushUrl(resp.getPushUrl(),0);
                        }else {
                            mPusherView.onGetPushUrl(resp.getPushUrl(),1);
                        }
                    }else {
                        mPusherView.onGetPushUrl(resp.getPushUrl(),1);
                    }
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {

            }
        });
    }

}
