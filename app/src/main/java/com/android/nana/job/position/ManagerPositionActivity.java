package com.android.nana.job.position;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.WantedItemOnClickListener;
import com.android.nana.menu.WantedMenuFragment;
import com.android.nana.widget.StateButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/3/8.
 */

public class ManagerPositionActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private StateButton mAddBtn;

    private LinearLayout mStateLl;
    private TextView mStateTv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_add_job);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mAddBtn = findViewById(R.id.btn_add);

        mStateLl = findViewById(R.id.ll_state);
        mStateTv = findViewById(R.id.tv_state);
    }

    @Override
    protected void init() {
        mTitleTv.setText("管理求职意向");
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mStateLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_add:
                startActivity(new Intent(this, AddPositionActivity.class));
                break;
            case R.id.ll_state:
                bottomCommentMenu();
                break;
            default:
                break;
        }
    }

    private void bottomCommentMenu() {

        final WantedMenuFragment bottomMenuFragment = new WantedMenuFragment(true);
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("求职状态");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);


        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("离职-随时到岗");

        menuItem2.setWantedItemOnClickListener(new WantedItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                mStateTv.setText(menuItem.getText());
            }
        });

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setText("在职-暂不考虑");

        menuItem3.setWantedItemOnClickListener(new WantedItemOnClickListener(bottomMenuFragment, menuItem3) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                mStateTv.setText(menuItem.getText());
            }
        });
        MenuItem menuItem4 = new MenuItem();
        menuItem4.setText("在职-考虑机会");


        menuItem4.setWantedItemOnClickListener(new WantedItemOnClickListener(bottomMenuFragment, menuItem4) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                mStateTv.setText(menuItem.getText());
            }
        });
        MenuItem menuItem5 = new MenuItem();
        menuItem5.setText("在职-月内到岗");

        menuItem5.setWantedItemOnClickListener(new WantedItemOnClickListener(bottomMenuFragment, menuItem5) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                mStateTv.setText(menuItem.getText());
            }
        });
        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        menuItemList.add(menuItem3);
        menuItemList.add(menuItem4);
        menuItemList.add(menuItem5);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuCommentFragment");
    }
}
