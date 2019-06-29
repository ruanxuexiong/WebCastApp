package com.android.nana.webcast;

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
import com.android.nana.bean.WebCastEntity;
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/18.
 */

public class WebCasetAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private WebCastEntity mItem;
    private ArrayList<WebCastEntity> mDataList;


    private WebListener mListener;

    public WebCasetAdapter(Context context, ArrayList<WebCastEntity> mDataList, WebListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public WebCastEntity getItem(int position) {
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
        TextView mTxtName, mTxtInfo, mTxtDesc, mCallTv, mTxtAppointment, mTxtAttention, mTxtMoney;
    }

    public interface WebListener {
        void onMakeClick(View view);

        void onContentClick(View view);

        void onCallClick(View view);
    }

}
