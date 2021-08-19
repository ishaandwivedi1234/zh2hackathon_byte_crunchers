package com.example.splitmonk.models;

public class Event_User {
    private String user_name, user_upi, user_to_pay, user_id;
    private String user_paid;

    public Event_User() {
    }

    public Event_User(String user_name, String user_upi, String user_paid, String user_to_pay, String user_id) {
        this.user_name = user_name;
        this.user_upi = user_upi;
        this.user_to_pay = user_to_pay;
        this.user_id = user_id;
        this.user_paid = user_paid;
    }

    public String getUser_paid() {
        return user_paid;
    }

    public void setUser_paid(String user_paid) {
        this.user_paid = user_paid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_upi() {
        return user_upi;
    }

    public void setUser_upi(String user_upi) {
        this.user_upi = user_upi;
    }

    public String getUser_to_pay() {
        return user_to_pay;
    }

    public void setUser_to_pay(String user_to_pay) {
        this.user_to_pay = user_to_pay;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
