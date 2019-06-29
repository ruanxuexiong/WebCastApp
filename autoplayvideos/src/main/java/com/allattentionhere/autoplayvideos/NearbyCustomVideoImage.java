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

public class NearbyCustomVideoImage extends FrameLayout {

    private NearbyCustomVideoView cvv;
    private ImageView iv;

    public NearbyCustomVideoImage(Context context) {
        super(context);
        init();
    }

    public NearbyCustomVideoImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NearbyCustomVideoImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NearbyCustomVideoImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public NearbyCustomVideoView getCustomVideoView() {
        return cvv;
    }

    public ImageView getImageView() {
        return iv;
    }


    private void init() {
        this.setTag("nearby_aah_vi");
        cvv = new NearbyCustomVideoView(getContext());
        iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.addView(cvv);
        this.addView(iv);
    }
}
