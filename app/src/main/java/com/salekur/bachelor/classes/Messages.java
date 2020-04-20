package com.salekur.bachelor.classes;

public class Messages {

    private String from, message, time;


    public  Messages() {
        //default constructor........
    }

    public Messages(String from, String message, String time) {
        this.from = from;
        this.message = message;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
