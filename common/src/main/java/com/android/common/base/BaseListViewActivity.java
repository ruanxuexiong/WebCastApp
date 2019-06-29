package com.android.common.base;

import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.ResultRequestListModel;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.utils.IOAuthCallBack;

import org.json.JSONObject;

import java.util.List;

public abstract class BaseListViewActivity<TViewHolder, TBaseJsonAdapter extends BaseJsonAdapter<TViewHolder>> extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

	protected abstract TBaseJsonAdapter getBaseJsonAdapter();
	
	protected int mPageIndex, mPageSize;
	
	private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
	private boolean mIsFirtst;
	
	protected abstract void initList();
	protected TBaseJsonAdapter mBaseJsonAdapter;
	
	protected ListView mListView;
	protected PullToRefreshLayout mPtrl;
	private TextView mTxtText;
	public TextView mTxtLoad;
	private RelativeLayout mVgLoad;

	@Override
	public void locationData() {
		
		mPtrl = (PullToRefreshLayout) findViewById(R.id.refresh_view);
		mPtrl.setOnRefreshListener(this);
		mListView = (ListView) findViewById(R.id.content_view);
		// mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.red)));
		// mListView.setDividerHeight(10);
		mListView.setDivider(null);
		
		mVgLoad = (RelativeLayout) findViewById(R.id.default_load_vg_load);
		mTxtText = (TextView) findViewById(R.id.default_load_txt_text);
		mTxtLoad = (TextView) findViewById(R.id.default_load_txt_load);
		
		mBaseJsonAdapter = getBaseJsonAdapter();
		mListView.setAdapter(mBaseJsonAdapter);

		mPageIndex = mBaseApplication.getFirstPageIndex();
		mPageSize = 10;
		
		// 重新加载
		/*mTxtLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				startActivity(new Intent(getApplicationContext(), MyIncomeAddBankCartActivity.class),1);
				*//*UIHelper.showOnLoadingDialog(BaseListViewActivity.this, "正在努力加载中");
				mVgLoad.setVisibility(View.GONE);
				
				new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
					@Override
					public void run() {
						
						initList();
						
					}
				}, 500);*//*
				
			}
		});*/
	
	}
	
	@Override
	protected void init() {
		
		if (!mIsFirtst) {
			mIsFirtst = true;
			autoRefresh();
		}
		
	}
	
	protected void autoRefresh() {
		
		mPtrl.setVisibility(View.VISIBLE);
		mPtrl.autoRefresh(); // 自动刷新

	}
	
	protected void hideRefersh() {
		
		mIsFirtst = true;
		mPtrl.setVisibility(View.GONE);

	}

	/**
	 * 处理数据
	 * @param mList
	 * @return
	 */
	protected List<JSONObject> populateListData(List<JSONObject> mList) {
		return mList;
	}
	
	private PullToRefreshLayout mPullToRefreshFinish;
	
	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		
		mPullToRefreshType = false;
		mPullToRefreshFinish = pullToRefreshLayout;
		mPageIndex = mBaseApplication.getFirstPageIndex();
		
		new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
			@Override
			public void run() {
				
				initList();
				
			}
		}, 500);
		
	}

	private PullToRefreshLayout mPullToLoadmore;
	
	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		
		mPullToRefreshType = true;
		mPullToLoadmore = pullToRefreshLayout;
		
		new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
			@Override
			public void run() {
				
				initList();
				
			}
		}, 500);
		
	}
	
	protected IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

		@Override
		public void onStartRequest() {
			
		}

		@Override
		public void getSuccess(String successJson) {

			BaseModel mResult = new ResultRequestListModel(successJson);

			if(mResult.mIsSuccess) {
				
				List<JSONObject> mList = populateListData(mResult.ToList());
				if (mList != null && mList.size() > 0) {
					if (mPageIndex == mBaseApplication.getFirstPageIndex()){
						mBaseJsonAdapter.setList(mList);
					} else {
						mBaseJsonAdapter.appendList(mList);
					}
					
					mPageIndex++;
					mBaseJsonAdapter.notifyDataSetChanged();
					
					if (!mPullToRefreshType) {
						mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
					} else {
						mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
					}
					mVgLoad.setVisibility(View.GONE);
					mPtrl.setVisibility(View.VISIBLE);
				} else {
					if (mPageIndex == mBaseApplication.getFirstPageIndex()){
						mVgLoad.setVisibility(View.VISIBLE);
						mTxtText.setText(R.string.default_load_null_str);
						if (!mPullToRefreshType) {
							mPullToRefreshFinish.setVisibility(View.GONE);
						} else {
							mPullToLoadmore.setVisibility(View.GONE);
						}
					} else {
						if (!mPullToRefreshType) {
							mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.NULL);
						} else {
							mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.NULL);
						}
					}
				}
			} else {
				if (mPageIndex == mBaseApplication.getFirstPageIndex()){
					mVgLoad.setVisibility(View.VISIBLE);
					mTxtText.setText("暂无数据，请稍后重试!"); // default_load_fail_str
					if (!mPullToRefreshType) {
						mPullToRefreshFinish.setVisibility(View.GONE);
					} else {
						mPullToLoadmore.setVisibility(View.GONE);
					}
				} else {
					if (!mPullToRefreshType) {
						mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.FAIL);
					} else {
						mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.NULL);
					}
				}
			}
			
			UIHelper.hideOnLoadingDialog();
		}

		@Override
		public void getFailue(String failueJson) {
			
			if(mPageIndex == mBaseApplication.getFirstPageIndex()){
				mVgLoad.setVisibility(View.VISIBLE);
				mTxtText.setText(R.string.default_load_fail_str);
				if (!mPullToRefreshType) {
					mPullToRefreshFinish.setVisibility(View.GONE);
				} else {
					mPullToLoadmore.setVisibility(View.GONE);
				}
			}
			UIHelper.hideOnLoadingDialog();
			
		}

	};

}
