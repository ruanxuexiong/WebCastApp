package com.android.nana.friend;

import android.content.ClipboardManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.find.ArticleActivity;
import com.android.nana.find.adapter.EditDataCommentsAdapter;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.nestlistview.NestFullListView;
import com.android.nana.nestlistview.NestFullListViewAdapter;
import com.android.nana.nestlistview.NestFullViewHolder;
import com.android.nana.ui.RoundImageView;
import com.android.nana.user.weight.VideoPlayerController;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.util.VibrateHelp;
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
 * Created by lenovo on 2017/11/16.
 */

public class WholeAlbumAdapter extends BaseAdapter {

    private Context context;
    private String mid;
    private WholeAlbumListener mListener;
    private ArrayList<AlbumEntity.Articles> mDataList;

    private LayoutInflater inflater = null;
    private PercentRelativeLayout  mThree2, mFour1, mFour2;
    private LinearLayout mTwoll, mThree1, mFive1, mFive2;
    private ImageView mOne1, mOne2, mTwo21, mTwo22;
    private RelativeLayout mOnePl;
    private TextView mUrlTv;
    private ImageView mThreeIv1, mThreeIv2, mThreeIv3, mThreeIv21, mThreeIv22, mThreeIv23;
    private ImageView mFourIv1, mFourIv2, mFourIv3, mFourIv4;
    private ImageView mFourIv21, mFourIv22, mFourIv23, mFourIv24;
    private ImageView mFiveIv1, mFiveIv2, mFiveIv3, mFiveIv4, mFiveIv5;
    private ImageView mFiveIv21, mFiveIv22, mFiveIv23, mFiveIv24, mFiveIv25;
    private LinearLayout mClearll;
    private SubsamplingScaleImageView mLongImg;
    private TextView mConutTv;
    private EditDataCommentsAdapter mAdapter;

    //视频
    private NiceVideoPlayer mVideoPlayer;

    public WholeAlbumAdapter(Context mContext, ArrayList<AlbumEntity.Articles> mDataList, WholeAlbumListener listener, String mid) {
        this.context = mContext;
        this.mDataList = mDataList;
        this.mListener = listener;
        this.mid = mid;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.item_friend, null);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.mView = view.findViewById(R.id.friend_view);
        viewHolder.mAvatarIv = view.findViewById(R.id.iv_avatar);
        viewHolder.mNameTv = view.findViewById(R.id.tv_user_name);
        viewHolder.mTimeTv = view.findViewById(R.id.tv_moment_time);
        viewHolder.mChoiceIv = view.findViewById(R.id.iv_choice);
        viewHolder.mTextTv = view.findViewById(R.id.tv_text);
        viewHolder.mZanView = view.findViewById(R.id.view_zan);
        viewHolder.mZanTv = view.findViewById(R.id.tv_zan);
        viewHolder.mCommentView = view.findViewById(R.id.view_comment);
        viewHolder.mShareView = view.findViewById(R.id.view_share);
        viewHolder.mDentyIv = view.findViewById(R.id.iv_identy);
        viewHolder.mImageLl = view.findViewById(R.id.image_layout);
        viewHolder.mCommentTv = view.findViewById(R.id.tv_comment_num);
        viewHolder.mRepostNumTv = view.findViewById(R.id.tv_repost_num);
        viewHolder.mViewNumTv = view.findViewById(R.id.tv_view_num);
        viewHolder.mRedLl = view.findViewById(R.id.ll_red);
        viewHolder.mPeopleNumTv = view.findViewById(R.id.tv_people_num);
        viewHolder.mSimpleDraweeView = view.findViewById(R.id.ic_red);
        viewHolder.mRecyclerViewA = view.findViewById(R.id.recycler_view_a);
        viewHolder.mHideTv = view.findViewById(R.id.tv_hide);

        viewHolder.mShareTv = view.findViewById(R.id.tv_share);
        viewHolder.mConentLl = view.findViewById(R.id.ll_conent);
        viewHolder.mListView = view.findViewById(R.id.list_view);
        viewHolder.mNumTv = view.findViewById(R.id.tv_num);
        viewHolder.mMapTv = view.findViewById(R.id.tv_map);
        viewHolder.mDistanceTv = view.findViewById(R.id.tv_distance);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        viewHolder.mDistanceTv.setLayoutParams(lp);
        viewHolder.mTimeTv.setVisibility(View.VISIBLE);

