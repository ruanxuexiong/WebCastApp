package com.android.nana.customer.myincome;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.widget.StateButton;


/**
 * Created by Cristina on 2017/3/23.
 */
public class MyIncomeAddBankCartActivity extends BaseActivity {

    private TextView mTxtTitle;
    private TextView mIbBack;

    private EditText mEtName, mEtCart;

    private TextView mTxtBank;
    private StateButton mTxtNext;

    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private String mBankCartId;
    private String mName;
    private TextView mWithdraw;

    @Override
    protected void bindViews() {

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.add_bank_cart);

    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtTitle.setText("添加银行卡");

        mEtName = findViewById(R.id.add_bank_cart_et_name);
        mTxtBank = findViewById(R.id.add_bank_cart_txt_bank);
        mEtCart = findViewById(R.id.add_bank_cart_et_cart);

        mTxtNext = findViewById(R.id.add_bank_cart_txt_next);
        mWithdraw = findViewById(R.id.withdraw_txt_withdraw_price);

    }

    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        mName = (String) SharedPreferencesUtils.getParameter(MyIncomeAddBankCartActivity.this, "username", "");
        String html = "请添加<font color='#000000'>" + mName + "</font>的银行卡";
        mWithdraw.setText(Html.fromHtml(html));
        mEtName.setText(mName);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next();

            }
        });


        mEtCart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 16 || s.length() == 19) {

                    MyIncomeHelper.queryBankCardTypeWithCardNumber(s.toString(), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {

                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);

                            if (mResultDetailModel.mIsSuccess) {

                                mBankCartId = JSONUtil.get(mResultDetailModel.mJsonData, "id", "");
                                mTxtBank.setText(JSONUtil.get(mResultDetailModel.mJsonData, "name", ""));
                            }

                        }

                        @Override
                        public void getFailue(String failueJson) {

                            showToast("获取数据失败，请稍后重试");

                        }
                    });

                } else {

                    mTxtBank.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void next() {

        String name = mEtName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showToast("请输入持卡人姓名");
            return;
        }
        String cart = mEtCart.getText().toString().trim();
        if (TextUtils.isEmpty(cart)) {
            showToast("请输入持卡人卡号");
            return;
        }
       /* String bank = mTxtBank.getText().toString().trim();
        if (TextUtils.isEmpty(bank)) {
            showToast("请选择所在银行");
            return;
        }*/

        MyIncomeHelper.addBankCard(BaseApplication.getInstance().getCustomerId(this),
                name, cart, mBankCartId, mIOAuthCallBack);

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(MyIncomeAddBankCartActivity.this);
        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                MyIncomeAddBankCartActivity.this.finish();
            }
            UIHelper.showToast(MyIncomeAddBankCartActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(MyIncomeAddBankCartActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {

            setResult(RESULT_OK);
            MyIncomeAddBankCartActivity.this.finish();
        }

    }
}
