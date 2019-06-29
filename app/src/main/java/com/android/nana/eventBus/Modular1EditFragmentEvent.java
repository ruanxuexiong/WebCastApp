package com.android.nana.eventBus;

/**
 * Created by lenovo on 2018/1/24.
 */

public class Modular1EditFragmentEvent {


    public String mid;
    public String name;
    public String post;
    public String company;
    public String phone;
    public String mobile;
    public String fax;
    public String mail;
    public String address;
    public String code;
    public String logo;
    public String card;
    public String carid;


    public Modular1EditFragmentEvent( String mid, String name, String post, String company, String phone, String mobile, String fax, String mail, String address, String code) {

        this.mid = mid;
        this.name = name;
        this.post = post;
        this.company = company;
        this.phone = phone;
        this.mobile = mobile;
        this.fax = fax;
        this.mail = mail;
        this.address = address;
        this.code = code;
    }

}
