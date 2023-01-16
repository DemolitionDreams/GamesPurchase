package com.gamespurchase.classes;

import android.util.Log;

import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.entities.ScheduleGame;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

public class InvokeGetterSetter {

    // Invoca i metodi getter con la java reflection
    public <T> boolean reflectionMethodScheduleGame(ScheduleGame scheduleGame, String parameterName, T compareObject) {

        Class<? extends ScheduleGame> c = scheduleGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(scheduleGame)).equals(compareObject);
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter ScheduleGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodProgressGame(ProgressGame progressGame, String parameterName, T compareObject) {

        Class<? extends ProgressGame> c = progressGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(progressGame)).equals(compareObject);
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter ScheduleGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodDatabaseGame(SagheDatabaseGame sagheDatabaseGame, String parameterName, T compareObject) {

        Class<? extends SagheDatabaseGame> c = sagheDatabaseGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(sagheDatabaseGame)).equals(compareObject);
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter DatabaseGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodForStringScheduleGame(ScheduleGame scheduleGame, String parameterName, String compareObject) {

        Class<? extends ScheduleGame> c = scheduleGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(scheduleGame);
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

    public <T> boolean reflectionMethodForStringProgressGame(ProgressGame progressGame, String parameterName, String compareObject) {

        Class<? extends ProgressGame> c = progressGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(progressGame);
                    if(parameterName.equals("label")){
                        return Objects.requireNonNull(methodToString).toLowerCase(Locale.ROOT).equals(compareObject.toLowerCase(Locale.ROOT));
                    }
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

    public <T> boolean reflectionMethodForStringDatabaseGame(SagheDatabaseGame sagheDatabaseGame, String parameterName, String compareObject) {

        Class<? extends SagheDatabaseGame> c = sagheDatabaseGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(sagheDatabaseGame);
                    return Objects.requireNonNull(methodToString).toLowerCase(Locale.ROOT).contains(compareObject.toLowerCase(Locale.ROOT));
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter DatabaseGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}


