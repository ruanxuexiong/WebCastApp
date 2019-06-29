package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.nana.R;
import com.android.nana.bean.RecordEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/14.
 */

public class MeetingAdapter extends BaseAdapter {

    private ArrayList<RecordEntity> mDataList;
    private Context mContext;
    private RecordEntity mItem;
    private String mThisUserId;

    public MeetingAdapter(Context context, ArrayList<RecordEntity> mDataList) {
        this.mDataList = mDataList;
        this.mContext = context;
        mThisUserId = BaseApplication.getInstance().getCustomerId(context);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public RecordEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;
        mItem = mDataList.get(position);
        viewHolder = new ViewHolder();

        view = LayoutInflater.from(mContext).inflate(R.layout.meeting_record_item, null);
        viewHolder.mTxtName = view.findViewById(R.id.meeting_record_txt_name);
        viewHolder.mTxtOther = view.findViewById(R.id.meeting_record_txt_other);
        viewHolder.mTxtTime = view.findViewById(R.id.meeting_record_txt_time);

        viewHolder.mTxtName.setText(mItem.getThisName());
        viewHolder.mTxtOther.setText(mItem.getUserName());
        viewHolder.mTxtTime.setText(mItem.getMeetLong());

        return view;
    }

    class ViewHolder {
        TextView mTxtName, mTxtOther, mTxtTime;
    }
}
