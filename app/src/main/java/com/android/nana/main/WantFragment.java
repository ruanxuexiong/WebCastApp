package com.android.nana.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/4/26.
 */

public class WantFragment extends DialogFragment implements View.OnClickListener {


    private TextView mTitleTv;
    private TextView mContentTv;
    private StateButton mSendBtn;
    private ImageView mCloseIv;
    private String mid;
    private HtmlBean bean;
    private String isWant = null;

    public static WantFragment newInstance(String isWant) {
        WantFragment fragment = new WantFragment();
        Bundle bundle = new Bundle();
        bundle.putString("isWant", isWant);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_send:
                Intent intent = new Intent(getActivity(), BaiduWebViewActivity.class);
                if (isWant.equals("1")) {
                    intent.putExtra("beSpecialist", bean.getBeSpecialist());//成为专家
                } else {
                    intent.putExtra("beCustomer", bean.getBeCustomer());//成为客户
                }
                intent.putExtra("mid", mid);
                startActivity(intent);
                break;
            case R.id.iv_close:
                this.dismiss();
                if ("1".equals(isWant)) {//1 代表专家
                    SharedPreferencesUtils.removeParameter(getActivity(),"who");
                } else {
                    SharedPreferencesUtils.removeParameter(getActivity(),"want");
                }
                break;
            default:
                break;
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        if (null != getArguments().getString("isWant")) {
            isWant = getArguments().getString("isWant");
        }
        View view = inflater.inflate(R.layout.activity_want, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleTv = view.findViewById(R.id.tv_title);
        mContentTv = view.findViewById(R.id.tv_content);
        mSendBtn = view.findViewById(R.id.btn_send);
        mCloseIv = view.findViewById(R.id.iv_close);

        mSendBtn.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);


        mid = (String) SharedPreferencesUtils.getParameter(getContext(), "userId", "");
        initData(mid);
    }

    private void initData(String mid) {
        CustomerDbHelper.AdvisoryHtmlUrl(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        bean = parseData(successJson);
                        if (isWant.equals("1")) {
                            mContentTv.setText(bean.getBeSpecialistTip());
                        } else {
                            mTitleTv.setText("成为客户");
                            mSendBtn.setText("成为客户");
                            mContentTv.setText(bean.getBeCustomerTip());
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


    public HtmlBean parseData(String result) {//Gson 解析
        HtmlBean detail = new HtmlBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), HtmlBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
