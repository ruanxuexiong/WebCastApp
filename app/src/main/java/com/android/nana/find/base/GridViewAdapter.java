package com.android.nana.find.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.nana.R;

import java.util.List;

/**
 * Created by lenovo on 2018/11/1.
 */

public class GridViewAdapter  extends BaseAdapter {
    private List<AvatarModel> mDataList;
    private LayoutInflater inflater;
    private int curIndex;
    private int pageSize;

    public GridViewAdapter(Context context, List<AvatarModel> mDataList, int curIndex, int pageSize){
        inflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
        this.curIndex = curIndex;
        this.pageSize = pageSize;
    }

    @Override
    public int getCount() {
        return mDataList.size() > (curIndex + 1) * pageSize ? pageSize : (mDataList.size() - curIndex * pageSize);
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position + curIndex * pageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + curIndex * pageSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder ;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv =  convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + curIndex * pageSize，
         */
        int pos = position + curIndex * pageSize;
        viewHolder.iv.setImageResource(mDataList.get(pos).iconRes);
        return convertView;
    }
    class ViewHolder {
        public ImageView iv;
    }
}
