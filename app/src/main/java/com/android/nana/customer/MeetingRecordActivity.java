package com.android.nana.customer;

import android.graphics.drawable.ColorDrawable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewActivity;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.MeetingRecordAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class MeetingRecordActivity extends BaseListViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private String mEndTime;

    private MeetingRecordAdapter mAdapter;
    private MultipleStatusView mMultipleStatusView;

    @Override
    protected MeetingRecordAdapter getBaseJsonAdapter() {
        mAdapter = new MeetingRecordAdapter(this);
        return mAdapter;
    }

    @Override
    protected void initList() {


        if (NetWorkUtils.isNetworkConnected(MeetingRecordActivity.this)) {


            if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0) {
                mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount() - 1), "addTime", "");
            } else {
                mEndTime = "";
            }
            CustomerDbHelper.queryUserInvitationRecord(this, mPageIndex, mPageSize, mEndTime,
                    BaseApplication.getInstance().getCustomerId(this), "SEE_RECORD", "", mIOAuthCallBack);
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    protected void bindViews() {

        setContentView(R.layout.meeting_record);

    }

    @Override
    protected void initOther() {
        super.initOther();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.grey_e6)));
        mListView.setDividerHeight(1);
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);

        mTxtTitle.setText("见面记录");

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

    }
}
