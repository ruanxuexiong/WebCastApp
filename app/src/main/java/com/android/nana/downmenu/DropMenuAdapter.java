package com.android.nana.downmenu;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.TradeEntity;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.typeview.DoubleListView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/1/3.
 */

public class DropMenuAdapter implements MenuAdapter {

    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private String[] titles;
    private ArrayList<TradeEntity.Profession> mTradeData = new ArrayList<>();
    private ArrayList<ProvinceBean.Area> mDataList = new ArrayList<>();

    public DropMenuAdapter(Context context, String[] titles, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.titles = titles;
        this.onFilterDoneListener = onFilterDoneListener;
    }

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 2) {
            return 0;
        }
        return UIUtil.dp(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        View view = parentContainer.getChildAt(position);

        switch (position) {
            case 0:
                view = createSingleListView();
                break;
            case 1:
                view = createDoubleListView();
                break;
            case 2:
                view = createSingleGridView();
                break;
        }
        return view;
    }

    private View createSingleListView() {

        final DoubleListView<ProvinceBean.Area, ProvinceBean.Area.City> comTypeDoubleListView = new DoubleListView<ProvinceBean.Area, ProvinceBean.Area.City>(mContext)
                .leftAdapter(new SimpleTextAdapter<ProvinceBean.Area>(null, mContext) {
                    @Override
                    public String provideText(ProvinceBean.Area filterType) {
                        return filterType.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 44), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        if (onFilterDoneListener != null){
                            onFilterDoneListener.onClickCity();
                        }
                    }
                })
                .rightAdapter(new SimpleTextAdapter<ProvinceBean.Area.City>(null, mContext) {
                    @Override
                    public String provideText(ProvinceBean.Area.City city) {
                        return city.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 30), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                })
                .onLeftItemClickListener(new DoubleListView.OnLeftItemClickListener<ProvinceBean.Area, ProvinceBean.Area.City>() {
                    @Override
                    public List<ProvinceBean.Area.City> provideRightList(ProvinceBean.Area leftAdapter, int position) {
                        return leftAdapter.getLists();
                    }
                })
                .onRightItemClickListener(new DoubleListView.OnRightItemClickListener<ProvinceBean.Area, ProvinceBean.Area.City>() {
                    @Override
                    public void onRightItemClick(ProvinceBean.Area item, ProvinceBean.Area.City city) {
                        FilterUrl.instance().singleListPosition = item.getName();
                        FilterUrl.instance().singleListPosition = city.getName();

                        FilterUrl.instance().position = 0;
                        FilterUrl.instance().positionTitle = item.getName() + city.getName();

                        FilterUrl.instance().mProfessionId = null;//清空行业ID
                        FilterUrl.instance().mNextId = null;//清空二级行业ID
                        FilterUrl.instance().mProvinceId = item.getId();//省份ID
                        FilterUrl.instance().mCityId = city.getId();//城市ID
                        onFilterDone();
                    }
                });


        FunctionDbHelper.getCitys(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        for (ProvinceBean.Area item : parseData(successJson)) {
                            if (!mDataList.contains(item)) {
                                mDataList.add(item);
                            }
                        }
                        comTypeDoubleListView.setLeftList(mDataList, 0);
                        comTypeDoubleListView.setRightList(mDataList.get(0).getLists(), -1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

        //初始化选中.
        comTypeDoubleListView.getLeftListView().setBackgroundColor(mContext.getResources().getColor(R.color.b_c_fafafa));

        return comTypeDoubleListView;
    }


    private View createDoubleListView() {
        final DoubleListView<TradeEntity.Profession, TradeEntity.Profession.Next> comTypeDoubleListView = new DoubleListView<TradeEntity.Profession, TradeEntity.Profession.Next>(mContext)
                .leftAdapter(new SimpleTextAdapter<TradeEntity.Profession>(null, mContext) {

                    @Override
                    public String provideText(TradeEntity.Profession profession) {
                        return profession.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 44), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        if (onFilterDoneListener != null){
                            onFilterDoneListener.onClickCity();
                        }
                    }
                })
                .rightAdapter(new SimpleTextAdapter<TradeEntity.Profession.Next>(null, mContext) {

                    @Override
                    public String provideText(TradeEntity.Profession.Next next) {
                        return next.getName();
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(UIUtil.dp(mContext, 30), UIUtil.dp(mContext, 15), 0, UIUtil.dp(mContext, 15));
                        checkedTextView.setBackgroundResource(android.R.color.white);
                    }
                })
                .onLeftItemClickListener(new DoubleListView.OnLeftItemClickListener<TradeEntity.Profession, TradeEntity.Profession.Next>() {
                    @Override
                    public List<TradeEntity.Profession.Next> provideRightList(TradeEntity.Profession leftAdapter, int position) {
                        return leftAdapter.getNext_pro();
                    }

                })
                .onRightItemClickListener(new DoubleListView.OnRightItemClickListener<TradeEntity.Profession, TradeEntity.Profession.Next>() {
                    @Override
                    public void onRightItemClick(TradeEntity.Profession item, TradeEntity.Profession.Next childItem) {
                        FilterUrl.instance().doubleListLeft = item.getName();
                        FilterUrl.instance().doubleListRight = childItem.getName();

                        FilterUrl.instance().position = 1;
                        FilterUrl.instance().positionTitle = item.getName() + childItem.getName();

                        FilterUrl.instance().mProvinceId = null;//清空省份ID
                        FilterUrl.instance().mCityId = null;//清空城市ID

                        FilterUrl.instance().mProfessionId = item.getId();//行业ID
                        FilterUrl.instance().mNextId = childItem.getId();//二级行业ID
                        onFilterDone();
                    }
                });


        FunctionDbHelper.selectProfession(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        for (TradeEntity.Profession item : parseTradeData(successJson)) {
                            if (!mTradeData.contains(item)) {
                                mTradeData.add(item);
                            }
                        }
                        comTypeDoubleListView.setLeftList(mTradeData, 0);
                        comTypeDoubleListView.setRightList(mTradeData.get(0).getNext_pro(), -1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

        comTypeDoubleListView.getLeftListView().setBackgroundColor(mContext.getResources().getColor(R.color.b_c_fafafa));
        return comTypeDoubleListView;
    }


    private View createSingleGridView() {
        ReturnView view = new ReturnView(mContext);
        return view;
    }


    private void onFilterDone() {
        if (onFilterDoneListener != null) {
            onFilterDoneListener.onFilterDone(0, "", "");
        }
    }

    private ArrayList<ProvinceBean.Area> parseData(String result) {//城市
        ArrayList<ProvinceBean.Area> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray area = new JSONArray(data.getString("area"));
            if (area.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < area.length(); i++) {
                    ProvinceBean.Area bean = gson.fromJson(area.optJSONObject(i).toString(), ProvinceBean.Area.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    private ArrayList<TradeEntity.Profession> parseTradeData(String result) {//行业
        ArrayList<TradeEntity.Profession> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray area = new JSONArray(data.getString("profession"));
            if (area.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < area.length(); i++) {
                    TradeEntity.Profession bean = gson.fromJson(area.optJSONObject(i).toString(), TradeEntity.Profession.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

}
