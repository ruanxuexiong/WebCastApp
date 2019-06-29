package com.android.nana.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.SharedEntity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/20.
 */

public class SharedGroupAdapter extends BaseAdapter {

    private Context mContext;
    private SharedGroupListener mListener;
    private ArrayList<SharedEntity.GroupList> group;

    public SharedGroupAdapter(Context mContext, ArrayList<SharedEntity.GroupList> group, SharedGroupListener mListener) {
        this.mContext = mContext;
        this.group = group;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return group.size();
    }

    @Override
    public Object getItem(int i) {
        return group.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        SharedEntity.GroupList mItem = group.get(i);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_shared, null);
        viewHolder.mContent = view.findViewById(R.id.content);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        view.setTag(viewHolder);

        Log.e("返回值===", mItem.getPicture());

        ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), viewHolder.mPictureIv);

        viewHolder.mNameTv.setText(mItem.getName());


        viewHolder.mContent.setTag(i);
        viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });


        return view;
    }

    final static class ViewHolder {
        private RoundImageView mPictureIv;
        private TextView mNameTv;
        private LinearLayout mContent;
    }

    public interface SharedGroupListener {
        void onItemClick(View view);
    }
}
