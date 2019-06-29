package com.android.nana.menu;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nana.R;

import java.util.List;

/**
 * Created by guorui.he on 2016/6/19.
 */
public class MenuItemAdapter extends BaseAdapter {


    private Context context;//运行上下文

    private LayoutInflater listContainer;  //视图容器
    private View mView;

    private List<MenuItem> menuItems;
    private boolean isSize;
    private String str;

    public MenuItemAdapter(Context _context, List<MenuItem> _menuItems, boolean isSize, String str) {
        this.context = _context;
        this.listContainer = LayoutInflater.from(_context);
        this.menuItems = _menuItems;
        this.isSize = isSize;
        this.str = str;
    }

    @Override
    public int getCount() {
        return this.menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= menuItems.size() || position < 0) {
            return null;
        } else {
            return menuItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            view = listContainer.inflate(R.layout.menu_item, null);
        }
        MenuItem menuItem = menuItems.get(position);



        TextView textView =  view.findViewById(R.id.menu_item);
        mView = view.findViewById(R.id.view);
        textView.setText(menuItem.getText());
        if (menuItems.size() == 1) {
            textView.setBackgroundResource(R.drawable.bottom_menu_btn_selector);
        } else if (position == 0) {
            textView.setBackgroundResource(R.drawable.bottom_menu_top_btn_selector);
        } else if (position < menuItems.size() - 1) {
            textView.setBackgroundResource(R.drawable.bottom_menu_mid_btn_selector);
        } else {
            textView.setBackgroundResource(R.drawable.bottom_menu_bottom_btn_selector);
        }

        if (menuItem.getStyle() == MenuItem.MenuItemStyle.COMMON && !str.equals("") && isSize) {
            if (position == 0) {
                textView.setTextSize(12);
                textView.setTextColor(ContextCompat.getColor(context, R.color.green_99));
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.green_33));
            }
        } else if (menuItem.getStyle() == MenuItem.MenuItemStyle.COMMON && isSize) {
            textView.setTextSize(12);
            textView.setTextColor(ContextCompat.getColor(context, R.color.green_99));
        } else if (menuItem.getStyle() == MenuItem.MenuItemStyle.COMMON) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.green_33));
        } else if (menuItem.getStyle() == MenuItem.MenuItemStyle.BLUE){
            textView.setTextColor(ContextCompat.getColor(context, R.color.blue_007));
        }else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.bottom_menu_btn_text_stress_color));
        }
        MenuItemOnClickListener _menuItemOnClickListener = menuItem.getMenuItemOnClickListener();
        if (_menuItemOnClickListener != null) {
            textView.setOnClickListener(_menuItemOnClickListener);
        }
        return view;
    }
}
