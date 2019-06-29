package com.android.nana.card.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.card.bean.SearchCardBean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/2/5.
 */

public class SearchCardAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private SearchCardListener mListener;
    private ArrayList<SearchCardBean.Cards> mDataList = new ArrayList<>();

    public SearchCardAdapter(Context mContext, ArrayList<SearchCardBean.Cards> mDataList,SearchCardListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_list, parent, false);
        final MakeCardViewHolder viewHolder = new MakeCardViewHolder(view);
        viewHolder.mEditIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onEditItemClick(viewHolder.item);
                }
            }
        });

        viewHolder.mCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCardItemClick(viewHolder.mCardIv,viewHolder.item);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchCardBean.Cards mItem = mDataList.get(position);
        MakeCardViewHolder viewHolder = (MakeCardViewHolder) holder;
        viewHolder.item = mItem;
        ImgLoaderManager.getInstance().showImageView(mItem.getCard(), viewHolder.mCardIv);

    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    class MakeCardViewHolder extends RecyclerView.ViewHolder {
        ImageView mCardIv;
        ImageView mEditIv;
        SearchCardBean.Cards item;

        public MakeCardViewHolder(View itemView) {
            super(itemView);
            mCardIv = itemView.findViewById(R.id.iv_card);
            mEditIv = itemView.findViewById(R.id.iv_edit);
        }
    }

    public interface SearchCardListener {
        void onEditItemClick(SearchCardBean.Cards item);

        void onCardItemClick(View view,SearchCardBean.Cards item);
    }
}
