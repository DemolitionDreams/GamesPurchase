package com.gamespurchase.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.ScheduleGame;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    Queries queries = new Queries();

    public void insertScheduleGameInScheduleDBAndCode(String day, Integer position, ProgressGame progressGame){

        ScheduleGame scheduleGame = new ScheduleGame();
        scheduleGame.setDay(day);

        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        if(!scheduleGameList.isEmpty()) {
            scheduleGameList.get(0).getPositionAndGame().put(position, progressGame);
            scheduleGame.setPositionAndGame(scheduleGameList.get(0).getPositionAndGame());
        }

        Queries.insertUpdateScheduleDB(scheduleGame);
    }

    /*
    private void populateDayTable(){
        // findByView
        // getText()
        List<ScheduleGame> scheduleGameList = Constants.getScheduleGameList().stream().filter(x -> x.getDay().equals()).collect(Collectors.toList());
        scheduleGameList = scheduleGameList.stream().sorted(Comparator.comparing(ScheduleGame::getTime)).collect(Collectors.toList());

    }*/

    private String convertDay(String day){
        String dayCode = "";

        switch (day){
            case "Lunedi":
                dayCode = "mon";
                break;
            case "Martedi":
                dayCode = "tue";
                break;
            case "Mercoledi":
                dayCode = "wed";
                break;
            case "Giovedi":
                dayCode = "thu";
                break;
            case "Venerdi":
                dayCode = "fri";
                break;
            case "Sabato":
                dayCode = "sat";
                break;
            case "Domenica":
                dayCode = "sun";
                break;
            default:
        }
        return dayCode;
    }

    public void setGlobalVariables(String day){
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        List<ProgressGame> progressGameList = new ArrayList(scheduleGameList.get(0).getPositionAndGame().values());
        Constants.setGameProgressList(progressGameList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().hide();
        setGlobalVariables("Lunedi");
    }
}



