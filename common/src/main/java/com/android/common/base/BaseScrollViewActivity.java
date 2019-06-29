package com.android.common.base;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.utils.IOAuthCallBack;

public abstract class BaseScrollViewActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

	protected abstract void initData();
	/**
	 * 为空，不保存数据
	 * @return
	 */
	protected abstract String dbModelName();
	public abstract void populateData(String success);
	/**
	 * 滑动内容
	 * @return
	 */
	protected abstract View addViewToLayout();
	
	protected PullToRefreshLayout mPtrl;
	private LinearLayout mLlAllLayout;
	private TextView mTxtText;
	private TextView mTxtLoad;
	private RelativeLayout mVgLoad;
	
	private boolean mIsFirstIn = true;

	@Override
	protected void locationData() {
		super.locationData();

		mPtrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		mPtrl.setOnRefreshListener(this);
		// 隐藏底部上拉加载更多
		mPtrl.getChildAt(2).setVisibility(View.GONE);
		// 滑动内容
		mLlAllLayout = (LinearLayout) findViewById(R.id.refresh_layout);
		mLlAllLayout.addView(addViewToLayout());
		
		mVgLoad = (RelativeLayout) findViewById(R.id.default_load_vg_load);
		mTxtText = (TextView) findViewById(R.id.default_load_txt_text);
		mTxtLoad = (TextView) findViewById(R.id.default_load_txt_load);

		//  重新加载
		mTxtLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				UIHelper.showOnLoadingDialog(BaseScrollViewActivity.this, "正在努力加载中...");
				mVgLoad.setVisibility(View.GONE);
				new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
					@Override
					public void run() {
						
						initData();
						
					}
				}, 500);
				
			}
		});
	}

	@Override
	protected void init() {

		if (mIsFirstIn) { // 第一次进入自动刷新
			mPtrl.autoRefresh();
			mIsFirstIn = false;
			mLlAllLayout.setVisibility(View.GONE);
		}
		
	}
	
	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		mPullToRefreshLayout = pullToRefreshLayout;
		new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
			@Override
			public void run() {
				
				initData();
				
			}
		}, 500);
	}
	
	private PullToRefreshLayout mPullToRefreshLayoutMore;

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		mPullToRefreshLayoutMore = pullToRefreshLayout;
		mPullToRefreshLayoutMore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
	
	protected IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

		@Override
		public void onStartRequest() {

			if (!mIsFirstIn) {
				mIsFirstIn = true;
				DBModel dbModel = DBModel.get(dbModelName());
				if(dbModel != null){
					ResultRequestModel mResultDetailModel = new ResultRequestModel(dbModel.Description);
					if (mResultDetailModel.mIsSuccess) {
						populateData(dbModel.Description);
						mLlAllLayout.setVisibility(View.VISIBLE);
						mVgLoad.setVisibility(View.GONE);
					} else {
						mVgLoad.setVisibility(View.VISIBLE);
					}
				} else {
					mVgLoad.setVisibility(View.GONE);
				}
			}

		}

		@Override
		public void getSuccess(String successJson) {

			BaseModel mResultDetailModel = new ResultRequestModel(successJson);
			if (mResultDetailModel.mIsSuccess) {
				DBModel dbModel = DBModel.get(dbModelName());
				if (dbModel != null) {
					if (!dbModel.Description.equals(successJson)) {
						DBModel.saveOrUpdate(dbModelName(),successJson);
						populateData(successJson);
					}
				} else {
					DBModel.saveOrUpdate(dbModelName(),successJson);
					populateData(successJson);
				}
				mLlAllLayout.setVisibility(View.VISIBLE);
				mVgLoad.setVisibility(View.GONE);
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			} else {
				mVgLoad.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
			UIHelper.hideOnLoadingDialog();

		}

		@Override
		public void getFailue(String failueJson) {

			DBModel dbModel = DBModel.get(dbModelName());
			if(dbModel!=null){
				ResultRequestModel mResultDetailModel = new ResultRequestModel(dbModel.Description);
				if (mResultDetailModel.mIsSuccess) {
					mLlAllLayout.setVisibility(View.VISIBLE);
					mVgLoad.setVisibility(View.GONE);
				} else {
					mVgLoad.setVisibility(View.VISIBLE);
				}
			} else {
				mVgLoad.setVisibility(View.VISIBLE);
			}
			mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			UIHelper.hideOnLoadingDialog();
			
		}
	};

}
