package com.android.nana.find.web;

import android.os.Bundle;
import android.widget.TextView;

import com.android.nana.R;
import com.fanneng.android.web.progress.BaseIndicatorView;


/**
 * Created by lenovo on 2019/1/24.
 */

public class SlidingFragment extends SuperWebX5Fragment  {

    private WebLayout webLayout;


    public static SlidingFragment getInstance(Bundle bundle) {

        SlidingFragment customWebFragment = new SlidingFragment();
        if (customWebFragment != null) {
            customWebFragment.setArguments(bundle);
        }

        return customWebFragment;
    }

    /**
     * 自定义进度条
     *
     * @return
     */
    @Override
    protected BaseIndicatorView getIndicatorView() {
        return new CoolIndicatorLayout(this.getActivity());
    }


  /**
     * 自定义回弹
     *
     * @return
     */
    @Override
    public WebLayout getWebLayout() {
        webLayout = new WebLayout(getActivity());
        SlidingLayout slidingLayout = (SlidingLayout) webLayout.getLayout();
        TextView tv = slidingLayout.getBackgroundView().findViewById(R.id.tv_sliding);
        tv.setText("网页由 " + getUrl() + " 提供");
        return webLayout;
    }
}
