package com.android.nana.menu;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.android.nana.R;

import java.util.List;

/**
 * Created by lenovo on 2018/3/19.
 */

public class WantedMenuFragment extends DialogFragment {


    private final String TAG = "BottomMenuFragment";
    private boolean isSize = false;//显示字体大小
    private String string = "";//回复删除

    public WantedMenuFragment() {
    }

    public WantedMenuFragment(boolean isSize) {
        this.isSize = isSize;
    }



    private List<MenuItem> menuItems;

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        getDialog().getWindow().setWindowAnimations(R.style.menu_animation);//添加一组进出动画
        View view = inflater.inflate(R.layout.fragment_bottom_menu, container, false);

        TextView tv_cancel =  view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WantedMenuFragment.this.dismiss();
            }
        });


        ListView lv_menu =  view.findViewById(R.id.lv_menu);

        WantedMenuItemAdapter menuItemAdapter = new WantedMenuItemAdapter(getActivity().getBaseContext(), this.menuItems, isSize);
        lv_menu.setAdapter(menuItemAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }

    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);
    }

    @Override
    public void onStop() {
        this.getView().setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_disappear));
        super.onStop();
    }


}
