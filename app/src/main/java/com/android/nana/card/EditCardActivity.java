package com.android.nana.card;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.StringHelper;
import com.android.nana.R;
import com.android.nana.activity.BasePagerAdapter;
import com.android.nana.activity.NoScrollViewPager;
import com.android.nana.card.bean.InfoBean;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.Modular1EditFragmentEvent;
import com.android.nana.eventBus.Modular1FragmentEvent;
import com.android.nana.eventBus.Modular2EditFragmentEvent;
import com.android.nana.eventBus.Modular2FragmentEvent;
import com.android.nana.eventBus.Modular3EditFragmentEvent;
import com.android.nana.eventBus.Modular3FragmentEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by lenovo on 2018/1/18.
 */

public class EditCardActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRight2Tv;
    private EditText mNameEt;
    private EditText mPostEt;
    private EditText mCompanyEt;
    private EditText mPhoneEt, mMobileEt, mFaxEt, mMmailEt, mAddressEt, mCodeEt;

    private BasePagerAdapter mPagerAdapter;
    private NoScrollViewPager mViewPager;
    private CheckBox mCheckBoxFrom1, mCheckBoxFrom2, mCheckBoxFrom3;
    private int selectedItem = 0;
    private long mLastClickTime = 0;
    private int CLICK_STEP = 200;

    private String mHasCard, mAddCard, mCardId;//是否有名片
    private String mid;
    private InfoBean mInfoBean;
    private LinearLayout mDeleteCard;
    private StateButton mDeleteBtn;

    private String name;
    private String post;
    private String company;
    private String phone;
    private String mobile;
    private String fax;
    private String mail;
    private String address;
    private String code;
    private String type;
    private String logo;
    private String card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_card);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mNameEt = findViewById(R.id.et_name);
        mPostEt = findViewById(R.id.et_post);
        mCompanyEt = findViewById(R.id.et_company);
        mViewPager = findViewById(R.id.container);

        mCheckBoxFrom1 = findViewById(R.id.checkBox_from1);
        mCheckBoxFrom2 = findViewById(R.id.checkBox_from2);
        mCheckBoxFrom3 = findViewById(R.id.checkBox_from3);

        mPhoneEt = findViewById(R.id.et_phone);
        mMobileEt = findViewById(R.id.et_mobile);
        mFaxEt = findViewById(R.id.et_fax);
        mMmailEt = findViewById(R.id.et_mail);
        mAddressEt = findViewById(R.id.et_address);
        mCodeEt = findViewById(R.id.et_code);
        mDeleteCard = findViewById(R.id.ll_del);
        mDeleteBtn = findViewById(R.id.btn_del);
    }

    @Override
    protected void init() {
        mTitleTv.setText("编辑名片");
        mRight2Tv.setText("保存");
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        String str = "请输入姓名<font color='#F85F48'>（必填）</font>";
        mNameEt.setHint(Html.fromHtml(str));
        String str1 = "请输入职务<font color='#F85F48'>（必填）</font>";
        mPostEt.setHint(Html.fromHtml(str1));//mCompanyEt
        String str2 = "请输入公司/单位<font color='#F85F48'>（必填）</font>";
        mCompanyEt.setHint(Html.fromHtml(str2));

        mid = (String) SharedPreferencesUtils.getParameter(EditCardActivity.this, "userId", "");
        if (null != getIntent().getStringExtra("hasCard")) {
            mHasCard = getIntent().getStringExtra("hasCard");//-1为没有名片
            if (null != getIntent().getStringExtra("addCard")) {//添加名片
                mAddCard = getIntent().getStringExtra("addCard");
                loadInfo(mid);
            } else {
                type = getIntent().getStringExtra("templatestl");
                mCardId = getIntent().getStringExtra("cardId");

                name = getIntent().getStringExtra("name");
                post = getIntent().getStringExtra("position");
                company = getIntent().getStringExtra("company");
                phone = getIntent().getStringExtra("phone");
                mobile = getIntent().getStringExtra("mobile");
                fax = getIntent().getStringExtra("fax");
                mail = getIntent().getStringExtra("email");
                address = getIntent().getStringExtra("address");
                code = getIntent().getStringExtra("postal");
                logo = getIntent().getStringExtra("logo");
                card = getIntent().getStringExtra("card");

                mNameEt.setText(name);
                mPostEt.setText(post);
                mCompanyEt.setText(company);
                mPhoneEt.setText(phone);
                mMobileEt.setText(mobile);
                mFaxEt.setText(fax);
                mMmailEt.setText(mail);
                mAddressEt.setText(address);
                mCodeEt.setText(code);
                mDeleteCard.setVisibility(View.VISIBLE);
            }
        }
        setAdapter();
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectedItem = mViewPager.getCurrentItem();
        setSelecteIcon();
    }

    private void setSelecteIcon() {
        Drawable drawableFrom1 = getResources().getDrawable(R.drawable.icon_card_from_unselect);
        drawableFrom1.setBounds(0, 0, drawableFrom1.getMinimumWidth(), drawableFrom1.getMinimumHeight());
        mCheckBoxFrom1.setCompoundDrawables(null, drawableFrom1, null, null);

        Drawable drawableFrom2 = getResources().getDrawable(R.drawable.icon_card_from2_unselect);
        drawableFrom2.setBounds(0, 0, drawableFrom2.getMinimumWidth(), drawableFrom2.getMinimumHeight());
        mCheckBoxFrom2.setCompoundDrawables(null, drawableFrom2, null, null);

        Drawable drawableFrom3 = getResources().getDrawable(R.drawable.icon_card_from3_unselect);
        drawableFrom3.setBounds(0, 0, drawableFrom3.getMinimumWidth(), drawableFrom3.getMinimumHeight());
        mCheckBoxFrom3.setCompoundDrawables(null, drawableFrom3, null, null);

        switch (selectedItem) {
            case 0:
                Drawable drawable = getResources().getDrawable(R.drawable.icon_card_mod1_select);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mCheckBoxFrom1.setCompoundDrawables(null, drawable, null, null);
                break;
            case 1:
                Drawable drawable1 = getResources().getDrawable(R.drawable.icon_card_mod2_select);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                mCheckBoxFrom2.setCompoundDrawables(null, drawable1, null, null);
                break;
            case 2:
                Drawable drawable2 = getResources().getDrawable(R.drawable.icon_card_mod3_select);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                mCheckBoxFrom3.setCompoundDrawables(null, drawable2, null, null);
                break;
            default:
                break;
        }
    }

    private void setAdapter() {
        FragmentManager fragmentManager = EditCardActivity.this.getSupportFragmentManager();
        String[] titles = new String[]{"名片模板1", "名片模板2", "名片模板3"};

        if (null != type) {

            Fragment[] fragments = new Fragment[]{
                    Modular1Fragment.newInstance(mHasCard, mAddCard, mCardId, name, post, company, phone, mobile, fax, mail, address, code, logo),
                    Modular2Fragment.newInstance(mHasCard, mAddCard,mCardId, name, post, company, phone, mobile, fax, mail, address, code, logo),
                    Modular3Fragment.newInstance(mHasCard, mAddCard,mCardId, name, post, company, phone, mobile, fax, mail, address, code, logo)
            };
            mPagerAdapter = new BasePagerAdapter(fragmentManager, titles, fragments);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setNoScroll(true);
            mViewPager.setOffscreenPageLimit(0);
        } else {
            Fragment[] fragments = new Fragment[]{
                    Modular1Fragment.newInstance(mHasCard, mAddCard),
                    Modular2Fragment.newInstance(mHasCard, mAddCard),
                    Modular3Fragment.newInstance(mHasCard, mAddCard)
            };
            mPagerAdapter = new BasePagerAdapter(fragmentManager, titles, fragments);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setNoScroll(true);
            mViewPager.setOffscreenPageLimit(0);
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mCheckBoxFrom1.setOnClickListener(this);
        mCheckBoxFrom2.setOnClickListener(this);
        mCheckBoxFrom3.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_del:
                delete();
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                if (null != getIntent().getStringExtra("addCard")) {
                    save();
                } else {
                    edit();
                }

                break;
            case R.id.checkBox_from1:
                if (selectedItem == 0) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //更新数据
                        //  EventBus.getDefault().post(new MessageEvent("logout"));
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 0;
                setSelecteIcon();
                break;
            case R.id.checkBox_from2:
                if (selectedItem == 1) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 1;
                setSelecteIcon();
                break;
            case R.id.checkBox_from3:
                if (selectedItem == 2) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 2;
                setSelecteIcon();
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(selectedItem);
    }

    private void edit() {//编辑名片
        name = mNameEt.getText().toString();
        post = mPostEt.getText().toString();
        company = mCompanyEt.getText().toString();
        phone = mPhoneEt.getText().toString();
        mobile = mMobileEt.getText().toString();
        fax = mFaxEt.getText().toString();
        mail = mMmailEt.getText().toString();
        address = mAddressEt.getText().toString();
        code = mCodeEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("姓名不能为空！");
            return;
        }

        if (TextUtils.isEmpty(post)) {
            ToastUtils.showToast("职务不能为空！");
            return;
        }

        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("公司名不能为空！");
            return;
        }

        if (!"".equals(mail)) {
            if (!StringHelper.checkEmail(mail)) {
                ToastUtils.showToast("请输入有效邮箱!");
                return;
            }
        }

        switch (selectedItem) {
            case 0:
                EventBus.getDefault().post(new Modular1EditFragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            case 1:
                EventBus.getDefault().post(new Modular2EditFragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            case 2:
                EventBus.getDefault().post(new Modular3EditFragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            default:
                break;
        }
    }

    private void save() {
        name = mNameEt.getText().toString();
        post = mPostEt.getText().toString();
        company = mCompanyEt.getText().toString();
        phone = mPhoneEt.getText().toString();
        mobile = mMobileEt.getText().toString();
        fax = mFaxEt.getText().toString();
        mail = mMmailEt.getText().toString();
        address = mAddressEt.getText().toString();
        code = mCodeEt.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("姓名不能为空！");
            return;
        }

        if (TextUtils.isEmpty(post)) {
            ToastUtils.showToast("职务不能为空！");
            return;
        }

        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("公司名不能为空！");
            return;
        }

        if (!"".equals(mail)) {
            if (!StringHelper.checkEmail(mail)) {
                ToastUtils.showToast("请输入有效邮箱!");
                return;
            }
        }

        switch (selectedItem) {
            case 0:
                EventBus.getDefault().post(new Modular1FragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            case 1:
                EventBus.getDefault().post(new Modular2FragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            case 2:
                EventBus.getDefault().post(new Modular3FragmentEvent(mid, name, post, company, phone, mobile, fax, mail, address, code));//更新通讯录数量
                break;
            default:
                break;
        }
    }

    private void loadInfo(String mid) {//原数据
        CardDbHelper.oldPersonInfo(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mInfoBean = parseData(successJson);
                        mNameEt.setText(mInfoBean.getUsername());
                        mPhoneEt.setText(mInfoBean.getPhone());
                        mPostEt.setText(mInfoBean.getPosition());
                        mCompanyEt.setText(mInfoBean.getCompany());
                        mNameEt.setSelection(mInfoBean.getUsername().length());
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

    public InfoBean parseData(String result) {
        InfoBean info = new InfoBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            Gson gson = new Gson();
            info = gson.fromJson(jsonobject.getString("data"), InfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private void delete() {
        DialogHelper.customAlert(EditCardActivity.this, "提示", "确定删除此名片?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                delCard();
            }
        }, null);
    }

    private void delCard() {
        CardDbHelper.delCard(mid, mCardId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("删除成功！");
                        EditCardActivity.this.finish();
                        EventBus.getDefault().post(new MessageEvent(""));//通知更新名片列表
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
}
