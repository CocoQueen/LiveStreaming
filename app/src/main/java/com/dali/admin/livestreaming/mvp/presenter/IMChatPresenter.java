package com.dali.admin.livestreaming.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.dali.admin.livestreaming.logic.IMLogin;
import com.dali.admin.livestreaming.model.GiftWithUerInfo;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.dali.admin.livestreaming.mvp.presenter.Ipresenter.IIMChatPresenter;
import com.dali.admin.livestreaming.mvp.view.Iview.IIMChatView;
import com.dali.admin.livestreaming.utils.AsimpleCache.ACache;
import com.dali.admin.livestreaming.utils.Constants;
import com.google.gson.Gson;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * IM聊天管理
 * Created by dali on 2017/7/15.
 */

public class IMChatPresenter extends IIMChatPresenter implements TIMMessageListener {

    public static final String TAG = IMChatPresenter.class.getSimpleName();
    private IIMChatView mIMChatView;
    private TIMConversation mGroupConversation;
    private String mRoomId;
    private Gson mGson = new Gson();

    public IMChatPresenter(IIMChatView baseView) {
        super(baseView);
        this.mIMChatView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {//群组解散或退出群组时移除监听
        TIMManager.getInstance().removeMessageListener(this);
        mGroupConversation = null;
    }

    @Override
    public void createGroup() {
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
                        //加入群组并进行推流
                        mRoomId = roomId;
                        mIMChatView.onJoinGroupResult(0, mRoomId);
                        //创建组成功的时候加入会话
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mRoomId);
                        TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                IMLogin.getInstace().removeIMLoginListener();
            }
        });
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

    @Override
    public void deleteGroup() {
        sendMessage(Constants.AVIMCMD_LIVE_END, "");
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("delete group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, String.format("delete group success"));
                finish();
            }
        });
    }

    private void sendMessage(int userAction, String msg) {
        Log.i(TAG, "sendMessage: userAction = " + userAction + " msg = " + msg);
        JSONObject json = new JSONObject();
        try {
            json.put("userAction", userAction);
            json.put("userId", ACache.get(mIMChatView.getContext()).getAsString("user_id"));
            json.put("nickname", ACache.get(mIMChatView.getContext()).getAsString("nickname"));
            json.put("headPic", ACache.get(mIMChatView.getContext()).getAsString("head_pic"));
            if (msg != null) {
                json.put("params", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonMsg = json.toString();
        TIMMessage message = new TIMMessage();
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(jsonMsg);
        if (message.addElement(textElem) != 0) {
            return;
        }
        sendTIMMessage(message, new TIMValueCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("send message onError: code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "send message onSuccess: ");
            }
        });
    }

    private void sendTIMMessage(TIMMessage message, TIMValueCallBack callBack) {
        if (mGroupConversation != null) {
            mGroupConversation.sendMessage(message, callBack);
        }
    }

    @Override
    public void joinGroup(final String roomId) {
        mRoomId = roomId;
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("join group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, String.format("join group success"));
                //加入组的时候创建会话
                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
                mIMChatView.onJoinGroupResult(0, mRoomId);
                sendMessage(Constants.AVIMCMD_ENTER_LIVE, "");
            }
        });
    }

    @Override
    public void quitGroup(String roomId) {
        sendMessage(Constants.AVIMCMD_EXIT_LIVE, "");
        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("quit group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, String.format("quit group success"));
                finish();
            }
        });
    }

    @Override
    public void sendTextMsg(final String msg) {
        TIMMessage message = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(msg);
        //将elem添加到消息
        if (message.addElement(elem) != 0) {
            return;
        }
        mGroupConversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, "onError: code=" + code + " msg=" + msg);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.i(TAG, "onSuccess: msg=" + msg);
            }
        });
    }

    @Override//加入群组成功之后消息监听回调
    public boolean onNewMessages(List<TIMMessage> list) {
        parserMessage(list);
        return false;
    }

    //加入群组成功之后消息监听回调
    private void parserMessage(List<TIMMessage> list) {
        for (TIMMessage msg : list) {
            TIMElem elem = msg.getElement(0);
            if (elem.getType() == TIMElemType.Text) {//判断当前内容是否是文本
                TIMTextElem text = (TIMTextElem) elem;
                handleCustomTextMsg(text.getText());
                Log.i(TAG, "onNewMessages: msg = " + text.getText());
            } else if (elem.getType() == TIMElemType.GroupSystem) {
                TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem;
                Log.i(TAG, "parserMessage: group msg");
                if (systemElem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE) {
                    Log.i(TAG, "parserMessage: delete group msg");
                    //观众 退出直播观看
                    mIMChatView.onGroupDeleteResult();
                }
            }
        }
    }

    private void handleCustomTextMsg(String jsonMsg) {
        JSONTokener jsonTokener = new JSONTokener(jsonMsg);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            int userAction = jsonObject.getInt("userAction");
            String nickname = null;
            String userId = null;
            String headPic = null;
            String msg = null;
            if (jsonObject.has("userId")) {
                userId = jsonObject.getString("userId");
            }
            if (jsonObject.has("nickname")) {
                nickname = jsonObject.getString("nickname");
            }
            if (jsonObject.has("headPic")) {
                headPic = jsonObject.getString("headPic");
            }
            if (jsonObject.has("msg")) {
                msg = jsonObject.getString("msg");
            }
            switch (userAction) {
                case Constants.AVIMCMD_TEXT_TYPE:
                    mIMChatView.handleTextMsg(new SimpleUserInfo(userId, nickname, headPic), msg);
                    break;
                case Constants.AVIMCMD_PRAISE_FIRST:
                    mIMChatView.handlePraiseFirstMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_PRAISE:
                    mIMChatView.handlePraiseMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_ENTER_LIVE:
                    mIMChatView.handleEnterLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_EXIT_LIVE:
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_HOST_LEAVE:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_LEAVE");
                    break;
                case Constants.AVIMCMD_HOST_BACK:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_BACK");
                    break;
                case Constants.AVIMCMD_LIVE_END:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_LIVE_END");
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_GIFT:
                    GiftWithUerInfo giftWithUerInfo = mGson.fromJson(jsonObject.getString("params"), GiftWithUerInfo.class);
                    mIMChatView.handleGift(giftWithUerInfo);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
