package com.android.nana.recruit.companyinfo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.recruit.eventBus.TheEvent;
import com.android.nana.util.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/3/29.
 */

public class TheActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRightTv;
    private Button mSaveBtn;
    private EditText mTheEt;
    private TextView mNumTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"".equals(getIntent().getStringExtra("the"))) {
            mTheEt.setText(getIntent().getStringExtra("the"));
            mTheEt.requestFocus();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_the);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mSaveBtn = findViewById(R.id.btn_save);
        mTheEt = findViewById(R.id.et_the);
        mNumTv = findViewById(R.id.tv_num);
    }

    @Override
    protected void init() {
        mTitleTv.setText("职位描述");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setVisibility(View.VISIBLE);
        mTheEt.addTextChangedListener(mTextWatcher);
        Utils.showSoftInputFromWindow(this, mTheEt);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }


    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mNumTv.setText(String.valueOf(temp.length()));
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
            case R.id.btn_save:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {
        this.finish();
        EventBus.getDefault().post(new TheEvent(mTheEt.getText().toString().trim()));
    }
}
