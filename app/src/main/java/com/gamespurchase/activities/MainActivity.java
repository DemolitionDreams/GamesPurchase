package com.gamespurchase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.TimeGame;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private void setGlobalVariables() {
        Constants.setAllLabelProgressGameList(Queries.selectDatabaseDB(Constants.PROGRESSDB,
                "name", ProgressGame.class));
        Constants.setSagheDatabaseGameList(Queries.selectDatabaseDB(Constants.DATABASEDB,
                "name", SagheDatabaseGame.class));
        Constants.setScheduleGameList(Queries.selectDatabaseDB(Constants.SCHEDULEDB,
                "name", ScheduleGame.class));
        Constants.setTimeGameList(Queries.selectDatabaseDB(Constants.TIMEDB,
                "name", TimeGame.class));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.maxIdDatabaseList = CollectionUtils.isEmpty(Constants.getSagheDatabaseGameList())
                    ? 0
                    : Integer.parseInt(Constants.getSagheDatabaseGameList().stream()
                    .max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
            Constants.maxIdProgressList = CollectionUtils.isEmpty(Constants.getAllLabelProgressGameList())
                    ? 0
                    : Integer.parseInt(Constants.getAllLabelProgressGameList().stream()
                    .max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
        }, 1200);

        handler = new Handler();
        handler.postDelayed(() -> {
            fillAllLabelGame();
            findViewById(R.id.start_button).setOnClickListener(this::callStartActivity);
        }, 1200);
    }

    public void fillAllLabelGame() {
        HashMap<String, List<ProgressGame>> startGameMap = new HashMap<>();
        Constants.getAllLabelProgressGameList().forEach(x -> {
            if (!startGameMap.containsKey(x.getLabel())) {
                startGameMap.put(x.getLabel(), new ArrayList<>());
            }
            Objects.requireNonNull(startGameMap.get(x.getLabel())).add(x);
        });
        Constants.setProgressGameMap(startGameMap);
    }

    private static void fillMaps(HashMap<String, Integer> counterBuyGame, HashMap<String, Integer> counterDatabaseGame) {

        counterBuyGame.put("Totale", 0);
        counterBuyGame.put("Transito", 0);
        counterBuyGame.put("HIGH", 0);
        counterBuyGame.put("MEDIUM", 0);
        counterBuyGame.put("LOW", 0);

        counterDatabaseGame.put("Totale", 0);
        counterDatabaseGame.put("Digitali", 0);
        counterDatabaseGame.put("Finiti", 0);
    }

    /*
    public static void count(List<BuyGame> buyGameList, List<DatabaseGame> databaseGameList) {

        fillMaps(Constants.getCounterBuyGame(), Constants.getCounterDatabaseGame());

        if(!CollectionUtils.isEmpty(buyGameList)){
            buyGameList.stream().forEach(x -> {

                Constants.getCounterBuyGame().put("Totale", Constants.getCounterBuyGame().get("Totale")+1);
                if(Constants.getCounterBuyGame().containsKey(x.getPriority())){
                    Constants.getCounterBuyGame().put(x.getPriority(), Constants.getCounterBuyGame().get(x.getPriority())+1);
                }
                if(x.getCheckInTransit()){
                    Constants.getCounterBuyGame().put("Transito", Constants.getCounterBuyGame().get("Transito")+1);
                }
            });
        }

        if(!CollectionUtils.isEmpty(databaseGameList)) {
            databaseGameList.stream().forEach(x -> {

                Constants.getCounterDatabaseGame().put("Totale", Constants.getCounterDatabaseGame().get("Totale") + 1);
                if (x.getFinished()) {
                    Constants.getCounterDatabaseGame().put("Finiti", Constants.getCounterDatabaseGame().get("Finiti") + 1);
                }
                if (x.getPlatform().equals("Digital")){
                    Constants.getCounterDatabaseGame().put("Digitali", Constants.getCounterDatabaseGame().get("Digitali") + 1);
                }
            });
        }
        Log.i("GamesPurchase", "HashMap Buy:" + Constants.getCounterBuyGame());
        Log.i("GamesPurchase", "HashMap Database:" + Constants.getCounterDatabaseGame());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setGlobalVariables();
    }

    public void callSagheActivity(View view) {
        Intent intent = new Intent(this, SagheActivity.class);
        startActivity(intent);
    }

    public void callScheduleActivity(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void callStartActivity(View view) {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

    public void callDatabaseActivity(View view) {
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }

    public void callBuyActivity(View view) {
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }
}



