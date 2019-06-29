package com.android.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.helper.UIHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class CustomWindowDialog<T> extends Dialog {

    private Context mContext;

    private Collection<T> mList;

    private CustomWindowAdapter mAdapter;

    public CustomWindowDialog(Context context) {
        super(context, R.style.myDialog);
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mAdapter = new CustomWindowAdapter();
    }

    public void initDialog(AdapterView.OnItemClickListener onItemClickListener, String... str) {
        for (int i = 0; i < str.length; i++) {
            mList.add((T) str[i]);
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_window_list, null);

        ListView mListView = (ListView) view.findViewById(R.id.custom_window_list_lv);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.setContentView(view);
    }

    public void initDialog(AdapterView.OnItemClickListener onItemClickListener, List<T> list) {
        mList = list;

        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_window_list, null);

        ListView mListView = (ListView) view.findViewById(R.id.custom_window_list_lv);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.setContentView(view);
    }

    class CustomWindowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList != null ? (T) ((List) mList).get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    UIHelper.dip2px(mContext, 50)));
            textView.setPadding(UIHelper.dip2px(mContext, 15), 0, 0, 0);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(mContext.getResources().getColor(R.color.black_32));
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            textView.setText(getItem(position).toString());

            return textView;
        }
    }
}
