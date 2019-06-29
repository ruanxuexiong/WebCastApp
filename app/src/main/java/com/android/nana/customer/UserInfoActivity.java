package com.android.nana.customer;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.ui.CustomWindowDialog;
import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.PurposeEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.main.MainActivity;
import com.android.nana.material.JsonBean;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.model.LoginModel;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.PhotoRotateUtil;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JsonFileReader;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.nana.util.PhotoRotateUtil.rotateBitmapByDegree;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtRight;

    private RelativeLayout mRlTop;
    private RoundImageView mIvHeader;
    private ImageView mIvBackground;
    private ImageView mIvHeaderPhotograph, mIvBackgroundPhotograph;
    private EditText mEtNickName, mEtUserName, mEtTitle, mEtIntroduce, mEditOther;
    private TextView mTxtGender, mTxtAddress, mTxtAge, mTxtPurpose;
    private LinearLayout mLlOther;

    private CustomWindowDialog mCwdPurpose; // 注册目的

    private File mHeadImage = null;
    private File mBackgroundImage = null;

    private List<EditText> mAllEdit = new ArrayList<EditText>();
    private PurposeEntity mPurposeEntity; // 注册目的

    //拍照后相片的Uri
    private Uri imageUri;
    //拍照后相片path
    private String imagePath;

    private Uri backgImageUri;
    private String backgImagePath;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int APPLY_PERMISSION = 3;

    private static final int BACKGROUND_PHOTO = 4;
    private static final int BACKGROUND_CHOOSE_PHOTO = 5;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private ArrayList<String> mSex = new ArrayList<>();
    private OptionsPickerView mPickerView;

    private ArrayList<String> mAge = new ArrayList<>();
    private OptionsPickerView mPickerAgeView;

    private ArrayList<PurposeEntity> mPurposeArray = new ArrayList<>();
    private OptionsPickerView mPurposeView;
    private String purposeId;

    @Override
    protected void bindViews() {
        mCwdPurpose = new CustomWindowDialog(this);
        setContentView(R.layout.user_info);
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("个人简介");
        mTxtRight = (TextView) findViewById(R.id.common_txt_right_text);
        mTxtRight.setText("保存");

        mRlTop = (RelativeLayout) findViewById(R.id.user_info_rl_top);
        mIvHeader = (RoundImageView) findViewById(R.id.user_info_iv_header_picture);
        mIvHeaderPhotograph = (ImageView) findViewById(R.id.user_info_iv_header_picture2);
        mIvBackground = (ImageView) findViewById(R.id.user_info_iv_back_picture);
        mIvBackgroundPhotograph = (ImageView) findViewById(R.id.user_info_iv_back_picture2);

        // mEtNickName = (EditText) findViewById(R.id.user_info_edit_nickname);
        mEtUserName = (EditText) findViewById(R.id.user_info_edit_username);
        //   mEtTitle = (EditText) findViewById(R.id.user_info_edit_title);
        mEtIntroduce = (EditText) findViewById(R.id.user_info_edit_introduce);
        mTxtAddress = (TextView) findViewById(R.id.user_info_txt_address);
        mTxtAge = (TextView) findViewById(R.id.user_info_txt_age);
        //  mTxtPurpose = (TextView) findViewById(R.id.user_info_txt_purpose);
        mTxtGender = (TextView) findViewById(R.id.user_info_txt_gender);

        mLlOther = (LinearLayout) findViewById(R.id.user_info_ll_other);
        mEditOther = (EditText) findViewById(R.id.user_info_txt_other);

        mTxtAddress.setOnClickListener(this);
        String successJson = JsonFileReader.getJson(this, "province_data.json");
        parseJson(successJson);

        initData();
        openmPickerView();

        initAge();
        openmPickerAgeView();

      //  initPurpose();
        //   openPurposeView();
    }

 /*   private void initPurpose() {

        DBModel dbModel = DBModel.get("queryPurpose");
        if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {
            JSONObject object = JSONUtil.getStringToJson(dbModel.Description);
            JSONArray array = JSONUtil.getArray(object, "data");
            for (int i = 0; i < array.length(); i++) {
                mPurposeArray.add(new PurposeEntity(array.optJSONObject(i).optString("id"), array.optJSONObject(i).optString("name")));
            }
        }
    }*/


  /*  private void openPurposeView() {
        mPurposeView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                purposeId = mPurposeArray.get(options2).getId();
                mTxtPurpose.setText(mPurposeArray.get(options2).getName());
            }
        }).build();
        mPurposeView.setNPicker(new ArrayList(), mPurposeArray, new ArrayList());
    }*/


    private void initAge() {
        for (int i = 0; i < 45; i++) {
            mAge.add((16 + i) + "");
        }
    }

    private void openmPickerAgeView() {

        mPickerAgeView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = mAge.get(options2);
                mTxtAge.setTextColor(getResources().getColor(R.color.black_32));
                mTxtAge.setText(str);
            }
        }).build();
        mPickerAgeView.setNPicker(new ArrayList(), mAge, new ArrayList());
    }

    private void openmPickerView() {//期望薪资
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = mSex.get(options2);
                mTxtGender.setTextColor(getResources().getColor(R.color.black_32));
                mTxtGender.setText(str);
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mSex, new ArrayList());
    }

    private void initData() {
        mSex.add("女");
        mSex.add("男");
        mSex.add("保密");
    }

    private void parseJson(String successJson) {

        ArrayList<JsonBean> jsonBean = parseData(successJson);
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            options1ItemName.add(jsonBean.get(i).getName());
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）

            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getLists().size(); c++) {//遍历该省份的所有城市

                String CityName = jsonBean.get(i).getLists().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                if (jsonBean.get(i).getLists().get(c).getLists() == null
                        || jsonBean.get(i).getLists().get(c).getLists().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getLists().get(c).getLists().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getLists().get(c).getLists().get(d).getName();

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        dismissProgressDialog();

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    protected void locationData() {
        super.locationData();

    }

    private void bOpenAlbum() {

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, BACKGROUND_CHOOSE_PHOTO);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void init() {

        UserInfo userInfo = (UserInfo) SharedPreferencesUtils.getObject(UserInfoActivity.this, "userInfo", UserInfo.class);
        if (userInfo != null) {
            mIvHeaderPhotograph.setVisibility(View.GONE);
            mIvBackgroundPhotograph.setVisibility(View.GONE);
            mRlTop.setBackgroundColor(getResources().getColor(R.color.white));
            new ImgLoaderManager(R.drawable.icon_default_header).showImageView(userInfo.getAvatar(), mIvHeader, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    if (TextUtils.isEmpty(s)) return;

                    mHeadImage = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            new ImgLoaderManager().showImageView(userInfo.getBackgroundImage(), mIvBackground, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    if (TextUtils.isEmpty(s)) return;

                    mBackgroundImage = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));

                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
            mEtUserName.setText(userInfo.getUsername());
            mTxtGender.setText(userInfo.getSex().equals("0") ? "保密" : userInfo.getSex().equals("1") ? "男" : "女");

            mTxtAge.setText(userInfo.getAge());
            mEtIntroduce.setText(userInfo.getIntroduce());


            if (!TextUtils.isEmpty(userInfo.getProvinceId()) && !TextUtils.isEmpty(userInfo.getCity()) && !TextUtils.isEmpty(userInfo.getDistrict())) {
                mTxtAddress.setText(userInfo.getProvince() + "-" + userInfo.getCity() + "-" + userInfo.getDistrict());
            }

            mProvinceId = Integer.parseInt(TextUtils.isEmpty(userInfo.getProvinceId()) ? "0" : userInfo.getProvinceId());
            mCityId = Integer.parseInt(TextUtils.isEmpty(userInfo.getCityId()) ? "0" : userInfo.getCityId());
            mCountyId = Integer.parseInt(TextUtils.isEmpty(userInfo.getDistrictId()) ? "0" : userInfo.getDistrictId());
        }

        mAllEdit.add(mEtUserName);
        mAllEdit.add(mEtIntroduce);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfoActivity.this.finish();

            }
        });

        mTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEtUserName.getText().toString().trim().equals("")) {
                    ToastUtils.showToast("真实姓名不能为空");
                } else {
                    save();
                }
            }
        });

        // 头像
        mIvHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();

                List<MenuItem> menuItemList = new ArrayList<MenuItem>();
                MenuItem menuItem1 = new MenuItem();
                menuItem1.setText("拍照");
                menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
                MenuItem menuItem2 = new MenuItem();
                menuItem2.setText("相册");
                menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);
                menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {
                        imagePath = getExternalCacheDir() + "/output_image.jpg";
                        File outputImage = new File(imagePath);
                        try {
                            if (outputImage.exists()) {
                                mHeadImage = outputImage;
                                outputImage.delete();
                                outputImage.createNewFile();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            imageUri = FileProvider.getUriForFile(UserInfoActivity.this,
                                    "com.android.nana.fileProvider",
                                    outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);
                    }
                });

                menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {
                        //Android6.0开始，WRITE_EXTERNAL_STORAGE被认为是危险权限，需要动态申请
                        if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    APPLY_PERMISSION);
                        } else {
                            openAlbum();
                        }
                    }
                });

                menuItemList.add(menuItem1);
                menuItemList.add(menuItem2);

                bottomMenuFragment.setMenuItems(menuItemList);

                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
            }
        });

        // 背景图片
        mIvBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  mCwdBackground.show();
                BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();

                List<MenuItem> menuItemList = new ArrayList<MenuItem>();
                MenuItem menuItem1 = new MenuItem();
                menuItem1.setText("拍照");
                menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
                MenuItem menuItem2 = new MenuItem();
                menuItem2.setText("相册");
                menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);
                menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {

                        backgImagePath = getExternalCacheDir() + "/backg_image.jpg";
                        File outputImage = new File(backgImagePath);
                        try {
                            if (outputImage.exists()) {
                                mBackgroundImage = outputImage;
                                outputImage.delete();
                                outputImage.createNewFile();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            backgImageUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.android.nana.fileProvider", outputImage);
                        } else {
                            backgImageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, backgImageUri);
                        startActivityForResult(intent, BACKGROUND_PHOTO);

                    }
                });
                menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
                    @Override
                    public void onClickMenuItem(View v, MenuItem menuItem) {
                        //Android6.0开始，WRITE_EXTERNAL_STORAGE被认为是危险权限，需要动态申请
                        if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    APPLY_PERMISSION);
                        } else {
                            bOpenAlbum();
                        }
                    }
                });
                menuItemList.add(menuItem1);
                menuItemList.add(menuItem2);

                bottomMenuFragment.setMenuItems(menuItemList);

                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
            }
        });

        // 性别
        mTxtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mPickerView) {
                    mPickerView.show();
                }

            }
        });

        // 年龄
        mTxtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPickerAgeView != null) {
                    mPickerAgeView.show();
                }
            }
        });

        // 注册目的
      /*  mTxtPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPurposeView != null) {
                    mPurposeView.show();
                }

            }
        });*/

    }

    private void save() {
        String userId = (String) SharedPreferencesUtils.getParameter(UserInfoActivity.this, "userId", "");
        String username = mEtUserName.getText().toString().trim();
        String gender = mTxtGender.getText().toString();
        String sex = gender.equals("保密") ? "0" : gender.equals("男") ? "1" : "2";
        String age = mTxtAge.getText().toString();

        String purpose = mEditOther.getText().toString();
        String introduce = mEtIntroduce.getText().toString().trim();

        CustomerDbHelper.updateUserInfo(userId, "", username, "", provinceid, cityid, areaid,
                age, sex, "", purpose, introduce, mHeadImage, mBackgroundImage, mIOAuthCallBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //某些手机拍完照之后会自动旋转照片，以下代码把图片还原为旋转前状态
                        int degree = PhotoRotateUtil.getBitmapDegree(imagePath);
                        bitmap = rotateBitmapByDegree(bitmap, degree);
                        mIvHeader.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String path = "";
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        path = handleImageOnKitKat(data);
                    } else {
                        //4.4以下使用这个方法处理图片
                        path = handleImageBeforeKitKat(data);
                    }

                    File outputImage = new File(path);
                    if (outputImage.exists()) {
                        mHeadImage = outputImage;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    int degree = PhotoRotateUtil.getBitmapDegree(path);
                    bitmap = rotateBitmapByDegree(bitmap, degree);
                    mIvHeader.setImageBitmap(bitmap);
                }
                break;

            case BACKGROUND_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(backgImageUri));

                        //某些手机拍完照之后会自动旋转照片，以下代码把图片还原为旋转前状态
                        int degree = PhotoRotateUtil.getBitmapDegree(backgImagePath);
                        bitmap1 = rotateBitmapByDegree(bitmap1, degree);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth = displayMetrics.widthPixels;
                        if (bitmap1.getWidth() <= screenWidth) {
                            mIvBackground.setImageBitmap(bitmap1);
                        } else {
                            Bitmap bmp = Bitmap.createScaledBitmap(bitmap1, screenWidth, bitmap1.getHeight() * screenWidth / bitmap1.getWidth(), true);
                            mIvBackground.setImageBitmap(bmp);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case BACKGROUND_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String path = "";
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        path = handleImageOnKitKat(data);
                    } else {
                        //4.4以下使用这个方法处理图片
                        path = handleImageBeforeKitKat(data);
                    }

                    File outputImage = new File(path);
                    if (outputImage.exists()) {
                        mBackgroundImage = outputImage;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    int degree = PhotoRotateUtil.getBitmapDegree(path);
                    bitmap = rotateBitmapByDegree(bitmap, degree);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int screenWidth = displayMetrics.widthPixels;
                    if (bitmap.getWidth() <= screenWidth) {
                        mIvBackground.setImageBitmap(bitmap);
                    } else {
                        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, bitmap.getHeight() * screenWidth / bitmap.getWidth(), true);
                        mIvBackground.setImageBitmap(bmp);
                    }
                }
                break;

        }
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data) {
        String imgPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contenturi = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imgPath = getImagePath(contenturi, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，使用普通方法处理
            imgPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
        return imgPath;
    }

    //通过Uri和selection来获取真实的图片路径
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(UserInfoActivity.this, "正在提交，请等待...");
        }

        @Override
        public void getSuccess(String successJson) {

            String pass = (String) SharedPreferencesUtils.getParameter(UserInfoActivity.this, "userPass", "");
            LoginModel.doLogin(UserInfoActivity.this, successJson, pass);
            UIHelper.hideOnLoadingDialog();
            setResult(RESULT_OK);
            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
            UserInfoActivity.this.finish();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(UserInfoActivity.this, "保存失败请稍后重试");
            UIHelper.hideOnLoadingDialog();
        }
    };

    private OptionsPickerView pvOptions;

    private int mProvinceId, mCityId, mCountyId;
    private String provinceid, cityid, areaid;

    private void cityShow() {

        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);

                provinceid = options1Items.get(options1).getPickerViewId();//省id
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                areaid = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id

                mTxtAddress.setText(tx);
            }
        }).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_txt_address:
                cityShow();
                break;
            case R.id.user_info_txt_gender:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case APPLY_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                break;

        }
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
}
