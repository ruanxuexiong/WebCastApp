package com.android.nana.widget;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.nana.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by THINK on 2017/6/23.
 */

public class ImageDetailFragment extends Fragment {

    private String mImageUrl;
    private ImageView mImageView;
    private ImageView image_small;
    private SubsamplingScaleImageView longImg;
    private View all_view;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private String isLongImg;

    public static ImageDetailFragment newInstance(String imageUrl, String isLongImg) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        args.putString("isLongImg", isLongImg);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = v.findViewById(R.id.image);
        all_view = v.findViewById(R.id.all_view);
        longImg = v.findViewById(R.id.longImg);
        all_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getActivity().finish();
            }
        });
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

            @Override
            public void onViewTap(View view, float x, float y) {
                // TODO Auto-generated method stub
                getActivity().finish();
            }
        });
        progressBar = v.findViewById(R.id.loading);
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        String url = getArguments().getString("url");
        isLongImg = getArguments().getString("isLongImg");
        if (url == null) return;
        if (!url.startsWith("http")) {
            url = "file:/" + url;
        }

        if (isLongImg.equals("111")) {
            longImg.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .download(url)
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);

                        }

                        @Override
                        public void onResourceReady(File resource, Transition<? super File> transition) {

                            progressBar.setVisibility(View.GONE);
                            all_view.setVisibility(View.GONE);
                            mImageView.setVisibility(View.GONE);
                            longImg.setQuickScaleEnabled(true);
                            longImg.setZoomEnabled(true);
                            longImg.setPanEnabled(true);
                            longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                            longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                            longImg.setImage(ImageSource.uri(resource.getAbsolutePath()),new ImageViewState(0, new PointF(0, 0), 0));
                        }
                    });
        } else {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
            String webUrl;
            if(url.contains("_400_auto")){
                webUrl = url.substring(0, url.indexOf("_400_auto"))+".jpg";//获取高清图
            }else
                webUrl=url;
            imageLoader.displayImage(webUrl, mImageView, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {
                    // TODO Auto-generated method stub
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason failReason) {
                    // TODO Auto-generated method stub
                    String message = "加载失败，请稍后重试";
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                    // TODO Auto-generated method stub
                    progressBar.setVisibility(View.GONE);
                    all_view.setVisibility(View.GONE);
                    mAttacher.update();
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }
}
