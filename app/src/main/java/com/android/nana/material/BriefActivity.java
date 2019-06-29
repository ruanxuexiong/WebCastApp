package com.android.nana.material;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.BriefEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by THINK on 2017/7/20.
 */

public class BriefActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv, mRightTv;
    private ImageButton mBack;
    private EditText mDocEt;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_brief);
    }

    @Override
    protected void findViewById() {
        mDocEt = findViewById(R.id.et_doc);
        mBack = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mRightTv = findViewById(R.id.common_txt_right_text);
    }

    @Override
    protected void init() {
        mTitleTv.setText("自我介绍");
        mRightTv.setText("保存");

        if (null != getIntent().getStringExtra("doc")) {
            mDocEt.setText(getIntent().getStringExtra("doc"));
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
                finish();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
        }
    }

    private void save() {
        String content = mDocEt.getText().toString().trim();
        EventBus.getDefault().post(new BriefEvent(content));
        BriefActivity.this.finish();
    }
}
