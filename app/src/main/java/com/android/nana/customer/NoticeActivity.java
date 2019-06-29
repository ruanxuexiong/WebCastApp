package com.android.nana.customer;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.NoticeEntity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.ui.SwitchButtonView;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class NoticeActivity extends BaseActivity {
    private TextView mIbBack;
    private TextView mTxtTitle;
    private String uid;
    private SwitchButtonView switchButton;
    private Gson gson = new Gson();

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_notice);
    }

    @Override
    protected void findViewById() {
        switchButton = findViewById(R.id.switch_btn);
        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtTitle.setText("通知设置");
        mIbBack.setVisibility(View.VISIBLE);
        if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
        }
        getNotice(uid);
    }

    @Override
    protected void init() {
        switchButton.builder();
    }

    @Override
    protected void setListener() {
        switchButton.setOnUITableClickLister(new SwitchButtonView.UITableClickLister() {
            @Override
            public void onClick(String type, String status, int item) {
                setNotice(uid, type, item, status);
            }
        });
        mIbBack.setOnClickListener(mBackPullListener);
    }

    public void setNotice(String userId, String type, final int position, final String status) {
        WebCastDbHelper.setNotice(userId, type, status, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    ToastUtils.showToast(jsonObject.getJSONObject("result").getString("description"));
//                    if (jsonObject.getJSONObject("result").getInt("state") == 0)
//                        switchButton.upDataSuccess(position, status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast(failueJson);
            }
        });
    }

    public void getNotice(String userId) {
        WebCastDbHelper.getNotice(userId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中...");
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                NoticeEntity noticeEntity = gson.fromJson(successJson, NoticeEntity.class);
                if (noticeEntity.getResult().getState() == 0) {
                 //   ToastUtils.showToast(noticeEntity.getResult().getDescription());
                    switchButton.setData(noticeEntity);
                } else {
                    ToastUtils.showToast(noticeEntity.getResult().getDescription());
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }
}
