package com.gamespurchase.entities;

public class TimeGame {

    private String id;

    private String hour;

    public TimeGame() {
    }

    public TimeGame(String id, String hour){
        this.id = id;
        this.hour = hour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
