package com.android.nana.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lenovo on 2017/11/1.
 */

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.RepViewHolder> {

    private Context mContext;
    private ReplyListener mListener;
    private ArrayList<AlbumDetailsEntity.Child> mDataList;

    public ReplyAdapter(Context context, ArrayList<AlbumDetailsEntity.Child> mDataList, ReplyListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }


    @Override
    public RepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        final RepViewHolder viewHolder = new RepViewHolder(view);
        viewHolder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onContentClick(viewHolder.item);
                }
            }
        });

        viewHolder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onReplyAvatarClick(viewHolder.item);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepViewHolder holder, int position) {
        AlbumDetailsEntity.Child item = mDataList.get(position);
        holder.item = item;
        holder.mNameTv.setText(item.getUname());
        holder.mTimeTv.setText(item.getDate());
        holder.mContentTv.setText( Html.fromHtml("回复<font color='#4B7AAC'>" + item.getTo_uname()+ "</font>: "+item.getContent()) );
        ImgLoaderManager.getInstance().showImageView(item.getAvatar(), holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return this.mDataList.size();
    }

    public static class RepViewHolder extends RecyclerView.ViewHolder {

        RoundImageView mAvatar;
        CircleImageView mDentyIv;
        TextView mNameTv;
        TextView mContentTv;
        TextView mTimeTv;
        LinearLayout mItemContent;
        AlbumDetailsEntity.Child item;

        public RepViewHolder(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.iv_avatar);
            mDentyIv = itemView.findViewById(R.id.iv_identy);
            mNameTv = itemView.findViewById(R.id.tv_user_name);
            mContentTv = itemView.findViewById(R.id.tv_content);
            mTimeTv = itemView.findViewById(R.id.tv_time);
            mItemContent = itemView.findViewById(R.id.rl_content);
        }
    }

    public interface ReplyListener {
        void onContentClick(AlbumDetailsEntity.Child item);

        void onReplyAvatarClick(AlbumDetailsEntity.Child item);
    }

}
