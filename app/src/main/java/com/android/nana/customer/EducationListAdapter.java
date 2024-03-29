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
 * Created by lenovo on 2018/8/2.
 */

public class EducationListAdapter  extends BaseAdapter {


    private Context mContext;
    private ArrayList<MeDataBean.EducationExperiences> mDataList;
    private EducationListListener mListener;
    private MeDataBean.EducationExperiences mItem;

    public EducationListAdapter(Context mContext, ArrayList<MeDataBean.EducationExperiences> mDataList, EducationListListener mListener) {
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

        viewHolder.mNameTv.setText(mItem.getName());
        viewHolder.mCompanyTv.setText(mItem.getName());
        viewHolder.mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());
        viewHolder.mDocTv.setText(mItem.getExperience());
        if (null != mItem.getPicture()) {
            ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mPictureIv);
        }else {
            viewHolder.mPictureIv.setImageResource(R.drawable.icon_education);
        }

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

    public interface EducationListListener {
        void onItemClick(View view);
    }
}
