package com.android.nana.inquiry;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.WantEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.LabelsView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lenovo on 2018/4/29.
 */

public class EvaluateFragment extends DialogFragment implements View.OnClickListener {

    private String mid;
    private String recordId;
    private String type;
    private ImageView mCloseIv;
    private TextView mSubTv;
    private ImageView mSureIv;
    private TextView mCommentTv;
    private CircleImageView mHeadIv;
    private TextView mNameTv;
    private TextView mTimeTv;
    private TextView mConsumptionTv;
    private RatingBar mRatingBar;
    private LabelsView mNameLv;
    private String mRatingStr;
    private String labes = "";

    private LinearLayout mCloseLl;
    private ImageView mRightCloseRl;
    private ArrayList<String> mLabels = new ArrayList<>();//存储选中标签

    public static EvaluateFragment newInstance(String mid, String recordId, String type) {
        EvaluateFragment fragment = new EvaluateFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("recordId", recordId);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
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
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明

        mid = getArguments().getString("mid");
        recordId = getArguments().getString("recordId");
        type = getArguments().getString("type");
        View view = inflater.inflate(R.layout.activity_evaluate, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCloseIv = view.findViewById(R.id.iv_close);
        mSubTv = view.findViewById(R.id.tv_sub);
        mSureIv = view.findViewById(R.id.iv_sure);
        mHeadIv = view.findViewById(R.id.iv_head);
        mNameTv = view.findViewById(R.id.tv_name);
        mTimeTv = view.findViewById(R.id.tv_time);
        mConsumptionTv = view.findViewById(R.id.tv_consumption);
        mRatingBar = view.findViewById(R.id.ratingBar);
        mNameLv = view.findViewById(R.id.lv_name);
        mCommentTv = view.findViewById(R.id.tv_comment);
        mCloseLl = view.findViewById(R.id.ll_close);
        mRightCloseRl = view.findViewById(R.id.iv_right_close);

        mCloseIv.setOnClickListener(this);
        mSubTv.setOnClickListener(this);


        if (type.equals("2")) {//2评论

            mCloseIv.setVisibility(View.VISIBLE);
            mCommentTv.setVisibility(View.VISIBLE);
            mSubTv.setVisibility(View.VISIBLE);
            mCloseLl.setVisibility(View.GONE);

            HomeDbHelper.evaluate(mid, recordId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            mLabels.clear();//清除选中标签
                            EvaluateBean bean = parseData(successJson);

                            mNameTv.setText(bean.getZxUser().getUsername());
                            mTimeTv.setText("联系时长：" + bean.getZxUser().getZx_time());
                            mConsumptionTv.setText(bean.getZxUser().getZx_money());

                            ImageLoader.getInstance().displayImage(bean.getZxUser().getAvatar(), mHeadIv);
                            if (null != bean.getZxUser().getStatus() && bean.getZxUser().getStatus().equals("1")) {
                                mSureIv.setVisibility(View.VISIBLE);
                            } else {
                                mSureIv.setVisibility(View.GONE);
                            }
                            mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                    mRatingStr = String.valueOf(rating);
                                }
                            });
                            ArrayList<String> label = new ArrayList<>();
                            for (int i = 0; i < bean.getOptions().size(); i++) {
                                label.add(bean.getOptions().get(i));
                            }
                            mNameLv.setSelectType(LabelsView.SelectType.MULTI);//标签多选
                            mNameLv.setLabels(label);
                            mNameLv.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                                @Override
                                public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                                    if (isSelect) {
                                        mLabels.add(labelText);
                                    } else {
                                        mLabels.remove(mNameLv.getLabels().get(position));
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void getFailue(String failueJson) {

                }
            });
        } else {//1代表查看评论
            mCloseIv.setVisibility(View.GONE);
            mCommentTv.setVisibility(View.GONE);
            mSubTv.setVisibility(View.GONE);
            mCloseLl.setVisibility(View.VISIBLE);
            mRightCloseRl.setOnClickListener(this);

            HomeDbHelper.evaluate(mid, recordId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            EvaluateBean bean = parseData(successJson);

                            mNameTv.setText(bean.getZxUser().getUsername());
                            mTimeTv.setText("联系时长：" + bean.getZxUser().getZx_time());
                            mConsumptionTv.setText(bean.getZxUser().getZx_money());
                            ImageLoader.getInstance().displayImage(bean.getZxUser().getAvatar(), mHeadIv);

                            if (bean.getZxUser().getStatus().equals("1")) {
                                mSureIv.setVisibility(View.VISIBLE);
                            } else {
                                mSureIv.setVisibility(View.GONE);
                            }
                            mRatingBar.setIsIndicator(true);
                            mRatingBar.setRating(Float.valueOf(bean.getEvaluate_new().getStars()));
                            mRatingBar.setNumStars(5);
                            ArrayList<String> label = new ArrayList<>();
                            for (int i = 0; i < bean.getOptions().size(); i++) {
                                label.add(bean.getOptions().get(i));
                            }
                            mNameLv.setSelectType(LabelsView.SelectType.NONE);//不可选
                            mNameLv.setLabels(label);
                            if (null != bean.getEvaluate_new() && bean.getEvaluate_new().getReviews_select().size() > 0) {
                                for (int i = 0; i < bean.getEvaluate_new().getReviews_select().size(); i++) {
                                    if (!"".equals(bean.getEvaluate_new().getReviews_select().get(i))) {
                                        mNameLv.setSingleSelect(Integer.valueOf(bean.getEvaluate_new().getReviews_select().get(i)));
                                    }
                                }
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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                this.dismiss();
                break;
            case R.id.tv_sub:
                if (TextUtils.isEmpty(mRatingStr)) {
                    ToastUtils.showToast("请选择星级评论!");
                    return;
                }

               /* if (mLabels.size() == 0) {
                    ToastUtils.showToast("请选择评论内容!");
                    return;
                }*/


                if (mLabels.size() > 0) {
                    for (int i = 0; i < mLabels.size(); i++) {
                        labes += mNameLv.getSelectLabels().get(i) + ",";
                    }
                }

                if (mLabels.size() == 0) {
                    labes = "";
                } else {
                    labes = labes.substring(0, labes.length() - 1);
                }


                HomeDbHelper.pushEvalution(mid, recordId, mRatingStr, labes, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {
                                ToastUtils.showToast("评论成功!");
                                EvaluateFragment.this.dismiss();
                                EventBus.getDefault().post(new WantEvent());//更新我要咨询谁界面
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        Log.e("====", failueJson);
                    }
                });
                break;
            case R.id.iv_right_close://关闭
                this.dismiss();
                break;
            default:
                break;
        }
    }

    public EvaluateBean parseData(String result) {//Gson 解析
        EvaluateBean detail = new EvaluateBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), EvaluateBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
