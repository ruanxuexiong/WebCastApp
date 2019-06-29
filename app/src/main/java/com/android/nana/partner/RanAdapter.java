package com.android.nana.partner;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/8.
 */

public class RanAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private RanBean mItem;
    private ArrayList<RanBean> mDataList;
    private OnItemClickListener itemClickListener;

    public RanAdapter(Context context, ArrayList<RanBean> mDataList, OnItemClickListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.itemClickListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public RanBean getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        mItem = mDataList.get(position);
        // if (convertView == null) {
        viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ran, null);
        viewHolder.mNoTv =  convertView.findViewById(R.id.tv_no);
        viewHolder.mNameTv =  convertView.findViewById(R.id.tv_name);
        viewHolder.mMoneyTv =  convertView.findViewById(R.id.tv_money);
        viewHolder.mAvatarIv =  convertView.findViewById(R.id.iv_avatar);
        viewHolder.mContentRL =  convertView.findViewById(R.id.ran_item);
        viewHolder.zisun_lin=convertView.findViewById(R.id.zisun_lin);
        viewHolder.mAvatarIv1=convertView.findViewById(R.id.iv_avatar1);
        viewHolder.mAvatarIv2=convertView.findViewById(R.id.iv_avatar2);
        viewHolder.mAvatarIv3=convertView.findViewById(R.id.iv_avatar3);
        viewHolder.zisun_money =  convertView.findViewById(R.id.zisun_money);
        viewHolder.xiao_num=convertView.findViewById(R.id.xiao_num);
        viewHolder.xiaore=convertView.findViewById(R.id.xiaore);
        convertView.setTag(viewHolder);
      /*  } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }*/

        viewHolder.mContentRL.setTag(position);
        viewHolder.mContentRL.setOnClickListener(this);

        if (null!=mItem.getTow_level_list()&&mItem.getTow_level_list().size()!=0){
            //显示徒孙
            viewHolder.zisun_lin.setVisibility(View.VISIBLE);
            if (mItem.getTow_level_list().size()==1){
                ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(0).getAvatar(), viewHolder.mAvatarIv1);
                viewHolder.zisun_money.setText(mItem.getTow_level_money());
            }
            else if(mItem.getTow_level_list().size()==2){
                ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(0).getAvatar(), viewHolder.mAvatarIv1);

                viewHolder.zisun_money.setText(mItem.getTow_level_money());
            }
            else if(mItem.getTow_level_list().size()==3){

                if (mItem.getTow_level_total()>3){
                    viewHolder.xiaore.setVisibility(View.VISIBLE);
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(0).getAvatar(), viewHolder.mAvatarIv1);
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(1).getAvatar(), viewHolder.mAvatarIv2);
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(2).getAvatar(), viewHolder.mAvatarIv3);
                    viewHolder.zisun_money.setText(mItem.getTow_level_money());
                    viewHolder.xiao_num.setText(mItem.getTow_level_total()+"");
                }
                else {
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(0).getAvatar(), viewHolder.mAvatarIv1);
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(1).getAvatar(), viewHolder.mAvatarIv2);
                    ImgLoaderManager.getInstance().showImageView(mItem.getTow_level_list().get(2).getAvatar(), viewHolder.mAvatarIv3);
                    viewHolder.zisun_money.setText(mItem.getTow_level_money());
                }

            }

        }
        else {
            viewHolder.zisun_lin.setVisibility(View.GONE);
        }

        viewHolder.mNoTv.setText(position+1+"");
        if ("null" != mItem.getUsername() && !"null".equals(mItem.getUsername())) {
            viewHolder.mNameTv.setText(mItem.getUsername());

            if (null != mItem.getBalance()) {
                String money = "<font color='#999999'>贡献收益 </font>" + "<font color='#FF0000'>" + mItem.getBalance() + "</font>" + "<font color='#999999'>元</font>";
                viewHolder.mMoneyTv.setText(Html.fromHtml(money));
            } else if (0 != mItem.getTow_level_total()) {
                String num = "<font color='#999999'> 共推荐 </font>" + "<font color='#FF0000'>" + mItem.getBalance() + "</font>" + "<font color='#999999'>人</font>";
                viewHolder.mMoneyTv.setText(Html.fromHtml(num));
            } else if (null != mItem.getBalance()) {
                String balance = "<font color='#999999'> 贡献收益 </font>" + "<font color='#FF0000'>" + mItem.getBalance() + "</font>" + "<font color='#999999'> 元  </font>";
                viewHolder.mMoneyTv.setText(Html.fromHtml(balance));
            }
            ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mAvatarIv);
        } else {
            viewHolder.mContentRL.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ran_item:
                if (null != itemClickListener) {
                    itemClickListener.onItemClick(view);
                }
                break;
        }
    }

    private class ViewHolder {
        private TextView mNoTv, mNameTv, mMoneyTv,zisun_money,xiao_num;
        private RoundImageView mAvatarIv,mAvatarIv1,mAvatarIv2,mAvatarIv3;
        private RelativeLayout mContentRL,xiaore;
        private LinearLayout zisun_lin;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

}
