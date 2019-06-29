package com.android.nana.friend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.common.base.BaseActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.Aitefriendbean;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.ExtensionEvent;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.eventBus.NullRedEvent;
import com.android.nana.eventBus.RedEvent;
import com.android.nana.eventBus.RemarksEvent;
import com.android.nana.eventBus.TipEvent;
import com.android.nana.main.CoopActivity;
import com.android.nana.photos.FullyGridLayoutManager;
import com.android.nana.photos.GridImageAdapter;
import com.android.nana.red.RedEnvelopesActivity;
import com.android.nana.util.MD5;
import com.android.nana.util.RecordSettings;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.TEditText;
import com.android.nana.util.TObject;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.widget.CustomProgressDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by lenovo on 2017/11/21.
 */

public class VideoDynamicActivity extends BaseActivity implements View.OnClickListener {
    private List<Aitefriendbean>aite_list=new ArrayList<>();
    private String userIdList="";
    private List<String>huati_list=new ArrayList<>();
    private int beforlong,bhlong;
    private String name;
    double platformPercentage=0;
    String toast="";
    String flag="1";
    public LatLng BEIJING;//经纬度
    private double lng, lat;
    private String address;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private ImageView huiti_;
    private ImageView haoyou_;

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;
    private RecyclerView mPhotos;

    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private TEditText mContentEt;
    boolean isVideo = false;
    private UploadManager uploadManager;
    private List<LocalMedia> selectList;
    private File mFileImag;
    private String mVideoPath;
    private PLShortVideoTranscoder mShortVideoTranscoder;
    private CustomProgressDialog mProcessingDialog;
    private LinearLayout mRedll;
    private TextView mMoneyTv;
    private int type = 0;
    private String km, num;
    private RedEvent mRed;
    private String mExtensionUrl = "";
    private String mExtensionUrlType = "0";
    private LinearLayout mAddressLl, mExtensionLl;
    private TextView mAddressTv, mExtensionTv;
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int RESULT_CODE_KEYWORDS = 102;
    private boolean isCustom = true;
    private String uid;
    private boolean isDef = false;//判断是否有缓存

    private LinearLayout mRemarksAddressLl;
    private TextView mRemarksAdressTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onAddPicClick();

        if (!EventBus.getDefault().isRegistered(VideoDynamicActivity.this)) {
            EventBus.getDefault().register(VideoDynamicActivity.this);
        }


