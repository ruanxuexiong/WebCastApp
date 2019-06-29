package com.android.nana.downmenu;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.PositionEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/1/6.
 */

public class PositionNameActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight1Tv;
    private EditText mPositionEt;
    private String mPositionName;//职位名称

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_position_name);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mPositionEt = findViewById(R.id.et_position);
        mRight1Tv = findViewById(R.id.toolbar_right_2);
        mPositionEt = findViewById(R.id.et_position);
    }

    @Override
    protected void init() {

        if (null != getIntent().getStringExtra("position")) {
            mPositionName = getIntent().getStringExtra("position");
            mPositionEt.setText(mPositionName);
        }

        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("职位名称");
        mRight1Tv.setVisibility(View.VISIBLE);
        mRight1Tv.setText("完成");
        mRight1Tv.setTextColor(getResources().getColor(R.color.green));
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight1Tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                String mPositionName = mPositionEt.getText().toString().trim();
                EventBus.getDefault().post(new PositionEvent(mPositionName));
                this.finish();
                break;
            default:
                break;
        }
    }
}
