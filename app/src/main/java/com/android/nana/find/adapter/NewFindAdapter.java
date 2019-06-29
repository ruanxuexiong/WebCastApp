package com.android.nana.find.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.connect.Constants;
import com.android.nana.find.ArticleActivity;
import com.android.nana.find.MyLayoutManager;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.find.weight.MyScrollerView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.nestlistview.NestFullListView;
import com.android.nana.nestlistview.NestFullListViewAdapter;
import com.android.nana.nestlistview.NestFullViewHolder;
import com.android.nana.ui.RoundImageView;
import com.android.nana.user.weight.VideoPlayerController;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.util.WeiBoContentTextUtil;
import com.android.nana.widget.ImageBrowserActivity;
import com.android.nana.widget.NestedScrollLinearLayoutManager;
import com.android.nana.widget.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by lenovo on 2018/12/11.
 */

public class NewFindAdapter extends RecyclerView.Adapter<NewFindAdapter.ViewHolder> {


    private Context context;
    private ArrayList<Moment.Moments> mDataList;
    private LayoutInflater inflater = null;
    private FlowListener mListener;
    private boolean isHide = false; //首页附近隐藏
    private CommentsAdapter mAdapter;

    private PercentRelativeLayout mThree2, mFour1, mFour2;
    private LinearLayout mTwoll, mThree1, mFive1, mFive2;
    private RelativeLayout mOnePl;
    private TextView mUrlTv;
    private ImageView mOne1, mOne2, mTwo21, mTwo22;
    private SubsamplingScaleImageView mLongImg;
    private ImageView mThreeIv1, mThreeIv2, mThreeIv3, mThreeIv21, mThreeIv22, mThreeIv23;
    private ImageView mFourIv1, mFourIv2, mFourIv3, mFourIv4;
    private ImageView mFourIv21, mFourIv22, mFourIv23, mFourIv24;
    private ImageView mFiveIv1, mFiveIv2, mFiveIv3, mFiveIv4, mFiveIv5;
    private ImageView mFiveIv21, mFiveIv22, mFiveIv23, mFiveIv24, mFiveIv25;
    private LinearLayout mClearll;
    private TextView mConutTv;
    private RecyclerView mRecyclerView;
    //视频
   // private NiceVideoPlayer mVideoPlayer;
    private VideoPlayerController mVideoPlayerController;
    MyLayoutManager myLayoutManager;

