package com.android.nana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.HomeRecordEntity;
import com.android.nana.eventBus.MessageArrayEvent;
import com.android.nana.ui.RoundImageView;

import net.soulwolf.widget.speedyselector.widget.SelectorTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/8/31.
 */

public class RecordAdapter extends BaseAdapter implements View.OnClickListener {


    private Context mContext;
    private HomeRecordEntity mItem;
    private ArrayList<HomeRecordEntity> mDataList;
    private RecordListener mListener;
    private ArrayList<Message> mMessage;

    public RecordAdapter(Context context, ArrayList<HomeRecordEntity> mDataList, RecordListener mListener, ArrayList<Message> mMessage) {
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
    public HomeRecordEntity getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        mItem = mDataList.get(position);
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_home, null);
        viewHolder.mContent = view.findViewById(R.id.content_item_home);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mDentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mTitleTv = view.findViewById(R.id.tv_title);
        viewHolder.mDialIv = view.findViewById(R.id.iv_dial);
        viewHolder.mDescTv = view.findViewById(R.id.tv_desc);
        viewHolder.mCommentTv = view.findViewById(R.id.tv_comment);
        viewHolder.mMsgRl = view.findViewById(R.id.rl_msg);
        viewHolder.mNubTv = view.findViewById(R.id.tv_num);
        view.setTag(viewHolder);

        if (mMessage.size() > 0) {
            for (int i = 0; i < mMessage.size(); i++) {
                if (mItem.getTalkId().equals(mMessage.get(i).getTargetId())) {
                    if (mMessage.get(i).getReceivedStatus().isRead()) {
                        viewHolder.mMsgRl.setVisibility(View.GONE);
                    } else {
                        viewHolder.mMsgRl.setVisibility(View.VISIBLE);
                        viewHolder.mNubTv.setVisibility(View.VISIBLE);
                        EventBus.getDefault().post(new MessageArrayEvent(mMessage));
                    }
                }
            }
        }

        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mPictureIv);
        viewHolder.mTitleTv.setText(mItem.getUname());

        if (null != mItem.getShstatus()) {
            if (mItem.getShstatus().equals("1")) {
                viewHolder.mDentyIv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mDentyIv.setVisibility(View.GONE);
            }
        }


        if (null != mItem.getMeetStatus()) {
            if (mItem.getMeetStatus().equals("1")) {
                viewHolder.mDialIv.setImageResource(R.drawable.icon_exhale);
            } else if (mItem.getMeetStatus().equals("2")) {
                viewHolder.mDialIv.setImageResource(R.drawable.icon_incoming);
                if (mItem.getZx_comment().equals("-1")) {
                    viewHolder.mCommentTv.setVisibility(View.VISIBLE);
                    viewHolder.mCommentTv.setTag(position);
                    viewHolder.mCommentTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onCommentClick(view);
                            }
                        }
                    });
                } else {
                    viewHolder.mCommentTv.setVisibility(View.GONE);
                }
            }
        }

        viewHolder.mPictureIv.setTag(position);
        viewHolder.mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onPictureClick(view);
                }
            }
        });

        if (null != mItem.getStatus()) {
            switch (mItem.getStatus()) {
                case "1":
                    viewHolder.mDescTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
                    viewHolder.mDescTv.setText("发起时间：" + mItem.getDate());
                    break;
                case "5":
                    viewHolder.mDescTv.setTextColor(mContext.getResources().getColor(R.color.bg_light_red));
                    viewHolder.mDescTv.setText("已拒绝" + mItem.getDate());
                    viewHolder.mCommentTv.setVisibility(View.GONE);
                    break;
                case "6":
                    viewHolder.mDescTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
                    viewHolder.mDescTv.setText("已失效" + mItem.getDate());
                    viewHolder.mCommentTv.setVisibility(View.GONE);
                    break;
                case "0":
                    viewHolder.mDescTv.setTextColor(mContext.getResources().getColor(R.color.green_99));
                    viewHolder.mDescTv.setText("已完成" + mItem.getDate());
                    break;
            }
        }

        if ("5".equals(mItem.getStatus())) {//已拒绝
            viewHolder.mContent.setTag(position);
            viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener) {
                        mListener.onRefuse(view);
                    }
                }
            });
        } else if ("6".equals(mItem.getStatus())) {//已失效
            viewHolder.mContent.setTag(position);
            viewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener) {
                        mListener.onInvalid(view);
                    }
                }
            });
        } else {
            viewHolder.mContent.setTag(position);
            viewHolder.mContent.setOnClickListener(this);
        }
        return view;
    }


    private class ViewHolder {
        private RoundImageView mPictureIv;
        private ImageView mDentyIv;
        private TextView mTitleTv;
        private ImageView mDialIv;
        private LinearLayout mContent;
        private TextView mDescTv, mCommentTv;
        private RelativeLayout mMsgRl;
        private SelectorTextView mNubTv;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.content_item_home:
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
                break;
        }
    }

    public interface RecordListener {
        void onItemClick(View view);

        void onVideoClick(View view);

        void onCallClick(View view);

        void onCommentClick(View view);

        void onPictureClick(View view);

        void onRefuse(View view);//拒绝

        void onInvalid(View view);//失效
    }
}
