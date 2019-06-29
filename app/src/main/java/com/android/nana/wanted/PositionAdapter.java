package com.android.nana.wanted;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.viewpager.ViewHolder;

import java.util.List;

/**
 * Created by THINK on 2017/6/28.
 */

public class PositionAdapter extends BaseAdapter {

    private Context mContext;
    List<Position> mDataList;

    private int selectedPos = -1;
    private int mSelectedBackgroundResource;
    private int mNormalBackgroundResource;
    private boolean hasDivider = true;

    public PositionAdapter(Context context, List<Position> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
    }

    public void setNormalBackgroundResource(int normalBackgroundResource) {
        this.mNormalBackgroundResource = normalBackgroundResource;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }

    public void setSelectedBackground(int res) {
        mSelectedBackgroundResource = res;
    }


    public void setSelectedItem(int position) {
        selectedPos = position;
        notifyDataSetChanged();
    }

    public void setData(List<Position> data) {
        this.mDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (this.mDataList == null)
            return 0;
        return this.mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mDataList == null)
            return null;
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_dict_item, parent, false);
        }

        LinearLayout itemLayout = ViewHolder.get(convertView, R.id.dict_item_ly);

        TextView nameText = ViewHolder.get(convertView, R.id.dict_item_textview);

        TextView dividerTextView = ViewHolder.get(convertView, R.id.dict_item_divider);

        final Position mItem = mDataList.get(position);

        nameText.setText(mItem.getName());
        nameText.setTag(position);

        convertView.setSelected(selectedPos == position);
        nameText.setSelected(selectedPos == position);

        if (selectedPos == position) {
           // itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.grey_e5));
            nameText.setTextColor(mContext.getResources().getColor(R.color.main_blue));
        } else {
            nameText.setTextColor(mContext.getResources().getColor(R.color.black));
          //  itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }


        if (mNormalBackgroundResource == 0)
            mNormalBackgroundResource = R.color.white;

        if (mSelectedBackgroundResource != 0)
            itemLayout.setBackgroundResource(selectedPos == position ? mSelectedBackgroundResource : mNormalBackgroundResource);

        dividerTextView.setVisibility(hasDivider ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }
}
