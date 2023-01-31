package com.gamespurchase.entities;

import java.util.List;

public class SerieGame {

    private String id;

    private String name;

    private List<String> games;

    public SerieGame(String name, List<String> games) {
        this.name = name;
        this.games = games;
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
}
