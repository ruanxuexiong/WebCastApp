package com.android.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.common.BaseApplication;
import com.android.common.R;
import com.android.common.models.AdvertisementDisplayModel;
import com.android.common.utils.JSONUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressLint("HandlerLeak")
public class SlideShowView extends FrameLayout {

	private Context mContext;
	private final static int IMAGE_COUNT = 5;
	public boolean mIsAutoPlay = true;
	public boolean mIsShowPoint= true;
	public boolean mIsLoop = true;
	private int[] imagesResIds;
	private LinearLayout mSlideLayout;
	private List<ImageView> imageViewsList;
	private List<View> dotViewsList;
	private List<AdvertisementDisplayModel> imagesUrls;
	private ChildViewPagerExtend viewPager;
	
	private Class<?> mCls;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private BaseApplication mBaseApplication;

	private int mDotSize = 18;
	private int mSpaceSize = 14;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}

	};
	
	public SlideShowView(Context context) {
		this(context, null);
		mContext = context;
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setImageResIds(int... pictureIds){
		imagesResIds = pictureIds;
	}
	
	/**
	 * 设置viewpager跳转的Activity
	 * @param cls
	 */
	public void setViewPagerStartActivity(Class<?> cls){
		mCls = cls;
	}

	public void play(BaseApplication application, List<AdvertisementDisplayModel> pictures) {
		mBaseApplication = application;
		syncInitPictures(pictures);
		initUI(mContext);
		if (mIsAutoPlay) {
			startPlay();
		}
	}

	public void play(BaseApplication application, AdvertisementDisplayModel... pictures) {
		mBaseApplication = application;
		if (pictures == null || pictures.length == 0) return;

		ArrayList<AdvertisementDisplayModel> list = new ArrayList<AdvertisementDisplayModel>();
		for (AdvertisementDisplayModel advertisementDisplayModel : pictures) {
			list.add(advertisementDisplayModel);
		}
		syncInitPictures(list);
		initUI(mContext);
		if (mIsAutoPlay) {
			startPlay();
		}
	}
	
	public void playByStrings(BaseApplication application, List<String> pictureUrls){
		mBaseApplication = application;
		
		ArrayList<AdvertisementDisplayModel> list = new ArrayList<AdvertisementDisplayModel>();
		for (String pictureUrl : pictureUrls) {
			
			AdvertisementDisplayModel model = new AdvertisementDisplayModel();
			model.mPictureUrl = pictureUrl;
			list.add(model);
		}
		syncInitPictures(list);
		initUI(mContext);
		if (mIsAutoPlay) {
			startPlay();
		}
	}
	
	public void playByJsonArray(BaseApplication application,List<JSONObject> dataSource,String pictureFiledName){
		
		mBaseApplication = application;
		
		ArrayList<AdvertisementDisplayModel> list = new ArrayList<AdvertisementDisplayModel>();

		for (JSONObject object : dataSource) {
			
			AdvertisementDisplayModel model = new AdvertisementDisplayModel();
			model.mPictureUrl = JSONUtil.get(object, pictureFiledName, "");
			model.mLinkUrl = JSONUtil.get(object, "", "");
			list.add(model);
		}
		
		syncInitPictures(list);
		initUI(mContext);
		if (mIsAutoPlay) {
			startPlay();
		}
		
	}
	
	public List<JSONObject> mDataSource;
	public void playByJsonArray(BaseApplication application,List<JSONObject> dataSource,String pictureFiledName, String linkUrl, String isLink){
		
		mDataSource = dataSource;
		mBaseApplication = application;
		
		ArrayList<AdvertisementDisplayModel> list = new ArrayList<>();
		
		for (JSONObject object : dataSource) {
			
			AdvertisementDisplayModel model = new AdvertisementDisplayModel();
			model.mPictureUrl =JSONUtil.get(object, pictureFiledName,"");
			model.mLinkUrl = JSONUtil.get(object, linkUrl, "");
			model.mIsLink = JSONUtil.get(object, isLink, false);
			list.add(model);
		}
		
		syncInitPictures(list);
		initUI(mContext);
		if (mIsAutoPlay) {
			startPlay();
		}
	}

	public void playByJson(BaseApplication application, List<View> views){

		imageViewsList = new ArrayList<>();
		dotViewsList = new ArrayList<>();

		initUIView(mContext, views);
		if (mIsAutoPlay) {
			startPlay();
		}
	}
	
	public void playByJsonBitmap(BaseApplication application,List<JSONObject> dataSource, List<Bitmap> mList, String pictureFiledName, String linkUrl, String isLink){
		
		mBaseApplication = application;
		
		ArrayList<AdvertisementDisplayModel> list = new ArrayList<AdvertisementDisplayModel>();
		
		for (JSONObject object : dataSource) {
			
			AdvertisementDisplayModel model = new AdvertisementDisplayModel();
			model.mPictureUrl =JSONUtil.getPictureUrl(object, pictureFiledName);
			model.mLinkUrl = JSONUtil.get(object, linkUrl, "");
			model.mIsLink = JSONUtil.get(object, isLink, false);
			list.add(model);
		}
		
		syncInitPictures(list);
		initUI(mContext, mList);
		if (mIsAutoPlay) {
			startPlay();
		}
		
	}

	/**
	 * 开始轮播图切换
	 */
	private void startPlay() {
		scheduledExecutorService = Executors .newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 5, 5, TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	public void stopPlay() {
		if(scheduledExecutorService!=null)
		   scheduledExecutorService.shutdown();
	}

	public void syncInitPictures(List<AdvertisementDisplayModel> pictureUrl) {
		imagesUrls = pictureUrl;
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();
	}

	/**
	 * 初始化Views等UI
	 */
	public ScaleType mDefaultScaleType = ScaleType.CENTER_CROP;

	private void initUI(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
		mSlideLayout = (LinearLayout) findViewById(R.id.slide_list);
		mSlideLayout.removeAllViews();
		imageViewsList.clear();
		dotViewsList.clear();

		if (imagesUrls != null && imagesUrls.size() > 0) {
			for (AdvertisementDisplayModel model : imagesUrls) {
				ImageView view = new ImageView(context);
				view.setScaleType(mDefaultScaleType);

				Picasso.with(context).load(model.mPictureUrl).error(R.mipmap.icon_defult)
						.placeholder(R.mipmap.icon_defult).fit().into(view);
//				ImgLoaderManager.getInstance().showImageView(model.mPictureUrl, view);

				imageViewsList.add(view);
				
				View dotView = new View(context);
				LayoutParams layoutParams = new LayoutParams(mDotSize, mDotSize);
				dotView.setLayoutParams(layoutParams);
				
				mSlideLayout.addView(dotView);
				
				View space = new View(context);
				LayoutParams spaceLayout = new LayoutParams(mSpaceSize, mSpaceSize);
				space.setLayoutParams(spaceLayout);
				mSlideLayout.addView(space);
				
				dotViewsList.add(dotView);
			}
		} else {

			for (int imageID : imagesResIds) {
				ImageView view = new ImageView(context);
				view.setImageResource(imageID);
				view.setScaleType(mDefaultScaleType);
				imageViewsList.add(view);

				View dotView = new View(context);
				LayoutParams layoutParams = new LayoutParams(mDotSize, mDotSize);
				dotView.setLayoutParams(layoutParams);
				mSlideLayout.addView(dotView);

				View space = new View(context);
				LayoutParams spaceLayout = new LayoutParams(mSpaceSize, mSpaceSize);
				space.setLayoutParams(spaceLayout);
				mSlideLayout.addView(space);

				dotViewsList.add(dotView);
			}
		}
		
		if(mIsShowPoint){
			mSlideLayout.setVisibility(View.VISIBLE);
		} else {
			mSlideLayout.setVisibility(View.GONE);
		}

		viewPager = (ChildViewPagerExtend) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);

		viewPager.setAdapter(new MyPagerAdapter(mOnSlideShowViewClickListener));
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setCurrentItem(currentItem);
	}
	
	private void initUI(Context context, List<Bitmap> mList) {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
		mSlideLayout = (LinearLayout) findViewById(R.id.slide_list);
		mSlideLayout.setPadding(10, 10, 10, 100);
		mSlideLayout.removeAllViews();
		imageViewsList.clear();
		dotViewsList.clear();
		
		if (mList != null && mList.size() > 0) {
			for (Bitmap model : mList) {
				ImageView view = new ImageView(context);
				view.setScaleType(mDefaultScaleType);
				view.setImageBitmap(model);
				imageViewsList.add(view);
				
				View dotView = new View(context);
				LayoutParams layoutParams = new LayoutParams(mDotSize, mDotSize);
				dotView.setLayoutParams(layoutParams);
				
				mSlideLayout.addView(dotView);
				
				View space = new View(context);
				LayoutParams spaceLayout = new LayoutParams(mSpaceSize, mSpaceSize);
				space.setLayoutParams(spaceLayout);
				mSlideLayout.addView(space);
				
				dotViewsList.add(dotView);
			}
		}
		
		if(mIsShowPoint){
			mSlideLayout.setVisibility(View.VISIBLE);
		} else {
			mSlideLayout.setVisibility(View.GONE);
		}
		
		viewPager = (ChildViewPagerExtend) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		
		viewPager.setAdapter(new MyPagerAdapter(null));
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setCurrentItem(currentItem);
	}

	private void initUIView(Context context, List<View> views) {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
		mSlideLayout = (LinearLayout) findViewById(R.id.slide_list);
		mSlideLayout.removeAllViews();
		imageViewsList.clear();
		dotViewsList.clear();

		if (views != null && views.size() > 0) {
			for (View view : views) {
				View dotView = new View(context);
				LayoutParams layoutParams = new LayoutParams(mDotSize, mDotSize);
				dotView.setLayoutParams(layoutParams);

				mSlideLayout.addView(dotView);

				View space = new View(context);
				LayoutParams spaceLayout = new LayoutParams(mSpaceSize, mSpaceSize);
				space.setLayoutParams(spaceLayout);
				mSlideLayout.addView(space);

				dotViewsList.add(dotView);

				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnSlideShowViewClickListener != null) {
							mOnSlideShowViewClickListener.jump(currentItem);
						}
					}
				});

			}
		}

		mSlideLayout.setVisibility(View.GONE);

		viewPager = (ChildViewPagerExtend) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);

		viewPager.setAdapter(new CustomPagerAdapter(views, mOnSlideShowViewClickListener));
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setCurrentItem(currentItem);
	}

	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		private OnSlideShowViewClickListener mClickListener;

		public MyPagerAdapter(OnSlideShowViewClickListener l) {
			mClickListener = l;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (position <= imageViewsList.size()-1) {
				((ChildViewPagerExtend)container).removeView(imageViewsList.get(position));
			}
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			((ChildViewPagerExtend)container).removeView(imageViewsList.get(position));
			((ChildViewPagerExtend)container).addView(imageViewsList.get(position), 0);
			((ChildViewPagerExtend)container).setOnSingleTouchListener(new ChildViewPagerExtend.OnSingleTouchListener() {
				@Override
				public void onSingleTouch() {

					if (imagesUrls != null && imagesUrls.size() > 0) {
						AdvertisementDisplayModel model = imagesUrls.get(currentItem);
						if (model != null && model.mIsLink && !TextUtils.isEmpty(model.mLinkUrl) && mCls != null) {
							Intent intent = new Intent(mContext, mCls);
							intent.putExtra("Url", model.mLinkUrl);
							mContext.startActivity(intent);
						}
					}

					if (mClickListener != null) {
						mClickListener.jump(position);
					}

				}
			});
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author caizhiming
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
//			switch (arg0) {
//			case 1:// 手势滑动，空闲中
//				mIsAutoPlay = false;
//			
//				break;
//			case 2:// 界面切换中
//				mIsAutoPlay = true;
//		
//				break;
//			case 0:// 滑动结束，即切换完毕或者加载完毕
//					// 当前为最后一张，此时从右向左滑，则切换到第一张
//				if(mIsLoop){
//					if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !mIsAutoPlay) {
//	                    viewPager.setCurrentItem(0);
//	                }
//	                // 当前为第一张，此时从左向右滑，则切换到最后一张
//	                else if (viewPager.getCurrentItem() == 0 && !mIsAutoPlay) {
//	                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
//	                }
//				} else{
//					if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !mIsAutoPlay) {
//	                    if(mOnSlideShowViewFinish!=null)
//	                    	mOnSlideShowViewFinish.onSlideShowViewFinished(mContext);
//	                }
//					
//				}
//				   
//				break;
//			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int pos) {
			currentItem = pos;
		
			if(mOnSlideShowViewChange != null){
			   mOnSlideShowViewChange.onSlideShowViewChange(pos);
			}
			
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					dotViewsList.get(pos).setBackgroundResource(R.mipmap.slide_yes);
				} else {
					dotViewsList.get(i).setBackgroundResource(R.mipmap.slide_no);
				}
			}
		}

	}

	private class SlideShowTask implements Runnable {
		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (++currentItem) % dotViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	private void destoryBitmaps() {

		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				drawable.setCallback(null);
			}
		}
	}
	
	public OnSlideShowViewChange mOnSlideShowViewChange;

	public void setOnSlideShowViewChange(OnSlideShowViewChange l){
		mOnSlideShowViewChange = l;
	}
	
	public interface OnSlideShowViewChange{
		
		void onSlideShowViewChange(int pageIndex);
		
	}
	
	public OnSlideShowViewClickListener mOnSlideShowViewClickListener;

	public void setOnSlideShowViewClickListener(OnSlideShowViewClickListener l){
		mOnSlideShowViewClickListener = l;
	}
	
	public interface OnSlideShowViewClickListener{
		
		void jump(int index);
		
	}
	
}