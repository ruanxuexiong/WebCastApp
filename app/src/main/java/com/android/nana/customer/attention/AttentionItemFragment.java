package com.android.nana.customer.attention;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.common.base.BaseListViewFragment;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.VisitorRecordAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.listener.AttentionListener;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class AttentionItemFragment extends BaseListViewFragment {

    private VisitorRecordAdapter mAdapter;
    private String mUserId;
    private String mEndTime;
    private String mColumnId;
    private String mType;

    public static AttentionItemFragment newInstance(String columnId, String type) {
        AttentionItemFragment f = new AttentionItemFragment();
        Bundle b = new Bundle();
        b.putString("ColumnId", columnId);
        b.putString("Type", type);
        f.setArguments(b);
        return f;
    }

    @Override
    protected VisitorRecordAdapter getBaseJsonAdapter() {
        mAdapter = new VisitorRecordAdapter(getActivity());
        return mAdapter;
    }

    @Override
    protected void initList() {

        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0) {
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount() - 1), "addTime", "");
        } else {
            mEndTime = "";
        }
        CustomerDbHelper.queryAttentionUserLists(mPageIndex, mPageSize, mEndTime, mUserId, mColumnId, mType, mIOAuthCallBack);

    }

    @Override
    protected int getLayoutId() {

        Bundle args = getArguments();

        if (args != null) {
            mColumnId = args.getString("ColumnId");
            mType = args.getString("Type");
        }

        mUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");

        return R.layout.common_listview;
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject obj = (JSONObject) parent.getItemAtPosition(position);
                JSONObject user = JSONUtil.getJsonObject(obj, "user");
                Intent intent = new Intent(getActivity(), EditDataActivity.class);
                intent.putExtra("UserId", user.optString("id"));
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onCompletedList(String json) {
        super.onCompletedList(json);

        JSONObject jsonObject = JSONUtil.getStringToJson(json);

        String attentionMyUserCount = JSONUtil.get(jsonObject, "attentionMyUserCount", "");
        String myAttentionUserCount = JSONUtil.get(jsonObject, "myAttentionUserCount", "");

        if (AttentionListener.getInstance().mOnAttentionListener != null) {
            AttentionListener.getInstance().mOnAttentionListener.result(myAttentionUserCount, attentionMyUserCount);
        }

    }

}
