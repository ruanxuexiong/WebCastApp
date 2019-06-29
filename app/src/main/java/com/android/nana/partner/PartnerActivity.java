package com.android.nana.partner;

import android.app.Dialog;
import android.graphics.Paint;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by THINK on 2017/7/6.
 */

public class PartnerActivity extends BaseActivity implements View.OnClickListener {

    private TextView mActivityTv, mBillingTv, mTitle, mProfitTv, mNumTv, mBtnTv;
    private ImageButton mBackBtn;
    private String mUid;

    private Dialog allMsg, mActivitDialog;
    private View allMsgView, mActivityView;
    private String mUserName, mAvatar, mIntroduce, mMsg, mUrl;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_partner);
    }

    @Override
    protected void findViewById() {
        mActivityTv =  findViewById(R.id.tv_billing);
        mBillingTv =  findViewById(R.id.tv_activity);
        mTitle =  findViewById(R.id.common_txt_title);
        mBackBtn =  findViewById(R.id.common_btn_back);
        mBtnTv =  findViewById(R.id.tv_btn);
        mNumTv =  findViewById(R.id.tv_num);
        mProfitTv =  findViewById(R.id.tv_profit);


        mTitle.setText("合伙人计划");
        mActivityTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mBillingTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("mid")) {
            mUid = getIntent().getStringExtra("mid");
            loadData(mUid);
        }

        allMsgView = LayoutInflater.from(this).inflate(R.layout.billing_dialog, null);
        allMsg = new AlertDialog.Builder(this).create();
        allMsg.setCanceledOnTouchOutside(false);

        mActivityView = LayoutInflater.from(this).inflate(R.layout.partner_activity_dialog, null);
        mActivitDialog = new AlertDialog.Builder(this).create();
        mActivitDialog.setCanceledOnTouchOutside(false);

        ImageButton imgBtn_dialog =  allMsgView.findViewById(R.id.dialog_pre_entry_close);
        imgBtn_dialog.setOnClickListener(this);

        ImageButton imageButton =  mActivityView.findViewById(R.id.close_iv);
        imageButton.setOnClickListener(this);
    }

    private void loadData(String mUid) {

        WebCastDbHelper.userInfo(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mProfitTv.setText(jsonObject1.getString("balance") + "元");
                        mNumTv.setText(jsonObject1.getString("num") + "人");

                        mUserName = jsonObject1.getString("username");
                        mAvatar = jsonObject1.getString("avatar");
                        mIntroduce = jsonObject1.getString("introduce");
                        mMsg = jsonObject1.getString("message");
                        mUrl = jsonObject1.getString("shareUrl");
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
        mBackBtn.setOnClickListener(this);
        mBtnTv.setOnClickListener(this);

        mBillingTv.setOnClickListener(this);
        mActivityTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.tv_btn:
                share();
                break;
            case R.id.tv_billing:
                allMsg.show();
                allMsg.getWindow().setContentView(allMsgView);
                break;
            case R.id.tv_activity:
                mActivitDialog.show();
                mActivitDialog.getWindow().setContentView(mActivityView);
                break;
            case R.id.dialog_pre_entry_close:
                allMsg.dismiss();
                break;
            case R.id.close_iv:
                mActivitDialog.dismiss();
                break;
        }
    }

    private void share() {
        FragmentManager fm = getSupportFragmentManager();
        PartnerFragment dialog = PartnerFragment.newInstance(mUid, mUserName, mAvatar, mIntroduce, mMsg, mUrl);
        dialog.show(fm, "fragment_bottom_dialog");
    }
}
