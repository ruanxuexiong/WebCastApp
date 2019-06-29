package com.android.nana.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.rong.common.FileUtils;
import io.rong.imlib.NativeClient;
import io.rong.imlib.model.Message;
import io.rong.message.MessageHandler;
import io.rong.push.common.RLog;

/**
 * Created by lenovo on 2018/11/26.
 */

public class VideoMessageHandler extends MessageHandler<VideoMessage> {
    private final static String TAG = "VideoMessageHandler";

    private static int THUMB_COMPRESSED_WIDTH_SIZE = 158;
    private static int THUMB_COMPRESSED_HEIGHT_SIZE = 280;
    private static int THUMB_COMPRESSED_QUALITY = 30;
    private final static String VIDEO_THUMBNAIL_PATH = "/video/thumbnail/";

    public VideoMessageHandler(Context context) {
        super(context);
    }

    @Override
    public void decodeMessage(Message message, VideoMessage model) {
        Uri uri = obtainImageUri(getContext());
        String name = message.getMessageId() + ".jpg";
        if (message.getMessageId() == 0) {
            name = message.getSentTime() + ".jpg";
        }

        String thumb = uri.toString() + VIDEO_THUMBNAIL_PATH;

        File thumbFile = new File(thumb + name);

        if (!TextUtils.isEmpty(model.getBase64()) && !thumbFile.exists()) {
            byte[] data = null;
            try {
                data = Base64.decode(model.getBase64(), Base64.NO_WRAP);
            } catch (IllegalArgumentException e) {
                RLog.e(TAG, "afterDecodeMessage Not Base64 Content!");
                e.printStackTrace();
            }

            if (!isImageFile(data)) {
                RLog.e(TAG, "afterDecodeMessage Not Image File!");
                return;
            }
            FileUtils.byte2File(data, thumb, name);
        }
        model.setThumbUri(Uri.parse("file://" + thumb + name));

        model.setBase64(null);
    }

    @Override
    public void encodeMessage(Message message) {
        VideoMessage model = (VideoMessage) message.getContent();
        Uri uri = obtainImageUri(getContext());
        String name = message.getMessageId() + ".jpg";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] data;
        if (model.getThumbUri() != null
                && model.getThumbUri().getScheme() != null
                && model.getThumbUri().getScheme().equals("file")) {

            File file = new File(uri.toString() + VIDEO_THUMBNAIL_PATH + name);
            if (file.exists()) {
                model.setThumbUri(Uri.parse("file://" + uri.toString() + VIDEO_THUMBNAIL_PATH + name));
                data = FileUtils.file2byte(file);
                if (data != null) {
                    model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
                }
                return;
            } else {
                String thumbPath = model.getThumbUri().toString().substring(5);
                File src = new File(thumbPath);
                data = FileUtils.file2byte(src);
                if (data != null) {
                    model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
                    String path = uri.toString() + VIDEO_THUMBNAIL_PATH;
                    if ((FileUtils.copyFile(src, path, name)) != null) {
                        model.setThumbUri(Uri.parse("file://" + path + name));
                        return;
                    }
                }
            }
        }
        try {
            String videoPath = model.getLocalPath().toString().substring(5);
            RLog.d(TAG, "beforeEncodeMessage Thumbnail not save yet! " + videoPath);
            Bitmap bitmap = createVideoThumbnail(videoPath, THUMB_COMPRESSED_WIDTH_SIZE, THUMB_COMPRESSED_HEIGHT_SIZE);
            if (bitmap != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, THUMB_COMPRESSED_QUALITY, outputStream);
                data = outputStream.toByteArray();
                model.setBase64(Base64.encodeToString(data, Base64.NO_WRAP));
                outputStream.close();
                FileUtils.byte2File(data, uri.toString() + VIDEO_THUMBNAIL_PATH, name);
                model.setThumbUri(Uri.parse("file://" + uri.toString() + VIDEO_THUMBNAIL_PATH + name));
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            RLog.e(TAG, "beforeEncodeMessage Not Base64 Content!");
        } catch (IOException e) {
            e.printStackTrace();
            RLog.e(TAG, "beforeEncodeMessage IOException");
        }
    }

    private static Uri obtainImageUri(Context context) {
        File file = context.getFilesDir();
        String path = file.getAbsolutePath();
        String userId = NativeClient.getInstance().getCurrentUserId();
        return Uri.parse(path + File.separator + userId);
    }

    private static boolean isImageFile(byte[] data) {
        if (data == null || data.length == 0) {
            return false;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return options.outWidth != -1;
    }

    /**
     * 获取视频的缩略图
     * 提供了一个统一的接口用于从一个输入媒体文件中取得帧和元数据。
     *
     * @param path   视频的路径
     * @param width  缩略图的宽
     * @param height 缩略图的高
     * @return 缩略图
     */
    public Bitmap createVideoThumbnail(String path, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            retriever.setDataSource(path);
            bitmap = retriever.getFrameAtTime(-1); //取得指定时间的Bitmap，即可以实现抓图（缩略图）功能
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) {
            return null;
        }
        String thumbPath = path.replace("mp4", "jpg");
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        //saveBitmap(bitmap,thumbPath);
        return bitmap;
    }

    public void saveBitmap(Bitmap bitmap, String thumbPath) {
        File f = new File(thumbPath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
