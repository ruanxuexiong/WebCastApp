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
import com.android.nana.bean.MeStartEntity;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/9/6.
 */

public class MeInAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MeStartEntity> mDataList;
    private MeStartEntity mItem;
    private MeInListener mListener;

    public MeInAdapter(Context mContext, ArrayList<MeStartEntity> mDataList, MeInListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public MeStartEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final MestartViewHolder viewHolder;
        mItem = mDataList.get(position);
        viewHolder = new MestartViewHolder();

        view = LayoutInflater.from(mContext).inflate(R.layout.launch_activity_item, null);
        viewHolder.mContentTv = view.findViewById(R.id.tv_content);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mNumTv = view.findViewById(R.id.tv_num);
        viewHolder.mCcontentItem = view.findViewById(R.id.content_item);
        viewHolder.mCoreIv = view.findViewById(R.id.iv_activity);
        viewHolder.mPwdTv = view.findViewById(R.id.tv_pwd);

        ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mCoreIv);

        viewHolder.mContentTv.setText(mItem.getTitle());
        viewHolder.mNumTv.setText(mItem.getNum() + "人参加");
        viewHolder.mNameTv.setText("发起人：" + mItem.getOrganizer());

        if (null != mItem.getPass()) {
            if (mItem.getPass().equals("")) {
                viewHolder.mPwdTv.setVisibility(View.GONE);
            } else {
                viewHolder.mPwdTv.setVisibility(View.VISIBLE);
                viewHolder.mPwdTv.setText("群组密码：" + mItem.getPass());
            }
        }

        viewHolder.mCcontentItem.setTag(position);
        viewHolder.mCcontentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMeItemClick(view);
                }
            }
        });

        return view;
    }

    class MestartViewHolder {
        ImageView mCoreIv;
        LinearLayout mCcontentItem;
        MeStartEntity mItem;
        TextView mContentTv, mNameTv, mNumTv, mPwdTv;
    }


    public interface MeInListener {
        void onMeItemClick(View view);
    }
}
