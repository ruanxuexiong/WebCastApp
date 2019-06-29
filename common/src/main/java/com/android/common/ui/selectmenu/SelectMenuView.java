package com.android.common.ui.selectmenu;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.ui.selectmenu.holder.BaseWidgetHolder;

/**
 * 搜索菜单栏
 */
public class SelectMenuView extends LinearLayout {

    private Context mContext;

    private View mPopupWindowView;

    private RelativeLayout mMainContentLayout;

    @SuppressWarnings("rawtypes")
    private BaseWidgetHolder[] mBaseWidgetHolders;

    /**
     * 与外部通信传递数据的接口
     */
    private OnMenuSelectDataChangedListener mOnMenuSelectDataChangedListener;

    private LinearLayout mLlTabLayout;
    private RelativeLayout mContentLayout;

    public SelectMenuView(Context context) {
        super(context);
        mContext = context;
    }

    public SelectMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initLevelHolder(BaseWidgetHolder<?>... holder) {

        for (int i = 0; i < holder.length; i++) holder[i].setIndex(i);

        mBaseWidgetHolders = holder;

    }

    public void postInvalidateView() {
        View.inflate(mContext, R.layout.select_menu, this);
        this.setBackgroundColor(mContext.getResources().getColor(R.color.black_32));
        this.getBackground().mutate().setAlpha(160);

        mPopupWindowView = View.inflate(mContext, R.layout.select_menu_view, null);
        mMainContentLayout = (RelativeLayout) mPopupWindowView.findViewById(R.id.select_menu_view_rl_main);

        mLlTabLayout = (LinearLayout) findViewById(R.id.select_menu_ll_tab);
        mContentLayout = (RelativeLayout) findViewById(R.id.select_menu_rl_content);

        for (int i = 0; mBaseWidgetHolders != null && i < mBaseWidgetHolders.length; i++) {
            final int index = i;
            View mVTabItem = View.inflate(mContext, R.layout.select_menu_tab_item, null);
            LinearLayout mLlTabItem = (LinearLayout) mVTabItem.findViewById(R.id.select_menu_tab_item_ll_layout);
            TextView mTxtText = (TextView) mVTabItem.findViewById(R.id.select_menu_tab_item_txt_text);
            View mVLine = mVTabItem.findViewById(R.id.select_menu_tab_item_v_line);

            mTxtText.setText(mBaseWidgetHolders[i].getTitle());
            if (i == mBaseWidgetHolders.length - 1) {
                mVLine.setVisibility(View.GONE);
            } else {
                mVLine.setVisibility(View.VISIBLE);
            }

            mLlTabItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (mOnMenuSelectDataChangedListener != null)
                        mOnMenuSelectDataChangedListener.onViewClicked(arg0);

                    mMainContentLayout.removeAllViews();
                    //将我们已经创建好的ViewHolder拿出，取出其中的View贴到内容View中
                    mMainContentLayout.addView(mBaseWidgetHolders[index].getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    //处理弹窗动作
                    extendsContent();

                    for (int j = 0; j < mBaseWidgetHolders.length; j++) {
                        View mItem = mLlTabLayout.getChildAt(j);
                        TextView mTxtText = (TextView) mItem.findViewById(R.id.select_menu_tab_item_txt_text);
                        ImageView mIvArrow = (ImageView) mItem.findViewById(R.id.select_menu_tab_item_iv_arrow);
                        if (index == j) {
                            mTxtText.setTextColor(getResources().getColor(R.color.right));
                            mIvArrow.setImageResource(R.drawable.icon_up_green);
                        } else {
                            mTxtText.setTextColor(getResources().getColor(R.color.grey_96));
                            mIvArrow.setImageResource(R.drawable.icon_down);
                        }
                    }
                }
            });

            mLlTabLayout.addView(mVTabItem, (int) (getScreenWidth((Activity) mContext) / mBaseWidgetHolders.length), ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // 点击黑色半透明部分，菜单收回
        mContentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissPopupWindow();

            }
        });

    }

    public void setOptionTitle(int index, String text) {

        View mItem = mLlTabLayout.getChildAt(index);
        TextView mTxtText = (TextView) mItem.findViewById(R.id.select_menu_tab_item_txt_text);
        ImageView mIvArrow = (ImageView) mItem.findViewById(R.id.select_menu_tab_item_iv_arrow);
        mTxtText.setText(text);
        mTxtText.setTextColor(getResources().getColor(R.color.right));
        mIvArrow.setImageResource(R.drawable.icon_down);
    }

    public void dismissPopup() {
        mContentLayout.removeAllViews();
    }


    private void extendsContent() {
        mContentLayout.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentLayout.addView(mPopupWindowView, params);
    }

    public void dismissPopupWindow() {
        mContentLayout.removeAllViews();
        setTabClose();
    }

    public interface OnMenuSelectDataChangedListener {

        void onViewClicked(View view);

    }

    private void setTabClose() {

        for (int i = 0; mBaseWidgetHolders != null && i < mBaseWidgetHolders.length; i++) {

            View mItem = mLlTabLayout.getChildAt(i);
            TextView mTxtText = (TextView) mItem.findViewById(R.id.select_menu_tab_item_txt_text);
            ImageView mIvArrow = (ImageView) mItem.findViewById(R.id.select_menu_tab_item_iv_arrow);
            mTxtText.setTextColor(getResources().getColor(R.color.grey_64));
            mIvArrow.setImageResource(R.drawable.icon_down);

        }

    }

    /**
     * 获取屏幕的宽度
     */
    public int getScreenWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}