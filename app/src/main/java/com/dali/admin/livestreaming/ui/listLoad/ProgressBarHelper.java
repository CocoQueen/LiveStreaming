package com.dali.admin.livestreaming.ui.listLoad;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dali.admin.livestreaming.R;

/**
 * 列表加载动画
 * Created by dali on 2017/5/11.
 */

public class ProgressBarHelper {
    private ImageView mAnimImg;
    //网络错误
    private int mNetErrorTipImageResId = R.drawable.net_error_tip;
    //没有数据
    private int mNoDataTipImageResId = R.drawable.no_data_tip;
    //数据加载
    public static final int STATE_LOADING = 0;
    //数据错误
    public static final int STATE_ERROR = 1;
    //数据为空
    public static final int STATE_EMPTY = 2;
    //加载完成
    public static final int STATE_FINISH = 3;

    public static int state = STATE_FINISH;

    public View loading;
    private TextView mTvTipText;
    private ProgressBar mProgressBar;
    private ProgressBarClickListener mProgressBarClickListener;
    private boolean isLoading = false;
    private Activity mContext;


    public interface ProgressBarClickListener {
        void clickRefresh();
    }

    public void setProgressBarClickListener(ProgressBarClickListener progressBarClickListener) {
        mProgressBarClickListener = progressBarClickListener;
    }

    public ProgressBarHelper(View view, Activity context) {
        mContext = context;
        if (view != null) {
            loading = view;
        } else {
            if (context == null)
                return;
            loading = context.findViewById(R.id.ll_data_loading);
        }
        if (loading == null)
            return;

        mTvTipText = (TextView) loading.findViewById(R.id.loading_tip_txt);
        mProgressBar = (ProgressBar) loading.findViewById(R.id.loading_progress_bar);
        mAnimImg = (ImageView) loading.findViewById(R.id.loading_image);
        //设置加载进度
        showLoading();
    }

    private long getTotalDuration(AnimationDrawable animationDrawable) {
        int duration = 0;
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
            duration += animationDrawable.getDuration(i);
        }
        return duration;
    }

    //显示网络错误状态
    public void showNetError() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
                if (mTvTipText != null) {
                    setFailue();
                }

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                    if (loading != null) {
                        loading.setVisibility(View.VISIBLE);
                        loading.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mProgressBarClickListener != null) {
                                    if (mProgressBarClickListener != null && !mProgressBar.isShown() && mProgressBar.getVisibility() == View.GONE) {
                                        mProgressBarClickListener.clickRefresh();
                                        showLoading();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //显示无数据状态
    public void showNoData() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
                if (mTvTipText != null) {
                    setNoData();
                }

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }

                if (loading != null) {
                    loading.setVisibility(View.VISIBLE);
                    loading.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mProgressBarClickListener != null && !mProgressBar.isShown() && mProgressBar.getVisibility() == View.GONE) {
                                mProgressBarClickListener.clickRefresh();
                                showLoading();
                            }
                        }
                    });
                }
            }
        });
    }

    //显示加载完成状态
    public void goneLoading() {
        if (loading != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isLoading = false;
                    if (loading != null)
                        loading.setVisibility(View.GONE);
                    state = STATE_FINISH;
                }
            });
        }
    }

    //设置加载动画
    public void showLoading() {
        isLoading = true;
        if (mTvTipText != null)
            mTvTipText.setText("");

        if (mAnimImg != null) {
            mAnimImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.common_loading_anim));
        }
        if (mAnimImg != null && mAnimImg.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) mAnimImg.getDrawable()).start();
        }

        if (loading != null) {
            loading.setEnabled(false);
            loading.setVisibility(View.VISIBLE);
        }

        state = STATE_LOADING;
    }

    //设置加载失败
    private void setFailue() {
        isLoading = false;
        mAnimImg.setVisibility(View.VISIBLE);
        mAnimImg.setImageDrawable(mContext.getResources().getDrawable(mNetErrorTipImageResId));
        loading.setEnabled(true);
        loading.setVisibility(View.VISIBLE);
        state = STATE_ERROR;
    }

    //设置无数据加载
    private void setNoData() {
        isLoading = false;
        mAnimImg.setVisibility(View.VISIBLE);
        mAnimImg.setImageDrawable(mContext.getResources().getDrawable(mNoDataTipImageResId));
        loading.setEnabled(true);
        loading.setVisibility(View.VISIBLE);

        state = STATE_EMPTY;
    }

//    public void showLoading() {
//        if (isLoading)
//            return;
//
//        mContext.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showLoading();
//            }
//        });
//    }

}
