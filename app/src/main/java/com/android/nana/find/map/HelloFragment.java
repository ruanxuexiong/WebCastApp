package com.android.nana.find.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.nana.R;

/**
 * Created by lenovo on 2018/10/31.
 */

public class HelloFragment extends DialogFragment implements View.OnClickListener {

    private TextView mCancelTv;
    private TextView mSendTv;
    private EditText mTextEt;

    public static HelloFragment newInstance() {
        HelloFragment fragment = new HelloFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_hello, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancelTv = view.findViewById(R.id.tv_cancel);
        mSendTv = view.findViewById(R.id.tv_send);
        mTextEt = view.findViewById(R.id.et_text);

        mCancelTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_send:
                break;
            default:
                break;
        }
    }
}
