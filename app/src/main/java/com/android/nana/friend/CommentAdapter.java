package com.android.nana.friend;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/1.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private CommentListener mListener;
    private ReplyAdapter mAdapter;
    private ReplyAdapter.ReplyListener mReplyListener;
    private ArrayList<AlbumDetailsEntity.Comments> mDataList;

    public CommentAdapter(Context context, ArrayList<AlbumDetailsEntity.Comments> mDataList, CommentListener mListener, ReplyAdapter.ReplyListener mReplyListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
        this.mReplyListener = mReplyListener;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_comment, parent, false);
        final CommentViewHolder viewHolder = new CommentViewHolder(view);

        viewHolder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(viewHolder.item);
                }
            }
        });

        viewHolder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAvatarClick(viewHolder.item);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        AlbumDetailsEntity.Comments item = mDataList.get(position);
        holder.item = item;
        holder.mTimeTv.setText(item.getDate());
        holder.mNameTv.setText(item.getUname());
        holder.mContentTv.setText(item.getContent());

        if (null!= item.getStatus() && item.getStatus().equals("1")) {
            holder.mDentyIv.setVisibility(View.VISIBLE);
        }

        ImgLoaderManager.getInstance().showImageView(item.getAvatar(), holder.mAvatar);

        if (null != item.getChild() && item.getChild().size() > 0) {
            holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));// 布局管理器。
            //holder.mRecyclerView.addItemDecoration(new ListViewDecoration());

            mAdapter = new ReplyAdapter(mContext, item.getChild(), this.mReplyListener);
            holder.mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mAvatar;
        ImageView mDentyIv;
        TextView mNameTv;
        TextView mContentTv;
        TextView mTimeTv;
        RelativeLayout mItemContent;
        AlbumDetailsEntity.Comments item;
        RecyclerView mRecyclerView;


        public CommentViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.iv_avatar);
            mDentyIv = itemView.findViewById(R.id.iv_identy);
            mNameTv = itemView.findViewById(R.id.tv_user_name);
            mContentTv = itemView.findViewById(R.id.tv_content);
            mTimeTv = itemView.findViewById(R.id.tv_time);
            mRecyclerView = itemView.findViewById(R.id.recycler_view);
            mItemContent = itemView.findViewById(R.id.rl_content);
        }
    }


    public interface CommentListener {
        void onItemClick(AlbumDetailsEntity.Comments item);

        void onAvatarClick(AlbumDetailsEntity.Comments item);
    }

}
