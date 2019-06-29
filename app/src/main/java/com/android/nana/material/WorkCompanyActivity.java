package com.android.nana.material;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.WorkCompanyNameEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/7/30.
 */

public class WorkCompanyActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight2Tv;
    private EditText mCompanyEt;
    private String mCompanyNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_work_company);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mCompanyEt = findViewById(R.id.et_company);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("公司");
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("保存");
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));

        if (!"".equals(getIntent().getExtras().getString("companyName"))) {
            mCompanyNameStr = getIntent().getExtras().getString("companyName");
            mCompanyEt.setText(mCompanyNameStr);
            mCompanyEt.requestFocus();
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
        String companyStr = mCompanyEt.getText().toString().trim();
        if (companyStr.equals("")) {
            ToastUtils.showToast("公司名不能为空");
            return;
        } else {
            EventBus.getDefault().post(new WorkCompanyNameEvent(companyStr));
            WorkCompanyActivity.this.finish();
        }
    }
}
