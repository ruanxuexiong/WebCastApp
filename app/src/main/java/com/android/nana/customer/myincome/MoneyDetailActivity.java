package com.android.nana.customer.myincome;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.ui.RoundImageView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/7/17.
 */

public class MoneyDetailActivity extends BaseActivity implements View.OnClickListener {

    private String id, mid;
    private TextView mTitleTv;
    private TextView mBackTv;
    private MoneyDetailBean item;
    private LinearLayout mCompanyLl;
    private LinearLayout mMoneyLl;
    private RoundImageView mPictureIv, mAvatarIv;
    private ImageView mAutIv;
    private TextView mNnameTv, mPositionTv, mTotalNameTv;
    private TextView mMoneyNameTv, mMoneyNumTv;
    private TextView mMoneyStateTv, mMoneyNumberTv;
    private TextView mTimeStateTv, mTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_money_detail);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);

        mCompanyLl = findViewById(R.id.ll_company);
        mMoneyLl = findViewById(R.id.ll_money);
        mPictureIv = findViewById(R.id.iv_picture);
        mAutIv = findViewById(R.id.iv_aut);
        mNnameTv = findViewById(R.id.tv_name);
        mPositionTv = findViewById(R.id.tv_position);
        mTotalNameTv = findViewById(R.id.tv_total_name);
        mAvatarIv = findViewById(R.id.iv_avatar);
        mMoneyNameTv = findViewById(R.id.tv_money_name);
        mMoneyNumTv = findViewById(R.id.tv_money_num);
        mMoneyStateTv = findViewById(R.id.tv_money_state);
        mTimeStateTv = findViewById(R.id.tv_time_state);
        mTimeTv = findViewById(R.id.tv_time);
        mMoneyNumberTv = findViewById(R.id.tv_money_number);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("收支明细");

        id = getIntent().getStringExtra("id");
        mid = getIntent().getStringExtra("mid");
        showProgressDialog("", "加载中...");
        initData(mid, id);
    }


    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    private void initData(String mid, String id) {
        CustomerDbHelper.getDetailData(mid, id, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (null != parseData(successJson)) {
                            item = parseData(successJson);
                            if (item.getType().equals("1")) {//支出
                                mCompanyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mPictureIv);
                                if (item.getUsers().getStatus().equals("1")) {
                                    mAutIv.setVisibility(View.VISIBLE);
                                } else {
                                    mAutIv.setVisibility(View.GONE);
                                }
                                mNnameTv.setText(item.getUsers().getUsername());
                                if ("发出动态红包".equals(item.getUsers().getUsername())){
                                    mAutIv.setVisibility(View.GONE);
                                }
                                if (!"".equals(item.getUsers().getCompany()) && !"".equals(item.getUsers().getPosition())) {
                                    mPositionTv.setText(item.getUsers().getPosition() + " | " + item.getUsers().getCompany());
                                } else if (!"".equals(item.getUsers().getCompany())) {
                                    mPositionTv.setText(item.getUsers().getCompany());
                                } else if (!"".equals(item.getUsers().getPosition())) {
                                    mPositionTv.setText(item.getUsers().getPosition());
                                } else {
                                    mPositionTv.setVisibility(View.GONE);
                                }
                                mMoneyNameTv.setText("消费金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("到账时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());

                            } else if (item.getType().equals("2")) {//收入

                                mCompanyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mPictureIv);
                                if (item.getUsers().getStatus().equals("1")) {
                                    mAutIv.setVisibility(View.VISIBLE);
                                } else {
                                    mAutIv.setVisibility(View.GONE);
                                }
                                mNnameTv.setText(item.getUsers().getUsername());
                                if ("收入动态红包".equals(item.getUsers().getUsername())){
                                    mAutIv.setVisibility(View.GONE);
                                }
                                if (!"".equals(item.getUsers().getCompany()) && !"".equals(item.getUsers().getPosition())) {
                                    mPositionTv.setText(item.getUsers().getPosition() + " | " + item.getUsers().getCompany());
                                } else if (!"".equals(item.getUsers().getCompany())) {
                                    mPositionTv.setText(item.getUsers().getCompany());
                                } else if (!"".equals(item.getUsers().getPosition())) {
                                    mPositionTv.setText(item.getUsers().getPosition());
                                } else {
                                    mPositionTv.setVisibility(View.GONE);
                                }
                                mMoneyNameTv.setText("收入金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("到账时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());

                            } else if (item.getType().equals("3")) {//充值
                                mMoneyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mAvatarIv);
                                mTotalNameTv.setText(item.getUsers().getUsername());
                                mMoneyNameTv.setText("充值金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("充值时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());

                            } else if (item.getType().equals("4")) {//提现
                                mMoneyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mAvatarIv);
                                mTotalNameTv.setText(item.getUsers().getUsername());
                                mMoneyNameTv.setText("提现金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("提现时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());
                            }
                            else if (item.getType().equals("5")) {//冻结金额
                                mMoneyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mAvatarIv);
                                mTotalNameTv.setText(item.getUsers().getUsername());
                                mMoneyNameTv.setText("冻结金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("冻结时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());
                            }
                            else if (item.getType().equals("6")) {//解冻金额
                                mMoneyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mAvatarIv);
                                mTotalNameTv.setText(item.getUsers().getUsername());
                                mMoneyNameTv.setText("解冻金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("解冻时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());
                            }
                            else if (item.getType().equals("7")) {//充值金额
                                mMoneyLl.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(item.getUsers().getAvatar(), mAvatarIv);
                                mTotalNameTv.setText(item.getUsers().getUsername());
                                mMoneyNameTv.setText("充值金额");
                                mMoneyNumTv.setText(item.getCharge());
                                mMoneyStateTv.setText(item.getPayStatus());
                                mTimeStateTv.setText("充值时间");
                                mTimeTv.setText(item.getDate());
                                mMoneyNumberTv.setText(item.getTranId());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    private MoneyDetailBean parseData(String result) {//Gson 解析
        MoneyDetailBean entity = new MoneyDetailBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), MoneyDetailBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

}
