package com.gamespurchase.entities;

public class BuyGame {

    private String id;

    private String name;

    private String saga;

    private String platform;

    private String priority;

    private Boolean checkInTransit;

    public BuyGame() {
    }

    public BuyGame(String id, String name, String saga, String platform, String priority, Boolean checkInTransit) {
        this.id = id;
        this.name = name;
        this.saga = saga;
        this.platform = platform;
        this.priority = priority;
        this.checkInTransit = checkInTransit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPriority() { return priority; }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCheckInTransit() {
        return checkInTransit;
    }

    public void setCheckInTransit(Boolean checkInTransit) {
        this.checkInTransit = checkInTransit;
    }
}
