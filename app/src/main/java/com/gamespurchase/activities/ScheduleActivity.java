package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.ScheduleGame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    Dialog dialog;

    public void insertScheduleGameInScheduleDBAndCode(String day, Integer position, ProgressGame progressGame){

        ScheduleGame scheduleGame = new ScheduleGame();
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        if(!scheduleGameList.isEmpty()) {
            scheduleGameList.get(0).getPositionAndGame().put(position, progressGame);

            scheduleGame.setDayCode(scheduleGameList.get(0).getDayCode());
            scheduleGame.setDay(day);
            scheduleGame.setPositionAndGame(scheduleGameList.get(0).getPositionAndGame());
        }
        Queries.insertUpdateScheduleDB(scheduleGame);
    }

    private View createPopUp(int id) {
        dialog = new Dialog(ScheduleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    public void setGlobalVariables(String dayCode){
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("dayCode", "dayCode", dayCode);
        List<ProgressGame> progressGameList = new ArrayList(scheduleGameList.get(0).getPositionAndGame().values());
        Constants.setGameProgressList(progressGameList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().hide();
        Date date = new Date();

        setGlobalVariables(date.toString().toLowerCase(Locale.ROOT).substring(0, 3));
    }
}



