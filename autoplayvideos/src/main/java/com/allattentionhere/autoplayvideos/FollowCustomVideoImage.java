package com.allattentionhere.autoplayvideos;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by lenovo on 2018/12/21.
 */

public class FollowCustomVideoImage extends FrameLayout {


    private FollowCustomVideoView cvv;
    private ImageView iv;

    public FollowCustomVideoImage(Context context) {
        super(context);
        init();
    }

    public FollowCustomVideoImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowCustomVideoImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FollowCustomVideoImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public FollowCustomVideoView getCustomVideoView() {
        return cvv;
    }

    public ImageView getImageView() {
        return iv;
    }


    private void init() {
        this.setTag("follow_aah_vi");
        cvv = new FollowCustomVideoView(getContext());
        iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.addView(cvv);
        this.addView(iv);
    }
}
