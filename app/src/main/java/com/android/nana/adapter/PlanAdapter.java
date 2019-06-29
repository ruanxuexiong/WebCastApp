package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/6/21.
 */

public class PlanAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mList;

    public PlanAdapter(Context mContext, ArrayList<String> mArrayList) {
        this.mContext = mContext;
        this.mList = mArrayList;
    }

    @Override
    public int getCount() {
        return mList.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        view = LayoutInflater.from(mContext).inflate(R.layout.item_plan, null);
        viewHolder.imageView = view.findViewById(R.id.iv_img);
        return view;
    }

    class ViewHolder {
        private ImageView imageView;
    }

}
