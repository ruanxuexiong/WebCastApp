package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.util.ImgLoaderManager;

import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * Created by lenovo on 2017/10/17.
 */

public class LatelyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Conversation> mList;
    private LateInterface mListener;
    private boolean isSelect;//判断是否显示选择框

    public LatelyAdapter(Context mContext, List<Conversation> conversationList, boolean isSelect, LateInterface mListener) {
        this.mContext = mContext;
        this.mList = conversationList;
        this.isSelect = isSelect;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Conversation mItem = mList.get(position);

        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_groups, null);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mItemContent = view.findViewById(R.id.content_item);
        viewHolder.mCheckBox = view.findViewById(R.id.dis_select);
        viewHolder.mPictureIv = view.findViewById(R.id.home_list_item_iv_picture);
        view.setTag(viewHolder);

        if (isSelect) {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.mCheckBox.setChecked(mItem.isTop());
        }

        ImgLoaderManager.getInstance().showImageView(mItem.getPortraitUrl(), viewHolder.mPictureIv);
        viewHolder.mNameTv.setText(mItem.getConversationTitle());

        viewHolder.mItemContent.setTag(position);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mItemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view, finalViewHolder.mCheckBox, position);
                }
            }
        });
        return view;
    }

    final static class ViewHolder {
        ImageView mPictureIv;
        TextView mNameTv;
        LinearLayout mItemContent;
        CheckBox mCheckBox;
    }

    public interface LateInterface {
        void onItemClick(View view, CheckBox mCheckBox, int position);
    }
}
