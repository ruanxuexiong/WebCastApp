package com.android.nana.customer.myincome;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.android.nana.R;
import com.android.nana.listener.DetailsListener;

/**
 * Created by lenovo on 2018/7/13.
 */

public class DetailedFragment extends DialogFragment implements View.OnClickListener {

    private RadioButton mRadioAll, mRadioExp, mRadioIncome;
    private RadioButton mRadioRecharge, mRadioPut;

    public static DetailedFragment newInstance() {
        DetailedFragment fragment = new DetailedFragment();
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();

        int width = dm.widthPixels;
        if (width >= 1080) {
            wlp.y = 120;
        } else {
            wlp.y = 70;
        }

        wlp.x = 0;
        wlp.dimAmount = 0.2f;
        wlp.gravity = Gravity.TOP;
        getDialog().getWindow().setAttributes(wlp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明


        View view = inflater.inflate(R.layout.pop_detailed_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRadioAll = view.findViewById(R.id.radio_all);
        mRadioExp = view.findViewById(R.id.radio_exp);
        mRadioPut = view.findViewById(R.id.radio_put);
        mRadioIncome = view.findViewById(R.id.radio_income);
        mRadioRecharge = view.findViewById(R.id.radio_recharge);

        mRadioAll.setOnClickListener(this);
        mRadioExp.setOnClickListener(this);
        mRadioPut.setOnClickListener(this);
        mRadioIncome.setOnClickListener(this);
        mRadioRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radio_all:
                if (DetailsListener.getInstance().mOnDetailsListener != null) {
                    DetailsListener.getInstance().mOnDetailsListener.onClickAll(0);
                    this.dismiss();
                }
                break;
            case R.id.radio_exp:
                if (DetailsListener.getInstance().mOnDetailsListener != null) {
                    DetailsListener.getInstance().mOnDetailsListener.onClickExp(1);
                    this.dismiss();
                }
                break;
            case R.id.radio_income:
                if (DetailsListener.getInstance().mOnDetailsListener != null) {
                    DetailsListener.getInstance().mOnDetailsListener.onClickIncome(2);
                    this.dismiss();
                }
                break;
            case R.id.radio_recharge:
                if (DetailsListener.getInstance().mOnDetailsListener != null) {
                    DetailsListener.getInstance().mOnDetailsListener.onClickRecharge(3);
                    this.dismiss();
                }
                break;
            case R.id.radio_put:
                if (DetailsListener.getInstance().mOnDetailsListener != null) {
                    DetailsListener.getInstance().mOnDetailsListener.onClickPut(4);
                    this.dismiss();
                }
                break;
            default:
                break;
        }
    }

}
