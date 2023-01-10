package com.gamespurchase.classes;

import android.util.Log;

import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
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

    public <T> boolean reflectionMethodBuyGame(BuyGame buyGame, String parameterName, T compareObject) {

        Class<? extends BuyGame> c = buyGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(buyGame)).equals(compareObject);
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter BuyGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodDatabaseGame(DatabaseGame databaseGame, String parameterName, T compareObject) {

        Class<? extends DatabaseGame> c = databaseGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {
                    return Objects.requireNonNull(method.invoke(databaseGame)).equals(compareObject);
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

    public <T> boolean reflectionMethodForStringBuyGame(BuyGame buyGame, String parameterName, String compareObject) {

        Class<? extends BuyGame> c = buyGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(buyGame);
                    return Objects.requireNonNull(methodToString).toLowerCase(Locale.ROOT).contains(compareObject.toLowerCase(Locale.ROOT));
                } catch (Exception e) {

                    Log.e("GamesPurchase", "Errore Reflection Getter BuyGame");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public <T> boolean reflectionMethodForStringDatabaseGame(DatabaseGame databaseGame, String parameterName, String compareObject) {

        Class<? extends DatabaseGame> c = databaseGame.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {

            if (method.getName().contains("get") && method.getName().toLowerCase(Locale.ROOT).contains(parameterName.toLowerCase(Locale.ROOT))) {
                try {

                    String methodToString = (String) method.invoke(databaseGame);
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


