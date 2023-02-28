package com.gamespurchase.entities;

import java.util.List;

public class SagheGame {

    private String id;

    private String name;

    private List<SerieGame> serie;

    public SagheGame() {
    }

    public SagheGame(String name, List<SerieGame> serie) {
        this.name = name;
        this.serie = serie;
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

    public List<SerieGame> getSerie() {
        return serie;
    }
}
