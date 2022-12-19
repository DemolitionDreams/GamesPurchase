package com.gamespurchase.entities;

import java.time.LocalTime;

public class TimeGame {

    private String id;

    private LocalTime hour;

    public TimeGame() {
    }

    public TimeGame(String id, LocalTime time) {
        this.id = id;
        this.hour = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }
}
