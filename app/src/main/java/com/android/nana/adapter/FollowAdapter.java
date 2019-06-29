package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.FollowEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/9/4.
 */

public class FollowAdapter extends BaseAdapter implements View.OnClickListener {


    private Context mContext;
    private FollowEntity mItem;
    private ArrayList<FollowEntity> mDataList;
    private FollowListener mListener;

    public FollowAdapter(Context mContext, ArrayList<FollowEntity> mDataList, FollowListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public FollowEntity getItem(int position) {
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
        view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, null);
        viewHolder.mIvPicture = view.findViewById(R.id.home_list_item_iv_picture);
        viewHolder.mIvIdenty = view.findViewById(R.id.home_list_item_iv_identy);
        viewHolder.mTxtName = view.findViewById(R.id.home_list_item_txt_name);
        viewHolder.mTxtDesc = view.findViewById(R.id.home_list_item_txt_desc);
        viewHolder.mTxtAppointment = view.findViewById(R.id.home_list_item_txt_appointment);
        viewHolder.mLLContent = view.findViewById(R.id.content);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.icon_head_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(mContext)
                .asBitmap()
                .load(mItem.getUser().getAvatar())
                .apply(options)
                .into(viewHolder.mIvPicture);

        String state = mItem.getUser().getStatus();
        if (null != state && state.equals("1")) {
            viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvIdenty.setVisibility(View.GONE);
        }

        viewHolder.mTxtName.setText(mItem.getUser().getUsername());

        if (null != mItem.getUser().getWorkHistorys()) {
            String strPosition = mItem.getUser().getWorkHistorys().getPosition();
            String name = mItem.getUser().getWorkHistorys().getName();
            if (!"".equals(name) && null != name) {
                viewHolder.mTxtDesc.setText(strPosition + " | " + name);
            } else {
                viewHolder.mTxtDesc.setVisibility(View.GONE);
            }
        }

        if (mItem.getUser().getIsFriend().equals("1")) {
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
                    mListener.onVideoClick(view);
                }
            });
        }

        viewHolder.mLLContent.setTag(position);
        viewHolder.mLLContent.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content:
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
                break;
        }
    }

    class ViewHolder {
        RoundedImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        TextView mTxtName, mTxtInfo, mCallTv, mTxtDesc, mTxtAppointment, mTxtAttention, mTxtMoney;
    }

    public interface FollowListener {
        void onVideoClick(View view);

        void onItemClick(View view);

        void onCallClick(View view);
    }
}
