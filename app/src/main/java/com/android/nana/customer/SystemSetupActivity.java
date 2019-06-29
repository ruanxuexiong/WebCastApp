package com.android.nana.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.partner.BlacklistActivity;
import com.android.nana.ui.UITableView;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.webview.JumpWebViewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class SystemSetupActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;
    private UserInfo mUserInfo;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SystemSetupActivity.this)) {
            EventBus.getDefault().register(SystemSetupActivity.this);
        }
    }

    @Override
    protected void bindViews() {

        setContentView(R.layout.system_setup);
    }

    @Override
    protected void findViewById() {

        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle =  findViewById(R.id.tv_title);
        mTxtTitle.setText("个人设置");
        mIbBack.setVisibility(View.VISIBLE);
        if (null != getIntent().getStringExtra("mid")) {
            uid = getIntent().getStringExtra("mid");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(this, "userInfo", UserInfo.class);

        UITableView table =  findViewById(R.id.system_setup_ll_tableview);
        table.clearView();
        table.addBasicItem(0, "邮箱地址", mUserInfo.getUser_email());
        table.addBasicItem(0, "手机号码", mUserInfo.getMobile());
        table.addBasicItem(0, "修改密码", "");
        table.addBasicItem(0, "通知设置", "", true);
        table.addBasicItem(0, "关于哪哪", "", true);
        table.addBasicItem(0, "帮助中心", "");
        table.addBasicItem(0, "隐私政策", "");
        table.addBasicItem(0, "用户协议", "");
        table.addBasicItem(0, "黑名单列表", "");
        table.addBasicItem(0, "退出登录", "");
        table.setOnUITableClickLister(new UITableView.UITableClickLister() {
            @Override
            public void onClick(int index) {

                switch (index) {
                    case 0: // 邮箱地址
                        startActivity(new Intent(SystemSetupActivity.this, EmailActivity.class));
                        break;
                    case 1: // 手机号码
                        startActivity(new Intent(SystemSetupActivity.this, UpdatePhoneActivity.class));
                        break;
                    case 2: // 修改密码
                        if (mUserInfo.getLogin_type().equals("MOBILE")) { // 只允许手机登录用户修改密码
                            Intent intent = new Intent(SystemSetupActivity.this, UpdatePassActivity.class);
                            intent.putExtra("phone", mUserInfo.getMobile());
                            startActivity(intent);
                        } else if (mUserInfo.getLogin_type().equals("WECHAT")) {
                            UIHelper.showToast(SystemSetupActivity.this, "不能修改微信登录密码");
                        } else if (mUserInfo.getLogin_type().equals("MICRO_BLOG")) {
                            UIHelper.showToast(SystemSetupActivity.this, "不能修改新浪登录密码");
                        }
                        break;
                    case 3://通知设置
                        Intent intents = new Intent(SystemSetupActivity.this, NoticeActivity.class);//uid
                        intents.putExtra("uid", uid);
                        startActivity(intents);
                        break;
                    case 4: // 关于直播
                        startActivity(new Intent(SystemSetupActivity.this, AboutWebCastActivity.class));
                        break;
                    case 5: // 帮助中心
                        startJumpActivity(JumpWebViewActivity.class, "帮助中心", "3");
                        break;
                    case 6: // 隐私政策
                        startJumpActivity(JumpWebViewActivity.class, "隐私政策", "4");
                        break;
                    case 7: // 用户协议
                        startJumpActivity(JumpWebViewActivity.class, "用户协议", "5");
                        break;
                    case 8: // 黑名单
                        Intent intent = new Intent(SystemSetupActivity.this, BlacklistActivity.class);//uid
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                        break;
                    case 9:
                        logout();
                        break;
                }
            }
        });
        table.builder();

    }

    private void logout() {
        DialogHelper.customAlert(SystemSetupActivity.this, "提示", "确定退出登录吗?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                String mui = (String) SharedPreferencesUtils.getParameter(SystemSetupActivity.this, "userId", "");
                WebCastDbHelper.outLogin(mui, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {
                                SharedPreferencesUtils.setParameter(SystemSetupActivity.this, "userId", "");
                                SharedPreferencesUtils.removeParameter(SystemSetupActivity.this, "userId");
                                SharedPreferencesUtils.saveObject(SystemSetupActivity.this, "userInfo", null);
                                deleteData();//清空搜索历史
                                RongIM.getInstance().disconnect();
                                EventBus.getDefault().post(new MessageEvent("logout"));
                                SystemSetupActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        ToastUtils.showToast("退出失败，请稍后重试!");
                    }
                });
            }
        }, null);
    }

    private void deleteData() {//清空历史记录

        if (null != SearchActivity.db) {
            SearchActivity.db = SearchActivity.helper.getWritableDatabase();
            SearchActivity.db.execSQL("delete from records");
            SearchActivity.db.close();
        }
    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {

        Intent intent = new Intent(SystemSetupActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {
        mIbBack.setOnClickListener(mBackPullListener);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onThisFinish(MessageEvent messageEvent) {
      /*  if (messageEvent.message.equals("mNewPass")) {
            SystemSetupActivity.this.finish();
            EventBus.getDefault().post(new MessageEvent("logout"));
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(SystemSetupActivity.this);
    }
}
