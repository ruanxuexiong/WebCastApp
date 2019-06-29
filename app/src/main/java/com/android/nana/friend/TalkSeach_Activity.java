package com.android.nana.friend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.Huatibean;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.find.bean.Moment;
import com.android.nana.widget.OverrideEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TalkSeach_Activity extends AppCompatActivity {
    private String state="";
    Huatiadapter huatiadapter;
    private Button huati_back;
    private OverrideEditText et_search;
    private ListView huati_list;
    private List<Huatibean> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_seach_);
        Intent intent=getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("state"))){
            state= intent.getStringExtra("state");
        }

        huati_back=findViewById(R.id.huati_back);
        et_search=findViewById(R.id.et_search);
        huati_list=findViewById(R.id.huati_list);
        huati_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_search.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                et_search.setText("");
            }
        });
        init();

        CustomerDbHelper.getHuati(et_search.getText().toString(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {


                try {
                    JSONObject jsonObject=new JSONObject(successJson);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    Gson gson=new Gson();
                    for (int i=0;i<jsonArray.length();i++){
                        Huatibean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), Huatibean.class);
                        list.add(entity);
                    }
                    huatiadapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }


    private void init(){
        huatiadapter=new Huatiadapter();
        huati_list.setAdapter(huatiadapter);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getHuati();
            }
        });
    }

    public class Huatiadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           view= LayoutInflater.from(TalkSeach_Activity.this).inflate(R.layout.huati_list,null);
            final TextView huati_text=view.findViewById(R.id.huati_text);
            huati_text.setText(list.get(i).getTag_name()+"");
            huati_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (state.equals("1")){
                        Intent intent = new Intent(TalkSeach_Activity.this, VideoDynamicActivity.class);
                        intent.putExtra("huati", huati_text.getText().toString()+"");
                        setResult(001, intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(TalkSeach_Activity.this, VideoDynamicActivity.class);
                        intent.putExtra("huati", huati_text.getText().toString()+"");
                        setResult(002, intent);
                        finish();
                    }

                }
            });
           return view;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(TalkSeach_Activity.this.getCurrentFocus().getWindowToken(), 0);
            }
            if (state.equals("1")){
                Intent intent = new Intent(TalkSeach_Activity.this, VideoDynamicActivity.class);
                intent.putExtra("huati", et_search.getText().toString()+"");
                setResult(001, intent);
                finish();
            }
            else {
                Intent intent = new Intent(TalkSeach_Activity.this, VideoDynamicActivity.class);
                intent.putExtra("huati", et_search.getText().toString()+"");
                setResult(002, intent);
                finish();
            }
          //  edittext.setText("success");
           // webview.loadUrl(URL);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    private void getHuati(){
        CustomerDbHelper.getHuati(et_search.getText().toString(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {


                try {
                    JSONObject jsonObject=new JSONObject(successJson);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    Gson gson=new Gson();
                    if (list!=null&&list.size()!=0){
                        list.clear();
                    }
                    else {

                    }

                    for (int i=0;i<jsonArray.length();i++){
                        Huatibean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), Huatibean.class);
                        list.add(entity);
                    }
                    huatiadapter.notifyDataSetChanged();

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
