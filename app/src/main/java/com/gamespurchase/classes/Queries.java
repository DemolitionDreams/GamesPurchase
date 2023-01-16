package com.gamespurchase.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.TimeGame;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Queries {

    private static final FirebaseDatabase DB_INSTANCE = FirebaseDatabase.getInstance("https://gamespurchase-b1f3d-default-rtdb.europe-west1.firebasedatabase.app/");
    private static DatabaseReference timeDBReference = DB_INSTANCE.getReference("TimeGame");
    private static DatabaseReference scheduleDBReference = DB_INSTANCE.getReference("GamesToSchedule");
    private static DatabaseReference progressDBReference = DB_INSTANCE.getReference("GamesToStart");
    private static DatabaseReference databaseDBReference = DB_INSTANCE.getReference("GamesToDatabase");

    // INSERT/UPDATE
    public static void insertUpdateTimeDB(@NonNull TimeGame timeGame) {
        timeDBReference.child(timeGame.getId()).setValue(timeGame);
        Log.i("GamesPurchase", "Aggiunto orario: " + timeGame.getHour() + " (" + timeGame.getId() + ")");
    }

    public static void insertUpdateScheduleDB(@NonNull ScheduleGame scheduleGame) {
        scheduleDBReference.child(scheduleGame.getId()).setValue(scheduleGame);
        Log.i("GamesPurchase", "Aggiornata schedulazione di: " + scheduleGame.getDay() + ": " + scheduleGame.getPositionAndGame());
    }

    public static void insertUpdateProgressDB(@NonNull ProgressGame progressGame){
        progressDBReference.child(progressGame.getId()).setValue(progressGame);
    }

    public static void insertUpdateDatabaseDB(@NonNull SagheDatabaseGame sagheDatabaseGame) {
        databaseDBReference.child(sagheDatabaseGame.getId()).setValue(sagheDatabaseGame);
        Log.i("GamesPurchase", "Aggiunta Saga: " + sagheDatabaseGame.getName() + " (ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati e " + sagheDatabaseGame.getGamesNotBuy().size() + " giochi da comprare");
    }

    // SELECT ALL
    public static List<TimeGame> selectTimeDB(String orderElement) {

        List<TimeGame> timeGameList = new ArrayList<>();
        timeDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                TimeGame timeGame = dataSnapshot.getValue(TimeGame.class);
                timeGameList.add(timeGame);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return timeGameList;
    }

    public static List<ScheduleGame> selectScheduleDB(String orderElement) {

        List<ScheduleGame> scheduleGameList = new ArrayList<>();
        scheduleDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                ScheduleGame scheduleGame = dataSnapshot.getValue(ScheduleGame.class);
                scheduleGameList.add(scheduleGame);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return scheduleGameList;
    }

    public static List<ProgressGame> selectProgressDB(String orderElement) {

        List<ProgressGame> progressGameList = new ArrayList<>();
        progressDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                ProgressGame progressGame = dataSnapshot.getValue(ProgressGame.class);
                progressGameList.add(progressGame);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return progressGameList;
    }

    public static List<SagheDatabaseGame> selectDatabaseDB(String orderElement) {

        List<SagheDatabaseGame> sagheDatabaseGameList = new ArrayList<>();
        databaseDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                SagheDatabaseGame sagheDatabaseGame = dataSnapshot.getValue(SagheDatabaseGame.class);
                sagheDatabaseGameList.add(sagheDatabaseGame);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return sagheDatabaseGameList;
    }

    // SELECT
    public static <T> List<ScheduleGame> filterScheduleDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<ScheduleGame> scheduleGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        scheduleDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                ScheduleGame scheduleGame = dataSnapshot.getValue(ScheduleGame.class);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringScheduleGame(Objects.requireNonNull(scheduleGame), fieldToCompare, (String) valueToCompare)) {
                        scheduleGameList.add(scheduleGame);
                        Log.i("GamesPurchase", "Filtrata schedulazione di: " + scheduleGame.getDay() + ": " + scheduleGame.getPositionAndGame());
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodScheduleGame(Objects.requireNonNull(scheduleGame), fieldToCompare, valueToCompare)) {
                        scheduleGameList.add(scheduleGame);
                        Log.i("GamesPurchase", "Filtrata schedulazione di: " + scheduleGame.getDay() + ": " + scheduleGame.getPositionAndGame());                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return scheduleGameList;
    }

    public static <T> List<ProgressGame> filterProgressDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<ProgressGame> progressGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        progressDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                ProgressGame progressGame = dataSnapshot.getValue(ProgressGame.class);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringProgressGame(Objects.requireNonNull(progressGame), fieldToCompare, (String) valueToCompare)) {
                        progressGameList.add(progressGame);
                        Log.i("GamesPurchase", "Filtrato " + progressGame.getName() + " della saga " + progressGame.getSaga());
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodProgressGame(Objects.requireNonNull(progressGame), fieldToCompare, valueToCompare)) {
                        progressGameList.add(progressGame);
                        Log.i("GamesPurchase", "Filtrato " + progressGame.getName() + " della saga " + progressGame.getSaga());                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return progressGameList;
    }

    public static <T> List<SagheDatabaseGame> filterDatabaseDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<SagheDatabaseGame> sagheDatabaseGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        databaseDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                SagheDatabaseGame sagheDatabaseGame = dataSnapshot.getValue(SagheDatabaseGame.class);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringDatabaseGame(Objects.requireNonNull(sagheDatabaseGame), fieldToCompare, (String) valueToCompare)) {
                        sagheDatabaseGameList.add(sagheDatabaseGame);
                        Log.i("GamesPurchase", "Filtrata saga: " + sagheDatabaseGame.getName() + " (ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati e " + sagheDatabaseGame.getGamesNotBuy().size() + " giochi da comprare");
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodDatabaseGame(Objects.requireNonNull(sagheDatabaseGame), fieldToCompare, valueToCompare)) {
                        sagheDatabaseGameList.add(sagheDatabaseGame);
                        Log.i("GamesPurchase", "Filtrata saga: " + sagheDatabaseGame.getName() + " (ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati e " + sagheDatabaseGame.getGamesNotBuy().size() + " giochi da comprare");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return sagheDatabaseGameList;
    }

    // DELETE
    public static void deleteScheduleDB(ScheduleGame scheduleGame) {

        scheduleDBReference.child(scheduleGame.getDay()).removeValue();
        Log.i("GamesPurchase", "Cancellata schedulazione di: " + scheduleGame.getDay());
    }

    public static void deleteProgressDB(ProgressGame progressGame) {

        progressDBReference.child(progressGame.getId()).removeValue();
        Log.i("GamesPurchase", "Cancellato elemento dallo startDB: " + progressGame.getId());
    }

    public static void deleteDatabaseDB(SagheDatabaseGame sagheDatabaseGame) {

        databaseDBReference.child(sagheDatabaseGame.getId()).removeValue();
        Log.i("GamesPurchase", "Cancellata saga " + sagheDatabaseGame.getName() + " (ID =" + sagheDatabaseGame.getId() + ") dal DatabaseDB");
    }
}
