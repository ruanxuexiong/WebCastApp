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
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.ImgLoaderManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by lenovo on 2017/9/26.
 */

public class GroupActivityAdapter extends BaseAdapter {


    private Context mContext;
    private String mGroupId;
    private List<GroupInfoEntity.Member> mDataList;

    public GroupActivityAdapter(Context context, List<GroupInfoEntity.Member> mDataList, String mGroupId) {

        if (mDataList.size() >= 31) {
            this.mDataList = mDataList.subList(0, 30);
        } else {
            this.mDataList = mDataList;
        }
        this.mGroupId = mGroupId;
        this.mContext = context;
    }

    public GroupActivityAdapter(Context context, List<GroupInfoEntity.Member> mDataList, String mGroupId, boolean is) {

        if (mDataList.size() >= 10) {
            this.mDataList = mDataList.subList(0, 10);
        } else {
            this.mDataList = mDataList;
        }
        this.mGroupId = mGroupId;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDataList.size();
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
        return view;
    }

    private class ViewHolder {
        private RoundedImageView mPictureIv;
        private TextView mNameTv;
    }
}
