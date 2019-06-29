package com.android.nana.recruit.companyinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.recruit.eventBus.CompanyTheEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/3/24.
 */

public class CompanyTheActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRightTv;
    private EditText mTheEt;
    private TextView mNumTv;
    private Button mSaveBtn;


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
        setContentView(R.layout.activity_company_the);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);

        mTheEt = findViewById(R.id.et_the);
        mNumTv = findViewById(R.id.tv_num);
        mSaveBtn = findViewById(R.id.btn_save);

    }


    @Override
    protected void init() {
        mTitleTv.setText("公司介绍");
        mRightTv.setText("保存");
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        mRightTv.setVisibility(View.VISIBLE);
        mTheEt.addTextChangedListener(mTextWatcher);
        showSoftInputFromWindow(this, mTheEt);
    }

    @Override
    protected void setListener() {
        mRightTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {
        this.finish();
        EventBus.getDefault().post(new CompanyTheEvent(mTheEt.getText().toString().trim()));
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

    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
