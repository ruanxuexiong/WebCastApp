package com.android.nana.inquiry;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.pattern.PatternDBHelper;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.widget.InputFilterMinMax;
import com.android.nana.widget.MoneyEditText;
import com.android.nana.widget.StateButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/4/12.
 */

public class SetMoneyActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private MoneyEditText mMoneyEt;
    private StateButton mSaveBtn;
    private String mid, money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getIntent().getStringExtra("mid")) {
            mid = getIntent().getStringExtra("mid");
            money = getIntent().getStringExtra("money");
            mMoneyEt.setText(money);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_set_money);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mMoneyEt = findViewById(R.id.et_money);
        mSaveBtn = findViewById(R.id.btn_save);
    }

    @Override
    protected void init() {
        mTitleTv.setText("设置收费标准");
        mBackTv.setVisibility(View.VISIBLE);
        Utils.showSoftInputFromWindow(SetMoneyActivity.this, mMoneyEt);
        mMoneyEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});
    }

    @Override
    protected void setListener() {
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
            default:
                break;
        }
    }


    private void save() {//保存三分钟模式
        showProgressDialog("", "加载中...");
        String money = mMoneyEt.getText().toString().trim().replace(",", "");
        if ("0.00".equals(money)) {
            dismissProgressDialog();
            ToastUtils.showToast("请输入正确金额");
            return;
        }
        if (TextUtils.isEmpty(money)) {
            dismissProgressDialog();
            ToastUtils.showToast("金额不能为空！");
            return;
        }

        PatternDBHelper.open(mid, "1", money, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        SetMoneyActivity.this.finish();
                        ToastUtils.showToast("设置成功!");
                    } else {
                        ToastUtils.showToast("设置失败，请检查后在设置！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("设置失败，请检查后在设置！");
            }
        });
    }

}
