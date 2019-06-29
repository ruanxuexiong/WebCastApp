package com.android.nana.find.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.common.BaseApplication;
import com.android.nana.R;
import com.android.nana.find.bean.Moment;
import com.android.nana.friend.AlbumDetailsActivity;
import com.android.nana.nestlistview.NestFullListView;
import com.android.nana.nestlistview.NestFullListViewAdapter;
import com.android.nana.nestlistview.NestFullViewHolder;
import com.android.nana.user.weight.VideoPlayerController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import java.util.ArrayList;

import static com.android.nana.R.drawable.icon_open;

/**
 * Created by lenovo on 2019/2/27.
 */

public class PhotoWallAdapter extends BaseAdapter {

    private Context mContext;
    private RelativeLayout mImage1Rl, mImage1R2, mImage1R3;
    private ImageView mRl1ImgIv, mRl1ImgRedIv, mRl1ImgRedMultiIv;
    private ImageView mRl2ImgIv, mRl2ImgRedIv, mRl2ImgRedMultiIv;
    private ImageView mRl3ImgIv, mRl3ImgRedIv, mRl3ImgRedMultiIv;
    private ImageView mImgIv4, mImgIv5, mImgIv6, mImgIv7, mImgIv8, mImgIv9;
    private ImageView mImgIv10, mImgIv11, mImgIv12, mImgIv13, mImgIv14, mImgIv15;
    private ImageView mImgIv16, mImgIv17, mImgIv18;
    private NiceVideoPlayer mVideoPlayer1, mVideoPlayer2;
    private VideoPlayerController mVideoPlayerController;
    private ImageView mRedIv16, mRedIv17, mRedIv18;
    private ImageView mRedIv4, mRedIv5, mRedIv6, mRedIv7, mRedIv8, mRedIv9;
    private ImageView mRedIv10, mRedIv11, mRedIv12, mRedIv13, mRedIv14, mRedIv15;

    private ImageView mMulti1Iv4, mMulti1Iv5, mMulti1Iv6, mMulti1Iv7, mMulti1Iv8, mMulti1Iv9;
    private ImageView mMulti1Iv10, mMulti1Iv11, mMulti1Iv12, mMulti1Iv13, mMulti1Iv14, mMulti1Iv15;
    private ImageView mMulti1Iv16, mMulti1Iv17, mMulti1Iv18;
    private ArrayList<ArrayList<Moment.Moments>> mDataList;
    private String mUserId;
    private RelativeLayout mVideoRl, m11Rl, m12Rl;

    public PhotoWallAdapter(Context mContext, ArrayList<ArrayList<Moment.Moments>> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mUserId = BaseApplication.getInstance().getCustomerId(mContext);
    }

    @Override
    public int getCount() {
        if (null == mDataList) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_phot_all, null);
            holder = new ViewHolder();
            holder.mView = convertView.findViewById(R.id.photo_view);
            holder.mListView = convertView.findViewById(R.id.list_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.mView.setVisibility(View.VISIBLE);
        } else {
            holder.mView.setVisibility(View.GONE);
        }

