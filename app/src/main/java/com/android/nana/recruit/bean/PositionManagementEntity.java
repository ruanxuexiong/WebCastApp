package com.android.nana.recruit.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/30.
 */

public class PositionManagementEntity {

    public ArrayList<Positions> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Positions> positions) {
        this.positions = positions;
    }

    private ArrayList<PositionManagementEntity.Positions> positions;

    public class Positions {
        private String id;
        private String positionId;
        private String companyId;
        private String uid;
        private String talk;
        private String view;
        private String share;
        private String job;
        private String provinceId;
        private String cityId;
        private String areaId;
        private String address;
        private String nature;
        private String experience;
        private String range;
        private String edu;
        private String desc;
        private String status;
        private String position;
        private String province;
        private String area;
        private String city;
        private String rangeName;
        private String experienceName;
        private String eduName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPositionId() {
            return positionId;
        }

        public void setPositionId(String positionId) {
            this.positionId = positionId;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTalk() {
            return talk;
        }

        public void setTalk(String talk) {
            this.talk = talk;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(String provinceId) {
            this.provinceId = provinceId;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getNature() {
            return nature;
        }

        public void setNature(String nature) {
            this.nature = nature;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getEdu() {
            return edu;
        }

        public void setEdu(String edu) {
            this.edu = edu;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRangeName() {
            return rangeName;
        }

        public void setRangeName(String rangeName) {
            this.rangeName = rangeName;
        }

        public String getExperienceName() {
            return experienceName;
        }

        public void setExperienceName(String experienceName) {
            this.experienceName = experienceName;
        }

        public String getEduName() {
            return eduName;
        }

        public void setEduName(String eduName) {
            this.eduName = eduName;
        }

        public String getNatureName() {
            return natureName;
        }

        public void setNatureName(String natureName) {
            this.natureName = natureName;
        }

        private String natureName;
    }

}
