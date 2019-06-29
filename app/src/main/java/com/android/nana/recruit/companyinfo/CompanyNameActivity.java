package com.android.nana.recruit.companyinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.recruit.eventBus.CompanyNameEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/3/23.
 */

public class CompanyNameActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv, mRightTv;
    private TextView mBackTv;
    private EditText mNameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"".equals(getIntent().getStringExtra("name"))) {
            mNameEt.setText(getIntent().getStringExtra("name"));
            mNameEt.requestFocus();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_company_name);
    }

    @Override
    protected void findViewById() {
        mNameEt = findViewById(R.id.et_name);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRightTv = findViewById(R.id.toolbar_right_2);
    }

    @Override
    protected void init() {
        mTitleTv.setText("公司名称");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        showSoftInputFromWindow(this, mNameEt);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                EventBus.getDefault().post(new CompanyNameEvent(mNameEt.getText().toString().trim()));
                this.finish();
                break;
            default:
                break;
        }
    }

    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
