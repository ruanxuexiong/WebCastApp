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
import com.android.nana.customer.AddSpecialtyActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class SpecialtyAdapter extends BaseJsonAdapter<SpecialtyAdapter.ViewHolder> {

    private boolean mIsHideEdit;
    private SpecialtyGridViewItemAdapter mAdapter;

    public SpecialtyAdapter(Activity context, boolean isHideEdit) {
        super(context);
        mIsHideEdit = isHideEdit;
        mAdapter = new SpecialtyGridViewItemAdapter(context);
    }

    @Override
    protected ViewHolder getViewById(View view) {
        ViewHolder viewHolder = new ViewHolder();
       viewHolder.mIvPicture = (ImageView) view.findViewById(R.id.specialty_item_iv_picture1);
        viewHolder.mTxtName = (TextView) view.findViewById(R.id.specialty_item_txt_name);
        viewHolder.mIvEdit = (ImageView) view.findViewById(R.id.specialty_item_iv_edit);
        if (mIsHideEdit) {
            viewHolder.mIvEdit.setVisibility(View.GONE);
        } else {
            viewHolder.mIvEdit.setVisibility(View.VISIBLE);
        }
        return viewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.specialty_item;
    }

    @Override
    protected void populateData(final JSONObject jsonObject, ViewHolder viewHolder) {

        JSONObject parentColumn = JSONUtil.getJsonObject(jsonObject, "parentColumn");

        ImgLoaderManager.getInstance().showImageView(JSONUtil.get(parentColumn, "picture", ""), viewHolder.mIvPicture);

        StringBuffer content = new StringBuffer();
        content.append(JSONUtil.get(parentColumn, "name", "") + " - ");
        List<JSONObject> columns = JSONUtil.getList(jsonObject, "columns");
        if (null != columns) {
            for (int k = 0; k < columns.size(); k++) {
                String name = JSONUtil.get(columns.get(k), "name", "");
                content.append(name);
                if (k != columns.size() - 1) content.append("、");
            }
        }

        viewHolder.mTxtName.setText(content.toString());

        viewHolder.mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AddSpecialtyActivity.class);
                intent.putExtra("Json", jsonObject.toString());
                mContext.startActivityForResult(intent, 1);

            }
        });

    }

    class ViewHolder {
        ImageView mIvPicture, mIvEdit;
        TextView mTxtName;
    }

}
