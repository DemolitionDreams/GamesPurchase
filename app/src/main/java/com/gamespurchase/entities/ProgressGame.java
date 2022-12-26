package com.gamespurchase.entities;

public class ProgressGame {

    private int currentProgress;

    private int total;

    private String startDate;

    private String name;

    private String saga;

    private String platform;

    private String priority;

    private Boolean buyed;

    private Boolean checkInTransit;

    public ProgressGame() {
    }

    public ProgressGame(int currentProgress, int total, String startDate, String name, String saga, String platform, String priority, Boolean buyed, Boolean checkInTransit) {
        this.currentProgress = currentProgress;
        this.total = total;
        this.startDate = startDate;
        this.name = name;
        this.saga = saga;
        this.platform = platform;
        this.priority = priority;
        this.buyed = buyed;
        this.checkInTransit = checkInTransit;
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

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaga() {
        return saga;
    }

    public void setSaga(String saga) {
        this.saga = saga;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getBuyed() {
        return buyed;
    }

    public void setBuyed(Boolean buyed) {
        this.buyed = buyed;
    }

    public Boolean getCheckInTransit() {
        return checkInTransit;
    }

    public void setCheckInTransit(Boolean checkInTransit) {
        this.checkInTransit = checkInTransit;
    }
}