package com.android.nana.customer;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseListViewActivity;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.EducationAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class EducationActivity extends BaseListViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private ImageView mIvAdd;

    private String mUserId;
    private String mEndTime = "";
    private boolean mIsHideEdit;

    private EducationAdapter mAdapter;

    @Override
    protected EducationAdapter getBaseJsonAdapter() {
        mAdapter = new EducationAdapter(this, mIsHideEdit);
        return mAdapter;
    }

    @Override
    protected void initList() {
        mPageIndex = 1;
        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0) {
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount() - 1), "addTime", "");
        } else {
            mEndTime = "";
        }
        CustomerDbHelper.queryUserEducationExperience(mPageIndex, mPageSize, mUserId, mEndTime, mIOAuthCallBack);

    }

    @Override
    protected void bindViews() {

        if (null != getIntent().getStringExtra("userId")) {
            mUserId = getIntent().getStringExtra("userId");
        } else {
            mUserId = (String) SharedPreferencesUtils.getParameter(EducationActivity.this, "userId", "");
        }
        mIsHideEdit = getIntent().getBooleanExtra("IsHideEdit", false);
        setContentView(R.layout.education);

    }

    @Override
    protected void findViewById() {

        mIbBack =  findViewById(R.id.common_btn_back);
        mTxtTitle =  findViewById(R.id.common_txt_title);
        mTxtTitle.setText("教育经历");

        mIvAdd =  findViewById(R.id.education_iv_add);

        if (mIsHideEdit) {
            mIvAdd.setVisibility(View.GONE);
        } else {
            mIvAdd.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(EducationActivity.this, AddEducationActivity.class), 1);

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
