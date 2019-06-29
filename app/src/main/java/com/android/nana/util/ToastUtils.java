package com.android.nana.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.nana.WebCastApplication;

/**
 * Created by THINK on 2017/6/17.
 */

public class ToastUtils {


    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;

    private static Toast mToast;

    private ToastUtils() {

    }

    // 整个app内toast单例，防止出现连续弹出toast导致toast显示时间加起来很长
    public static Toast getToast() {
        if (mToast == null) {
            mToast = new Toast(WebCastApplication.getInstance());
        }
        return mToast;
    }

    public static void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public static void showToast(String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(WebCastApplication.getInstance(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void showToast(int textId) {
        showToast(textId, Toast.LENGTH_SHORT);
    }

    public static void showToast(int textId, int duration) {
        String text = WebCastApplication.getInstance().getString(textId);
        showToast(text, duration);
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public static void ToatBytTime(Context c, String info, int time) {
        final Toast toast = Toast.makeText(c, info, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                toast.cancel();
            }
        }, time);
    }
}
