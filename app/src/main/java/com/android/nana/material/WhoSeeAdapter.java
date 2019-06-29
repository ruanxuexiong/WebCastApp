package com.android.nana.material;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;

import net.soulwolf.widget.speedyselector.widget.SelectorTextView;

import java.util.ArrayList;

import io.rong.imlib.model.Message;

/**
 * Created by THINK on 2017/7/3.
 */

public class WhoSeeAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private WantBean mItem;
    private ArrayList<WantBean> mDataList;
    private WhoSeeListener mListener;
    private ArrayList<Message> mMessage;

    public WhoSeeAdapter(Context context, ArrayList<WantBean> mDataList, WhoSeeListener mListener, ArrayList<Message> mMessage) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
        this.mMessage = mMessage;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public WantBean getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder viewHolder;
        mItem = mDataList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_who, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_time);
        viewHolder.mItemll = view.findViewById(R.id.ll_item);
        viewHolder.mRefusell = view.findViewById(R.id.ll_refuse);
        viewHolder.mRgreell = view.findViewById(R.id.ll_agree);
        viewHolder.mNumTv = view.findViewById(R.id.tv_want_num);
        viewHolder.mBtnLl = view.findViewById(R.id.ll_btn);

        viewHolder.mCallTv = view.findViewById(R.id.tv_call);
        viewHolder.mStateTv = view.findViewById(R.id.tv_state);

        if (mMessage.size() > 0) {
            for (int i = 0; i < mMessage.size(); i++) {
                if (mItem.getTalkId().equals(mMessage.get(i).getTargetId())) {
                    if (mMessage.get(i).getReceivedStatus().isRead()) {
                        viewHolder.mNumTv.setVisibility(View.GONE);
                    } else {
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (mItem.getZx_user().getStatus().equals("1")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
        }

        switch (mItem.getStatus()) {
            case "0"://完成
                viewHolder.mCallTv.setVisibility(View.GONE);//视频
                viewHolder.mBtnLl.setVisibility(View.GONE);//同意拒绝
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已完成");
                break;
            case "1"://1=通过或拒绝
                viewHolder.mCallTv.setVisibility(View.GONE);//视频
                viewHolder.mBtnLl.setVisibility(View.VISIBLE);//同意拒绝按钮
                break;
            case "3"://等待同意咨询

                break;
            case "4"://已同意
                viewHolder.mBtnLl.setVisibility(View.GONE);//同意拒绝按钮
                viewHolder.mCallTv.setVisibility(View.VISIBLE);//视频
                break;
            case "5"://已拒绝
                viewHolder.mCallTv.setVisibility(View.GONE);//视频
                viewHolder.mBtnLl.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已拒绝");
                break;
            case "6"://已失效
                viewHolder.mCallTv.setVisibility(View.GONE);//视频
                viewHolder.mBtnLl.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已失效");
                break;
            default:
                break;
        }

        viewHolder.mNameTv.setText(mItem.getZx_user().getUsername());
        viewHolder.mTimeTv.setText(mItem.getZx_time());
        ImgLoaderManager.getInstance().showImageView(mItem.getZx_user().getAvatar(), viewHolder.mPictureIv);


        //同意拒绝
        viewHolder.mRefusell.setTag(position);
        viewHolder.mRefusell.setOnClickListener(this);
        viewHolder.mRgreell.setTag(position);
        viewHolder.mRgreell.setOnClickListener(this);

        //视频
        viewHolder.mCallTv.setTag(position);
        viewHolder.mCallTv.setOnClickListener(this);

        //消息
        viewHolder.mItemll.setTag(position);
        viewHolder.mItemll.setOnClickListener(this);

        //头像
        viewHolder.mPictureIv.setTag(position);
        viewHolder.mPictureIv.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_refuse://拒绝
                if (mListener != null) {
                    mListener.onRefuseClick(v);
                }
                break;
            case R.id.ll_agree://同意
                if (mListener != null) {
                    mListener.onAgreeClick(v);
                }
                break;
            case R.id.tv_money:
                if (mListener != null) {
                    mListener.onAgreeClick(v);
                }
                break;
            case R.id.ll_item:
                if (mListener != null) {
                    mListener.onOpenItemClick(v);
                }
                break;
            case R.id.iv_picture:
                if (mListener != null) {
                    mListener.onHeadClick(v);
                }
                break;
            case R.id.tv_call://视频按钮
                if (mListener != null) {
                    mListener.onCallClick(v);
                }
                break;
            default:
                break;
        }
    }


    private class ViewHolder {
        private RoundImageView mPictureIv;
        private ImageView mIdentyIv;
        private TextView mNameTv;
        private TextView mTimeTv;
        private LinearLayout mHidell;
        private TextView mMessageTv;
        private TextView mMoneyTv;
        private View mView;
        private LinearLayout mItemll;
        private LinearLayout mRefusell;
        private LinearLayout mRgreell;
        private SelectorTextView mNumTv;
        private TextView mCallTv;
        private LinearLayout mBtnLl;
        private TextView mStateTv;
    }

    public interface WhoSeeListener {
        void onVideoClick(View view);

        void onRefuseClick(View view);

        void onAgreeClick(View view);

        void onOpenItemClick(View view);

        void onHeadClick(View view);

        void onCallClick(View view);
    }
}
