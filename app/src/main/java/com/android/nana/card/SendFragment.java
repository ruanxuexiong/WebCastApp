package com.android.nana.card;

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
import android.widget.TextView;

import com.android.nana.R;

/**
 * Created by lenovo on 2018/1/29.
 */

public class SendFragment extends DialogFragment implements View.OnClickListener {

    private String money;
    private String enough;
    private TextView mMoneyTv;
    private TextView mCancelTv;
    private io.rong.imlib.model.Message mMessage;
    private SendListener mListener;
    private View mSendTv, mSendRemindTv;

    public void setSendListener(SendListener mListener) {
        this.mListener = mListener;
    }

    public static SendFragment newInstance(String mMoney, String mEnough,io.rong.imlib.model.Message message) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putString("money", mMoney);
        args.putString("enough", mEnough);
        args.putParcelable("message",message);
        fragment.setArguments(args);
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
        money = getArguments().getString("money");
        enough = getArguments().getString("enough");
        mMessage = getArguments().getParcelable("message");

        View view = inflater.inflate(R.layout.activity_send, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSendTv = view.findViewById(R.id.tv_send);
        mMoneyTv = view.findViewById(R.id.tv_money);
        mCancelTv = view.findViewById(R.id.tv_cancel);
        mSendRemindTv = view.findViewById(R.id.tv_send_remind);

        if (null != money) {
            mMoneyTv.setText("您还不是对方好友，发送消息将送出一元招\n呼礼，7天内未被回复，招呼礼将自动退回。");
        }

        mSendTv.setOnClickListener(this);
        mSendRemindTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send:
                if (null != mListener) {
                    mListener.onSendClick(mMessage);
                }
                break;
            case R.id.tv_send_remind:
                if (null != mListener) {
                    mListener.onSendRemindClick(mMessage);
                }
                break;
            case R.id.tv_cancel:
                SendFragment.this.dismiss();
                break;
            default:
                break;
        }
    }

    public interface SendListener {
        void onSendClick(io.rong.imlib.model.Message message);

        void onSendRemindClick(io.rong.imlib.model.Message message);
    }
}
