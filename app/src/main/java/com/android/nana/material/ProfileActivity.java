package com.android.nana.material;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by THINK on 2017/6/28.
 */

public class ProfileActivity extends BaseActivity {


    private ImageButton mBack;
    private TextView mTitleTv, mTextTv, mTitle;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void findViewById() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mTextTv = (TextView) findViewById(R.id.tv_text);
        mTitle = (TextView) findViewById(R.id.common_txt_title);
        mBack = (ImageButton) findViewById(R.id.common_btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void init() {
        mTitle.setText("个人简介");
        if (null != getIntent().getStringExtra("title") && null != getIntent().getStringExtra("introduce")) {
            mTextTv.setText(getIntent().getStringExtra("introduce"));
            mTitleTv.setText(getIntent().getStringExtra("title"));
        }
    }

    @Override
    protected void setListener() {
    }

}
