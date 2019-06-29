package com.android.nana.customer.myincome;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.pay.AliPayBuilder;
import com.android.common.pay.WxPaymentBuilder;
import com.android.nana.R;
import com.android.nana.util.Constant;
import com.android.nana.util.pay.AliPaymentBuilder;


/**
 * Created by Cristina on 2017/3/23.
 */
public class MyIncomeRechargeActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private EditText mEtReharge;

    private RadioGroup mRgPay;
    private TextView mTxtCommit;
    private String mPayType = "PAY_WITH_WECHAT";

    private AliPayBuilder mAliPayBuilder;
    private WxPaymentBuilder mWxPaymentBuilder;

    private AliPaymentBuilder mAliPaymentBuilder;

    private boolean mIsAnchor;
    private String mUserId, mMoney;

    @Override
    protected void bindViews() {

        mUserId = getIntent().getStringExtra("UserId");
        mMoney = getIntent().getStringExtra("Money");
        mIsAnchor = getIntent().getBooleanExtra("IsAnchor", false);
        setContentView(R.layout.activity_my_income_recharge);

    }

    @Override
    protected void findViewById() {
        mIbBack =  findViewById(R.id.common_btn_back);
        mTxtTitle =  findViewById(R.id.common_txt_title);
        mTxtTitle.setText("充值");

        mEtReharge =  findViewById(R.id.my_income_et_recharge);
        mRgPay = (RadioGroup) findViewById(R.id.my_income_rg_pay);
        mTxtCommit = (TextView) findViewById(R.id.my_income_txt_commit);
    }

    @Override
    protected void init() {

        /*if (mIsAnchor) {
            mEtReharge.setText(mMoney);
        }*/

        mAliPayBuilder = new AliPayBuilder(this);
//        mAliPayBuilder.build();

        mAliPayBuilder.registerAliConfig(Constant.Payment.AliPay.Partner,
                Constant.Payment.AliPay.Seller, Constant.Payment.AliPay.RsaPrivate);

        mWxPaymentBuilder = new WxPaymentBuilder(this);

        mAliPaymentBuilder = new AliPaymentBuilder(this);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mRgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.my_income_rb_pay_wx: // 微信
                        mPayType = "PAY_WITH_WECHAT";
                        break;
                    case R.id.my_income_rb_pay_ali: // 支付宝
                        mPayType = "PAY_WITH_ALIPAY";
                        break;
                }

            }
        });

        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recharge();


            }
        });

    }

    private void recharge() {

        String money = mEtReharge.getText().toString().trim();
        if (TextUtils.isEmpty(money) || Double.parseDouble(money) <= 0) {
            showToast("请输入有效的金额");
            return;
        }
        if (TextUtils.isEmpty(mPayType)) {
            showToast("请选择支付方式");
            return;
        }

     /*   MyIncomeModel.recharge(this, money, mPayType, new MyIncomeModel.OnMyIncomeLinstener() {
            @Override
            public void doWxPay(String money, String orderId) {

                if (ForAllUtils.isWeixinAvilible(MyIncomeRechargeActivity.this)) {
                    mWxPaymentBuilder.registerAppInfo(Constant.Payment.WxPay.APP_ID, Constant.Payment.WxPay.API_KEY, Constant.Payment.WxPay.MCH_ID)
                            .registerNotifyUrl(Constant.Payment.WxPay.NotifyUrl)
                            .registerOrderDescription("三分钟-订单支付")
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
                mAliPaymentBuilder.payV2(money, "三分钟-订单支付", "三分钟-订单支付", orderId);
            }
        });*/

    }

}
