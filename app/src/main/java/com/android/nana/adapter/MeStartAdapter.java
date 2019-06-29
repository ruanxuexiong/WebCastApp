package com.android.nana.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.MeStartEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/22.
 */

public class MeStartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MeStartEntity> mDataList;
    private MeStartListener mListener;

    public MeStartAdapter(Context mContext, ArrayList<MeStartEntity> mDataList, MeStartListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.launch_activity_item, parent, false);
        final MestartViewHolder viewHolder = new MestartViewHolder(view);
        viewHolder.mCcontentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(viewHolder.mItem.getId());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MeStartEntity mItem = mDataList.get(position);
        MestartViewHolder viewHolder = (MestartViewHolder) holder;
        viewHolder.mItem = mItem;

        ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mCoreIv);

        viewHolder.mContentTv.setText(mItem.getTitle());
        viewHolder.mNumTv.setText(mItem.getNum() + "人参加");
        viewHolder.mNameTv.setText("发起人：" + mItem.getOrganizer());

        if (mItem.getPass().equals("")) {
            viewHolder.mPwdTv.setVisibility(View.GONE);
        } else {
            viewHolder.mPwdTv.setVisibility(View.VISIBLE);
            viewHolder.mPwdTv.setText("群组密码：" + mItem.getPass());
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    static class MestartViewHolder extends RecyclerView.ViewHolder {
        ImageView mCoreIv;
        LinearLayout mCcontentItem;
        MeStartEntity mItem;
        TextView mContentTv, mNameTv, mNumTv, mPwdTv;

        public MestartViewHolder(View view) {
            super(view);
            mContentTv = view.findViewById(R.id.tv_content);
            mNameTv = view.findViewById(R.id.tv_name);
            mNumTv = view.findViewById(R.id.tv_num);
            mCcontentItem = view.findViewById(R.id.content_item);
            mCoreIv = view.findViewById(R.id.iv_activity);
            mPwdTv = view.findViewById(R.id.tv_pwd);
        }
    }

    public interface MeStartListener {
        void onItemClick(String id);
    }
}