        ArrayList<Moment.Moments> mListData = mDataList.get(position);
        holder.mListView.setAdapter(new NestFullListViewAdapter<Moment.Moments>(R.layout.item_photo_wall, mListData) {
            @Override
            public void onBind(int pos, final Moment.Moments moments, NestFullViewHolder holder) {

                mImage1Rl = holder.getView(R.id.rl_image1);
                mRl1ImgIv = holder.getView(R.id.iv_rl1_img);
                mRl1ImgRedIv = holder.getView(R.id.iv_rl1_red);
                mRl1ImgRedMultiIv = holder.getView(R.id.iv_rl1_multi);

                mImage1R2 = holder.getView(R.id.rl_image2);
                mRl2ImgIv = holder.getView(R.id.iv_rl2_img);
                mRl2ImgRedIv = holder.getView(R.id.iv_rl2_red);
                mRl2ImgRedMultiIv = holder.getView(R.id.iv_rl2_multi);

                mImage1R3 = holder.getView(R.id.rl_image3);
                mRl3ImgIv = holder.getView(R.id.iv_rl3_img);
                mRl3ImgRedIv = holder.getView(R.id.iv_rl3_red);
                mRl3ImgRedMultiIv = holder.getView(R.id.iv_rl3_multi);

                mImgIv4 = holder.getView(R.id.iv_4_img);
                mImgIv5 = holder.getView(R.id.iv_5_img);
                mImgIv6 = holder.getView(R.id.iv_6_img);
                mImgIv7 = holder.getView(R.id.iv_7_img);
                mImgIv8 = holder.getView(R.id.iv_8_img);
                mImgIv9 = holder.getView(R.id.iv_9_img);

                mRedIv4 = holder.getView(R.id.iv_4_red);
                mRedIv5 = holder.getView(R.id.iv_5_red);
                mRedIv6 = holder.getView(R.id.iv_6_red);
                mRedIv7 = holder.getView(R.id.iv_7_red);
                mRedIv8 = holder.getView(R.id.iv_8_red);
                mRedIv9 = holder.getView(R.id.iv_9_red);

                mRedIv10 = holder.getView(R.id.iv_10_red);
                mRedIv11 = holder.getView(R.id.iv_11_red);
                mRedIv12 = holder.getView(R.id.iv_12_red);
                mRedIv13 = holder.getView(R.id.iv_13_red);
                mRedIv14 = holder.getView(R.id.iv_14_red);
                mRedIv15 = holder.getView(R.id.iv_15_red);
                mRedIv16 = holder.getView(R.id.iv_16_red);
                mRedIv17 = holder.getView(R.id.iv_17_red);
                mRedIv18 = holder.getView(R.id.iv_18_red);

                mMulti1Iv4 = holder.getView(R.id.iv_4_multi1);
                mMulti1Iv5 = holder.getView(R.id.iv_5_multi1);
                mMulti1Iv6 = holder.getView(R.id.iv_6_multi1);
                mMulti1Iv7 = holder.getView(R.id.iv_7_multi1);
                mMulti1Iv8 = holder.getView(R.id.iv_8_multi1);
                mMulti1Iv9 = holder.getView(R.id.iv_9_multi1);

                mMulti1Iv10 = holder.getView(R.id.iv_10_multi1);
                mMulti1Iv11 = holder.getView(R.id.iv_11_multi1);
                mMulti1Iv12 = holder.getView(R.id.iv_12_multi1);
                mMulti1Iv13 = holder.getView(R.id.iv_13_multi1);
                mMulti1Iv14 = holder.getView(R.id.iv_14_multi1);
                mMulti1Iv15 = holder.getView(R.id.iv_15_multi1);
                mMulti1Iv16 = holder.getView(R.id.iv_16_multi1);
                mMulti1Iv17 = holder.getView(R.id.iv_17_multi1);
                mMulti1Iv18 = holder.getView(R.id.iv_18_multi1);

                mImgIv10 = holder.getView(R.id.iv_10_img);
                mImgIv11 = holder.getView(R.id.iv_11_img);
                mImgIv12 = holder.getView(R.id.iv_12_img);
                mImgIv13 = holder.getView(R.id.iv_13_img);
                mImgIv14 = holder.getView(R.id.iv_14_img);
                mImgIv15 = holder.getView(R.id.iv_15_img);
                mImgIv16 = holder.getView(R.id.iv_16_img);
                mImgIv17 = holder.getView(R.id.iv_17_img);
                mImgIv18 = holder.getView(R.id.iv_18_img);


                mVideoPlayer1 = holder.getView(R.id.video_player_1);
                mVideoPlayer2 = holder.getView(R.id.video_player_2);
                mVideoRl = holder.getView(R.id.rl_video);
                m11Rl = holder.getView(R.id.rl_11);
                m12Rl = holder.getView(R.id.rl_12);

                //获取屏幕宽度
                WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                double width = manager.getDefaultDisplay().getWidth();
                double three = (width - 2) / 3;//得到3张图宽的比例
                double paramsWidth = Math.floor(three);//一张图的宽度
                double number = (paramsWidth * 2) + 1;//得到大图宽的比例

                ViewGroup.LayoutParams params1 = mImage1Rl.getLayoutParams();
                ViewGroup.LayoutParams params2 = mImage1R2.getLayoutParams();
                ViewGroup.LayoutParams params3 = mImage1R3.getLayoutParams();
                ViewGroup.LayoutParams params4 = mVideoRl.getLayoutParams();
                ViewGroup.LayoutParams params5 = m11Rl.getLayoutParams();
                ViewGroup.LayoutParams params6 = m12Rl.getLayoutParams();


                if (pos == 0) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRl1ImgRedIv.setVisibility(View.VISIBLE);
                    } else {
                        mRl1ImgRedIv.setVisibility(View.GONE);
                    }

                    if (moments.getIsReceived().equals("1")) {
                        mRl1ImgRedIv.setVisibility(View.GONE);
                    }


                    params1.height = (int) paramsWidth;
                    params1.width = (int) paramsWidth;
                    mRl1ImgIv.setMaxWidth((int) paramsWidth);
                    mRl1ImgIv.setMaxHeight((int) paramsWidth);

                    mImage1Rl.setLayoutParams(params1);

                    if (moments.getType().equals("2")) {
                        mRl1ImgRedMultiIv.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mRl1ImgIv);
                    } else {
                        mRl1ImgRedMultiIv.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mRl1ImgIv);
                    }

