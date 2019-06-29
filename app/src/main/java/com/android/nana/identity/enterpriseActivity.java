package com.android.nana.identity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.util.pay.SignUtils;
import com.android.nana.webview.JumpWebViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class enterpriseActivity extends AppCompatActivity {
    TextView tv_title;
    AppCompatTextView mBackTv;
    Button start_enterprise;
    LinearLayout qiye_lin1, qiye_lin2, renzhi_lin;
    ImageView renzhi_x;
    ScrollView renzhen_scroll;
    private TextView price_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("企业认证");
        renzhen_scroll = findViewById(R.id.renzhen_scroll);
        renzhi_lin = findViewById(R.id.renzhi_lin);
        renzhi_x = findViewById(R.id.renzhi_x);

        mBackTv = findViewById(R.id.iv_toolbar_back);
        qiye_lin1 = findViewById(R.id.qiye_lin1);
        qiye_lin2 = findViewById(R.id.qiye_lin2);
        start_enterprise = findViewById(R.id.start_enterprise);
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        price_tv=findViewById(R.id.price_tv);
        start_enterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 18566
                //开始认证
                Intent intent = new Intent(enterpriseActivity.this, enterprise_twoActivity.class);
                startActivity(intent);
            }
        });

        qiye_lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renzhi_lin.setVisibility(View.VISIBLE);
                renzhen_scroll.setVisibility(View.GONE);
            }
        });
        qiye_lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                showNormalDialog();
            }
        });

        renzhi_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                renzhen_scroll.setVisibility(View.VISIBLE);
                renzhi_lin.setVisibility(View.GONE);
            }
        });
        initData();
    }

    public void onJumpWeb(View view) {
        startJumpActivity(JumpWebViewActivity.class, "企业认证审核标准", "11");

    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {
        Intent intent = new Intent(enterpriseActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(enterpriseActivity.this);
        normalDialog.setTitle("下载模版");
        normalDialog.setMessage("下载公函,打印后填写相关资料并加盖公章。上传清晰无残缺加盖公章的公函扫描件。");
        normalDialog.setPositiveButton("下载模版",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("http://www.nanapal.com/data/upload/verifyTemplate/template.docx");//此处填链接
                        intent.setData(content_url);
                        startActivity(intent);

                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    public String mSign(String userId, String mTime) {
        Map<String, String> map = new TreeMap<>();

        map.put("userId", userId);
        map.put("mTime", mTime);
        return SignUtils.signSort(map);

    }

    public void initData() {
        String userId = BaseApplication.getInstance().getCustomerId(enterpriseActivity.this);
        String mTime = System.currentTimeMillis() + "";
        RecruitDbHelper.getApplyCompany(userId, mTime, mSign(userId, mTime), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                if (( mResultDetailModel.mJsonData instanceof JSONObject)) {
                    try {
                        price_tv.setText(mResultDetailModel.mJsonData.get("money").toString()+"元/次");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }
}
