package com.android.nana.identity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.friend.VideoDynamicActivity;

public class identity_homeActivity extends AppCompatActivity {
    LinearLayout qiye_next;
    TextView tv_title;
    AppCompatTextView mBackTv;
    LinearLayout id_next;
    TextView id_video_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_home);
        tv_title=findViewById(R.id.tv_title);
        tv_title.setText("身份认证");
        id_next=findViewById(R.id.id_next);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        qiye_next=findViewById(R.id.qiye_next);
        id_video_next=findViewById(R.id.id_video_next);
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        id_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //立即申请
                Intent intent=new Intent(identity_homeActivity.this,user_idActivity.class);
                startActivity(intent);
            }
        });
        qiye_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //企业
                Intent intent=new Intent(identity_homeActivity.this,enterpriseActivity.class);
                startActivity(intent);
            }
        });
        id_video_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转发布页
                Intent intent=new Intent(identity_homeActivity.this,VideoDynamicActivity.class);
                startActivity(intent);
            }
        });
    }

}
