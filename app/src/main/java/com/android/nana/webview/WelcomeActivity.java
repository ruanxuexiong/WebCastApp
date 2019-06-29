package com.android.nana.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.common.BaseApplication;
import com.android.nana.R;
import com.android.nana.main.MainActivity;

public class WelcomeActivity extends Activity implements Runnable {
    private ImageView start_login;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.welcome);
        start_login=findViewById(R.id.start_login);
        anim();
        new Thread(this).start();
    }

    public void run() {
        try {

           // Thread.sleep(2000);
            Thread.sleep(3000);
            if (!BaseApplication.getInstance().checkLogin(WelcomeActivity.this)) {
                startActivity(new Intent(WelcomeActivity.this, com.android.nana.auth.WelcomeActivity.class));
                WelcomeActivity.this.finish();
                return;
            } else {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                WelcomeActivity.this.finish();
            }


        } catch (InterruptedException e) {

        }
    }

    private void anim(){

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);

                    handler.sendEmptyMessage(123);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        if (msg.what==123){
            Animation rotate = new ScaleAnimation(0f,20f, 0f, 20f, Animation.RELATIVE_TO_SELF,  0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            LinearInterpolator lin = new LinearInterpolator();
            rotate.setInterpolator(lin); //设置插值器
            rotate.setDuration(800);//设置动画持续周期
            //  rotate.setRepeatCount(1);//设置重复次数
            rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
            start_login.startAnimation(rotate);
        }
        }
    };
}
