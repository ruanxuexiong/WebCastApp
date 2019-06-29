package com.android.nana.material;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.PositionEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by THINK on 2017/7/20.
 */

public class EditTitleActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv, mRightTv;
    private ImageButton mBack;
    private EditText mTitleEt;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_title);
    }

    @Override
    protected void findViewById() {
        mTitleEt = findViewById(R.id.et_title);
        mBack = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mRightTv = findViewById(R.id.common_txt_right_text);
    }

    @Override
    protected void init() {
        mTitleTv.setText("填写职称");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        if (null != getIntent().getStringExtra("title")) {
            mTitleEt.setText(getIntent().getStringExtra("title"));
        }
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
                EditTitleActivity.this.finish();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
        }
    }

    private void save() {
        String position = mTitleEt.getText().toString().trim();
        EventBus.getDefault().post(new PositionEvent(position));
        EditTitleActivity.this.finish();
    }
}
