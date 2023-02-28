package com.gamespurchase.entities;

import java.util.List;

public class SagheDatabaseGame {

    private String id;

    private String name;

    private Boolean buyAll;

    private Boolean finishAll;

    private List<DatabaseGame> gamesBuy;

    private List<DatabaseGame> gamesNotBuy;

    public SagheDatabaseGame(){}

    public SagheDatabaseGame(String id, String name, Boolean buyAll, Boolean finishAll, List<DatabaseGame> gamesBuy, List<DatabaseGame> gamesNotBuy) {
        this.id = id;
        this.name = name;
        this.buyAll = buyAll;
        this.finishAll = finishAll;
        this.gamesBuy = gamesBuy;
        this.gamesNotBuy = gamesNotBuy;
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

    public Boolean getBuyAll() { return buyAll; }

    public void setBuyAll(Boolean buyAll) { this.buyAll = buyAll; }

    public Boolean getFinishAll() { return finishAll; }

    public void setFinishAll(Boolean finishAll) { this.finishAll = finishAll; }

    public List<DatabaseGame> getGamesBuy() {
        return gamesBuy;
    }

    public List<DatabaseGame> getGamesNotBuy() {
        return gamesNotBuy;
    }
}