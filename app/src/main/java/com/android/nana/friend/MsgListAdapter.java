package com.android.nana.friend;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.MsgListEntity;
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/26.
 */

public class MsgListAdapter extends BaseAdapter {

    private Context mContext;
    private MsgAdapterListener mListener;
    private ArrayList<MsgListEntity.Msg> mDataList;

    public MsgListAdapter(Context context, ArrayList<MsgListEntity.Msg> mDataList, MsgAdapterListener mListener) {
        this.mContext = context;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MsgListEntity.Msg msg = mDataList.get(i);
        ViewHolder viewHolder = new ViewHolder();
        view = LinearLayout.inflate(mContext, R.layout.item_msg_list, null);
        viewHolder.mAvatarIv = view.findViewById(R.id.iv_avatar);
        viewHolder.mIdentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mNameTv = view.findViewById(R.id.tv_user_name);
        viewHolder.mTextTv = view.findViewById(R.id.tv_text);
        viewHolder.mImgIv = view.findViewById(R.id.iv_img);
        viewHolder.mContentLl = view.findViewById(R.id.ll_content);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_moment_time);


        viewHolder.mNameTv.setText(msg.getUser().getUsername());
        if (msg.getUser().getStatus().equals("1")) {
            viewHolder.mIdentyIv.setVisibility(View.VISIBLE);
        }

        if (msg.getType().equals("laud")) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_friend_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mTextTv.setCompoundDrawables(drawable, null, null, null);
            viewHolder.mTextTv.setTextColor(mContext.getResources().getColor(R.color.green__66));
            viewHolder.mTextTv.setText("赞了这条动态");
        } else  if(msg.getType().equals("at")) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.at);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mTextTv.setCompoundDrawables(drawable, null, null, null);
            viewHolder.mTextTv.setTextColor(mContext.getResources().getColor(R.color.green__66));
            viewHolder.mTextTv.setText("提到了我");
        }else {
            viewHolder.mTextTv.setText(msg.getContent());
        }

        if (!"".equals(msg.getPicture())) {
            viewHolder.mImgIv.setVisibility(View.VISIBLE);
            ImgLoaderManager.getInstance().showImageView(msg.getPicture(), viewHolder.mImgIv);
        }

        viewHolder.mTimeTv.setText(msg.getDate());
        ImgLoaderManager.getInstance().showImageView(msg.getUser().getAvatar(), viewHolder.mAvatarIv);

        viewHolder.mContentLl.setTag(i);
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        private RoundImageView mAvatarIv;
        private ImageView mIdentyIv;
        private TextView mNameTv;
        private TextView mTextTv;
        private TextView mTimeTv;
        private ImageView mImgIv;
        private LinearLayout mContentLl;
    }

    public interface MsgAdapterListener {
        void onItemClick(View view);
    }
}
