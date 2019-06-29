package com.android.nana.find;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.common.BaseApplication;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.find.adapter.NearbyAdapter;

import com.android.nana.find.adapter.NewFindAdapter;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.friend.AlbumDetailsActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditPersonalActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.util.MD5;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.ImageBrowserActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewFindActivity extends BaseFragment implements NewFindAdapter.FlowListener {
    String falg="0";
    private boolean isPrepared;
    NewFindActivity context;
    private CustomPopWindow mCustomPopWindow;
    private boolean isZan = false;//是否点击加载列表
    private boolean isLoad = false;//是否需要加载更多
    private String mArticleId;
    private ImageView shuaxin_btn;
    private ListView listView;
    private FrameLayout main_frame;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private String mUserId;
    private List<String> video_=new ArrayList<>();
    private MultipleStatusView mMultipleStatusView;
    private RecyclerView mRecyclerView;
    private NewFindAdapter mAdapter;
    MyLayoutManager myLayoutManager;
    private double lng = 0.0, lat = 0.0;
    private ArrayList<Moment.Moments> mDataList = new ArrayList<>();
    private ArrayList<ArrayList<Moment.Moments>> mMapDataList = new ArrayList<>();
    private boolean isLoadMore = false;
    private RefreshLayout mRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      //  setUserVisibleHint(true);
        super.onCreate(savedInstanceState);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected void initData() {

//        initListener();
    }

    @Override
    public int onSetLayoutId() {

        return  R.layout.activity_new_find;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initView() {
        context=this;
        getvideo();
        shuaxin_btn=mContentView.findViewById(R.id.shuaxin_btn);
        mMultipleStatusView=mContentView.findViewById(R.id.multiple_status_view);
        mRecyclerView = mContentView.findViewById(R.id.recycler);
        myLayoutManager = new MyLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        initLocation();
     //   loadData("1");

        shuaxin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoadMore=false;
                shuaxin_btn.setEnabled(false);
                anim();
                loadData("1");
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View view=rv.findChildViewUnder(e.getX(),e.getY());
//                if(view!=null){
//                    NewFindAdapter.ViewHolder holder= (NewFindAdapter.ViewHolder) rv.getChildViewHolder(view);
//                    rv.requestDisallowInterceptTouchEvent(holder.isTouchNsv(e.getRawX(), e.getRawY()));
//                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position3) {
                //  Toast.makeText(context, position3+"释放", Toast.LENGTH_SHORT).show();
                if (isNext){
                    int index = 0;
                    if (isNext) {
                        index = 0;
                    } else {
                        index = 1;
                    }
                    releaseVideo(index);
                }
            }

            @Override
            public void onPageSelected(int position2, boolean isBottom) {
                playVideo(0);
                if (mDataList.size()>0){
                    if (mDataList.size()-1==position2){
                       // Toast.makeText(getActivity(), "加载更多", Toast.LENGTH_SHORT).show();

                        if (falg.equals("0")){
                            isLoadMore=true;
                            loadData("2");
                        }
                        else {
                            isLoadMore=false;
                            loadData("1");
                        }

                    }


                }


                // Toast.makeText(context, position2+"", Toast.LENGTH_SHORT).show();
            }
        });




    }

    public static NewFindActivity newInstance() {
        NewFindActivity fragment = new NewFindActivity();
        return fragment;
    }

    @Override
    public void bindEvent() {

    }

    private void initListener() {
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
            //    Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean bottom) {
              //  Log.e(TAG, "选择位置:" + position + " 下一页:" + bottom);
                Toast.makeText(getActivity(), position+"", Toast.LENGTH_SHORT).show();
               playVideo(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
     //   main_frame.setVisibility(View.GONE);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    private void loadData(String refresh) {

        HttpService.Newdiscovery(mUserId, lat, lng,refresh, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                       // mDataList = new ArrayList<>();
                        if (isLoadMore){
                            if (parseData(successJson).size() > 0) {
                                for (Moment.Moments item : parseData(successJson)) {
                                    mDataList.add(item);
                                }
                            }
                            mMapDataList.add(mDataList);
                            mAdapter.notifyDataSetChanged();
                        }
                        else {

                            mDataList.clear();;
                            if (parseData(successJson).size() > 0) {
                                for (Moment.Moments item : parseData(successJson)) {
                                    mDataList.add(item);
                                }
                            }
                            mMapDataList.add(mDataList);
                            if (null!=mAdapter){
                                mAdapter.notifyDataSetChanged();
                            }
                            else {
                                mAdapter = new NewFindAdapter(getActivity(),mDataList,context,true,myLayoutManager,mRecyclerView);
                                mRecyclerView.setLayoutManager(myLayoutManager);
                                mRecyclerView.setAdapter(mAdapter);
                            }

                            if (shuaxin_btn.getAnimation()!=null){
                                shuaxin_btn.getAnimation().cancel();
                            }


                        }

                        shuaxin_btn.setEnabled(true);

                    } else {
                        if (isLoadMore) {
                            isLoadMore = false;
                            ToastUtils.showToast("暂无数据");
                        //    mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            isLoadMore = false;
                        }
                        shuaxin_btn.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                    shuaxin_btn.setEnabled(true);
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
            }
        });

    }


    private ArrayList<Moment.Moments> parseData(String result) {
        ArrayList<Moment.Moments> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
             falg=data.getString("falg");
            JSONArray array = new JSONArray(data.getString("moments"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                Moment.Moments entity = gson.fromJson(array.optJSONObject(i).toString(), Moment.Moments.class);
                moment.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        final NiceVideoPlayer mVideoPlayer = itemView.findViewById(R.id.video_player);
        mVideoPlayer.release();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void playVideo(int position) {
        View itemView = mRecyclerView.getChildAt(0);
        final NiceVideoPlayer mVideoPlayer = itemView.findViewById(R.id.video_player);
        mVideoPlayer.start();
    }

    @Override
    public void onClick(View view) {

    }


//    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//
//      //  private int[] imgs = {R.mipmap.img_video_1, R.mipmap.img_video_2, R.mipmap.img_video_3, R.mipmap.img_video_4, R.mipmap.img_video_5, R.mipmap.img_video_6, R.mipmap.img_video_7, R.mipmap.img_video_8};
//       // private int[] videos = {R.raw.video_1, R.raw.video_2, R.raw.video_3, R.raw.video_4, R.raw.video_5, R.raw.video_6, R.raw.video_7, R.raw.video_8};
//
//
//        private int index = 0;
//        private Context mContext;
//
//        public MyAdapter(Context context) {
//            this.mContext = context;
//        }
//
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
//
//
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//         //   holder.img_thumb.setImageResource(imgs[index]);
//            holder.videoView.setVideoURI(Uri.parse(video_.get(index)));
//            index++;
//            if (index >= 4) {
//                index = 0;
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return 88;
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView img_thumb;
//            VideoView videoView;
//            ImageView img_play;
//            RelativeLayout rootView;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//
//                videoView = itemView.findViewById(R.id.video_view);
//                img_play = itemView.findViewById(R.id.img_play);
//                rootView = itemView.findViewById(R.id.root_view);
//            }
//        }
//    }

    private void getvideo(){
        mUserId = BaseApplication.getInstance().getCustomerId(getActivity());
        video_.add("http://qiniu.nanapal.com/SFZ_video_20190403093739927.mp4");
        video_.add("http://qiniu.nanapal.com/20190403084752.mp4");
        video_.add("http://qiniu.nanapal.com/20190402222029.mp4");
        video_.add("http://qiniu.nanapal.com/20190402221625.mp4");
        video_.add("http://qiniu.nanapal.com/20190402220515.mp4");
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                double lngMath = location.getLongitude();//经    度
                double latMath = location.getLatitude();//纬    度
                lng = (Math.round(lngMath * 1000000) / 1000000.0);
                lat = (Math.round(latMath * 1000000) / 1000000.0);
                if (lng != 0.0 && lat != 0.0) {
                    loadData("1");
                    locationClient.stopLocation();
                    mRefreshLayout.autoRefresh();

                }
                else {
                    loadData("1");
                    locationClient.stopLocation();
                    mRefreshLayout.autoRefresh();
                }
            }
        }
    };

    private void initLocation() {
        locationClient = new AMapLocationClient(getActivity());
        locationClient.setLocationOption(getDefaultOption());
        locationClient.setLocationListener(locationListener);
        locationClient.setLocationOption(locationOption);
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }



    private void cancelGood(final Moment.Moments item, final NewFindAdapter.ViewHolder viewHolder) {//取消点赞
        CustomerDbHelper.cancelLaudUserArticle(mUserId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));

                    if (result.getString("description").equals("已领红包无法取消点赞")){
                        //   ToastUtils.showToast(result.getString("description"));
                        //         ToastUtils.showToast("已领红包无法取消点赞");
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                        // int timeout=data.getInt("timeout");
                        //  Log.e("TAG","bound=="+bound+"mAdvertising="+mAdvertising+"mAdvType="+mAdvType+"mAdvUrl="+mAdvUrl+"timeout="+timeout);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",0);
                        dialog.show(fm, "dialog");

                    }

                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        //isZan = true;//清除数据重新加载
                        //loadData(true);
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.green_54));
                    } else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                     //   int timeout=data.getInt("timeout");
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "-1",0);
                        dialog.show(fm, "dialog");
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


    @Override
    public void onMsgClick(Moment message) {

    }

    @Override
    public void onZanClick(View view, NewFindAdapter.ViewHolder viewHolder) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());

        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item,viewHolder);
        } else {
            article(mUserId, item,viewHolder);
        }
    }



    //领取红包点赞
    public void article(String mUserId, final Moment.Moments item, final NewFindAdapter.ViewHolder viewHolder) {
        String time = Utils.getTime();
        String secret = mUserId + "&" + item.getId() + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + item.getShowBound();
        String sign = MD5.MD5Hash(secret);

        HttpService.laudUserArticle(mUserId, item.getId(), time, sign, item.getShowBound(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.main_blue));

                        if (result.getString("description").equals("点赞成功")) {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            ToastUtils.showToast(result.getString("description"));
                        } else {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String bound = data.getString("bound");
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            int timeout=data.getInt("timeout");
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",timeout);
                            dialog.show(fm, "dialog");
                        }
                    }
                    else if (result.getString("state").equals("-2")){
                        showNormalDialog();
                    }
                    else {
                        if (result.getString("description").equals("此红包已领完")) {

                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                            dialog.show(fm, "dialog");

                            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.main_blue));
                        } else {
                            ToastUtils.showToast(result.getString("description"));
                        }

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

    @Override
    public void onShareClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        mArticleId = item.getId();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mUserId);
        startActivity(intent);
    }

    @Override
    public void onAvatarClick(Moment.Mine mine) {
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mUserId);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());

        if (mUserId.equals(item.getUserId())) {
            Intent intent = new Intent(getActivity(), EditDataActivity.class);
            intent.putExtra("UserId", mUserId);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(getActivity(), EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {
        Moment.Moments mItem = mDataList.get((Integer) view.getTag());
        bottomMenu(mItem);
    }

    private void bottomMenu(final Moment.Moments mItem) {

        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();

        if (mItem.getCollectioned().equals("1")) {
            menuItem1.setText("取消收藏");
        } else {
            menuItem1.setText("收藏");
        }
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
        MenuItem menuItem2 = new MenuItem();

        if (mItem.getUserId().equals(mUserId)) {
            menuItem2.setText("删除");
        } else {
            menuItem2.setText("举报");
        }

        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                if (mItem.getCollectioned().equals("1")) {
                    cancelCollection(mItem);//取消收藏
                } else {
                    collection(mItem);//收藏
                }
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                if (mItem.getUserId().equals(mUserId)) {//删除
                    delete(mItem);
                } else {
                    onReport(mItem);
                }
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getActivity().getFragmentManager(), "BottomMenuFragment");
    }

    @Override
    public void onImageItemClick(View view, int index) {
        Moment.Moments mItem = mDataList.get((Integer) view.getTag());
        ArrayList<String> urls = new ArrayList<>();
        for (Moment.Pictures image : mItem.getUserArticlePictures()) {
            urls.add(image.getPath());
        }
        Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
        startActivity(intent);
    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(getActivity()).externalPictureVideo(path);
    }

    @Override
    public void onAMapLocation(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), LocationActivity.class);
        intent.putExtra("lat", item.getLat());
        intent.putExtra("lng", item.getLng());
        intent.putExtra("name", item.getUser().getUsername());
        startActivity(intent);
    }

    @Override
    public void onRedClick(View view) {//点击红包
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_red_layout, null);

        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .setFocusable(true)
                .setBgDarkAlpha(0.7f)
                .setOutsideTouchable(true)
                .create();
        mCustomPopWindow.showAsDropDown(view, -168, 0);
        handleLogic(contentView);
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请先完善您的头像和昵称资料？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        //    getHuafei();
                        Intent intent=new Intent(getActivity(),EditPersonalActivity.class);
                        intent.putExtra("thisUid",mUserId);
                        startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


    private void cancelCollection(Moment.Moments mItem) {//取消收藏
        CustomerDbHelper.cancelCollectionUserArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        isZan = true;
                      //  loadData("");
                        ToastUtils.showToast("取消收藏成功！");
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

    private void delete(Moment.Moments mItem) {//自己的朋友圈删除功能
        FriendDbHelper.delArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        isZan = true;//清除数据重新加载
                     //   loadData(true);
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

    private void onReport(final Moment.Moments mItem) {//举报
        FriendDbHelper.reportArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        Intent intent = new Intent(getActivity(), ReportActivity.class);
                        intent.putExtra("uid", mItem.getUserId());
                        intent.putExtra("thisId", mUserId);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast(failueJson);
            }
        });
    }


    private void collection(Moment.Moments mItem) {//收藏

        CustomerDbHelper.collectionUserArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        isZan = true;
                    //    loadData(true);
                        ToastUtils.showToast("收藏成功");
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


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
            }
        };
        contentView.findViewById(R.id.ll_red).setOnClickListener(listener);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //可见的并且是初始化之后才加载
        if (isPrepared && isVisibleToUser) {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }


    private void anim(){
        Animation rotate = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin); //设置插值器
        rotate.setDuration(500);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        shuaxin_btn.startAnimation(rotate);
    }
}
