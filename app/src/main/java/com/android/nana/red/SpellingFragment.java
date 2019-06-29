package com.android.nana.red;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.eventBus.NullRedEvent;
import com.android.nana.eventBus.RedEvent;
import com.android.nana.eventBus.SettingRedEvent;
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

public class SpellingFragment extends BaseFragment {

    private StateButton mBtn, mDelBtn;
    private EditText mMoneyEt;
    private EditText mNubEt;
    private TextView mDesTv, mNumTv, mOneTv;
    private LinearLayout mDesLl;
    private String mid;
    private TextView mMoneyTv;
    private EditText mKmEt;
    private RedEvent mRed;
    private LinearLayout mSetLl;

    private String mAdvertising, mAdvType, mAdvUrl;
    private TextView mSetTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SpellingFragment.this)) {
            EventBus.getDefault().register(SpellingFragment.this);
        }
    }

    public static SpellingFragment newInstance(RedEvent mRed) {
        SpellingFragment fragment = new SpellingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("red", mRed);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {

    }

    @Override
    public int onSetLayoutId() {
        return R.layout.spelling_fragment;
    }

    @Override
    public void initView() {
        mBtn = mContentView.findViewById(R.id.btn_sure);
        mMoneyEt = mContentView.findViewById(R.id.et_money);
        mNubEt = mContentView.findViewById(R.id.et_nub);
        mDesTv = mContentView.findViewById(R.id.tv_des);
        mDesLl = mContentView.findViewById(R.id.ll_des);
        mNumTv = mContentView.findViewById(R.id.tv_num);
        mOneTv = mContentView.findViewById(R.id.tv_one);
        mMoneyTv = mContentView.findViewById(R.id.tv_money);
        mKmEt = mContentView.findViewById(R.id.et_km);
        mDelBtn = mContentView.findViewById(R.id.btn_del);
        mSetLl = mContentView.findViewById(R.id.ll_set);
        mSetTv = mContentView.findViewById(R.id.tv_set);

        InputFilter[] filters = {new CashierInputFilter(1000000000)};
        mMoneyEt.setFilters(filters);
        Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
        mBtn.setBackground(drawable);
        mBtn.setEnabled(false);

        if (null != getArguments().getSerializable("red")) {
            hideIMM(getActivity(), mKmEt);
            mRed = (RedEvent) getArguments().getSerializable("red");
            mDelBtn.setVisibility(View.VISIBLE);
            if (mRed.type == 1) {
                mNubEt.setText(mRed.num);
                mMoneyEt.setText(mRed.money);
                mNubEt.requestFocus();
                mMoneyEt.requestFocus();

                if (null != mRed.km && !"".equals(mRed.km)) {
                    mKmEt.setText(mRed.km);
                    mKmEt.requestFocus();
                } else {
                    mKmEt.setHint("不限");
                    mKmEt.setHintTextColor(getResources().getColor(R.color.d1d1d1));
                }
                Drawable drawable1 = getResources().getDrawable(R.drawable.edit_bg_shape);
                mBtn.setBackground(drawable1);
                mBtn.setEnabled(true);
            }
        } else {
            mDelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindEvent() {
        mBtn.setOnClickListener(this);
        //金额
        mMoneyEt.addTextChangedListener(mMoneyTw);
        //红包个数
        mNubEt.addTextChangedListener(mNubTw);
        mDelBtn.setOnClickListener(this);
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
                mMoneyTv.setText("￥" + df.format(Double.parseDouble(money)));
            } else {
                mMoneyTv.setText("￥0.00");
            }
            if (!"".equals(num) && !"".equals(money)) {
                String newNum;
                double count = 0;
                if (Utils.isNumeric(num)) {
                    newNum = num.replaceFirst("^0*", "");//去掉前面的0
                    if (!"".equals(newNum)) {
                        count = Double.parseDouble(money) / Double.parseDouble(newNum);
                    } else {
                        mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                        mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mDesTv.setText("请选择红包个数");
                        mNumTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mOneTv.setTextColor(getResources().getColor(R.color.e33d43));

                        Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
                        mBtn.setBackground(drawable);
                        mBtn.setEnabled(false);
                        return;
                    }
                } else {
                    count = Double.parseDouble(money) / Double.parseDouble(num);
                }
                if (count > 200) {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                    mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                    mDesTv.setText("单个红包金额不可超过200元");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));

                    Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
                    mBtn.setBackground(drawable);
                    mBtn.setEnabled(false);
                } else if (count < 0.1) {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                    mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                    mDesTv.setText("单个红包不可低于0.1元");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));

                    Drawable drawable = getResources().getDrawable(R.drawable.btn_bg_shape);
                    mBtn.setBackground(drawable);
                    mBtn.setEnabled(false);
                } else {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                    mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                    mDesTv.setText("拼手气红包，每人抢到的红包金额随机不等。");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));

                    Drawable drawable = getResources().getDrawable(R.drawable.edit_bg_shape);
                    mBtn.setBackground(drawable);
                    mBtn.setEnabled(true);
                }
            } else {
                mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                mDesTv.setText("拼手气红包，每人抢到的红包金额随机不等。");
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
            String money = mMoneyEt.getText().toString();
            String num = mNubEt.getText().toString();

            if (!"".equals(num) && !"".equals(money)) {

                String newNum;
                double count = 0;
                if (Utils.isNumeric(num)) {
                    newNum = num.replaceFirst("^0*", "");//去掉前面的0
                    if (!"".equals(newNum)) {
                        count = Double.parseDouble(money) / Double.parseDouble(newNum);
                    } else {
                        mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                        mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mDesTv.setText("请选择红包个数");
                        mNumTv.setTextColor(getResources().getColor(R.color.e33d43));
                        mOneTv.setTextColor(getResources().getColor(R.color.e33d43));
                        return;
                    }
                } else {
                    count = Double.parseDouble(money) / Double.parseDouble(num);
                }

                if (count > 200) {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                    mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                    mDesTv.setText("单个红包金额不可超过200元");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                } else if (count < 0.1) {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.eccd97));
                    mDesTv.setTextColor(getResources().getColor(R.color.e33d43));
                    mDesTv.setText("单个红包不可低于0.1元");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                } else {
                    mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                    mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                    mDesTv.setText("拼手气红包，每人抢到的红包金额随机不等。");
                    mNumTv.setTextColor(getResources().getColor(R.color.green_33));
                    mOneTv.setTextColor(getResources().getColor(R.color.green_33));
                }
            } else {
                mDesLl.setBackgroundColor(getResources().getColor(R.color.activity_bg));
                mDesTv.setTextColor(getResources().getColor(R.color.green_99));
                mDesTv.setText("拼手气红包，每人抢到的红包金额随机不等。");
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
            case R.id.btn_del:
                mNubEt.setText("");
                mNubEt.setHint("输入个数");
                mNubEt.setHintTextColor(getResources().getColor(R.color.d1d1d1));
                mMoneyEt.setText("");
                mMoneyEt.setHint("输入金额");
                mMoneyEt.setHintTextColor(getResources().getColor(R.color.d1d1d1));
                mKmEt.setText("");
                mKmEt.setHint("不限");
                mKmEt.setHintTextColor(getResources().getColor(R.color.d1d1d1));
                EventBus.getDefault().post(new NullRedEvent());
                getActivity().finish();
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
                intent.putExtra("isRed", "yes");//拼手气红包
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
        RedHttps.checkIsEnough(mid, money, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EventBus.getDefault().post(new RedEvent(money, km, 1, num,mAdvertising,mAdvType,mAdvUrl));// 1 代表拼手气红包
                        getActivity().finish();
                    } else {
                        new AlertDialog.Builder(getActivity()).setTitle("温馨提示").setMessage("您的余额不足，请充值后再3！")
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

    public void hideIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SpellingFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(SettingRedEvent event) {
        if (null != event.adv_type && !"0".equals(event.adv_type)) {
            mSetTv.setText("已设置");
            mSetTv.setTextColor(getResources().getColor(R.color.main_blue));
            mAdvType = event.adv_type;
            mAdvertising = event.advertising;
            mAdvUrl = event.adv_url;
        }else {
            mAdvType = "0";
            mSetTv.setText("未设置");
            mSetTv.setTextColor(getResources().getColor(R.color.d1_d1));
        }
    }
}
