package com.android.nana.job.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.android.nana.downmenu.ReturnView;
import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.util.UIUtil;

/**
 * Created by lenovo on 2018/3/6.
 */

public class CompanyDropDownAdapter implements MenuAdapter {

    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private String[] titles;

    public CompanyDropDownAdapter(Context context, String[] titles, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.titles = titles;
        this.onFilterDoneListener = onFilterDoneListener;
    }

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 2) {
            return 0;
        }
        return UIUtil.dp(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        View view = parentContainer.getChildAt(position);

        switch (position) {
            case 0:
                view = createSingleGridView();
                break;
            case 1:
                view = createSingleGridView();
                break;
            case 2:
                view = createSingleGridView();
                break;
        }
        return view;
    }

    private View createSingleGridView() {
        ReturnView view = new ReturnView(mContext);
        return view;
    }
}
