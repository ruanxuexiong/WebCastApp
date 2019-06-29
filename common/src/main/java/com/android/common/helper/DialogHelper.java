package com.android.common.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.models.InputFilterMinMax;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class DialogHelper {

    public static void customAlert(Context context, String title, String message,
                                   final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();

        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.alert_dialog_layout);


        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);
        mAlertDialog.getWindow().findViewById(R.id.dialog_et).setVisibility(View.GONE);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }

    public static void customAlert(Context context, String title, String message, String okStr, String cancelStr,
                                   final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAlertDialog.getWindow().setContentView(R.layout.alert_dialog_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);
        final EditText mDialogEt = (EditText) mAlertDialog.getWindow().findViewById(R.id.dialog_et);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick(mDialogEt.getText().toString().trim());
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }


    public static void customPriceAlert(Context context, String title, String message, String okStr, String cancelStr,
                                        final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAlertDialog.getWindow().setContentView(R.layout.alert_dialog_price_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);
        final EditText mDialogEt = (EditText) mAlertDialog.getWindow().findViewById(R.id.dialog_et);

        mDialogEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick(mDialogEt.getText().toString().trim());
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }

    public static void pwdAlert(Context context, String title, String message, String okStr, String cancelStr,
                                final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAlertDialog.getWindow().setContentView(R.layout.alert_pwd_dialog_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);
        final EditText mDialogEt = (EditText) mAlertDialog.getWindow().findViewById(R.id.dialog_et);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick(mDialogEt.getText().toString().trim());
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }

    public static void costAlert(Context context, String title, String message, String okStr, String cancelStr,
                                 final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAlertDialog.getWindow().setContentView(R.layout.alert_cost_dialog_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);
        final EditText mDialogEt = (EditText) mAlertDialog.getWindow().findViewById(R.id.dialog_et);
        mDialogEt.setFilters(new InputFilter[]{new InputFilterMinMax(0.01, 100000)});

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick(mDialogEt.getText().toString().trim());
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }

    public static void agreeAlert(Context context, String title, String message, String okStr, String cancelStr,
                                  final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {

        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.alert_dialog_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);
        final EditText mDialogEt = (EditText) mAlertDialog.getWindow().findViewById(R.id.dialog_et);
        mDialogEt.setVisibility(View.GONE);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });
    }


    public static void loginAlert(Context context, String okStr, String cancelStr,
                                  final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {

        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.login_dialog_layout);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok)).setText(okStr);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setText(cancelStr);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });
    }

    public static void customAlert(Context context, String title, String message, final OnAlertConfirmClick okListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.alert_dialog_layout);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);


        (mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setVisibility(View.GONE);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });

    }

    public static void callAlert(Context context, final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {

        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.alert_call_dialog_layout);

        TextView mMsgTv = (TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message);
        mMsgTv.setText("提示");
        mMsgTv.setTextSize(14);

        ((TextView) mAlertDialog.getWindow().findViewById(R.id.tv_cost)).setText("加好友需要双方成功约见一次后才能发起申请");
        (mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel)).setVisibility(View.GONE);
        TextView mOkTv = (TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok);
        mOkTv.setTextColor(context.getResources().getColor(R.color.blue));
        mOkTv.setText("知道了");

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });
  /*      mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });*/
    }

    /**
     * 身份认证
     */

    public static void customMeAlert(Context context, String title, String message,
                                     final OnAlertConfirmClick okListener, final OnAlertConfirmClick cancelListener) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();

        mAlertDialog.show();

        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setContentView(R.layout.alert_me_dialog_layout);


        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_title)).setText(title);
        ((TextView) mAlertDialog.getWindow().findViewById(R.id.alert_txt_message)).setText(message);
        mAlertDialog.getWindow().findViewById(R.id.dialog_et).setVisibility(View.GONE);

        mAlertDialog.getWindow().findViewById(R.id.alert_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (okListener != null) {
                    mAlertDialog.dismiss();
                    okListener.OnClick();
                }
            }
        });
        mAlertDialog.getWindow().findViewById(R.id.alert_txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mAlertDialog.dismiss();
                if (cancelListener != null) {
                    cancelListener.OnClick();
                }
            }
        });

    }


    public interface OnAlertConfirmClick {

        void OnClick(String content);

        void OnClick();

    }
}
