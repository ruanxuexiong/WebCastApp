package com.android.nana.find.map;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.find.base.AvatarModel;
import com.android.nana.find.base.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/10/31.
 * 地图个性化设置
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRightTv;
    private String mUid;
    private ViewPager mPager;
    private List<View> mPagerList;
    private List<AvatarModel> mDataList;
    private LinearLayout mLlDot;
    private LayoutInflater inflater;
    //总页数
    private int pageCount;
    // 每一页显示的个数
    private int pageSize = 8;
    // 当前显示的是第几页
    private int curIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUid = getIntent().getStringExtra("uid");
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_set);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mPager = findViewById(R.id.view_pager);
        mLlDot = findViewById(R.id.ll_dot);
    }

    @Override
    protected void init() {
        mTitleTv.setText("个性化设置");
        mBackTv.setText("取消");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setCompoundDrawables(null, null, null, null);
        initData();
        inflater = LayoutInflater.from(this);
        //总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDataList.size() * 1.0 / pageSize);
        mPagerList = new ArrayList();

        for (int i = 0; i < pageCount; i++) {
            //每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.gridview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(this, mDataList, i, pageSize));
            mPagerList.add(gridView);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position + curIndex * pageSize;
                    Log.e("=post===",pos+"");
                }
            });
        }
        //设置适配器
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        //设置圆点
        setOvalLayout();
    }

    public void setOvalLayout() {
        for (int i = 0; i < pageCount; i++) {
            mLlDot.addView(inflater.inflate(R.layout.dot, null));
        }
        // 默认显示第一页
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.dot_selected);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                // 取消圆点选中
                mLlDot.getChildAt(curIndex)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal);
                // 圆点选中
                mLlDot.getChildAt(position)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected);
                curIndex = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            //动态获取资源ID，第一个参数是资源名，第二个参数是资源类型例如drawable，string等，第三个参数包名
            int imageId = getResources().getIdentifier("ic_category_" + i, "mipmap", getPackageName());
            mDataList.add(new AvatarModel(imageId));
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                break;
            default:
                break;
        }
    }
}
