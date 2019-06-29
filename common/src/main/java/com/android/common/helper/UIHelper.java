package com.android.common.helper;

import android.content.Context;
import android.widget.Toast;

import com.android.common.R;
import com.android.common.ui.OnLoadingDialog;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class UIHelper {

    public static void showToast(Context context, String message){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Toast对象
    private static Toast mToast = null;

    public static void showToast(Context context, String message, int duration){
        if (mToast == null) {
            mToast = Toast.makeText(context, message, duration);
        }
        mToast.setText(message);
        mToast.show();
    }

    private static OnLoadingDialog mOnLoadingDialog;

    public static void showOnLoadingDialog(Context context) {
        if (mOnLoadingDialog == null) {
            mOnLoadingDialog = new OnLoadingDialog(context, R.style.myDialog);
        } else {
            mOnLoadingDialog.show();
        }
    }

    public static void showOnLoadingDialog(Context context,String text) {
        if (mOnLoadingDialog == null) {
            mOnLoadingDialog = new OnLoadingDialog(context, text, R.style.myDialog);
        } else {
            mOnLoadingDialog.show();
        }
    }

    public static void hideOnLoadingDialog() {
        if (mOnLoadingDialog != null) {
            mOnLoadingDialog.dismiss();
            mOnLoadingDialog = null;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
