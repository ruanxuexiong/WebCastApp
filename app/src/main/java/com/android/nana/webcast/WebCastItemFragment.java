package com.android.nana.webcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.ui.selectmenu.SelectMenuView;
import com.android.common.ui.selectmenu.holder.CustomLevelHolder;
import com.android.common.ui.selectmenu.holder.SingleLevelHolder;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.bean.WebCastEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.JsonBean;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JsonFileReader;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class WebCastItemFragment extends BaseRequestFragment implements PullToRefreshLayout.OnRefreshListener, WebCasetAdapter.WebListener, View.OnClickListener {

    private SelectMenuView mLlSelectMenu;
    private SingleLevelHolder mSingleLevelHolder;
    private CustomLevelHolder mCustomLevelHolder;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private int page = 1;

    private TextView mCityTv;
    private RadioButton mRadioMale, mRadioFemale, mRadioUnlimited;
    private EditText mMinEt, mHigEt;
    private RelativeLayout mCancelRl, mSureRl;
    private View mSelectorView;

    // 筛选

    private String mUserId;
    private String mOrderByType = "total";
    private String mColumnId;
    private WebCasetAdapter mAdapter;

    private String cityId = "";
    private String sex = "";
    private String lowPrice = "";//最低见面金额
    private String hightPrice = "";//最高见面金额

    private ArrayList<WebCastEntity> mDataList = new ArrayList<>();

    private UserInfo mUserInfo;
    private AppointmentModel mAppointmentModel;

    //城市
    private OptionsPickerView pvOptions;
    //  private String cityid = "1", salary, mLowStr, mTopStr;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
    private boolean isTalking = false;

    public static WebCastItemFragment newInstance(String columnId) {
        WebCastItemFragment f = new WebCastItemFragment();
        Bundle b = new Bundle();
        b.putString("ColumnId", columnId);
        f.setArguments(b);
        return f;
    }


    public static WebCastItemFragment newInstance(String columnId, String count) {
        WebCastItemFragment f = new WebCastItemFragment();
        Bundle b = new Bundle();
        b.putString("ColumnId", columnId);
        b.putString("count", count);
        f.setArguments(b);
        return f;
    }

    private void loadWebData(String mid, String cityId, String sex, String lowPrice, String hightPrice, String tag, String order, int page) {


        CustomerDbHelper.searchFilterUser(mid, cityId, sex, lowPrice, hightPrice, tag, order, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (WebCastEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                mMultipleStatusView.empty();
                            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.webcast_item_fragment;
    }

    @Override
    protected void findViewById() {
        mLlSelectMenu = (SelectMenuView) findViewById(R.id.webcast_item_fragment_smv);
        mLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mListView = (PullableListView) findViewById(R.id.content_view);
        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);

        mAdapter = new WebCasetAdapter(getContext(), mDataList, WebCastItemFragment.this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        if (NetWorkUtils.isNetworkConnected(getContext())) {
            Bundle args = getArguments();
            if (null != args) {
                mColumnId = args.getString("ColumnId");
                mUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
                page = 1;
                if (mColumnId.equals("category")) {
                    loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
                } else {
                    mLayout.autoRefresh();
                }
            }
        } else {
            mMultipleStatusView.noNetwork();
        }

        mAppointmentModel = new AppointmentModel(getActivity());
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(getActivity(), "userInfo", UserInfo.class);


        // 筛选
        mSelectorView = LayoutInflater.from(getActivity()).inflate(R.layout.webcast_selector, null);
        mCityTv = (TextView) mSelectorView.findViewById(R.id.tv_city);
        mMinEt = (EditText) mSelectorView.findViewById(R.id.et_min);
        mHigEt = (EditText) mSelectorView.findViewById(R.id.et_hig);
        mCancelRl = (RelativeLayout) mSelectorView.findViewById(R.id.cancel_rl);
        mSureRl = (RelativeLayout) mSelectorView.findViewById(R.id.view_sure);
        mRadioMale = (RadioButton) mSelectorView.findViewById(R.id.radio_male);
        mRadioFemale = (RadioButton) mSelectorView.findViewById(R.id.radio_female);
        mRadioUnlimited = (RadioButton) mSelectorView.findViewById(R.id.radio_unlimited);

        mCityTv.setOnClickListener(this);
        mCancelRl.setOnClickListener(this);
        mSureRl.setOnClickListener(this);

        mCustomLevelHolder = new CustomLevelHolder(getActivity(), mLlSelectMenu, 0);
        mCustomLevelHolder.setTitle("筛选");
        mCustomLevelHolder.setCustomContentView(mSelectorView);

        // 被邀请次数
        List<String> mSingleDataList = new ArrayList<>();
        mSingleDataList.add("综合排序");
        //    mSingleDataList.add("被邀请视频次数");
        mSingleDataList.add("见面金额");

        mSingleLevelHolder = new SingleLevelHolder(getActivity(), mLlSelectMenu);
        mSingleLevelHolder.setTitle("综合排序");
        mSingleLevelHolder.refreshData(mSingleDataList);
        mSingleLevelHolder.setOnSingleLevelSelectedListener(new SingleLevelHolder.OnSingleLevelSelectedListener() {
            @Override
            public void onSingleLevelSelected(String info) {

                switch (info) {
                    case "综合排序":
                        mOrderByType = "total";
                        break;
                    case "见面金额":
                        mOrderByType = "money";
                        break;
                 /*   case "被邀请视频次数":
                        mOrderByType = "invite";
                        break;*/
                }
                loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
            }
        });

        mLlSelectMenu.initLevelHolder(mSingleLevelHolder, mCustomLevelHolder);
        mLlSelectMenu.postInvalidateView();
    }

    @Override
    protected void setListener() {

        mLayout.setOnRefreshListener(this);
        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                page = 1;
                mMultipleStatusView.loading();
                loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
                mMultipleStatusView.dismiss();
            }
        });
    }


    public ArrayList<WebCastEntity> parseData(String result) {//Gson 解析
        ArrayList<WebCastEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                WebCastEntity entity = gson.fromJson(data.optJSONObject(i).toString(), WebCastEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
            }
        }, 500);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;

        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
            }
        }, 500);
    }

    @Override
    public void onMakeClick(View view) {
        WebCastEntity mItem = mDataList.get((Integer) view.getTag());
        mAppointmentModel.init(mUserInfo.getId(), mItem.getId(), mUserInfo.getPayPassword());
        mAppointmentModel.doDialog(mItem.getMoney(), mItem.getUsername());
    }


    @Override
    public void onContentClick(View view) {
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(View view) {

        WebCastEntity mItem = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", mItem.getId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getActivity().getPackageName());
        getActivity().startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_city:

                if (isSHowKeyboard(getContext(), v)) {
                    Log.e("显示软键盘", "");
                }

                parseJson(JsonFileReader.getJson(getActivity(), "city_data.json"));
                cityShow();
                break;
            case R.id.cancel_rl:
                mCityTv.setText("不限");
                mRadioUnlimited.setChecked(true);
                mMinEt.setText("");
                mHigEt.setText("");
                mLlSelectMenu.dismissPopupWindow();
                break;
            case R.id.view_sure:
                lowPrice = mMinEt.getText().toString().trim();
                hightPrice = mHigEt.getText().toString().trim();
                if (mRadioMale.isChecked()) {
                    sex = "1";
                }
                if (mRadioFemale.isChecked()) {
                    sex = "2";
                }
                loadWebData(mUserId, cityId, sex, lowPrice, hightPrice, mColumnId, mOrderByType, page);
                mLlSelectMenu.setOptionTitle(1, "筛选");
                mLlSelectMenu.dismissPopup();
                break;
        }
    }

    private void cityShow() {
        pvOptions = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options2Items.get(options1).get(options2);

                cityId = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id

                mCityTv.setText(tx);
            }
        }).setCancelColor(getResources().getColor(R.color.green)).setSubmitColor(getResources().getColor(R.color.green)).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }

    private void parseJson(String successJson) {
        ArrayList<JsonBean> jsonBean = parseCityData(successJson);
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            options1ItemName.add(jsonBean.get(i).getName());
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）

            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getLists().size(); c++) {//遍历该省份的所有城市

                String CityName = jsonBean.get(i).getLists().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                City_AreaList.add("");
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        dismissProgressDialog();

    }

    public ArrayList<JsonBean> parseCityData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public static boolean isSHowKeyboard(Context context, View v) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            imm.showSoftInput(v, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }
}
