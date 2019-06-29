package com.android.nana.material;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.AutographEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2017/11/16.
 */

public class AutographActivity extends BaseActivity implements View.OnClickListener {

    private EditText mIntroduceEt;
    private TextView mNumTv;
    private ImageButton mBackBtn;
    private TextView mTitleTv, mRightTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("doc")) {
            mIntroduceEt.setText(getIntent().getStringExtra("doc"));
            mIntroduceEt.requestFocus();//有内容光标显示在后面
        }
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_autograph);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.common_txt_title);
        mBackBtn = findViewById(R.id.common_btn_back);
        mRightTv = findViewById(R.id.common_txt_right_text);

        mNumTv = findViewById(R.id.tv_num);
        mIntroduceEt = findViewById(R.id.et_doc);
    }

    @Override
    protected void init() {
        mRightTv.setText("保存");
        mTitleTv.setText("个性签名");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setEnabled(false);

        mIntroduceEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    mRightTv.setEnabled(true);
                    mRightTv.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mRightTv.setEnabled(false);
                    mRightTv.setTextColor(getResources().getColor(R.color.green_99));
                }
                mNumTv.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        showSoftInputFromWindow(AutographActivity.this, mIntroduceEt);
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                this.finish();
                break;
            case R.id.common_txt_right_text:
                String content = mIntroduceEt.getText().toString().trim();
                EventBus.getDefault().post(new AutographEvent(content));
                this.finish();
                break;
        }
    }

    //获取焦点弹出软件盘
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
