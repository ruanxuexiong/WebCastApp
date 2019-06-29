package com.android.common.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



public abstract class BaseListAdapter<TModel,TViewHolder> extends BaseAdapter{

	protected Activity mContext;
	protected LayoutInflater mInflater;
	public List<TModel> mList;

	public BaseListAdapter(Activity context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mList = new ArrayList<TModel>();
	}
	
	public void setList(List<TModel> list){
		mList = list;
	}
	
	public void set(int index, TModel t){
		mList.set(index, t);
	}
	
	public List<TModel> getList(){
		return mList;
	}
	
	public void appendList(List<TModel> list){
		mList.addAll(list);
	}
	
	public void insertJsonObject(TModel object,int index){
		mList.add(index, object);
	}
	
	public void clear(){
		mList.clear();
	}
	
	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TViewHolder holder  =null;
		if (convertView == null) {		
			convertView = mInflater.inflate(getLayoutId(), null); 
			
			holder = getViewById(convertView);
			convertView.setTag(holder);
		} else {
			holder = (TViewHolder) convertView.getTag();
		}
		
		TModel jsonObject = mList.get(position);
		
		if(jsonObject!=null){
			Log.d("格式化结果数据：","List:"+jsonObject.toString());
			populateData(jsonObject, holder);
		}
	    
		return convertView;
	}

	protected abstract TViewHolder getViewById(View view);
	
	protected abstract int getLayoutId();
	
	protected abstract void populateData(TModel jsonObject,TViewHolder holder);
	
}
