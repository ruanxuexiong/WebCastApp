package com.android.nana.material;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/6/26.
 */

public class ReportActivity extends BaseActivity implements View.OnClickListener {


    private ImageButton mBackBtn;
    private TextView mTitleTv;
    private Button mSubmitBtn;
    private int selectPosition = -1;//用于记录用户选择的变量
    private LinearLayout mSubmitLL, mSuccessLL;
    private ListView mList;
    private ReportAdapter mAdapter;
    private String name;
    private String uid, mThisId;
    private ArrayList<ReportItem> mItem = new ArrayList<>();

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_report);
    }

    @Override
    protected void findViewById() {

        mBackBtn = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);

        mList = findViewById(R.id.lv_report);
        mSubmitBtn = findViewById(R.id.btn_submit);
        mSubmitLL = findViewById(R.id.ll_submit);
        mSuccessLL = findViewById(R.id.ll_report_success);
    }

    @Override
    protected void init() {
        mTitleTv.setText("举报");
        if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            mThisId = getIntent().getStringExtra("thisId");
        }
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mAdapter = new ReportAdapter(ReportActivity.this, mItem);
        mAdapter.setDatas(mItem);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int currentNum = -1;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (ReportItem item : mItem) {
                    item.setChecked(false);
                }

                if (currentNum == -1) { //选中
                    mItem.get(position).setChecked(true);
                    currentNum = position;
                    ReportItem mitem = mItem.get(position);
                    name = mitem.getName();
                } else if (currentNum == position) { //同一个item选中变未选中
                    for (ReportItem item : mItem) {
                        item.setChecked(false);
                    }
                    currentNum = -1;
                } else if (currentNum != position) { //不是同一个item选中当前的，去除上一个选中的
                    for (ReportItem item : mItem) {
                        item.setChecked(false);
                    }
                    mItem.get(position).setChecked(true);
                    currentNum = position;
                    ReportItem mitem = mItem.get(position);
                    name = mitem.getName();
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (null != name) {
                    submit();
                } else {
                    ToastUtils.showToast("请选择举报内容！");
                }
                break;
            case R.id.common_btn_back:
                finish();
                break;
        }
    }

    private void submit() {
        CustomerDbHelper.addReport(mThisId, uid, name, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mSubmitLL.setVisibility(View.GONE);
                mSuccessLL.setVisibility(View.VISIBLE);
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    private void getData() {
        CustomerDbHelper.getReportList(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mItem.clear();
                try {
                    JSONObject jsonObject1 = new JSONObject(successJson);
                    JSONArray js = JSONUtil.getStringToJsonArray(jsonObject1.getString("data"));
                    ReportItem item;
                    for (int i = 0; i < js.length(); i++) {
                        item = new ReportItem();
                        item.setId(i);
                        item.setName(js.get(i).toString());
                        mItem.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }


    public class ReportAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<ReportItem> mDataList;
        private ReportItem maiItem;

        public void setDatas(ArrayList datas) {
            mDataList.addAll(datas);
            notifyDataSetChanged();
        }

        public ReportAdapter(Context mContext, ArrayList<ReportItem> dataList) {
            this.mContext = mContext;
            this.mDataList = dataList;
        }

        @Override
        public int getCount() {
            return this.mDataList.size();
        }

        @Override
        public ReportItem getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            ViewHolder viewHolder;
            maiItem = mDataList.get(position);
            if (itemView == null) {
                viewHolder = new ViewHolder();
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_report, null);
                viewHolder.mRadioButton = itemView.findViewById(R.id.radio_btn);
                itemView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) itemView.getTag();
            }

            if (mDataList.get(position).isChecked()) {
                viewHolder.mRadioButton.setChecked(true);
            } else {
                viewHolder.mRadioButton.setChecked(false);
            }

            viewHolder.mRadioButton.setId(maiItem.getId());
            viewHolder.mRadioButton.setText(maiItem.getName());

            return itemView;
        }


        class ViewHolder {
            CheckBox mRadioButton;
        }
    }
}
