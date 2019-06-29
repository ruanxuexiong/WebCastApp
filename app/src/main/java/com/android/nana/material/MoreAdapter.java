package com.android.nana.material;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/2.
 */

public class MoreAdapter extends BaseAdapter {

    private Context mContext;
    private MoreListener mListener;
    private ArrayList<MoreBean.Profession> mDataList;

    public MoreAdapter(Context mContext, ArrayList<MoreBean.Profession> mDataList, MoreListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        if (null != mDataList) {
            return mDataList.size();
        } else {
            return 0;
        }
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final MoreBean.Profession item = mDataList.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_more, null);
            viewHolder.mLableTv = convertView.findViewById(R.id.tv_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mLableTv.setText(item.getName());

        viewHolder.mLableTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.OnItemClick(item.getId(),item.getName());
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        public TextView mLableTv;
    }

    public interface MoreListener {
        void OnItemClick(String id,String mContent);
    }
}
