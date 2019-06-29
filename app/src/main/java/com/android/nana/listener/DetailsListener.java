package com.android.nana.listener;

/**
 * Created by lenovo on 2018/7/16.
 */

public class DetailsListener {

    private static DetailsListener mDetailsListener;
    public OnDetailsListener mOnDetailsListener;

    public DetailsListener(){

    }
    public DetailsListener(OnDetailsListener mOnDetailsListener) {
        this.mOnDetailsListener = mOnDetailsListener;
    }

    public static DetailsListener getInstance() {
        if (mDetailsListener == null) {
            mDetailsListener = new DetailsListener();
        }
        return mDetailsListener;
    }

    public interface OnDetailsListener {

        void onClickAll(int type);

        void onClickExp(int type);

        void onClickIncome(int type);

        void onClickRecharge(int type);

        void onClickPut(int type);
    }

}
