package com.android.nana.material;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.WorkPositionNameEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/7/30.
 */

public class WorkPositionActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight2Tv;
    private EditText mPositionEt;
    private String mPositionNameStr;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_work_position);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mPositionEt = findViewById(R.id.et_position);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("职位");
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("保存");
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));

        if (!"".equals(getIntent().getExtras().getString("positionName"))) {
            mPositionNameStr = getIntent().getExtras().getString("positionName");
            mPositionEt.setText(mPositionNameStr);
            mPositionEt.requestFocus();
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {
        String positionStr = mPositionEt.getText().toString().trim();
        EventBus.getDefault().post(new WorkPositionNameEvent(positionStr));
        WorkPositionActivity.this.finish();
    }
}
