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
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/25.
 */

public class MeAdapter extends SwipeMenuAdapter<MeAdapter.MeViewHolder> {

    private Context mContext;
    private DetailsListener mListener;
    private ArrayList<DetailsEntity.Users> mDataList;

    public MeAdapter(Context context, ArrayList<DetailsEntity.Users> mDataList, DetailsListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }


    @Override
    public void onBindViewHolder(MeViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.home_list_item, parent, false);
    }

    @Override
    public MeViewHolder onCompatCreateViewHolder(View realContentView, int position) {
        MeViewHolder viewHolder = new MeViewHolder(realContentView);
        viewHolder.mListener = mListener;
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }


    static class MeViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        DetailsListener mListener;
        TextView mTxtName, mTxtDesc, mTxtAppointment;

        public MeViewHolder(View view) {
            super(view);
            mIvPicture = view.findViewById(R.id.home_list_item_iv_picture);
            mIvIdenty = view.findViewById(R.id.home_list_item_iv_identy);
            mTxtName = view.findViewById(R.id.home_list_item_txt_name);
            mTxtDesc = view.findViewById(R.id.home_list_item_txt_desc);
            mTxtAppointment = view.findViewById(R.id.home_list_item_txt_appointment);
            mLLContent = view.findViewById(R.id.content);
        }

        public void setData(final DetailsEntity.Users mItem) {

            ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), mIvPicture);

            String state = mItem.getStatus(); // 0待审核  1审核通过  2审核未通过
            if (state.equals("1")) {
                mIvIdenty.setVisibility(View.VISIBLE);
            } else {
                mIvIdenty.setVisibility(View.GONE);
            }
            mTxtName.setText(mItem.getUsername());

            if (!"".equals(mItem.getCompany()) && !"".equals(mItem.getPosition())) {
                mTxtDesc.setText(mItem.getPosition() + " | " + mItem.getCompany());
            } else if (!"".equals(mItem.getCompany())) {
                mTxtDesc.setText(mItem.getCompany());
            } else if (!"".equals(mItem.getPosition())) {
                mTxtDesc.setText(mItem.getPosition());
            } else {
                mTxtDesc.setVisibility(View.GONE);
            }


            if (mItem.getIsFriend().equals("1")) {
                mTxtAppointment.setText("呼叫");
                mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onCallClick(mItem);
                    }
                });
            } else {
                mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onMakeClick(mItem);
                    }
                });
            }

            mLLContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onContentClick(mItem);
                    }
                }
            });
        }
    }

    public interface DetailsListener {
        void onMakeClick(DetailsEntity.Users mItem);

        void onContentClick(DetailsEntity.Users mItem);

        void onCallClick(DetailsEntity.Users mItem);
    }
}
