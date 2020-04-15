package com.salekur.bachelor.classes;

public class Complain {
    public String title, details, date, time;

    public Complain() {
    }

    public Complain(String title, String details, String date, String time) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