                    mRl1ImgIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 2) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRl2ImgRedIv.setVisibility(View.VISIBLE);
                    } else {
                        mRl2ImgRedIv.setVisibility(View.GONE);
                    }

                    if (moments.getIsReceived().equals("1")) {
                        mRl2ImgRedIv.setVisibility(View.GONE);
                    }

                    params2.height = (int) paramsWidth;
                    params2.width = (int) paramsWidth;
                    mRl2ImgIv.setMaxWidth((int) paramsWidth);
                    mRl2ImgIv.setMaxHeight((int) paramsWidth);
                    mImage1R2.setLayoutParams(params2);

                    if (moments.getType().equals("2")) {
                        mRl2ImgRedMultiIv.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mRl2ImgIv);
                    } else {
                        mRl2ImgRedMultiIv.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                              /*  .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mRl2ImgIv);
                    }

                    mRl2ImgIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 1) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRl3ImgRedIv.setVisibility(View.VISIBLE);
                    } else {
                        mRl3ImgRedIv.setVisibility(View.GONE);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRl3ImgRedIv.setVisibility(View.GONE);
                    }

                    params3.height = (int) number;
                    params3.width = (int) number;
                    mImage1R3.setLayoutParams(params3);
                    mVideoPlayerController = new VideoPlayerController(mContext);
                    if (moments.getType().equals("2")) {
                        mVideoPlayer1.setVisibility(View.VISIBLE);

                        mVideoPlayer1.setController(mVideoPlayerController);
                        mVideoPlayerController.setVisibility(View.VISIBLE);
                        mRl3ImgRedMultiIv.setImageResource(R.drawable.icon_open);

                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).into(mVideoPlayerController.imageView());
                        mVideoPlayer1.continueFromLastPosition(false);
                        mVideoPlayer1.getTcpSpeed();
                        mVideoPlayer1.setUp(moments.getUserArticlePictures().get(0).getPath(), null);
                        mVideoPlayer1.start();
                        mVideoPlayerController.mVoiceIv.setVisibility(View.GONE);
                        mRl3ImgRedMultiIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mRl3ImgRedMultiIv.getTag().toString().equals("select")) {
                                    mRl3ImgRedMultiIv.setTag("unSelect");
                                    mVideoPlayer1.setVolume(0);
                                    mRl3ImgRedMultiIv.setImageResource(R.drawable.icon_mute);
                                } else {
                                    mRl3ImgRedMultiIv.setTag("select");
                                    mVideoPlayer1.setVolume(10);
                                    mRl3ImgRedMultiIv.setImageResource(icon_open);
                                }
                            }
                        });

                        mVideoPlayerController.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                                intent.putExtra("id", moments.getId());
                                intent.putExtra("mid", mUserId);
                                mContext.startActivity(intent);
                            }
                        });

                    } else {
                        mRl3ImgRedMultiIv.setImageResource(R.drawable.icon_multi);
                        mVideoPlayer1.setVisibility(View.GONE);
                        mVideoPlayerController.setVisibility(View.GONE);
                        RequestOptions options = new RequestOptions()
                                .override((int) number, (int) number)
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);


                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).apply(options).into(mRl3ImgIv);


                        mRl3ImgIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                                intent.putExtra("id", moments.getId());
                                intent.putExtra("mid", mUserId);
                                mContext.startActivity(intent);
                            }
                        });
                    }

                } else if (pos == 3) {

                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv4.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv4.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv4.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getType().equals("2")) {
                        mMulti1Iv4.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv4);
                    } else {
                        mMulti1Iv4.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv4);
                    }

                    mImgIv4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 4) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv5.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv5.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv5.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv5.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv5);
                    } else {
                        mMulti1Iv5.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv5);
                    }

                    mImgIv5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 5) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv6.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv6.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv6.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv6.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv6);
                    } else {
                        mMulti1Iv6.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv6);
                    }

                    mImgIv6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 6) {


                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv7.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv7.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv7.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getType().equals("2")) {
                        mMulti1Iv7.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv7);
                    } else {
                        mMulti1Iv7.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv7);
                    }

                    mImgIv7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 7) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv8.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv8.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv8.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv8.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv8);
                    } else {
                        mMulti1Iv8.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                              /*  .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv8);
                    }

                    mImgIv8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 8) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv9.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv9.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv9.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv9.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv9);
                    } else {
                        mMulti1Iv9.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv9);
                    }

                    mImgIv9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 9) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv10.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv10.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv10.setImageResource(R.drawable.icon_stance);
                    }

                    params4.width = (int) number;
                    params4.height = (int) number;
                    mVideoRl.setLayoutParams(params4);

                    if (moments.getType().equals("2")) {

                        mVideoPlayer2.setVisibility(View.VISIBLE);
                        mVideoPlayerController = new VideoPlayerController(mContext);
                        mVideoPlayer2.setController(mVideoPlayerController);
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).into(mVideoPlayerController.imageView());
                        mVideoPlayer2.continueFromLastPosition(false);
                        mVideoPlayer2.getTcpSpeed();
                        mVideoPlayer2.setUp(moments.getUserArticlePictures().get(0).getPath(), null);
                        mVideoPlayer2.start();
                        mVideoPlayerController.mVoiceIv.setVisibility(View.GONE);

                        mMulti1Iv10.setImageResource(R.drawable.icon_open);
                        mMulti1Iv10.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mMulti1Iv10.getTag().toString().equals("select")) {
                                    mMulti1Iv10.setTag("unSelect");
                                    mVideoPlayer2.setVolume(0);
                                    mMulti1Iv10.setImageResource(R.drawable.icon_mute);
                                } else {
                                    mMulti1Iv10.setTag("select");
                                    mVideoPlayer2.setVolume(10);
                                    mMulti1Iv10.setImageResource(icon_open);
                                }
                            }
                        });

                        mVideoPlayer2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                                intent.putExtra("id", moments.getId());
                                intent.putExtra("mid", mUserId);
                                mContext.startActivity(intent);
                            }
                        });

                    } else {
                        mVideoPlayer2.setVisibility(View.GONE);
                        mMulti1Iv10.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .override((int) number, (int) number)
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;

                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv10);

                        mImgIv10.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                                intent.putExtra("id", moments.getId());
                                intent.putExtra("mid", mUserId);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                } else if (pos == 10) {

                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv11.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv11.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv11.setImageResource(R.drawable.icon_stance);
                    }

                    params5.height = (int) paramsWidth;
                    params5.width = (int) paramsWidth;
                    mImgIv11.setMaxWidth((int) paramsWidth);
                    mImgIv11.setMaxHeight((int) paramsWidth);
                    m11Rl.setLayoutParams(params5);

                    if (moments.getType().equals("2")) {
                        mMulti1Iv11.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv11);

                    } else {
                        mMulti1Iv11.setImageResource(R.drawable.icon_multi);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv11);
                    }

                    mImgIv11.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 11) {

                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv12.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv12.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv12.setImageResource(R.drawable.icon_stance);
                    }

                    params6.height = (int) paramsWidth;
                    params6.width = (int) paramsWidth;
                    mImgIv12.setMaxWidth((int) paramsWidth);
                    mImgIv12.setMaxHeight((int) paramsWidth);
                    m12Rl.setLayoutParams(params6);

                    if (moments.getType().equals("2")) {
                        mMulti1Iv12.setImageResource(R.drawable.icon_find_video);


                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv12);

                    } else {
                        mMulti1Iv12.setImageResource(R.drawable.icon_multi);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv12);

                    }

                    mImgIv12.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 12) {

                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv13.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv13.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv13.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv13.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv13);

                    } else {
                        mMulti1Iv13.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv13);
                    }

                    mImgIv13.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 13) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv14.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv14.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv14.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv14.setImageResource(R.drawable.icon_find_video);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                              /*  .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv14);

                    } else {
                        mMulti1Iv14.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv14);
                    }

                    mImgIv14.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 14) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv15.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv15.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv15.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv15.setImageResource(R.drawable.icon_find_video);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv15);
                    } else {
                        mMulti1Iv15.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv15);
                    }

                    mImgIv15.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 15) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv16.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv16.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv16.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv16.setImageResource(R.drawable.icon_find_video);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv16);
                    } else {
                        mMulti1Iv16.setImageResource(R.drawable.icon_multi);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv16);
                    }

                    mImgIv16.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 16) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv17.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv17.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv17.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv17.setImageResource(R.drawable.icon_find_video);

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv17);
                    } else {
                        mMulti1Iv17.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv17);
                    }

                    mImgIv17.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (pos == 17) {
                    if (moments.getShowBound().equals("1")) {//1显示红包
                        mRedIv18.setImageResource(R.drawable.icon_find_red);
                    } else {
                        mRedIv18.setImageResource(R.drawable.icon_stance);
                    }

                    if (moments.getIsReceived().equals("1")) {//红包已领完
                        mRedIv18.setImageResource(R.drawable.icon_stance);
                    }
                    if (moments.getType().equals("2")) {
                        mMulti1Iv18.setImageResource(R.drawable.icon_find_video);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                               /* .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPicture_url()).apply(options).into(mImgIv18);
                    } else {
                        mMulti1Iv18.setImageResource(R.drawable.icon_multi);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.img_df)
                                .error(R.drawable.img_df)
                                /*.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(true)*/;
                        Glide.with(mContext).load(moments.getUserArticlePictures().get(0).getPath()).apply(options).into(mImgIv18);
                    }

                    mImgIv18.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                            intent.putExtra("id", moments.getId());
                            intent.putExtra("mid", mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                }

            }
        });
        return convertView;
    }

    class ViewHolder {
        private View mView;
        private NestFullListView mListView;
    }
}
