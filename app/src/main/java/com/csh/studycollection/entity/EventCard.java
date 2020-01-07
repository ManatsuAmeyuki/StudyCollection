package com.csh.studycollection.entity;

public class EventCard {
    public int consume;
    public int cardLevel;

    public EventCard(int cardLevel) {
        this.cardLevel = cardLevel;
    }
    public EventCard(int cardLevel, int consume) {
        this.cardLevel = cardLevel;
        this.consume = consume;
    }
    public EventCard() {
    }
}
