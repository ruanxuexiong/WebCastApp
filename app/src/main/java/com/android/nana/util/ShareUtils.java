package com.android.nana.util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

/**
 * Created by THINK on 2017/6/17.
 */

public class ShareUtils {

    private static UMImage image;
    private static MyUMShareListener myUMShareListener;
    private static ShareAction shareAction;


    /**
     * 分享数据
     *
     * @param title   标题
     * @param content 内容
     * @param imgUrl  图片地址，不传就直接用默认图片
     * @param url     点击之后的链接地址
     */
    public static void shareAll(Activity activity, final String title, final String content, String imgUrl, final String url) {
        image = new UMImage(activity, imgUrl);
        new ShareAction(activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .withText(content)
                .withTitle(title)
                .withMedia(image)
                .withTargetUrl(url)
                .setCallback(new MyUMShareListener())
                .open();
    }

    /**
     * 单个平台分享
     *
     * @param activity    分享页面Activity
     * @param share_media 分享的平台
     * @param title       标题
     * @param content     内容
     * @param imgUrl      图片url
     * @param url         点击之后的链接url
     */
    public static void shareSingle(Activity activity, SHARE_MEDIA share_media, final String title, final String content, String imgUrl, final String url) {
        initConfig(activity, title, content, imgUrl, url);
        shareAction.setPlatform(share_media).share();
    }


    public static void shareImage(Activity activity, SHARE_MEDIA share_media, final File file) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("正在加载...");
        Config.dialog = dialog;
        image = new UMImage(activity, file);
        shareAction = new ShareAction(activity);
        shareAction.withFile(file).withMedia(image).setCallback(new MyUMShareListener());
        shareAction.setPlatform(share_media).share();
    }


    private static void initConfig(Activity activity, final String title, final String content, String imgUrl, final String url) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("正在加载...");
        Config.dialog = dialog;
        image = new UMImage(activity, imgUrl);
        shareAction = new ShareAction(activity);
        shareAction.withText(content).withTitle(title).withMedia(image).withTargetUrl(url).setCallback(new MyUMShareListener());
    }


    static class MyUMShareListener implements UMShareListener {

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (share_media.name().equals("WEIXIN_FAVORITE")) {
            } else {
                ToastUtils.showToast("分享成功");
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            if (share_media.name().equals("WEIXIN")) {
                ToastUtils.showToast("分享失败");
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtils.showToast("分享取消");
        }
    }

}
