package com.gamespurchase.classes;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

public class InvokeGetterSetter {

    // Invoca i metodi getter con la java reflection
    public <T, V> boolean reflectionMethodGame(V game, String parameterName, T compareObject) {

        Method[] methods = game.getClass().getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(game)).equals(compareObject);
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter DatabaseGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodForStringGame(T game, String parameterName, String compareObject) {

        Method[] methods = game.getClass().getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(game);
                    return Objects.requireNonNull(methodToString).toLowerCase(Locale.ROOT).contains(compareObject.toLowerCase(Locale.ROOT));
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter ScheduleGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}


