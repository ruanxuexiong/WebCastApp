package com.android.nana.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/12/29.
 */

public class FunctionLabelAdapter extends RecyclerView.Adapter<FunctionLabelAdapter.FunctionViewHolder> {

    private Context mContext;
    private FunctionListener mListener;
    private ArrayList<FunctionBean.Profession> mDataList;

    public FunctionLabelAdapter(Context context, ArrayList<FunctionBean.Profession> mDataList, FunctionListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function, parent, false);
        FunctionViewHolder viewHolder = new FunctionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {

        final FunctionBean.Profession item = mDataList.get(position);
        FunctionLabelAdapter.FunctionViewHolder viewHolder = holder;
        if (position == 0) {
            viewHolder.mItemView.setVisibility(View.GONE);
        }
        viewHolder.mNameTv.setText(item.getName());

        if (Integer.valueOf(item.getNext_pro_num()) > 5) {
            viewHolder.mMoreTv.setClickable(true);
            viewHolder.mMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener) {
                        mListener.onMoreClick(item.getId(),item.getName());
                    }
                }
            });
            viewHolder.mMoreTv.setTextColor(mContext.getResources().getColor(R.color.green_4B));
        }

        for (int i = 0; i < item.getNext_pro().size(); i++) {
            switch (i) {
                case 0:
                    viewHolder.mLabel1Tv.setText(item.getNext_pro().get(i).getName());
                    viewHolder.mLabel1Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener) {
                                mListener.onLabeClick(item.getId(), item.getNext_pro().get(0).getId(), item.getNext_pro().get(0).getName(),item.getName());
                            }
                        }
                    });
                    break;
                case 1:
                    viewHolder.mLabel2Tv.setText(item.getNext_pro().get(i).getName());
                    viewHolder.mLabel2Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener) {
                                mListener.onLabeClick(item.getId(), item.getNext_pro().get(1).getId(), item.getNext_pro().get(1).getName(),item.getName());
                            }
                        }
                    });
                    break;
                case 2:
                    viewHolder.mLabel3Tv.setText(item.getNext_pro().get(i).getName());
                    viewHolder.mLabel3Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener) {
                                mListener.onLabeClick(item.getId(), item.getNext_pro().get(2).getId(), item.getNext_pro().get(2).getName(),item.getName());
                            }
                        }
                    });
                    break;
                case 3:
                    viewHolder.mLabel4Tv.setText(item.getNext_pro().get(i).getName());
                    viewHolder.mLabel4Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener) {
                                mListener.onLabeClick(item.getId(), item.getNext_pro().get(3).getId(), item.getNext_pro().get(3).getName(),item.getName());
                            }
                        }
                    });
                    break;
                case 4:
                    viewHolder.mLabel5Tv.setText(item.getNext_pro().get(4).getName());
                    viewHolder.mLabel5Tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != mListener) {
                                mListener.onLabeClick(item.getId(), item.getNext_pro().get(4).getId(), item.getNext_pro().get(4).getName(),item.getName());
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

    public static class FunctionViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTv;
        View mItemView;
        TextView mMoreTv;
        TextView mLabel1Tv, mLabel2Tv, mLabel3Tv, mLabel4Tv, mLabel5Tv;

        public FunctionViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.tv_name);
            mItemView = itemView.findViewById(R.id.view_item);
            mMoreTv = itemView.findViewById(R.id.tv_label6);
            mLabel1Tv = itemView.findViewById(R.id.tv_label1);
            mLabel2Tv = itemView.findViewById(R.id.tv_label2);
            mLabel3Tv = itemView.findViewById(R.id.tv_label3);
            mLabel4Tv = itemView.findViewById(R.id.tv_label4);
            mLabel5Tv = itemView.findViewById(R.id.tv_label5);
        }
    }

    public interface FunctionListener {
        void onLabeClick(String mOneId, String mTwoId, String mContent,String mTitle);

        void onMoreClick(String mOneId,String mTitle);
    }
}
