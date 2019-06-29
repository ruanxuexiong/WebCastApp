package com.android.nana.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.material.MeDataBean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/7/31.
 */

public class WorkExpertAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MeDataBean.WorkHistorys> mDataList;
    private WorkExpertListener mListener;
    private MeDataBean.WorkHistorys mItem;

    public WorkExpertAdapter(Context mContext, ArrayList<MeDataBean.WorkHistorys> mDataList, WorkExpertListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
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
        mItem = mDataList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_work_expert, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mCompanyTv = view.findViewById(R.id.tv_company);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_time);
        viewHolder.mDocTv = view.findViewById(R.id.tv_doc);
        viewHolder.mItemLl = view.findViewById(R.id.ll_item);

        viewHolder.mNameTv.setText(mItem.getPosition());
        viewHolder.mCompanyTv.setText(mItem.getName());
        viewHolder.mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());
        viewHolder.mDocTv.setText(mItem.getIntroduce());
        ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mPictureIv);

        viewHolder.mItemLl.setTag(position);
        viewHolder.mItemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView mPictureIv;
        TextView mNameTv, mCompanyTv, mTimeTv, mDocTv;
        LinearLayout mItemLl;
    }

    public interface WorkExpertListener {
        void onItemClick(View view);
    }
}
