package com.android.nana.friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.nestlistview.NestFullListView;
import com.android.nana.nestlistview.NestFullListViewAdapter;
import com.android.nana.nestlistview.NestFullViewHolder;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.widget.ImageBrowserActivity;
import com.android.nana.widget.WordToSpan;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/13.
 */

public class FriendSearchAdapter extends BaseAdapter {

    private Context mContext;
    private String keyword;
    private WordToSpan WTS = new WordToSpan();
    private WordToSpan mContentWTS = new WordToSpan();
    private FriendSearchListener mListener;
    private ArrayList<FriendSearchEntity.Moments> mDataList;
    //视频
    private ImageView mVideoIv1, mVideoIv2;
    private LinearLayout mVideoLl1, mVideoLl2;
    private LayoutInflater inflater = null;

    private TextView mNubTv;
    private LinearLayout mImgLl1, mImgLl2, mImgLl3, mImgL14, mImgL15, mClearLl;
    private ImageView m120x67Iv, m108x108Iv, m120x100Iv;
    private ImageView mImag2_1Iv, mImag2_2Iv;
    private ImageView mImag3_1Iv, mImag3_2Iv, mImag3_3Iv;
    private ImageView mImag4_1Iv, mImag4_2Iv, mImag4_3Iv, mImag4_4Iv;
    private ImageView mImag5_1Iv, mImag5_2Iv, mImag5_3Iv, mImag5_4Iv;

