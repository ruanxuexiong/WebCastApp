package com.android.common.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPagerExtend extends ViewPager {

	private float lastActionDownX, lastActionDownY;

	private int xOffset;
	private int yOffset;

	public ChildViewPagerExtend(Context context) {
		super(context);

	}

	public ChildViewPagerExtend(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// 当拦截触摸事件到达此位置的时候，返回true，
		// 说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// 每次进行onTouch事件都记录当前的按下的坐标
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			// 记录按下时候的坐标
			// 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
			getParent().requestDisallowInterceptTouchEvent(true);
			lastActionDownX = arg0.getX();
			lastActionDownY = arg0.getY();
		}
		if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
			xOffset = (int) (arg0.getX() - lastActionDownX);
			yOffset = (int) (arg0.getY() - lastActionDownY);
			
			if (Math.abs(xOffset) > Math.abs(yOffset)) {
				 getParent().requestDisallowInterceptTouchEvent(true);
			} else {
				 getParent().requestDisallowInterceptTouchEvent(false);
			}
		}
		if (arg0.getAction() == MotionEvent.ACTION_UP) {
			if (Math.abs(xOffset) <= 5 && Math.abs(yOffset) <= 5) {
				onSingleTouch();
				getParent().requestDisallowInterceptTouchEvent(false);
				return true;
			}
		}

		return super.onTouchEvent(arg0);
	}
	
	private OnSingleTouchListener onSingleTouchListener;
	
    /** 
     * 单击 
     */  
    public void onSingleTouch() {  
        if (onSingleTouchListener!= null) {  
            onSingleTouchListener.onSingleTouch();  
        }  
    }  
  
    /** 
     * 创建点击事件接口 
     * 
     */  
    public interface OnSingleTouchListener {  
        void onSingleTouch();
    }  
  
    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {  
        this.onSingleTouchListener = onSingleTouchListener;  
    }

}