package com.android.common.ui.selectmenu.holder;

import android.content.Context;
import android.view.View;

public abstract class BaseWidgetHolder<TMode> {

    protected View mRootView;

    protected Context mContext;
    
    /** holder在Tab中的位置 */
    protected int mIndex;
    public void setIndex(int index){
    	mIndex = index;
    }
    
    protected String mTitle;
    public void setTitle(String title){
    	mTitle = title;
    }
    
	public String getTitle() {
		return mTitle;
	}

    public abstract View initView();
    public abstract void refreshView(TMode data);

    public BaseWidgetHolder(Context context){
        mContext = context;
        mRootView = initView();
        if(mRootView != null) mRootView.setTag(this);
    }

    public View getRootView(){
        return mRootView;
    }
}