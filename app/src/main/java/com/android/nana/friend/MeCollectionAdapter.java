package com.android.nana.friend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.tools.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/3/11.
 */

public class MeCollectionAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MeCollectionEntity> mDataList = new ArrayList<>();
    private CollectionPictureImageGridAdapter mAdapter;
    private CollectionPictureImageGridAdapter.CollectionPictureListener mListener;

    public MeCollectionAdapter(Context mContext,ArrayList<MeCollectionEntity> mDataList,CollectionPictureImageGridAdapter.CollectionPictureListener mListener){
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MeCollectionEntity entity = mDataList.get(position);
        ViewHolder viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album, null);
        viewHolder.mMonthTv = convertView.findViewById(R.id.tv_month);
        viewHolder.mRecyclerView = convertView.findViewById(R.id.picture_recycler);

        viewHolder.mMonthTv.setText(entity.getMonth());
        viewHolder.mRecyclerView.setHasFixedSize(true);
        viewHolder.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, ScreenUtils.dip2px(mContext, 2), false));
        viewHolder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) viewHolder.mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new CollectionPictureImageGridAdapter(mContext,entity.getArticle(),mListener);
        viewHolder.mRecyclerView.setAdapter(mAdapter);
        return convertView;
    }

    class ViewHolder {
        private TextView mMonthTv;
        private RecyclerView mRecyclerView;
    }
}
