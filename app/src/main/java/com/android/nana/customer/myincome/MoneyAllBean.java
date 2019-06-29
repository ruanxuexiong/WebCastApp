package com.android.nana.customer.myincome;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/7/16.
 */

public class MoneyAllBean {

    private String totalExpendiure;
    private String totalRevenue;
    private ArrayList<MoneyAllBean.Orders>orderse;

    public String getTotalExpendiure() {
        return totalExpendiure;
    }

    public void setTotalExpendiure(String totalExpendiure) {
        this.totalExpendiure = totalExpendiure;
    }

    public String getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(String totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public ArrayList<Orders> getOrderse() {
        return orderse;
    }

    public void setOrderse(ArrayList<Orders> orderse) {
        this.orderse = orderse;
    }



    public class Orders {
        private String id;
        private String userId;
        private String money;
        private String addTime;
        private String orderType;
        private String payType;
        private String orderStatus;
        private String bankCardId;
        private String transaction_id;
        private String rate;
        private String expenserate;
        private String wxId;
        private String balance;
        private String directBalance;
        private String avatar;
        private String status;
        private String tips;
        private String charge;
        private String color;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getBankCardId() {
            return bankCardId;
        }

        public void setBankCardId(String bankCardId) {
            this.bankCardId = bankCardId;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getExpenserate() {
            return expenserate;
        }

        public void setExpenserate(String expenserate) {
            this.expenserate = expenserate;
        }

        public String getWxId() {
            return wxId;
        }

        public void setWxId(String wxId) {
            this.wxId = wxId;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getDirectBalance() {
            return directBalance;
        }

        public void setDirectBalance(String directBalance) {
            this.directBalance = directBalance;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getCharge() {
            return charge;
        }

        public void setCharge(String charge) {
            this.charge = charge;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        private String date;
    }
}
