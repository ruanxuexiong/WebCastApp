package com.android.nana.partner;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.eventBus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by THINK on 2017/7/8.
 */

public class ProfitDialogFragment extends DialogFragment implements View.OnClickListener {


    private LinearLayout mTotalLl, mMonthLL, mWeekLL, mDayLL;
    private TextView mCancelTv;

    public static ProfitDialogFragment newInstance() {
        ProfitDialogFragment fragment = new ProfitDialogFragment();
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
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.activity_profit, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCancelTv = (TextView) view.findViewById(R.id.tv_cancel);
        mTotalLl = (LinearLayout) view.findViewById(R.id.ll_total);
        mMonthLL = (LinearLayout) view.findViewById(R.id.ll_month);
        mWeekLL = (LinearLayout) view.findViewById(R.id.ll_week);
        mDayLL = (LinearLayout) view.findViewById(R.id.ll_day);

        mCancelTv.setOnClickListener(this);
        mTotalLl.setOnClickListener(this);
        mMonthLL.setOnClickListener(this);
        mWeekLL.setOnClickListener(this);
        mDayLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                ProfitDialogFragment.this.dismiss();
                break;
            case R.id.ll_total:
                EventBus.getDefault().post(new MessageEvent("total"));
                ProfitDialogFragment.this.dismiss();
                break;
            case R.id.ll_month:
                EventBus.getDefault().post(new MessageEvent("month"));
                ProfitDialogFragment.this.dismiss();
                break;
            case R.id.ll_week:
                EventBus.getDefault().post(new MessageEvent("week"));
                ProfitDialogFragment.this.dismiss();
                break;
            case R.id.ll_day:
                EventBus.getDefault().post(new MessageEvent("day"));
                ProfitDialogFragment.this.dismiss();
                break;
        }
    }
}
