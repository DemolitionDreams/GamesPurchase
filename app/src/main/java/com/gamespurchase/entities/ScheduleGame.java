package com.gamespurchase.entities;

import java.util.HashMap;

public class ScheduleGame {

    private String dayCode;

    private String day;

    private HashMap<Integer, ProgressGame> positionAndGame;

    public ScheduleGame() {
    }

    public ScheduleGame(String dayCode, String day, HashMap<Integer, ProgressGame> positionAndGame) {
        this.dayCode = dayCode;
        this.day = day;
        this.positionAndGame = positionAndGame;
    }

    public String getDayCode() {
        return dayCode;
    }

    public void setDayCode(String dayCode) {
        this.dayCode = dayCode;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public HashMap<Integer, ProgressGame> getPositionAndGame() {
        return positionAndGame;
    }

    public void setPositionAndGame(HashMap<Integer, ProgressGame> positionAndGame) {
        this.positionAndGame = positionAndGame;
    }
}