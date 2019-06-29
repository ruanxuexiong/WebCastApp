package com.android.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.BaseApplication;
import com.android.common.R;

public abstract class BaseRequestFragment extends Fragment {

    protected View mView;
    protected BaseApplication mBaseApplication;
    protected ProgressDialog mProgressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(getLayoutId(), container, false);
        mBaseApplication = (BaseApplication) getActivity().getApplication();
        findViewById();
        locationData();
        initOther();
        init();
        setListener();

        return mView;
    }

    protected abstract int getLayoutId();

    protected void locationData() {
    }

    protected void initOther() {
    }

    protected abstract void findViewById();

    protected abstract void init();

    protected abstract void setListener();

    protected void showProgressDialog() {
        showProgressDialog(null, getString(R.string.loading));
    }

    protected void showProgressDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        if (title != null && !title.isEmpty()) {
            mProgressDialog.setTitle(title);
        }
        if (message != null && !message.isEmpty()) {
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected View findViewById(int id) {
        return mView.findViewById(id);
    }



}
