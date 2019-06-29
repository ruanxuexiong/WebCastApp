package com.android.common.ui.pull.pullableview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.LinkedList;
import java.util.List;


public class PullableScrollView extends ScrollView implements Pullable {
	
	private static final String STICKY = "sticky";
	private View mCurrentStickyView ;
	private Drawable mShadowDrawable;
	private List<View> mStickyViews;
	private int mStickyViewTopOffset;
	private int defaultShadowHeight = 10;
	private float density;
	private boolean redirectTouchToStickyView;
	
	private int mCustomOffset = 0;
	
	public void setCustomOffset(int y){
		mCustomOffset = y;
	}
	
	/**
	 * 当点击Sticky的时候，实现某些背景的渐变
	 */
	private Runnable mInvalidataRunnable = new Runnable() {
		@Override
		public void run() {
			if(mCurrentStickyView != null){
				int left = mCurrentStickyView.getLeft();
				int top = mCurrentStickyView.getTop();
				int right = mCurrentStickyView.getRight();
				int bottom = getScrollY() + (mCurrentStickyView.getHeight() + mStickyViewTopOffset);
				
				invalidate(left, top, right, bottom);
			}
			
			postDelayed(this, 16);
		}
	};

	public PullableScrollView(Context context) {
		super(context);
		init(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mStickyViews = new LinkedList<View>();
		density = context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * 找到设置tag的View
	 * @param viewGroup
	 */
	private void findViewByStickyTag(ViewGroup viewGroup){
		int childCount = ((ViewGroup)viewGroup).getChildCount();
		for(int i=0; i<childCount; i++){
			View child = viewGroup.getChildAt(i);
			
			if(getStringTagForView(child).contains(STICKY)){
				mStickyViews.add(child);
			}
			
			if(child instanceof ViewGroup){
				findViewByStickyTag((ViewGroup)child);
			}
		}
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed){
			findViewByStickyTag((ViewGroup)getChildAt(0));
		}
		showStickyView();
	}
	
	/**
	 * 
	 */
	private void showStickyView(){
		View curStickyView = null;
		View nextStickyView = null;
		
		for(View v : mStickyViews){
			int topOffset = v.getTop() - mCustomOffset - getScrollY();
			
			if(topOffset <= 0){
				if(curStickyView == null || topOffset > curStickyView.getTop() - getScrollY()){
					curStickyView = v;
				}
			}else{
				if(nextStickyView == null || topOffset < nextStickyView.getTop() - getScrollY()){
					nextStickyView = v;
				}
			}
		}
		
		if(curStickyView != null){
			mStickyViewTopOffset = nextStickyView == null ? 0 : Math.min(0, nextStickyView.getTop() - getScrollY() - curStickyView.getHeight());
			mCurrentStickyView = curStickyView;
			post(mInvalidataRunnable);
		}else{
			mCurrentStickyView = null;
			removeCallbacks(mInvalidataRunnable);
		}
	}
	
	private String getStringTagForView(View v){
		Object tag = v.getTag();
		return String.valueOf(tag);
	}
	
	/**
	 * 将sticky画出来
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(mCurrentStickyView != null){
			//先保存起来
			canvas.save();
			//将坐标原点移动到(0, getScrollY() + mStickyViewTopOffset)
			canvas.translate(0, mCustomOffset + getScrollY() + mStickyViewTopOffset);
			
			if(mShadowDrawable != null){
				int left = 0;
				int top = mCurrentStickyView.getHeight() + mStickyViewTopOffset;
				int right = mCurrentStickyView.getWidth();
				int bottom = top + (int)(density * defaultShadowHeight + 0.5f);
				mShadowDrawable.setBounds(left, top, right, bottom);
				mShadowDrawable.draw(canvas);
			}
			
			canvas.clipRect(0, mStickyViewTopOffset, mCurrentStickyView.getWidth(), mCurrentStickyView.getHeight());
			
			mCurrentStickyView.draw(canvas);
			
			//重置坐标原点参数
			canvas.restore();
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			redirectTouchToStickyView = true;
		}
		
		if(redirectTouchToStickyView){
			redirectTouchToStickyView = mCurrentStickyView != null;
			
			if(redirectTouchToStickyView){
				boolean c = ev.getY() <=  (mCurrentStickyView.getHeight() + mCustomOffset + mStickyViewTopOffset);
				redirectTouchToStickyView = c && ev.getX() >= mCurrentStickyView.getLeft() && ev.getX() <= mCurrentStickyView.getRight();
			}
		}
		
		if (redirectTouchToStickyView) {
			ev.offsetLocation(0, -1 * ((getScrollY()+ mCustomOffset + mStickyViewTopOffset) - mCurrentStickyView.getTop()));
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private boolean hasNotDoneActionDown = true;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(redirectTouchToStickyView){
			ev.offsetLocation(0, ((getScrollY()+ mCustomOffset + mStickyViewTopOffset) - mCurrentStickyView.getTop()));
		} 
		
		if(ev.getAction()==MotionEvent.ACTION_DOWN){
			hasNotDoneActionDown = false;
		}
		
		if(hasNotDoneActionDown){
			MotionEvent down = MotionEvent.obtain(ev);
			down.setAction(MotionEvent.ACTION_DOWN);
			super.onTouchEvent(down);
			hasNotDoneActionDown = false;
		}
		
		if(ev.getAction()==MotionEvent.ACTION_UP || ev.getAction()==MotionEvent.ACTION_CANCEL){
			hasNotDoneActionDown = true;
		}
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean canPullDown() {
		
		return getScrollY() == 0 ? true:false;
		
	}

	@Override
	public boolean canPullUp() {
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true;
		else
			return false;
	}

	private boolean mDisableEdgeEffects = true;
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		showStickyView();
		if (mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}
		handler.sendMessageDelayed(handler.obtainMessage(0, getScrollY()), 5);
	}
	
	private int tempY;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            
            int sY = (Integer) msg.obj;
            if (tempY == sY) {
            	if (mOnScrollStopListener != null) {
    				mOnScrollStopListener.onScrollStop();
    			}
			} else {
				handler.sendMessageDelayed(handler.obtainMessage(0, getScrollY()), 5);
				tempY = getScrollY();
			}
        }  
    };
	
	private OnScrollChangedListener mOnScrollChangedListener;
	
	public void setOnScrollChangedListener(OnScrollChangedListener l) {
		mOnScrollChangedListener = l;
	}
	
	public interface OnScrollChangedListener {
		void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
	}
	
	private OnScrollStopListener mOnScrollStopListener;
	
	public void setOnScrollStopListener(OnScrollStopListener l) {
		mOnScrollStopListener = l;
	}
	
	public interface OnScrollStopListener {
		void onScrollStop();
	}

	@Override
	protected float getTopFadingEdgeStrength() {
		
		if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return 0.0f;
		}
		return super.getTopFadingEdgeStrength();
	}

	@Override
	protected float getBottomFadingEdgeStrength() {
		
		if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return 0.0f;
		}
		return super.getBottomFadingEdgeStrength();
	}
}
