package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.ScreenEntity;
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/26.
 */

public class ScreenAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private ArrayList<ScreenEntity> mDataList;
    private ScreenListener mListener;
    private ScreenEntity mItem;

    public ScreenAdapter(Context context, ArrayList<ScreenEntity> mDataList, ScreenListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content:
                if (mListener != null) {
                    mListener.onContentClick(v);
                }
                break;
            case R.id.home_list_item_txt_appointment:
                if (mListener != null) {
                    mListener.onMakeClick(v);
                }
                break;
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public ScreenEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        mItem = mDataList.get(position);

        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, null);
        viewHolder.mIvPicture = (RoundImageView) view.findViewById(R.id.home_list_item_iv_picture);
        viewHolder.mIvIdenty = (ImageView) view.findViewById(R.id.home_list_item_iv_identy);
        viewHolder.mTxtName = (TextView) view.findViewById(R.id.home_list_item_txt_name);
        viewHolder.mTxtDesc = (TextView) view.findViewById(R.id.home_list_item_txt_desc);
        //  viewHolder.mCallTv = view.findViewById(R.id.tv_call);

        //viewHolder.mTxtInfo = (TextView) view.findViewById(R.id.home_list_item_txt_info);
        viewHolder.mTxtAppointment = (TextView) view.findViewById(R.id.home_list_item_txt_appointment);
     //   viewHolder.mTxtMoney = (TextView) view.findViewById(R.id.home_list_item_txt_money);
        viewHolder.mLLContent = (LinearLayout) view.findViewById(R.id.content);
        view.setTag(viewHolder);

        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mIvPicture);

        String state = mItem.getStatus(); // 0待审核  1审核通过  2审核未通过
        if (state.equals("1")) {
            viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvIdenty.setVisibility(View.GONE);
        }
        viewHolder.mTxtName.setText(mItem.getUsername());

        if (!"".equals(mItem.getCompany()) && !"".equals(mItem.getPosition())) {
            viewHolder.mTxtDesc.setText(mItem.getPosition() + " | " + mItem.getCompany());
        } else if (!"".equals(mItem.getCompany())) {
            viewHolder.mTxtDesc.setText(mItem.getCompany());
        } else if (!"".equals(mItem.getPosition())) {
            viewHolder.mTxtDesc.setText(mItem.getPosition());
        } else {
            viewHolder.mTxtDesc.setVisibility(View.GONE);
        }

        /*String str = "被成功邀请:<font color='#FF0000'>" + mItem.getInviteSuccessCount() + "</font>次";
        viewHolder.mTxtInfo.setText(Html.fromHtml(str));*/

         viewHolder.mTxtAppointment.setTag(position);
          viewHolder.mTxtAppointment.setOnClickListener(this);

       // viewHolder.mCallTv.setTag(position);
       // viewHolder.mCallTv.setOnClickListener(this);

        viewHolder.mLLContent.setTag(position);
        viewHolder.mLLContent.setOnClickListener(this);


        String money = mItem.getMoney();

      /*  if ("1".equals(mItem.getIsJob())) {
            viewHolder.mTxtMoney.setText(money + "元/" + "24小时");
        } else {
            viewHolder.mTxtMoney.setText(money + "元/" + mItem.getTime() + "分钟");
        }
*/
        return view;
    }

    class ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        TextView mTxtName, mTxtInfo, mCallTv, mTxtDesc, mTxtAppointment, mTxtAttention, mTxtMoney;
    }

    public interface ScreenListener {
        void onMakeClick(View view);

        void onContentClick(View view);
    }
}
