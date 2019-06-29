package com.android.nana.partner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.PlanAdapter;
import com.android.nana.dbhelper.WebCastDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/8.
 */

public class PlanFragment extends Fragment implements View.OnClickListener {

    private TextView mActivityTv, mBillingTv, mProfitTv, mNumTv, mBtnTv;
    private String mUid;
    private ImageView huodong_share,huodong_jiangli,huodong_img;
    private Dialog allMsg, mActivitDialog;
    private View allMsgView, mActivityView;
    private String mUserName, mAvatar, mIntroduce, mMsg, mUrl;
    private ListView mListView;
    private PlanAdapter mAdapter;
    private FloatingActionButton mFaButn;
    private ArrayList<String> mList = new ArrayList<>();

    public PlanFragment newInstance(String mUid) {
        PlanFragment fragment = new PlanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mUid", mUid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        huodong_img=view.findViewById(R.id.huodong_img);
        mActivityTv =  view.findViewById(R.id.tv_billing);
        mBillingTv =  view.findViewById(R.id.tv_activity);
        huodong_share=view.findViewById(R.id.huodong_share);
        mBtnTv =  view.findViewById(R.id.tv_btn);
        mNumTv =  view.findViewById(R.id.tv_num);
        mProfitTv =  view.findViewById(R.id.tv_profit);
        huodong_jiangli=view.findViewById(R.id.huodong_jiangli);
        mActivityTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mBillingTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (null != getArguments().getString("mUid")) {
            mUid = getArguments().getString("mUid");
            loadData(mUid);
        }

        huodong_jiangli.setOnClickListener(this);
        mBtnTv.setOnClickListener(this);
        huodong_share.setOnClickListener(this);
        mBillingTv.setOnClickListener(this);
        mActivityTv.setOnClickListener(this);

        allMsgView = LayoutInflater.from(getActivity()).inflate(R.layout.billing_dialog, null);
        allMsg = new AlertDialog.Builder(getActivity()).create();
        allMsg.setCanceledOnTouchOutside(false);

        mActivityView = LayoutInflater.from(getActivity()).inflate(R.layout.partner_activity_dialog, null);
        mActivitDialog = new AlertDialog.Builder(getActivity()).create();
        mActivitDialog.setCanceledOnTouchOutside(false);

        ImageButton imgBtn_dialog = allMsgView.findViewById(R.id.dialog_pre_entry_close);
        imgBtn_dialog.setOnClickListener(this);

        ImageButton imageButton = mActivityView.findViewById(R.id.close_iv);
        imageButton.setOnClickListener(this);
        if (null != getArguments().getString("mUid")) {
            mUid = getArguments().getString("mUid");
            loadData(mUid);
        }

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();


        ViewGroup.LayoutParams lp = huodong_img.getLayoutParams();
        lp.width = width;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        huodong_img.setLayoutParams(lp);

        huodong_img.setMaxWidth(width);
        huodong_img.setMaxHeight(height * 5); //这里其实可以根据需求而定，我这里测试为最大宽度的5倍

/*
        mListView = view.findViewById(R.id.list_view);
        mFaButn = view.findViewById(R.id.fab);
        mList.add("1");
        mAdapter = new PlanAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);

        mFaButn.setOnClickListener(this);*/
    }

    private void loadData(String mUid) {

        WebCastDbHelper.userInfo(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                       /* mProfitTv.setText(jsonObject1.getString("balance") + "元");
                        mNumTv.setText(jsonObject1.getString("num") + "人");
*/
                        mUserName = jsonObject1.getString("shareTitle");
                        mAvatar = jsonObject1.getString("shareLogo");
                        mIntroduce = jsonObject1.getString("shareDesc");
                        mMsg = jsonObject1.getString("message");
                        mUrl = jsonObject1.getString("shareUrl");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.huodong_jiangli:
                allMsg.show();
                allMsg.getWindow().setContentView(allMsgView);
                break;
            case R.id.huodong_share:
                share();
                break;
            case R.id.tv_btn:
                share();
                break;
            case R.id.tv_billing:
                allMsg.show();
                allMsg.getWindow().setContentView(allMsgView);
                break;
            case R.id.tv_activity:
                mActivitDialog.show();
                mActivitDialog.getWindow().setContentView(mActivityView);
                break;
            case R.id.dialog_pre_entry_close:
                allMsg.dismiss();
                break;
            case R.id.close_iv:
                mActivitDialog.dismiss();
                break;
            case R.id.fab:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PartnerFragment partnerFragment = PartnerFragment.newInstance(mUid, mUserName, mAvatar, mIntroduce, mMsg, mUrl);
                partnerFragment.show(fragmentManager, "fragment_bottom_dialog");
                break;
            default:
                break;
        }
    }

    private void share() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PartnerFragment dialog = PartnerFragment.newInstance(mUid, mUserName, mAvatar, mIntroduce, mMsg, mUrl);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
