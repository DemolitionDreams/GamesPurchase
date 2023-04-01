package com.gamespurchase.entities;

public class DatabaseGame {

    private String id;

    private String name;

    private String platform;

    private String priority;

    private Boolean finished;

    private Boolean checkInTransit;

    public DatabaseGame(){
    }

    public DatabaseGame(String name, String platform, String priority, Boolean finished, Boolean checkInTransit) {
        this.name = name;
        this.platform = platform;
        this.priority = priority;
        this.finished = finished;
        this.checkInTransit = checkInTransit;
    }

    public DatabaseGame(String id, String name, String platform, String priority, Boolean finished, Boolean checkInTransit) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.priority = priority;
        this.finished = finished;
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

    public String getPlatform() {
        return platform;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getFinished() {
        return finished;
    }

    public Boolean getCheckInTransit() {
        return checkInTransit;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public void setCheckInTransit(Boolean checkInTransit) {
        this.checkInTransit = checkInTransit;
    }
}
