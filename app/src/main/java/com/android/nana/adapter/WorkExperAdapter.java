package com.android.nana.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseJsonAdapter;
import com.android.common.utils.ImgLoaderManager;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.customer.AddWorkExperActivity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class WorkExperAdapter extends BaseJsonAdapter<WorkExperAdapter.ViewHolder> {

    private boolean mIsHideEdit;

    public WorkExperAdapter(Activity context, boolean isHideEdit) {
        super(context);
        mIsHideEdit = isHideEdit;
    }

    @Override
    protected ViewHolder getViewById(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mIvPicture = (ImageView) view.findViewById(R.id.we_item_iv_picture);
        viewHolder.mTxtName = (TextView) view.findViewById(R.id.we_item_txt_name);
        viewHolder.mTxtInfo = (TextView) view.findViewById(R.id.we_item_txt_info);
        viewHolder.mTxtDesc = (TextView) view.findViewById(R.id.we_item_txt_desc);
        viewHolder.mTxtText = (TextView) view.findViewById(R.id.we_item_txt_text);
        viewHolder.mIvEdit = (ImageView) view.findViewById(R.id.we_item_iv_edit);
        if (mIsHideEdit) {
            viewHolder.mIvEdit.setVisibility(View.GONE);
        } else {
            viewHolder.mIvEdit.setVisibility(View.VISIBLE);
        }
        return viewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_item;
    }

    @Override
    protected void populateData(final JSONObject jsonObject, ViewHolder viewHolder) {

        new ImgLoaderManager(R.drawable.icon_work).showImageView(JSONUtil.get(jsonObject, "picture", ""), viewHolder.mIvPicture);
        viewHolder.mTxtName.setText(JSONUtil.get(jsonObject, "name", ""));
        viewHolder.mTxtInfo.setText(JSONUtil.get(jsonObject, "position", ""));
        if ("".equals(JSONUtil.get(jsonObject, "beginTime", ""))) {
           // viewHolder.mTxtDesc.setTextColor(mContext.getResources().getColor(R.color.gules));
            viewHolder.mTxtDesc.setText("");
        } else {
            viewHolder.mTxtDesc.setText(JSONUtil.get(jsonObject, "beginTime", "") + "-" + JSONUtil.get(jsonObject, "endTime", ""));
        }

       viewHolder.mTxtText.setText(JSONUtil.get(jsonObject, "introduce", ""));
       // Utils.toggleEllipsize(viewHolder.mTxtText,JSONUtil.get(jsonObject, "introduce", ""));

        viewHolder.mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AddWorkExperActivity.class);
                intent.putExtra("Json", jsonObject.toString());
                mContext.startActivityForResult(intent, 1);

            }
        });

    }

    class ViewHolder {
        ImageView mIvPicture, mIvEdit;
        TextView mTxtName, mTxtInfo, mTxtDesc, mTxtText;
    }

}
