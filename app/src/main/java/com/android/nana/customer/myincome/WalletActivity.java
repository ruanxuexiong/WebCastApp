package com.android.nana.customer.myincome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.main.AuditedActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/7/10.
 */

public class WalletActivity extends BaseActivity implements View.OnClickListener {
    String state = "";
    private ImageButton mBtnBack;
    private TextView mTotalTv, mForwardTv;
    private TextView mRechargeTv;
    private LinearLayout mWalletLl;
    private LinearLayout mDetailedLl;
    private TextView mRechargeBalanceTv;
    private TextView mProfitBalanceTv;
    private LinearLayout mPaySetLl;
    private TextView mInfoTv;
    private String balance;
    private TextView wallet_tv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_wallet);
    }

    @Override
    protected void findViewById() {
        Intent intent = getIntent();
        state = intent.getStringExtra("state");
        mBtnBack = findViewById(R.id.btn_back);
        mTotalTv = findViewById(R.id.tv_total);
        mForwardTv = findViewById(R.id.tv_forward);
        mRechargeTv = findViewById(R.id.tv_recharge);
        mWalletLl = findViewById(R.id.ll_wallet);
        mDetailedLl = findViewById(R.id.ll_detailed);
        mRechargeBalanceTv = findViewById(R.id.tv_recharge_balance);
        mProfitBalanceTv = findViewById(R.id.tv_profit_balance);
        mPaySetLl = findViewById(R.id.ll_pay_set);
        mInfoTv = findViewById(R.id.tv_info);
        wallet_tv = findViewById(R.id.wallet_tv);

    }

    @Override
    protected void init() {
        showProgressDialog("", "加载中...");
        CustomerDbHelper.getMoneyWallet(BaseApplication.getInstance().getCustomerId(this), mIOAuthCallBack);
    }

    @Override
    protected void setListener() {
        mBtnBack.setOnClickListener(this);
        mPaySetLl.setOnClickListener(this);
        mWalletLl.setOnClickListener(this);
        mRechargeTv.setOnClickListener(this);
        mForwardTv.setOnClickListener(this);
        mDetailedLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.ll_pay_set:
                startActivity(new Intent(WalletActivity.this, PaySetActivity.class));
                break;
            case R.id.ll_wallet:
                //  JrmfClient.intentWallet(WalletActivity.this);
                //话费充值
                Intent inten2t = new Intent(WalletActivity.this, Prepaid_Activity.class);
                startActivity(inten2t);

                break;
            case R.id.tv_recharge:
                startActivity(new Intent(WalletActivity.this, RechargeActivity.class));
                break;
            case R.id.tv_forward:
                String mPayPwd = (String) SharedPreferencesUtils.getParameter(WalletActivity.this, "payPassword", "");
                if (null != mPayPwd && !"".equals(mPayPwd)) {
                    if ("3".equals(state)) {
                        ToastUtils.showToast("审核中请稍后重试！");
                    } else if ("1".equals(state)) {
                        Intent intent = new Intent(WalletActivity.this, MyIncomeWithdrawActivity.class);
                        //  intent.putExtra("state",state);
                        intent.putExtra("DirectBalance", balance);
                        startActivity(intent);
                    } else if ("2".equals(state)) {
                        //   startActivity(new Intent(WalletActivity.this, IDAuthenticationActivity.class));
                        showNormalDialog();
                        //   startActivity(new Intent(getActivity(), identity_homeActivity.class));
                    } else {
                        //       startActivity(new Intent(getActivity(), identity_homeActivity.class));
                        showNormalDialog();

                    }


                } else {
                    new AlertDialog.Builder(WalletActivity.this).setTitle("温馨提示").setMessage("请先设置提现密码！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    Intent intent = new Intent(WalletActivity.this, PaySetActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }

                            }).show();
                }

                break;
            case R.id.ll_detailed:
                startActivity(new Intent(WalletActivity.this, DetailedActivity.class));
                break;
            default:
                break;
        }
    }

    IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {
            dismissProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));

                mInfoTv.setText("*" + jsonObject1.getString("rateTit"));
                balance = jsonObject1.getString("totalBalance");//总提现金额
                mRechargeBalanceTv.setText(jsonObject1.getString("balance") + "元");//充值余额
                mProfitBalanceTv.setText(jsonObject1.getString("directBalance") + "元");//我的收益余额

//                double total = Double.parseDouble(jsonObject1.getString("balance")) + Double.parseDouble(jsonObject1.getString("directBalance"));
//                mTotalTv.setText(balance+"");
                if (balance.equals("0")||balance=="0")
                    mTotalTv.setText("0.00");
                else
                    mTotalTv.setText(balance);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            dismissProgressDialog();
        }
    };

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(WalletActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("根据《支付机构反洗钱和反恐怖融资管理办法》，个人身份证名下全部账号30天内累计收款、支付、充值、提现总金额超过5万元需要上传身份证照片，以完善身份资料。你已达到上传身份证照片的法规标准，请完成上传，以继续使用支付提现功能.");
        normalDialog.setPositiveButton("立即上传",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        startActivity(new Intent(WalletActivity.this, IDAuthenticationActivity.class));
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
}
