package com.android.nana.customer.attention;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.common.base.BaseRequestFragment;
import com.android.common.builder.FragmentBuilder;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.CategoryEntity;
import com.android.nana.bean.CategoryInfoEntity;
import com.android.nana.dbhelper.WebCastDbHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@SuppressLint("ValidFragment")
public class AttentionFragment extends BaseRequestFragment {

    private RadioGroup mRgList;

    /**
     * Item宽度
     */
    private int mItemWidth = 0;
    private FragmentBuilder mFragmentBuilder;

    private String mType;
    private boolean mIsFirstInto;

    public AttentionFragment() {

    }

    public static AttentionFragment newInstance(String type) {
        AttentionFragment f = new AttentionFragment();
        Bundle b = new Bundle();
        b.putString("Type", type);
        f.setArguments(b);
        return f;
    }

    @Override
    protected int getLayoutId() {

        Bundle args = getArguments();

        if (args != null) mType = args.getString("Type");

        //  mItemWidth = UIUtil.getInstance().getWindowsWidth(getActivity()) / 7 + 10;
        return R.layout.attention_fragment;

    }

    @Override
    protected void findViewById() {
    }

    private List<CategoryInfoEntity> mList = new ArrayList<>();

    @Override
    protected void init() {

        DBModel dbModel = DBModel.get("queryCategory");
        if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {

        } else {

        }
        WebCastDbHelper.lists(mIOAuthCallBack);

    }

    @Override
    protected void setListener() {


    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(getActivity());

        }

        @Override
        public void getSuccess(String success) {


            BaseModel mResultDetailModel = new ResultRequestModel(success);
            if (mResultDetailModel.mIsSuccess) {

                DBModel.saveOrUpdate("queryCategory", success);
                mFragmentBuilder = new FragmentBuilder(getChildFragmentManager(), R.id.attention_fragment_tab_content, mRgList);

                mFragmentBuilder.registerFragement("全部", AttentionItemFragment.newInstance("0", mType));

                JSONObject jsonObject = JSONUtil.getStringToJson(success);
                List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");

                for (int i = 0; i < lists.size(); i++) {
                    JSONObject object = lists.get(i);
                    List<JSONObject> list = JSONUtil.getList(object, "lists");

                    String id = JSONUtil.get(object, "id", "");
                    String name = JSONUtil.get(object, "name", "");
                    String picture = JSONUtil.get(object, "picture", "");
                    List<CategoryEntity> datas = new ArrayList<>();
                    if (list != null) {
                        for (JSONObject object1 : list) {
                            datas.add(new CategoryEntity(object1.optString("id"), object1.optString("name"), false));
                        }
                    }
                    mList.add(new CategoryInfoEntity(id, name, picture, datas));
                    mFragmentBuilder.registerFragement(name, AttentionItemFragment.newInstance(id, mType));
                }

                for (int i = 0; i < mList.size(); i++) {
                    RadioButton radioButton = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.radio_button_item, null);
                    radioButton.setId(i);
                    radioButton.setText(mList.get(i).getName());
                    if (i == 0) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }
                }

                mFragmentBuilder.switchFragment(0);
                mIsFirstInto = true;
            } else {

                UIHelper.showToast(getActivity(), mResultDetailModel.mMessage);
            }
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(getActivity(), "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };

}
