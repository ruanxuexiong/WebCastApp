package com.android.nana.addialog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lenovo on 2017/12/19.
 * 广告弹窗实体类
 */

public class AdInfo implements Parcelable {

    private String adId = null;
    private String title = null;
    private String url = null;
    private String activityImg = null;
    private int activityImgId = -1;
    private String pic = null;
    private String ctime = null;
    private String sort = null;
    private String cycle = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id = null;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status = null;

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getActivityImg() {
        return activityImg;
    }

    public void setActivityImg(String activityImg) {
        this.activityImg = activityImg;
    }

    public int getActivityImgId() {
        return activityImgId;
    }

    public void setActivityImgId(int activityImgId) {
        this.activityImgId = activityImgId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.adId);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.activityImg);
        dest.writeInt(this.activityImgId);
        dest.writeString(this.pic);
        dest.writeString(this.ctime);
        dest.writeString(this.sort);
        dest.writeString(this.cycle);
        dest.writeString(this.status);
        dest.writeString(this.id);
    }

    public AdInfo() {
    }

    protected AdInfo(Parcel in) {
        this.adId = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.activityImg = in.readString();
        this.activityImgId = in.readInt();
        this.pic = in.readString();
        this.ctime = in.readString();
        this.sort = in.readString();
        this.ctime = in.readString();
        this.cycle = in.readString();
        this.status = in.readString();
        this.id = in.readString();
    }

    public static final Creator<AdInfo> CREATOR = new Creator<AdInfo>() {
        @Override
        public AdInfo createFromParcel(Parcel source) {
            return new AdInfo(source);
        }

        @Override
        public AdInfo[] newArray(int size) {
            return new AdInfo[size];
        }
    };
}
