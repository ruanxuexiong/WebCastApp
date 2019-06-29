package com.android.nana.customer.myincome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.find.base.MineEvent;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.http.HttpService;
import com.android.nana.login.ForgetPassActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.sina.weibo.sdk.api.share.Base;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Prepaid_Activity extends AppCompatActivity {
    String time = "";
    String Sign = "";
    String key = "";
    private List<String> list2 = new ArrayList<>();
    private ImageView txl_img;
    private GridView pre_grdview;
    grdadapter adapter;
    private ImageButton btn_back;
    private UserInfo mUserInfo;
    private EditText phone_edit;
    private TextView text_city;
    private List<Integer> list = new ArrayList<>();
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_prepaid_);
        phone_edit = findViewById(R.id.phone_edit);
        pre_grdview = findViewById(R.id.pre_grdview);
        text_city = findViewById(R.id.text_city);
        btn_back = findViewById(R.id.btn_back);
        txl_img = findViewById(R.id.txl_img);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txl_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jumpIntent = new Intent(Intent.ACTION_PICK);
                jumpIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(jumpIntent, 86);

            }
        });
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(Prepaid_Activity.this, "userInfo", UserInfo.class);
        phone_edit.setText(mUserInfo.getMobile());

        pre_grdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //   Toast.makeText(Prepaid_Activity.this, "我要充值"+list.get(i)+"元", Toast.LENGTH_SHORT).show();

                if (phone_edit.getText().length() == 11) {
                    position = i;
                    showNormalDialog();
                } else {
                    Toast.makeText(Prepaid_Activity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                }

            }
        });
        add();
        getdata();
        adapter = new grdadapter();
        pre_grdview.setAdapter(adapter);


        phone_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 11) {
                    getdata();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void getdata() {
        HttpService.getPhone(phone_edit.getText().toString(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (result.get("description") != null && !"".equals((result.get("description")))) {
                            text_city.setText(result.get("description").toString());
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void getFailue(String failueJson) {
                String aa = failueJson;
            }
        });
    }

    public class grdadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 8;
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
            view = LayoutInflater.from(Prepaid_Activity.this).inflate(R.layout.huafei_list, null);
            TextView huafei_money1 = view.findViewById(R.id.huafei_money1);
            TextView huafei_money2 = view.findViewById(R.id.huafei_money2);
            huafei_money1.setText(list.get(i) + "元");
            huafei_money2.setText("售价:" + list.get(i) + ".00元");
            return view;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86) {
            //选择通讯录联系人返回

            if (data == null) {
                return;
            }

            if (data.getData() != null) {
                Cursor cursor = getContentResolver()
                        .query(data.getData(),
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                null, null, null);
                while (cursor.moveToNext()) {
//取出该条数据的联系人姓名
                    String name = cursor.getString(1).replaceAll(" ", "");
                    //取出该条数据的联系人的手机号
                    String number = cursor.getString(0).replaceAll(" ", "").replaceAll("-", "");
                    if (number.length() > 11) {
                        number = number.substring(number.length() - 11, number.length());
                    }
                    phone_edit.setText(number);
                    //  et_connect_phone.setSelection(number.length());
                }

                cursor.close();
            }


        }
    }


    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(Prepaid_Activity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确认支付?");
        normalDialog.setPositiveButton("支付",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        getHuafei();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void add() {
        //        list.add(1);
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(50);
        list.add(100);
        list.add(200);
        list.add(300);
        list.add(500);
    }

    private void getHuafei() {

        list2.clear();
        time = System.currentTimeMillis() + "";
        key = "";
        list2.add("phone");
        list2.add("money");
        list2.add("userId");
        list2.add("time");
        Collections.sort(list2);

        Map<String, String> map = new TreeMap<String, String>();
        map.put("phone", phone_edit.getText().toString());
        map.put("money", list.get(position) + "");
        map.put("userId", mUserInfo.getId());
        map.put("time", time);


        for (int i = 0; i < list2.size(); i++) {
            key = key + list2.get(i) + "=" + map.get(list2.get(i)) + "&";
        }

        key = key + "key=Yf7mBdnSR!eUW&bz7JQ@H@rHuth9N25@vHYVwGr5";

        Sign = encode(key);

        HttpService.getHuafei(phone_edit.getText().toString(), list.get(position) + "", mUserInfo.getId(), time, Sign, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (result.get("description") != null && !"".equals((result.get("description")))) {
                            Toast.makeText(Prepaid_Activity.this, result.get("description").toString() + "", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Prepaid_Activity.this, result.get("description").toString() + "", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void getFailue(String failueJson) {
                String aa = failueJson;
            }
        });
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encode(String password) {
        // MessageDigest专门用于加密的类
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] result = messageDigest.digest(password.getBytes()); // 得到加密后的字符组数

            StringBuffer sb = new StringBuffer();

            for (byte b : result) {
                int num = b & 0xff; // 这里的是为了将原本是byte型的数向上提升为int型，从而使得原本的负数转为了正数
                String hex = Integer.toHexString(num); //这里将int型的数直接转换成16进制表示
                //16进制可能是为1的长度，这种情况下，需要在前面补0，
                if (hex.length() == 1) {
                    sb.append(0);
                }
                sb.append(hex);
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
