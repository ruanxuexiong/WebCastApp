package com.android.nana.downmenu;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.SchoolEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/1/6.
 */

public class SchoolActivity extends BaseActivity implements View.OnClickListener {
    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight1Tv;
    private EditText mSchoolEt;
    private String mSchoolName;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_school);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mSchoolEt = findViewById(R.id.et_position);
        mRight1Tv = findViewById(R.id.toolbar_right_2);
    }

    @Override
    protected void init() {

        if (null != getIntent().getStringExtra("school")) {
            mSchoolName = getIntent().getStringExtra("school");
            mSchoolEt.setText(mSchoolName);
        }

        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("学校名称");
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
                String mName = mSchoolEt.getText().toString().trim();
                EventBus.getDefault().post(new SchoolEvent(mName));
                this.finish();
                break;
            default:
                break;
        }
    }
}
