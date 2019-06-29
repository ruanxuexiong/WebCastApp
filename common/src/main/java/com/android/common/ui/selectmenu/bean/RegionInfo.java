package com.android.common.ui.selectmenu.bean;

import java.util.List;

public class RegionInfo {

    int groupId;
    String groupName;
    List<Region> regionList;

    public RegionInfo(int groupId, String groupName, List<Region> regionList) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.regionList = regionList;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

}
