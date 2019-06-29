package com.android.nana.red;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.eventBus.RedEvent;
import com.android.nana.eventBus.SettingOrdinaryRedEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by lenovo on 2018/11/5.
 */

public class OrdinaryFragment extends BaseFragment {

    private StateButton mBtn;
    private EditText mMoneyEt;
    private EditText mNubEt;
    private TextView mDesTv, mNumTv, mOneTv;
    private LinearLayout mDesLl;
    private String mid;
    private TextView mMoneyTv;
    private EditText mKmEt;
    private LinearLayout mSetLl;
    private String mAdvertising, mAdvType, mAdvUrl;
    private TextView mSetTv;
    private TextView toast_;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(OrdinaryFragment.this)) {
            EventBus.getDefault().register(OrdinaryFragment.this);
        }
    }

    public static OrdinaryFragment newInstance() {
        OrdinaryFragment fragment = new OrdinaryFragment();
        return fragment;
    }

    @Override
    protected void initData() {
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.ordinary_fragment;
    }

    @Override
    public void initView() {
        toast_=mContentView.findViewById(R.id.toast_);
        mBtn = mContentView.findViewById(R.id.btn_sure);
        mMoneyEt = mContentView.findViewById(R.id.et_money);
        mDesTv = mContentView.findViewById(R.id.tv_des);
        mDesLl = mContentView.findViewById(R.id.ll_des);
        mNumTv = mContentView.findViewById(R.id.tv_num);
        mOneTv = mContentView.findViewById(R.id.tv_one);
        mNubEt = mContentView.findViewById(R.id.et_nub);
        mMoneyTv = mContentView.findViewById(R.id.tv_money);
        mKmEt = mContentView.findViewById(R.id.et_km);
        mSetLl = mContentView.findViewById(R.id.ll_set);
        mSetTv = mContentView.findViewById(R.id.tv_set);

        InputFilter[] filters = {new CashierInputFilter(200)};
        mMoneyEt.setFilters(filters);
        Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
        mBtn.setBackground(drawable);
        mBtn.setEnabled(false);
    }

    @Override
    public void bindEvent() {
        mBtn.setOnClickListener(this);
        //金额
        mMoneyEt.addTextChangedListener(mMoneyTw);
        //红包个数
        mNubEt.addTextChangedListener(mNubTw);
        mSetLl.setOnClickListener(this);
    }

    TextWatcher mMoneyTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String num = mNubEt.getText().toString();
            String money = mMoneyEt.getText().toString();

            if (!"".equals(money)) {
                DecimalFormat df = new DecimalFormat("0.00");
                mMoneyTv.setText("￥" + df.format(Double.parseDouble(num) * Double.parseDouble(money)));
                toast_.setText("点赞用户将获得"+Double.parseDouble(money)/2+"元,另外"+Double.parseDouble(money)/2+"元归到点赞用户的邀请人及平台。");
            } else {
                mMoneyTv.setText("￥0.00");
            }

            if (!"".equals(num) && !"".equals(money)) {
                String newNum;
                if (Utils.isNumeric(num)) {
                    newNum = num.replaceFirst("^0*", "");//去掉前面的0
                    if ("".equals(newNum)) {
                        mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                        mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mDesTv.setText("请选择红包个数");
                        mNumTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mOneTv.setTextColor(getResources().getColor(R.color.e33d43));

                        Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
                        mBtn.setBackground(drawable);
                        mBtn.setEnabled(false);
                        return;
                    } else {
                        if (Double.valueOf(money) < 0.1) {
                            mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                            mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                            mDesTv.setText("单个红包不可低于0.1元");
                            mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                            mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                        } else {
                            mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                            mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                            mDesTv.setText("普通红包，按点赞时间顺序给到点赞此条动态用户。");
                            mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                            mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                            if (!"".equals(money)) {
                                Drawable drawable = getResources().getDrawable(R.drawable.edit_bg_shape);
                                mBtn.setBackground(drawable);
                                mBtn.setEnabled(true);
                            }
                        }
                    }
                }
            } else {
                mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                mDesTv.setText("普通红包，按点赞时间顺序给到点赞此条动态用户。");
                mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                if (!"".equals(money)) {
                    Drawable drawable = getResources().getDrawable(R.drawable.edit_bg_shape);
                    mBtn.setBackground(drawable);
                    mBtn.setEnabled(true);
                }
            }
        }
    };

    TextWatcher mNubTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String num = mNubEt.getText().toString();
            String money = mMoneyEt.getText().toString();
            if (!"".equals(num) && !"".equals(money)) {
                String newNum;
                if (Utils.isNumeric(num)) {
                    newNum = num.replaceFirst("^0*", "");//去掉前面的0
                    if ("".equals(newNum)) {
                        mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                        mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mDesTv.setText("请选择红包个数");
                        mNumTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mOneTv.setTextColor(getResources().getColor(R.color.e33d43));
                        return;
                    } else {
                        mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                        mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                        mDesTv.setText("红包按点赞时间顺序给到点赞此条动态用户。");
                        mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                        mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                    }
                }
            } else {
                mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                mDesTv.setText("红包按点赞时间顺序给到点赞此条动态用户。");
                mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                mOneTv.setTextColor(getResources().getColor(R.color.green_33));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                sureBtn();
                break;
            case R.id.ll_set:
                Intent intent = new Intent(getActivity(), SettingRedActivity.class);
                String mNubStr = mNubEt.getText().toString().trim();
                String mMoneyStr = mMoneyEt.getText().toString().trim();
                if (TextUtils.isEmpty(mNubStr)) {
                    ToastUtils.showToast("请输入红包个数及金额");
                    return;
                }
                if (TextUtils.isEmpty(mMoneyStr)) {
                    ToastUtils.showToast("请输入红包个数及金额");
                    return;
                }

                String mKmStr;
                if ("".equals(mKmEt.getText().toString())) {
                    mKmStr = "-1";
                } else {
                    mKmStr = mKmEt.getText().toString();
                }

                if (null != mAdvType && !"0".equals(mAdvType)) {
                    intent.putExtra("type", mAdvType);
                    intent.putExtra("url", mAdvUrl);
                    intent.putExtra("advertising", mAdvertising);
                }
                intent.putExtra("isRed", "no");//普通红包
                intent.putExtra("nub", mNubStr);
                intent.putExtra("money", mMoneyStr);
                intent.putExtra("km", mKmStr);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void sureBtn() {
        final String money = mMoneyEt.getText().toString();
        final String num = mNubEt.getText().toString();
        final String km;
        if ("".equals(mKmEt.getText().toString())) {
            km = "-1";
        } else {
            km = mKmEt.getText().toString();
        }
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        final double total = Double.parseDouble(num) * Double.parseDouble(money); //总金额
        final String mTotalMoney =  String.format("%.2f", total);

        RedHttps.checkIsEnough(mid, mTotalMoney, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EventBus.getDefault().post(new RedEvent(mTotalMoney, km, 2, num, mAdvertising, mAdvType, mAdvUrl));// 2 代表普通红包
                        getActivity().finish();
                    } else {
                        new AlertDialog.Builder(getActivity()).setTitle("温馨提示").setMessage("您的余额不足，请充值后再发布！")
                                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("UserId", mid);
                                        intent.putExtra("IsAnchor", true);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(OrdinaryFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(SettingOrdinaryRedEvent event) {
        if (null != event.adv_type && !"0".equals(event.adv_type)) {
            mSetTv.setText("已设置");
            mSetTv.setTextColor(getResources().getColor(R.color.main_blue));
            mAdvType = event.adv_type;
            mAdvertising = event.advertising;
            mAdvUrl = event.adv_url;
        } else {
            mAdvType = "0";
            mSetTv.setText("未设置");
            mSetTv.setTextColor(getResources().getColor(R.color.d1_d1));
        }
    }
}
