package com.android.nana.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.BlackEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/22.
 */

public class BlackAdapter extends SwipeMenuAdapter<BlackAdapter.DefaultViewHolder> {


    private Context mContext;
    private ArrayList<BlackEntity> mDataList;

    private OnItemClickListener mOnItemClickListener;

    public BlackAdapter(Context mContext, ArrayList<BlackEntity> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_black, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        BlackEntity mItem;
        OnItemClickListener mOnItemClickListener;
        RoundImageView mAvatarIv;
        TextView mNameTv;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAvatarIv = (RoundImageView) itemView.findViewById(R.id.iv_avatar);
            mNameTv = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void setData(BlackEntity entity) {
            mItem = entity;
            ImgLoaderManager.getInstance().showImageView(mItem.getUserInfo().getAvatar(), mAvatarIv);
            mNameTv.setText(mItem.getUserInfo().getUsername());
        }


        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mItem);
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(BlackEntity entity);

    }
}
