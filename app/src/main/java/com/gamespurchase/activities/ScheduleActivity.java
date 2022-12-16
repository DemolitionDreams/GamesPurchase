package com.gamespurchase.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ScheduleGame;

import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    Queries queries = new Queries();

    public void insertScheduleGameInScheduleDBAndCode(String day, String time, String name){

        String dayCode = convertDay(day);
        String id = dayCode + " - " + time;

        ScheduleGame scheduleGame = new ScheduleGame();
        scheduleGame.setId(id);
        scheduleGame.setTime(time);
        scheduleGame.setDay(day);
        scheduleGame.setName(name);
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

    public void onClickAddScheduleGame(View view){

        AutoCompleteTextView day = findViewById(R.id.day_text);
        String dayText = day.getText().toString();
        AutoCompleteTextView time = findViewById(R.id.time_text);
        String timeText = time.getText().toString();
        AutoCompleteTextView name = findViewById(R.id.name_text);
        String nameText = name.getText().toString();

        insertScheduleGameInScheduleDBAndCode(dayText, timeText, nameText);
    }

    public void setGlobalVariables(){
        List<ScheduleGame> scheduleGameList = Queries.selectScheduleDB("Day");
        Constants.setScheduleGameList(scheduleGameList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().hide();
        setGlobalVariables();
    }
}



