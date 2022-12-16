package com.gamespurchase.entities;

public class ScheduleGame {

    // day - time
    private String id;

    private String day;

    private String time;

    private String name;

    public ScheduleGame() {
    }

    public ScheduleGame(String id, String day, String time, String name) {
        this.id = id;
        this.day = day;
        this.time = time;
        this.name = name;
    }

    public String getId() {return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
