package com.android.nana.pattern;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.InputFilterMinMax;
import com.android.nana.widget.MoneyEditText;
import com.android.nana.widget.StateButton;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/12/7.
 */

public class SanPatternSetActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private String uid;
    private TextView mBackTv;
    private TextView mTitleTv;
    private MoneyEditText mMoneyEt;
    private SwitchButton mSwitchBtn;
    private RadioGroup mSetBtn;
    private String mType = "1";
    private String money = "";
    private StateButton mSaveBtn;
    private String isOpenFace;//是否开启三分钟模式

    private RadioButton mCostBtn, mChoiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_pattern);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mMoneyEt = findViewById(R.id.et_money);
        mSwitchBtn = findViewById(R.id.switch_button);
        mSwitchBtn.setChecked(true);
        mSaveBtn = findViewById(R.id.btn_save);
        mSetBtn = findViewById(R.id.rg_set);

        mCostBtn = findViewById(R.id.rb_cost);
        mChoiceBtn = findViewById(R.id.rb_choice);
    }

    @Override
    protected void init() {

        if (null != getIntent().getStringExtra("mType")) {
            uid = getIntent().getStringExtra("uid");
            money = getIntent().getStringExtra("money");
            isOpenFace = getIntent().getStringExtra("openFace");
            mType = getIntent().getStringExtra("mType");
            mMoneyEt.setText(money);
            if (mType.equals("1")) {//约见费用
                mCostBtn.setChecked(true);
                mMoneyEt.setHintTextColor(getResources().getColor(R.color.text_40));
                mMoneyEt.setTextColor(getResources().getColor(R.color.text_40));
            } else {
                mChoiceBtn.setChecked(true);
                mMoneyEt.setFocusable(false);
                mMoneyEt.setHintTextColor(getResources().getColor(R.color.green_99));
                mMoneyEt.setTextColor(getResources().getColor(R.color.green_99));
            }
        } else if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            money = getIntent().getStringExtra("money");
            isOpenFace = getIntent().getStringExtra("openFace");
            mMoneyEt.setText(money);
        }

        mTitleTv.setText("哪哪模式设置");
        mMoneyEt.setCursorVisible(false);
        mMoneyEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});

        mMoneyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideIMM(SanPatternSetActivity.this,v);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setOnClickListener(this);
        mMoneyEt.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mSetBtn.setOnCheckedChangeListener(this);
        mSwitchBtn.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.et_money:
                if (mType.equals("1")) {
                    mMoneyEt.setFocusable(true);
                    mMoneyEt.setCursorVisible(true);
                    mMoneyEt.setFocusableInTouchMode(true);
                    mMoneyEt.requestFocus();
                    showIMM(SanPatternSetActivity.this, view);
                }
                break;
            case R.id.btn_save:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {//保存三分钟模式
        showProgressDialog("", "加载中...");
        String money = mMoneyEt.getText().toString().trim().replace(",", "");
        if (mType.equals("1")) {
            if ("0.00".equals(money)) {
                dismissProgressDialog();
                ToastUtils.showToast("请输入正确金额");
                return;
            }
            if (TextUtils.isEmpty(money)) {
                dismissProgressDialog();
                ToastUtils.showToast("金额不能为空！");
                return;
            }
        } else if (mType.equals("0")) {
            money = "";
        }
        PatternDBHelper.open(uid, mType, money, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        SanPatternSetActivity.this.finish();
                        ToastUtils.showToast("设置成功!");
                    } else {
                        ToastUtils.showToast("设置失败，请检查后在设置！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("设置失败，请检查后在设置！");
            }
        });
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked) {

        } else {
            if (isOpenFace.equals("-1")) {//未开启三分钟模式
                SanPatternSetActivity.this.finish();
            } else {
                close();
            }
        }
    }

    private void close() {//关闭三分钟模式
        PatternDBHelper.close(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        SanPatternSetActivity.this.finish();
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
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        RadioButton radioButton = findViewById(mSetBtn.getCheckedRadioButtonId());
        String str = radioButton.getText().toString();
        if (str.equals("设置约见费用")) {
            mType = "1";//是约见费用
            mMoneyEt.setHintTextColor(getResources().getColor(R.color.text_40));
            mMoneyEt.setTextColor(getResources().getColor(R.color.text_40));
        } else {
            mType = "0";//选择由对方出价
            mMoneyEt.setFocusable(false);
            mMoneyEt.setHintTextColor(getResources().getColor(R.color.green_99));
            mMoneyEt.setTextColor(getResources().getColor(R.color.green_99));
            hideIMM(SanPatternSetActivity.this, radioGroup);
        }
    }

    public void hideIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
