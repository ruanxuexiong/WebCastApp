package com.android.nana.main;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/3.
 */

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {

    private Context mContext;
    private NavigationListener mListener;
    private ArrayList<NavigationBean.OneNav> mDataList;

    public NavigationAdapter(Context mContext, ArrayList<NavigationBean.OneNav> mDataList, NavigationListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation, parent, false);
        NavigationViewHolder viewHolder = new NavigationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        final NavigationBean.OneNav item = mDataList.get(position);
        NavigationAdapter.NavigationViewHolder viewHolder = holder;
        viewHolder.mTitleTv.setText(item.getName());
        if (position == 0) {
            viewHolder.mViewHead.setVisibility(View.GONE);
        }

        viewHolder.mViewColor.setBackgroundColor(Color.parseColor(item.getTitcolor()));
        for (int i = 0; i < item.getNav().size(); i++) {
            switch (i) {
                case 0:
                    viewHolder.mLabel1Tv.setText(item.getNav().get(0).getName());
                    viewHolder.mLabel1Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(0).getName());
                            }
                        }
                    });
                    break;
                case 1:
                    viewHolder.mLabel2Tv.setText(item.getNav().get(1).getName());
                    viewHolder.mLabel2Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(1).getName());
                            }
                        }
                    });
                    break;
                case 2:
                    viewHolder.mLabel3Tv.setText(item.getNav().get(2).getName());
                    viewHolder.mLabel3Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(2).getName());
                            }
                        }
                    });
                    break;
                case 3:
                    viewHolder.mLabe2.setVisibility(View.VISIBLE);
                    viewHolder.mLabel4Tv.setText(item.getNav().get(3).getName());

                    viewHolder.mLabel4Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(3).getName());
                            }
                        }
                    });
                    break;
                case 4:
                    viewHolder.mLabe2.setVisibility(View.VISIBLE);
                    viewHolder.mLabel5Tv.setText(item.getNav().get(4).getName());

                    viewHolder.mLabel5Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(4).getName());
                            }
                        }
                    });
                    break;
                case 5:
                    viewHolder.mLabe2.setVisibility(View.VISIBLE);
                    viewHolder.mLabel6Tv.setText(item.getNav().get(5).getName());

                    viewHolder.mLabel6Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener){
                                mListener.OnItemClick(item.getNav().get(5).getName());
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public static class NavigationViewHolder extends RecyclerView.ViewHolder {

        View mViewColor;
        TextView mTitleTv;
        View mViewHead;
        LinearLayout mLabe1, mLabe2;
        TextView mLabel1Tv, mLabel2Tv, mLabel3Tv, mLabel4Tv, mLabel5Tv, mLabel6Tv;

        public NavigationViewHolder(View itemView) {
            super(itemView);
            mViewColor = itemView.findViewById(R.id.view_color);
            mLabe1 = itemView.findViewById(R.id.ll_label1);
            mLabe2 = itemView.findViewById(R.id.ll_label2);
            mLabel1Tv = itemView.findViewById(R.id.tv_label1);
            mLabel2Tv = itemView.findViewById(R.id.tv_label2);
            mLabel3Tv = itemView.findViewById(R.id.tv_label3);
            mLabel4Tv = itemView.findViewById(R.id.tv_label4);
            mLabel5Tv = itemView.findViewById(R.id.tv_label5);
            mLabel6Tv = itemView.findViewById(R.id.tv_label6);
            mTitleTv = itemView.findViewById(R.id.tv_title);
            mViewHead = itemView.findViewById(R.id.view_head);
        }
    }

    public interface NavigationListener {
        void OnItemClick(String mContent);
    }
}
