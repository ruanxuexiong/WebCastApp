package com.android.nana.qiniu;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.nana.R;
import com.android.nana.base.BaseActivity;
import com.android.nana.connect.Constants;
import com.android.nana.eventBus.OpenVideoPathEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CustomProgressDialog;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by lenovo on 2018/11/30.
 */

public class VideoTrimActivity extends BaseActivity {

    private static final String TAG = "VideoTrimActivity";

    private static final int SLICE_COUNT = 8;

    private PLShortVideoTrimmer mShortVideoTrimmer;
    private PLMediaFile mMediaFile;

    private LinearLayout mFrameListView;
    private View mHandlerLeft;
    private View mHandlerRight;

    private CustomProgressDialog mProcessingDialog;
    private VideoView mPreview;

    private long mSelectedBeginMs;
    private long mSelectedEndMs;
    private long mDurationMs;

    private int mVideoFrameCount;
    private int mSlicesTotalLength;

    private Handler mHandler = new Handler();
    private TextView mBackTv, mAction, mTitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTrimmer.cancelTrim();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTrackPlayProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShortVideoTrimmer != null) {
            mShortVideoTrimmer.destroy();
        }
        if (mMediaFile != null) {
            mMediaFile.release();
        }
    }

    private void stopTrackPlayProgress() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void play() {
        if (mPreview != null) {
            mPreview.seekTo((int) mSelectedBeginMs);
            mPreview.start();
            startTrackPlayProgress();
        }
    }

    private void startTrackPlayProgress() {
        stopTrackPlayProgress();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPreview.getCurrentPosition() >= mSelectedEndMs) {
                    mPreview.seekTo((int) mSelectedBeginMs);
                }
                mHandler.postDelayed(this, 100);
            }
        }, 100);
    }


    @Override
    public void initView() {
        TextView duration = findViewById(R.id.duration);
        mPreview = findViewById(R.id.preview);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitletv = findViewById(R.id.tv_title);
        mAction = findViewById(R.id.toolbar_right_2);
        mAction.setText("发送");
        mAction.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mTitletv.setText("视频剪辑");

        String videoPath = getIntent().getStringExtra("video_path");


        mShortVideoTrimmer = new PLShortVideoTrimmer(this, videoPath, Constants.TRIM_FILE_PATH);
        mMediaFile = new PLMediaFile(videoPath);

        mSelectedEndMs = mDurationMs = mMediaFile.getDurationMs();
        duration.setText("时长: " + formatTime(mDurationMs));
        Log.i(TAG, "video duration: " + mDurationMs);

        mVideoFrameCount = mMediaFile.getVideoFrameCount(false);
        Log.i(TAG, "video frame count: " + mVideoFrameCount);

        mPreview.setVideoPath(videoPath);
        mPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play();
            }
        });
        initVideoFrameList();
    }

    private String formatTime(long timeMs) {
        return String.format(Locale.CHINA, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs),
                TimeUnit.MILLISECONDS.toSeconds(timeMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }


    private void initVideoFrameList() {
        mFrameListView = findViewById(R.id.video_frame_list);
        mHandlerLeft = findViewById(R.id.handler_left);
        mHandlerRight = findViewById(R.id.handler_right);

        mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;
                updateHandlerLeftPosition(finalX);

                if (action == MotionEvent.ACTION_UP) {
                    calculateRange();
                }

                return true;
            }
        });

        mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;
                updateHandlerRightPosition(finalX);

                if (action == MotionEvent.ACTION_UP) {
                    calculateRange();
                }

                return true;
            }
        });

        mFrameListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFrameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int sliceEdge = mFrameListView.getWidth() / SLICE_COUNT;
                mSlicesTotalLength = sliceEdge * SLICE_COUNT;
                Log.i(TAG, "slice edge: " + sliceEdge);
                final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());

                new AsyncTask<Void, PLVideoFrame, Void>() {
                    @Override
                    protected Void doInBackground(Void... v) {
                        for (int i = 0; i < SLICE_COUNT; ++i) {
                            PLVideoFrame frame = mMediaFile.getVideoFrameByTime((long) ((1.0f * i / SLICE_COUNT) * mDurationMs), true, sliceEdge, sliceEdge);
                            publishProgress(frame);
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(PLVideoFrame... values) {
                        super.onProgressUpdate(values);
                        PLVideoFrame frame = values[0];
                        if (frame != null) {
                            View root = LayoutInflater.from(VideoTrimActivity.this).inflate(R.layout.frame_item, null);

                            int rotation = frame.getRotation();
                            ImageView thumbnail = root.findViewById(R.id.thumbnail);
                            thumbnail.setImageBitmap(frame.toBitmap());
                            thumbnail.setRotation(rotation);
                            FrameLayout.LayoutParams thumbnailLP = (FrameLayout.LayoutParams) thumbnail.getLayoutParams();
                            if (rotation == 90 || rotation == 270) {
                                thumbnailLP.leftMargin = thumbnailLP.rightMargin = (int) px;
                            } else {
                                thumbnailLP.topMargin = thumbnailLP.bottomMargin = (int) px;
                            }
                            thumbnail.setLayoutParams(thumbnailLP);

                            LinearLayout.LayoutParams rootLP = new LinearLayout.LayoutParams(sliceEdge, sliceEdge);
                            mFrameListView.addView(root, rootLP);
                        }
                    }
                }.execute();
            }
        });
    }

    private void updateHandlerLeftPosition(float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerLeft.getLayoutParams();
        if ((movedPosition + mHandlerLeft.getWidth()) > mHandlerRight.getX()) {
            lp.leftMargin = (int) (mHandlerRight.getX() - mHandlerLeft.getWidth());
        } else if (movedPosition < 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerLeft.setLayoutParams(lp);
    }

    private void calculateRange() {
        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        beginPercent = clamp(beginPercent);
        endPercent = clamp(endPercent);

        Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);

        mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        mSelectedEndMs = (long) (endPercent * mDurationMs);

        Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);
        updateRangeText();
        play();
    }

    private void updateHandlerRightPosition(float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerRight.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (movedPosition < (mHandlerLeft.getX() + mHandlerLeft.getWidth())) {
            lp.leftMargin = (int) (mHandlerLeft.getX() + mHandlerLeft.getWidth());
        } else if ((movedPosition + (mHandlerRight.getWidth() / 2)) > (mFrameListView.getX() + mSlicesTotalLength)) {
            lp.leftMargin = (int) ((mFrameListView.getX() + mSlicesTotalLength) - (mHandlerRight.getWidth() / 2));
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerRight.setLayoutParams(lp);
    }

    private float clamp(float origin) {
        if (origin < 0) {
            return 0;
        }
        if (origin > 1) {
            return 1;
        }
        return origin;
    }

    private void updateRangeText() {
        TextView range = (TextView) findViewById(R.id.range);
        range.setText("剪裁范围: " + formatTime(mSelectedBeginMs) + " - " + formatTime(mSelectedEndMs));
    }

    @Override
    public void bindEvent() {
        mBackTv.setOnClickListener(this);
        mAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_right_2:
                mProcessingDialog.show();
                PLShortVideoTrimmer.TRIM_MODE mode = ((RadioButton) findViewById(R.id.mode_fast)).isChecked() ? PLShortVideoTrimmer.TRIM_MODE.FAST : PLShortVideoTrimmer.TRIM_MODE.ACCURATE;
                mShortVideoTrimmer.trim(mSelectedBeginMs, mSelectedEndMs, mode, new PLVideoSaveListener() {
                    @Override
                    public void onSaveVideoSuccess(String path) {
                        mProcessingDialog.dismiss();

                        VideoTrimActivity.this.finish();
                        EventBus.getDefault().post(new OpenVideoPathEvent(path));
                      //  RxBus.getDefault().post(new CloseVideoPathEvent());

                       /* Intent intent = new Intent(VideoTrimActivity.this,VideoEditActivity.class);
                        intent.putExtra("path",path);
                        startActivity(intent);*/
                    }

                    @Override
                    public void onSaveVideoFailed(final int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProcessingDialog.dismiss();
                                ToastUtils.showToast(errorCode);
                            }
                        });
                    }

                    @Override
                    public void onSaveVideoCanceled() {
                        mProcessingDialog.dismiss();
                    }

                    @Override
                    public void onProgressUpdate(final float percentage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProcessingDialog.setProgress((int) (100 * percentage));
                            }
                        });
                    }
                });
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
