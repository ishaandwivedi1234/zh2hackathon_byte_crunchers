package com.example.splitmonk.models;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String event_name, event_total_amount, event_individual_amount, event_group_id, event_id;
    private ArrayList<Event_User> event_users = new ArrayList<>();

    public Event() {
    }

    public Event(String event_name, String event_total_amount, String event_individual_amount, String event_group_id, String event_id, ArrayList<Event_User> event_users) {
        this.event_name = event_name;
        this.event_total_amount = event_total_amount;
        this.event_individual_amount = event_individual_amount;
        this.event_group_id = event_group_id;
        this.event_id = event_id;
        this.event_users = event_users;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_total_amount() {
        return event_total_amount;
    }

    public void setEvent_total_amount(String event_total_amount) {
        this.event_total_amount = event_total_amount;
    }

    public String getEvent_individual_amount() {
        return event_individual_amount;
    }

    public void setEvent_individual_amount(String event_individual_amount) {
        this.event_individual_amount = event_individual_amount;
    }

    public String getEvent_group_id() {
        return event_group_id;
    }

    public void setEvent_group_id(String event_group_id) {
        this.event_group_id = event_group_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public ArrayList<Event_User> getEvent_users() {
        return event_users;
    }

    public void setEvent_users(ArrayList<Event_User> event_users) {
        this.event_users = event_users;
    }
}