        //new一个uploadManager类
        uploadManager = new UploadManager();
 /*      mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTranscoder.cancelTranscode();
            }
        });*/
        //初始化定位
        initLocation();



    }


    private void onAddPicClick() {
        PictureSelector.create(VideoDynamicActivity.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(false)// 是否裁剪
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(10)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this);
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }


    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (isCustom) {
                if (null != location) {
                    locationClient.stopLocation();
                    lng = location.getLongitude();//经    度
                    lat = location.getLatitude();//纬    度
                    address = location.getCity() + location.getPoiName();
                    BEIJING = new LatLng(lat, lng);
                }
            }
        }
    };

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_video_dynamic);
    }

    @Override
    protected void findViewById() {
        huiti_=findViewById(R.id.huiti_);
        haoyou_=findViewById(R.id.haoyou_);
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);
        mPhotos = findViewById(R.id.recycler);
        mContentEt = findViewById(R.id.et_introduce);
        mRedll = findViewById(R.id.ll_red);
        mMoneyTv = findViewById(R.id.tv_money);
        mAddressLl = findViewById(R.id.ll_address);
        mExtensionLl = findViewById(R.id.ll_extension);
        mAddressTv = findViewById(R.id.tv_address);
        mExtensionTv = findViewById(R.id.tv_extension);
        mRemarksAdressTv = findViewById(R.id.tv_remarks_address);

        mRemarksAddressLl = findViewById(R.id.ll_remarks_address);

        huiti_.setOnClickListener(this);
        haoyou_.setOnClickListener(this);
        mContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforlong = charSequence.toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
               bhlong = editable.toString().length();
                String  name=editable.toString();
                if (beforlong>=bhlong&&!TextUtils.isEmpty(name)){//判断是否是清除状态 return;

                  //  showToast("删");
                  //  return;
                }
                else {
                    if (bhlong!=0){
                        if (editable.charAt(editable.length()-1)=='#'){

                        Intent intent=new Intent(VideoDynamicActivity.this,TalkSeach_Activity.class);
                        intent.putExtra("state","2");
                        startActivityForResult(intent,002);

                    }

                    if (editable.charAt(editable.length()-1)=='@'){
                            //showToast("@你们了哦");

                        Intent intent001=new Intent(VideoDynamicActivity.this,aiteFriendActivity.class);
                        intent001.putExtra("state","2");
                        startActivityForResult(intent001,003);
                        }
                    }
                    else {
                    //    showToast("删");


                    }



                }



            }
        });
        Intent intent=getIntent();
        if (null!=intent.getStringExtra("huati")&&!intent.getStringExtra("huati").equals("")){
            TObject object = new TObject();
            //匹配规则
            object.setObjectRule("#");
            //话题内容
            object.setObjectText(intent.getStringExtra("huati"));
            mContentEt.setObject(object);
        }

    }



    @Override
    protected void init() {
        uid = (String) SharedPreferencesUtils.getParameter(VideoDynamicActivity.this, "userId", "");
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setText("取消");
        mTitleTv.setText("");
        mBackTv.setTextColor(getResources().getColor(R.color.white));
        mBackTv.setCompoundDrawables(null, null, null, null);
        mAction2.setVisibility(View.VISIBLE);
        mAction2.setText("发布");
        mAction2.setTextColor(getResources().getColor(R.color.white));
        selectList = new ArrayList<>();
        FullyGridLayoutManager manager = new FullyGridLayoutManager(VideoDynamicActivity.this, 3, GridLayoutManager.VERTICAL, false);
        mPhotos.setLayoutManager(manager);
        adapter = new GridImageAdapter(VideoDynamicActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mPhotos.setAdapter(adapter);


        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(VideoDynamicActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(VideoDynamicActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            //   PictureSelector.create(VideoDynamicActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(VideoDynamicActivity.this);
                } else {
                    Toast.makeText(VideoDynamicActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        isAllow();

    }

    private void isAllow() {
        CustomerDbHelper.isAllowLocation(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data=new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));

                  //  platformPercentage=data.getDouble("platformPercentage");


                    if (result.getString("state").equals("0")) {
                        flag="0";
                        mAddressLl.setVisibility(View.VISIBLE);
                        mExtensionLl.setVisibility(View.VISIBLE);
                         platformPercentage =data.getDouble("platformPercentage");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAction2.setOnClickListener(this);
        mRedll.setOnClickListener(this);
        mAddressLl.setOnClickListener(this);
        mExtensionLl.setOnClickListener(this);
        mRemarksAddressLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.huiti_:

                Intent intent0=new Intent(VideoDynamicActivity.this,TalkSeach_Activity.class);
                intent0.putExtra("state","1");
                startActivityForResult(intent0,001);
                break;
            case R.id.haoyou_:
                //setEdtext();

                Intent intent001=new Intent(VideoDynamicActivity.this,aiteFriendActivity.class);
                intent001.putExtra("state","1");
                startActivityForResult(intent001,003);
                break;

                case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                publish();
                break;
            case R.id.ll_red:
                if (flag.equals("0")){
                 Intent intent = new Intent(VideoDynamicActivity.this, RedEnvelopesActivity.class);
                intent.putExtra("red", mRed);
                startActivity(intent);
                }
                else {
                    showNormalDialog();
                }


                break;
            case R.id.ll_address:
                Intent intent1 = new Intent(VideoDynamicActivity.this, SearchAddressActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_remarks_address:
                Intent intent2 = new Intent(VideoDynamicActivity.this, RemarksAddressActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_extension:
                Intent intent3 = new Intent(VideoDynamicActivity.this, ExtensionActivity.class);
                intent3.putExtra("extension", mExtensionUrl);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }

    private void publish() {//发布
        if (aite_list.size()!=0){
            for (int i=0;i<aite_list.size();i++){
                if (mContentEt.getText().toString().indexOf(aite_list.get(i).getName())==-1){


                }
                else {
                    if (aite_list.size()-1==i){
                        userIdList=userIdList+aite_list.get(i).getId();
                    }
                    else {
                        userIdList=userIdList+aite_list.get(i).getId()+",";
                    }

                }
            }
        }
        showProgressDialog("", "加载中...");
        final String text = mContentEt.getText().toString().trim();

        if (null != mExtensionUrl && !"".equals(mExtensionUrl) && selectList.size() == 0) {
            dismissProgressDialog();
            ToastUtils.showToast("添加推广链接，请选择一张图片或视频");
            return;
        }

        if (!TextUtils.isEmpty(text) || selectList.size() > 0) {
            ArrayList<File> files = new ArrayList<>();
            for (final LocalMedia media : selectList) {
                if (media.getPictureType().equals("video/mp4")) {
                    isVideo = true;
                } else {
                  if (null != mExtensionUrl && !"".equals(mExtensionUrl) && selectList.size() > 1) {
                        dismissProgressDialog();
                        ToastUtils.showToast("添加推广链接，图片只可选择一张");
                        return;
                    } else if (media.getPath().endsWith(".webp")) {
                        dismissProgressDialog();
                        ToastUtils.showToast("暂时不支持webp格式图片,请重新选择图片");
                        return;
                    } else if (media.getPath().endsWith("png") || media.getPath().endsWith("jpeg") || media.getPath().endsWith("jpg") || media.getPath().endsWith("PNG") || media.getPath().endsWith("JPEG") || media.getPath().endsWith("JPG")){
                        File file = new File(media.getPath());
                        if (file.exists()) {
                            files.add(file);
                        }
                    }else {
                      dismissProgressDialog();
                      ToastUtils.showToast("请选择png/jpeg格式图片,请重新选择图片");
                      return;
                  }
                }
            }

            if (isVideo) {//视频上传到七牛云
                isVideo = false;
                CustomerDbHelper.getToken(new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            if (result.getString("state").equals("0")) {

                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                Date date = new Date(System.currentTimeMillis());
                                final String mName = format.format(date);
                                final String token = data.getString("token");

                                uploadManager.put(mVideoPath, mName + ".mp4", token, new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        if (info.isOK()) {
                                            publistVideo(uid, text, key);
                                        } else {
                                            dismissProgressDialog();
                                            ToastUtils.showToast("发布失败请稍后重试");
                                        }
                                    }
                                }, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        ToastUtils.showToast("发布失败请稍后重试！");
                        dismissProgressDialog();
                    }
                });
            } else {
                if (type == 2) {
                    String time = getTime();
                    double single = Double.parseDouble(mRed.money) / Double.parseDouble(num);
                    String secret = uid + "&" + type + "&" + num + "&" + mRed.money + "&" + time + "&" + single + "&" + km + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2";
                    String appSignature = MD5.MD5Hash(secret);
                    CustomerDbHelper.publishArticle(uid, text, files.size(), files, "1", String.valueOf(lng), String.valueOf(lat), address, type, num, mRed.money, String.valueOf(single), km, time, appSignature, mRed.mAdvertising, mRed.mAdvType, mRed.mAdvUrl, mExtensionUrl,mExtensionUrlType,userIdList, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            dismissProgressDialog();
                            if (mResultDetailModel.mIsSuccess) {
                                mContentEt.setText("");
                                selectList.clear();
                                adapter.setList(selectList);
                                EventBus.getDefault().post(new FriendEvent());
                                VideoDynamicActivity.this.finish();
                            }

                            try {
                                JSONObject jsonObject1=new JSONObject(successJson);
                                JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                                toast= jsonObject2.getString("description");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            showToast(toast+"");
                        }

                        @Override
                        public void getFailue(String failueJson) {
                            ToastUtils.showToast("发布失败请稍后重试!");
                            dismissProgressDialog();
                        }
                    });
                } else if (type == 1) {

                    String time = getTime();
                    String secret = uid + "&" + type + "&" + num + "&" + mRed.money + "&" + time + "&" + km + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2";
                    String appSignature = MD5.MD5Hash(secret);


                    CustomerDbHelper.publishArticle(uid, text, files.size(), files, "1", String.valueOf(lng), String.valueOf(lat), address, type, num, mRed.money, "", km, time, appSignature, mRed.mAdvertising, mRed.mAdvType, mRed.mAdvUrl, mExtensionUrl, mExtensionUrlType,userIdList,new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            dismissProgressDialog();
                            if (mResultDetailModel.mIsSuccess) {
                                mContentEt.setText("");
                                selectList.clear();
                                adapter.setList(selectList);
                                EventBus.getDefault().post(new FriendEvent());
                                VideoDynamicActivity.this.finish();
                            }

                            try {
                                JSONObject jsonObject1=new JSONObject(successJson);
                                JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                                toast= jsonObject2.getString("description");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            showToast(toast+"");
                        }

                        @Override
                        public void getFailue(String failueJson) {
                            ToastUtils.showToast("发布失败请稍后重试!");
                            dismissProgressDialog();
                        }
                    });
                } else {
                    CustomerDbHelper.publishArticle(uid, text, files.size(), files, "1", String.valueOf(lng), String.valueOf(lat), address, type, "", "", "", "", "", "", "", "", "", mExtensionUrl,mExtensionUrlType,userIdList, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            dismissProgressDialog();
                            if (mResultDetailModel.mIsSuccess) {
                                mContentEt.setText("");
                                selectList.clear();
                                adapter.setList(selectList);
                                EventBus.getDefault().post(new FriendEvent());
                                VideoDynamicActivity.this.finish();
                            }
                            try {
                                JSONObject jsonObject1=new JSONObject(successJson);
                                JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                                toast= jsonObject2.getString("description");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            showToast(toast+"");
                        }

                        @Override
                        public void getFailue(String failueJson) {
                            ToastUtils.showToast("发布失败请稍后重试!");
                            dismissProgressDialog();
                        }
                    });


                }
            }
        } else {
            dismissProgressDialog();
            ToastUtils.showToast("内容或相册不能为空！");
        }
    }


    private void publistVideo(String uid, String text, String key) {//上传视频

        if (type == 2) {
            String time = getTime();
            double single = Double.parseDouble(mRed.money) / Double.parseDouble(num);
            String secret = uid + "&" + type + "&" + num + "&" + mRed.money + "&" + time + "&" + single  + "&" + km + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2";
            String appSignature = MD5.MD5Hash(secret);

            CustomerDbHelper.publishArticle(uid, text, 1, mFileImag, "2", key, String.valueOf(lng), String.valueOf(lat), address, type, num, mRed.money,String.valueOf(single), km, time, appSignature, mRed.mAdvertising, mRed.mAdvType, mRed.mAdvUrl, mExtensionUrl,mExtensionUrlType,userIdList, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    dismissProgressDialog();
                    if (mResultDetailModel.mIsSuccess) {
                        //删除本地视频首帧图
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(mFileImag);
                        intent.setData(uri);
                        VideoDynamicActivity.this.sendBroadcast(intent);
                        mFileImag.delete();

                        mContentEt.setText("");
                        selectList.clear();
                        adapter.setList(selectList);
                        EventBus.getDefault().post(new FriendEvent());
                        VideoDynamicActivity.this.finish();
                    }
                    try {
                        JSONObject jsonObject1=new JSONObject(successJson);
                        JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                        toast= jsonObject2.getString("description");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast(toast+"");
                }

                @Override
                public void getFailue(String failueJson) {
                    dismissProgressDialog();
                }
            });
        } else if (type == 1) {
            String time = getTime();
            String secret = uid + "&" + type + "&" + num + "&" + mRed.money + "&" + time + "&" + km + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2";
            String appSignature = MD5.MD5Hash(secret);

            CustomerDbHelper.publishArticle(uid, text, 1, mFileImag, "2", key, String.valueOf(lng), String.valueOf(lat), address, type, num, mRed.money, "", km, time, appSignature, mRed.mAdvertising, mRed.mAdvType, mRed.mAdvUrl, mExtensionUrl,mExtensionUrlType,userIdList, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    dismissProgressDialog();
                    if (mResultDetailModel.mIsSuccess) {

                        //删除本地视频首帧图
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(mFileImag);
                        intent.setData(uri);
                        VideoDynamicActivity.this.sendBroadcast(intent);
                        mFileImag.delete();

                        mContentEt.setText("");
                        selectList.clear();
                        adapter.setList(selectList);
                        EventBus.getDefault().post(new FriendEvent());
                        VideoDynamicActivity.this.finish();
                    }
                    try {
                        JSONObject jsonObject1=new JSONObject(successJson);
                        JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                        toast= jsonObject2.getString("description");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast(toast+"");
                }

                @Override
                public void getFailue(String failueJson) {
                    dismissProgressDialog();
                }
            });
        } else {
            CustomerDbHelper.publishArticle(uid, text, 1, mFileImag, "2", key, String.valueOf(lng), String.valueOf(lat), address, type, "", "", "", "", "", "", "", "", "", mExtensionUrl,mExtensionUrlType,userIdList, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    dismissProgressDialog();
                    if (mResultDetailModel.mIsSuccess) {

                        //删除本地视频首帧图
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(mFileImag);
                        intent.setData(uri);
                        VideoDynamicActivity.this.sendBroadcast(intent);
                        mFileImag.delete();

                        mContentEt.setText("");
                        selectList.clear();
                        adapter.setList(selectList);
                        EventBus.getDefault().post(new FriendEvent());
                        VideoDynamicActivity.this.finish();
                    }   try {
                        JSONObject jsonObject1=new JSONObject(successJson);
                        JSONObject jsonObject2=jsonObject1.getJSONObject("result");
                        toast= jsonObject2.getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast(toast+"");

                }

                @Override
                public void getFailue(String failueJson) {
                    dismissProgressDialog();
                }
            });
        }

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(VideoDynamicActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(false) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .enableCrop(false)// 是否裁剪
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                 //   .selectionMedia(selectList)// 是否传入已选图片
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .compress(true)//是否压缩
                    .videoQuality(1)// 视频录制质量 0 or 1 int
                    .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                    .recordVideoSecond(10)
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    String path = null;
                    for (LocalMedia media : selectList) {
                        if (media.getPictureType().equals("video/mp4")) {
                           // mProcessingDialog.show();
                            isVideo = true;
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(media.getPath());
                            Bitmap bitmap = mmr.getFrameAtTime();
                            mFileImag = Utils.compressImage(bitmap);
                            path = media.getPath();
                        }
                    }

                    if (isVideo) {
                        isVideo = false;
                        adapter.setList(selectList, true);
                        adapter.notifyDataSetChanged();
                        mVideoPath = path;
                        //视频压缩
                     /*   mShortVideoTranscoder = new PLShortVideoTranscoder(this, path, RecordSettings.TRANSCODE_FILE_PATH);
                        mShortVideoTranscoder.transcode(960, 540, getEncodingBitrateLevel(3), false, new PLVideoSaveListener() {
                            @Override
                            public void onSaveVideoSuccess(final String s) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProcessingDialog.dismiss();
                                        adapter.setList(selectList, true);
                                        adapter.notifyDataSetChanged();
                                        mVideoPath = s;
                                    }
                                });
                            }

                            @Override
                            public void onSaveVideoFailed(final int errorCode) {
                                mProcessingDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (errorCode) {
                                            case ERROR_NO_VIDEO_TRACK:
                                                ToastUtils.showToast("该文件没有视频信息！");
                                                break;
                                            case ERROR_SRC_DST_SAME_FILE_PATH:
                                                ToastUtils.showToast("源文件路径和目标路径不能相同！");
                                                break;
                                            case ERROR_LOW_MEMORY:
                                                ToastUtils.showToast("手机内存不足，无法对该视频进行时光倒流！");
                                                break;
                                            default:
                                                ToastUtils.showToast("无法获取文件请稍后重试！");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onSaveVideoCanceled() {
                                mProcessingDialog.dismiss();
                            }

                            @Override
                            public void onProgressUpdate(float percentage) {
                                mProcessingDialog.setProgress((int) (100 * percentage));
                            }
                        });*/
                    } else {
                        adapter.setList(selectList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        } else if (null != PictureSelector.getDataResult() && PictureSelector.getDataResult().size() > 0) {
            selectList = PictureSelector.getDataResult();
            for (LocalMedia media : selectList) {
                if (media.getPictureType().equals("video/mp4")) {
                    adapter.setList(selectList, true);
                }else if ("image/jpeg".equals(media.getPictureType())){
                    isDef = true;
                }
            }
//            if (isDef){
//                selectList.clear();
//            }
            adapter.notifyDataSetChanged();
        }
        if (resultCode==001){
            //话题返回值
            TObject object = new TObject();
            //匹配规则
            object.setObjectRule("#");
            //话题内容
            object.setObjectText(data.getStringExtra("huati"));
            mContentEt.setObject(object);
              //  mContentEt.setText(""+mContentEt.getText().toString()+data.getStringExtra("huati")+" ");
        }
        if (resultCode==002){
            //话题返回值
            int index=mContentEt.getSelectionStart();
            String str=mContentEt.getText().toString();
            if (!str.equals("")) {//判断输入框不为空，执行删除
                mContentEt.getText().delete(index-1,index);
            }
            //  mContentEt.setText(mContentEt.getText().toString().substring(0,mContentEt.getText().length()-1));
            TObject object = new TObject();
            //匹配规则
            object.setObjectRule("#");
            //话题内容
            object.setObjectText(data.getStringExtra("huati"));
            mContentEt.setObject(object);

            //  mContentEt.setText(""+mContentEt.getText().toString()+data.getStringExtra("huati")+" ");

        }

        if (resultCode==003){
            String input=mContentEt.getText().toString()+data.getStringExtra("friendname");
            SpannableString msp = new SpannableString(input);
            Pattern pattern = Pattern.compile("\\@([a-zA-Z0-9\\u4e00-\\u9fa5]+)");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                msp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_huati)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            mContentEt.setText(msp);
            mContentEt.setSelection(mContentEt.getText().toString().length());

            Aitefriendbean aite=new Aitefriendbean();
            aite.setId(data.getStringExtra("friendid"));
            aite.setName(data.getStringExtra("friendname"));
            aite_list.add(aite);

        }



    }

    private int getEncodingBitrateLevel(int position) {
        return RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[position];
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void RedEvent(RedEvent event) {
        mMoneyTv.setText(event.money + "元红包");
        type = event.type;
        km = event.km;
        num = event.num;
        mRed = event;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void ExtensionEvent(ExtensionEvent event) {
        mExtensionUrl = event.text;
        mExtensionUrlType = event.type;
        mExtensionTv.setText(event.text);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void NullRedEvent(NullRedEvent event) {
        mMoneyTv.setText("");
        mRed = null;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTipEvent(TipEvent event) {

        isCustom = false;

        lng = event.tip.getPoint().getLongitude();
        lat = event.tip.getPoint().getLatitude();
        address = event.tip.getDistrict() + "." + event.tip.getName();
        mAddressTv.setText(event.tip.getDistrict() + "." + event.tip.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(VideoDynamicActivity.this);
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRemarksEvent(RemarksEvent event) {
        isCustom = false;//添加备注后不请求地址

        address += "." + event.address;
        mRemarksAdressTv.setText(event.address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(VideoDynamicActivity.this);

        normalDialog.setTitle("提示");
        normalDialog.setMessage("此功能尚未开通，请前往“身份认证”进行申请，审核通过将开通此功能。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Intent coopIntent = new Intent(VideoDynamicActivity.this, CoopActivity.class);
                        startActivity(coopIntent);
                        finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


    private void setEdtext(){
        Pattern p = Pattern.compile("#([^\\#|.]+)#");
      //  String aa="asdasd";

     //   SpannableString ss = new SpannableString(mContentEt.getText().toString());
        Matcher m = p.matcher(mContentEt.getText().toString());



        /*循环找出每个复合正则的字符串，逐个处理*/
        while (m.find()) {
            String s=m.group(1);
            huati_list.add(s);
           // String s2=m.group(0);
            /*取出 字符串 前后添加#*/
            /*区域处理*/
//            final String s = "#" + m.group(1) + "#";
//            int startIndex = m.start(1) - 1;
//            int endIndex = startIndex + s.length();
//            ss.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(@NonNull View view) {
//
//                }
//
//                @Override
//                public void updateDrawState(@NonNull TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setColor(Color.parseColor("#507daf"));
//                }
//            },startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        int i=huati_list.size();
       // mContentEt.setText(ss);
      //  mContentEt.setMovementMethod(LinkMovementMethod.getInstance());

    }


}
