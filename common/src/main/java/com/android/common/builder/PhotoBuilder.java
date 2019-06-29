package com.android.common.builder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class PhotoBuilder {
	
	public final static String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WebCast/";
	
	public final static int DEAL_PHOTO = 10; // 处理
	public final static int ALBUM_PHOTO = 20; // 本地相册
	public final static int CAMERA_PHOTO = 30; // 拍照
	
	private String mPhotoName;
	
	private Activity mContext;
	private Uri mPhotoUri;
	
	private int mAspectX = 1;
	private int mAspectY = 1;
	private int mOutputX = 600;
	private int mOutputY = 600;
	
	/**
	 * 设置裁剪图片比例
	 * @param aspectX
	 * @param aspectY
	 */
	public void setAspectXY(int aspectX, int aspectY) {
		mAspectX = aspectX;
		mAspectY = aspectY;
	}
	
	/**
	 * 设置裁剪过后图片大小
	 * @param outputX
	 * @param outputY
	 */
	public void setOutputXY(int outputX, int outputY) {
		mOutputX = outputX;
		mOutputY = outputY;
	}
	
	public PhotoBuilder(Activity context) {
		mContext = context;

		initPhotoUri();
	}

	private void initPhotoUri(){

		mPhotoName = getPhotoFileName();

		File picFile = getFileByName();

		if (!picFile.exists()){
			try {
				picFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mPhotoUri = Uri.fromFile(picFile);

	}

	/**
	 * 本地相册
	 */
	public void doLocalPhoto(){
		
		Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
		albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		mContext.startActivityForResult(albumIntent, ALBUM_PHOTO);
		
	}
	
	/**
	 * 拍照
	 */
	public void doCameraPhoto() {
		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
		mContext.startActivityForResult(cameraIntent, CAMERA_PHOTO);
		
	}
	
	/**
	 * 启动裁剪
	 */
	public void cropImageUriByTakePhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (uri == null) {
			intent.setDataAndType(mPhotoUri, "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", mAspectX);
		intent.putExtra("aspectY", mAspectY);
		intent.putExtra("outputX", mOutputX);
		intent.putExtra("outputY", mOutputY);
		intent.putExtra("noFaceDetection", true); // no face detection
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		mContext.startActivityForResult(intent, DEAL_PHOTO);
	}
	
	/**
	 * 使用系统当前日期加以调整作为照片的名称
	 * @return
	 */
    @SuppressLint("SimpleDateFormat")
	public String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss"+new Random().nextInt(100));
        return dateFormat.format(date) + ".jpeg";
    }
	
	public File getFileByName() {
		// 保存裁剪后的图片文件
		File pictureFileDir = new File(getFileAbsolutePath());
		
		if (!pictureFileDir.exists()) pictureFileDir.mkdir();
		
		File picFile = new File(pictureFileDir, mPhotoName);
		return picFile;
	}
	
	public File getFileByName(String fileName) {
		
		File pictureFileDir = new File(getFileAbsolutePath());
		
		if (!pictureFileDir.exists()) pictureFileDir.mkdir();
		
		File picFile = new File(pictureFileDir, fileName);
		return picFile;
	}
	
	/**
	 * 文件全路径
	 * @return
	 */
	public String getFileAbsolutePath() {
		
		File f = new File(file_path);
		if (!f.exists()) f.mkdirs();
		
		File file = new File(file_path+"/image/");
		if (!file.exists()) file.mkdirs();
		
		return file_path+"/image/";
	}
	
}