    public NewFindAdapter(Context mContext, ArrayList<Moment.Moments> mDataList, FlowListener mListener, boolean isHide,MyLayoutManager myLayoutManager,RecyclerView mRecyclerView) {
        this.context = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
        this.isHide = isHide;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       this.myLayoutManager=myLayoutManager;
       this.mRecyclerView=mRecyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

     //   holder.mListView.setVisibility(View.GONE);
      //  holder.mVideoPlayer.setVisibility(View.VISIBLE);
     //   holder.mPlayerUrlTv.setVisibility(View.VISIBLE);
      //  holder.video_view.setVisibility(View.VISIBLE);


        NestedScrollLinearLayoutManager layoutManager = new NestedScrollLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.mRecyclerViewA.setLayoutManager(layoutManager);// 布局管理器。
        final Moment.Moments item = mDataList.get(position);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.icon_gif))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        holder.mSimpleDraweeView.setController(controller);

        if (null != item.getComments() && item.getComments().size() > 0) {
           // holder.mHideTv.setVisibility(View.VISIBLE);
        } else {
            holder.mHideTv.setVisibility(View.GONE);
        }

        mAdapter = new CommentsAdapter(context, item.getComments());
        holder.mRecyclerViewA.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if ("".equals(item.getJuli())) {
            holder.mDistanceTv.setVisibility(GONE);
        } else {
            holder.mDistanceTv.setText(item.getJuli());
        }
        if ("".equals(item.getAddress())) {
            holder.mMapTv.setVisibility(GONE);
        } else {
            holder.mMapTv.setText(item.getAddress());
            holder.mMapTv.setTag(position);
            holder.mMapTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAMapLocation(view);
                    }
                }
            });
        }

        if (null != item.getUser()) {
            if (item.getUser().getStatus().equals("1")) {
                holder.mDentyIv.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.icon_authen).into(holder.mDentyIv);
            }
            else if (item.getUser().getStatus().equals("4")){
                holder.mDentyIv.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.mipmap.user_vip).into(holder.mDentyIv);
            }
            else {
                holder.mDentyIv.setVisibility(View.GONE);
            }
            holder.mNameTv.setText(item.getUser().getUsername());
            holder.mTimeTv.setText(item.getTime());
            ImgLoaderManager.getInstance().showImageView(item.getUser().getAvatar(), holder.mAvatarIv);

            holder.mNameTv.setTag(position);
            holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemAvatarClick(view);
                    }
                }
            });
            holder.mTimeTv.setTag(position);
            holder.mTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemAvatarClick(view);
                    }
                }
            });
        }

        if (item.getContent().equals("")) {
            holder.mTextTv.setVisibility(GONE);
        } else {
//            holder.mTextTv.setText(item.getContent());
            holder.mTextTv.setUrlText(item.getContent());
            holder.mTextTv.setTag(position);
            holder.mTextTv.setMovementMethod(ScrollingMovementMethod.getInstance());
            holder.mTextTv.setText(WeiBoContentTextUtil.getWeiBoContent(item.getContent(), context, holder.mTextTv, new WeiBoContentTextUtil.OnClistener() {
                @Override
                public void toUserData(String name) {
                    for (Moment.UserListBean userID : item.getUser_list()) {
                        if (name.equals(userID.getUname())) {

                            Intent intent = new Intent(context, EditDataActivity.class);
                            intent.putExtra("UserId", userID.getTouid());
                            context.startActivity(intent);

                            return;
                        }
                    }

                }

                @Override
                public void toArticle(String name) {
                    for (Moment.Article article : item.getTag_list()) {
                        if (name.equals(article.getTag_name())) {
                            Intent intent = new Intent(context, ArticleActivity.class);
                            intent.putExtra("tagid", article.getTag_id());
                            context.startActivity(intent);
                            return;
                        }
                    }
                }
            }));
            holder.mTextTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mTextTv.isClick) {//用于判断全文收起点击处理
                        holder.mTextTv.isClick = false;
                        holder.mTextTv.setMovementMethod(ScrollingMovementMethod.getInstance());
                    } else {
                        if (mListener != null) {
                            mListener.onItemClick(view);
                        }
                    }
                }
            });
        }

        if (item.getLaudResult().equals("0")) {
            holder.mPeopleNumTv.setText("点赞得现金红包");
        } else {
            holder.mPeopleNumTv.setText(item.getBoundCount() + "人领取");
        }

        if (item.getIsReceived().equals("1")){
            holder.mPeopleNumTv.setText("红包已领完");
        }

        if (item.getShowBound().equals("1")) {
            holder.mRedLl.setVisibility(View.VISIBLE);
            holder.mSimpleDraweeView.setTag(position);
            holder.mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRedClick(v);
                    }
                }
            });
        } else {
            holder.mRedLl.setVisibility(GONE);
        }
        if (item.getLaudResult().equals("1")) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.icon_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.mZanTv.setCompoundDrawables(drawable, null, null, null);
            holder.mZanTv.setTextColor(context.getResources().getColor(R.color.main_blue));
        }


        holder.mNumTv.setVisibility(View.VISIBLE);
        holder.mNumTv.setText(item.getLaudCount() + "人点赞");

        if (Integer.valueOf(item.getComment_count()) > 0) {//动态数
            holder.mCommentNumTv.setVisibility(View.VISIBLE);
            holder.mCommentNumTv.setText(item.getComment_count() + "条评论·");
        } else {
            holder.mCommentNumTv.setVisibility(View.VISIBLE);
            holder.mCommentNumTv.setText("0条评论·");
        }

        if (Integer.valueOf(item.getRepost_count()) > 0) {//分享数
            holder.mRepostNumTv.setVisibility(View.VISIBLE);
            holder.mRepostNumTv.setText(item.getRepost_count() + "次分享·");
        } else {
            holder.mRepostNumTv.setVisibility(View.VISIBLE);
            holder.mRepostNumTv.setText("0次分享·");
        }

        if (Integer.valueOf(item.getView_count()) > 0) {//浏览数
            holder.mViewNum.setVisibility(View.VISIBLE);
            holder.mViewNum.setText(item.getView_count() + "次浏览");
        } else {
            holder.mViewNum.setVisibility(View.VISIBLE);
            holder.mViewNum.setText("0次浏览");
        }

        //点赞
        holder.mZanView.setTag(position);
        holder.mZanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick()) {
                    if (mListener != null) {
                        mListener.onZanClick(view,holder);
                    }
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
            }
        });

        //分享
        holder.mShareView.setTag(position);
        holder.mShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onShareClick(view);
                }
            }
        });

        holder.mConentLl.setTag(position);
        holder.mConentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });

        holder.mAvatarIv.setTag(position);//点击头像
        holder.mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemAvatarClick(view);
                }
            }
        });
        if (item.getType().equals("2")) {
            holder.mListView.setVisibility(View.GONE);
            holder.mVideoPlayer.setVisibility(View.VISIBLE);

        mVideoPlayerController = new VideoPlayerController(context);
        holder.mVideoPlayer.setController(mVideoPlayerController);

        //TextView mPlayerUrlTv = holder.getView(R.id.tv_player_url);
        if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
            holder.mPlayerUrlTv.setVisibility(View.VISIBLE);

            if (item.getSpread_url_type().equals("1")) {
                holder.mPlayerUrlTv.setText("点击立即下载");
                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
            } else if (item.getSpread_url_type().equals("2")) {
                holder.mPlayerUrlTv.setText("点击了解详情");
                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
            } else if (item.getSpread_url_type().equals("3")) {
                holder.mPlayerUrlTv.setText("点击立即购买");
                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
            }else if (item.getSpread_url_type().equals("4")){
                holder. mPlayerUrlTv.setText("点击阅读文章");
                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
            }

            holder. mPlayerUrlTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommonActivity.class);
                    intent.putExtra("title", item.getSpread_url());
                    intent.putExtra("url", item.getSpread_url());
                    context.startActivity(intent);
                }
            });
        }
        else {
            holder.mPlayerUrlTv.setVisibility(View.GONE);
        }


        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        double width = manager.getDefaultDisplay().getWidth();

        ViewGroup.LayoutParams params = holder.mVideoPlayer.getLayoutParams();

        if (Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getHeight()) > Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getWidth())) { //高视频
            if (Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent()))) >= 500) {
                double total = Double.valueOf(width) / 375.00;
                double videoWidth = 237 * Double.valueOf(total);
                params.width = (int) width;
                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
            } else {
                params.width = (int) width;
                params.height = Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
            }
        } else if (Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getWidth()) > Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getHeight())) {
            if (Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent()))) >= 500) {
                params.width = (int) width;
                params.height = (int) 600.0;
            } else {
                params.width = (int) width;
                params.height = Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
            }
        } else {
            double total = Double.valueOf(width) / 375.00;
            double videoWidth = 237 * Double.valueOf(total);
            params.width = (int) width;
            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
        }


        holder. mVideoPlayer.setLayoutParams(params);


        Glide.with(context).load(mDataList.get(position).getUserArticlePictures().get(0).getPicture_url()).into(mVideoPlayerController.imageView());


        holder. mVideoPlayer.continueFromLastPosition(false);
        holder.  mVideoPlayer.getTcpSpeed();


        holder.  mVideoPlayer.setUp(mDataList.get(position).getUserArticlePictures().get(0).getPath(), null);
       // holder.mVideoPlayer.start();
       // if (position==0){
         //   holder.mVideoPlayer.start();
     //   }
        mVideoPlayerController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable.ConstantState drawableCs = mVideoPlayerController.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                if (mVideoPlayerController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                    holder.    mVideoPlayer.setVolume(0);
                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                    SharedPreferencesUtils.setParameter(context, Constants.NEARBY_VOICE, "nearby");
                } else {
                    holder.     mVideoPlayer.setVolume(50);
                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                    SharedPreferencesUtils.removeParameter(context, Constants.NEARBY_VOICE);
                }
            }
        });

        if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
        } else {
            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
        }

        mVideoPlayerController.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPlayView(mDataList.get(position).getUserArticlePictures().get(0).getPath());
                }
            }
        });

        }
        else {
            holder.mPlayerUrlTv.setVisibility(View.GONE);
            holder.mListView.setVisibility(View.VISIBLE);
            holder.mVideoPlayer.setVisibility(View.GONE);
            if (null != mVideoPlayerController) {
                if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                } else {
                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                }
            }

            if (null != holder.mVideoPlayer) {
                holder.mVideoPlayer.release();
            }
            final ArrayList<Moment.Pictures> list = item.getUserArticlePictures();
            if (list == null || list.size() < 1) {
                holder.mListView.setVisibility(GONE);
            } else {
                holder.mListView.setAdapter(new NestFullListViewAdapter<Moment.Pictures>(R.layout.item_img, list) {
                    @Override
                    public void onBind(int pos, Moment.Pictures pictures, NestFullViewHolder holder) {
                        mOnePl = holder.getView(R.id.prl_one);
                        mOne1 = holder.getView(R.id.iv_img_one1);
                        mLongImg = holder.getView(R.id.longImg);
                        mOne2 = holder.getView(R.id.iv_img_one2);
                        mUrlTv = holder.getView(R.id.tv_url);
                        mTwoll = holder.getView(R.id.ll_two);
                        mTwo21 = holder.getView(R.id.iv_img_two21);
                        mTwo22 = holder.getView(R.id.iv_img_two22);

                        mThree1 = holder.getView(R.id.ll_three);
                        mThreeIv1 = holder.getView(R.id.iv_img_three1);
                        mThreeIv2 = holder.getView(R.id.iv_img_three2);
                        mThreeIv3 = holder.getView(R.id.iv_img_three3);

                        mThree2 = holder.getView(R.id.prl_three2);
                        mThreeIv21 = holder.getView(R.id.iv_img_three21);
                        mThreeIv22 = holder.getView(R.id.iv_img_three22);
                        mThreeIv23 = holder.getView(R.id.iv_img_three23);

                        mFour1 = holder.getView(R.id.prl_four);
                        mFourIv1 = holder.getView(R.id.iv_img_four1);
                        mFourIv2 = holder.getView(R.id.iv_img_four2);
                        mFourIv3 = holder.getView(R.id.iv_img_four3);
                        mFourIv4 = holder.getView(R.id.iv_img_four4);

                        mFour2 = holder.getView(R.id.prl_four2);
                        mFourIv21 = holder.getView(R.id.iv_img_four21);
                        mFourIv22 = holder.getView(R.id.iv_img_four22);
                        mFourIv23 = holder.getView(R.id.iv_img_four23);
                        mFourIv24 = holder.getView(R.id.iv_img_four24);

                        mFive1 = holder.getView(R.id.prl_five);
                        mFiveIv1 = holder.getView(R.id.iv_img_five);
                        mFiveIv2 = holder.getView(R.id.iv_img_five2);
                        mFiveIv3 = holder.getView(R.id.iv_img_five3);
                        mFiveIv4 = holder.getView(R.id.iv_img_five4);
                        mFiveIv5 = holder.getView(R.id.iv_img_five5);

                        mFive2 = holder.getView(R.id.prl_five1);
                        mFiveIv21 = holder.getView(R.id.iv_img_five21);
                        mFiveIv22 = holder.getView(R.id.iv_img_five22);
                        mFiveIv23 = holder.getView(R.id.iv_img_five23);
                        mFiveIv24 = holder.getView(R.id.iv_img_five24);
                        mFiveIv25 = holder.getView(R.id.iv_img_five25);
                        mClearll = holder.getView(R.id.ll_clear);
                        mConutTv = holder.getView(R.id.tv_count_img);


//                        mFive1.setVisibility(View.GONE);
//                        mFour1.setVisibility(View.GONE);
//                        mThree2.setVisibility(View.GONE);
//                        mThree1.setVisibility(View.GONE);
//                        mTwoll.setVisibility(View.GONE);

                        if (list.size() == 1) {
                            mOnePl.setVisibility(View.VISIBLE);
                            if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
                                mUrlTv.setVisibility(View.VISIBLE);

                                if (item.getSpread_url_type().equals("1")) {
                                    mUrlTv.setText("点击立即下载");
                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
                                } else if (item.getSpread_url_type().equals("2")) {
                                    mUrlTv.setText("点击了解详情");
                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
                                } else if (item.getSpread_url_type().equals("3")) {
                                    mUrlTv.setText("点击立即购买");
                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
                                }else if (item.getSpread_url_type().equals("4")){
                                    mUrlTv.setText("点击阅读文章");
                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
                                }


                                mUrlTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, CommonActivity.class);
                                        intent.putExtra("title", item.getSpread_url());
                                        intent.putExtra("url", item.getSpread_url());
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mOne1.setVisibility(View.VISIBLE);
                                mOne2.setVisibility(GONE);
                                if (Integer.valueOf(list.get(0).getHeight()) >= 500) {
                                    mLongImg.setVisibility(View.VISIBLE);
                                    mOne1.setVisibility(GONE);
                                    Glide.with(context)
                                            .download(list.get(0).getPath())
                                            .into(new SimpleTarget<File>() {
                                                @Override
                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                    super.onLoadFailed(errorDrawable);
                                                }

                                                @Override
                                                public void onResourceReady(File resource, Transition<? super File> transition) {
                                                    mLongImg.setQuickScaleEnabled(true);
                                                    mLongImg.setZoomEnabled(true);
                                                    mLongImg.setPanEnabled(true);
                                                    mLongImg.setDoubleTapZoomDuration(500);
                                                    mLongImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                                    mLongImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                                    mLongImg.setImage(ImageSource.uri(resource.getAbsolutePath()), new ImageViewState(0, new PointF(0, 0), 0));
                                                }
                                            });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne1);
                                }

                                mLongImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_LONG_IMG_URLS, "111");
                                        context.startActivity(intent);
                                    }
                                });

                                mOne1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        context.startActivity(intent);
                                    }
                                });
                            } else {
                                mOne1.setVisibility(GONE);
                                mOne2.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne2);

                                mOne2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        context.startActivity(intent);
                                    }
                                });
                            }

                        } else if (list.size() == 2) {
                            mTwoll.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mTwo21);
                                mTwo21.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        context.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mTwo22);
                                mTwo22.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        } else if (list.size() == 3) {
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mThree2.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv21);
                                    mThreeIv21.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv22);

                                    mThreeIv22.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv23);

                                    mThreeIv23.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mThree1.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv1);

                                    mThreeIv1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv2);

                                    mThreeIv2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv3);
                                    mThreeIv3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } else if (list.size() == 4) {
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mFour1.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv1);

                                    mFourIv1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv2);
                                    mFourIv2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 2) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv3);

                                    mFourIv3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 3) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv4);

                                    mFourIv4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mFour2.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv21);

                                    mFourIv21.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv22);

                                    mFourIv22.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 2) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv23);

                                    mFourIv23.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            context.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 3) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv24);

                                    mFourIv24.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (Moment.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } else if (list.size() == 5) {
                            mFive1.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFiveIv1);

                                mFiveIv1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFiveIv2);

                                mFiveIv2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFiveIv3);

                                mFiveIv3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFiveIv4);

                                mFiveIv4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 4) {
                                ImgLoaderManager.getInstance().showImageView(list.get(4).getPath(), mFiveIv5);

                                mFiveIv5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 4);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            mFive2.setVisibility(View.VISIBLE);
                            mClearll.getBackground().setAlpha(90);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv21);

                                mFiveIv21.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv22);

                                mFiveIv22.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv23);

                                mFiveIv23.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv24);

                                mFiveIv24.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (Moment.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if (pos == 4) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv25);
                            }
                        }
                        mConutTv.setText("+" + String.valueOf(Integer.valueOf(list.size()) - 5));

                        mFiveIv25.setTag(position);
                        mFiveIv25.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mListener != null) {
                                    mListener.onItemClick(view);
                                }
                            }
                        });
                    }
                });

            }
        }

