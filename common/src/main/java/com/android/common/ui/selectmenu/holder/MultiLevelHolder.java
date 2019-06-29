package com.android.common.ui.selectmenu.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.ui.selectmenu.SelectMenuView;
import com.android.common.ui.selectmenu.bean.Region;
import com.android.common.ui.selectmenu.bean.RegionInfo;

import java.util.List;

/**
 * 多级选择
 */
public class MultiLevelHolder extends BaseWidgetHolder<List<RegionInfo>> {

	private SelectMenuView mSelectMenuView;
	
    private List<RegionInfo> mDataList;

    private ListView mLeftListView;
    private ListView mRightListView;

    private LeftAdapter mLeftAdapter;
    private RightAdapter mRightAdapter;

    private int mLeftSelectedIndex = 0;
    private int mRightSelectedIndex = 0;
    private int mLeftSelectedIndexRecord = mLeftSelectedIndex;
    private int mRightSelectedIndexRecord = mRightSelectedIndex;

    /** 记录左侧条目背景位置 */
    private View mLeftRecordView = null;
    /** 记录右侧条目对勾位置 */
   // private ImageView mRightRecordImageView = null;

    //用于首次测量时赋值标志
    private boolean mIsFirstMeasureLeft = true;
    private boolean mIsFirstMeasureRight = true;

    private OnRightListViewItemSelectedListener mOnRightListViewItemSelectedListener;

    public MultiLevelHolder(Context context, SelectMenuView menuView) {
        super(context);
        mSelectMenuView = menuView;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.multil_level_holder, null);
        mLeftListView = (ListView) view.findViewById(R.id.multil_level_lv_left);
        mRightListView = (ListView) view.findViewById(R.id.multil_level_lv_right);

        mLeftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mLeftSelectedIndex = position;
                
                if(mLeftRecordView != null) mLeftRecordView.setBackgroundResource(R.color.white);
                
                view.setBackgroundResource(R.color.grey_f5);
                mLeftRecordView = view;

                mRightAdapter.setDataList(mDataList.get(position).getRegionList(), mRightSelectedIndex);
                mRightAdapter.notifyDataSetChanged();
            }
        });

        mRightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRightSelectedIndex = position;
                mLeftSelectedIndexRecord = mLeftSelectedIndex;
              //  ImageView imageView = (ImageView) view.findViewById(R.id.single_level_item_right);

            //    if(mRightRecordImageView != null) mRightRecordImageView.setVisibility(View.INVISIBLE);

              //  imageView.setVisibility(View.VISIBLE);

              //  mRightRecordImageView = imageView;

                if(mOnRightListViewItemSelectedListener != null) {
                	
                	RegionInfo regionInfo = mDataList.get(mLeftSelectedIndex);
                	Region region = regionInfo.getRegionList().get(mRightSelectedIndex);

                    mOnRightListViewItemSelectedListener.OnRightListViewItemSelected(regionInfo, region);
                    
                    mSelectMenuView.dismissPopupWindow();
                    mSelectMenuView.setOptionTitle(mIndex, region.getRegionName());
                    
                }
            }
        });

        return view;
    }

    @Override
    public void refreshView(List<RegionInfo> data) {

    }

    public void refreshData(List<RegionInfo> data, int leftSelectedIndex, int rightSelectedIndex){

        this.mDataList = data;

        mLeftSelectedIndex = leftSelectedIndex;
        mRightSelectedIndex = rightSelectedIndex;

        mLeftSelectedIndexRecord = mLeftSelectedIndex;
        mRightSelectedIndexRecord = mRightSelectedIndex;

        mLeftAdapter = new LeftAdapter(data, mLeftSelectedIndex);
        mRightAdapter = new RightAdapter(data.get(0).getRegionList(), mRightSelectedIndex);

        mLeftListView.setAdapter(mLeftAdapter);
        mRightListView.setAdapter(mRightAdapter);
    }

    private class LeftAdapter extends BaseAdapter{

        private List<RegionInfo> mLeftDataList;

        public LeftAdapter(List<RegionInfo> list, int leftIndex){
            this.mLeftDataList = list;
            mLeftSelectedIndex = leftIndex;
        }

        @Override
        public int getCount() {
            return mLeftDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeftDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LeftViewHolder holder;
            if(convertView == null){
                holder = new LeftViewHolder();
                convertView = View.inflate(mContext, R.layout.multil_level_item, null);
                holder.leftText = (TextView) convertView.findViewById(R.id.multil_level_item_txt_text);
                holder.backgroundView = convertView.findViewById(R.id.multil_level_item_ll_main);
                convertView.setTag(holder);
            }else{
                holder = (LeftViewHolder) convertView.getTag();
            }

            holder.leftText.setText(mLeftDataList.get(position).getGroupName());
            if(mLeftSelectedIndex == position){
                holder.backgroundView.setBackgroundResource(R.color.grey_f5);  //选中项背景
                if(position == 0 && mIsFirstMeasureLeft){
                    mIsFirstMeasureLeft = false;
                    mLeftRecordView = convertView;
                }
            }else{
                holder.backgroundView.setBackgroundResource(R.color.white);  //其他项背景
            }

            return convertView;
        }
    }

    public void clearSelectedInfo(){

    }

    public class RightAdapter extends BaseAdapter{

        private List<Region> mRightDataList;

        public RightAdapter(List<Region> list, int rightSelectedIndex){
            this.mRightDataList = list;
            mRightSelectedIndex = rightSelectedIndex;
        }

        public void setDataList(List<Region> list, int rightSelectedIndex){
            this.mRightDataList = list;
            mRightSelectedIndex = rightSelectedIndex;
        }

        @Override
        public int getCount() {
            return mRightDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRightDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RightViewHolder holder;
            if(convertView == null){
                holder = new RightViewHolder();
                convertView = View.inflate(mContext, R.layout.single_level_item, null);
                holder.rightText = (TextView) convertView.findViewById(R.id.single_level_item_child);
              //  holder.selectedImage = (ImageView)convertView.findViewById(R.id.single_level_item_right);
                convertView.setTag(holder);
            }else{
                holder = (RightViewHolder) convertView.getTag();
            }

            holder.rightText.setText(mRightDataList.get(position).getRegionName());
            if(mRightSelectedIndex == position && mLeftSelectedIndex == mLeftSelectedIndexRecord){
                holder.selectedImage.setVisibility(View.VISIBLE);
              //  mRightRecordImageView = holder.selectedImage;
            }else{
                holder.selectedImage.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private static class LeftViewHolder {
        TextView leftText;
        View backgroundView;
    }

    private static class RightViewHolder{
        TextView rightText;
        ImageView selectedImage;
    }

    public void setOnRightListViewItemSelectedListener(OnRightListViewItemSelectedListener onRightListViewItemSelectedListener){
        this.mOnRightListViewItemSelectedListener = onRightListViewItemSelectedListener;
    }

    public interface OnRightListViewItemSelectedListener{
    	
        void OnRightListViewItemSelected(RegionInfo regionInfo, Region region);
        
    }
    
}
