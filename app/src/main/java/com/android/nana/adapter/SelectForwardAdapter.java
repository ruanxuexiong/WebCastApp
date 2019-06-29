package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;

import com.android.nana.R;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.util.ImgLoaderManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/5/14.
 */

public class SelectForwardAdapter extends BaseAdapter implements SectionIndexer {

    private ArrayList<FriendsBookEntity> mList;
    private Context mContext;
    private boolean isSelect;//判断是否显示选择框
    private SelectForwardAdapter.SortListener mListener;

    public SelectForwardAdapter(Context mContext, ArrayList<FriendsBookEntity> mList, SelectForwardAdapter.SortListener mListener, boolean isSelect) {
        this.mList = mList;
        this.mContext = mContext;
        this.mListener = mListener;
        this.isSelect = isSelect;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final FriendsBookEntity mItem = mList.get(position);
        FriendsBookAdapter.ViewHolder viewHolder = new FriendsBookAdapter.ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_select_friends, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mLetterTv = view.findViewById(R.id.catalog);
        viewHolder.mDescribeTv = view.findViewById(R.id.tv_describe);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);
        viewHolder.mCheckBox = view.findViewById(R.id.dis_select);
        viewHolder.mCallTv = view.findViewById(R.id.tv_call);

        int section = getSectionForPosition(position);

        if (position - 1 == getPositionForSection(section)) {
            viewHolder.mLetterTv.setVisibility(View.VISIBLE);
            viewHolder.mLetterTv.setText(mItem.getSortLetters());
        } else {
            viewHolder.mLetterTv.setVisibility(View.GONE);
        }
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


        if (isSelect) {//是否显示多选框
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mCallTv.setVisibility(View.GONE);
            viewHolder.mCheckBox.setChecked(mItem.isChcked());
        }


        if (null != mItem.getWorkHistorys()) {
            if (!"".equals(mItem.getWorkHistorys().getName()) && !"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition() + " | " + mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getName())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition());
            }
        }


        viewHolder.mContentLl.setTag(position);
        final FriendsBookAdapter.ViewHolder finalViewHolder = viewHolder;
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onContentClick(view, finalViewHolder.mCheckBox, position);
                }
            }
        });
        return view;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    public interface SortListener {
        void onContentClick(View view, CheckBox checkBox, int position);
    }
}
