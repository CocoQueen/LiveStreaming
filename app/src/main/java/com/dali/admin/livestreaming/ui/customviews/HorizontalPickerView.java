package com.dali.admin.livestreaming.ui.customviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.HorizontalScrollView;

/**
 * 水平选择滚动View
 * Created by dali on 2017/7/11.
 */

public class HorizontalPickerView extends HorizontalScrollView{

    private DataSetObserver mDataSetObserver;
    private Adapter mAdapter;

    public HorizontalPickerView(Context context) {
        this(context,null);
    }

    public HorizontalPickerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    private void initialize() {
        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateAdapter();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                ((ViewGroup)getChildAt(0)).removeAllViews();
            }
        };
    }

    private void updateAdapter() {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        viewGroup.removeAllViews();

        for (int i=0;i<mAdapter.getCount();i++){
            View view = mAdapter.getView(i,null,viewGroup);
            viewGroup.addView(view);
        }

    }

    public void setAdapter(Adapter adapter) {
        if (this.mAdapter != null){
            this.mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);
        updateAdapter();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int side = getWidth()/2;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            getChildAt(0).setPaddingRelative(side,0,side,0);
        }else {
            getChildAt(0).setPadding(side,0,side,0);
        }
    }

    public void setClicked(int position){
        ((ViewGroup)getChildAt(0)).getChildAt(position).performClick();
    }
}
