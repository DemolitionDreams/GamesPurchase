package com.gamespurchase.entities;

public class DatabaseGame {

    private String id;

    private String name;

    private String saga;

    private String platform;

    private Boolean buyed;

    private Boolean finished;

    public DatabaseGame() {
    }

    public DatabaseGame(String name, String saga, String platform, Boolean buyed, Boolean finished) {
        this.name = name;
        this.saga = saga;
        this.platform = platform;
        this.buyed = buyed;
        this.finished = finished;
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

    public Boolean getBuyed() { return buyed; }

    public void setBuyed(Boolean buyed) {
        this.buyed = buyed;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}
