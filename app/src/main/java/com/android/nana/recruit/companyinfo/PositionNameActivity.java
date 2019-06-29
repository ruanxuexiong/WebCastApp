package com.android.nana.recruit.companyinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.recruit.eventBus.PositionNameEvent;
import com.android.nana.util.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/3/27.
 */

public class PositionNameActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRightTv;
    private EditText mPositionEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"".equals(getIntent().getStringExtra("name"))) {
            mPositionEt.setText(getIntent().getStringExtra("name"));
            mPositionEt.requestFocus();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_position_name);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mPositionEt = findViewById(R.id.et_position);
    }

    @Override
    protected void init() {
        mTitleTv.setText("职位名称");
        mRightTv.setText("保存");
        mPositionEt.setHint("请输入职位名称");
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        Utils.showSoftInputFromWindow(this, mPositionEt);
    }

    @Override
    protected void setListener() {
        mRightTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                EventBus.getDefault().post(new PositionNameEvent(mPositionEt.getText().toString().trim()));
                this.finish();
                break;
            default:
                break;
        }
    }
}
