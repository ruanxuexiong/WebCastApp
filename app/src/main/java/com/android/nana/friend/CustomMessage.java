package com.android.nana.friend;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by Qin on 2017/11/10.
 */
@MessageTag(value = "app:custom", flag = MessageTag.ISCOUNTED)
public class CustomMessage extends MessageContent {

    private String fromUserId;
    private String count;
    private String avatar;


    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @Override
    public byte[] encode() {

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("fromUserId", this.fromUserId);
            jsonObj.put("count", this.count);
            jsonObj.put("avatar", this.avatar);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    CustomMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("fromUserId")) {
                fromUserId = jsonObj.optString("fromUserId");
            }

            if (jsonObj.has("count")) {
                count = jsonObj.optString("count");
            }

            if (jsonObj.has("avatar")) {
                avatar = jsonObj.optString("avatar");
            }

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }

    public CustomMessage(Parcel in) {
        fromUserId = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        count = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        avatar = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<CustomMessage> CREATOR = new Creator<CustomMessage>() {

        @Override
        public CustomMessage createFromParcel(Parcel source) {
            return new CustomMessage(source);
        }

        @Override
        public CustomMessage[] newArray(int size) {
            return new CustomMessage[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        ParcelUtils.writeToParcel(dest, fromUserId);
        ParcelUtils.writeToParcel(dest, count);
        ParcelUtils.writeToParcel(dest, avatar);
    }
}
