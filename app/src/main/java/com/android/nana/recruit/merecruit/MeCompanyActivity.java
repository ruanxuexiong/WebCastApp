package com.android.nana.recruit.merecruit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.recruit.bean.CompanyInfoEntity;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by lenovo on 2018/3/26.
 */

public class MeCompanyActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private RoundedImageView mLogoIv;
    private TextView mNameTv;
    private TextView mIndustryTv;
    private TextView mScaleTv;
    private TextView mTradeTv;
    private TextView mStageTv;
    private TextView mWebTv;
    private LinearLayout mRecruitLl;
    private Button mEditBtn;
    private String mid, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            initData();
        }
    }

    private void initData() {
        RecruitDbHelper.companyInfo(mid, id, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        CompanyInfoEntity.Info entity = parseData(successJson);

                        mIndustryTv.setText(entity.getProfession());
                        mNameTv.setText(entity.getCompany());
                        mTradeTv.setText(entity.getProfession());
                        mScaleTv.setText(entity.getScale());
                        mStageTv.setText(entity.getFinace());
                        mWebTv.setText(entity.getWebsite());
                        ImageLoader.getInstance().displayImage(entity.getLogo(), mLogoIv);
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
    protected void bindViews() {
        setContentView(R.layout.activity_me_company);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mLogoIv = findViewById(R.id.iv_logo);

        mNameTv = findViewById(R.id.tv_name);
        mIndustryTv = findViewById(R.id.tv_industry);
        mScaleTv = findViewById(R.id.tv_scale);
        mTradeTv = findViewById(R.id.tv_trade);
        mStageTv = findViewById(R.id.tv_stage);
        mWebTv = findViewById(R.id.tv_web);
        mRecruitLl = findViewById(R.id.ll_recruit);
        mEditBtn = findViewById(R.id.btn_edit);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我的公司");
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRecruitLl.setOnClickListener(this);
        mEditBtn.setOnClickListener(this);
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


    private CompanyInfoEntity.Info parseData(String result) {//Gson 解析
        CompanyInfoEntity.Info entity = new CompanyInfoEntity.Info();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONObject info = new JSONObject(data.getString("info"));
            Gson gson = new Gson();
            entity = gson.fromJson(info.toString(), CompanyInfoEntity.Info.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
