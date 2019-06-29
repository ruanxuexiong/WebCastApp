package com.android.nana.downmenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.OthreEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.widget.WordToSpan;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/4.
 */

public class OthreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<OthreEntity.User> mDataList;
    private String keyword;
    private WordToSpan WTS = new WordToSpan();
    private WordToSpan mContentWTS = new WordToSpan();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private boolean isLoading = false;//加载最后
    private OthreListener mListener;

    public OthreAdapter(Context mContext, ArrayList<OthreEntity.User> mDataList, String keyword, OthreListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.keyword = keyword;
        this.mListener = mListener;

        WTS.setBackgroundHIGHLIGHT(mContext.getResources().getColor(R.color.white));
        WTS.setColorHIGHLIGHT(mContext.getResources().getColor(R.color.create_activity));
        mContentWTS.setBackgroundHIGHLIGHT(mContext.getResources().getColor(R.color.white));
        mContentWTS.setColorHIGHLIGHT(mContext.getResources().getColor(R.color.create_activity));
    }

    public void setKeyword(String keyword) {//搜索key
        this.keyword = keyword;
    }

    public void isLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_othre, parent, false);
            final OthreViewHolder viewHolder = new OthreViewHolder(view);
            viewHolder.mContentll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(viewHolder.entity);
                    }
                }
            });
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent,
                    false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof OthreViewHolder) {
            holder.setIsRecyclable(false);//关闭复用
            OthreEntity.User entity = mDataList.get(position);
            OthreViewHolder viewHolder = (OthreViewHolder) holder;
            viewHolder.entity = entity;
            viewHolder.mNameTv.setText(entity.getUsername());
            viewHolder.mActiveTv.setVisibility(View.VISIBLE);
            viewHolder.mActiveTv.setText(entity.getActiveTime());
            WTS.setHighlight(viewHolder.mNameTv.getText().toString(), keyword, viewHolder.mNameTv);

            if (entity.getStatus().equals("1")) {
                viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mDentyIv.setVisibility(View.GONE);
            }

            if (!"".equals(entity.getCompany()) && !"".equals(entity.getPosition()) && null != entity.getCompany() && null != entity.getPosition()) {
                viewHolder.mDescribeTv.setText(entity.getPosition() + " | " + entity.getCompany());
            } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                viewHolder.mDescribeTv.setText(entity.getCompany());
            } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                viewHolder.mDescribeTv.setText(entity.getPosition());
            }

            mContentWTS.setHighlight(viewHolder.mDescribeTv.getText().toString(), keyword, viewHolder.mDescribeTv);
            ImgLoaderManager.getInstance().showImageView(entity.getAvatar(), viewHolder.mAvatarIv);
        } else {
            FootViewHolder viewHolder = (FootViewHolder) holder;
            if (isLoading) {
                viewHolder.mProgressll.setVisibility(View.GONE);
            } else {
                viewHolder.mProgressll.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() == 0 ? 0 : mDataList.size() + 1;
    }

    public static class OthreViewHolder extends RecyclerView.ViewHolder {

        RoundImageView mAvatarIv;
        ImageView mDentyIv;
        TextView mNameTv, mDescribeTv;
        LinearLayout mContentll;
        OthreEntity.User entity;
        TextView mActiveTv;

        public OthreViewHolder(View itemView) {
            super(itemView);
            mAvatarIv = itemView.findViewById(R.id.iv_avatar);
            mDentyIv = itemView.findViewById(R.id.iv_identy);
            mNameTv = itemView.findViewById(R.id.tv_name);
            mContentll = itemView.findViewById(R.id.ll_content);

            mActiveTv = itemView.findViewById(R.id.tv_active);
            mDescribeTv = itemView.findViewById(R.id.tv_describe);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar mProgressBar;
        LinearLayout mProgressll;

        public FootViewHolder(View view) {
            super(view);
            mProgressBar = view.findViewById(R.id.progressBar);
            mProgressll = view.findViewById(R.id.progress);
        }
    }

    public interface OthreListener {
        void onItemClick(OthreEntity.User entity);
    }
}
