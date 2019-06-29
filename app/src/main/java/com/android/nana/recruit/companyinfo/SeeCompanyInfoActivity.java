package com.android.nana.recruit.companyinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.recruit.bean.CompanyInfoEntity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/3/27.
 */

public class SeeCompanyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private String mid, id;

    private TextView mCompanyNameTv;
    private TextView mTradeTv;
    private TextView mScaleTv;
    private TextView mStageTv;
    private TextView mWebTv;
    private ImageView mLogoIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            initData();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_see_company_info);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mCompanyNameTv = findViewById(R.id.tv_company_name);

        mTradeTv = findViewById(R.id.tv_trade);
        mScaleTv = findViewById(R.id.tv_scale);
        mStageTv = findViewById(R.id.tv_stage);
        mWebTv = findViewById(R.id.tv_web);
        mLogoIv = findViewById(R.id.iv_logo);
    }

    @Override
    protected void init() {
        mTitleTv.setText("公司信息");
        mBackTv.setVisibility(View.VISIBLE);
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

                        mCompanyNameTv.setText(entity.getCompany());
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
    protected void setListener() {
        mBackTv.setOnClickListener(this);
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
        CompanyInfoEntity.Info entity = new  CompanyInfoEntity.Info();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONObject info = new JSONObject(data.getString("info"));
            Gson gson = new Gson();
            entity = gson.fromJson(info.toString(),  CompanyInfoEntity.Info.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
