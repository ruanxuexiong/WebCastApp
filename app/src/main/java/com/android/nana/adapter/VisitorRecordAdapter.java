package com.android.nana.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseJsonAdapter;
import com.android.common.utils.ImgLoaderManager;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class VisitorRecordAdapter extends BaseJsonAdapter<VisitorRecordAdapter.ViewHolder> {

    private AppointmentModel mAppointmentModel;

    private UserInfo mUserInfo;

    public VisitorRecordAdapter(Activity context) {
        super(context);
        mAppointmentModel = new AppointmentModel(context);
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(context, "userInfo", UserInfo.class);
    }


    @Override
    protected ViewHolder getViewById(View mItem) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mIvPicture = (RoundImageView) mItem.findViewById(R.id.home_list_item_iv_picture);
        viewHolder.mIvIdenty = (ImageView) mItem.findViewById(R.id.home_list_item_iv_identy);
        viewHolder.mTxtName = (TextView) mItem.findViewById(R.id.home_list_item_txt_name);
        viewHolder.mTxtDesc = (TextView) mItem.findViewById(R.id.home_list_item_txt_desc);
        //viewHolder.mCallTv = mItem.findViewById(R.id.tv_call);
        // viewHolder.mTxtInfo = (TextView) mItem.findViewById(R.id.home_list_item_txt_info);
        viewHolder.mTxtAppointment = (TextView) mItem.findViewById(R.id.home_list_item_txt_appointment);
       // viewHolder.mTxtMoney = (TextView) mItem.findViewById(R.id.home_list_item_txt_money);
        viewHolder.mLLContent = (LinearLayout) mItem.findViewById(R.id.content);
        return viewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_list_item;
    }

    @Override
    protected void populateData(final JSONObject jsonObject, final ViewHolder viewHolder) {

        JSONObject userJsonObject = JSONUtil.getJsonObject(jsonObject, "user");
        if (userJsonObject == null) userJsonObject = jsonObject;

        final String userId = JSONUtil.get(userJsonObject, "id", "");

        ImgLoaderManager.getInstance().showImageView(JSONUtil.get(userJsonObject, "avatar", ""), viewHolder.mIvPicture);

        String state = JSONUtil.get(userJsonObject, "status", ""); // 0待审核  1审核通过  2审核未通过
        if (state.equals("1")) {
            viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvIdenty.setVisibility(View.GONE);
        }

        viewHolder.mTxtName.setText(JSONUtil.get(userJsonObject, "username", ""));

        JSONObject workHistorys = userJsonObject.optJSONObject("workHistorys");

        if (!"".equals(workHistorys) && null != workHistorys && !"null".equals(workHistorys)) {
            String position = workHistorys.optString("position");

            String name = workHistorys.optString("name");
            if (!"".equals(name) && null != name) {
                viewHolder.mTxtDesc.setText(position + " | " + name);
            } else {
                viewHolder.mTxtDesc.setVisibility(View.GONE);
            }
        }


       /* String str = "被成功邀请:<font color='#FF0000'>" + userJsonObject.optInt("inviteSuccessCount") + "</font>次";
        viewHolder.mTxtInfo.setText(Html.fromHtml(str));
*/
        viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(mContext, "userInfo", UserInfo.class);

                JSONObject userJsonObject = JSONUtil.getJsonObject(jsonObject, "user");
                if (userJsonObject == null) {
                    userJsonObject = jsonObject;
                }

                mAppointmentModel.init(mUserInfo.getId(), userId, mUserInfo.getPayPassword());

                mAppointmentModel.doDialog(JSONUtil.get(userJsonObject, "money", ""), JSONUtil.get(userJsonObject, "username", ""));

            }
        });

        String money = JSONUtil.get(userJsonObject, "money", "");
/*
        if ("1".equals(JSONUtil.get(userJsonObject, "isJob", ""))) {
            viewHolder.mTxtMoney.setText(money + "元/" + "24小时");
        } else {
            viewHolder.mTxtMoney.setText(money + "元/" + JSONUtil.get(userJsonObject, "time", "") + "分钟");
        }*/


        viewHolder.mLLContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EditDataActivity.class);
                intent.putExtra("UserId", userId);
                mContext.startActivity(intent);
            }
        });

    }

    class ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLLContent;
        TextView mTxtName, mTxtInfo, mCallTv, mTxtDesc, mTxtAppointment, mTxtAttention, mTxtMoney;
    }

}
