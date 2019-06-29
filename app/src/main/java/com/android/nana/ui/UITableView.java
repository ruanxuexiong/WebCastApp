package com.android.nana.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.nana.R;

import java.util.ArrayList;
import java.util.List;

public class UITableView extends LinearLayout{
	
	private LayoutInflater mInflater;
	private View mMainContainer;
	private LinearLayout mViewsListContainer;
	private UITableClickLister mTableClickLister;
	
	private List<BasicItem> mBasicItems;
	
	public UITableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		mBasicItems = new ArrayList<BasicItem>();
		mInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainContainer = mInflater.inflate(R.layout.uitable_list_container, null);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mMainContainer, params);
		
		mViewsListContainer = (LinearLayout) mMainContainer.findViewById(R.id.uitable_viewsContainer);
	}
	
	public void addBasicItem(int resId, String title, String text) {
		mBasicItems.add(new BasicItem(resId, title, text));
	}
	
	public void addBasicItem(int resId, String title, String text, boolean isSpace) {
		mBasicItems.add(new BasicItem(resId, title, text, isSpace));
	}
	
	public void addBasicItem(int resId, String title, String text, boolean isSpace, boolean isArrow) {
		mBasicItems.add(new BasicItem(resId, title, text, isSpace, isArrow));
	}
	
	public View getRowView(int itemIndex, int childItem){
		return ((RelativeLayout)mViewsListContainer.getChildAt(itemIndex).findViewById(R.id.uitable_baseic_item_layout)).getChildAt(childItem);
	}
	
	public TextView getRowView(int itemIndex){
		return (TextView) mViewsListContainer.getChildAt(itemIndex).findViewById(R.id.uitable_baseic_item_txt_text);
	}
	
	public void builder() {
		mViewsListContainer.removeAllViews();
		
		for (int i = 0; i < mBasicItems.size(); i++) {
			BasicItem item = mBasicItems.get(i);

			View itemView = mInflater.inflate(R.layout.uitable_baseic_item_layout, null);
			View mVTopSpace = (View) itemView.findViewById(R.id.uitable_baseic_item_top_space);
			View mVBotSpace = (View) itemView.findViewById(R.id.uitable_baseic_item_bot_space);
			ImageView mImageView = (ImageView) itemView.findViewById(R.id.uitable_baseic_item_iv_image);
			TextView mTitleView = (TextView) itemView.findViewById(R.id.uitable_baseic_item_txt_title);
			TextView mTextView = (TextView) itemView.findViewById(R.id.uitable_baseic_item_txt_text);
			ImageView mIvArrow = (ImageView) itemView.findViewById(R.id.uitable_baseic_item_iv_arrow);
			View mVTopLine = (View) itemView.findViewById(R.id.uitable_baseic_item_top_line);
			View mVBotLine = (View) itemView.findViewById(R.id.uitable_baseic_item_bot_line);
			
			if (!item.isSpace()) { // 不为空白
				mVTopSpace.setVisibility(View.GONE);
				mVTopLine.setVisibility(View.GONE);
			} else {
				mVTopSpace.setVisibility(View.VISIBLE);
				mVTopLine.setVisibility(View.VISIBLE);
			}
			if (!item.isArrow()) {
				mIvArrow.setVisibility(View.VISIBLE);
			} else {
				mIvArrow.setVisibility(View.GONE);
			}
			
			int measuredLeft = 0;
			if (item.getResId() != 0) {
				mImageView.setImageResource(item.getResId());
				mImageView.setVisibility(View.VISIBLE);
				mImageView.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), 
								   View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
				measuredLeft = mImageView.getMeasuredWidth()+dip2px(getContext(), 22);
			} else {
				mImageView.setVisibility(View.GONE);
			}
			mTitleView.setText(item.getTitle());
			mTextView.setText(item.getText());
			
			if (i != mBasicItems.size()-1) {
				if (!(i+1<=mBasicItems.size()-1 && mBasicItems.get(i+1).isSpace())) {
					LinearLayout.LayoutParams lp = (LayoutParams) mVBotLine.getLayoutParams();
					lp.setMargins(measuredLeft, 0, 0, 0);
					mVBotLine.setLayoutParams(lp);
				}
				mVBotSpace.setVisibility(View.GONE);
			} else {
				mVBotSpace.setVisibility(View.VISIBLE);
			}
			itemView.setTag(i);
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mTableClickLister != null) {
						mTableClickLister.onClick((Integer)arg0.getTag());
					}
				}
			});

			mViewsListContainer.addView(itemView);
			
		}
	}

	public void clearView(){

		mBasicItems.clear();
		mViewsListContainer.removeAllViews();

	}
	
	public void setOnUITableClickLister(UITableClickLister tableClickLister){
		this.mTableClickLister = tableClickLister;
	}
	
	public interface UITableClickLister{
		void onClick(int index);
	}
	
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((dipValue * scale) + 0.5);
	}
	
	public class BasicItem {
		
		int resId;
		String title;
		String text;
		boolean isSpace;
		boolean isArrow;
		
		public boolean isArrow() {
			return isArrow;
		}

		public void setArrow(boolean isArrow) {
			this.isArrow = isArrow;
		}

		public int getResId() {
			return resId;
		}

		public void setResId(int resId) {
			this.resId = resId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public boolean isSpace() {
			return isSpace;
		}

		public void setSpace(boolean isSpace) {
			this.isSpace = isSpace;
		}

		public BasicItem(int resId, String title, String text) {
			super();
			this.resId = resId;
			this.title = title;
			this.text = text;
		}
		
		public BasicItem(int resId, String title, String text, boolean isSpace) {
			super();
			this.resId = resId;
			this.title = title;
			this.text = text;
			this.isSpace = isSpace;
		}
		
		public BasicItem(int resId, String title, String text, boolean isSpace, boolean isArrow) {
			super();
			this.resId = resId;
			this.title = title;
			this.text = text;
			this.isSpace = isSpace;
			this.isArrow = isArrow;
		}
		
	}

}
