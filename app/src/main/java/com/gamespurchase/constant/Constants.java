package com.gamespurchase.constant;

import androidx.appcompat.widget.AppCompatButton;

import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.SagheGame;
import com.gamespurchase.entities.SerieGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constants {

    // Variabili Globali per individuare il giorno attuale, la sua codifica e la label attualmente visualizzata
    public static String actualDay = "";
    public static String actualDayCode = "";
    public static String actualList = "";

    // Variabili Globali per l'ordinamento delle liste
    public static String sortBuyGame = "ASC";
    public static String sortListGame = "ASC";
    public static String sortSagheGame = "ASC";
    public static String sortDatabaseGame = "DESC";

    // Variabili Globali per il filetro delle liste
    public static String filterBuyGame = "NO";
    public static String filterSagheGame = "NO";

    // Variabili Globali per recuperare l'ultimo id in tabella
    public static int maxIdProgressList;
    public static int maxIdSagheList;
    public static int maxIdDatabaseList;

    // Variabili Globali per le liste di giorni e label
    private static List<String> dayCodeList = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun");
    private static List<String> listGameList = Arrays.asList("Try AAA", "Try AA", "AAA", "AA", "A", "Still", "DLC", "Old", "PlayStation 5", "PlayStation 4", "PlayStation 3", "PlayStation 2", "PlayStation", "PlayStation Portable", "PlayStation Vita", "Nintendo Switch", "Nintendo Wii", "Nintendo Wii U", "Nintendo 3DS", "Nintendo DS", "Game Boy Advance", "Game Boy Color", "Game Boy", "Xbox 360", "Xbox One", "Xbox Series X", "Graphic Adventure");

    // Variabili Globali per recuperare i dati a DB
    private static List<String> allDayGameProgressList;
    private static List<SerieGame> gameSerieList;
    private static List<SagheGame> gameSagheList;
    private static List<ProgressGame> actualLabelGameProgressList;
    private static List<ProgressGame> allLabelGameProgressList;
    private static List<ProgressGame> actualDayGameProgressList;
    private static List<SagheDatabaseGame> gameSagheDatabaseList;
    private static List<DatabaseGame> gameDatabaseList;
    private static List<AppCompatButton> timeButtonList;
    private static List<AppCompatButton> gameButtonList;

    // Variabili Globali per i contatori
    private static int counterProgressGame;
    private static int counterSagheDatabaseGame;
    private static HashMap<String, Integer> counterBuyGame = new HashMap<>();
    private static HashMap<String, Integer> counterDatabaseGame = new HashMap<>();
    private static HashMap<String, List<ProgressGame>> scheduledGameMap = new HashMap<>();

    // Variabile per copia/incolla
    private static ProgressGame progressGameCopy;

    public static String getActualDay() {
        return actualDay;
    }

    public static void setActualDay(String actualDay) {
        Constants.actualDay = actualDay;
    }

    public static String getActualDayCode() {
        return actualDayCode;
    }

    public static void setActualDayCode(String actualDayCode) {
        Constants.actualDayCode = actualDayCode;
    }

    public static String getActualList() {
        return actualList;
    }

    public static void setActualList(String actualList) {
        Constants.actualList = actualList;
    }

    public static String getSortBuyGame() {
        return sortBuyGame;
    }

    public static void setSortBuyGame(String sortBuyGame) {
        Constants.sortBuyGame = sortBuyGame;
    }

    public static String getSortListGame() {
        return sortListGame;
    }

    public static void setSortListGame(String sortListGame) {
        Constants.sortListGame = sortListGame;
    }

    public static String getSortSagheGame() {
        return sortSagheGame;
    }

    public static void setSortSagheGame(String sortSagheGame) {
        Constants.sortSagheGame = sortSagheGame;
    }

    public static String getSortDatabaseGame() {
        return sortDatabaseGame;
    }

    public static void setSortDatabaseGame(String sortDatabaseGame) {
        Constants.sortDatabaseGame = sortDatabaseGame;
    }

    public static String getFilterBuyGame() {
        return filterBuyGame;
    }

    public static void setFilterBuyGame(String filterBuyGame) {
        Constants.filterBuyGame = filterBuyGame;
    }

    public static String getFilterSagheGame() {
        return filterSagheGame;
    }

    public static void setFilterSagheGame(String filterSagheGame) {
        Constants.filterSagheGame = filterSagheGame;
    }

    public static int getMaxIdProgressList() {
        return maxIdProgressList;
    }

    public static void setMaxIdProgressList(int maxIdProgressList) {
        Constants.maxIdProgressList = maxIdProgressList;
    }

    public static int getMaxIdSagheList() {
        return maxIdSagheList;
    }

    public static void setMaxIdSagheList(int maxIdSagheList) {
        Constants.maxIdSagheList = maxIdSagheList;
    }

    public static int getMaxIdDatabaseList() {
        return maxIdDatabaseList;
    }

    public static void setMaxIdDatabaseList(int maxIdDatabaseList) {
        Constants.maxIdDatabaseList = maxIdDatabaseList;
    }

    public static List<String> getDayCodeList() {
        return dayCodeList;
    }

    public static void setDayCodeList(List<String> dayCodeList) {
        Constants.dayCodeList = dayCodeList;
    }

    public static List<String> getListGameList() {
        return listGameList;
    }

    public static void setListGameList(List<String> listGameList) {
        Constants.listGameList = listGameList;
    }

    public static List<SerieGame> getGameSerieList() {
        return gameSerieList;
    }

    public static void setGameSerieList(List<SerieGame> gameSerieList) {
        Constants.gameSerieList = gameSerieList;
    }

    public static List<SagheGame> getGameSagheList() {
        return gameSagheList;
    }

    public static void setGameSagheList(List<SagheGame> gameSagheList) {
        Constants.gameSagheList = gameSagheList;
    }

    public static List<ProgressGame> getActualLabelGameProgressList() {
        return actualLabelGameProgressList;
    }

    public static void setActualLabelGameProgressList(List<ProgressGame> actualLabelGameProgressList) {
        Constants.actualLabelGameProgressList = actualLabelGameProgressList;
    }

    public static List<ProgressGame> getAllLabelGameProgressList() {
        return allLabelGameProgressList;
    }

    public static void setAllLabelGameProgressList(List<ProgressGame> allLabelGameProgressList) {
        Constants.allLabelGameProgressList = allLabelGameProgressList;
    }

    public static List<ProgressGame> getActualDayGameProgressList() {
        return actualDayGameProgressList;
    }

    public static void setActualDayGameProgressList(List<ProgressGame> actualDayGameProgressList) {
        Constants.actualDayGameProgressList = actualDayGameProgressList;
    }

    public static List<String> getAllDayGameProgressList() {
        return allDayGameProgressList;
    }

    public static void setAllDayGameProgressList(List<String> allDayGameProgressList) {
        Constants.allDayGameProgressList = allDayGameProgressList;
    }

    public static List<SagheDatabaseGame> getGameSagheDatabaseList() {
        return gameSagheDatabaseList;
    }

    public static void setGameSagheDatabaseList(List<SagheDatabaseGame> gameSagheDatabaseList) {
        Constants.gameSagheDatabaseList = gameSagheDatabaseList;
    }

    public static List<DatabaseGame> getGameDatabaseList() {
        return gameDatabaseList;
    }

    public static void setGameDatabaseList(List<DatabaseGame> gameDatabaseList) {
        Constants.gameDatabaseList = gameDatabaseList;
    }

    public static List<AppCompatButton> getTimeButtonList() {
        return timeButtonList;
    }

    public static void setTimeButtonList(List<AppCompatButton> timeButtonList) {
        Constants.timeButtonList = timeButtonList;
    }

    public static List<AppCompatButton> getGameButtonList() {
        return gameButtonList;
    }

    public static void setGameButtonList(List<AppCompatButton> gameButtonList) {
        Constants.gameButtonList = gameButtonList;
    }

    public static int getCounterProgressGame() {
        return counterProgressGame;
    }

    public static void setCounterProgressGame(int counterProgressGame) {
        Constants.counterProgressGame = counterProgressGame;
    }

    public static HashMap<String, Integer> getCounterBuyGame() {
        return counterBuyGame;
    }

    public static void setCounterBuyGame(HashMap<String, Integer> counterBuyGame) {
        Constants.counterBuyGame = counterBuyGame;
    }

    public static int getCounterSagheDatabaseGame() {
        return counterSagheDatabaseGame;
    }

    public static void setCounterSagheDatabaseGame(int counterSagheDatabaseGame) {
        Constants.counterSagheDatabaseGame = counterSagheDatabaseGame;
    }

    public static HashMap<String, Integer> getCounterDatabaseGame() {
        return counterDatabaseGame;
    }

    public static void setCounterDatabaseGame(HashMap<String, Integer> counterDatabaseGame) {
        Constants.counterDatabaseGame = counterDatabaseGame;
    }

    public static HashMap<String, List<ProgressGame>> getScheduledGameMap() {
        return scheduledGameMap;
    }

    public static void setScheduledGameMap(HashMap<String, List<ProgressGame>> scheduledGameMap) {
        Constants.scheduledGameMap = scheduledGameMap;
    }

    public static ProgressGame getProgressGameCopy() {
        return progressGameCopy;
    }

    public static void setProgressGameCopy(ProgressGame progressGameCopy) {
        Constants.progressGameCopy = progressGameCopy;
    }
}
