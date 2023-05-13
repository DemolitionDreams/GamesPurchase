package com.gamespurchase.constant;

import androidx.appcompat.widget.AppCompatButton;

import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.SagheGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.SerieGame;
import com.gamespurchase.entities.TimeGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constants {

    // Variabili Globali per i nomi dei Database
    public static final String TIMEDB = "TimeGame";
    public static final String SCHEDULEDB = "GamesToSchedule";
    public static final String PROGRESSDB = "GamesToStart";
    public static final String DATABASEDB = "GamesToDatabase";

    // Variabili Globali per individuare il giorno attuale, la sua codifica e la label attualmente visualizzata
    public static String actualDay = "";
    public static String actualDayCode = "";
    public static String actualLabel = "Try AAA";

    // Variabili Globali per l'ordinamento delle liste
    public static String sortBuyGame = "ASC";
    public static String sortProgressGame = "ASC";
    public static String sortSagheGame = "ASC";
    public static String sortDatabaseGame = "DESC";

    // Variabili Globali per il filetro delle liste
    public static String filterBuyGame = "NO";
    public static String filterSagheGame = "NO";

    // Variabili Globali per recuperare l'ultimo id in tabella
    public static int maxIdDatabaseList;
    public static int maxIdProgressList;

    // Variabili Globali per le liste di giorni e label
    private static List<String> dayCodeList = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun");
    private static List<String> listGameList = Arrays.asList("Try AAA", "Try AA", "AAA", "AA", "A", "Still", "DLC", "Old", "PlayStation 5", "PlayStation 4", "PlayStation 3", "PlayStation 2", "PlayStation", "PlayStation Portable", "PlayStation Vita", "Nintendo Switch", "Nintendo Wii", "Nintendo Wii U", "Nintendo 3DS", "Nintendo DS", "Game Boy Advance", "Game Boy Color", "Game Boy", "Xbox 360", "Xbox One", "Xbox Series X", "Graphic Adventure");

    // Variabili Globali per recuperare i dati a DB
    private static List<SerieGame> gameSerieList;
    private static List<SagheGame> gameSagheList;
    private static List<TimeGame> timeGameList;
    private static List<DatabaseGame> databaseGameList;
    private static List<ScheduleGame> scheduleGameList;
    private static List<SagheDatabaseGame> sagheDatabaseGameList;

    private static List<ProgressGame> actualLabelProgressGameList;
    private static List<ProgressGame> allLabelProgressGameList;
    private static HashMap<String, List<ProgressGame>> progressGameMap = new HashMap<>();
    private static List<ProgressGame> actualDayScheduledGameList;
    private static List<String> allDayScheduleGameList;

    private static List<AppCompatButton> timeButtonList;
    private static List<AppCompatButton> gameButtonList;

    // Variabili Globali per i contatori
    private static int counterProgressGame;
    private static int counterSagheDatabaseGame;
    private static HashMap<String, Integer> counterBuyGame = new HashMap<>();
    private static HashMap<String, Integer> counterDatabaseGame = new HashMap<>();

    public static HashMap<String, Integer> getCollectionInfoMap() {
        return collectionInfoMap;
    }

    public static void setCollectionInfoMap(HashMap<String, Integer> collectionInfoMap) {
        Constants.collectionInfoMap = collectionInfoMap;
    }

    private static HashMap<String, Integer> collectionInfoMap = new HashMap<>();

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

    public static String getActualLabel() {
        return actualLabel;
    }

    public static void setActualLabel(String actualLabel) {
        Constants.actualLabel = actualLabel;
    }

    public static String getSortBuyGame() {
        return sortBuyGame;
    }

    public static void setSortBuyGame(String sortBuyGame) {
        Constants.sortBuyGame = sortBuyGame;
    }

    public static String getSortProgressGame() {
        return sortProgressGame;
    }

    public static void setSortProgressGame(String sortProgressGame) {
        Constants.sortProgressGame = sortProgressGame;
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

    public static List<ProgressGame> getActualLabelProgressGameList() {
        return actualLabelProgressGameList;
    }

    public static void setActualLabelProgressGameList(List<ProgressGame> actualLabelProgressGameList) {
        Constants.actualLabelProgressGameList = actualLabelProgressGameList;
    }

    public static List<ProgressGame> getAllLabelProgressGameList() {
        return allLabelProgressGameList;
    }

    public static void setAllLabelProgressGameList(List<ProgressGame> allLabelProgressGameList) {
        Constants.allLabelProgressGameList = allLabelProgressGameList;
    }

    public static List<ProgressGame> getActualDayScheduledGameList() {
        return actualDayScheduledGameList;
    }

    public static void setActualDayScheduledGameList(List<ProgressGame> actualDayScheduledGameList) {
        Constants.actualDayScheduledGameList = actualDayScheduledGameList;
    }

    public static List<String> getAllDayScheduleGameList() {
        return allDayScheduleGameList;
    }

    public static void setAllDayScheduleGameList(List<String> allDayScheduleGameList) {
        Constants.allDayScheduleGameList = allDayScheduleGameList;
    }

    public static List<SagheDatabaseGame> getSagheDatabaseGameList() {
        return sagheDatabaseGameList;
    }

    public static void setSagheDatabaseGameList(List<SagheDatabaseGame> sagheDatabaseGameList) {
        Constants.sagheDatabaseGameList = sagheDatabaseGameList;
    }

    public static List<DatabaseGame> getDatabaseGameList() {
        return databaseGameList;
    }

    public static void setDatabaseGameList(List<DatabaseGame> databaseGameList) {
        Constants.databaseGameList = databaseGameList;
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

    public static HashMap<String, List<ProgressGame>> getProgressGameMap() {
        return progressGameMap;
    }

    public static void setProgressGameMap(HashMap<String, List<ProgressGame>> scheduledGameMap) {
        Constants.progressGameMap = scheduledGameMap;
    }

    public static ProgressGame getProgressGameCopy() {
        return progressGameCopy;
    }

    public static void setProgressGameCopy(ProgressGame progressGameCopy) {
        Constants.progressGameCopy = progressGameCopy;
    }


    public static List<ScheduleGame> getScheduleGameList() {
        return scheduleGameList;
    }

    public static void setScheduleGameList(List<ScheduleGame> scheduleGameList) {
        Constants.scheduleGameList = scheduleGameList;
    }


    public static List<TimeGame> getTimeGameList() {
        return timeGameList;
    }

    public static void setTimeGameList(List<TimeGame> timeGameList) {
        Constants.timeGameList = timeGameList;
    }
}
