package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.TimeGame;
import com.gamespurchase.utilities.Utility;
import com.google.android.gms.common.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

            for(SagheDatabaseGame saga : Constants.getSagheDatabaseGameList()){

                try{
                    saga.getGamesBuy().size();
                } catch(Exception e){
                    List<DatabaseGame> gamesEmptyList = new ArrayList<>();
                    saga.setGamesBuy(gamesEmptyList);
                }
                try{
                    saga.getGamesNotBuy().size();
                } catch(Exception e){
                    List<DatabaseGame> gamesEmptyList = new ArrayList<>();
                    saga.setGamesNotBuy(gamesEmptyList);
                }
            }

            Constants.maxIdProgressList = CollectionUtils.isEmpty(Constants.getAllLabelProgressGameList())
                    ? 0
                    : Integer.parseInt(Constants.getAllLabelProgressGameList().stream()
                    .max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
        }, 2000);

        handler = new Handler();
        handler.postDelayed(() -> {
            fillAllLabelGame();
            ImageButton startButton = findViewById(R.id.start_button);
            Drawable gameListEnabled = getResources().getDrawable(R.drawable.icon_game_list);
            startButton.setBackground(gameListEnabled);
            startButton.setOnClickListener(this::callStartActivity);
            ImageButton scheduleButton = findViewById(R.id.schedule_button);
            Drawable calendarEnabled = getResources().getDrawable(R.drawable.icon_calendar);
            scheduleButton.setBackground(calendarEnabled);
            scheduleButton.setOnClickListener(this::callScheduleActivity);

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

    public static <O> boolean existsMethod(final String methodName, final O o) {
        return Stream.of(o.getClass().getMethods()).map(Method::getName).anyMatch(methodName::equals);
    }

    public void fillCollectionInfoHashMap(){
        Integer buyedCount = 0;
        Integer notBuyedCount = 0;
        Constants.getCollectionInfoMap().put("Completi", 0);
        Constants.getCollectionInfoMap().put("Da Completare", 0);
        Constants.setDatabaseGameList(new ArrayList<>());

        for(SagheDatabaseGame saga : Constants.getSagheDatabaseGameList()){
            Log.i("GamesPurchase", "games" + saga.toString());
            buyedCount = buyedCount + (CollectionUtils.isEmpty(saga.getGamesBuy()) ? 0 : saga.getGamesBuy().size());
            notBuyedCount = notBuyedCount + (CollectionUtils.isEmpty(saga.getGamesNotBuy()) ? 0 : saga.getGamesNotBuy().size());

            if(!CollectionUtils.isEmpty(saga.getGamesBuy())) {
                Constants.getDatabaseGameList().addAll(saga.getGamesBuy());
            }
            if(!CollectionUtils.isEmpty(saga.getGamesNotBuy())) {
                Constants.getDatabaseGameList().addAll(saga.getGamesNotBuy());
            }
        }

        Constants.getCollectionInfoMap().put("Acquistati", buyedCount);
        Constants.getCollectionInfoMap().put("Mancanti", notBuyedCount);

        if(!CollectionUtils.isEmpty(Constants.getDatabaseGameList())){
            for (DatabaseGame databaseGame : Constants.getDatabaseGameList()) {
                if (databaseGame.getFinished()) {
                    Constants.getCollectionInfoMap().put("Completi", Constants.getCollectionInfoMap().get("Completi") + 1);
                } else {
                    Constants.getCollectionInfoMap().put("Da Completare", Constants.getCollectionInfoMap().get("Da Completare") + 1);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setGlobalVariables();
        Dialog dialog = new Dialog(this);
        AppCompatButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(x -> {
            fillCollectionInfoHashMap();

            View popupView = Utility.createPopUp(R.layout.popup_info_game, this, dialog);
            TextView buyedNum = popupView.findViewById(R.id.buyed_num);
            buyedNum.setText(String.valueOf(Constants.getCollectionInfoMap().get("Acquistati")));
            TextView notBuyedNum = popupView.findViewById(R.id.not_buyed_num);
            notBuyedNum.setText(String.valueOf(Constants.getCollectionInfoMap().get("Mancanti")));
            TextView finishedNum = popupView.findViewById(R.id.finished_num);
            finishedNum.setText(String.valueOf(Constants.getCollectionInfoMap().get("Completi")));
            TextView notFinishedNum = popupView.findViewById(R.id.not_finished_num);
            notFinishedNum.setText(String.valueOf(Constants.getCollectionInfoMap().get("Da Completare")));

            dialog.show();
        });
       /* List<DatabaseGame> buy = new ArrayList<>();
        DatabaseGame b = new DatabaseGame();
        b.setId("0");
        b.setName("Bayonetta");
        b.setPriority("HIGH");
        b.setPlatform("PS3");
        b.setFinished(true);
        b.setCheckInTransit(false);
        buy.add(b);
        List<DatabaseGame> noBuy = new ArrayList<>();

        DatabaseGame c = new DatabaseGame();
        c.setId("1");
        c.setName("Bayonetta 3");
        c.setPriority("HIGH");
        c.setPlatform("Switch");
        c.setFinished(false);
        c.setCheckInTransit(false);
        noBuy.add(c);
        SagheDatabaseGame s = new SagheDatabaseGame();
        s.setName("Bayonetta");
        s.setFinishAll(false);
        s.setBuyAll(false);
        s.setGamesBuy(buy);
        s.setGamesNotBuy(noBuy);
        s.setId("0");
        Queries.insertUpdateItemDB(s, s.getId(), Constants.DATABASEDB);
        */
    }

    public void callCompletedActivity(View view) {
        Intent intent = new Intent(this, CompletedActivity.class);
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



