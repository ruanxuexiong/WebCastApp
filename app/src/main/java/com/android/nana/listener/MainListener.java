package com.android.nana.listener;

import com.android.nana.receiver.PushExtra;

import io.rong.imlib.model.Message;

public class MainListener {

    private static MainListener mMainListener;
    public OnMessageRefreshListener mOnMessageRefreshListener;
    public OnCustomerRefreshListener mOnCustomerRefreshListener;
    public OnMessageListener mOnMessageListener;
    public OnAgreeListener mOnAgreeListener;
    public OnShowDialogListener mOnShowDialogListener;
    public OnEvaluateListener mOnEvaluateListener;

    public static MainListener getInstance() {
        if (mMainListener == null) {
            mMainListener = new MainListener();
        }
        return mMainListener;
    }

    public interface OnMessageRefreshListener {
        void refersh();

        void clear();
    }

    public interface OnCustomerRefreshListener {
        void refersh();
    }

    public interface OnAddFriendMsgRefreshListener {
        void refersh();
    }

    public interface OnMessageListener {
        void refersh(Message message);
    }

    public interface OnAgreeListener {
        void refresh(PushExtra pushExtra);
    }

    public interface OnShowDialogListener {
        void showDialog(String charge, String isFree, String total);
    }

    public interface OnEvaluateListener {
        void showEvaluate(String charge, String isFree, String total);
    }


}
