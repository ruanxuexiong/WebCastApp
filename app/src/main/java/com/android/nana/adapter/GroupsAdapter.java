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
import com.android.nana.bean.GroupsEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/9/19.
 */

public class GroupsAdapter extends BaseAdapter {

    private Context mContext;
    private GroupsEntity mItem;
    private ArrayList<GroupsEntity> mDataList;
    private GroupsInterface mListener;
    private boolean isSelect;//是否显示选择框

    public GroupsAdapter(Context context, ArrayList<GroupsEntity> mDataList, GroupsInterface mListener, boolean isSelect) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
        this.isSelect = isSelect;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public GroupsEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder;

        view = inflater.inflate(R.layout.item_groups, null);
        holder = new ViewHolder();
        holder.mNameTv = view.findViewById(R.id.tv_name);
        holder.mItemContent = view.findViewById(R.id.content_item);
        holder.mCheckBox = view.findViewById(R.id.dis_select);
        holder.mPictureIv = view.findViewById(R.id.home_list_item_iv_picture);

        mItem = mDataList.get(position);
        holder.mNameTv.setText(mItem.getName());
        if ("".equals(mItem.getPicture())) {
            holder.mPictureIv.setImageResource(R.drawable.rc_default_group_portrait);
        } else {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.rc_default_group_portrait)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(mContext)
                    .asBitmap()
                    .load(mItem.getPicture())
                    .apply(options)
                    .into(holder.mPictureIv);
        }

        if (isSelect) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(mItem.isChcked());
        }

        holder.mItemContent.setTag(position);
        final ViewHolder finalViewHolder = holder;
        holder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view, finalViewHolder.mCheckBox, position);
                }
            }
        });
        return view;
    }

    public class ViewHolder {
        public ImageView mPictureIv;
        public TextView mNameTv;
        public LinearLayout mItemContent;
        public CheckBox mCheckBox;
    }

    public interface GroupsInterface {
        void onItemClick(View view, CheckBox mCheckBox, int position);
    }
}
