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
import com.android.nana.bean.VisitorEntity;
import com.android.nana.ui.RoundImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/20.
 */

public class VisitorAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private VisitorEntity mItem;
    private ArrayList<VisitorEntity> mDataList;


    private VisitorListener mListener;

    public VisitorAdapter(Context context, ArrayList<VisitorEntity> mDataList, VisitorListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public VisitorEntity getItem(int position) {
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
        viewHolder.mIvPicture = view.findViewById(R.id.home_list_item_iv_picture);
        viewHolder.mIvIdenty = view.findViewById(R.id.home_list_item_iv_identy);
        viewHolder.mTxtName = view.findViewById(R.id.home_list_item_txt_name);
        viewHolder.mTxtDesc = view.findViewById(R.id.home_list_item_txt_desc);

        viewHolder.mTxtAppointment = view.findViewById(R.id.home_list_item_txt_appointment);
        viewHolder.mLLContent = view.findViewById(R.id.content);
        view.setTag(viewHolder);
        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mIvPicture);

        if (null != mItem.getStatus()) {
            if (mItem.getStatus().equals("1")) {
                viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.icon_authen).into(viewHolder.mIvIdenty);
            }
            else if(mItem.getStatus().equals("4")) {
                viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.mipmap.user_vip).into(viewHolder.mIvIdenty);
            }
            else {
                viewHolder.mIvIdenty.setVisibility(View.GONE);
            }
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


        if (mItem.getIsFriend().equals("1")) {
            viewHolder.mTxtAppointment.setText("呼叫");
            viewHolder.mTxtAppointment.setTag(position);
            viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onCallClick(view);
                }
            });
        } else {
            viewHolder.mTxtAppointment.setTag(position);
            viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMakeClick(view);
                }
            });
        }

        viewHolder.mLLContent.setTag(position);
        viewHolder.mLLContent.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.home_list_item_txt_appointment:
                if (mListener != null) {
                    mListener.onMakeClick(v);
                }
                break;*/
            case R.id.content:
                if (mListener != null) {
                    mListener.onContentClick(v);
                }
                break;
        }
    }

    class ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        TextView mTxtName, mTxtInfo, mTxtDesc, mTxtAppointment, mTxtMoney, mCallTv;
    }

    public interface VisitorListener {
        void onMakeClick(View view);

        void onContentClick(View view);

        void onCallClick(View view);
    }

}
