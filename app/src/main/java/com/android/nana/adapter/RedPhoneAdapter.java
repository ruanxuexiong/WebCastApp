package com.android.nana.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.PhoneEntity;
import com.android.nana.util.ImgLoaderManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/1/8.
 */

public class RedPhoneAdapter extends BaseAdapter implements SectionIndexer {

    private Context mContext;
    private PhoneListener mListener;
    private String mThisTel;
    private ArrayList<PhoneEntity> mDataList;

    public RedPhoneAdapter(Context mContext, ArrayList<PhoneEntity> mDataList, PhoneListener mListener, String tel) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.mDataList = mDataList;
        this.mThisTel = tel;
    }

    @Override
    public int getCount() {
        if (null == mDataList) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        PhoneEntity entity = mDataList.get(position);
        view = LayoutInflater.from(mContext).inflate(R.layout.item_phone, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mCatalogTv = view.findViewById(R.id.catalog);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);
        viewHolder.mPictureIv = view.findViewById(R.id.iv_picture);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_name);
        viewHolder.mIdTv = view.findViewById(R.id.tv_id);
        viewHolder.mAddTv = view.findViewById(R.id.tv_add);
        viewHolder.mAddIv = view.findViewById(R.id.iv_add);
        viewHolder.mSendIv = view.findViewById(R.id.iv_send);

        viewHolder.mShowRl = view.findViewById(R.id.rl_show);
        viewHolder.mNoLl = view.findViewById(R.id.ll_no);
        viewHolder.mNoNameTv = view.findViewById(R.id.tv_no_name);

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.mCatalogTv.setVisibility(View.VISIBLE);
            viewHolder.mCatalogTv.setText(entity.getSortLetters());
        } else {
            viewHolder.mCatalogTv.setVisibility(View.GONE);
        }


        if (entity.getStatus().equals("1")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIdentyIv.setVisibility(View.GONE);
        }
        viewHolder.mNameTv.setText(entity.getUsername());

        if (entity.getIsMember().equals("1")) {//是否是注册用户
            viewHolder.mNoLl.setVisibility(View.GONE);
            viewHolder.mShowRl.setVisibility(View.VISIBLE);
            viewHolder.mAddIv.setBackground(mContext.getResources().getDrawable(R.drawable.icon_phone_send));
            if (entity.getIsFriend().equals("1")) {//是好友
//                viewHolder.mAddTv.setVisibility(View.VISIBLE);
                viewHolder.mAddIv.setVisibility(View.VISIBLE);
                viewHolder.mSendIv.setVisibility(View.GONE);
            } else {//非好友
//                viewHolder.mAddTv.setVisibility(View.GONE);
                viewHolder.mAddIv.setVisibility(View.VISIBLE);
                viewHolder.mSendIv.setVisibility(View.GONE);

                if (null != mThisTel && entity.getMobile().equals(mThisTel)) {
                    viewHolder.mAddTv.setVisibility(View.GONE);
                    viewHolder.mAddIv.setVisibility(View.GONE);
                    viewHolder.mSendIv.setVisibility(View.GONE);
                    viewHolder.mNoLl.setVisibility(View.VISIBLE);
                    viewHolder.mShowRl.setVisibility(View.GONE);
                    viewHolder.mIdTv.setText(entity.getMobile());
                    viewHolder.mNoNameTv.setText("我");
                }
            }
            viewHolder.mIdTv.setText("ID:" + entity.getIdcard());
            ImgLoaderManager.getInstance().showImageView(entity.getAvatar(), viewHolder.mPictureIv);
        } else {//非注册用户显示
            viewHolder.mNoLl.setVisibility(View.VISIBLE);
            viewHolder.mShowRl.setVisibility(View.GONE);
            viewHolder.mAddTv.setVisibility(View.GONE);
            viewHolder.mAddIv.setVisibility(View.GONE);
            viewHolder.mSendIv.setVisibility(View.VISIBLE);
            viewHolder.mSendIv.setBackground(mContext.getResources().getDrawable(R.drawable.icon_phone_xiao));
            if (TextUtils.isEmpty(entity.getUsername()))
                viewHolder.mNoNameTv.setText("#");
            else
                viewHolder.mNoNameTv.setText(entity.getUsername().substring(0, 1));
            viewHolder.mIdTv.setText(entity.getMobile());


            if (null != mThisTel && entity.getMobile().equals(mThisTel)) {
                viewHolder.mAddTv.setVisibility(View.GONE);
                viewHolder.mAddIv.setVisibility(View.GONE);
                viewHolder.mSendIv.setVisibility(View.GONE);
                viewHolder.mNoLl.setVisibility(View.VISIBLE);
                viewHolder.mShowRl.setVisibility(View.GONE);
                viewHolder.mIdTv.setText(entity.getMobile());
                viewHolder.mNoNameTv.setText("我");
            }
        }

        viewHolder.mContentLl.setTag(position);
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view, position);
                }
            }
        });

        viewHolder.mAddIv.setTag(position);
        viewHolder.mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAddFriendClick(view);
                }
            }
        });

        viewHolder.mSendIv.setTag(position);
        viewHolder.mSendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSendMsgClick(view);
                }
            }
        });

        return view;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mDataList.get(position).getSortLetters().charAt(0);
    }

    class ViewHolder {
        private TextView mCatalogTv;
        private LinearLayout mContentLl;
        private RoundedImageView mPictureIv;
        private ImageView mIdentyIv;
        private TextView mNameTv;
        private TextView mIdTv, mAddTv;
        private ImageView mAddIv;
        private ImageView mSendIv;

        private RelativeLayout mShowRl;
        private LinearLayout mNoLl;
        private TextView mNoNameTv;
    }

    public interface PhoneListener {
        void onItemClick(View view, int position);

        void onAddFriendClick(View view);

        void onSendMsgClick(View view);
    }
}
