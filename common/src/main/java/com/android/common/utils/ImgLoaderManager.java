package com.android.common.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.common.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImgLoaderManager {

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	private static ImgLoaderManager mImgLoaderManager;

	public static ImgLoaderManager getInstance(){

		return mImgLoaderManager == null?mImgLoaderManager = new ImgLoaderManager():mImgLoaderManager;

	}

	public static ImgLoaderManager getInstance(int drawableId){

		return mImgLoaderManager == null?mImgLoaderManager = new ImgLoaderManager(drawableId):mImgLoaderManager;

	}

	public ImgLoaderManager() {
		// animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.icon_default)
		.showImageForEmptyUri(R.drawable.icon_default)
		.showImageOnFail(R.drawable.icon_default).cacheInMemory(true)
		.bitmapConfig(Bitmap.Config.RGB_565)//设置为RGB565比起默认的ARGB_8888要节省大量的内存
		.delayBeforeLoading(100)
		.cacheOnDisc(true).considerExifParams(true).displayer(new SimpleBitmapDisplayer())
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}
	
	public ImgLoaderManager(int drawableId) {
		animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(drawableId)
		.showImageForEmptyUri(drawableId)
		.showImageOnFail(drawableId).cacheInMemory(true)
		.bitmapConfig(Bitmap.Config.RGB_565)//设置为RGB565比起默认的ARGB_8888要节省大量的内存
		.delayBeforeLoading(100)
		.cacheOnDisc(true).considerExifParams(true).displayer(new SimpleBitmapDisplayer())
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}
	
	public void showImageView(String url, ImageView image) {
		imageLoader.displayImage(url, image, options);
	}

	public void showImageView(String url, ImageView image, ImageLoadingListener imageLoadingListener) {
		imageLoader.displayImage(url, image, options, imageLoadingListener);
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}