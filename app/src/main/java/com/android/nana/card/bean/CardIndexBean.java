package com.android.nana.card.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/19.
 */

public class CardIndexBean {

    private String hasCard;
    private ArrayList<Cards> cards;

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

    public class Cards {
        private String id;
        private String card;
        private String logo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
    }
}
