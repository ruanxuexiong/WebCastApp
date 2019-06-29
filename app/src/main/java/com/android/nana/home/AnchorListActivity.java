package com.android.nana.home;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewActivity;
import com.android.nana.R;
import com.android.nana.adapter.VisitorRecordAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class AnchorListActivity extends BaseListViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private String mUserId;
    private String mBeginAge = "0";
    private String mEndAge = "0";
    private String mPurposeId = "0";
    private String mProvinceId = "0";
    private String mGender = "0";
    private String mOrderByType = "comprehensive";

    private VisitorRecordAdapter mAdapter;

    @Override
    protected VisitorRecordAdapter getBaseJsonAdapter() {
        mAdapter = new VisitorRecordAdapter(this);
        return mAdapter;
    }

    @Override
    protected void initList() {

        CustomerDbHelper.queryUserLists(this, 1, mPageIndex, mPageSize, mUserId, "0",
                mBeginAge, mEndAge, mGender, mPurposeId, mProvinceId, mOrderByType, mIOAuthCallBack);

    }

    @Override
    protected void bindViews() {

        mUserId = BaseApplication.getInstance().getCustomerId(this);
        mBeginAge = getIntent().getStringExtra("BeginAge");
        mEndAge = getIntent().getStringExtra("EndAge");
        mGender = getIntent().getStringExtra("Gender");
        mPurposeId = getIntent().getStringExtra("PurposeId");
        mProvinceId = getIntent().getStringExtra("ProvinceId");
        mOrderByType = getIntent().getStringExtra("OrderByType");

        setContentView(R.layout.common_list);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("筛选");

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             /*   JSONObject obj = (JSONObject) parent.getItemAtPosition(position);
                Intent intent = new Intent(AnchorListActivity.this, AnchorDetailActivity.class);
                intent.putExtra("UserId", obj.optString("id"));
                intent.putExtra("Number", obj.optString("user_nicename"));
                startActivity(intent);*/

            }
        });

    }
}
