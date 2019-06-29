package com.android.nana.card.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/23.
 */

public class CardList {

    private String hasCard;
    private ArrayList<CardList.Cards> cards;

    public String getHasCard() {
        return hasCard;
    }

    public void setHasCard(String hasCard) {
        this.hasCard = hasCard;
    }

    public ArrayList<Cards> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Cards> cards) {
        this.cards = cards;
    }


    public class Cards extends SelectedBean {
        private String id;
        private String uid;
        private String name;
        private String position;
        private String company;
        private String phone;
        private String mobile;
        private String fax;
        private String email;
        private String address;
        private String postal;
        private String templatestl;
        private String card;
        private String logo;
        private String ctime;
        private String send;
        private String date;
        private String cardURL;
        private String shareTitle;
        private String shareContent;
        private String sharePic;
        private String oldCard;

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        private String thumb;

        public String getOldCard() {
            return oldCard;
        }

        public void setOldCard(String oldCard) {
            this.oldCard = oldCard;
        }


        public String getSharLogo() {
            return sharLogo;
        }

        public void setSharLogo(String sharLogo) {
            this.sharLogo = sharLogo;
        }

        private String sharLogo;

        public String getCardURL() {
            return cardURL;
        }

        public void setCardURL(String cardURL) {
            this.cardURL = cardURL;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareContent() {
            return shareContent;
        }

        public void setShareContent(String shareContent) {
            this.shareContent = shareContent;
        }

        public String getSharePic() {
            return sharePic;
        }

        public void setSharePic(String sharePic) {
            this.sharePic = sharePic;
        }


        public Cards(String card, boolean isSelected) {
            this.card = card;
            setSelected(isSelected);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPostal() {
            return postal;
        }

        public void setPostal(String postal) {
            this.postal = postal;
        }

        public String getTemplatestl() {
            return templatestl;
        }

        public void setTemplatestl(String templatestl) {
            this.templatestl = templatestl;
        }

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getSend() {
            return send;
        }

        public void setSend(String send) {
            this.send = send;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }


    }
}
