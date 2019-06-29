package com.android.nana.customer.myincome;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseFragmentActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.nana.R;
import com.android.nana.listener.MyIncomeListener;
import com.android.nana.util.Constant;

/**
 * Created by Cristina on 2017/3/22.
 */
public class MyIncomeStatisticsActivity extends BaseFragmentActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle,mTxtSr,mTxtZc;
    private RadioGroup mRgTab;
    private FragmentBuilder mFragmentBuilder;

    @Override
    protected void locationData() {

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("收支明细");
        mTxtSr = (TextView) findViewById(R.id.my_income_statistics_txt_sr);
        mTxtZc = (TextView) findViewById(R.id.my_income_statistics_txt_zc);

        mRgTab = (RadioGroup) findViewById(R.id.my_income_statistics_tab_rg_menu);
    }

    @Override
    protected void init() {

        mTxtSr.setText(Html.fromHtml("<font color='#646464'>收入：</font><font color='#5C9B29'>+￥"+0+"</font>"));
        mTxtZc.setText(Html.fromHtml("<font color='#646464'>支出：</font><font color='#5C9B29'>-￥"+0+"</font>"));


        MyIncomeListener.getInstance().mOnMyIncomeListener = new MyIncomeListener.OnMyIncomeListener() {
            @Override
            public void result(String t, String s) {

                mTxtSr.setText(Html.fromHtml("<font color='#646464'>收入：</font><font color='#5C9B29'>+￥"+(TextUtils.isEmpty(t)?"0":t)+"</font>"));
                mTxtZc.setText(Html.fromHtml("<font color='#646464'>支出：</font><font color='#5C9B29'>-￥"+(TextUtils.isEmpty(s)?"0":s)+"</font>"));

            }
        };

    }

    @Override
    protected void initFragments() {

        mFragmentBuilder = new FragmentBuilder(this, R.id.my_income_statistics_tab_content);
        mFragmentBuilder.registerFragement("全部", MyIncomeStatisticsFragment.newInstance(Constant.IncomeStatistics.All));
        mFragmentBuilder.registerFragement("收入", MyIncomeStatisticsFragment.newInstance(Constant.IncomeStatistics.Income));
        mFragmentBuilder.registerFragement("支出", MyIncomeStatisticsFragment.newInstance(Constant.IncomeStatistics.Pay));

        mFragmentBuilder.switchFragment(0);

    }

    @Override
    protected void setListener() {

        mRgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.my_income_statistics_tab_rb_1:
                        mFragmentBuilder.switchFragment(0);
                        break;
                    case R.id.my_income_statistics_tab_rb_2:
                        mFragmentBuilder.switchFragment(1);
                        break;
                    case R.id.my_income_statistics_tab_rb_3:
                        mFragmentBuilder.switchFragment(2);
                        break;
                }
            }
        });

        mIbBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                MyIncomeStatisticsActivity.this.finish();

            }
        });



    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_my_income_statistics);
    }

}
