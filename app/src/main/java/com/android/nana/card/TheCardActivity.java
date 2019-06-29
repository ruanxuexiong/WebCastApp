package com.android.nana.card;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.bean.InfoBean;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.TheCardEvent;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by lenovo on 2018/1/24.
 * 备注名片信息
 */

public class TheCardActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mCardIv;
    private EditText mNameEt;
    private EditText mPostEt;
    private EditText mCompanyEt;
    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRight2Tv;
    private String mPath;
    private LinearLayout mDelll;
    private StateButton mDelBtn;

    private String name;
    private String post;
    private String company;
    private String mid;
    private File file;

    private String cardId;
    private String mUid;
    private String type;
    private String mCreate;
    private InfoBean mInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_the_card);
    }

    @Override
    protected void findViewById() {
        mCardIv = findViewById(R.id.iv_card);
        mNameEt = findViewById(R.id.et_name);
        mPostEt = findViewById(R.id.et_post);
        mCompanyEt = findViewById(R.id.et_company);
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mDelll = findViewById(R.id.ll_del);
        mDelBtn = findViewById(R.id.btn_del);
    }

    @Override
    protected void init() {
        mTitleTv.setText("备注名片信息");
        mRight2Tv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("保存");
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        mid = (String) SharedPreferencesUtils.getParameter(TheCardActivity.this, "userId", "");

        if (null != getIntent().getStringExtra("info")) {
            loadInfo(mid);
        }


        if (null != getIntent().getStringExtra("create")) {
            mCreate = getIntent().getStringExtra("create");
        }

        if (null != getIntent().getStringExtra("type")) {
            type = getIntent().getStringExtra("type");
        }

        if (null != getIntent().getStringExtra("path")) {
            mPath = getIntent().getStringExtra("path");
            file = new File(mPath);
            mCardIv.setImageURI(Uri.fromFile(new File(mPath)));
        }
        String str = "请输入姓名<font color='#F85F48'></font>";
        mNameEt.setHint(Html.fromHtml(str));
        String str1 = "请输入职务<font color='#F85F48'></font>";
        mPostEt.setHint(Html.fromHtml(str1));//mCompanyEt
        String str2 = "请输入公司/单位<font color='#F85F48'></font>";
        mCompanyEt.setHint(Html.fromHtml(str2));


        if (null != getIntent().getStringExtra("cardId")) {
            cardId = getIntent().getStringExtra("cardId");
            mUid = getIntent().getStringExtra("uid");
            mNameEt.setText(getIntent().getStringExtra("name"));
            mPostEt.setText(getIntent().getStringExtra("position"));
            mCompanyEt.setText(getIntent().getStringExtra("company"));
            mDelll.setVisibility(View.VISIBLE);

            new ImgLoaderManager(R.drawable.icon_default).showImageView(getIntent().getStringExtra("card"), mCardIv, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    if (TextUtils.isEmpty(s)) return;

                    file = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

    private void loadInfo(String mid) {

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
                        mPostEt.setText(mInfoBean.getPosition());
                        mCompanyEt.setText(mInfoBean.getCompany());

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
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                TheCardActivity.this.finish();
                break;
            case R.id.toolbar_right_2:
                if (null != type && type.equals("2") && null == mCreate) {
                    edit2();
                } else if (null != cardId) {
                    edit();
                } else {
                    save();
                }
                break;
            case R.id.btn_del:
                if (null != type && type.equals("2")) {
                    delete2();
                } else {
                    delete();
                }
                break;
            default:
                break;
        }
    }

    private void delete2() {
        DialogHelper.customAlert(TheCardActivity.this, "提示", "确定删除此名片?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                delCard2();
            }
        }, null);

    }

    private void delCard2() {
        CardDbHelper.delCard(mid, cardId, new IOAuthCallBack() {
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
                        TheCardActivity.this.finish();
                        EventBus.getDefault().post(new TheCardEvent());//更新名片夹列表
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

    private void edit2() {

        showProgressDialog("", "加载中...");
        name = mNameEt.getText().toString();
        post = mPostEt.getText().toString();
        company = mCompanyEt.getText().toString();
/*
      if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("姓名不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(post)) {
            ToastUtils.showToast("职务不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("公司名不能为空！");
            dismissProgressDialog();
            return;
        }*/
        CardDbHelper.addCard("2", mid, cardId, name, post, company, "", file, null, "", "", "", "", "0", "", new IOAuthCallBack() {
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
                        ToastUtils.showToast("编辑成功!");
                        TheCardActivity.this.finish();
                        EventBus.getDefault().post(new TheCardEvent());//更新名片夹列表
                    } else {
                        ToastUtils.showToast(result.getString("description"));
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

    private void delete() {//删除
        DialogHelper.customAlert(TheCardActivity.this, "提示", "确定删除此名片?", new DialogHelper.OnAlertConfirmClick() {
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
        CardDbHelper.delCard(mid, cardId, new IOAuthCallBack() {
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
                        TheCardActivity.this.finish();
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

    private void edit() {
        showProgressDialog("", "加载中...");
        name = mNameEt.getText().toString();
        post = mPostEt.getText().toString();
        company = mCompanyEt.getText().toString();
/*
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("姓名不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(post)) {
            ToastUtils.showToast("职务不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("公司名不能为空！");
            dismissProgressDialog();
            return;
        }*/
        CardDbHelper.addCard("1", mid, cardId, name, post, company, "", file, null, "", "", "", "", "0", "", new IOAuthCallBack() {
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
                        ToastUtils.showToast("编辑成功!");
                        TheCardActivity.this.finish();
                        EventBus.getDefault().post(new MessageEvent(""));//通知更新名片列表
                    } else {
                        ToastUtils.showToast(result.getString("description"));
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

    private void save() {
        showProgressDialog("", "加载中...");
        name = mNameEt.getText().toString();
        post = mPostEt.getText().toString();
        company = mCompanyEt.getText().toString();

      /* if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("姓名不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(post)) {
            ToastUtils.showToast("职务不能为空！");
            dismissProgressDialog();
            return;
        }

        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("公司名不能为空！");
            dismissProgressDialog();
            return;
        }
*/
        if (null != type && type.equals("2")) {//2代表名片夹

            CardDbHelper.addCard("2", mid, "", name, post, company, "", file, null, "", "", "", "", "0", "", new IOAuthCallBack() {
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
                            ToastUtils.showToast("保存成功!");
                            TheCardActivity.this.finish();
                            EventBus.getDefault().post(new TheCardEvent());//更新名片夹列表
                        } else {
                            ToastUtils.showToast(result.getString("description"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {

                }
            });
        } else {

            CardDbHelper.addCard("1", mid, "", name, post, company, "", file, null, "", "", "", "", "0", "", new IOAuthCallBack() {
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
                            ToastUtils.showToast("保存成功!");
                            TheCardActivity.this.finish();
                            EventBus.getDefault().post(new MessageEvent(""));//通知更新名片列表
                        } else {
                            ToastUtils.showToast(result.getString("description"));
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
}
