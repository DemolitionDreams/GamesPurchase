package com.gamespurchase.entities;

import java.util.HashMap;

public class ScheduleGame {

    private String id;

    private String day;

    private HashMap<String, ProgressGame> positionAndGame;

    public ScheduleGame(){
    }

    public ScheduleGame(String id, String day, HashMap<String, ProgressGame> positionAndGame) {
        this.id = id;
        this.day = day;
        this.positionAndGame = positionAndGame;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public HashMap<String, ProgressGame> getPositionAndGame() {
        return positionAndGame;
    }

    public void setPositionAndGame(HashMap<String, ProgressGame> positionAndGame) {
        this.positionAndGame = positionAndGame;
    }
}