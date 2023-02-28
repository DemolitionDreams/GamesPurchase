package com.gamespurchase.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Queries {

    private static final FirebaseDatabase DB_INSTANCE = FirebaseDatabase.getInstance("https://gamespurchase-b1f3d-default-rtdb.europe-west1.firebasedatabase.app/");

    // INSERT/UPDATE
    public static <T> void insertUpdateItemDB(@NonNull T game, String idElement, String nameDB) {
        DB_INSTANCE.getReference(nameDB).child(idElement).setValue(game);
        Log.i("GamesPurchase", "Inserito elemento " + idElement + " da " + nameDB);
    }

    // SELECT ALL
    public static <V> List<V> selectDatabaseDB(String nameDB, String orderElement, Class<V> vClass) {

        List<V> gameList = new ArrayList<>();
        DB_INSTANCE.getReference(nameDB).orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                V game = dataSnapshot.getValue(vClass);
                gameList.add(game);
                //Log.i("GamesPurchase", "Filtrato elemento da " + nameDB);
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
        return gameList;
    }

    // SELECT
    public static <T, V> List<V> filterDatabaseDB(String nameDB, String orderElement, String fieldToCompare, T valueToCompare, Class<V> vClass) {

        List<V> gameList = new ArrayList<>();
        InvokeGetterSetter invokeGetterSetter = new InvokeGetterSetter();
        DB_INSTANCE.getReference(nameDB).orderByChild(orderElement).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                V game = dataSnapshot.getValue(vClass);

                if (valueToCompare instanceof String) {
                    if (invokeGetterSetter.reflectionMethodForStringGame(Objects.requireNonNull(game), fieldToCompare, (String) valueToCompare)) {
                        gameList.add(game);
                        Log.i("GamesPurchase", "Filtrato elemento da " + nameDB);
                    }
                } else {
                    if (invokeGetterSetter.reflectionMethodGame(Objects.requireNonNull(game), fieldToCompare, valueToCompare)) {
                        gameList.add(game);
                        Log.i("GamesPurchase", "Filtrato elemento da " + nameDB);
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
        return gameList;
    }

    // DELETE
    public static void deleteItemDB(String nameDB, String idElement) {

        DB_INSTANCE.getReference(nameDB).child(idElement).removeValue();
        Log.i("GamesPurchase", "Cancellato elemento " + idElement + " da " + nameDB);
    }
}
