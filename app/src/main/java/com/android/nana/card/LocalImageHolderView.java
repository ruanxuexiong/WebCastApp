package com.android.nana.card;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.android.nana.util.ImgLoaderManager;
import com.bigkoo.convenientbanner.holder.Holder;

/**
 * Created by lenovo on 2018/1/16.
 */

public class LocalImageHolderView implements Holder<String> {

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        ImgLoaderManager.getInstance().showImageView(data, imageView);
    }

}
