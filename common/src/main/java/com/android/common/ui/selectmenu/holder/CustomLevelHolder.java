package com.android.common.ui.selectmenu.holder;

import android.content.Context;
import android.view.View;

import com.android.common.ui.selectmenu.SelectMenuView;

import java.util.List;

/**
 * 筛选
 */
public class CustomLevelHolder extends BaseWidgetHolder<List<String>> {

    private SelectMenuView mSelectMenuView;

    public void dismissSelectMenuView() {

        mSelectMenuView.dismissPopupWindow();
        mSelectMenuView.setOptionTitle(mIndex, mTitle);

    }

    public OnSelectedInfoListener mOnSelectedInfoListener;

    public CustomLevelHolder(Context context, SelectMenuView menuView, int index) {
        super(context);
        mIndex = index;
        mSelectMenuView = menuView;
    }

    public CustomLevelHolder(Context context, int index) {
        super(context);
        mIndex = index;
    }

    public void setCustomContentView(View v) {

        mRootView = v;

    }

    @Override
    public View initView() {

        return null;
    }

    @Override
    public void refreshView(List<String> data) {

    }

    public void setOnSelectedInfoListener(OnSelectedInfoListener onSelectedInfoListener) {
        this.mOnSelectedInfoListener = onSelectedInfoListener;
    }

    public interface OnSelectedInfoListener {

        void OnselectedInfo();

    }
}
