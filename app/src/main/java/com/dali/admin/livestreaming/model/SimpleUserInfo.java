package com.dali.admin.livestreaming.model;

/**
 * Created by dali on 2017/7/11.
 */

public class SimpleUserInfo {
    private String userId;
    private String nickname;
    private String headPic;

    public SimpleUserInfo(String userId, String nickname, String headpic) {
        this.userId = userId;
        this.nickname = nickname;
        this.headPic = headpic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
}
