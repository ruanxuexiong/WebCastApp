package com.android.nana.eventBus;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/11/6.
 */

public class RedEvent implements Serializable {
    public String money;//红包总金额
    public String num;//红包个数
    public String km;
    public int type;//红包类型
    public String mAdvertising = "", mAdvType = "", mAdvUrl = "";

    public RedEvent(String money, String km, int type, String num, String mAdvertising, String mAdvType, String mAdvUrl) {
        this.money = money;
        this.km = km;
        this.type = type;
        this.num = num;

        this.mAdvertising = mAdvertising;
        this.mAdvType = mAdvType;
        this.mAdvUrl = mAdvUrl;

    }
}
