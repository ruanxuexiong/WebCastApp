package com.android.nana.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.nana.R;

/**
 * Created by THINK on 2017/7/18.
 */

public class CountdownButton extends StateButton {

    private long length = 60 * 1000;

    private String beforeText = "获取验证码";

    private String afterText = "秒后重新获取";

    private CountDownTimer countDownTimer = new CountDownTimer(length, 1000) {
        // 第一个参数是总的倒计时时间
        // 第二个参数是每隔多少时间(ms)调用一次onTick()方法
        public void onTick(long millisUntilFinished) {
            Drawable drawable = getResources().getDrawable(R.drawable.retrieval_code_welcome);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            CountdownButton.this.setTextColor(getResources().getColor(R.color.grey_dd));
            CountdownButton.this.setBackgroundDrawable(drawable);
            CountdownButton.this.setPadding(10, 0, 10, 0);
            CountdownButton.this.setText(millisUntilFinished / 1000 + afterText);
            CountdownButton.this.setEnabled(false);
        }

        public void onFinish() {
            Drawable drawable = getResources().getDrawable(R.drawable.login_welcome_selector);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            CountdownButton.this.setTextColor(getResources().getColor(R.color.right));
            CountdownButton.this.setBackgroundDrawable(drawable);
            CountdownButton.this.setPadding(10, 0, 10, 0);
            CountdownButton.this.setText(beforeText);
            CountdownButton.this.setEnabled(true);
        }
    };

    public CountdownButton(Context context) {
        super(context);
        initView();
    }

    public CountdownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CountdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        if (!TextUtils.isEmpty(getText())) {
            beforeText = getText().toString().trim();
        }
        this.setText(beforeText);
    }


    public void setLength(long length) {
        this.length = length;
    }

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    /**
     * 设置未点击后显示的文字
     *
     * @param beforeText
     */
    public void setAfterText(String beforeText) {
        this.afterText = afterText;
    }

    public void start() {
        countDownTimer.start();
    }

    private void clearTimer() {
        countDownTimer.cancel();
    }

    /**
     * 记得一定要在activity或者fragment消亡的时候清除倒计时，
     * 因为如果倒计时没有完的话子线程还在跑，
     * 这样的话就会引起内存溢出
     */
    @Override
    protected void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
    }
}
