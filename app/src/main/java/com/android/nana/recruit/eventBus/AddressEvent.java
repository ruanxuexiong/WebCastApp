package com.android.nana.recruit.eventBus;

/**
 * Created by lenovo on 2018/3/28.
 */

public class AddressEvent {

    public String mProvinceId, mCityId, mAreaId;
    public String mCityName, mAreaName;
    public String mDetails;

    public AddressEvent(String mProvinceId, String mCityId, String mAreaId, String mCityName, String mAreaName, String mDetails) {
        this.mProvinceId = mProvinceId;
        this.mCityId = mCityId;
        this.mAreaId = mAreaId;
        this.mCityName = mCityName;
        this.mAreaName = mAreaName;
        this.mDetails = mDetails;
    }
}
