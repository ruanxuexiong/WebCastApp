package com.android.nana.card.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.card.bean.CardList;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/25.
 */

public class SendCardAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private SendCardListener mListener;
    private ArrayList<CardList.Cards> mDataList = new ArrayList<>();

    public SendCardAdapter(Context mContext, ArrayList<CardList.Cards> mDataList, SendCardListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_send_card, parent, false);
        final SendCardViewHolder viewHolder = new SendCardViewHolder(view);

     /*   viewHolder.mCardBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCheckBoxItemClick(viewHolder.item);
                }
            }
        });*/
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CardList.Cards mItem = mDataList.get(position);
        final SendCardViewHolder viewHolder = (SendCardViewHolder) holder;
        viewHolder.item = mItem;
        ImgLoaderManager.getInstance().showImageView(mItem.getCard(), viewHolder.mCardIv);

        viewHolder.mCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CardList.Cards item : mDataList) {
                    item.setSelected(false);
                }
                mDataList.get(position).setSelected(true);
                if (null != mListener) {
                    mListener.onCheckBoxItemClick(viewHolder.item);
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.mCardBox.setSelected(mDataList.get(position).isSelected());
        viewHolder.mCardBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CardList.Cards item : mDataList) {
                    item.setSelected(false);
                }
                mDataList.get(position).setSelected(true);
                if (null != mListener) {
                    mListener.onCheckBoxItemClick(viewHolder.item);
                }
                notifyDataSetChanged();
            }
        });
        if (mItem.isSelected()) {
            viewHolder.mCardBox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_new_choice));
        } else {
            viewHolder.mCardBox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_checkbox_unchecked));
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    class SendCardViewHolder extends RecyclerView.ViewHolder {
        ImageView mCardIv;
        ImageView mCardBox;
        CardList.Cards item;

        public SendCardViewHolder(View itemView) {
            super(itemView);
            mCardIv = itemView.findViewById(R.id.iv_card);
            mCardBox = itemView.findViewById(R.id.iv_box);
        }
    }

    public interface SendCardListener {
        void onCheckBoxItemClick(CardList.Cards item);
    }

}
