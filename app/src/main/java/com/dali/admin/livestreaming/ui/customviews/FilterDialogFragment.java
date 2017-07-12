package com.dali.admin.livestreaming.ui.customviews;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dali.admin.livestreaming.R;

import java.util.ArrayList;

/**
 * 滤镜
 * Created by dali on 2017/7/11.
 */

public class FilterDialogFragment extends DialogFragment {

    private ArrayList<String> mFilterList;
    private HorizontalPickerView mHorizontalPickerView;
    private ArrayAdapter<String> mFilterAdapter;
    private Context mContext;

    private int mFilterType = FILTERTYPE_NONE; //滤镜类型
    /**
     * 滤镜定义
     */
    public static final int FILTERTYPE_NONE = 0;    //无特效滤镜
    public static final int FILTER_LANGMAN = 1;    //浪漫滤镜
    public static final int FILTER_QINGXIN = 2;    //清新滤镜
    public static final int FILTER_WEIMEI = 3;    //唯美滤镜
    public static final int FILTER_FENNEN = 4;    //粉嫩滤镜
    public static final int FILTER_HUAIJIU = 5;    //怀旧滤镜
    public static final int FILTER_LANDIAO = 6;    //蓝调滤镜
    public static final int FILTER_QINGLIANG = 7;    //清凉滤镜
    public static final int FILTER_RIXI = 8;    //日系滤镜

    private FilterCallback mFilterCallback;

    public void setFilterCallback(FilterCallback filterCallback) {
        mFilterCallback = filterCallback;
    }

    public interface FilterCallback{
        void setFilter(Bitmap filterBitmap);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_filter);
        dialog.setCanceledOnTouchOutside(true);

        mContext = dialog.getContext();
        initView(dialog);
        initData();

        return dialog;
    }

    private void initData() {
        mFilterList = new ArrayList<String>();
        mFilterList.add("无滤镜");
        mFilterList.add("浪漫滤镜");
        mFilterList.add("清新滤镜");
        mFilterList.add("唯美滤镜");
        mFilterList.add("粉嫩滤镜");
        mFilterList.add("怀旧滤镜");
        mFilterList.add("蓝调滤镜");
        mFilterList.add("清凉滤镜");
        mFilterList.add("日系滤镜");
        mFilterAdapter = new ArrayAdapter<String>(mContext,0,mFilterList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                String value = getItem(position);
                if (convertView == null){
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(android.R.layout.simple_list_item_1,null);
                }
                TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
                tv.setTag(position);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                tv.setText(value);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        ViewGroup group = (ViewGroup) mHorizontalPickerView.getChildAt(0);
                        for (int i=0;i<mFilterAdapter.getCount();i++){
                            View view = group.getChildAt(i);
                            if (view instanceof TextView){
                                if (i==index){
                                    ((TextView) view).setTextColor(Color.GRAY);
                                }else {
                                    ((TextView) view).setTextColor(Color.BLACK);
                                }
                            }
                        }
                        setFilter(index);
                    }
                });

                return convertView;
            }
        };

        mHorizontalPickerView.setAdapter(mFilterAdapter);
        mHorizontalPickerView.setClicked(0);
    }

    private void setFilter(int filterType) {
        mFilterType = filterType;
        Bitmap bmp = null;
        switch (mFilterType){
            case FILTER_LANGMAN:
                bmp = decodeResource(getResources(),R.drawable.filter_langman);
                break;
            case FILTER_QINGXIN:
                bmp = decodeResource(getResources(), R.drawable.filter_qingxin);
                break;
            case FILTER_WEIMEI:
                bmp = decodeResource(getResources(), R.drawable.filter_weimei);
                break;
            case FILTER_FENNEN:
                bmp = decodeResource(getResources(), R.drawable.filter_fennen);
                break;
            case FILTER_HUAIJIU:
                bmp = decodeResource(getResources(), R.drawable.filter_huaijiu);
                break;
            case FILTER_LANDIAO:
                bmp = decodeResource(getResources(), R.drawable.filter_landiao);
                break;
            case FILTER_QINGLIANG:
                bmp = decodeResource(getResources(), R.drawable.fliter_qingliang);
                break;
            case FILTER_RIXI:
                bmp = decodeResource(getResources(), R.drawable.filter_rixi);
                break;
            default:
                bmp = null;
                break;
        }
        if (mFilterCallback != null){
            mFilterCallback.setFilter(bmp);
        }
    }

    private void initView(Dialog dialog) {
        mHorizontalPickerView = (HorizontalPickerView) dialog.findViewById(R.id.filterPicker);
        //设置宽度为屏宽，靠近屏幕底部
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    private Bitmap decodeResource(Resources resources,int id){
        TypedValue value = new TypedValue();
        resources.openRawResource(id,value);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources,id,options);
    }
}
