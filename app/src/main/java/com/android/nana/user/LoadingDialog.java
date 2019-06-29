package com.android.nana.user;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.nana.R;

/**
 * Created by lenovo on 2017/9/8.
 */

public class LoadingDialog extends Dialog {


    private TextView mTextView;

    public LoadingDialog(Context context) {

        super(context, R.style.WinDialog);//WinDialog
        setContentView(R.layout.ui_dialog_loading);
        mTextView = (TextView) findViewById(android.R.id.message);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
    public void setText(String s) {
        if (mTextView != null) {
            mTextView.setText(s);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setText(int res) {
        if (mTextView != null) {
            mTextView.setText(res);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}
