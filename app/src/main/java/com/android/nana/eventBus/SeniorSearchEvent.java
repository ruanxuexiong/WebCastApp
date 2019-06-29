package com.android.nana.eventBus;

/**
 * Created by lenovo on 2018/1/8.
 * 高级搜索
 */

public class SeniorSearchEvent {

    public String mGender;
    public String mPostName;//职位名称
    public String mProvinceId;//省份ID
    public String mCityId;
    public String mRegionName;//地区名
    public String mFunctionOneId;//职能标签一级ID
    public String mFunctionTwoId;//职能标签二级ID
    public String mFunctionName;//职能标签名称
    public String mSchoolName;//学校名称

    /**
     * @param mPostName      职位名称
     * @param mProvinceId    省份ID
     * @param mCityId        城市ID
     * @param mFunctionOneId 职能标签一级ID
     * @param mFunctionTwoId 职能标签二级ID
     * @param mFunctionName  职能标签名称
     * @param mSchoolName    学校名称
     */
    public SeniorSearchEvent(String mGender,String mPostName, String mProvinceId, String mCityId, String mRegionName, String mFunctionOneId, String mFunctionTwoId, String mFunctionName, String mSchoolName) {

        this.mGender = mGender;
        this.mPostName = mPostName;
        this.mProvinceId = mProvinceId;
        this.mCityId = mCityId;
        this.mRegionName = mRegionName;
        this.mFunctionOneId = mFunctionOneId;
        this.mFunctionTwoId = mFunctionTwoId;
        this.mFunctionName = mFunctionName;
        this.mSchoolName = mSchoolName;
    }


}
