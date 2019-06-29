package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.common.utils.JSONUtil;
import com.android.nana.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebCastItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<JSONObject> mList;

    public WebCastItemAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<JSONObject> list) {
        mList = list;
    }

    @Override
    public int getCount() {

        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int arg0) {

        return mList != null ? mList.get(arg0) : null;
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        JSONObject item = mList.get(position);
        ViewHolder viewHolder;

        view = LayoutInflater.from(mContext).inflate(R.layout.text_item, null);
        viewHolder = new ViewHolder();
        viewHolder.mTxtName = view.findViewById(R.id.text_item_txt_name);
        view.setTag(viewHolder);

        if (position == 0 || position == 1 || position == 2) {
            viewHolder.mTxtName.setTextColor(android.graphics.Color.RED);
            viewHolder.mTxtName.setText(JSONUtil.get(item, "name", ""));
        } else {
            viewHolder.mTxtName.setText(JSONUtil.get(item, "name", ""));
        }
        return view;
    }

    class ViewHolder {
        TextView mTxtName;
    }

}
