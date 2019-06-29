package com.android.nana.friend;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.nana.R;
import com.android.nana.util.ImgLoaderManager;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/3.
 */

public class ImagePage extends PagerAdapter {


    private Context mContext;
    private ArrayList<FriendEntity.Pictures> mDataList;

    public ImagePage(Context context, ArrayList<FriendEntity.Pictures> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return this.mDataList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_pager_image, view, false);
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.image);
        FriendEntity.Pictures pictures = mDataList.get(position);
        ImgLoaderManager.getInstance().showImageView(pictures.getPath(), imageView);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
