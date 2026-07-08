package com.alburagh.alburagh.Models;


public class Subscription {
    public enum Type {
        MONTHLY,
        ANNUAL,
        SIXMONTH
    }

    public Type type;
    public String token;
    public Long purchaseTime;
    public Long endTime;
    public boolean autoRenewing;
    public String price;
}
