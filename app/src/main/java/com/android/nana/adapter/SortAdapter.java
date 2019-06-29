package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.SortEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/30.
 */

public class SortAdapter extends BaseAdapter implements SectionIndexer, View.OnClickListener {

    private ArrayList<SortEntity> mList;
    private Context mContext;
    private SortListener mListener;

    public SortAdapter(Context mContext, ArrayList<SortEntity> mList, SortListener mListener) {
        this.mList = mList;
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public void updateListView(ArrayList<SortEntity> list) {//分页使用
        this.mList = list;
        notifyDataSetChanged();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final SortEntity mItem = mList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_mail, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mLetterTv = view.findViewById(R.id.catalog);
        viewHolder.mDescribeTv = view.findViewById(R.id.tv_describe);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.mLetterTv.setVisibility(View.VISIBLE);
            viewHolder.mLetterTv.setText(mItem.getSortLetters());
        } else {
            viewHolder.mLetterTv.setVisibility(View.GONE);
        }

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

        if (null != mItem.getWorkHistorys()) {
            if ("".equals(mItem.getWorkHistorys().getName()) || "".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setVisibility(View.GONE);
            } else if (!"".equals(mItem.getWorkHistorys().getName()) && !"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition() + " | " + mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getName())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getName());
            } else if (!"".equals(mItem.getWorkHistorys().getPosition())) {
                viewHolder.mDescribeTv.setText(mItem.getWorkHistorys().getPosition());
            }
        }
        viewHolder.mContentLl.setTag(position);
        viewHolder.mContentLl.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_content:
                if (mListener != null) {
                    mListener.onContentClick(view);
                }
                break;
        }
    }


    final static class ViewHolder {
        RoundedImageView mPictureIv;
        ImageView mIdentyIv;
        TextView mLetterTv;
        TextView mNameTv, mDescribeTv;
        LinearLayout mContentLl;
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
        void onContentClick(View view);
    }

}
