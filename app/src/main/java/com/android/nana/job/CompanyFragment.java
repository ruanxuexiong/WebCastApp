package com.android.nana.job;

import android.view.View;

import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;
import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;

/**
 * Created by lenovo on 2018/3/6.
 */

public class CompanyFragment extends BaseRequestFragment implements View.OnClickListener, OnFilterDoneListener, DropDownMenu.DropDownMenuSearchListener {


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_company;
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
    public void onClick(View view) {

    }

    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {

    }

    @Override
    public void onClickCity() {

    }

    @Override
    public void onSeniorClick() {

    }
}
