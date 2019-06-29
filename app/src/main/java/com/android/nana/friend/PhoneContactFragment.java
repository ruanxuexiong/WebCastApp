package com.android.nana.friend;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.util.PhoneUtils;
import com.android.nana.widget.DrawableTextView;

/**
 * Created by lenovo on 2019/1/8.
 */

public class PhoneContactFragment extends DialogFragment {

    private TextView mNameTv;
    private EditText mTextEt;
    private String name, msg, phone;
    private DrawableTextView mSendTv;

    public static PhoneContactFragment newInstance(String name, String msg, String phone) {
        PhoneContactFragment fragment = new PhoneContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("msg", msg);
        bundle.putString("phone", phone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        name = getArguments().getString("name");
        msg = getArguments().getString("msg");
        phone = getArguments().getString("phone");

        mNameTv = view.findViewById(R.id.tv_name);
        mTextEt = view.findViewById(R.id.et_text);
        mSendTv = view.findViewById(R.id.tv_send);
        mTextEt.setCursorVisible(false);
        mTextEt.setFocusable(false);
        mTextEt.setFocusableInTouchMode(false);

        mNameTv.setText("向"+name + "发送信息：");
        mTextEt.setText(msg);

        mSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick == null)
                    PhoneUtils.sendSms(getActivity(), phone, msg);
                else
                    onClick.sendMsg(msg);
            }
        });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onClick != null)
            onClick.dismiss();
    }

    private OnClick onClick;

    public void setOnClickListener(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void sendMsg(String msg);

        void dismiss();
    }
}