//        if (item.getType().equals("2")) {
//            final ArrayList<Moment.Pictures> list = item.getUserArticlePictures();
//            if (list.size() >= 1) {
//                holder.mListView.setAdapter(new NestFullListViewAdapter<Moment.Pictures>(R.layout.item_video, list) {
//                    @Override
//                    public void onBind(final int pos, final Moment.Pictures pictures, NestFullViewHolder holder2) {

                   //    holder.mVideoPlayer = holder.getView(R.id.video_player);
//                        mVideoPlayerController = new VideoPlayerController(context);
//                      holder.mVideoPlayer.setController(mVideoPlayerController);
//
//                        TextView mPlayerUrlTv = holder2.getView(R.id.tv_player_url);
//                        if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
//                            mPlayerUrlTv.setVisibility(View.VISIBLE);
//
//                            if (item.getSpread_url_type().equals("1")) {
//                                mPlayerUrlTv.setText("点击立即下载");
//                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
//                            } else if (item.getSpread_url_type().equals("2")) {
//                                mPlayerUrlTv.setText("点击了解详情");
//                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
//                            } else if (item.getSpread_url_type().equals("3")) {
//                                mPlayerUrlTv.setText("点击立即购买");
//                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
//                            }else if (item.getSpread_url_type().equals("4")){
//                                mPlayerUrlTv.setText("点击阅读文章");
//                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
//                            }
//
//                            mPlayerUrlTv.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(context, CommonActivity.class);
//                                    intent.putExtra("title", item.getSpread_url());
//                                    intent.putExtra("url", item.getSpread_url());
//                                    context.startActivity(intent);
//                                }
//                            });
//                        }
//
//                        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//                        double width = manager.getDefaultDisplay().getWidth();
//
//                        ViewGroup.LayoutParams params = holder.mVideoPlayer.getLayoutParams();
//
//                        if (Integer.valueOf(pictures.getHeight()) > Integer.valueOf(pictures.getWidth())) { //高视频
//                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
//                                double total = Double.valueOf(width) / 375.00;
//                                double videoWidth = 237 * Double.valueOf(total);
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
//                            }
//                        } else if (Integer.valueOf(pictures.getWidth()) > Integer.valueOf(pictures.getHeight())) {
//                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
//                                params.width = (int) width;
//                                params.height = (int) 600.0;
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
//                            }
//                        } else {
//                            double total = Double.valueOf(width) / 375.00;
//                            double videoWidth = 237 * Double.valueOf(total);
//                            params.width = (int) width;
//                            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
//                        }
//
//
//                       holder. mVideoPlayer.setLayoutParams(params);
//
//
//                        Glide.with(context).load(pictures.getPicture_url()).into(mVideoPlayerController.imageView());
//
//
//                        holder. mVideoPlayer.continueFromLastPosition(false);
//                        holder.  mVideoPlayer.getTcpSpeed();
//                        holder.  mVideoPlayer.setUp(pictures.getPath(), null);
//                       // mVideoPlayer.start();
//
//                        mVideoPlayerController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Drawable.ConstantState drawableCs = mVideoPlayerController.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
//                                if (mVideoPlayerController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
//                                    holder.    mVideoPlayer.setVolume(0);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                                    SharedPreferencesUtils.setParameter(context, Constants.NEARBY_VOICE, "nearby");
//                                } else {
//                                    holder.     mVideoPlayer.setVolume(50);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                                    SharedPreferencesUtils.removeParameter(context, Constants.NEARBY_VOICE);
//                                }
//                            }
//                        });
//
//                        if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                        } else {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                        }
//
//                        mVideoPlayerController.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (mListener != null) {
//                                    mListener.onPlayView(list.get(pos).getPath());
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }


