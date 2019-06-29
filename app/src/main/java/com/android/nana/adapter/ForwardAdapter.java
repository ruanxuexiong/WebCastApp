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
import com.android.nana.bean.ForwardEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/17.
 */

public class ForwardAdapter extends BaseAdapter {

    private Context mContext;
    private ForwardListener mListener;
    private ArrayList<ForwardEntity.Friends> mFriends;

    public ForwardAdapter(Context mContext, ArrayList<ForwardEntity.Friends> mFriends, ForwardListener mListener) {
        this.mContext = mContext;
        this.mFriends = mFriends;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        ForwardEntity.Friends mItem = mFriends.get(i);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_friends, null);
        viewHolder.mContent = view.findViewById(R.id.content);
        viewHolder.checkBox = view.findViewById(R.id.dis_select);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        view.setTag(viewHolder);

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
        viewHolder.checkBox.setChecked(mItem.isChcked());
        viewHolder.mContent.setTag(i);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onContentClick(view, finalViewHolder.checkBox, i);
                }
            }
        });


        return view;
    }

    final static class ViewHolder {
        private CheckBox checkBox;
        private RoundImageView mPictureIv;
        private ImageView mIdentyIv;
        private TextView mNameTv;
        private LinearLayout mContent;
    }

    public interface ForwardListener {
        void onContentClick(View view, CheckBox checkBox, int position);
    }
}
