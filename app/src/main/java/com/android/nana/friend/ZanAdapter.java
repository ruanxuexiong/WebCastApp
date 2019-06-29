package com.android.nana.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.ZanEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/2.
 */

public class ZanAdapter extends BaseAdapter {

    private Context mContext;
    private ZanListener mListener;
    private ArrayList<ZanEntity.Users> mDataList;

    public ZanAdapter(Context context, ArrayList<ZanEntity.Users> mDataList, ZanListener mListener) {
        this.mContext = context;
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
    public View getView(int position, View itemView, ViewGroup parent) {

        ZanEntity.Users mItem = mDataList.get(position);
        ZanViewHolder viewHolder = new ZanViewHolder();
        viewHolder.item = mItem;
        itemView = LayoutInflater.from(mContext).inflate(R.layout.item_zan, null);
        viewHolder.mAvatarIv = itemView.findViewById(R.id.iv_avatar);
        viewHolder.mDentyIv = itemView.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = itemView.findViewById(R.id.tv_user_name);
        viewHolder.mContentTv = itemView.findViewById(R.id.tv_content);
        viewHolder.mItemContent = itemView.findViewById(R.id.ll_conent);

        viewHolder.mNameTv.setText(mItem.getUname());
        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mAvatarIv);

        if (mItem.getStatus().equals("1")) {
            viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.icon_authen).into(viewHolder.mDentyIv);
        }
        else if(mItem.getStatus().equals("4")) {
            viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.mipmap.user_vip).into(viewHolder.mDentyIv);
        }
        else {
            viewHolder.mDentyIv.setVisibility(View.GONE);
        }

        if (null != mItem.getWorkHistorys()) {
            if (!"".equals(mItem.getWorkHistorys().getName()) && !"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mContentTv.setText(mItem.getWorkHistorys().getPosition() + " | " + mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getName())) {
                viewHolder.mContentTv.setText(mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mContentTv.setText(mItem.getWorkHistorys().getPosition());
            }
        }

        viewHolder.mItemContent.setTag(position);
        viewHolder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener){
                    mListener.onItemClick(v);
                }
            }
        });
        return itemView;
    }

    public class ZanViewHolder {
        ZanEntity.Users item;
        RoundImageView mAvatarIv;
        ImageView mDentyIv;
        TextView mNameTv;
        TextView mContentTv;
        LinearLayout mItemContent;
    }

    public interface ZanListener {
        void onItemClick(View view);
    }
}
