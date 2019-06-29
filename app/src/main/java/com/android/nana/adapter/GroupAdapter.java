package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.ForwardEntity;
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/17.
 */

public class GroupAdapter extends BaseAdapter {

    private Context mContext;
    private GroupsInterface mListener;
    private ArrayList<ForwardEntity.Groups> mGroups;

    public GroupAdapter(Context mContext, ArrayList<ForwardEntity.Groups> mGroups, GroupsInterface mListener) {
        this.mContext = mContext;
        this.mGroups = mGroups;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int i) {
        return mGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View itemView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        ForwardEntity.Groups mItem = mGroups.get(position);

        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_groups, null);
            viewHolder.mNameTv = itemView.findViewById(R.id.tv_name);
            viewHolder.mItemContent = itemView.findViewById(R.id.content_item);
            viewHolder.mCheckBox = itemView.findViewById(R.id.dis_select);
            viewHolder.mPictureIv = itemView.findViewById(R.id.home_list_item_iv_picture);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemView.getTag();
        }

        viewHolder.mNameTv.setText(mItem.getName());
        if ("".equals(mItem.getPicture())) {
            viewHolder.mPictureIv.setImageResource(R.drawable.rc_default_group_portrait);
        } else {
            ImgLoaderManager.getInstance().getInstance().showImageView(mItem.getPicture(), viewHolder.mPictureIv);
        }

        viewHolder.mCheckBox.setVisibility(View.VISIBLE);
        viewHolder.mCheckBox.setChecked(mItem.isChcked());
        viewHolder.mItemContent.setTag(position);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClick(view, finalViewHolder.mCheckBox, position);
                }
            }
        });
        return itemView;
    }

    class ViewHolder {
        RoundImageView mPictureIv;
        TextView mNameTv;
        LinearLayout mItemContent;
        CheckBox mCheckBox;
    }

    public interface GroupsInterface {
        void onClick(View view, CheckBox mCheckBox, int position);
    }
}
