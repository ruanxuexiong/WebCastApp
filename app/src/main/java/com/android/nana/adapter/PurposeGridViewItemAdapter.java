package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.PurposeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class PurposeGridViewItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<PurposeEntity> mList;

    public PurposeGridViewItemAdapter(Context context) {

        mContext = context;
        mList = new ArrayList<>();

    }

    public void setList(List<PurposeEntity> list){
        mList = list;
    }

    public void setChecked(PurposeEntity en){
        for (PurposeEntity purposeEntity : mList) {
            if (en.getId() == purposeEntity.getId()){
                purposeEntity.setChecked(true);
            } else {
                purposeEntity.setChecked(false);
            }
        }
    }

    public List<PurposeEntity> getList(){
        return mList;
    }

    public List<PurposeEntity> getSelectList(){
        List<PurposeEntity> selectList = new ArrayList<>();
        for (PurposeEntity purposeEntity : mList) {
            if (purposeEntity.isChecked()) {
                selectList.add(purposeEntity);
            }
        }
        return selectList;
    }

    public void set(int index, PurposeEntity en){

        mList.set(index, en);

    }

    public void clear(){

        for (PurposeEntity entity : mList) {
            entity.setChecked(false);
        }

    }

    @Override
    public int getCount() {
        return mList != null? mList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mList != null? mList.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PurposeEntity en = mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.specialty_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.specialty_gv_item_txt_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTxtName.setText(en.getName());

        if (en.isChecked()){
            viewHolder.mTxtName.setBackgroundResource(R.drawable.bule_bg_shape);
            viewHolder.mTxtName.setTextColor(mContext.getResources().getColor(R.color.main_color));
        } else {
            viewHolder.mTxtName.setBackgroundResource(R.drawable.grey_bg_shape);
            viewHolder.mTxtName.setTextColor(mContext.getResources().getColor(R.color.black_32));
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTxtName;
    }

}
