package com.dali.admin.livestreaming.fragment;


import android.view.View;

import com.dali.admin.livestreaming.R;
import com.dali.admin.livestreaming.base.BaseFragment;
import com.dali.admin.livestreaming.mvp.presenter.LiveMainPresenter;

/**
 * 直播首页
 * Created by dali on 2017/4/10.
 */
public class LiveMainFragment extends BaseFragment implements View.OnClickListener {

    private LiveMainPresenter mLiveMainPresenter;


    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        obtainView(R.id.iv_search).setOnClickListener(this);
        obtainView(R.id.iv_message).setOnClickListener(this);
    }

    @Override
    protected void initView(View rootView) {
        mLiveMainPresenter = new LiveMainPresenter(mContext);
        mLiveMainPresenter.initViewPager(rootView);
        mLiveMainPresenter.initViewPagerData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_main;
    }


    @Override
    public void onClick(View v) {

    }
}
