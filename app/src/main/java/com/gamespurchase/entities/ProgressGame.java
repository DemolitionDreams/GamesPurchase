package com.gamespurchase.entities;

public class ProgressGame {

    private String startDate;

    private int currentProgress;

    private int total;

    private String percentage;

    private String name;

    private Boolean buyed;

    public ProgressGame() {
    }

    public ProgressGame(String startDate, int currentProgress, int total, String percentage, String name, Boolean buyed) {
        this.startDate = startDate;
        this.currentProgress = currentProgress;
        this.total = total;
        this.percentage = percentage;
        this.name = name;
        this.buyed = buyed;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) { this.total = total; }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBuyed() {
        return buyed;
    }

    public void setBuyed(Boolean buyed) {
        this.buyed = buyed;
    }
}