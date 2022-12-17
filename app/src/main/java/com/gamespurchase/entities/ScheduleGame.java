package com.gamespurchase.entities;

import java.time.LocalTime;
import java.util.HashMap;

public class ScheduleGame {

    private String day;

    private HashMap<LocalTime, String> timeAndName;

    public ScheduleGame() {
    }

    public ScheduleGame(String day, HashMap<LocalTime, String> timeAndName) {
        this.day = day;
        this.timeAndName = timeAndName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public HashMap<LocalTime, String> getTimeAndName() {
        return timeAndName;
    }

    public void setTimeAndName(HashMap<LocalTime, String> timeAndName) {
        this.timeAndName = timeAndName;
    }
}