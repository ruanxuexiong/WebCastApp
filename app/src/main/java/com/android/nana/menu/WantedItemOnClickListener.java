package com.android.nana.menu;

import android.util.Log;
import android.view.View;

/**
 * Created by lenovo on 2018/3/19.
 */

public abstract class WantedItemOnClickListener implements View.OnClickListener{


    public WantedItemOnClickListener(WantedMenuFragment _bottomMenuFragment, MenuItem _menuItem) {
        this.bottomMenuFragment = _bottomMenuFragment;
        this.menuItem = _menuItem;
    }


    private final String TAG = "MenuItemOnClickListener";

    public WantedMenuFragment getWantedMenuFragment() {
        return bottomMenuFragment;
    }

    public void setWantedMenuFragment(WantedMenuFragment bottomMenuFragment) {
        this.bottomMenuFragment = bottomMenuFragment;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    private WantedMenuFragment bottomMenuFragment;
    private MenuItem menuItem;

    @Override
    public void onClick(View v){

        Log.i(TAG, "onClick: ");

        if(bottomMenuFragment != null && bottomMenuFragment.isVisible()) {
            bottomMenuFragment.dismiss();
        }

        this.onClickMenuItem(v, this.menuItem);
    }
    public abstract void onClickMenuItem(View v, MenuItem menuItem);
}
