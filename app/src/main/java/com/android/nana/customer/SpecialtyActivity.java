package com.android.nana.customer;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseListViewActivity;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.SpecialtyAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class SpecialtyActivity extends BaseListViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private ImageView mIvAdd;

    private String mUserId;
    private String mEndTime = "";
    private boolean mIsHideEdit;

    private SpecialtyAdapter mAdapter;

    @Override
    protected SpecialtyAdapter getBaseJsonAdapter() {
        mAdapter = new SpecialtyAdapter(this, mIsHideEdit);
        return mAdapter;
    }

    @Override
    protected void initList() {

        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0){
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount()-1), "addTime","");
        } else {
            mEndTime = "";
        }

        if (null != getIntent().getStringExtra("userid")){
            CustomerDbHelper.queryUserPersonalExpertise(mPageIndex, mPageSize, getIntent().getStringExtra("userid"), mEndTime, mIOAuthCallBack);
        }else {
            CustomerDbHelper.queryUserPersonalExpertise(mPageIndex, mPageSize, mUserId, mEndTime, mIOAuthCallBack);
        }


    }

    @Override
    protected void bindViews() {

        mUserId = (String) SharedPreferencesUtils.getParameter(SpecialtyActivity.this, "userId", "");
        mIsHideEdit = getIntent().getBooleanExtra("IsHideEdit", false);
        setContentView(R.layout.specialty);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("个人标签");

        mIvAdd = (ImageView) findViewById(R.id.specialty_iv_add);

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

                startActivityForResult(new Intent(SpecialtyActivity.this, AddSpecialtyActivity.class),1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1){
            mAdapter.clear();
            initList();
        }
    }
}
