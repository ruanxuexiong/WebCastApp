package com.android.nana.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.nana.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/3/11.
 */

public class CollectionPictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private CollectionPictureListener mListener;
    private ArrayList<MeCollectionEntity.Article> mDataList = new ArrayList<>();

    public CollectionPictureImageGridAdapter(Context mContext,ArrayList<MeCollectionEntity.Article> mDataList,CollectionPictureListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_picture_image_grid_item, parent, false);
        final CollectionPictureImageGridAdapter.ViewHolder viewHolder = new CollectionPictureImageGridAdapter.ViewHolder(view);
        viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(viewHolder.item.getId());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder contentHolder = (ViewHolder) holder;
        MeCollectionEntity.Article entity = mDataList.get(position);
        contentHolder.item = entity;
        if (entity.getType().equals("1")){//图片
            contentHolder.mDurationIv.setVisibility(View.GONE);
            contentHolder.mImageIv.setVisibility(View.VISIBLE);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.img_df)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true);

            Glide.with(mContext)
                    .asBitmap()
                    .load(entity.getUserArticlePictures().get(0).getPath())
                    .apply(options)
                    .into(contentHolder.mPictureIv);
        }else {//视频
            contentHolder.mDurationIv.setVisibility(View.VISIBLE);
            contentHolder.mImageIv.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.img_df)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true);

            Glide.with(mContext)
                    .asBitmap()
                    .load(entity.getUserArticlePictures().get(0).getPath())
                    .apply(options)
                    .into(contentHolder.mPictureIv);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPictureIv;
        ImageView mDurationIv, mImageIv;
        View contentView;
        MeCollectionEntity.Article item;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            mPictureIv =  itemView.findViewById(R.id.iv_picture);
            mDurationIv =  itemView.findViewById(R.id.iv_duration);
            mImageIv =  itemView.findViewById(R.id.iv_img);
        }
    }

    public interface CollectionPictureListener{
        void onItemClick(String id);
    }
}
