package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by lenovo on 2018/6/4.
 */

public class ServiceBookAdapter extends BaseAdapter {

    private ArrayList<FriendsBookEntity> mList;
    private Context mContext;
    private ServiceListener mListener;

    public ServiceBookAdapter(Context mContext, ArrayList<FriendsBookEntity> mList, ServiceListener mListener) {
        this.mList = mList;
        this.mContext = mContext;
        this.mListener = mListener;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        final FriendsBookEntity mItem = mList.get(position);
        FriendsBookAdapter.ViewHolder viewHolder = new FriendsBookAdapter.ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_select_friends, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mViewHead = view.findViewById(R.id.view_head);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mLetterTv = view.findViewById(R.id.catalog);
        viewHolder.mDescribeTv = view.findViewById(R.id.tv_describe);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);
        viewHolder.mCheckBox = view.findViewById(R.id.dis_select);
        viewHolder.mCallTv = view.findViewById(R.id.tv_call);


        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mPictureIv);
        viewHolder.mNameTv.setText(mItem.getUname());

        if (mItem.getStatus().equals("1")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.icon_authen).into(viewHolder.mIdentyIv);
        }
        else if(mItem.getStatus().equals("4")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.mipmap.user_vip).into(viewHolder.mIdentyIv);
        }
        else {
            viewHolder.mIdentyIv.setVisibility(View.GONE);
        }


        if (null != mItem.getWorkHistorys()) {
            if (!"".equals(mItem.getWorkHistorys().getName()) && !"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition() + " | " + mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getName())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition());
            }
        }


        viewHolder.mContentLl.setTag(position);
        final FriendsBookAdapter.ViewHolder finalViewHolder = viewHolder;
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onServiceItemClick(view, position);
                }
            }
        });
        return view;
    }

    final static class ViewHolder {
        RoundImageView mPictureIv;
        ImageView mIdentyIv;
        TextView mLetterTv;
        TextView mNameTv, mDescribeTv;
        LinearLayout mContentLl;
        CheckBox mCheckBox;
        TextView mCallTv;
        View mViewHead;
    }


    public interface ServiceListener {
        void onServiceItemClick(View view, int position);
    }
}
