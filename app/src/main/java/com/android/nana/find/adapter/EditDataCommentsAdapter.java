package com.android.nana.find.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.friend.AlbumEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/11/12.
 */

public class EditDataCommentsAdapter extends RecyclerView.Adapter<EditDataCommentsAdapter.CommentsViewHolder>  {


    private Context mContext;
    private ArrayList<AlbumEntity.Comments> mDataList;

    public EditDataCommentsAdapter(Context mContext,ArrayList<AlbumEntity.Comments> mDataList){
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public EditDataCommentsAdapter.CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
        final CommentsViewHolder viewHolder = new CommentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        AlbumEntity.Comments item = mDataList.get(position);
        holder.item = item;

        holder.mNameTv.setText(  Html.fromHtml("<font color='#0080E9'> "+item.getUser().getUsername()+":"+" </font>"+"<font color='#2A2A2A'> "+item.getContent()+"</font>"));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        private TextView mNameTv;
        private AlbumEntity.Comments item;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.tv_name);
        }
    }
}
