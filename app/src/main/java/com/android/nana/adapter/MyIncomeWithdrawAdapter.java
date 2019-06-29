package com.android.nana.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseJsonAdapter;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;

import org.json.JSONObject;

/**
 * Created by Cristina on 2017/3/24.
 */
public class MyIncomeWithdrawAdapter extends BaseJsonAdapter<MyIncomeWithdrawAdapter.ViewHolder>{

    public MyIncomeWithdrawAdapter(Activity context) {
        super(context);
    }

    @Override
    protected ViewHolder getViewById(View view) {
        ViewHolder holder = new ViewHolder();
        holder.mTxtName = (TextView) view.findViewById(R.id.withdraw_item_txt_name);
        return holder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_lv_my_income_withdraw_activity;
    }

    @Override
    protected void populateData(JSONObject jsonObject, ViewHolder viewHolder) {

        viewHolder.mTxtName.setText(JSONUtil.get(jsonObject,"name",""));

    }

    public class ViewHolder{
        private TextView mTxtName;
    }
}
