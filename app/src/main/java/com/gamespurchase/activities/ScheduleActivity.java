package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.ScheduleGame;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;

    public void insertScheduleGameInScheduleDBAndCode(String day, String position, ProgressGame progressGame){

        ScheduleGame scheduleGame = new ScheduleGame();
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        if(!scheduleGameList.isEmpty()) {
            scheduleGameList.get(0).getPositionAndGame().put(position, progressGame);

            scheduleGame.setId(scheduleGameList.get(0).getId());
            scheduleGame.setDay(day);
            scheduleGame.setPositionAndGame(scheduleGameList.get(0).getPositionAndGame());
        }
        scheduleGame.setDay(day);
        scheduleGame.setId("tue");
        Queries.insertUpdateScheduleDB(scheduleGame);
    }

    public void insertSchedule(String day, String dayCode, HashMap<String, ProgressGame> hp){

        ScheduleGame scheduleGame = new ScheduleGame();
        scheduleGame.setDay(day);
        scheduleGame.setId(dayCode);
        scheduleGame.setPositionAndGame(hp);
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

    public void setGlobalVariables(){
        Date date = new Date();
        String dayCode = date.toString().toLowerCase(Locale.ROOT).substring(0, 3);
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("id", "id", dayCode);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            AppCompatButton dayButton = rootView.findViewById(R.id.day_button);
            dayButton.setText(scheduleGameList.get(0).getDay());
            List<ProgressGame> progressGameList = new ArrayList(scheduleGameList.get(0).getPositionAndGame().values());
            Constants.setGameProgressList(progressGameList);
        }, 500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        getSupportActionBar().hide();
        setGlobalVariables();
    }
}

/*
        ProgressGame pg = new ProgressGame();
        pg.setName("Disney's PK: Out of The Shadows");
        pg.setBuyed(Boolean.TRUE);
        pg.setCurrentProgress(4);
        pg.setTotal(6);
        pg.setPercentage("60%");
        pg.setStartDate("16 Dicembre 2022");

        ProgressGame pg0 = new ProgressGame("", 0, 0, "", "Primo", Boolean.FALSE);
        ProgressGame pg1 = new ProgressGame("", 0, 0, "", "Secondo", Boolean.FALSE);
        ProgressGame pg2 = new ProgressGame("", 0, 0, "", "Terzo", Boolean.FALSE);
        ProgressGame pg3 = new ProgressGame("", 0, 0, "", "Quarto", Boolean.FALSE);
        ProgressGame pg4 = new ProgressGame("", 0, 0, "", "Quinto", Boolean.FALSE);
        ProgressGame pg5 = new ProgressGame("", 0, 0, "", "Sesto", Boolean.FALSE);
        ProgressGame pg6 = new ProgressGame("", 0, 0, "", "Settimo", Boolean.FALSE);
        ProgressGame pg7 = new ProgressGame("", 0, 0, "", "Ottavo", Boolean.FALSE);
        ProgressGame pg8 = new ProgressGame("", 0, 0, "", "Nono", Boolean.FALSE);
        HashMap<String, ProgressGame> hp = new HashMap<>();
        hp.put("0", pg0);
        hp.put("1", pg1);
        hp.put("2", pg2);
        hp.put("3", pg3);
        hp.put("4", pg4);
        hp.put("5", pg5);
        hp.put("6", pg6);
        hp.put("7", pg7);
        hp.put("8", pg8);
        insertSchedule("Lunedi", "mon", hp);
        insertSchedule("Martedi", "tue", hp);
        insertSchedule("Mercoledi", "wed", hp);
        insertSchedule("Giovedi", "thu", hp);
        insertSchedule("Venerdi", "fri", hp);
        insertSchedule("Sabato", "sat", hp);
        insertSchedule("Domenica", "sun", hp);
 */



