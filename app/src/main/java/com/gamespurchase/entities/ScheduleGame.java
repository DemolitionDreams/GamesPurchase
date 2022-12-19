package com.gamespurchase.entities;

import java.util.HashMap;

public class ScheduleGame {

    private String day;

    private HashMap<Integer, ProgressGame> positionAndGame;

    public ScheduleGame() {
    }

    public ScheduleGame(String day, HashMap<Integer, ProgressGame> positionAndGame) {
        this.day = day;
        this.positionAndGame = positionAndGame;
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