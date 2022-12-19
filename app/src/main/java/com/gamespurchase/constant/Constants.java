package com.gamespurchase.constant;

import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheGame;
import com.gamespurchase.entities.SerieGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

    public static String sortBuyGame = "ASC";
    public static String sortSagheGame = "ASC";
    public static String sortDatabaseGame = "ASC";

    public static String filterBuyGame = "NO";
    public static String filterSagheGame = "NO";
    public static String filterDatabaseGame = "NO";

    // Variabile globale per recuperare l'ultimo id in tabella
    public static int maxIdBuyList;
    public static int maxIdSagheList;
    public static int maxIdDatabaseList;

    private static List<BuyGame> gameBuyList;
    private static List<SerieGame> gameSerieList;
    private static List<SagheGame> gameSagheList;
    private static List<ProgressGame> gameProgressList;
    private static List<DatabaseGame> gameDatabaseList;

    private static HashMap<String, Integer> counterBuyGame = new HashMap<>();
    private static HashMap<String, Integer> counterDatabaseGame = new HashMap<>();

    public static HashMap<String, Integer> getCounterBuyGame() {
        return counterBuyGame;
    }

    public static void setCounterBuyGame(HashMap<String, Integer> counterBuyGame) {
        Constants.counterBuyGame = counterBuyGame;
    }

    public static HashMap<String, Integer> getCounterDatabaseGame() {
        return counterDatabaseGame;
    }

    public static void setCounterDatabaseGame(HashMap<String, Integer> counterDatabaseGame) {
        Constants.counterDatabaseGame = counterDatabaseGame;
    }

    public static List<BuyGame> getGameBuyList(){

        if(gameBuyList == null){

            gameBuyList = new ArrayList<>();
        }
        return gameBuyList;
    }

    public static void setGameBuyList(List<BuyGame> list){

        gameBuyList = list;
    }

    public static List<ProgressGame> getGameProgressList() {
        return gameProgressList;
    }

    public static void setGameProgressList(List<ProgressGame> gameProgressList) {
        Constants.gameProgressList = gameProgressList;
    }

    public static List<SerieGame> getGameSerieList(){

        if(gameSerieList == null){

            gameSerieList = new ArrayList<>();
        }
        return gameSerieList;
    }

    public static void setGameSerieList(List<SerieGame> list){

        gameSerieList = list;
    }

    public static List<SagheGame> getGameSagheList(){

        if(gameSagheList == null){

            gameSagheList = new ArrayList<>();
        }
        return gameSagheList;
    }

    public static void setGameSagheList(List<SagheGame> list){

        gameSagheList = list;
    }

    public static List<DatabaseGame> getGameDatabaseList(){

        if(gameDatabaseList == null){

            gameDatabaseList = new ArrayList<>();
        }
        return gameDatabaseList;
    }

    public static void setGameDatabaseList(List<DatabaseGame> list){

        gameDatabaseList = list;
    }

}
