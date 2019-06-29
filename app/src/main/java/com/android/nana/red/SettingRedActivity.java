package com.android.nana.red;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.connect.Constants;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.SettingOrdinaryRedEvent;
import com.android.nana.eventBus.SettingRedEvent;
import com.android.nana.eventBus.SettingRedUrlEvent;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.user.weight.VideoPlayerController;
import com.android.nana.util.RecordSettings;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

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


/**
 * Created by lenovo on 2019/1/2.
 */

public class SettingRedActivity extends BaseActivity implements View.OnClickListener {

    String mNubStr, mKmStr, mMoneyStr;
    private TextView mTitleTv, mBackTv, mRightTv;
    private TextView mMoneyTv, mUrlTv;
    private LinearLayout mCoverLl, mUrlLl;
    private ImageView mDefIv;
    private NiceVideoPlayer mPlayer;
    private boolean isVideo = false;
    private File mFileImag;
    private String path;
    private int type = 0;//红包广告类型：type（1=图片   2=视频   0=默认没上传）
    private List<LocalMedia> selectList = new ArrayList<>();
    private String isRed;
    private String mAdvUrl;
    private UploadManager uploadManager;
   // private PLShortVideoTranscoder mShortVideoTranscoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SettingRedActivity.this)) {
            EventBus.getDefault().register(SettingRedActivity.this);
        }
        uploadManager = new UploadManager();
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_set_red);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mMoneyTv = findViewById(R.id.tv_money);
        mCoverLl = findViewById(R.id.ll_cover);
        mUrlLl = findViewById(R.id.ll_url);
        mUrlTv = findViewById(R.id.tv_url);
        mDefIv = findViewById(R.id.iv_def);
        mPlayer = findViewById(R.id.video_player);
    }

    @Override
    protected void init() {
        mRightTv.setText("确定");
        mTitleTv.setText("设置红包封面");
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setVisibility(View.VISIBLE);

        mNubStr = getIntent().getStringExtra("nub");
        mMoneyStr = getIntent().getStringExtra("money");
        mKmStr = getIntent().getStringExtra("km");
        isRed = getIntent().getStringExtra("isRed");

        if (null != getIntent().getStringExtra("type")) {
            type = Integer.valueOf(getIntent().getStringExtra("type"));
            path = getIntent().getStringExtra("advertising");
            mAdvUrl = getIntent().getStringExtra("url");
            mUrlTv.setText(mAdvUrl);

            if (type == 1) {
                isVideo = false;
                mDefIv.setVisibility(View.VISIBLE);
                mPlayer.setVisibility(View.GONE);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.icon_red_def)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);

                Glide.with(SettingRedActivity.this)
                        .asBitmap()
                        .load(path)
                        .apply(options)
                        .into(mDefIv);
            } else {
                isVideo = true;
                mDefIv.setVisibility(View.GONE);
                mPlayer.setVisibility(View.VISIBLE);

                VideoPlayerController controller = new VideoPlayerController(SettingRedActivity.this);
                final VideoPlayerController mController = controller;
                mPlayer.setController(mController);

                mPlayer.continueFromLastPosition(false);
                mPlayer.getTcpSpeed();
                if (path.endsWith(".mp4")) {
                    mPlayer.setUp(Constants.HOST_URL + path, null);
                } else {
                    mPlayer.setUp(path, null);
                }

                mPlayer.start();
                mController.mCenterStart.setVisibility(View.GONE);

                mController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drawable.ConstantState drawableCs = SettingRedActivity.this.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                        if (mController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                            mPlayer.setVolume(0);
                            mController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                        } else {
                            mPlayer.setVolume(50);
                            mController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                        }
                    }
                });
            }
        }

        mMoneyTv.setText(mMoneyStr);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mCoverLl.setOnClickListener(this);
        mUrlLl.setOnClickListener(this);
        mPlayer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                mPlayer.releasePlayer();
                this.finish();
                if (isRed.equals("yes")) {//拼手气红包
                    EventBus.getDefault().post(new SettingRedEvent(path, String.valueOf(type), mUrlTv.getText().toString()));
                } else {//普通红包
                    EventBus.getDefault().post(new SettingOrdinaryRedEvent(path, String.valueOf(type), mUrlTv.getText().toString()));
                }
                break;
            case R.id.toolbar_right_2:
                sure();
                break;
            case R.id.ll_cover:
                bottomMenu();
                break;
            case R.id.ll_url:
                startActivity(new Intent(SettingRedActivity.this, SettingRedUrlActivity.class));
                break;
            default:
                break;
        }
    }

    private void sure() {
        showProgressDialog("", "加载中...");
        mPlayer.releasePlayer();

        if (type == 0){
            SettingRedActivity.this.finish();
            return;
        }


        if (type == 2) {
            //压缩视频
            shortVideo(path);
        } else {//图片
            final File file = new File(path);
            if (file.exists()) {
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
                                //上传七牛云
                                uploadManagerFile(file,mName+".jpg",token);
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
        }
    }

    private void uploadManagerFile(File file,String name,String token) {//上传图片
        uploadManager.put(file, name, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                dismissProgressDialog();
                if (info.isOK()) {
                    if (isRed.equals("yes")) {//拼手气红包
                        EventBus.getDefault().post(new SettingRedEvent(key, String.valueOf(type), mUrlTv.getText().toString()));
                    } else {//普通红包
                        EventBus.getDefault().post(new SettingOrdinaryRedEvent(key, String.valueOf(type), mUrlTv.getText().toString()));
                    }
                    SettingRedActivity.this.finish();
                } else {
                    ToastUtils.showToast("上传失败请稍后重试");
                }
            }
        }, null);
    }


    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("恢复默认");
        menuItem1.setStyle(MenuItem.MenuItemStyle.BLUE);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("从手机相册选择");
        menuItem2.setStyle(MenuItem.MenuItemStyle.BLUE);

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(SettingRedActivity.this.getFragmentManager(), "BottomMenuFragment");

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                type = 0;
                path = null;
                mAdvUrl = null;

                mUrlTv.setText("");
                mPlayer.releasePlayer();
                mPlayer.setVisibility(View.GONE);
                mDefIv.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.icon_red_def);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mDefIv.setImageDrawable(drawable);
            }
        });


        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                onPicture();
            }
        });
    }

    private void onPicture() {

        mPlayer.releasePlayer();

        PictureSelector.create(SettingRedActivity.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(16,9)
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(100000)
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);

                    for (LocalMedia media : selectList) {
                        if (media.getPictureType().equals("video/mp4")) {
                            isVideo = true;
                            type = 2;
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(media.getPath());
                            Bitmap bitmap = mmr.getFrameAtTime();
                            mFileImag = Utils.compressImage(bitmap);
                            path = media.getPath();
                            mDefIv.setVisibility(View.GONE);
                            mPlayer.setVisibility(View.VISIBLE);

                            VideoPlayerController controller = new VideoPlayerController(SettingRedActivity.this);
                            final VideoPlayerController mController = controller;
                            mPlayer.setController(mController);

                            mPlayer.continueFromLastPosition(false);
                            mPlayer.getTcpSpeed();
                            mPlayer.setUp(path, null);
                            mPlayer.start();
                            mController.mCenterStart.setVisibility(View.GONE);

                            mController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Drawable.ConstantState drawableCs = SettingRedActivity.this.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                                    if (mController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                                        mPlayer.setVolume(0);
                                        mController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                                    } else {
                                        mPlayer.setVolume(50);
                                        mController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                                    }
                                }
                            });
                        } else {
                            isVideo = false;
                            type = 1;
                            mDefIv.setVisibility(View.VISIBLE);
                            mPlayer.setVisibility(View.GONE);
                            path = media.getCompressPath();
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.icon_head_default)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true);

                            Glide.with(SettingRedActivity.this)
                                    .asBitmap()
                                    .load(path)
                                    .apply(options)
                                    .into(mDefIv);
                        }
                    }
                    break;
                default:
                    break;

            }
        } else {
            if (isVideo) {
                if (null != path) {
                    mDefIv.setVisibility(View.GONE);
                    mPlayer.setVisibility(View.VISIBLE);

                    VideoPlayerController controller = new VideoPlayerController(SettingRedActivity.this);
                    final VideoPlayerController mController = controller;
                    mPlayer.setController(mController);

                    mPlayer.continueFromLastPosition(false);
                    mPlayer.getTcpSpeed();
                    mPlayer.setUp(path, null);
                    mPlayer.start();
                    mController.mCenterStart.setVisibility(View.GONE);

                    mController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Drawable.ConstantState drawableCs = SettingRedActivity.this.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                            if (mController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                                mPlayer.setVolume(0);
                                mController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                            } else {
                                mPlayer.setVolume(50);
                                mController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                            }
                        }
                    });
                }
            }
        }
    }

    private void shortVideo(final String videoPath) {
        path = videoPath;
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
                        //上传七牛云
                        uploadManager(videoPath, mName + ".mp4", token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
   /*     mShortVideoTranscoder = new PLShortVideoTranscoder(this, videoPath, RecordSettings.TRANSCODE_FILE_PATH);
        mShortVideoTranscoder.transcode(960, 540, getEncodingBitrateLevel(3), false, new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path = s;
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
                                        //上传七牛云
                                        uploadManager(s, mName + ".mp4", token);
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
                });
            }

            @Override
            public void onSaveVideoFailed(final int errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode) {
                            case ERROR_NO_VIDEO_TRACK:
                                dismissProgressDialog();
                                ToastUtils.showToast("该文件没有视频信息！");
                                break;
                            case ERROR_SRC_DST_SAME_FILE_PATH:
                                dismissProgressDialog();
                                ToastUtils.showToast("源文件路径和目标路径不能相同！");
                                break;
                            case ERROR_LOW_MEMORY:
                                dismissProgressDialog();
                                ToastUtils.showToast("手机内存不足，无法对该视频进行时光倒流！");
                                break;
                            default:
                                dismissProgressDialog();
                                ToastUtils.showToast("无法获取文件请稍后重试！");
                        }
                    }
                });
            }

            @Override
            public void onSaveVideoCanceled() {
            }

            @Override
            public void onProgressUpdate(float percentage) {
            }
        });*/
    }

    private void uploadManager(String path, String mName, String token) {
        uploadManager.put(path, mName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                dismissProgressDialog();
                if (info.isOK()) {
                    if (isRed.equals("yes")) {//拼手气红包
                        EventBus.getDefault().post(new SettingRedEvent(key, String.valueOf(type), mUrlTv.getText().toString()));
                    } else {//普通红包
                        EventBus.getDefault().post(new SettingOrdinaryRedEvent(key, String.valueOf(type), mUrlTv.getText().toString()));
                    }
                    SettingRedActivity.this.finish();
                } else {
                    ToastUtils.showToast("上传失败请稍后重试");
                }
            }
        }, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SettingRedActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(SettingRedUrlEvent event) {
        mUrlTv.setText(event.url);
    }

    private int getEncodingBitrateLevel(int position) {
        return RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[position];
    }
}
