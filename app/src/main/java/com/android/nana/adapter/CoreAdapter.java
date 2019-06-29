package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.CoreEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/21.
 */

public class CoreAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private CoreEntity mItem;
    private CoreListener mListener;
    private ArrayList<CoreEntity> mDataList;

    public CoreAdapter(Context context, ArrayList<CoreEntity> mDataList, CoreListener mListener) {
        this.mContext = context;
        this.mListener = mListener;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public CoreEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        mItem = mDataList.get(i);

        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.activity_item, null);
        viewHolder.mContentTv = view.findViewById(R.id.tv_content);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mNumTv = view.findViewById(R.id.tv_num);
        viewHolder.mPwdIv = view.findViewById(R.id.iv_pwd);
        viewHolder.mCcontentItem = view.findViewById(R.id.content_item);
        viewHolder.mCoreIv = view.findViewById(R.id.iv_activity);
        view.setTag(viewHolder);

        ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mCoreIv);

        viewHolder.mContentTv.setText(mItem.getTitle());
        viewHolder.mNumTv.setText(mItem.getNum() + "人参加");
        viewHolder.mNameTv.setText("发起人：" + mItem.getOrganizer());

        if (mItem.getIsEncryte().equals("1")) {
            viewHolder.mPwdIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mPwdIv.setVisibility(View.GONE);
        }

        viewHolder.mCcontentItem.setTag(i);
        viewHolder.mCcontentItem.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content_item:
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
                break;
        }
    }

    class ViewHolder {
        ImageView mCoreIv, mPwdIv;
        LinearLayout mCcontentItem;
        TextView mContentTv, mNameTv, mNumTv;
    }

    public interface CoreListener {
        void onItemClick(View view);
    }
}
