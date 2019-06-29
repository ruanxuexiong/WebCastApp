package com.android.nana.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseJsonAdapter;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class MeetingRecordAdapter extends BaseJsonAdapter<MeetingRecordAdapter.ViewHolder> {

    private String mThisUserId;

    public MeetingRecordAdapter(Activity context) {
        super(context);
        mThisUserId = BaseApplication.getInstance().getCustomerId(context);
    }

    @Override
    protected ViewHolder getViewById(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mTxtName = (TextView) view.findViewById(R.id.meeting_record_txt_name);
        viewHolder.mTxtOther = (TextView) view.findViewById(R.id.meeting_record_txt_other);
        viewHolder.mTxtTime = (TextView) view.findViewById(R.id.meeting_record_txt_time);
        return viewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.meeting_record_item;
    }

    @Override
    protected void populateData(JSONObject jsonObject, MeetingRecordAdapter.ViewHolder viewHolder) {


        JSONObject thisUser = JSONUtil.getJsonObject(jsonObject, "thisUser");
        String thisUserId = JSONUtil.get(thisUser, "id", "");
        if (thisUserId.equals(mThisUserId)) {
            viewHolder.mTxtOther.setText("我");
        } else {
            viewHolder.mTxtOther.setText(JSONUtil.get(thisUser, "username", ""));
        }

        JSONObject user = JSONUtil.getJsonObject(jsonObject, "user");
        String userId = JSONUtil.get(user, "id", "");
        if (userId.equals(mThisUserId)) {
            viewHolder.mTxtName.setText("我");
        } else {
            viewHolder.mTxtName.setText(JSONUtil.get(user, "username", ""));
        }

        viewHolder.mTxtTime.setText(JSONUtil.get(jsonObject, "timeLong", "") + "分钟");

    }

    class ViewHolder {
        TextView mTxtName, mTxtOther, mTxtTime;
    }

}
