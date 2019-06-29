package com.android.nana.wanted;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.viewpager.PositionPagerAdapter;
import com.android.nana.viewpager.PositionViewPager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINK on 2017/6/27.
 */

public class PositionActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackBtn;
    private TextView mTitle;
    private View view1, view2, view3;
    private ListView mListView1, mListView2, mListView3;
    private PositionViewPager mViewPager;
    private PositionAdapter mAdapter, mAdapter2, mAdapter3;
    private List<View> views = new ArrayList();

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_position);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.common_btn_back);
        mTitle = findViewById(R.id.common_txt_title);
        mViewPager = findViewById(R.id.viewpager);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.item_pager, null);
        view2 = inflater.inflate(R.layout.item_pager, null);
        view3 = inflater.inflate(R.layout.item_pager, null);

        mListView1 = view1.findViewById(R.id.listview1);
        mListView2 = view2.findViewById(R.id.listview1);
        mListView3 = view3.findViewById(R.id.listview1);

        WebCastDbHelper.getJobsList(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    List<JSONObject> list = JSONUtil.getList(jsonobject, "data");

                    Position position;
                    ArrayList<Position> mList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        position = new Position();
                        position.setName(list.get(i).getString("name"));
                        position.setId(list.get(i).getString("id"));
                        position.setOrderId(list.get(i).getString("orderId"));
                        position.setType(list.get(i).getString("type"));
                        position.setParentId(list.get(i).getString("parentId"));
                        position.setStatud(list.get(i).getJSONArray("second"));
                        mList.add(position);
                    }
                    mAdapter = new PositionAdapter(PositionActivity.this, mList);

                    mListView1.setAdapter(mAdapter);

                    views.add(view1);
                    views.add(view2);
                    mViewPager.setAdapter(new PositionPagerAdapter(views));

                    mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mAdapter != null)
                                mAdapter.setSelectedItem(position);
                            if (mAdapter != null)
                                mAdapter.setSelectedItem(-1);

                            if (views.contains(view3)) {
                                views.remove(view3);
                                mViewPager.getAdapter().notifyDataSetChanged();
                            }
                            Position mPosition = (Position) parent.getItemAtPosition(position);

                            Position position1;
                            ArrayList<Position> mList = new ArrayList<>();
                            for (int i = 0; i < mPosition.getStatud().length(); i++) {
                                try {
                                    JSONObject jsonObject = new JSONObject(mPosition.getStatud().get(i).toString());
                                    position1 = new Position();
                                    position1.setName(jsonObject.getString("name"));
                                    position1.setId(jsonObject.getString("id"));
                                    position1.setOrderId(jsonObject.getString("orderId"));
                                    position1.setType(jsonObject.getString("type"));
                                    position1.setParentId(jsonObject.getString("parentId"));
                                    position1.setStatud(jsonObject.getJSONArray("three"));
                                    mList.add(position1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (mAdapter2 == null) {
                                mAdapter2 = new PositionAdapter(getApplicationContext(), mList);
                                mAdapter2.setNormalBackgroundResource(R.color.white);
                                mListView2.setAdapter(mAdapter2);
                            } else {
                                mAdapter2.setData(mList);
                                mAdapter2.notifyDataSetChanged();
                            }

                        }
                    });

                    mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mAdapter2 != null) {
                                mAdapter2.setSelectedItem(position);
                            }
                            if (views.contains(view3)) {
                                views.remove(view3);
                            }
                            Position mPosition = (Position) parent.getItemAtPosition(position);

                            Position position2;
                            ArrayList<Position> mList = new ArrayList<>();
                            for (int i = 0; i < mPosition.getStatud().length(); i++) {
                                try {
                                    JSONObject jsonObject = new JSONObject(mPosition.getStatud().get(i).toString());
                                    position2 = new Position();
                                    position2.setName(jsonObject.getString("name"));
                                    position2.setId(jsonObject.getString("id"));
                                    position2.setOrderId(jsonObject.getString("orderId"));
                                    position2.setType(jsonObject.getString("type"));
                                    position2.setParentId(jsonObject.getString("parentId"));
                                    mList.add(position2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mAdapter3 == null) {
                                mAdapter3 = new PositionAdapter(getApplicationContext(), mList);
                                mAdapter3.setHasDivider(false);
                                mListView3.setDividerHeight(1);
                                mListView3.setBackgroundColor(getResources().getColor(R.color.grey_f5));
                                mListView3.setAdapter(mAdapter3);
                            } else {
                                mAdapter3.setData(mList);
                                mAdapter3.notifyDataSetChanged();
                            }


                            views.add(view3);
                            mViewPager.getAdapter().notifyDataSetChanged();
                            mViewPager.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mViewPager.setCurrentItem(views.size() - 1);
                                }
                            }, 300);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position mPosition = (Position) parent.getItemAtPosition(position);
                EventBus.getDefault().post(new PositionEvent(mPosition));
                PositionActivity.this.finish();
            }
        });
    }

    @Override
    protected void init() {
        mTitle.setText("选择职位类型");
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
        }
    }
}
