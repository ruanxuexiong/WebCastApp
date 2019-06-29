package com.android.nana.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.common.base.BaseWebViewActivity;
import com.android.nana.R;

public class WebViewActivity extends BaseWebViewActivity {

    private TextView mShareTv;
    private String thisUid, mShareTitle, mShareContent, mSharePic, mShareUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisUid = getIntent().getStringExtra("thisUserId");
        mShareTitle = getIntent().getStringExtra("share_title");
        mShareContent = getIntent().getStringExtra("share_content");
        mSharePic = getIntent().getStringExtra("share_pic");
        mShareUrl = getIntent().getStringExtra("share_url");


        mShareTv = (TextView) findViewById(R.id.base_webview_txt_right_text);
        mShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                WebFragment dialog = WebFragment.newInstance(thisUid,mShareTitle,mShareContent,mSharePic,mShareUrl);
                dialog.show(fm, "fragment_bottom_dialog");
            }
        });
    }

    @Override
    public String getErrorDescription() {
        return "<html><body>" +
                "<div style=' background: #EC641A;color: white;height:49px;width: 100%;position:fixed;top:0px;left: 0px;text-align: center;'>" +
                "    <div style='padding:13px;font-size:18px;'>网上村庄</div></div>" +
                "<h4 style='margin-top:50px;font-weight:500;'>当前网络不可用！请检查网络后点击<a href='" + mUrl + "'>" +
                "刷新</a>页面！</h4></body></html>";
    }

    @Override
    public void setCookier(String url) {


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onShouldOverrideUrlLoading(WebView view, String url) {

        if (url.contains("tel:")) { // 打电话
            String mobile = url.substring(url.lastIndexOf("/") + 1);
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile)));
            return true;
        } else if (url.contains("TenPay/")) { // 立即支付

            return true;
        }

        return false;
    }

}
