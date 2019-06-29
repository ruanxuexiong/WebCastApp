package com.android.nana.card;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.activity.BasePagerAdapter;
import com.android.nana.activity.NoScrollViewPager;

import java.util.Calendar;

/**
 * Created by lenovo on 2018/1/16.
 */

public class CardMainActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;

    private CheckBox checkBoxCard, checkBoxCardClip;
    private View mCardView, mMeView;
    private int selectedItem = 0;
    private long mLastClickTime = 0;
    private int CLICK_STEP = 200;
    private BasePagerAdapter mPagerAdapter;
    private NoScrollViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_card_main);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);

        mViewPager = findViewById(R.id.container);
        checkBoxCard = findViewById(R.id.checkBox_card);
        checkBoxCardClip = findViewById(R.id.checkBox_me);
        mCardView = findViewById(R.id.navigation_card);
        mMeView = findViewById(R.id.navigation_me);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发名片");
        mBackTv.setVisibility(View.VISIBLE);
        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedItem = mViewPager.getCurrentItem();
        setSelecteIcon();
    }

    private void setSelecteIcon() {
        Drawable drawableCard = getResources().getDrawable(R.drawable.icon_card_unselect);
        drawableCard.setBounds(0, 0, drawableCard.getMinimumWidth(), drawableCard.getMinimumHeight());
        checkBoxCard.setCompoundDrawables(null, drawableCard, null, null);
        checkBoxCard.setTextColor(getResources().getColor(R.color.grey_99));

        Drawable drawableCardClip = getResources().getDrawable(R.drawable.icon_card_clip_unselect);
        drawableCardClip.setBounds(0, 0, drawableCardClip.getMinimumWidth(), drawableCardClip.getMinimumHeight());
        checkBoxCardClip.setCompoundDrawables(null, drawableCardClip, null, null);
        checkBoxCardClip.setTextColor(getResources().getColor(R.color.grey_99));

        switch (selectedItem) {
            case 0:
                Drawable drawable = getResources().getDrawable(R.drawable.icon_card_select);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                checkBoxCard.setCompoundDrawables(null, drawable, null, null);
                checkBoxCard.setTextColor(getResources().getColor(R.color.green));
                break;
            case 1:
                Drawable drawable1 = getResources().getDrawable(R.drawable.icon_card_clip_select);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                checkBoxCardClip.setCompoundDrawables(null, drawable1, null, null);
                checkBoxCardClip.setTextColor(getResources().getColor(R.color.green));
                break;
            default:
                break;
        }
    }

    private void setAdapter() {
        FragmentManager fragmentManager = CardMainActivity.this.getSupportFragmentManager();
        String[] titles = new String[]{"我的名片", "名片夹"};
        Fragment[] fragments = new Fragment[]{
                CardFragment.newInstance(),
                CardClipFragment.newInstance()
        };

        mPagerAdapter = new BasePagerAdapter(fragmentManager, titles, fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(true);
        mViewPager.setOffscreenPageLimit(0);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mCardView.setOnClickListener(this);
        mMeView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.navigation_card:
                if (selectedItem == 0) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //更新数据
                        //  EventBus.getDefault().post(new MessageEvent("logout"));
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 0;
                setSelecteIcon();
                break;
            case R.id.navigation_me:
                if (selectedItem == 1) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 1;
                setSelecteIcon();
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(selectedItem);
    }
}
