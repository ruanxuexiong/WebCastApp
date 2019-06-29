package com.android.nana.main;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2019/1/28.
 */

public class CoopActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private String mid;

    private String mCmpanyUrl, mPromotionUrl, mCityUrl;
    private String mCompanyStatus, mCityStatus, mPromotionStatus;
    private LinearLayout mEnterpriseServiceLl, mEnterpriseExtensionll, mCityll;
    private ImageView mEnterpriseServiceIv, mEnterpriseExtensionIv, mCityIv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_coop);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);

        mEnterpriseServiceLl = findViewById(R.id.ll_enterprise_service);
        mEnterpriseServiceIv = findViewById(R.id.iv_enterprise_service);
        mEnterpriseExtensionll = findViewById(R.id.ll_enterprise_extension);
        mEnterpriseExtensionIv = findViewById(R.id.iv_enterprise_extension);
        mCityll = findViewById(R.id.ll_city);
        mCityIv = findViewById(R.id.iv_city);
    }

    @Override
    protected void init() {
        mTitleTv.setText("合作通道");
        mBackTv.setVisibility(View.VISIBLE);
        mid = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        loadData(mid);
    }

    private void loadData(String mid) {
        HomeDbHelper.extensionUrl(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject url = new JSONObject(data.getString("url"));
                    JSONObject mStatus = new JSONObject(data.getString("status"));
                    if (result.getString("state").equals("0")) {
                        mCmpanyUrl = url.getString("apply_company_url");//企业账号申请url
                        mPromotionUrl = url.getString("apply_promotion_url");// 企业推广服务url
                        mCityUrl = url.getString("apply_city_url");//城市url

                        mCompanyStatus = mStatus.getString("apply_company");// 企业账号申请
                        mPromotionStatus = mStatus.getString("apply_promotion");//企业推广服务
                        mCityStatus = mStatus.getString("apply_city");//城市代理申请


                        if (mCompanyStatus.equals("1")) {
                            mEnterpriseServiceIv.setImageResource(R.drawable.icon_account_aut);
                        }
                        if (mPromotionStatus.equals("1")) {
                            mEnterpriseExtensionIv.setImageResource(R.drawable.icon_account_opening);
                        }
                        if (mCityStatus.equals("1")) {
                            mCityIv.setImageResource(R.drawable.icon_settled);
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
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mEnterpriseServiceLl.setOnClickListener(this);
        mEnterpriseExtensionll.setOnClickListener(this);
        mCityll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_enterprise_service://企业申请
                intent.setClass(CoopActivity.this, CommonActivity.class);
                intent.putExtra("title", "哪哪");
                intent.putExtra("url", mCmpanyUrl + mid);
                startActivity(intent);
                break;
            case R.id.ll_enterprise_extension://企业推广
                intent.setClass(CoopActivity.this, CommonActivity.class);
                intent.putExtra("title", "哪哪");
                intent.putExtra("url", mPromotionUrl + mid);
                startActivity(intent);
                break;
            case R.id.ll_city://城市服务
                intent.setClass(CoopActivity.this, CommonActivity.class);
                intent.putExtra("title", "哪哪");
                intent.putExtra("url", mCityUrl + mid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
