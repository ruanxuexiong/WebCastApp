package com.android.nana.webcast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.common.models.DBModel;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.WebCastItemAdapter;
import com.android.nana.eventBus.CategoryEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lenovo on 2017/7/30.
 */

public class CategoryDialogFragment extends DialogFragment {

    private int num = 1;

    public static CategoryDialogFragment newInstance() {
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        return fragment;
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
        wlp.gravity = Gravity.TOP;
        wlp.x = 0;
        wlp.y = 200;
        getDialog().getWindow().setAttributes(wlp);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明


        View view = inflater.inflate(R.layout.category_fragment, container, false);

        final DBModel dbModel = DBModel.get("queryCategory");
        if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {
            JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
            List<JSONObject> mList = JSONUtil.getList(jsonObject, "data");

            if (mList.size() > 0 && num == 1) {
                num++;
                for (int i = 0; i < mList.size(); i++) {
                    JSONObject object = mList.get(i);
                    GridView mGvList = view.findViewById(R.id.webcast_item_gv_list);
                    View mView = view.findViewById(R.id.category_content);
                    WebCastItemAdapter adapter = new WebCastItemAdapter(getActivity());
                    mGvList.setAdapter(adapter);

                    final String Id = JSONUtil.get(object, "id", "");
                    final String name = JSONUtil.get(object, "name", "");

                    List<JSONObject> list = JSONUtil.getList(object, "lists");
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();

                    // 子分类
                    mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            JSONObject object = (JSONObject) parent.getItemAtPosition(position);
                            EventBus.getDefault().post(new CategoryEvent(name, Id, JSONUtil.get(object, "id", "0"), dbModel));
                            dismiss();
                        }
                    });

                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });
                }
            }

        }
        return view;
    }
}
