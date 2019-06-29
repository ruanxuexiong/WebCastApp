package com.android.nana.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Handler;

public class TransformationUtils extends ImageViewTarget<Bitmap> {
    Bitmap bt;
    private ImageView target;

    public TransformationUtils(ImageView target) {
        super(target);
        this.target = target;
    }

    @Override
    protected void setResource(Bitmap resource) {
      //  view.setImageBitmap(resource);
        new Thread(){
            @Override
            public void run() {
                super.run();
               bt=   getBitmap("http://qiniu.nanapal.com/HBFMian_img_20190413103026432img.png");
                //获取原图的宽高


               handler.sendEmptyMessage(123);
            }
        }.start();


    }
    public Bitmap getBitmap(String url) {


        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==123){
                int width = bt.getWidth();
                int height = bt.getHeight();

                //获取imageView的宽
                int imageViewWidth = target.getWidth();

                //计算缩放比例
                float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

                //计算图片等比例放大后的高
                int imageViewHeight = (int) (height * sy);
                ViewGroup.LayoutParams params = target.getLayoutParams();
                params.height = imageViewHeight;
                target.setLayoutParams(params);
            }
        }
    };
}