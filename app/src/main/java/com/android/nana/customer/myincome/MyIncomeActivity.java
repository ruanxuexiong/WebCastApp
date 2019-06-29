package com.android.nana.customer.myincome;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.customer.UpdatePassActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.login.ForgetPassActivity;
import com.android.nana.ui.UITableView;
import com.android.nana.util.Constant;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cristina on 2017/3/21.
 */
public class MyIncomeActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mMoneyTv, mIncomeMoney, mTotalTv;

    private String mPayPassword;

    private UITableView table;

    private String balance;

    @Override
    protected void bindViews() {

        setContentView(R.layout.activity_my_income);

    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(R.id.common_btn_back);
        mTxtTitle = findViewById(R.id.common_txt_title);
        mTxtTitle.setText("我的钱包");
        mMoneyTv = findViewById(R.id.tv_money);
        mIncomeMoney = findViewById(R.id.tv_income_money);
        mTotalTv = findViewById(R.id.my_income_txt_total_income_info);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressDialog("", "加载中请稍后...");
        mPayPassword = (String) SharedPreferencesUtils.getParameter(this, "payPassword", "");

        table = findViewById(R.id.my_income_ll_tableview);
        table.clearView();
        table.addBasicItem(R.drawable.me_red, "我的红包", "");
        table.addBasicItem(R.drawable.ic_detailed, "收支明细", "", true);
        table.addBasicItem(R.drawable.ic_balance, "我的充值余额", "", false, true);
        table.addBasicItem(R.drawable.ic_shou, "我的收益余额", "", false, true);
        table.addBasicItem(R.drawable.ic_recharge, "余额充值", "");
        table.addBasicItem(R.drawable.ic_profit, "提现", "");

        if (TextUtils.isEmpty(mPayPassword)) {
            table.addBasicItem(R.drawable.ic_update, "设置提现密码", "", true);
        }
        table.setOnUITableClickLister(new UITableView.UITableClickLister() {
            @Override
            public void onClick(int index) {

                switch (index) {
                    case 0: // 我的红包
                        //JrmfClient.intentWallet(MyIncomeActivity.this);

                        break;
                    case 1: //我的收益
                        startActivity(new Intent(MyIncomeActivity.this, MyIncomeStatisticsActivity.class));
                        break;
                    case 2:// 我的充值余额
                        break;
                    case 3:// 我的收益余额


                        break;
                    case 4:
                        startActivity(new Intent(MyIncomeActivity.this, RechargeActivity.class));

                        break;

                    case 5: // 收益提现

                        Intent intent = new Intent(MyIncomeActivity.this, MyIncomeWithdrawActivity.class);
                        intent.putExtra("DirectBalance", balance);
                        startActivity(intent);
                        break;
                    case 6:// 设置提现密码
                        String mobile = (String) SharedPreferencesUtils.getParameter(MyIncomeActivity.this, "mobile", "");
                        if (TextUtils.isEmpty(mobile)) {
                            DialogHelper.customAlert(MyIncomeActivity.this, "提示", "检测到您未绑定手机号，确定要前去绑定吗?", new DialogHelper.OnAlertConfirmClick() {
                                @Override
                                public void OnClick(String content) {

                                }

                                @Override
                                public void OnClick() {

                                    Intent intent = new Intent(MyIncomeActivity.this, ForgetPassActivity.class);
                                    intent.putExtra("IsUpdatePassword", Constant.UpdatePassword.BindPhoneNumber);
                                    startActivity(intent);

                                }
                            }, null);
                            return;
                        }
                        Intent i = new Intent(MyIncomeActivity.this, ForgetPassActivity.class);
                        i.putExtra("IsUpdatePassword", Constant.UpdatePassword.SetUpWithdrawPass);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });
        table.builder();

        UITableView table2 = findViewById(R.id.my_income_ll_tableview2);
        table2.clearView();
        if (TextUtils.isEmpty(mPayPassword)) {
            table2.setVisibility(View.GONE);
        } else {
            table2.setVisibility(View.VISIBLE);
        }
        table2.addBasicItem(R.drawable.ic_update, "修改提现密码", "");
        table2.addBasicItem(R.drawable.ic_cackpwd, "找回提现密码", "");
        table2.setOnUITableClickLister(new UITableView.UITableClickLister() {
            @Override
            public void onClick(int index) {

                switch (index) {
                    case 0: // 修改提现密码
                        Intent intent = new Intent(MyIncomeActivity.this, UpdatePassActivity.class);
                        intent.putExtra("IsUpdatePayPassword", true);
                        startActivity(intent);
                        break;
                    case 1: // 找回提现密码
                        Intent i = new Intent(MyIncomeActivity.this, ForgetPassActivity.class);
                        i.putExtra("IsUpdatePassword", Constant.UpdatePassword.UpdateWithdrawPass);
                        startActivity(i);
                        break;
                }
            }
        });
        table2.builder();

        CustomerDbHelper.getMoneyWallet(BaseApplication.getInstance().getCustomerId(this), mIOAuthCallBack);

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

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

                mMoneyTv.setText(jsonObject1.getString("totalDirectBalance"));
                mIncomeMoney.setText(jsonObject1.getString("totalMoney"));

                //  double num = Double.valueOf(jsonObject1.getString("rate"));
                //double result = num * 100;

                mTotalTv.setText(jsonObject1.getString("rateTit"));
                balance = jsonObject1.getString("totalBalance");//总提现金额
                table.getRowView(2).setText(Html.fromHtml("<font color='#F34551'>" + jsonObject1.getString("balance") + "元" + "</font>"));//充值余额
                table.getRowView(3).setText(Html.fromHtml("<font color='#F34551'>" + jsonObject1.getString("directBalance") + "元" + "</font>"));//我的收益余额
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(MyIncomeActivity.this, "获取数据失败，请稍后重试");
            dismissProgressDialog();

        }
    };
}
