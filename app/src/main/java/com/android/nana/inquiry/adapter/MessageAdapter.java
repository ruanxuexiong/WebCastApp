package com.android.nana.inquiry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/4/24.
 */

public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mDataList;
    private MessageInterface mListener;

    public MessageAdapter(Context mContext, ArrayList<String> mDataList, MessageInterface mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final String item = mDataList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
        viewHolder.mMessageTv = view.findViewById(R.id.tv_message);
        viewHolder.mMessageTv.setText(item);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);

        if ("请选择您希望通话的时间".equals(item)) {
            viewHolder.mMessageTv.setTextColor(mContext.getResources().getColor(R.color.bg_light_red));
        }
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClick(item);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        TextView mMessageTv;
        LinearLayout mContentLl;
    }

    public interface MessageInterface {
        void onClick(String string);
    }

}
