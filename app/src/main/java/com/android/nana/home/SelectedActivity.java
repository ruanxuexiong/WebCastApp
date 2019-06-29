package com.android.nana.home;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.VisitorRecordAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.UIUtil;
import com.bigkoo.convenientbanner.ConvenientBanner;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

public class SelectedActivity extends BaseListViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private View mHeadView;
    private ConvenientBanner mConvenientBanner;

    private VisitorRecordAdapter mAdapter;

    private String mEndTime;

    @Override
    protected VisitorRecordAdapter getBaseJsonAdapter() {
        mAdapter = new VisitorRecordAdapter(this);
        return mAdapter;
    }

    @Override
    protected void initList() {

        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0){
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount()-1), "addTime","");
        } else {
            mEndTime = "";
        }
        CustomerDbHelper.queryUserLists(this,mPageIndex, mPageSize, mEndTime, "1", mIOAuthCallBack);

    }

    @Override
    protected void bindViews() {

        setContentView(R.layout.common_list);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("精选专场");

        int mScreenWidth = mBaseApplication.getScreenWidth(this);
        mHeadView = getLayoutInflater().inflate(R.layout.selected_header, null);
        mConvenientBanner = (ConvenientBanner) mHeadView.findViewById(R.id.selected_header_cb_top);
        UIUtil.getInstance().setLayoutParams(mConvenientBanner, (int) (0.45 * mScreenWidth));

    }

    @Override
    public void locationData() {
        super.locationData();

        HomeDbHelper.lists("EXQUISITE_SPECIAL", new IOAuthCallBack() {

            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                if (mResultDetailModel.mIsSuccess){

                   // UserModel.doBanner(SelectedActivity.this, mConvenientBanner, JSONUtil.getList(mResultDetailModel.mJsonData, "pictures"));

                    mConvenientBanner.setPageIndicator(new int[] {com.android.common.R.mipmap.slide_no,com.android.common.R.mipmap.slide_yes});

                    mListView.addHeaderView(mHeadView);
                }

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg0) {

                JSONObject object = (JSONObject) parent.getItemAtPosition(position);

                String id = object.optString("id");
                if (!BaseApplication.getInstance().getCustomerId(SelectedActivity.this).equals(id)) {
                    Intent intent = new Intent(SelectedActivity.this, EditDataActivity.class);
                    intent.putExtra("UserId", id);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SelectedActivity.this, CustomerDetailActivity.class));
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConvenientBanner.stopTurning();
    }
}
