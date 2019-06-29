package com.android.nana.find.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;

import java.lang.reflect.Field;

/**
 * Created by lenovo on 2018/9/29.
 */

public abstract class BaseFragment extends BaseRequestFragment {

    private TextureMapView mMapView;
    protected AMap aMap;
    private LinearLayout.LayoutParams mParams;
    private LinearLayout mContainerLayout;

    protected abstract LatLng getTarget();

    protected abstract CameraPosition getCameraPosition();

    protected abstract void setCameraPosition(CameraPosition cameraPosition);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContainerLayout = (LinearLayout) findViewById(R.id.map_container);
        mMapView = new TextureMapView(getActivity());
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(mMapView, mParams);
        mMapView.onCreate(savedInstanceState);

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            aMap = mMapView.getMap();
            if (getCameraPosition() == null) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(getTarget(), 10, 0, 0)));
            } else {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition()));
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (null != mMapView) {
            mMapView.onResume();
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (null != mMapView) {
            mMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mMapView) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        setCameraPosition(aMap.getCameraPosition());
        super.onDestroy();
        if (null != mMapView) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
