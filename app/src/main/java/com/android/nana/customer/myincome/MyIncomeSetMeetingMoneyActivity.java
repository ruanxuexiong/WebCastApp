package com.android.nana.customer.myincome;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.InputFilterMinMax;
import com.android.nana.widget.MoneyEditText;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Cristina on 2017/3/23.
 */
public class MyIncomeSetMeetingMoneyActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private MoneyEditText mMoneyEt;

    private TextView mTxtCommit;
    private String money;

    @Override
    protected void bindViews() {

        setContentView(R.layout.activity_my_income_set_meeting_money);

    }

    @Override
    protected void findViewById() {
        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("设置见面金额");

        mMoneyEt = findViewById(R.id.my_income_set_meeting_money_et_recharge);
        mTxtCommit = (TextView) findViewById(R.id.my_income_set_meeting_money_txt_commit);

        if (null != getIntent().getStringExtra("money")) {
            money = getIntent().getStringExtra("money");
            mMoneyEt.setText(money);
            mMoneyEt.requestFocus();
        }
        mMoneyEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recharge();

            }
        });

    }

    private void recharge() {

        String money = mMoneyEt.getText().toString().trim();
        if (TextUtils.isEmpty(money) || Double.parseDouble(money) <= 0) {
            showToast("请输入有效的金额");
            return;
        } else if (Double.parseDouble(money) > 1000000) {
            ToastUtils.showToast("见面金额不能大于1百万");
        } else {
            MyIncomeHelper.setSeeMoney(BaseApplication.getInstance().getCustomerId(this), money, new IOAuthCallBack() {

                @Override
                public void onStartRequest() {
                    UIHelper.showOnLoadingDialog(MyIncomeSetMeetingMoneyActivity.this);
                }

                @Override
                public void getSuccess(String successJson) {

                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    if (mResultDetailModel.mIsSuccess) {
                        EventBus.getDefault().post(new MessageEvent("money"));
                        MyIncomeSetMeetingMoneyActivity.this.finish();
                    }
                    UIHelper.showToast(MyIncomeSetMeetingMoneyActivity.this, mResultDetailModel.mMessage);
                    UIHelper.hideOnLoadingDialog();

                }

                @Override
                public void getFailue(String failueJson) {

                    UIHelper.showToast(MyIncomeSetMeetingMoneyActivity.this, "获取数据失败，请稍后重试");
                    UIHelper.hideOnLoadingDialog();
                }
            });
        }

    }

}
