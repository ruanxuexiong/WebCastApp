package com.android.nana.find.map;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by lenovo on 2018/11/1.
 */

public class MapMsgActivity extends BaseActivity {
    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRightTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_map_msg);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
    }

    @Override
    protected void init() {
        mTitleTv.setText("个性化设置");
        mBackTv.setText("取消");
        mRightTv.setText("清空");
        mRightTv.setTextColor(getResources().getColor(R.color.green_33));
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setCompoundDrawables(null, null, null, null);
    }

    @Override
    protected void setListener() {

    }
}
