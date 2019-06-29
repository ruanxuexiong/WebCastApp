package com.android.nana.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.DetailsEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/22.
 */

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private DetailsListener mListener;
    private DetailsEntity.Users mItem;
    private ArrayList<DetailsEntity.Users> mDataList;

    public DetailsAdapter(Context context, ArrayList<DetailsEntity.Users> mDataList, DetailsListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, parent, false);
        DetailsViewHolder viewHolder = new DetailsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        mItem = mDataList.get(position);
        DetailsViewHolder viewHolder = (DetailsViewHolder) holder;

        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mIvPicture);

        String state = mItem.getStatus(); // 0待审核  1审核通过  2审核未通过
        if (state.equals("1")) {
            viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvIdenty.setVisibility(View.GONE);
        }
        viewHolder.mTxtName.setText(mItem.getUsername());

        viewHolder.mTxtDesc.setText(mItem.getPosition());

        if (!"".equals(mItem.getCompany())) {
            viewHolder.mDescTv.setVisibility(View.VISIBLE);
            viewHolder.mDescTv.setText("| " + mItem.getCompany());
        } else {
            viewHolder.mDescTv.setVisibility(View.GONE);
        }

        if (mItem.getIsFriend().equals("1")) {
            viewHolder.mTxtAppointment.setText("呼叫");
            viewHolder.mTxtAppointment.setTag(position);
            viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onCallClick(view);
                }
            });
        } else {
            viewHolder.mTxtAppointment.setTag(position);
            viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMakeClick(view);
                }
            });
        }


        viewHolder.mLLContent.setTag(position);
        viewHolder.mLLContent.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content:
                if (mListener != null) {
                    mListener.onContentClick(view);
                }
                break;
        }
    }

    static class DetailsViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        TextView mTxtName, mTxtDesc, mTxtAppointment, mDescTv;

        public DetailsViewHolder(View view) {
            super(view);
            mIvPicture = view.findViewById(R.id.home_list_item_iv_picture);
            mIvIdenty = view.findViewById(R.id.home_list_item_iv_identy);
            mTxtName = view.findViewById(R.id.home_list_item_txt_name);
            mTxtDesc = view.findViewById(R.id.home_list_item_txt_desc);
            mDescTv = view.findViewById(R.id.tv_desc);
            mTxtAppointment = view.findViewById(R.id.home_list_item_txt_appointment);
            mLLContent = view.findViewById(R.id.content);

        }
    }

    public interface DetailsListener {
        void onMakeClick(View view);

        void onContentClick(View view);

        void onCallClick(View view);
    }
}
