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
import com.android.nana.bean.SharedEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/19.
 */

public class SharedAdapter extends BaseAdapter {

    private Context mContext;
    private SharedListener mListener;
    private ArrayList<SharedEntity.Users> users;

    public SharedAdapter(Context mContext, ArrayList<SharedEntity.Users> users, SharedListener mListener) {
        this.mContext = mContext;
        this.users = users;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        SharedEntity.Users mItem = users.get(i);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_shared, null);
        viewHolder.mContent = view.findViewById(R.id.content);
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

        viewHolder.mContent.setTag(i);
        viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onContentClick(view);
                }
            }
        });

        return view;
    }

    final static class ViewHolder {
        private RoundImageView mPictureIv;
        private ImageView mIdentyIv;
        private TextView mNameTv;
        private LinearLayout mContent;
    }

    public interface SharedListener {
        void onContentClick(View view);
    }
}
