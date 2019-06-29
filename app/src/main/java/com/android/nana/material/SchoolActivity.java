package com.android.nana.material;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.SchoolEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/8/1.
 */

public class SchoolActivity extends BaseActivity implements View.OnClickListener {
    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight2Tv;
    private EditText mSchoolEt;
    private String mSchoolNameStr;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_school);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mSchoolEt = findViewById(R.id.et_position);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("学校");
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("保存");
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        mSchoolEt.setHint("请输入学校名称");

        if (!"".equals(getIntent().getExtras().getString("school"))) {
            mSchoolNameStr = getIntent().getExtras().getString("school");
            mSchoolEt.setText(mSchoolNameStr);
            mSchoolEt.requestFocus();
        }
    }

    @Override
    protected void setListener() {
        mRight2Tv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
        String school = mSchoolEt.getText().toString().trim();
        if (school.equals("")) {
            ToastUtils.showToast("学校名不能为空");
            return;
        } else {
            EventBus.getDefault().post(new SchoolEvent(school));
            SchoolActivity.this.finish();
        }
    }
}
