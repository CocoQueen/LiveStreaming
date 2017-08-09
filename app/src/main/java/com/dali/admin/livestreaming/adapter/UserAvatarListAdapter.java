package com.dali.admin.livestreaming.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.base.RecyclerViewAdapter;
import com.dali.admin.livestreaming.base.RecyclerViewHolder;
import com.dali.admin.livestreaming.model.SimpleUserInfo;
import com.dali.admin.livestreaming.utils.OtherUtils;

import java.util.List;

/**
 * Created by dali on 2017/8/8.
 */

public class UserAvatarListAdapter extends RecyclerViewAdapter<SimpleUserInfo> {

    //主播id
    private String mPusherId;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 50;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClickListener(SimpleUserInfo userInfo);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mItemClickListener = onItemClickListener;
    }

    public UserAvatarListAdapter(Context context, String pusherId, List<SimpleUserInfo> datas,OnItemClickListener listener) {
        super(context, R.layout.item_user_avatar, datas);
        this.mPusherId = pusherId;
        this.mItemClickListener = listener;
    }


    @Override
    protected void bindData(RecyclerViewHolder holder, final SimpleUserInfo userInfo, final int position) {
        //主播头像（圆角显示图片）
        OtherUtils.showPicWithUrl(mContext, (ImageView) holder.getView(R.id.avatar), userInfo.getHeadPic(), R.drawable.default_head);

        holder.getView(R.id.avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClickListener(userInfo);
                Log.e("UserAvatarListAdapter","position:"+position+"");
            }
        });
    }

    /**
     * 添加用户信息
     *
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(SimpleUserInfo userInfo) {

        //去除主播头像
        if (userInfo.getUserId().equals(mPusherId))
            return false;

        //去重操作
        for (SimpleUserInfo tcSimpleUserInfo : mDatas) {
            if (tcSimpleUserInfo.getUserId().equals(userInfo.getUserId()))
                return false;
        }

        //始终显示新加入item为第一位
        mDatas.add(0, userInfo);
        //超出时删除末尾项
        if (mDatas.size() > TOP_STORGE_MEMBER)
            mDatas.remove(TOP_STORGE_MEMBER);
        notifyItemInserted(0);
        return true;
    }

    public void removeItem(String userId) {
        SimpleUserInfo tempUserInfo = null;

        for (SimpleUserInfo userInfo : mDatas)
            if (userInfo.getUserId().equals(userId))
                tempUserInfo = userInfo;


        if (null != tempUserInfo) {
            mDatas.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }
}
