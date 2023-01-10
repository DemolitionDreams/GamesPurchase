package com.gamespurchase.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
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
    private static DatabaseReference buyDBReference = DB_INSTANCE.getReference("GamesToBuy");
    private static DatabaseReference databaseDBReference = DB_INSTANCE.getReference("GamesToDatabase");
    private static DatabaseReference scheduleDBReference = DB_INSTANCE.getReference("GamesToSchedule");
    private static DatabaseReference startDBReference = DB_INSTANCE.getReference("GamesToStart");
    private static DatabaseReference timeDBReference = DB_INSTANCE.getReference("TimeGame");

    // INSERT/UPDATE
    public static void insertUpdateScheduleDB(@NonNull ScheduleGame scheduleGame) {
        scheduleDBReference.child(scheduleGame.getId()).setValue(scheduleGame);
        Log.i("GamesPurchase", "Aggiornata schedulazione di: " + scheduleGame.getDay() + ": " + scheduleGame.getPositionAndGame());
    }

    public static void insertUpdateTimeDB(@NonNull TimeGame timeGame) {
        timeDBReference.child(timeGame.getId()).setValue(timeGame);
        Log.i("GamesPurchase", "Aggiunto orario: " + timeGame.getHour() + " (" + timeGame.getId() + ")");
    }

    public static void insertUpdateBuyDB(@NonNull BuyGame buyGame) {
        buyDBReference.child(buyGame.getId()).setValue(buyGame);
        Log.i("GamesPurchase", "Aggiunto: " + buyGame.getName() + " (" + buyGame.getPlatform() + ")" + " con ID " + buyGame.getId() + ", appartenente alla saga " + buyGame.getSaga() + " con priorità " + buyGame.getPriority() + " (" + buyGame.getCheckInTransit() + ")");
    }

    public static void insertUpdateDatabaseDB(@NonNull DatabaseGame databaseGame) {
        databaseDBReference.child(databaseGame.getId()).setValue(databaseGame);
        Log.i("GamesPurchase", "Aggiunto: " + databaseGame.getName() + " (" + databaseGame.getPlatform() + ")" + " con ID " + databaseGame.getId() + ", appartenente alla saga " + databaseGame.getSaga() + ". " + "Finito? (" + databaseGame.getFinished() + ") - Acquistato? (" + databaseGame.getBuyed() + ")");
    }

    public static void insertUpdateStartDB(@NonNull ProgressGame progressGame){
        startDBReference.child(progressGame.getId()).setValue(progressGame);
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

    public static List<ProgressGame> selectStartDB(String orderElement) {

        List<ProgressGame> progressGameList = new ArrayList<>();
        startDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
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

    public static List<BuyGame> selectBuyDB(String orderElement) {

        List<BuyGame> buyGameList = new ArrayList<>();
        buyDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                BuyGame buyGame = dataSnapshot.getValue(BuyGame.class);
                buyGameList.add(buyGame);
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
        return buyGameList;
    }

    public static List<DatabaseGame> selectDatabaseDB(String orderElement) {

        List<DatabaseGame> databaseGameList = new ArrayList<>();
        databaseDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                DatabaseGame databaseGame = dataSnapshot.getValue(DatabaseGame.class);
                databaseGameList.add(databaseGame);
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
        return databaseGameList;
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

    public static <T> List<ProgressGame> filterStartDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<ProgressGame> progressGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        startDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
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

    public static <T> List<BuyGame> filterBuyDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<BuyGame> buyGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        buyDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                BuyGame buyGame = dataSnapshot.getValue(BuyGame.class);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringBuyGame(Objects.requireNonNull(buyGame), fieldToCompare, (String) valueToCompare)) {
                        buyGameList.add(buyGame);
                        Log.i("GamesPurchase", "Filtrato: " + buyGame.getName() + " (" + buyGame.getPlatform() + ") " + "con ID " + buyGame.getId() + " appartenente alla saga " + buyGame.getSaga() + " con priorità " + buyGame.getPriority() + " (" + buyGame.getCheckInTransit() + ")");
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodBuyGame(Objects.requireNonNull(buyGame), fieldToCompare, valueToCompare)) {
                        buyGameList.add(buyGame);
                        Log.i("GamesPurchase", "Filtrato: " + buyGame.getName() + " (" + buyGame.getPlatform() + ") " + "con ID " + buyGame.getId() + " appartenente alla saga " + buyGame.getSaga() + " con priorità " + buyGame.getPriority() + " (" + buyGame.getCheckInTransit() + ")");
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
        return buyGameList;
    }

    public static <T> List<DatabaseGame> filterDatabaseDB(String orderElement, String fieldToCompare, T valueToCompare) {

        List<DatabaseGame> databaseGameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        databaseDBReference.orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                DatabaseGame databaseGame = dataSnapshot.getValue(DatabaseGame.class);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringDatabaseGame(Objects.requireNonNull(databaseGame), fieldToCompare, (String) valueToCompare)) {
                        databaseGameList.add(databaseGame);
                        Log.i("GamesPurchase", "Filtrato: " + databaseGame.getName() + " (" + databaseGame.getPlatform() + ") " + "con ID " + databaseGame.getId() + " appartenente alla saga " + databaseGame.getSaga() + ". " + "Finito? (" + databaseGame.getFinished() + ") - Acquistato? (" + databaseGame.getBuyed() + ")");
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodDatabaseGame(Objects.requireNonNull(databaseGame), fieldToCompare, valueToCompare)) {
                        databaseGameList.add(databaseGame);
                        Log.i("GamesPurchase", "Filtrato: " + databaseGame.getName() + " (" + databaseGame.getPlatform() + ") " + "con ID " + databaseGame.getId() + " appartenente alla saga " + databaseGame.getSaga() + ". " + "Finito? (" + databaseGame.getFinished() + ") - Acquistato? (" + databaseGame.getBuyed() + ")");
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
        return databaseGameList;
    }

    // DELETE
    public static void deleteScheduleDB(ScheduleGame scheduleGame) {

        scheduleDBReference.child(scheduleGame.getDay()).removeValue();
        Log.i("GamesPurchase", "Cancellata schedulazione di: " + scheduleGame.getDay());
    }

    public static void deleteStartDB(ProgressGame progressGame) {

        startDBReference.child(progressGame.getId()).removeValue();
        Log.i("GamesPurchase", "Cancellato elemento dallo startDB: " + progressGame.getId());
    }

    public static void deleteBuyDB(BuyGame buyGame) {

        buyDBReference.child(buyGame.getId()).removeValue();
        Log.i("GamesPurchase", "Cancellato elemento dal BuyDB: " + buyGame.getName());
    }

    public static void deleteDatabaseDB(DatabaseGame databaseGame) {

        databaseDBReference.child(databaseGame.getId()).removeValue();
        Log.i("GamesPurchase", "Cancellato elemento dal DatabaseDB: " + databaseGame.getName());
    }
}
