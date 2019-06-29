package com.android.nana.main;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by lenovo on 2017/11/16.
 */

public class AuditedActivity extends BaseActivity {

    private TextView mBackBtn;
    private TextView mTitleTv;
    private ImageView audited_img;
    private TextView audited_tv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_audited);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        audited_tv = findViewById(R.id.audited_tv);
        audited_img = findViewById(R.id.audited_img);
    }

    @Override
    protected void init() {
        String state = getIntent().getStringExtra("state");
        if("1".equals(state)){
            audited_img.setBackground(getResources().getDrawable(R.drawable.preson_audited));
            audited_tv.setText("您的个人信息已通过认证");
        }else{
            audited_tv.setText("您的企业信息已通过认证");
            audited_img.setBackground(getResources().getDrawable(R.drawable.icon_audited));
        }
        mBackBtn.setVisibility(View.VISIBLE);
        mTitleTv.setText("身份认证");
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuditedActivity.this.finish();
            }
        });
    }
}
