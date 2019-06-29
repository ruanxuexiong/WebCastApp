package com.android.nana.user;

import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by lenovo on 2017/9/7.
 */

public class UserDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv, mTitleTv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_user_details);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
    }

    @Override
    protected void init() {
        mTitleTv.setText("详细资料");
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                UserDetailsActivity.this.finish();
                break;
        }
    }
}
