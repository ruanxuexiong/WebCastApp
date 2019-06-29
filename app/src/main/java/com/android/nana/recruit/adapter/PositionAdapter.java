package com.android.nana.recruit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.recruit.bean.PositionManagementEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/30.
 */

public class PositionAdapter extends BaseAdapter {

    private Context mContext;
    private PositionManagementEntity.Positions item;
    private ArrayList<PositionManagementEntity.Positions> mDataList;

    public PositionAdapter(Context mContext, ArrayList<PositionManagementEntity.Positions> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        item = mDataList.get(position);
        viewHolder = new ViewHolder();

        view = LayoutInflater.from(mContext).inflate(R.layout.item_position, null);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mTalkTv = view.findViewById(R.id.tv_talk);
        viewHolder.mViewTv = view.findViewById(R.id.tv_view);
        viewHolder.mShareTv = view.findViewById(R.id.tv_share);
        viewHolder.mRecruitTv = view.findViewById(R.id.tv_recruit);

        viewHolder.mNameTv.setText(item.getJob());
        viewHolder.mTalkTv.setText(item.getTalk());
        viewHolder.mViewTv.setText(item.getView());
        viewHolder.mShareTv.setText(item.getShare());
        if (item.getStatus().equals("1")) {
            viewHolder.mRecruitTv.setText("招聘中");
            viewHolder.mRecruitTv.setTextColor(mContext.getResources().getColor(R.color.activity_red));
        } else {
            viewHolder.mRecruitTv.setText("已关闭");
            viewHolder.mNameTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
            viewHolder.mRecruitTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
        }
        return view;
    }

    class ViewHolder {
        TextView mNameTv, mTalkTv, mViewTv, mShareTv, mRecruitTv;
    }
}