//        if (item.getType().equals("2")) {
//            final ArrayList<Moment.Pictures> list = item.getUserArticlePictures();
//            if (list.size() >= 1) {
//           //     holder.mListView.setAdapter(new NestFullListViewAdapter<Moment.Pictures>(R.layout.item_video, list) {
//           //         @Override
//          //          public void onBind(final int pos, final Moment.Pictures pictures, NestFullViewHolder holder) {
//
//                  //      mVideoPlayer = holder.mVideoPlayer(R.id.video_player);
//                        mVideoPlayerController = new VideoPlayerController(context);
//                      holder.mVideoPlayer.setController(mVideoPlayerController);
//
//
//                        if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
//                            holder.mPlayerUrlTv.setVisibility(View.VISIBLE);
//
//                            if (item.getSpread_url_type().equals("1")) {
//                                holder.mPlayerUrlTv.setText("点击立即下载");
//                                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
//                            } else if (item.getSpread_url_type().equals("2")) {
//                                holder.mPlayerUrlTv.setText("点击了解详情");
//                                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
//                            } else if (item.getSpread_url_type().equals("3")) {
//                                holder.mPlayerUrlTv.setText("点击立即购买");
//                                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
//                            }else if (item.getSpread_url_type().equals("4")){
//                                holder.mPlayerUrlTv.setText("点击阅读文章");
//                                holder.mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
//                            }
//
//                            holder.mPlayerUrlTv.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(context, CommonActivity.class);
//                                    intent.putExtra("title", item.getSpread_url());
//                                    intent.putExtra("url", item.getSpread_url());
//                                    context.startActivity(intent);
//                                }
//                            });
//                        }
//
//                        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//                        double width = manager.getDefaultDisplay().getWidth();
//
//                        ViewGroup.LayoutParams params = holder.mVideoPlayer.getLayoutParams();
//
//                        if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) { //高视频
//                            if (Integer.valueOf((int) (width / Double.valueOf(list.get(0).getPercent()))) >= 500) {
//                                double total = Double.valueOf(width) / 375.00;
//                                double videoWidth = 237 * Double.valueOf(total);
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(list.get(0).getPercent())));
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(list.get(0).getPercent())));
//                            }
//                        } else if (Integer.valueOf(list.get(0).getWidth()) > Integer.valueOf(list.get(0).getHeight())) {
//                            if (Integer.valueOf((int) (width / Double.valueOf(list.get(0).getPercent()))) >= 500) {
//                                params.width = (int) width;
//                                params.height = (int) 600.0;
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(list.get(0).getPercent())));
//                            }
//                        } else {
//                            double total = Double.valueOf(width) / 375.00;
//                            double videoWidth = 237 * Double.valueOf(total);
//                            params.width = (int) width;
//                            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(list.get(0).getPercent())));
//                        }
//
//
//                holder.mVideoPlayer.setLayoutParams(params);
//
//
//                        Glide.with(context).load(list.get(0).getPicture_url()).into(mVideoPlayerController.imageView());
//
//
//                holder.mVideoPlayer.continueFromLastPosition(false);
//                holder.mVideoPlayer.getTcpSpeed();
//
//
//                        mVideoPlayerController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Drawable.ConstantState drawableCs = mVideoPlayerController.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
//                                if (mVideoPlayerController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
//                                    holder.mVideoPlayer.setVolume(0);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                                    SharedPreferencesUtils.setParameter(context, Constants.NEARBY_VOICE, "nearby");
//                                } else {
//                                    holder.mVideoPlayer.setVolume(50);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                                    SharedPreferencesUtils.removeParameter(context, Constants.NEARBY_VOICE);
//                                }
//                            }
//                        });
//
//                        if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                        } else {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                        }
//
//                        mVideoPlayerController.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (mListener != null) {
//                                    mListener.onPlayView(list.get(position).getPath());
//                                }
//                            }
//                        });
//                    }
//           //     });
//        //    }
//            holder.mVideoPlayer.setUp(list.get(0).getPath(), null);
//          //  holder.mVideoPlayer.start();
//            myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
//                @Override
//                public void onInitComplete() {
//
//                }
//
//                @Override
//                public void onPageRelease(boolean isNext, int position) {
//                    //释放
//                    //     Toast.makeText(context, "释放", Toast.LENGTH_SHORT).show();
//                    //          holder.video_view.stopPlayback();
//
//                }
//
//                @Override
//                public void onPageSelected(int position, boolean isBottom) {
//                    //播放
//                    //     holder.video_view.setVideoURI(Uri.parse(mDataList.get(position).getUserArticlePictures().get(0).getPath()));
//                    //      holder.video_view.start();
//                    //    Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
//
//        }
//         else {
//
//            if (null != mVideoPlayerController) {
//                if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
//                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                } else {
//                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                }
//            }
//
//            if (null != mVideoPlayer) {
//                mVideoPlayer.release();
//            }
//            final ArrayList<Moment.Pictures> list = item.getUserArticlePictures();
//            if (list == null || list.size() < 1) {
//                holder.mListView.setVisibility(GONE);
//            } else {
//                holder.mListView.setAdapter(new NestFullListViewAdapter<Moment.Pictures>(R.layout.item_img, list) {
//                    @Override
//                    public void onBind(int pos, Moment.Pictures pictures, NestFullViewHolder holder) {
//                        mOnePl = holder.getView(R.id.prl_one);
//                        mOne1 = holder.getView(R.id.iv_img_one1);
//                        mLongImg = holder.getView(R.id.longImg);
//                        mOne2 = holder.getView(R.id.iv_img_one2);
//                        mUrlTv = holder.getView(R.id.tv_url);
//                        mTwoll = holder.getView(R.id.ll_two);
//                        mTwo21 = holder.getView(R.id.iv_img_two21);
//                        mTwo22 = holder.getView(R.id.iv_img_two22);
//
//                        mThree1 = holder.getView(R.id.ll_three);
//                        mThreeIv1 = holder.getView(R.id.iv_img_three1);
//                        mThreeIv2 = holder.getView(R.id.iv_img_three2);
//                        mThreeIv3 = holder.getView(R.id.iv_img_three3);
//
//                        mThree2 = holder.getView(R.id.prl_three2);
//                        mThreeIv21 = holder.getView(R.id.iv_img_three21);
//                        mThreeIv22 = holder.getView(R.id.iv_img_three22);
//                        mThreeIv23 = holder.getView(R.id.iv_img_three23);
//
//                        mFour1 = holder.getView(R.id.prl_four);
//                        mFourIv1 = holder.getView(R.id.iv_img_four1);
//                        mFourIv2 = holder.getView(R.id.iv_img_four2);
//                        mFourIv3 = holder.getView(R.id.iv_img_four3);
//                        mFourIv4 = holder.getView(R.id.iv_img_four4);
//
//                        mFour2 = holder.getView(R.id.prl_four2);
//                        mFourIv21 = holder.getView(R.id.iv_img_four21);
//                        mFourIv22 = holder.getView(R.id.iv_img_four22);
//                        mFourIv23 = holder.getView(R.id.iv_img_four23);
//                        mFourIv24 = holder.getView(R.id.iv_img_four24);
//
//                        mFive1 = holder.getView(R.id.prl_five);
//                        mFiveIv1 = holder.getView(R.id.iv_img_five);
//                        mFiveIv2 = holder.getView(R.id.iv_img_five2);
//                        mFiveIv3 = holder.getView(R.id.iv_img_five3);
//                        mFiveIv4 = holder.getView(R.id.iv_img_five4);
//                        mFiveIv5 = holder.getView(R.id.iv_img_five5);
//
//                        mFive2 = holder.getView(R.id.prl_five1);
//                        mFiveIv21 = holder.getView(R.id.iv_img_five21);
//                        mFiveIv22 = holder.getView(R.id.iv_img_five22);
//                        mFiveIv23 = holder.getView(R.id.iv_img_five23);
//                        mFiveIv24 = holder.getView(R.id.iv_img_five24);
//                        mFiveIv25 = holder.getView(R.id.iv_img_five25);
//                        mClearll = holder.getView(R.id.ll_clear);
//                        mConutTv = holder.getView(R.id.tv_count_img);
//
//                        if (list.size() == 1) {
//                            mOnePl.setVisibility(View.VISIBLE);
//                            if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
//                                mUrlTv.setVisibility(View.VISIBLE);
//
//                                if (item.getSpread_url_type().equals("1")) {
//                                    mUrlTv.setText("点击立即下载");
//                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
//                                } else if (item.getSpread_url_type().equals("2")) {
//                                    mUrlTv.setText("点击了解详情");
//                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
//                                } else if (item.getSpread_url_type().equals("3")) {
//                                    mUrlTv.setText("点击立即购买");
//                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
//                                }else if (item.getSpread_url_type().equals("4")){
//                                    mUrlTv.setText("点击阅读文章");
//                                    mUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
//                                }
//
//
//                                mUrlTv.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(context, CommonActivity.class);
//                                        intent.putExtra("title", item.getSpread_url());
//                                        intent.putExtra("url", item.getSpread_url());
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            }
//                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
//                                mOne1.setVisibility(View.VISIBLE);
//                                mOne2.setVisibility(GONE);
//
//                                if (Integer.valueOf(list.get(0).getHeight()) >= 500) {
//                                    mLongImg.setVisibility(View.VISIBLE);
//                                    mOne1.setVisibility(GONE);
//
//                                    Glide.with(context)
//                                            .download(list.get(0).getPath())
//                                            .into(new SimpleTarget<File>() {
//                                                @Override
//                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                                                    super.onLoadFailed(errorDrawable);
//                                                }
//
//                                                @Override
//                                                public void onResourceReady(File resource, Transition<? super File> transition) {
//                                                    mLongImg.setQuickScaleEnabled(true);
//                                                    mLongImg.setZoomEnabled(true);
//                                                    mLongImg.setPanEnabled(true);
//                                                    mLongImg.setDoubleTapZoomDuration(500);
//                                                    mLongImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
//                                                    mLongImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
//                                                    mLongImg.setImage(ImageSource.uri(resource.getAbsolutePath()), new ImageViewState(0, new PointF(0, 0), 0));
//                                                }
//                                            });
//                                } else {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne1);
//                                }
//
//                                mLongImg.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_LONG_IMG_URLS, "111");
//                                        context.startActivity(intent);
//                                    }
//                                });
//
//                                mOne1.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else {
//                                mOne1.setVisibility(GONE);
//                                mOne2.setVisibility(View.VISIBLE);
//                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne2);
//
//                                mOne2.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            }
//
//                        } else if (list.size() == 2) {
//                            mTwoll.setVisibility(View.VISIBLE);
//                            if (pos == 0) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mTwo21);
//                                mTwo21.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else {
//                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mTwo22);
//                                mTwo22.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            }
//                        } else if (list.size() == 3) {
//                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
//                                mThree2.setVisibility(View.VISIBLE);
//                                if (pos == 0) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv21);
//                                    mThreeIv21.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 1) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv22);
//
//                                    mThreeIv22.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv23);
//
//                                    mThreeIv23.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                }
//                            } else {
//                                mThree1.setVisibility(View.VISIBLE);
//                                if (pos == 0) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv1);
//
//                                    mThreeIv1.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 1) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv2);
//
//                                    mThreeIv2.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv3);
//                                    mThreeIv3.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                }
//                            }
//                        } else if (list.size() == 4) {
//                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
//                                mFour1.setVisibility(View.VISIBLE);
//                                if (pos == 0) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv1);
//
//                                    mFourIv1.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 1) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv2);
//                                    mFourIv2.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 2) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv3);
//
//                                    mFourIv3.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 3) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv4);
//
//                                    mFourIv4.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                }
//                            } else {
//                                mFour2.setVisibility(View.VISIBLE);
//                                if (pos == 0) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv21);
//
//                                    mFourIv21.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 1) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv22);
//
//                                    mFourIv22.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 2) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv23);
//
//                                    mFourIv23.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                } else if (pos == 3) {
//                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv24);
//
//                                    mFourIv24.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            ArrayList<String> urls = new ArrayList<>();
//                                            for (Moment.Pictures image : list) {
//                                                urls.add(image.getPath());
//                                            }
//                                            Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
//                                            context.startActivity(intent);
//                                        }
//                                    });
//                                }
//                            }
//                        } else if (list.size() == 5) {
//                            mFive1.setVisibility(View.VISIBLE);
//                            if (pos == 0) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFiveIv1);
//
//                                mFiveIv1.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 1) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFiveIv2);
//
//                                mFiveIv2.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 2) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFiveIv3);
//
//                                mFiveIv3.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 3) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFiveIv4);
//
//                                mFiveIv4.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 4) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(4).getPath(), mFiveIv5);
//
//                                mFiveIv5.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 4);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            }
//                        } else {
//                            mFive2.setVisibility(View.VISIBLE);
//                            mClearll.getBackground().setAlpha(90);
//                            if (pos == 0) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv21);
//
//                                mFiveIv21.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 1) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv22);
//
//                                mFiveIv22.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 2) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv23);
//
//                                mFiveIv23.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 3) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv24);
//
//                                mFiveIv24.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ArrayList<String> urls = new ArrayList<>();
//                                        for (Moment.Pictures image : list) {
//                                            urls.add(image.getPath());
//                                        }
//                                        Intent intent = new Intent(context, ImageBrowserActivity.class);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
//                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
//                                        context.startActivity(intent);
//                                    }
//                                });
//                            } else if (pos == 4) {
//                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv25);
//                            }
//                        }
//                        mConutTv.setText("+" + String.valueOf(Integer.valueOf(list.size()) - 5));
//
//                        mFiveIv25.setTag(position);
//                        mFiveIv25.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (mListener != null) {
//                                    mListener.onItemClick(view);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }

        holder.mChoiceIv.setTag(position);
        holder.mChoiceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick()) {
                    if (mListener != null) {
                        mListener.onReportClick(view);
                    }
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
            }
        });

        holder.mImageLl.setTag(position);
        holder.mImageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick()) {
                    if (mListener != null) {
                        mListener.onImageItemClick(view, position - 1);
                    }
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (null == mDataList) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       private NiceVideoPlayer mVideoPlayer;
       private TextView mPlayerUrlTv;
        //    private FullWindowVideoView video_view;
        private View mView;
        private RoundImageView mAvatarIv;
        private TextView mNameTv;
        private TextView mTimeTv;
        private ImageView mChoiceIv;
        private ImageView mDentyIv;
        private ReadMoreTextView mTextTv;
        private SimpleDraweeView mSimpleDraweeView;
        private RecyclerView mRecyclerViewA;
        private TextView mHideTv;

        private LinearLayout mConentLl;
        private LinearLayout mImageLl;
        private NestFullListView mListView;
        private RelativeLayout mZanView, mCommentView, mShareView;
        private TextView mDistanceTv, mMapTv;
        private LinearLayout mRedLl;
        private TextView mPeopleNumTv;
        public TextView mZanTv, mCommentTv, mShareTv, mNumTv, mCommentNumTv, mRepostNumTv, mViewNum;

        private MyScrollerView scroll_view;
        public ViewHolder(View itemView) {
            super(itemView);
           // video_view=itemView.findViewById(R.id.video_view);
            mPlayerUrlTv=itemView.findViewById(R.id.tv_player_url);
            mVideoPlayer=itemView.findViewById(R.id.video_player);
            mView = itemView.findViewById(R.id.friend_view);
            mAvatarIv = itemView.findViewById(R.id.iv_avatar);
            mNameTv = itemView.findViewById(R.id.tv_user_name);
            mSimpleDraweeView = itemView.findViewById(R.id.ic_red);
            mPeopleNumTv = itemView.findViewById(R.id.tv_people_num);
            mRedLl = itemView.findViewById(R.id.ll_red);

            mTimeTv = itemView.findViewById(R.id.tv_moment_time);
            mChoiceIv = itemView.findViewById(R.id.iv_choice);
            mTextTv = itemView.findViewById(R.id.tv_text);
            mZanView = itemView.findViewById(R.id.view_zan);
            mZanTv = itemView.findViewById(R.id.tv_zan);
            mCommentView = itemView.findViewById(R.id.view_comment);
            mShareView = itemView.findViewById(R.id.view_share);
            mDentyIv = itemView.findViewById(R.id.iv_identy);
            mImageLl = itemView.findViewById(R.id.image_layout);
            mCommentTv = itemView.findViewById(R.id.tv_comment);
            mShareTv = itemView.findViewById(R.id.tv_share);
            mConentLl = itemView.findViewById(R.id.ll_conent);
            mListView = itemView.findViewById(R.id.list_view);
            mNumTv = itemView.findViewById(R.id.tv_num);
            mCommentNumTv = itemView.findViewById(R.id.tv_comment_num);
            mRepostNumTv = itemView.findViewById(R.id.tv_repost_num);
            mViewNum = itemView.findViewById(R.id.tv_view_num);

            mDistanceTv = itemView.findViewById(R.id.tv_distance);
            mMapTv = itemView.findViewById(R.id.tv_map);
            mRecyclerViewA = itemView.findViewById(R.id.recycler_view_a);
            mHideTv = itemView.findViewById(R.id.tv_hide);
            scroll_view=itemView.findViewById(R.id.scroll_view);
        }
        /**
         *
         * @param x event的rowX
         * @param y event的rowY
         * @return 这个点在不在sv的范围内.
         */
        public boolean isTouchNsv(float x,float y) {
            int[] pos = new int[2];
            //获取sv在屏幕上的位置
            scroll_view.getLocationOnScreen(pos);
            int width = scroll_view.getMeasuredWidth();
            int height = scroll_view.getMeasuredHeight();
            return x >= pos[0] && x <= pos[0] + width && y >= pos[1] && y <= pos[1] + height;
        }
    }





    public interface FlowListener {
        void onMsgClick(Moment message);

        void onZanClick(View view, ViewHolder viewHolder);

        void onShareClick(View view);

        void onItemClick(View view);

        void onAvatarClick(Moment.Mine mine);

        void onItemAvatarClick(View view);

        void onReportClick(View view);

        void onImageItemClick(View view, int index);

        void onPlayView(String path);

        void onAMapLocation(View view);

        void onRedClick(View view);
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        final NiceVideoPlayer mVideoPlayer = itemView.findViewById(R.id.video_player);
        mVideoPlayer.release();
    }

    private void playVideo(int position){
        View itemView = mRecyclerView.getChildAt(0);
        final NiceVideoPlayer mVideoPlayer = itemView.findViewById(R.id.video_player);
//        mVideoPlayerController = new VideoPlayerController(context);
//               mVideoPlayer.setController(mVideoPlayerController);
//
//             ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        double width = manager.getDefaultDisplay().getWidth();
//                        if (Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getHeight()) > Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getWidth())) { //高视频
//                            if (Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent()))) >= 500) {
//                                double total = Double.valueOf(width) / 375.00;
//                                double videoWidth = 237 * Double.valueOf(total);
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
//                            }
//                        } else if (Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getWidth()) > Integer.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getHeight())) {
//                            if (Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent()))) >= 500) {
//                                params.width = (int) width;
//                                params.height = (int) 600.0;
//                            } else {
//                                params.width = (int) width;
//                                params.height = Integer.valueOf((int) (width / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
//                            }
//                        } else {
//                            double total = Double.valueOf(width) / 375.00;
//                            double videoWidth = 237 * Double.valueOf(total);
//                            params.width = (int) width;
//                            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(mDataList.get(position).getUserArticlePictures().get(0).getPercent())));
//                        }
//
//
//            mVideoPlayer.setLayoutParams(params);
//
//
//             Glide.with(context).load(mDataList.get(position).getUserArticlePictures().get(0).getPicture_url()).into(mVideoPlayerController.imageView());
//
//
//                        mVideoPlayer.continueFromLastPosition(false);
//                        mVideoPlayer.getTcpSpeed();
//                        mVideoPlayer.setUp(mDataList.get(position).getUserArticlePictures().get(0).getPath(), null);
//                        mVideoPlayer.start();
//
//                        mVideoPlayerController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Drawable.ConstantState drawableCs = mVideoPlayerController.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
//                                if (mVideoPlayerController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
//                                    mVideoPlayer.setVolume(0);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                                    SharedPreferencesUtils.setParameter(context, Constants.NEARBY_VOICE, "nearby");
//                                } else {
//                                    mVideoPlayer.setVolume(50);
//                                    mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                                    SharedPreferencesUtils.removeParameter(context, Constants.NEARBY_VOICE);
//                                }
//                            }
//                        });
//
//                        if (SharedPreferencesUtils.getParameter(context, Constants.NEARBY_VOICE, "").equals("nearby")) {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
//                        } else {
//                            mVideoPlayerController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
//                        }
//
//                        mVideoPlayerController.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (mListener != null) {
//                                  //  mListener.onPlayView(list.get(pos).getPath());
//                                }
//                            }
//                        });




        mVideoPlayer.start();
    }





}
