package com.android.nana.customer.myincome;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.MyIncomeWithdrawAdapter;
import com.android.nana.dbhelper.MyIncomeHelper;

import org.json.JSONObject;

/**
 * Created by Cristina on 2017/3/22.
 */
public class MyIncomeWithdrawSelectBankActivity extends BaseListViewActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;
    private TextView mIbRight;
    private MyIncomeWithdrawAdapter mAdapter;
    private ImageView img_empty;


    @Override
    protected void bindViews() {

        setContentView(R.layout.activity_my_income_withdraw);
    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtTitle.setText("选择银行卡");
        mIbRight = findViewById(R.id.toolbar_right_2);
        Drawable drawable = this.getResources().getDrawable(R.drawable.icon_add);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIbRight.setCompoundDrawables(drawable, null, null, null);
        mIbRight.setVisibility(View.VISIBLE);
        mIbBack.setVisibility(View.VISIBLE);
        img_empty = findViewById(R.id.img_empty);
        img_empty.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initOther() {
        super.initOther();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.grey_e6)));
        mListView.setDividerHeight(1);
        mTxtLoad.setText("添加银行卡");
    }

    @Override
    protected MyIncomeWithdrawAdapter getBaseJsonAdapter() {
        mAdapter = new MyIncomeWithdrawAdapter(this);
        return mAdapter;
    }

    @Override
    protected void initList() {

        MyIncomeHelper.queryUserBankCard(BaseApplication.getInstance().getCustomerId(this), mIOAuthCallBack);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mIbRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(MyIncomeWithdrawSelectBankActivity.this, MyIncomeAddBankCartActivity.class), 1);

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {

                JSONObject object = (JSONObject) parent.getItemAtPosition(position);

                Intent intent = new Intent(MyIncomeWithdrawSelectBankActivity.this, MyIncomeWithdrawSelectBankActivity.class);
                intent.putExtra("Json", object.toString());
                setResult(RESULT_OK, intent);
                MyIncomeWithdrawSelectBankActivity.this.finish();

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject object = (JSONObject) parent.getItemAtPosition(position);
                final String bankCardId = JSONUtil.get(object, "id", "");
                DialogHelper.customAlert(MyIncomeWithdrawSelectBankActivity.this, "提示", "确定要删除该银行卡吗?", new DialogHelper.OnAlertConfirmClick() {
                    @Override
                    public void OnClick(String content) {

                    }

                    @Override
                    public void OnClick() {

                        MyIncomeHelper.deleteBankCard(bankCardId, new IOAuthCallBack() {
                            @Override
                            public void onStartRequest() {

                                UIHelper.showOnLoadingDialog(MyIncomeWithdrawSelectBankActivity.this, "正在处理，请稍后");
                            }

                            @Override
                            public void getSuccess(String successJson) {

                                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                                if (mResultDetailModel.mIsSuccess) {

                                    mBaseJsonAdapter.clear();
                                    mPageIndex = 1;
                                    initList();

                                } else {
                                    showToast(mResultDetailModel.mMessage);
                                }
                                UIHelper.hideOnLoadingDialog();
                            }

                            @Override
                            public void getFailue(String failueJson) {

                                showToast("获取数据失败，请稍后重试");
                                UIHelper.hideOnLoadingDialog();

                            }
                        });

                    }
                }, null);
                return true;
            }
        });

        mTxtLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MyIncomeWithdrawSelectBankActivity.this, MyIncomeAddBankCartActivity.class), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            mAdapter.clear();
            initList();
        }
    }

}
