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
import com.android.nana.bean.NewFriendEntity;
import com.android.nana.mail.NewFriendActivity;
import com.android.nana.widget.SwipeListLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/9/12.
 */

public class NewFriendAdapter extends BaseAdapter {

    private ArrayList<NewFriendEntity> mList;
    private FriendListene mListene;
    private Context mContext;

    public NewFriendAdapter(Context mContext, ArrayList<NewFriendEntity> mDataList, FriendListene mListene) {
        this.mContext = mContext;
        this.mList = mDataList;
        this.mListene = mListene;
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        NewFriendEntity mItem = mList.get(i);

        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_new_friend, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mDescribeTv = view.findViewById(R.id.tv_describe);
        viewHolder.mAgreeTv = view.findViewById(R.id.tv_agree);
        viewHolder.mDentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mItemNewFriend = view.findViewById(R.id.item_new_friend);
        viewHolder.sll_main = view.findViewById(R.id.sll_main);
        viewHolder.tv_delete = view.findViewById(R.id.tv_delete);

        view.setTag(viewHolder);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.icon_head_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(mContext)
                .asBitmap()
                .load(mItem.getAvatar())
                .apply(options)
                .into(viewHolder.mPictureIv);

        viewHolder.mNameTv.setText(mItem.getUname());

        if (null != mItem.getStatus() && !"".equals(mItem.getStatus()) && mItem.getStatus().equals("1")) {
            viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.icon_authen).into(viewHolder.mDentyIv);
        } else if (mItem.getStatus().equals("4")) {
            viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.mipmap.user_vip).into(viewHolder.mDentyIv);
        } else {
            viewHolder.mDentyIv.setVisibility(View.GONE);
        }

        if (null != mItem.getStatus() && !"".equals(mItem.getMessage())) {
            viewHolder.mDescribeTv.setText(mItem.getMessage());
        } else if (null != mItem.getStatus()) {
            viewHolder.mDescribeTv.setText("申请加你为好友");
        }

        if (null != mItem.getStatus() && mItem.getFollow().equals("0")) {
            viewHolder.mAgreeTv.setTag(i);
            viewHolder.mAgreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListene != null) {
                        mListene.onAgreeClick(view);
                    }
                }
            });
        } else if (null != mItem.getStatus() && mItem.getFollow().equals("1")) {
            viewHolder.mAgreeTv.setBackground(null);
            viewHolder.mAgreeTv.setText("等待验证");
            viewHolder.mDescribeTv.setText("已发送验证消息");
            viewHolder.mAgreeTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
        } else if (null != mItem.getStatus() && mItem.getFollow().equals("2")) {
            viewHolder.mAgreeTv.setBackground(null);
            viewHolder.mAgreeTv.setText("已同意");
            viewHolder.mAgreeTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
        } else if (null != mItem.getStatus() && mItem.getFollow().equals("3")) {
            viewHolder.mAgreeTv.setBackground(null);
            viewHolder.mAgreeTv.setText("已拒绝");
            viewHolder.mAgreeTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
        }


        viewHolder.mItemNewFriend.setTag(i);
        viewHolder.mItemNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListene != null) {
                    mListene.onItemClick(view);
                }
            }
        });
        if (mListene != null)
            mListene.setOnSwipeStatusListener(viewHolder.sll_main);
        viewHolder.tv_delete.setTag(i);
        viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListene != null) {
                    mListene.onDeleteItem(view);
                }
            }
        });
        if (null == mItem.getAvatar()) {
            viewHolder.mItemNewFriend.setVisibility(View.GONE);
        }

        return view;
    }

    class ViewHolder {
        private RoundedImageView mPictureIv;
        private ImageView mDentyIv;
        private TextView mNameTv;
        private TextView mDescribeTv;
        private TextView mAgreeTv;
        private TextView tv_delete;
        private LinearLayout mItemNewFriend;
        private SwipeListLayout sll_main;
    }

    public interface FriendListene {
        void onItemClick(View view);

        void onAgreeClick(View view);

        void onDeleteItem(View view);

        void setOnSwipeStatusListener(SwipeListLayout sll_main);
    }
}
