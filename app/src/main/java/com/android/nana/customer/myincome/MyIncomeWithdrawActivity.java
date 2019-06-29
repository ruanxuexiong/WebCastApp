package com.android.nana.customer.myincome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.ui.RelativeRadioGroup;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.pay.SignUtils;
import com.android.nana.widget.PasswordDialog;
import com.android.nana.widget.StateButton;
import org.json.JSONObject;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by Cristina on 2017/3/25.
 */
public class MyIncomeWithdrawActivity extends BaseActivity {
    String state = "";
    private TextView mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtSelectBank, mTxtWithdrawInfo;
    private StateButton mCommitBtn;
    private EditText mEtMoney, mEtPass;

    private String mBankCardTypeId;
    private String mPayPassword;
    private String mDirectBalance;
    private UserInfo mUserInfo;
    private TextView mAllTv;
    private TextView tv_withdraw_2, tv_withdraw_3;
    private RelativeRadioGroup radioGroup;
    private LinearLayout layout_bank;
    private RelativeLayout layout_ali_pay;
    private boolean isAliPay = true;
    private EditText ali_pay_account;
    private TextView tv_name;

    @Override
    protected void bindViews() {

        if (null != getIntent().getStringExtra("DirectBalance") && !"".equals(getIntent().getStringExtra("DirectBalance"))) {
            mDirectBalance = getIntent().getStringExtra("DirectBalance");
        } else {
            mDirectBalance = "0";
        }
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(this, "userInfo", UserInfo.class);
        mPayPassword = (String) SharedPreferencesUtils.getParameter(this, "payPassword", "");
        setContentView(R.layout.activity_my_income_withdraw_ensure);
    }

    @Override
    protected void findViewById() {
        //   Intent intent=getIntent();
        // state =intent.getStringExtra("state");
        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtTitle.setText("提现");

        mTxtSelectBank = findViewById(R.id.withdraw_txt_select_bank);
        mTxtWithdrawInfo = findViewById(R.id.withdraw_txt_withdraw_info);
        mEtMoney = findViewById(R.id.withdraw_txt_money);
        mEtPass = findViewById(R.id.withdraw_txt_pass);
        mAllTv = findViewById(R.id.tv_all);

        mCommitBtn = findViewById(R.id.btn_commit);
        tv_withdraw_2 = findViewById(R.id.tv_withdraw_2);
        tv_withdraw_3 = findViewById(R.id.tv_withdraw_3);

        radioGroup = findViewById(R.id.radioGroup);
        layout_bank = findViewById(R.id.layout_bank);
        layout_ali_pay = findViewById(R.id.layout_ali_pay);
        ali_pay_account = findViewById(R.id.ali_pay_account);
        tv_name = findViewById(R.id.tv_name);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        mTxtWithdrawInfo.setText("本次最多能提现￥" + mDirectBalance + "元，");
//        Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
        Drawable drawable = getResources().getDrawable(R.drawable.edit_bg_shape);
        mCommitBtn.setBackground(drawable);
//        mCommitBtn.setEnabled(false);
//        String strHtml_1 = "1.您可以选择提现到<font color='#00303F'>支付宝和银行卡</font>，收款账户信息需要和您认证的身份证上的姓名保持一致 <br/>2.审核成功后，需要等待<font color='#00303F'>1-3个工作日</font>，资金才会到账哦";
        String strHtml_1 = "提现到<font color='#00303F'>支付宝</font>的收款账户信息需要和您认证的身份证上的姓名保持一致";
        tv_withdraw_2.setText(Html.fromHtml(strHtml_1));
        String mName = (String) SharedPreferencesUtils.getParameter(MyIncomeWithdrawActivity.this, "username", "");
        String html = "姓名：<font color='#000000'>" + mName + "</font>";
        tv_name.setText(Html.fromHtml(html));


    }

    /**
     * 查找radioButton控件
     */
    public RadioButton findRadioButton(ViewGroup group) {
        RadioButton resBtn = null;
        int len = group.getChildCount();
        for (int i = 0; i < len; i++) {
            if (group.getChildAt(i) instanceof RadioButton) {
                resBtn = (RadioButton) group.getChildAt(i);
            } else if (group.getChildAt(i) instanceof ViewGroup) {
                findRadioButton((ViewGroup) group.getChildAt(i));
            }
        }
        return resBtn;
    }

    @Override
    protected void setListener() {

        radioGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                switch (compoundButton.getId()) {
                    case R.id.radio_1:
                        layout_bank.setVisibility(View.GONE);
                        layout_ali_pay.setVisibility(View.VISIBLE);
                        isAliPay = true;
                        break;
                    case R.id.radio_2:
                        layout_bank.setVisibility(View.VISIBLE);
                        layout_ali_pay.setVisibility(View.GONE);
                        isAliPay = false;
                        break;
                }

            }
        });
        mIbBack.setOnClickListener(mBackPullListener);

        mTxtSelectBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(MyIncomeWithdrawActivity.this, MyIncomeWithdrawSelectBankActivity.class), 1);
            }
        });

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = mEtMoney.getText().toString().trim();
                if (TextUtils.isEmpty(money) || Double.parseDouble(money) <= 0) {
                    showToast("请输入有效的金额");
                    return;
                }
                showDialog();
