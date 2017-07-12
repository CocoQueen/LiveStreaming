package com.dali.admin.livestreaming.ui.customviews;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dali.admin.livestreaming.R;

/**
 * 美颜功能
 * Created by dali on 2017/7/10.
 */

public class BeautyDialogFragment extends DialogFragment{

    private SeekBarCallback mSeekBarCallback;
    private SeekBar mBeautySeekbar;
    private TextView mTvBeauty;
    private TextView mTvWhitening;
    public static final int STATE_BEAUTY = 0,STATE_WHITE = 1;
    private int mBeautyProgress = 100;
    private int mWhiteningProgress = 100;
    private int state;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_area);
        //外部点击取消
        dialog.setCanceledOnTouchOutside(true);

        mBeautySeekbar = (SeekBar) dialog.findViewById(R.id.beauty_seekbar);
        mBeautySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mSeekBarCallback!=null){
                    mSeekBarCallback.onProgressChanged(progress,state);
                }

                if (state == STATE_BEAUTY){
                    mBeautyProgress = progress;
                }else {
                    mWhiteningProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBeautySeekbar.setProgress(mBeautyProgress);

        mTvBeauty = (TextView) dialog.findViewById(R.id.tv_face_beauty);
        mTvWhitening = (TextView) dialog.findViewById(R.id.tv_face_whitening);
        mTvBeauty.setSelected(true);
        mTvWhitening.setSelected(false);

        state = STATE_BEAUTY;

        mTvBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvBeauty.setSelected(true);
                mTvWhitening.setSelected(false);

                //seek bae init
                state = STATE_BEAUTY;
                mBeautySeekbar.setProgress(mBeautyProgress);
            }
        });

        mTvWhitening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvBeauty.setSelected(false);
                mTvWhitening.setSelected(true);

                //seek bae init
                state = STATE_WHITE;
                mBeautySeekbar.setProgress(mWhiteningProgress);
            }
        });

        //设置宽度为屏宽，靠近屏幕底部
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽度持平
        window.setAttributes(lp);

        return dialog;
    }

    public void setSeekBarListener(SeekBarCallback callback){
        this.mSeekBarCallback = callback;
    }

    public interface SeekBarCallback{
        /**
         * @param progress 值
         * @param state 美颜，美白
         */
        void onProgressChanged(int progress,int state);
    }
}
