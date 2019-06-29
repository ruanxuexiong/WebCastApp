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

public class RecordAdapter extends BaseAdapter implements View.OnClickListener {


    private Context mContext;
    private WantBean mItem;
    private ArrayList<WantBean> mDataList;
    private RecordListener mListener;
    private ArrayList<Message> mMessage;

    public RecordAdapter(Context context, ArrayList<WantBean> mDataList, RecordListener listener, ArrayList<Message> mMessage) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = listener;
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

        ViewHolder viewHolder;
        mItem = mDataList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_see, null);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_time);
        viewHolder.mCallTv = view.findViewById(R.id.tv_call);
        viewHolder.mItemll = view.findViewById(R.id.ll_item);
        viewHolder.mStateTv = view.findViewById(R.id.tv_state);
        viewHolder.mWantNumTv = view.findViewById(R.id.tv_want_num);

        if (mMessage.size() > 0) {
            for (int i = 0; i < mMessage.size(); i++) {
                if (mItem.getTalkId().equals(mMessage.get(i).getTargetId())) {
                    if (mMessage.get(i).getReceivedStatus().isRead()) {
                        viewHolder.mWantNumTv.setVisibility(View.GONE);
                    } else {
                        viewHolder.mWantNumTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (mItem.getZx_user().getStatus().equals("1")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
        }

        switch (mItem.getStatus()) {
            case "0":
                viewHolder.mCallTv.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已完成");
                break;
            case "1":
                break;
            case "3":
                viewHolder.mCallTv.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("等待同意");
                break;
            case "4"://已同意
                viewHolder.mCallTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setVisibility(View.GONE);
                break;
            case "5":
                viewHolder.mCallTv.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已拒绝");
                break;
            case "6":
                viewHolder.mCallTv.setVisibility(View.GONE);
                viewHolder.mStateTv.setVisibility(View.VISIBLE);
                viewHolder.mStateTv.setText("已失效");
                break;
            default:
                break;
        }


        viewHolder.mCallTv.setTag(position);
        viewHolder.mCallTv.setOnClickListener(this);

        viewHolder.mItemll.setTag(position);
        viewHolder.mItemll.setOnClickListener(this);

        viewHolder.mPictureIv.setTag(position);
        viewHolder.mPictureIv.setOnClickListener(this);

        viewHolder.mNameTv.setText(mItem.getZx_user().getUsername());
        viewHolder.mTimeTv.setText(mItem.getZx_time());
        ImgLoaderManager.getInstance().showImageView(mItem.getZx_user().getAvatar(), viewHolder.mPictureIv);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_call:
                if (mListener != null) {
                    mListener.onVideoClick(v);
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
        private LinearLayout mStatell;
        private TextView mStateTv;
        private TextView mCallTv;
        private SelectorTextView mWantNumTv;
    }

    public interface RecordListener {
        void onVideoClick(View view);

        void onOpenItemClick(View view);

        void onHeadClick(View view);
    }
}