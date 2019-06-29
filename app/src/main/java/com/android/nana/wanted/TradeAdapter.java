package com.android.nana.wanted;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.common.helper.DialogHelper;
import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/21.
 */

public class TradeAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private TradeOnCheckChangedListener mListener;
    private ArrayList<Labels> mDataList = new ArrayList<>();

    public TradeAdapter(Context mContext, ArrayList<Labels> mDataList, TradeOnCheckChangedListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trade, parent, false);
        TradeViewHolder viewHolder = new TradeViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Labels item = mDataList.get(position);
        final TradeViewHolder viewHolder = (TradeViewHolder) holder;
        viewHolder.item = item;
        viewHolder.mNameTv.setText(item.getName());
        viewHolder.mLabelName.setLabels(item.getSec());
        viewHolder.mLabelName.setMaxCheckCount(3);
        viewHolder.mLabelName.setOnCheckChangedListener(new LabelLayout.OnCheckChangeListener() {
            @Override
            public void onCheckChanged(Labels.Sec label, boolean isChecked) {

                if (mListener != null) {
                    mListener.onSelectedLabels(label, isChecked);
                }
            }

            @Override
            public void onBeyondMaxCheckCount() {
                showToast();
            }
        });
    }

    private void showToast() {

        DialogHelper.customAlert(mContext, "提示", "最多选择3个标签!", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

            }
        }, null);
    }

    class TradeViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTv;
        LabelLayout mLabelName;
        Labels item;

        public TradeViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.tv_label_name);
            mLabelName = itemView.findViewById(R.id.ll_label);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public interface TradeOnCheckChangedListener {
        void onSelectedLabels(Labels.Sec label, boolean isChecked);
    }
}
