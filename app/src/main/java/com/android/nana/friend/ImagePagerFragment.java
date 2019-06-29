package com.android.nana.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.nana.R;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/3.
 */

public class ImagePagerFragment extends Fragment {

    private ArrayList<FriendEntity.Pictures> mDataList;
    private ViewPager pager;
    private View rootView;

    public static ImagePagerFragment newInstance(ArrayList<FriendEntity.Pictures> mDataList) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", mDataList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
        pager = rootView.findViewById(R.id.pager);
        pager.setAdapter(new ImagePage(getContext(), mDataList));
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = getArguments().getParcelableArrayList("data");
    }


}