    public FriendSearchAdapter(Context mContext, ArrayList<FriendSearchEntity.Moments> mDataList, FriendSearchListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;

        WTS.setBackgroundHIGHLIGHT(mContext.getResources().getColor(R.color.white));
        WTS.setColorHIGHLIGHT(mContext.getResources().getColor(R.color.green));
        mContentWTS.setBackgroundHIGHLIGHT(mContext.getResources().getColor(R.color.white));
        mContentWTS.setColorHIGHLIGHT(mContext.getResources().getColor(R.color.green));
        inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setKeyword(String keyword) {//搜索key
        this.keyword = keyword;
    }

    @Override
    public int getCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
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
    public View getView(final int position, View itemView, ViewGroup viewGroup) {

        itemView = inflater.inflate(R.layout.item_friend_search, null);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.mAvatarIv = itemView.findViewById(R.id.iv_avatar);
        viewHolder.mNameTv = itemView.findViewById(R.id.tv_name);
        viewHolder.mTimeTv = itemView.findViewById(R.id.tv_time);
        viewHolder.mTextTv = itemView.findViewById(R.id.tv_text);
        viewHolder.mListView = itemView.findViewById(R.id.list_view);
        viewHolder.mContentLl = itemView.findViewById(R.id.ll_content);

        FriendSearchEntity.Moments item = mDataList.get(position);
        if (null != item.getUser().getUname() && !"".equals(item.getUser().getUname())) {
            viewHolder.mNameTv.setText(item.getUser().getUname());
        }
        viewHolder.mTimeTv.setText(item.getTime());
        if (null != item.getContent() && !"".equals(item.getContent())) {
            viewHolder.mTextTv.setText(item.getContent());
        } else {
            viewHolder.mTextTv.setVisibility(View.GONE);
        }

        WTS.setHighlight(viewHolder.mNameTv.getText().toString(), keyword, viewHolder.mNameTv);
        mContentWTS.setHighlight(viewHolder.mTextTv.getText().toString(), keyword, viewHolder.mTextTv);
        if (null != item.getUser().getAvatar() && !"".equals(item.getUser().getAvatar())) {
            ImageLoader.getInstance().displayImage(item.getUser().getAvatar(), viewHolder.mAvatarIv);
        }

        viewHolder.mContentLl.setTag(position);
        viewHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view);
                }
            }
        });

        if (item.getType().equals("2")) {
            final ArrayList<FriendSearchEntity.UserArticlePictures> list = item.getUserArticlePictures();
            if (list == null || list.size() < 1) {
                viewHolder.mListView.setVisibility(View.GONE);
            } else {
                if (Integer.valueOf(item.getVideoWidth()) > Integer.valueOf(item.getVideoHeight())) {
                    viewHolder.mListView.setAdapter(new NestFullListViewAdapter<FriendSearchEntity.UserArticlePictures>(R.layout.item_friend_search_120x67_video, list) {
                        @Override
                        public void onBind(final int pos, FriendSearchEntity.UserArticlePictures pictures, NestFullViewHolder holder) {
                            mVideoIv1 = holder.getView(R.id.video_iv);
                            mVideoLl1 = holder.getView(R.id.ll_video);

                            ImgLoaderManager.getInstance().showImageView(list.get(pos).getPicture_url(), mVideoIv1);
                            mVideoLl1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mListener != null) {
                                        mListener.onPlayView(list.get(pos).getPath());
                                    }
                                }
                            });
                        }
                    });
                } else {
                    viewHolder.mListView.setAdapter(new NestFullListViewAdapter<FriendSearchEntity.UserArticlePictures>(R.layout.item_friend_search_video, list) {
                        @Override
                        public void onBind(final int pos, FriendSearchEntity.UserArticlePictures pictures, NestFullViewHolder holder) {
                            mVideoIv2 = holder.getView(R.id.iv_img1);
                            mVideoLl2 = holder.getView(R.id.ll_video1);

                            ImgLoaderManager.getInstance().showImageView(list.get(pos).getPicture_url(), mVideoIv2);
                            mVideoLl2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mListener != null) {
                                        mListener.onPlayView(list.get(pos).getPath());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        } else {
            final ArrayList<FriendSearchEntity.UserArticlePictures> list = item.getUserArticlePictures();
            if (list == null || list.size() < 1) {
                viewHolder.mListView.setVisibility(View.GONE);
            } else {
                viewHolder.mListView.setAdapter(new NestFullListViewAdapter<FriendSearchEntity.UserArticlePictures>(R.layout.item_friend_search_img, list) {
                    @Override
                    public void onBind(int pos, FriendSearchEntity.UserArticlePictures pictures, NestFullViewHolder holder) {
                        mImgLl1 = holder.getView(R.id.ll_img_1);
                        m120x67Iv = holder.getView(R.id.iv_120x67);
                        m108x108Iv = holder.getView(R.id.iv_108x108);
                        m120x100Iv = holder.getView(R.id.iv_120x100);

                        mImgLl2 = holder.getView(R.id.ll_img_2);
                        mImag2_1Iv = holder.getView(R.id.iv_img2_1);
                        mImag2_2Iv = holder.getView(R.id.iv_img2_2);

                        mImgLl3 = holder.getView(R.id.ll_img_3);
                        mImag3_1Iv = holder.getView(R.id.iv_img3_1);
                        mImag3_2Iv = holder.getView(R.id.iv_img3_2);
                        mImag3_3Iv = holder.getView(R.id.iv_img3_3);

                        mImgL14 = holder.getView(R.id.ll_img_4);
                        mImag4_1Iv = holder.getView(R.id.iv_img1);
                        mImag4_2Iv = holder.getView(R.id.iv_img2);
                        mImag4_3Iv = holder.getView(R.id.iv_img3);
                        mImag4_4Iv = holder.getView(R.id.iv_img4);

                        mImgL15 = holder.getView(R.id.ll_img_5);
                        mImag5_1Iv = holder.getView(R.id.iv_img51);
                        mImag5_2Iv = holder.getView(R.id.iv_img52);
                        mImag5_3Iv = holder.getView(R.id.iv_img53);
                        mImag5_4Iv = holder.getView(R.id.iv_img54);
                        mNubTv = holder.getView(R.id.tv_img_num);
                        mClearLl = holder.getView(R.id.ll_clear);

                        if (list.size() == 1) {
                            mImgLl1.setVisibility(View.VISIBLE);
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), m120x67Iv);
                                m120x67Iv.setVisibility(View.VISIBLE);
                                m120x67Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), m108x108Iv);
                                m108x108Iv.setVisibility(View.VISIBLE);
                                m108x108Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else if (list.size() == 2) {
                            mImgLl2.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mImag2_1Iv);
                                mImag2_1Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mImag2_2Iv);
                                mImag2_2Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else if (list.size() == 3) {
                            mImgLl3.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mImag3_1Iv);
                                mImag3_1Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mImag3_2Iv);
                                mImag3_2Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mImag3_3Iv);
                                mImag3_3Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else if (list.size() == 4) {

                            mImgL14.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mImag4_1Iv);
                                mImag4_1Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mImag4_2Iv);
                                mImag4_2Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mImag4_3Iv);
                                mImag4_3Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mImag4_4Iv);
                                mImag4_4Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            mImgL15.setVisibility(View.VISIBLE);

                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mImag5_1Iv);
                                mImag5_1Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mImag5_2Iv);
                                mImag5_2Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mImag5_3Iv);
                                mImag5_3Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mImag5_4Iv);
                                mImag5_4Iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (FriendSearchEntity.UserArticlePictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                            mNubTv.setText("+" + String.valueOf(Integer.valueOf(list.size()) - 4));
                            mClearLl.setTag(position);
                            mClearLl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mListener != null) {
                                        mListener.onItemClick(view);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        return itemView;
    }

    public interface FriendSearchListener {
        void onPlayView(String path);

        void onItemClick(View view);
    }

    class ViewHolder {
        RoundImageView mAvatarIv;
        TextView mNameTv;
        TextView mTimeTv;
        TextView mTextTv;
        NestFullListView mListView;
        LinearLayout mContentLl;
    }

}
