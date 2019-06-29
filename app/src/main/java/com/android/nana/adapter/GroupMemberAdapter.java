package com.android.nana.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.GroupInfoEntity;
import com.android.nana.mail.SelectFriendsActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.ImgLoaderManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by lenovo on 2017/9/20.
 */

public class GroupMemberAdapter extends BaseAdapter {

    private Context mContext;
    private boolean isCreated = false;//判断是否是自己创建的群聊
    private String mGroupId;
    private List<GroupInfoEntity.Member> mDataList;

    public GroupMemberAdapter(Context context, List<GroupInfoEntity.Member> mDataList, boolean isCreated, String mGroupId) {

        if (mDataList.size() >= 31) {
            this.mDataList = mDataList.subList(0, 30);
        } else {
            this.mDataList = mDataList;
        }
        this.mGroupId = mGroupId;
        this.mContext = context;
        this.isCreated = isCreated;
    }

    @Override
    public int getCount() {
        if (isCreated) {
            return mDataList.size() + 2;
        } else {
            return mDataList.size() + 1;
        }
    }

    @Override
    public GroupInfoEntity.Member getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_group, null);
            viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
            viewHolder.mNameTv = view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 最后一个item，减人按钮
        if (position == getCount() - 1 && isCreated) {
            viewHolder.mNameTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
            viewHolder.mNameTv.setText("删除");
            viewHolder.mPictureIv.setImageResource(R.drawable.icon_btn_deleteperson);
            viewHolder.mPictureIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SelectFriendsActivity.class);
                    intent.putExtra("isDeleteGroupMember", true);
                    intent.putExtra("GroupId", mGroupId);
                    mContext.startActivity(intent);
                }
            });
        } else if ((isCreated && position == getCount() - 2) || (!isCreated && position == getCount() - 1)) {
            viewHolder.mNameTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
            viewHolder.mNameTv.setText("添加");
            viewHolder.mPictureIv.setImageResource(R.drawable.jy_drltsz_btn_addperson);
            viewHolder.mPictureIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SelectFriendsActivity.class);
                    intent.putExtra("isAddGroupMember", true);
                    intent.putExtra("GroupId", mGroupId);
                    mContext.startActivity(intent);
                }
            });
        } else {
            final GroupInfoEntity.Member mItem = mDataList.get(position);
            viewHolder.mNameTv.setText(mItem.getUname());
            ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mPictureIv);
            viewHolder.mPictureIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditDataActivity.class);
                    intent.putExtra("UserId", mItem.getId());
                    mContext.startActivity(intent);
                }
            });
        }

        return view;
    }

    private class ViewHolder {
        private RoundedImageView mPictureIv;
        private TextView mNameTv;
    }

    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<GroupInfoEntity.Member> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }
}
