package com.android.nana.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;

/**
 * Created by THINK on 2017/7/11.
 */

public class MultipleStatusView extends FrameLayout {

    private View mChildView;
    private View mCustomView;
    private OnActionListener mOnActionListener;

    public MultipleStatusView(Context context) {
        super(context);
    }

    public MultipleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultipleStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalStateException("MultipleStatusVIew only host 1 elements");
        } else if (childCount == 1) {
            mChildView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    public void none() {
        custom(new View(getContext()));
    }

    public void loading() {
        custom(R.layout.layout_loading);
    }

    public void empty() {
        Resources res = getResources();
        custom(res.getDrawable(R.drawable.icon_new_empty), res.getString(R.string.multiple_status_view_tips_empty), false);
    }

    public void noComment(){
        Resources res = getResources();
        custom(null, res.getString(R.string.multiple_status_view_no_comment), false);
    }
    public void searchEmpty() {
        Resources res = getResources();
        custom(null, res.getString(R.string.multiple_status_view_tips_search), false);
    }
    public void empty(boolean isGroup) {//是否是群聊详情
        Resources res = getResources();
        custom(res.getDrawable(R.drawable.icon_new_empty), res.getString(R.string.multiple_status_view_tips_empty_group), false);
    }

    public void onMsg() {
        Resources res = getResources();
        custom(res.getDrawable(R.drawable.icon_no_msg), res.getString(R.string.multiple_status_view_tips_empty_msg), false);
    }


    public void noEmpty() {
        Resources res = getResources();
        noCustom(res.getDrawable(R.drawable.icon_new_empty), res.getString(R.string.multiple_status_view_tips_empty), "");
    }

    public void noRecruit() {//招聘
        recruit();
    }


    public void noNetwork() {
        Resources res = getResources();
        custom(res.getDrawable(R.drawable.icon_new_no_network),
                res.getString(R.string.multiple_status_view_tips_no_network), true);
    }

    public void error() {
        Resources res = getResources();
        custom(res.getDrawable(R.drawable.icon_new_empty), res.getString(R.string.multiple_status_view_tips_error), true);
    }

    public void noCustom(Drawable drawable, String tips, String recruit) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_hr_action, this, false);

        ImageView imageView = view.findViewById(R.id.image);
        if (imageView != null) {
            if (drawable == null) {
                imageView.setVisibility(GONE);
            } else {
                imageView.setVisibility(VISIBLE);
                imageView.setImageDrawable(drawable);
            }
        }

        TextView textView = view.findViewById(R.id.txt_tips);
        if (textView != null) {
            if (TextUtils.isEmpty(tips)) {
                textView.setVisibility(GONE);
            } else {
                textView.setVisibility(VISIBLE);
                textView.setText(tips);
            }
        }

       /* TextView recruitView = view.findViewById(R.id.txt_recruit);
        if (recruitView != null) {
            if (TextUtils.isEmpty(recruit)) {
                recruitView.setVisibility(GONE);
            } else {
                recruitView.setVisibility(VISIBLE);
                recruitView.setText(recruit);
            }
        }
*/
        custom(view);
    }

    public void custom(Drawable drawable, String tips, boolean isBut) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_action, this, false);

        ImageView imageView = view.findViewById(R.id.image);
        if (imageView != null) {
            if (drawable == null) {
                imageView.setVisibility(GONE);
            } else {
                imageView.setVisibility(VISIBLE);
                imageView.setImageDrawable(drawable);
            }
        }

        TextView textView = (TextView) view.findViewById(R.id.txt_tips);
        if (textView != null) {
            if (TextUtils.isEmpty(tips)) {
                textView.setVisibility(GONE);
            } else {
                textView.setVisibility(VISIBLE);
                textView.setText(tips);
            }
        }

        Button button = (Button) view.findViewById(R.id.btn_action);
        if (button != null) {
            if (isBut) {
                button.setVisibility(VISIBLE);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnActionListener != null) {
                            mOnActionListener.onLoad(v);
                        }
                    }
                });
            } else {
                button.setVisibility(GONE);
            }
        }

        custom(view);
    }

    private void recruit() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_recruit, this, false);
        Button button =  view.findViewById(R.id.btn_recruit);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null) {
                    mOnActionListener.onLoad(v);
                }
            }
        });
        custom(view);
    }

    public void custom(int resId) {
        custom(LayoutInflater.from(getContext()).inflate(resId, this, false));
    }

    public void custom(View customView) {
        removeView(mCustomView);
        mCustomView = customView;
        if (mChildView != null) mChildView.setVisibility(GONE);
        if (mCustomView != null) mCustomView.setVisibility(VISIBLE);
        addView(mCustomView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void dismiss() {
        removeView(mCustomView);
        if (mChildView != null) mChildView.setVisibility(VISIBLE);
    }

    public interface OnActionListener {

        void onLoad(View view);
    }


    public void setOnLoadListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }
}
