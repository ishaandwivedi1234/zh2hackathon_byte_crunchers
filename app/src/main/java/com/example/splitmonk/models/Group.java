package com.example.splitmonk.models;

import java.util.ArrayList;

public class Group {
    private String group_name, group_id, group_desc;
    private ArrayList<String> users = new ArrayList<>(); //will contain user ids
    private ArrayList<String> events = new ArrayList<>(); //will contain events

    public Group() {
    }

    public Group(String group_name, String group_id, String group_desc, ArrayList<String> users, ArrayList<String> events) {
        this.group_name = group_name;
        this.group_id = group_id;
        this.group_desc = group_desc;
        this.users = users;
        this.events = events;
    }

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }
}
