package com.android.common.ui.selectmenu.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.ui.selectmenu.SelectMenuView;

import java.util.List;

/**
 * 综合排序
 */
public class SingleLevelHolder extends BaseWidgetHolder<List<String>> {

    private SelectMenuView mSelectMenuView;

    private RightAdapter mRightAdapter;

    private ListView mListView;

    private OnSingleLevelSelectedListener mOnSingleLevelSelectedListener;

    public SingleLevelHolder(Context context, SelectMenuView menuView) {
        super(context);
        mSelectMenuView = menuView;
    }

    public SingleLevelHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.single_level_holder, null);

        mListView = (ListView) view.findViewById(R.id.single_level_lv_list);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mRightSelectedIndex = position;
                mRightAdapter.notifyDataSetChanged();

                String text = (String) parent.getItemAtPosition(position);
                if (mOnSingleLevelSelectedListener != null) {
                    mOnSingleLevelSelectedListener.onSingleLevelSelected(text);
                }
                mSelectMenuView.dismissPopupWindow();
                mSelectMenuView.setOptionTitle(mIndex, text);

            }
        });

        return view;
    }

    @Override
    public void refreshView(List<String> data) {

    }

    public void refreshData(List<String> list) {

        mRightAdapter = new RightAdapter();
        mListView.setAdapter(mRightAdapter);
        mRightAdapter.setDataList(list, -1);
        mRightAdapter.notifyDataSetChanged();

    }

    public void setOnSingleLevelSelectedListener(OnSingleLevelSelectedListener onSingleLevelSelectedListener) {

        mOnSingleLevelSelectedListener = onSingleLevelSelectedListener;

    }

    public interface OnSingleLevelSelectedListener {

        void onSingleLevelSelected(String info);

    }

    private int mRightSelectedIndex;

    public class RightAdapter extends BaseAdapter {

        private List<String> mRightDataList;

        public RightAdapter() {

        }

        public void setDataList(List<String> list, int rightSelectedIndex) {
            this.mRightDataList = list;
            mRightSelectedIndex = rightSelectedIndex;
        }

        @Override
        public int getCount() {
            return mRightDataList != null ? mRightDataList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mRightDataList != null ? mRightDataList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RightViewHolder holder;
            if (convertView == null) {
                holder = new RightViewHolder();
                convertView = View.inflate(mContext, R.layout.single_level_item, null);
                holder.rightText = (TextView) convertView.findViewById(R.id.single_level_item_child);
                holder.rightText.setPadding(dip2px(mContext, 15), 0, 0, 0);
                holder.mRlLayout = (RelativeLayout) convertView.findViewById(R.id.single_level_item_layout);
                holder.mRlLayout.setBackgroundResource(R.color.white);
                convertView.setTag(holder);
            } else {
                holder = (RightViewHolder) convertView.getTag();
            }

            holder.rightText.setText(mRightDataList.get(position));
            if (mRightSelectedIndex == position) {
                holder.rightText.setTextColor(mContext.getResources().getColor(R.color.right));
            } else {
                holder.rightText.setTextColor(mContext.getResources().getColor(R.color.grey_64));
            }

            return convertView;
        }
    }

    private class RightViewHolder {
        TextView rightText;
        RelativeLayout mRlLayout;
    }

    /**
     * dip转为 px
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5);
    }

}