//                commit();
            }
        });

        mAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtMoney.setText(mDirectBalance);
                mEtMoney.requestFocus();
            }
        });

        mEtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pwd = mEtPass.getText().toString();
                if (!"".equals(pwd)) {
                    Drawable drawable = getResources().getDrawable(R.drawable.edit_bg_shape);
                    mCommitBtn.setBackground(drawable);
                    mCommitBtn.setEnabled(true);
                }
            }
        });
    }

    private void showDialog() {
        if (!isAliPay) {
            if (TextUtils.isEmpty(mBankCardTypeId)) {
                showToast("请选择您要提现的银行卡");
                return;
            }
        } else {
            String str_ali_pay = ali_pay_account.getText().toString().trim();
            if (TextUtils.isEmpty(str_ali_pay)) {
                showToast("请输入支付宝账号");
                return;
            }
        }
        //点击弹出对话框
        final PasswordDialog editDialog = new PasswordDialog(this);
        editDialog.setTitle("请输入支付密码");
        editDialog.setYesOnclickListener("确定", new PasswordDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast("请输入支付密码");
                } else {
                    mEtPass.setText(phone);
                    commit();
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
                }
            }
        });
        editDialog.setNoOnclickListener("取消", new PasswordDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
            }
        });
        editDialog.show();
        editDialog.showSoftInputFromWindow();
    }

    public String sign(String alipayAccount, String money, String userId, String time, String payPassword) {

        Map<String, String> map = new TreeMap<String, String>();
        map.put("alipayAccount", alipayAccount);
        map.put("money", money);
        map.put("userId", userId);
        map.put("time", time);
        map.put("payPassword", payPassword);

        return SignUtils.signSort(map);
    }

    private void commit() {
        String money = mEtMoney.getText().toString().trim();
        String pass = mEtPass.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            showToast("请输入密码");
            return;
        }
        String time = getTime();
        if (null != mUserInfo.getMobile() && !"".equals(mUserInfo.getMobile())) {
            String appSignature = MD5.MD5Hash(time + "&" + mUserInfo.getMobile() + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");
            if (!isAliPay) {
                MyIncomeHelper.setWithDraw(BaseApplication.getInstance().getCustomerId(this), money, mBankCardTypeId, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass), mUserInfo.getMobile(), time, appSignature, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {
                        showProgressDialog("", "加载中...");
                    }

                    @Override
                    public void getSuccess(String successJson) {
                        ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                        if (mResultDetailModel.mIsSuccess) {
                            dismissProgressDialog();
                            mEtMoney.setText("");
                            mEtPass.setText("");
                            MyIncomeWithdrawActivity.this.finish();
                        }
                        showToast(mResultDetailModel.mMessage);
                        dismissProgressDialog();

                    }

                    @Override
                    public void getFailue(String failueJson) {
                        dismissProgressDialog();
                    }
                });
            } else {
                String str_ali_pay = ali_pay_account.getText().toString().trim();
                if (!TextUtils.isEmpty(str_ali_pay)) {
                    String mTime = System.currentTimeMillis() + "";
                    String mStr = sign(str_ali_pay, money, BaseApplication.getInstance().getCustomerId(this), mTime, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass));
                    MyIncomeHelper.withDrawAliPay(str_ali_pay, money, BaseApplication.getInstance().getCustomerId(this), mTime, mStr, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                            showProgressDialog("", "加载中...");
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            if (mResultDetailModel.mIsSuccess) {
                                dismissProgressDialog();
                                mEtMoney.setText("");
                                mEtPass.setText("");
                                MyIncomeWithdrawActivity.this.finish();
                            }
                            showToast(mResultDetailModel.mMessage);
                            dismissProgressDialog();
                        }

                        @Override
                        public void getFailue(String failueJson) {
                            dismissProgressDialog();
                            showToast(failueJson);
                        }
                    });
                } else {
                    showToast("请输入支付宝账号");
                }
            }

        } else {
            showToast("繁忙中，请尝试重新登录再来提现");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {

            JSONObject object = JSONUtil.getStringToJson(data.getStringExtra("Json"));
            mBankCardTypeId = JSONUtil.get(object, "id", "");
            mTxtSelectBank.setText(JSONUtil.get(object, "name", ""));
            mTxtSelectBank.setTextColor(getResources().getColor(R.color.green_03));
        }
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }


}