        final AlbumEntity.Articles item = mDataList.get(position);

        if (position == 0) {
            viewHolder.mView.setVisibility(GONE);
        } else {
            viewHolder.mView.setVisibility(View.VISIBLE);
        }

        if (null != item.getAddress() && !"".equals(item.getAddress())) {
            viewHolder.mMapTv.setText(item.getAddress());
        } else {
            viewHolder.mMapTv.setVisibility(GONE);
        }

        if ("".equals(item.getJuli())) {
            viewHolder.mDistanceTv.setVisibility(GONE);
        } else {
            viewHolder.mDistanceTv.setText(item.getJuli());
        }

        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.icon_gif))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        viewHolder.mSimpleDraweeView.setController(controller);

        if (null != item.getComments() && item.getComments().size() > 0) {
            viewHolder.mHideTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mHideTv.setVisibility(GONE);
        }
        NestedScrollLinearLayoutManager layoutManager = new NestedScrollLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.mRecyclerViewA.setLayoutManager(layoutManager);// 布局管理器。
        mAdapter = new EditDataCommentsAdapter(context, item.getComments());
        viewHolder.mRecyclerViewA.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (item.getLaudResult().equals("0")) {
            viewHolder.mPeopleNumTv.setText("点赞得现金红包");
        } else {
            viewHolder.mPeopleNumTv.setText(item.getBoundCount() + "人领取");
        }
        if (item.getIsReceived().equals("1")){
            viewHolder.mPeopleNumTv.setText("红包已领完");
        }

        if (item.getShowBound().equals("1")) {
            viewHolder.mRedLl.setVisibility(View.VISIBLE);
            viewHolder.mRedLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isFastClick()) {
                        if (mListener != null) {
                            mListener.onRedClick(v);
                        }
                    } else {
                        ToastUtils.showToast("操作太频繁，请稍后再试！");
                    }
                }
            });
        } else {
            viewHolder.mRedLl.setVisibility(GONE);
        }

        if (null != item.getUser()) {
            if (item.getUser().getStatus().equals("1")) {
                viewHolder.mDentyIv.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.icon_authen).into(viewHolder.mDentyIv);
            }
            else if (item.getUser().getStatus().equals("4")){
                viewHolder.mDentyIv.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.mipmap.user_vip).into(viewHolder.mDentyIv);
            }
            else {
                viewHolder.mDentyIv.setVisibility(View.GONE);
            }
            viewHolder.mNameTv.setText(item.getUser().getUsername());
            viewHolder.mTimeTv.setText(item.getTime());
            ImgLoaderManager.getInstance().showImageView(item.getUser().getAvatar(), viewHolder.mAvatarIv);
        }

        if (item.getContent().equals("")) {
            viewHolder.mTextTv.setVisibility(GONE);
        } else {
//            viewHolder.mTextTv.setText(item.getContent());
            viewHolder.mTextTv.setText(WeiBoContentTextUtil.getWeiBoContent(item.getContent(), context, viewHolder.mTextTv, new WeiBoContentTextUtil.OnClistener() {
                @Override
                public void toUserData(String name) {
                    for (AlbumEntity.Articles.UserListBean userID : item.getUser_list()) {
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
                    for (AlbumEntity.Articles.Article article : item.getTag_list()) {
                        if (name.equals(article.getTag_name())) {
                            Intent intent = new Intent(context, ArticleActivity.class);
                            intent.putExtra("tagid", article.getTag_id());
                            context.startActivity(intent);
                            return;
                        }
                    }
                }
            }));
            viewHolder.mTextTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.mTextTv.isClick) {//用于判断全文收起点击处理
                        viewHolder.mTextTv.isClick = false;
                        viewHolder.mTextTv.setMovementMethod(ScrollingMovementMethod.getInstance());
                    } else {
                        if (mListener != null) {
                            mListener.onItemClick(view);
                        }
                    }
                }
            });
        }
        if (item.getLaudResult().equals("1")) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.icon_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
        } else if (item.getLaudResult().equals("0")) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
        }

        if (Integer.valueOf(item.getLaudCount()) > 0) {//点赞数
            viewHolder.mZanTv.setText(item.getLaudCount());
            viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");
        } else {
            viewHolder.mNumTv.setText("0人点赞");
        }

        if (Integer.valueOf(item.getComment_count()) > 0) {//动态数
            viewHolder.mCommentTv.setVisibility(View.VISIBLE);
            viewHolder.mCommentTv.setText(item.getComment_count() + "条评论·");
        } else {
            viewHolder.mCommentTv.setVisibility(View.VISIBLE);
            viewHolder.mCommentTv.setText("0条评论·");
        }

        if (Integer.valueOf(item.getRepost_count()) > 0) {//分享数
            viewHolder.mRepostNumTv.setVisibility(View.VISIBLE);
            viewHolder.mRepostNumTv.setText(item.getRepost_count() + "次分享·");
        } else {
            viewHolder.mRepostNumTv.setVisibility(View.VISIBLE);
            viewHolder.mRepostNumTv.setText("0次分享·");
        }

        if (Integer.valueOf(item.getView_count()) > 0) {//浏览数
            viewHolder.mViewNumTv.setVisibility(View.VISIBLE);
            viewHolder.mViewNumTv.setText(item.getView_count() + "次浏览");
        } else {
            viewHolder.mViewNumTv.setVisibility(View.VISIBLE);
            viewHolder.mViewNumTv.setText("0次浏览");
        }

        //点赞
        viewHolder.mZanView.setTag(position);
        viewHolder.mZanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick()) {
                    if (mListener != null) {
                        mListener.onZanClick(view,viewHolder);
                    }
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
            }
        });

        //分享
        viewHolder.mShareView.setTag(position);
        viewHolder.mShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onShareClick(view);
                }
            }
        });

        viewHolder.mConentLl.setTag(position);
        viewHolder.mConentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });

        viewHolder.mAvatarIv.setTag(position);//点击头像
        viewHolder.mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemAvatarClick(view);
                }
            }
        });


        //复制文本
        viewHolder.mTextTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                VibrateHelp.vSimple(view.getContext(), 30);
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(viewHolder.mTextTv.getText().toString());
                ToastUtils.showToast("复制成功");
                return false;
            }
        });


        if (item.getType().equals("2")) {
            final ArrayList<AlbumEntity.Articles.Pictures> list = item.getUserArticlePictures();
            if (list == null || list.size() < 1) {
                viewHolder.mListView.setVisibility(GONE);
            } else {
                viewHolder.mListView.setAdapter(new NestFullListViewAdapter<AlbumEntity.Articles.Pictures>(R.layout.item_video, list) {
                    @Override
                    public void onBind(final int pos, AlbumEntity.Articles.Pictures pictures, NestFullViewHolder holder) {
                        mVideoPlayer  = holder.getView(R.id.video_player);
                        final VideoPlayerController controller = new VideoPlayerController(context);
                        mVideoPlayer.setController(controller);


                        TextView mPlayerUrlTv = holder.getView(R.id.tv_player_url);
                        if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {

                            mPlayerUrlTv.setVisibility(View.VISIBLE);

                            if (item.getSpread_url_type().equals("1")) {
                                mPlayerUrlTv.setText("点击立即下载");
                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.c2f0));
                            } else if (item.getSpread_url_type().equals("2")) {
                                mPlayerUrlTv.setText("点击了解详情");
                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffb));
                            } else if (item.getSpread_url_type().equals("3")) {
                                mPlayerUrlTv.setText("点击立即购买");
                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ff8080));
                            }else if (item.getSpread_url_type().equals("4")){
                                mPlayerUrlTv.setText("点击阅读文章");
                                mPlayerUrlTv.setBackgroundColor(context.getResources().getColor(R.color.ffc));
                            }

                            mPlayerUrlTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, CommonActivity.class);
                                    intent.putExtra("title", item.getSpread_url());
                                    intent.putExtra("url", item.getSpread_url());
                                    context.startActivity(intent);
                                }
                            });
                        }

                        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        double width = manager.getDefaultDisplay().getWidth();

                        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();

                        if (Integer.valueOf(pictures.getHeight()) > Integer.valueOf(pictures.getWidth())) { //高视频
                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
                                double total = Double.valueOf(width) / 375.00;
                                double videoWidth = 237 * Double.valueOf(total);
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
                            } else {
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
                            }
                        } else if (Integer.valueOf(pictures.getWidth()) > Integer.valueOf(pictures.getHeight())) {
                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
                                params.width = (int) width;
                                params.height = (int) 600.0;
                            } else {
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
                            }
                        }else {
                            double total = Double.valueOf(width) / 375.00;
                            double videoWidth = 237 * Double.valueOf(total);
                            params.width = (int) width;
                            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
                        }
                        mVideoPlayer.setLayoutParams(params);

                        Glide.with(context).load(pictures.getPicture_url()).into(controller.imageView());

                        mVideoPlayer.continueFromLastPosition(false);
                        mVideoPlayer.getTcpSpeed();
                        mVideoPlayer.setUp(pictures.getPath(),null);
                        mVideoPlayer.start();


                        controller.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Drawable.ConstantState drawableCs = controller.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                                if (controller.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                                    mVideoPlayer.setVolume(0);
                                    controller.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                                } else {
                                    mVideoPlayer.setVolume(100);
                                    controller.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                                }
                            }
                        });

                        controller.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mListener != null) {
                                    mListener.onPlayView(list.get(pos).getPath());
                                }
                            }
                        });

                    }
                });
            }

        } else {

            if (null != mVideoPlayer) {
                mVideoPlayer.release();
            }

            final ArrayList<AlbumEntity.Articles.Pictures> list = item.getUserArticlePictures();
            if (list == null || list.size() < 1) {
                viewHolder.mListView.setVisibility(GONE);
            } else {
                viewHolder.mListView.setAdapter(new NestFullListViewAdapter<AlbumEntity.Articles.Pictures>(R.layout.item_img, list) {
                    @Override
                    public void onBind(int pos, AlbumEntity.Articles.Pictures pictures, NestFullViewHolder holder) {

                        mOnePl = holder.getView(R.id.prl_one);
                        mOne1 = holder.getView(R.id.iv_img_one1);
                        mOne2 = holder.getView(R.id.iv_img_one2);
                        mLongImg = holder.getView(R.id.longImg);
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

                            if (null != list.get(0).getHeight() && Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
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


                                mOne1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                            if (null != list.get(0).getHeight() && Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mThree2.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv21);
                                    mThreeIv21.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                            if (null != list.get(0).getHeight() && Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mFour1.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv1);

                                    mFourIv1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                            for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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
                                        for (AlbumEntity.Articles.Pictures image : list) {
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


        viewHolder.mChoiceIv.setTag(position);
        viewHolder.mChoiceIv.setOnClickListener(new View.OnClickListener() {
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

        viewHolder.mImageLl.setTag(position);
        viewHolder.mImageLl.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    public interface WholeAlbumListener {
        void onZanClick(View view,WholeAlbumAdapter.ViewHolder viewHolder);

        void onShareClick(View view);

        void onItemClick(View view);

        void onItemAvatarClick(View view);

        void onReportClick(View view);

        void onImageItemClick(View view, int index);

        void onAvatarClick(View view);

        void onPlayView(String path);

        void onRedClick(View view);
    }


    public class ViewHolder {
        private View mView;
        private RoundImageView mAvatarIv;
        private TextView mNameTv;
        private TextView mTimeTv;
        private ImageView mChoiceIv;
        private ImageView mDentyIv;
        private ReadMoreTextView mTextTv;

        private LinearLayout mConentLl;
        private LinearLayout mImageLl;
        private NestFullListView mListView;
        private RelativeLayout mZanView, mCommentView, mShareView;
        public TextView mZanTv, mCommentTv, mShareTv, mRepostNumTv, mViewNumTv;
        public TextView mNumTv;
        private TextView mMapTv;
        private LinearLayout mRedLl;
        private TextView mPeopleNumTv;
        private SimpleDraweeView mSimpleDraweeView;
        private TextView mDistanceTv;

        private RecyclerView mRecyclerViewA;
        private TextView mHideTv;
    }

}
