package com.android.nana.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.helper.UIHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.CheckPermissionsActivity;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.eventBus.WelcomeEvent;
import com.android.nana.listener.LoginListener;
import com.android.nana.login.thirdlogin.ThirdSinaLoginBuilder;
import com.android.nana.login.thirdlogin.UserInfo;
import com.android.nana.main.MainActivity;
import com.android.nana.model.LoginModel;
import com.android.nana.util.Constant;
import com.android.nana.util.ForAllUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webview.JumpWebViewActivity;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by THINK on 2017/7/18.
 */

public class WelcomeActivity extends CheckPermissionsActivity implements View.OnClickListener, OnItemClickListener {

    private TextView mLoginBtn;
    private AlertView mAlertView;
    private TextView mRegisterTv;
    private RelativeLayout mWxLayout, mWbLayout;
    private IWXAPI mIWXAPI;

    private TextView mProtocolTv;

    private ThirdSinaLoginBuilder mSinaLoginBuilder;
    private String mSinaAppKey = "3371819193"; // 3371819193
    private String mRedirectUrl = "https://sns.whalecloud.com/sina2/callback";
    private String mScope = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != SharedPreferencesUtils.getParameter(getApplicationContext(), "mtcLogouted", "") && !"".equals(SharedPreferencesUtils.getParameter(getApplicationContext(), "mtcLogouted", ""))) {
            showDialogs();
        }

        if (!EventBus.getDefault().isRegistered(WelcomeActivity.this)) {
            EventBus.getDefault().register(WelcomeActivity.this);
        }
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void findViewById() {
        mLoginBtn = findViewById(R.id.btn_logi);
        mRegisterTv = findViewById(R.id.tv_register);

        mWxLayout = findViewById(R.id.wx_login);
        mWbLayout = findViewById(R.id.wb_login);

        mProtocolTv = findViewById(R.id.tv_protocol);
        mProtocolTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void init() {
        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.Payment.WxPay.APP_ID);
        mSinaLoginBuilder = new ThirdSinaLoginBuilder(this, mSinaAppKey, mRedirectUrl, mScope);
    }

    @Override
    protected void setListener() {
        mLoginBtn.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);
        mWxLayout.setOnClickListener(this);
        mWbLayout.setOnClickListener(this);
        mProtocolTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                break;
            case R.id.btn_logi:
                startActivity(new Intent(WelcomeActivity.this, LoginCodeActivity.class));
                break;
            case R.id.wx_login:
                if (ForAllUtils.isWeixinAvilible(WelcomeActivity.this)) {
                    wxLogin();
                } else {
                    ToastUtils.showToast("登录失败，请安装微信客户端！");
                }

                break;
            case R.id.wb_login:
                if (ForAllUtils.isWeiboAvilible(WelcomeActivity.this)) {
                    wbLogin();
                } else {
                    ToastUtils.showToast("登录失败，请安装微博客户端！");
                }
                break;
            case R.id.tv_protocol:
                startJumpActivity(JumpWebViewActivity.class, "用户协议", "5");
                break;
        }
    }

    private void wbLogin() {//微博登录
        mSinaLoginBuilder.loginSina(new ThirdSinaLoginBuilder.LoginSinaBackListener() {

            @Override
            public void onWeiboException(WeiboException e) {

                UIHelper.showToast(WelcomeActivity.this, e.getMessage());

            }

            @Override
            public void onCancel() {

                UIHelper.showToast(WelcomeActivity.this, "取消授权");

            }

            @Override
            public void onSuccess(UserInfo user, String success) {

                doThirdLogin(success, "MICRO_BLOG");

            }

            @Override
            public void onError(String error) {

                UIHelper.showToast(WelcomeActivity.this, error);
            }
        });

    }


    private void startJumpActivity(Class<?> clx, String title, String termId) {
        Intent intent = new Intent(WelcomeActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

    private void wxLogin() {//微信登录
        SendAuth.Req req = new SendAuth.Req();
        //授权读取用户信息
        req.scope = "snsapi_userinfo";
        //自定义信息
        req.state = "wechat_sdk_demo_test";
        //向微信发送请求
        mIWXAPI.sendReq(req);

        LoginListener.getInstance().mThirdBackListener = new LoginListener.ThirdLoginBackListener() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (null != jsonObject.getString("openid")) {
                        doThirdLogin(result, "WECHAT");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    private void doThirdLogin(final String success, final String type) {

        try {
            final String json = URLEncoder.encode(success, "UTF-8");

            final String registrationId = JPushInterface.getRegistrationID(this);

            LoginDbHelper.thirdLogin(type, registrationId, json, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonobject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonobject.getString("result"));
                        if (result.getString("state").equals("-1")) {
                            Intent intent = new Intent(WelcomeActivity.this, LoginPhoneActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("registrationId", registrationId);
                            intent.putExtra("grantString", json);
                            startActivity(intent);
                        } else {
                            LoginModel.doLogin(WelcomeActivity.this, successJson, "");
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            WelcomeActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {

                    Log.e("返回值===", failueJson);
                }
            });

        /*    LoginDbHelper.login(type, json, registrationId, new IOAuthCallBack() {

                @Override
                public void onStartRequest() {

                    UIHelper.showOnLoadingDialog(WelcomeActivity.this);
                }

                @Override
                public void getSuccess(String successJson) {

                    SharedPreferencesUtils.setParameter(WelcomeActivity.this, "json", json);

                    LoginModel.doLogin(WelcomeActivity.this, successJson, "FacethreeZhibo2017");

                    UIHelper.hideOnLoadingDialog();
                }

                @Override
                public void getFailue(String failueJson) {

                    UIHelper.showToast(WelcomeActivity.this, failueJson);
                    UIHelper.hideOnLoadingDialog();

                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialogs() {
        mAlertView = new AlertView("下线通知", "您的账号已在其他设备登录", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, this).setCancelable(true);
        mAlertView.show();
    }

    @Override
    public void onItemClick(Object o, int position) {
        SharedPreferencesUtils.removeParameter(getApplicationContext(), "mtcLogouted");
        mAlertView.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case ThirdSinaLoginBuilder.mRequestCode: // Sina 登录返回

                mSinaLoginBuilder.onActivityResult(requestCode, resultCode, data);

                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(WelcomeActivity.this);
    }

    //关闭欢迎页面
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onThiFinish(WelcomeEvent welcomeEvent) {
          WelcomeActivity.this.finish();
    }
}
