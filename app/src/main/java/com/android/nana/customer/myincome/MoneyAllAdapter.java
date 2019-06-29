package com.android.nana.customer.myincome;

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
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/7/16.
 */

public class MoneyAllAdapter extends BaseAdapter {
    private Context mContext;
    private MoneyAllBean.Orders item;
    private String mTotalExpendiureStr;//总支出
    private String mTotalRevenueStr;//总收入
    private MoneyAllListener mListener;
    private ArrayList<MoneyAllBean.Orders> mDataList;

    public MoneyAllAdapter(Context mContext, ArrayList<MoneyAllBean.Orders> mDataList, MoneyAllListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    public void ShowData(String mTotalExpendiureStr, String mTotalRevenueStr) {
        this.mTotalExpendiureStr = mTotalExpendiureStr;
        this.mTotalRevenueStr = mTotalRevenueStr;
    }

    @Override
    public int getCount() {
        return this.mDataList.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        item = mDataList.get(i);
        view = LayoutInflater.from(mContext).inflate(R.layout.item_money_all, null);
        viewHolder.mTotalTv = view.findViewById(R.id.tv_total);
        viewHolder.mShowLl = view.findViewById(R.id.ll_show);
        viewHolder.mTotalIncomeTv = view.findViewById(R.id.tv_total_income);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mAutIv = view.findViewById(R.id.iv_aut);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_time);
        viewHolder.mMoneyTv = view.findViewById(R.id.tv_money);
        viewHolder.mItemLl = view.findViewById(R.id.ll_item);

        if (i == 0) {
            if (mTotalRevenueStr.equals("totalTixian")) {
                viewHolder.mShowLl.setVisibility(View.VISIBLE);
                viewHolder.mTotalTv.setText("累计提现：" + mTotalExpendiureStr);
            } else if (mTotalRevenueStr.equals("totalRecharge")) {
                viewHolder.mShowLl.setVisibility(View.VISIBLE);
                viewHolder.mTotalTv.setText("累计充值：" + mTotalExpendiureStr);
            } else if (mTotalExpendiureStr.equals("0") && mTotalRevenueStr.equals("0")) {
                viewHolder.mShowLl.setVisibility(View.GONE);
            } else {
                viewHolder.mShowLl.setVisibility(View.VISIBLE);
                if (mTotalExpendiureStr.equals("0")) {
                    viewHolder.mTotalTv.setVisibility(View.GONE);
                } else {
                    viewHolder.mTotalTv.setText("总支出：" + mTotalExpendiureStr);
                }
                if (mTotalRevenueStr.equals("0")) {
                    viewHolder.mTotalIncomeTv.setVisibility(View.GONE);
                } else {
                    viewHolder.mTotalIncomeTv.setText("总收入：" + mTotalRevenueStr);
                }
            }
        } else {
            viewHolder.mShowLl.setVisibility(View.GONE);
        }

        ImgLoaderManager.getInstance().showImageView(item.getAvatar(), viewHolder.mPictureIv);
        if (null != item.getStatus()) {
            if (item.getStatus().equals("1")) {
                viewHolder.mAutIv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mAutIv.setVisibility(View.GONE);
            }
        }
        viewHolder.mNameTv.setText(item.getTips());
        viewHolder.mTimeTv.setText(item.getDate());
        viewHolder.mMoneyTv.setText(item.getCharge());
        if (null != item.getType() && item.getType().equals("2") || item.getType().equals("3")) {
            viewHolder.mMoneyTv.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            viewHolder.mMoneyTv.setTextColor(mContext.getResources().getColor(R.color.green_15));
        }

        viewHolder.mItemLl.setTag(i);
        viewHolder.mItemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClickItem(view);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        LinearLayout mItemLl;
        TextView mTotalTv, mTotalIncomeTv;
        RoundImageView mPictureIv;
        ImageView mAutIv;
        LinearLayout mShowLl;
        TextView mNameTv, mTimeTv, mMoneyTv;
    }

    interface MoneyAllListener {
        void onClickItem(View view);
    }
}
