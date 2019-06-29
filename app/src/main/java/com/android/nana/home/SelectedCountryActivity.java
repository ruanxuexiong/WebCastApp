package com.android.nana.home;

import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.ResultRequestListModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.listener.HomeListener;
import com.android.nana.ui.UITableView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class SelectedCountryActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private List<String> mList;

    @Override
    protected void bindViews() {

        mList = new ArrayList<>();
        setContentView(R.layout.selected_country);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("选择国家");

    }

    @Override
    protected void init() {

        HomeDbHelper.areas(new IOAuthCallBack() {

            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                BaseModel mResult = new ResultRequestListModel(successJson);
                if (mResult.mIsSuccess) {

                    UITableView table = (UITableView) findViewById(R.id.selected_country_ll_tableview);
                    for (JSONObject s : mResult.ToList()) {
                        String name = JSONUtil.get(s,"name","");
                        table.addBasicItem(0, name, "");
                        mList.add(name);
                    }
                    table.setOnUITableClickLister(new UITableView.UITableClickLister() {
                        @Override
                        public void onClick(int index) {

                            String text = mList.get(index);
                            if (text.equals("中国")) {
                                if (HomeListener.getInstance().mOnHomeListener != null) {
                                    HomeListener.getInstance().mOnHomeListener.result(text);
                                    SelectedCountryActivity.this.finish();
                                }
                            } else {
                                UIHelper.showToast(SelectedCountryActivity.this, text+"暂时没有开通");
                            }

                        }
                    });
                    table.builder();
                }

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

    }
}
