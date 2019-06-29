package com.android.nana.customer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.pay.AliPayBuilder;
import com.android.common.pay.WxPaymentBuilder;
import com.android.nana.R;
import com.android.nana.customer.myincome.MyIncomeModel;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.util.Constant;
import com.android.nana.util.ForAllUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.pay.AliPaymentBuilder;
import com.android.nana.widget.InputFilterMinMax;
import com.android.nana.widget.MoneyEditText;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lenovo on 2017/8/4.
 */

public class RechargeActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioButton mAliPayBtn, mWxPayBtn;
    private TextView mBackBtn;
    private TextView mTitleTv;
    private RadioGroup mRadioGroup;
    private RadioButton mRadio10, mRadio50, mRadio100, mRadio200, mRadio2000;
    private MoneyEditText mMoneyEt;
    private StateButton mSubBtn;
    private String mMoney = "100";
    private boolean isMoneyEt = false;
    private String mPayType = "PAY_WITH_ALIPAY";
    private AliPayBuilder mAliPayBuilder;
    private LinearLayout mGroupLl;
    private WxPaymentBuilder mWxPaymentBuilder;
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private AliPaymentBuilder mAliPaymentBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAliPayBuilder = new AliPayBuilder(this);
        mAliPayBuilder.registerAliConfig(Constant.Payment.AliPay.Partner, Constant.Payment.AliPay.Seller, Constant.Payment.AliPay.RsaPrivate);
        mWxPaymentBuilder = new WxPaymentBuilder(this);
        mAliPaymentBuilder = new AliPaymentBuilder(this);

        if (!EventBus.getDefault().isRegistered(RechargeActivity.this)) {
            EventBus.getDefault().register(RechargeActivity.this);
        }
        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_recharge);
    }

    @Override
    protected void findViewById() {
        mAliPayBtn = findViewById(R.id.rb_ali_pay);
        mWxPayBtn = findViewById(R.id.rb_wx_pay);
        mBackBtn = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);

        mRadio10 = findViewById(R.id.radio_10);
        mRadio50 = findViewById(R.id.radio_50);
        mRadio100 = findViewById(R.id.radio_100);
        mRadio200 = findViewById(R.id.radio_200);
        mRadio2000 = findViewById(R.id.radio_2000);
        mMoneyEt = findViewById(R.id.et_money);
        mSubBtn = findViewById(R.id.btn_recharge);
        mGroupLl = findViewById(R.id.ll_group);

        mRadioGroup = findViewById(R.id.radio_group);
        mMoneyEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});
    }

    @Override
    protected void init() {
        mBackBtn.setVisibility(View.VISIBLE);
        mTitleTv.setText("充值");
    }

    @Override
    protected void setListener() {
        mMoneyEt.setOnClickListener(this);
        mMoneyEt.setCursorVisible(false);

        mRadio10.setChecked(true);
        mSubBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);


        mMoneyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.hideSoftInputFromWindow(textView.getApplicationWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                hideIMM(RechargeActivity.this, view);
                finish();
                break;
            case R.id.btn_recharge:
                submit();
                break;
            case R.id.et_money:

                mRadio10.setChecked(false);
                mRadio50.setChecked(false);
                mRadio100.setChecked(false);
                mRadio200.setChecked(false);
                mRadio2000.setChecked(false);

                mMoney = "";
                isMoneyEt = true;
                mMoneyEt.setFocusable(true);
                mMoneyEt.setCursorVisible(true);
                mMoneyEt.setFocusableInTouchMode(true);
                mMoneyEt.requestFocus();
                showIMM(this, view);
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    private void submit() {//提交
        if (mAliPayBtn.isChecked()) {
            mPayType = "PAY_WITH_ALIPAY";
        }
        if (mWxPayBtn.isChecked()) {
            mPayType = "PAY_WITH_WECHAT";
        }

        if (isMoneyEt) {
            mMoney = mMoneyEt.getText().toString().trim().replace(",", "");

            if ("0.00".equals(mMoney)) {
                ToastUtils.showToast("输入金额错误");
                return;
            }
            if (TextUtils.isEmpty(mMoney)) {
                ToastUtils.showToast("金额不能为空！");
                return;
            }
            if (Double.valueOf(mMoney) < 100) {
                showIncomeDialogs();
                return;
            }
            isMoneyEt = false;
            pay(RechargeActivity.this, mMoney, mPayType);
        } else {
            pay(RechargeActivity.this, mMoney, mPayType);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.radio_10:
                mMoney = "100";
                mMoneyEt.setText("");
                if (mRadio10.isChecked()) {
                    mRadio10.setChecked(true);
                } else {
                    mRadio10.setChecked(false);
                }
                mMoneyEt.setCursorVisible(false);
                hideIMM(RechargeActivity.this, radioGroup);

                isMoneyEt = false;
                break;
            case R.id.radio_50:
                mMoney = "200";
                mMoneyEt.setText("");
                if (mRadio50.isChecked()) {
                    mRadio50.setChecked(true);
                } else {
                    mRadio50.setChecked(false);
                }
                mMoneyEt.setCursorVisible(false);
                hideIMM(RechargeActivity.this, radioGroup);

                isMoneyEt = false;
                break;
            case R.id.radio_100:
                mMoney = "500";
                mMoneyEt.setText("");

                if (mRadio100.isChecked()) {
                    mRadio100.setChecked(true);
                } else {
                    mRadio100.setChecked(false);
                }
                mMoneyEt.setCursorVisible(false);
                hideIMM(RechargeActivity.this, radioGroup);
                isMoneyEt = false;
                break;
            case R.id.radio_200:
                mMoney = "1000";
                mMoneyEt.setText("");

                if (mRadio200.isChecked()) {
                    mRadio200.setChecked(true);
                } else {
                    mRadio200.setChecked(false);
                }

                isMoneyEt = false;
                mMoneyEt.setCursorVisible(false);
                hideIMM(RechargeActivity.this, radioGroup);
                break;
            case R.id.radio_2000:
                mMoney = "2000";
                mMoneyEt.setText("");

                if (mRadio2000.isChecked()) {
                    mRadio2000.setChecked(true);
                } else {
                    mRadio2000.setChecked(false);
                }

                isMoneyEt = false;
                mMoneyEt.setCursorVisible(false);
                hideIMM(RechargeActivity.this, radioGroup);
                break;
            default:
                break;
        }
    }

    public void hideIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    private void pay(Context context, String money, String payType) {

        String userId = (String) SharedPreferencesUtils.getParameter(RechargeActivity.this, "userId", "");
        String time = getTime();
        MyIncomeModel.recharge(context, money, payType, userId, time, new MyIncomeModel.OnMyIncomeLinstener() {
            @Override
            public void doWxPay(String money, String orderId) {
                if (ForAllUtils.isWeixinAvilible(RechargeActivity.this)) {
                    mWxPaymentBuilder.registerAppInfo(Constant.Payment.WxPay.APP_ID, Constant.Payment.WxPay.API_KEY, Constant.Payment.WxPay.MCH_ID)
                            .registerNotifyUrl(Constant.Payment.WxPay.NotifyUrl)
                            .registerOrderDescription("哪哪-订单支付")
                            .registerPrice((int) ((Double.parseDouble(money) * 100)))
                            .registerTransactionNumber(orderId)
                            .build();

                    mWxPaymentBuilder.doPay();
                } else {
                    ToastUtils.showToast("充值失败，请安装微信客户端！");
                }

            }

            @Override
            public void doAliPay(String money, String orderId) {
                mAliPaymentBuilder.payV2(money, "哪哪-订单支付", "哪哪-订单支付", orderId);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRechargeMessageEvent(MessageEvent event) {

        RechargeActivity.this.finish();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(RechargeActivity.this);
    }

    private String getTime() {//当前时间
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }

    private void showIncomeDialogs() {//身份证未认证提示
        DialogHelper.customAlert(RechargeActivity.this, "提示", "充值金额需要≥100元，充值金额可\n随时提现。", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {
            }
        }, null);
    }

}
