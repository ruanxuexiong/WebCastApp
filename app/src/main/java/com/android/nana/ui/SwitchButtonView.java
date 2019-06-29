package com.android.nana.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.NoticeEntity;

import java.util.ArrayList;
import java.util.List;

public class SwitchButtonView extends LinearLayout {

    private LayoutInflater mInflater;
    private View mMainContainer;
    private LinearLayout mViewsListContainer;
    private UITableClickLister mTableClickLister;

    private List<BasicItem> mBasicItems;

    public SwitchButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mBasicItems = new ArrayList<BasicItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMainContainer = mInflater.inflate(R.layout.uitable_list_container, null);

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mMainContainer, params);

        mViewsListContainer = mMainContainer.findViewById(R.id.uitable_viewsContainer);
        addBasicItem("1", "0", "0", "赞");
//        addBasicItem("1", "1", "0", "评论");
//        addBasicItem("1", "2", "0", "关注");
    }

    public void addBasicItem(String id, String type, String status, String title) {
        mBasicItems.add(new BasicItem(id, type, status, title));
    }

    public void setData(NoticeEntity noticeEntity) {
        if (mBasicItems == null || mBasicItems.size() == 0)
            return;
        if (noticeEntity.getData().getNotices() == null || noticeEntity.getData().getNotices().size() == 0)
            return;
        for (int i = 0; i < noticeEntity.getData().getNotices().size(); i++) {
            for (int j = 0; j < mBasicItems.size(); j++) {
                if (noticeEntity.getData().getNotices().get(i).getType().equals(mBasicItems.get(j).getType())) {
                    mBasicItems.get(j).setStatus(noticeEntity.getData().getNotices().get(i).getStatus());
                }
            }
        }
        builder();
    }

    public void upDataSuccess(int position, String status) {
        if (mBasicItems != null && position < mBasicItems.size()) {
            mBasicItems.get(position).setStatus(status);
        }

    }

    public View getRowView(int itemIndex, int childItem) {
        return ((RelativeLayout) mViewsListContainer.getChildAt(itemIndex).findViewById(R.id.uitable_baseic_item_layout)).getChildAt(childItem);
    }

    public TextView getRowView(int itemIndex) {
        return (TextView) mViewsListContainer.getChildAt(itemIndex).findViewById(R.id.uitable_baseic_item_txt_text);
    }

    public void builder() {
        mViewsListContainer.removeAllViews();

        for (int i = 0; i < mBasicItems.size(); i++) {
            final BasicItem item = mBasicItems.get(i);

            View itemView = mInflater.inflate(R.layout.switch_btn_item, null);
            View mVTopSpace = itemView.findViewById(R.id.uitable_baseic_item_top_space);
            View mVBotSpace = itemView.findViewById(R.id.uitable_baseic_item_bot_space);
            ImageView mImageView = itemView.findViewById(R.id.uitable_baseic_item_iv_image);
            TextView mTitleView = itemView.findViewById(R.id.uitable_baseic_item_txt_title);
            Switch switchBtn = itemView.findViewById(R.id.switch_btn);
            View mVTopLine = itemView.findViewById(R.id.uitable_baseic_item_top_line);
            View mVBotLine = itemView.findViewById(R.id.uitable_baseic_item_bot_line);

//			if (!item.isSpace()) { // 不为空白
//				mVTopSpace.setVisibility(View.GONE);
//				mVTopLine.setVisibility(View.GONE);
//			} else {
//				mVTopSpace.setVisibility(View.VISIBLE);
//				mVTopLine.setVisibility(View.VISIBLE);
//			}
            if (!item.getStatus().equals("1")) {
                switchBtn.setChecked(false);
            } else {
                switchBtn.setChecked(true);
            }

			int measuredLeft = 0;
//			if (item.getResId() != 0) {
//				mImageView.setImageResource(item.getResId());
//				mImageView.setVisibility(View.VISIBLE);
//				mImageView.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED),
//								   MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
//				measuredLeft = mImageView.getMeasuredWidth()+dip2px(getContext(), 22);
//			} else {
//				mImageView.setVisibility(View.GONE);
//			}
            mTitleView.setText(item.getTitle());
			if (i != mBasicItems.size()-1) {
//				if (!(i+1<=mBasicItems.size()-1 )) {
//					LayoutParams lp = (LayoutParams) mVBotLine.getLayoutParams();
//					lp.setMargins(measuredLeft, 0, 0, 0);
//					mVBotLine.setLayoutParams(lp);
//				}
                mVBotLine.setVisibility(View.GONE);
			} else {
                mVBotLine.setVisibility(View.VISIBLE);
			}
//			itemView.setTag(i);
//			itemView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if (mTableClickLister != null) {
//						mTableClickLister.onClick((Integer)arg0.getTag());
//					}
//				}
//			});
            switchBtn.setTag(i);
            switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String STATUS;
                    if(b){
                        STATUS="1";//开启通知
                    }else{
                        STATUS="0";//关闭通知
                    }
                    if (mTableClickLister != null) {
                        mTableClickLister.onClick(item.getType(), STATUS, (Integer) compoundButton.getTag());
                    }
                }
            });
            mViewsListContainer.addView(itemView);

        }
    }

    public void clearView() {

        mBasicItems.clear();
        mViewsListContainer.removeAllViews();

    }

    public void setOnUITableClickLister(UITableClickLister tableClickLister) {
        this.mTableClickLister = tableClickLister;
    }

    public interface UITableClickLister {
        void onClick(String type, String status, int item);
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5);
    }

    public class BasicItem {

        private String id;
        private String type;
        private String status;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BasicItem(String id, String type, String status, String title) {
            super();
            this.id = id;
            this.type = type;
            this.status = status;
            this.title = title;
        }

    }

}
