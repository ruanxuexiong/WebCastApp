package com.android.nana.material;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by THINK on 2017/7/20.
 */

public class EditCompanyActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleTv, mRightTv;
    private ImageButton mBack;
    private EditText mCompanyEt;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_company);
    }

    @Override
    protected void findViewById() {
        mCompanyEt = findViewById(R.id.et_company);
        mBack = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mRightTv = findViewById(R.id.common_txt_right_text);
    }

    @Override
    protected void init() {
        mTitleTv.setText("填写任职机构");
        mRightTv.setText("保存");

        if (null != getIntent().getStringExtra("company")) {
            mCompanyEt.setText(getIntent().getStringExtra("company"));
        }
        mRightTv.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                /*String companyStr = mCompanyEt.getText().toString().trim();
                if (companyStr.equals("")) {
                    ToastUtils.showToast("公司名不能为空");
                    return;
                } else {
                    EditCompanyActivity.this.finish();
                }*/
                EditCompanyActivity.this.finish();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
        }
    }

    private void save() {
        String companyStr = mCompanyEt.getText().toString().trim();

      /*  if (companyStr.equals("")) {
            ToastUtils.showToast("公司名不能为空");
            return;
        } else {
            EventBus.getDefault().post(new MessageEvent(companyStr));
            EditCompanyActivity.this.finish();
        }*/

        EventBus.getDefault().post(new MessageEvent(companyStr));
        EditCompanyActivity.this.finish();
    }
}
