package com.android.nana.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseJsonAdapter;
import com.android.common.utils.DateUtils;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class StatisticsAdapter extends BaseJsonAdapter<StatisticsAdapter.ViewHolder> {

    private String mType;

    private String mThisUserId;

    public StatisticsAdapter(Activity context, String type) {
        super(context);
        mType = type;
        mThisUserId = BaseApplication.getInstance().getCustomerId(context);
    }

    @Override
    protected ViewHolder getViewById(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mTxtInfo = (TextView) view.findViewById(R.id.statistic_item_txt_info);
        viewHolder.mTxtPrice = (TextView) view.findViewById(R.id.statistic_item_txt_price);
        viewHolder.mTxtTime = (TextView) view.findViewById(R.id.statistic_item_txt_time);
        return viewHolder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.statistics_item;
    }

    @Override
    protected void populateData(JSONObject jsonObject, ViewHolder viewHolder) {

        String menoy = JSONUtil.get(jsonObject,"money","");
        viewHolder.mTxtTime.setText(DateUtils.getStringformatLong(Long.parseLong(JSONUtil.get(jsonObject,"updateTime",""))*1000,"yyyy-MM-dd HH:mm:ss"));

        String orderType = JSONUtil.get(jsonObject,"orderType","");
        if (orderType.equals("RECHARGE")){ // 充值

            viewHolder.mTxtInfo.setText("充值");
            viewHolder.mTxtPrice.setText("+￥"+menoy);

        } else {

            JSONObject directSeedingRecord = JSONUtil.getJsonObject(jsonObject,"directSeedingRecord");
            if (directSeedingRecord != null) {
                String userNickName = JSONUtil.get(JSONUtil.getJsonObject(directSeedingRecord,"user"),"username","");

                String thisUserNickName = JSONUtil.get(JSONUtil.getJsonObject(directSeedingRecord,"thisUser"),"username","");

                String type = JSONUtil.get(jsonObject,"type","");
                if (type.equals("INCOME")){ // 收入

                    viewHolder.mTxtPrice.setText("+￥"+menoy);
                    viewHolder.mTxtInfo.setText(Html.fromHtml("<font color='#4388BF'> "+thisUserNickName+" </font>"+"支付给"+"<font color='#4388BF'> 我 </font>"));
                }
                if (type.equals("EXPENDITURE")){ // 支出

                    viewHolder.mTxtPrice.setText("-￥"+menoy);
                    viewHolder.mTxtInfo.setText(Html.fromHtml("<font color='#4388BF'> 我 </font>"+"支付给"+"<font color='#4388BF'> "+userNickName+"</font>"));

                }

            }
        }


    }

    class ViewHolder {
        TextView mTxtPrice,mTxtInfo,mTxtTime;
    }

}
