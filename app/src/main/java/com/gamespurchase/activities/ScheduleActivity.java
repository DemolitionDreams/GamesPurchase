package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.TimeGame;
import com.gamespurchase.utilities.CompareUtility;
import com.gamespurchase.utilities.DatabaseUtility;
import com.gamespurchase.utilities.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    OnSwipeTouchListener onSwipeTouchListener;

    public void insertScheduleGameInScheduleDBAndCode(ScheduleGame scheduleGame) {
        Queries.insertUpdateItemDB(scheduleGame, scheduleGame.getId(), Constants.SCHEDULEDB);
        Optional<ScheduleGame> optScheduleGame = Constants.getScheduleGameList().stream().filter(x -> x.getId().equals(scheduleGame.getId())).findAny();
        if (optScheduleGame.isPresent()) {
            Constants.getScheduleGameList().set(Constants.getScheduleGameList().indexOf(optScheduleGame.get()), scheduleGame);
        } else {
            Constants.getScheduleGameList().add(scheduleGame);
        }
        updateAllDayScheduledGameList();
    }

    public void insertProgressGameInScheduleDBAndCode(String day, String position, ProgressGame progressGame) {
        Optional<ScheduleGame> oldScheduleGame = Constants.getScheduleGameList().stream().filter(x -> x.getDay().equals(day)).findAny();
        if (oldScheduleGame.isPresent()) {
            oldScheduleGame.get().getPositionAndGame().put(position, progressGame);
            Queries.insertUpdateItemDB(oldScheduleGame.get(), oldScheduleGame.get().getId(), Constants.SCHEDULEDB);
        }
        updateAllDayScheduledGameList();
        fillAllLabelFromLabelMap();
    }

    public static void fillAllLabelFromLabelMap(){
        Constants.setAllLabelProgressGameList(new ArrayList<>());
        for(List<ProgressGame> pgList : Constants.getProgressGameMap().values()){
            Constants.getAllLabelProgressGameList().addAll(pgList);
        }
    }

    private void updateAllDayScheduledGameList() {
        List<String> allDayScheduledList = new ArrayList<>();
        Constants.getScheduleGameList().forEach(x -> {
            for (String key : x.getPositionAndGame().keySet()) {
                allDayScheduledList.add(Objects.requireNonNull(x.getPositionAndGame().get(key)).getName());
            }
        });
        Constants.setAllDayScheduleGameList(allDayScheduledList);
    }

    public void updateAllScheduleGame(ProgressGame progressGame, String oldName) {
        Constants.getScheduleGameList().forEach(x -> {
            for (String key : x.getPositionAndGame().keySet()) {
                if (Objects.requireNonNull(x.getPositionAndGame().get(key)).getName().equals(oldName)) {
                    x.getPositionAndGame().put(key, progressGame);
                }
            }
            insertScheduleGameInScheduleDBAndCode(x);
        });
    }

    public static class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;
        View rootView;

        OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
            rootView = mainView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        void onSwipeRight() {
            swipeToDirection(Boolean.FALSE, rootView);
            this.onSwipe.swipeRight();
        }

        void onSwipeLeft() {
            swipeToDirection(Boolean.TRUE, rootView);
            this.onSwipe.swipeLeft();
        }

        void onSwipeTop() {
            this.onSwipe.swipeTop();
        }

        void onSwipeBottom() {
            this.onSwipe.swipeBottom();
        }

        interface onSwipeListener {
            void swipeRight();

            void swipeTop();

            void swipeBottom();

            void swipeLeft();
        }

        onSwipeListener onSwipe;
    }

    private static void swipeToDirection(Boolean isLeft, View rootView) {
        String actualDayCode = Constants.getActualDayCode();
        String newDayCode = changeDay(Constants.getDayCodeList(), actualDayCode, isLeft);
        Constants.setActualDayCode(newDayCode);
        Constants.setActualDay(retrieveActualDay(newDayCode));
        fillGameButton(rootView);
    }

    public void createOnClickListenerInTimeButton() {

        Constants.getTimeButtonList().forEach(x -> x.setOnLongClickListener(v -> {
            View popupView = Utility.createPopUp(R.layout.popup_change_hour, this, dialog);
            TextView hourText = popupView.findViewById(R.id.hour_text_view);
            hourText.setText(x.getText().toString().substring(0, 2));
            TextView minuteText = popupView.findViewById(R.id.minute_text_view);
            minuteText.setText(x.getText().toString().substring(3, 5));
            ImageButton changeButton = popupView.findViewById(R.id.change_button);
            AppCompatButton moreHour = popupView.findViewById(R.id.more_hour);
            moreHour.setOnClickListener(y -> {
                int actualHour = Integer.parseInt(hourText.getText().toString());
                String newHour = actualHour == 23 ? "00" : String.valueOf(actualHour + 1);
                hourText.setText(newHour.length() == 2 ? newHour : "0" + newHour);
            });
            AppCompatButton lessHour = popupView.findViewById(R.id.less_hour);
            lessHour.setOnClickListener(y -> {
                int actualHour = Integer.parseInt(hourText.getText().toString());
                String newHour = actualHour == 0 ? "23" : String.valueOf(actualHour - 1);
                hourText.setText(newHour.length() == 2 ? newHour : "0" + newHour);
            });
            AppCompatButton moreMinute = popupView.findViewById(R.id.more_minute);
            moreMinute.setOnClickListener(y -> minuteText.setText(minuteText.getText().toString().equals("00") ? "30" : "00"));
            AppCompatButton lessMinute = popupView.findViewById(R.id.less_minute);
            lessMinute.setOnClickListener(y -> minuteText.setText(minuteText.getText().toString().equals("00") ? "30" : "00"));
            changeButton.setOnClickListener(y -> {
                String hour = hourText.getText().toString() + ":" + minuteText.getText().toString();
                TimeGame timeGame = new TimeGame(x.getTag().toString(), hour);
                Queries.insertUpdateItemDB(timeGame, timeGame.getId(), Constants.TIMEDB);
                Handler handler = new Handler();
                handler.postDelayed(this::fillTimeButton, 50);
                dialog.dismiss();
            });
            dialog.show();
            return false;
        }));
    }

    public void createOnClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnClickListener(view -> Constants.getActualDayScheduledGameList().forEach(y ->
        {
            if (y.getTotal() == 0) {
                y.setTotal(y.getCurrentProgress() + 1);
            }
            int percentage = (y.getCurrentProgress() * 100) / y.getTotal();
            if (x.getText().equals(y.getName())) {
                View popupView = Utility.createPopUp(R.layout.popup_info_progress_game, this, dialog);
                TextView nameText = popupView.findViewById(R.id.name_text);
                nameText.setText(y.getName());
                ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
                progressBar.setProgress(percentage);
                TextView currentText = popupView.findViewById(R.id.current_text_view);
                currentText.setText(String.valueOf(y.getCurrentProgress()));
                TextView totalText = popupView.findViewById(R.id.total_text_view);
                totalText.setText(String.valueOf(y.getTotal()));
                TextView percentageText = popupView.findViewById(R.id.percentage_text_view);
                String percentageWithSymbol = percentage + "%";
                percentageText.setText(percentageWithSymbol);
                TextView dataText = popupView.findViewById(R.id.data_text_view);
                dataText.setText(y.getStartDate());
                AppCompatButton moreButton = popupView.findViewById(R.id.more_progress);
                moreButton.setOnClickListener(v -> changeProgress(Boolean.TRUE, y, x, currentText, totalText, percentageText, progressBar));
                AppCompatButton lessButton = popupView.findViewById(R.id.less_progress);
                lessButton.setOnClickListener(v -> changeProgress(Boolean.FALSE, y, x, currentText, totalText, percentageText, progressBar));
                dialog.show();
            }
        })));
    }

    public void fillTimeButton() {
        Constants.setTimeGameList(Queries.selectDatabaseDB(Constants.TIMEDB, "id", TimeGame.class));
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!Constants.getTimeGameList().isEmpty()) {
                Constants.getTimeButtonList().forEach(x -> x.setText(Constants.getTimeGameList().get(Integer.parseInt(x.getTag().toString())).getHour()));
            }
        }, 150);
    }

    public static void fillGameButton(View rView) {
        Optional<ScheduleGame> actualScheduleGame = Constants.getScheduleGameList().stream().filter(x -> x.getId().equals(Constants.getActualDayCode())).findAny();
        AppCompatButton dayButton = rView.findViewById(R.id.day_button);
        dayButton.setText(actualScheduleGame.isPresent() ? actualScheduleGame.get().getDay() : "DB don't load");
        if (actualScheduleGame.isPresent()) {
            List<ProgressGame> progressGameList = new ArrayList<>();
            HashMap<String, ProgressGame> gameMap = actualScheduleGame.get().getPositionAndGame();
            for (int i = 0; i < 9; i++) {
                String key = "ID".concat(String.valueOf(i));
                progressGameList.add(gameMap.get(key));
            }
            Constants.setActualDayScheduledGameList(progressGameList);
            Constants.getGameButtonList().forEach(x -> {
                if (gameMap.containsKey(x.getTag())) {
                    x.setText(Objects.requireNonNull(gameMap.get(x.getTag())).getName());
                }
            });
        }
    }

    public void changeProgress(Boolean sum, ProgressGame pg, AppCompatButton button, TextView currentText, TextView totalText, TextView percentageText, ProgressBar progressBar) {
        if (sum) {
            pg.setCurrentProgress(pg.getCurrentProgress() + 1);
            if (pg.getCurrentProgress() == pg.getTotal()) {
                addCompleteGame(pg, button.getTag().toString());
            }
        } else {
            pg.setCurrentProgress((pg.getCurrentProgress()) <= 0 ? 0 : pg.getCurrentProgress() - 1);
        }
        updateAllScheduleGame(pg, pg.getName());
        fillGameButton(rootView);
        currentText.setText(String.valueOf(pg.getCurrentProgress()));
        totalText.setText(String.valueOf(pg.getTotal()));
        int newPercentage = (pg.getCurrentProgress() * 100) / pg.getTotal();
        String newPercentageWithSymbol = newPercentage + "%";
        percentageText.setText(newPercentageWithSymbol);
        progressBar.setProgress(newPercentage);
    }

    public void addCompleteGame(ProgressGame progressGame, String tag) {

        View popupView = Utility.createPopUp(R.layout.popup_database_game, this, dialog);

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        nameText.setText(progressGame.getName());
        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        sagaText.setText(progressGame.getSaga());
        Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
        int consolePosition = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(progressGame.getPlatform());
        consoleSpinner.setSelection(consolePosition);
        Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
        int priorityPosition = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(progressGame.getPriority());
        prioritySpinner.setSelection(priorityPosition);
        CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
        finishedCheckbox.setChecked(Boolean.TRUE);
        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
        transitCheckbox.setChecked(progressGame.getCheckInTransit());
        ImageButton addButton = popupView.findViewById(R.id.add_button);
        addButton.setOnClickListener(x -> {
            DatabaseGame databaseGame = new DatabaseGame(nameText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), Boolean.TRUE, transitCheckbox.isChecked());
            resetProgressInsertDatabaseGameAndChangeActivity(databaseGame, sagaText.getText().toString(), progressGame.getBuyed(), tag, databaseGame.getName());
        });
        dialog.show();
    }

    public void resetProgressInsertDatabaseGameAndChangeActivity(DatabaseGame databaseGame, String saga, Boolean buy, String tag, String oldName) {
        ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", buy, Boolean.FALSE);
        insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), tag, newProgressGame);
        updateAllScheduleGame(newProgressGame, oldName);
        DatabaseUtility.insertNewGameDatabaseDBAndCode(databaseGame, saga, buy ? new DatabaseActivity() : new BuyActivity(), null);
        Intent intent = new Intent(this, buy ? DatabaseActivity.class : BuyActivity.class);
        startActivity(intent);
    }

    private static String changeDay(List<String> dayList, String actualDay, Boolean sum) {
        String newDay;
        int oldIndex = Constants.getDayCodeList().indexOf(actualDay);
        if (sum) {
            newDay = (oldIndex == 6) ? dayList.get(0) : dayList.get(oldIndex + 1);
        } else {
            newDay = (oldIndex == 0) ? dayList.get(6) : dayList.get(oldIndex - 1);
        }
        return newDay;
    }

    public static String retrieveActualDay(String dayCode) {
        String day = "";
        switch (dayCode) {
            case "mon":
                day = "Lunedi";
                break;
            case "tue":
                day = "Martedi";
                break;
            case "wed":
                day = "Mercoledi";
                break;
            case "thu":
                day = "Giovedi";
                break;
            case "fri":
                day = "Venerdi";
                break;
            case "sat":
                day = "Sabato";
                break;
            case "sun":
                day = "Domenica";
                break;
        }
        return day;
    }

    public void createOnLongClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnLongClickListener(v -> {
            ProgressGame progressGame = Constants.getActualDayScheduledGameList().get(Integer.parseInt(x.getTag().toString().substring(x.getTag().toString().length() - 1)));
            View popupView = Utility.createPopUp(R.layout.popup_scheduled_game, this, dialog);
            AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
            nameText.setText(progressGame.getName());
            Utility.addAutoCompleteVoice(this, popupView, R.id.name_text, Constants.getAllLabelProgressGameList(), ProgressGame::getName);
            String oldName = progressGame.getName();
            TextView sagaText = popupView.findViewById(R.id.saga_text);
            sagaText.setText(progressGame.getSaga());
            Utility.addAutoCompleteVoice(this, popupView, R.id.saga_text, Constants.getAllLabelProgressGameList(), ProgressGame::getSaga);
            TextView dataText = popupView.findViewById(R.id.data_edit_text);
            dataText.setText(progressGame.getStartDate());
            TextView hourText = popupView.findViewById(R.id.hour_edit_text);
            hourText.setText(String.valueOf(progressGame.getHour()));
            TextView actualText = popupView.findViewById(R.id.actual_edit_text);
            actualText.setText(String.valueOf(progressGame.getCurrentProgress()));
            TextView totalText = popupView.findViewById(R.id.total_edit_text);
            totalText.setText(String.valueOf(progressGame.getTotal()));
            actualText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(totalText.getText()) && !TextUtils.isEmpty(actualText.getText()) && !totalText.getText().toString().equals("0")) {
                        if (Integer.parseInt(actualText.getText().toString()) >= Integer.parseInt(totalText.getText().toString())) {
                            actualText.setText(String.valueOf(Integer.parseInt(totalText.getText().toString()) - 1));
                        }
                    }
                }
            });
            totalText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!TextUtils.isEmpty(totalText.getText()) && !TextUtils.isEmpty(actualText.getText())) {
                        if (Integer.parseInt(totalText.getText().toString()) <= Integer.parseInt(actualText.getText().toString())) {
                            totalText.setText(String.valueOf(Integer.parseInt(actualText.getText().toString()) + 1));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
            int consolePosition = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(progressGame.getPlatform());
            consoleSpinner.setSelection(consolePosition);
            Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
            int priorityPosition = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(progressGame.getPriority());
            prioritySpinner.setSelection(priorityPosition);
            CheckBox buyedCheckbox = popupView.findViewById(R.id.buyed_checkbox);
            buyedCheckbox.setChecked(progressGame.getBuyed() != null ? progressGame.getBuyed() : false);
            CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
            transitCheckbox.setChecked(progressGame.getCheckInTransit() != null ? progressGame.getCheckInTransit() : false);
            ImageButton updateButton = popupView.findViewById(R.id.update_button);
            updateButton.setOnClickListener(w ->
            {
                ProgressGame newProgressGame = new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), Integer.parseInt(hourText.getText().toString()), dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), "", buyedCheckbox.isChecked(), transitCheckbox.isChecked());
                insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), newProgressGame);
                Optional<ProgressGame> optProgressGame = Constants.getAllLabelProgressGameList().stream().filter(k -> k.getName().toLowerCase(Locale.ROOT).equals(newProgressGame.getName().toLowerCase(Locale.ROOT))).findAny();
                if(optProgressGame.isPresent()) {
                    Utility.removedItemFromDatabase(Constants.PROGRESSDB, optProgressGame.get().getId());
                    int index = Constants.getProgressGameMap().get(optProgressGame.get().getLabel()).indexOf(optProgressGame.get());
                    Constants.getProgressGameMap().get(optProgressGame.get().getLabel()).remove(index);
                }
                if (!oldName.equals("TBD")) {
                    updateAllScheduleGame(newProgressGame, oldName);
                }
                fillGameButton(rootView);
                dialog.dismiss();
            });
            ImageButton resetButton = popupView.findViewById(R.id.reset_button);
            resetButton.setOnClickListener(w ->
            {
                ProgressGame oldProgressGame = new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), Integer.parseInt(hourText.getText().toString()), dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), "", buyedCheckbox.isChecked(), transitCheckbox.isChecked());
                oldProgressGame.setId(String.valueOf(Constants.getMaxIdProgressList() + 1));
                oldProgressGame.setLabel("Still");
                ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", Boolean.FALSE, Boolean.FALSE);
                insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), newProgressGame);
                if (Collections.frequency(Constants.getAllDayScheduleGameList(), oldProgressGame.getName()) == 0) {
                    Queries.insertUpdateItemDB(oldProgressGame, oldProgressGame.getId(), Constants.PROGRESSDB);
                    List<ProgressGame> tempProgressList = Constants.getProgressGameMap().get("Still");
                    Objects.requireNonNull(tempProgressList).add(oldProgressGame);
                    tempProgressList = Utility.sortList(ProgressGame::getName, Objects.requireNonNull(tempProgressList),
                            CompareUtility.Order.ASCENDING);
                    Constants.getProgressGameMap().remove("Still");
                    Constants.getProgressGameMap().put("Still", tempProgressList);
                }
                fillGameButton(rootView);
                dialog.dismiss();
            });
            nameText.setOnItemClickListener((adapterView, view, i, l) -> {
                Optional<ProgressGame> pgOpt = Constants.getAllLabelProgressGameList().stream().filter(z -> z.getName().equals(nameText.getText().toString())).findAny();
                if (pgOpt.isPresent()) {
                    nameText.setText(pgOpt.get().getName());
                    sagaText.setText(pgOpt.get().getSaga());
                    dataText.setText(pgOpt.get().getStartDate());
                    hourText.setText(String.valueOf(pgOpt.get().getHour()));
                    totalText.setText(String.valueOf(pgOpt.get().getTotal()));
                    actualText.setText(String.valueOf(pgOpt.get().getCurrentProgress()));
                    int consolePosition1 = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(pgOpt.get().getPlatform());
                    consoleSpinner.setSelection(consolePosition1);
                    int priorityPosition1 = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(pgOpt.get().getPriority());
                    prioritySpinner.setSelection(priorityPosition1);
                    buyedCheckbox.setChecked(pgOpt.get().getBuyed());
                    transitCheckbox.setChecked(pgOpt.get().getCheckInTransit());
                }
            });
            AppCompatButton pasteButton = popupView.findViewById(R.id.paste_button);
            AppCompatButton copyButton = popupView.findViewById(R.id.copy_button);
            AppCompatButton deleteButton = popupView.findViewById(R.id.delete_button);

            if (Constants.getProgressGameCopy() != null) {
                copyButton.setVisibility(View.INVISIBLE);
                pasteButton.setVisibility(View.VISIBLE);

            } else {
                pasteButton.setVisibility(View.INVISIBLE);
                copyButton.setVisibility(View.VISIBLE);
            }

            deleteButton.setOnClickListener(w -> {
                ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", Boolean.FALSE, Boolean.FALSE);
                insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), newProgressGame);
                fillGameButton(rootView);
                dialog.dismiss();
            });

            copyButton.setOnClickListener(w -> {
                Constants.setProgressGameCopy(new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), Integer.parseInt(hourText.getText().toString()), dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), "", buyedCheckbox.isChecked(), transitCheckbox.isChecked()));
                copyButton.setVisibility(View.INVISIBLE);
                pasteButton.setVisibility(View.VISIBLE);
            });

            pasteButton.setOnClickListener(w -> {
                ProgressGame pastePG = Constants.getProgressGameCopy();
                nameText.setText(pastePG.getName());
                sagaText.setText(pastePG.getSaga());
                dataText.setText(pastePG.getStartDate());
                hourText.setText(String.valueOf(pastePG.getHour()));
                totalText.setText(String.valueOf(pastePG.getTotal()));
                actualText.setText(String.valueOf(pastePG.getCurrentProgress()));
                int pasteConsolePosition = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(pastePG.getPlatform());
                consoleSpinner.setSelection(pasteConsolePosition);
                int pastePriorityPosition = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(pastePG.getPriority());
                prioritySpinner.setSelection(pastePriorityPosition);
                buyedCheckbox.setChecked(pastePG.getBuyed());
                transitCheckbox.setChecked(pastePG.getCheckInTransit());
                Constants.setProgressGameCopy(null);
                copyButton.setVisibility(View.VISIBLE);
                pasteButton.setVisibility(View.INVISIBLE);
            });
            dialog.show();
            return true;
        }));
    }

    public void setButtonContent() {
        List<AppCompatButton> timeButtonList = new ArrayList<>();
        RelativeLayout timeRelativeLayout = rootView.findViewById(R.id.time_relative_layout);
        for (int i = 0; i < timeRelativeLayout.getChildCount(); i++) {
            AppCompatButton timeButton = (AppCompatButton) timeRelativeLayout.getChildAt(i);
            timeButtonList.add(timeButton);
        }
        Constants.setTimeButtonList(timeButtonList);
        fillTimeButton();

        List<AppCompatButton> gameButtonList = new ArrayList<>();
        RelativeLayout gameRelativeLayout = rootView.findViewById(R.id.game_relative_layout);
        for (int i = 0; i < gameRelativeLayout.getChildCount(); i++) {
            AppCompatButton gameButton = (AppCompatButton) gameRelativeLayout.getChildAt(i);
            gameButtonList.add(gameButton);
        }
        Constants.setGameButtonList(gameButtonList);
        updateAllDayScheduledGameList();
        fillGameButton(rootView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        dialog = new Dialog(this);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        Objects.requireNonNull(getSupportActionBar()).hide();
        onSwipeTouchListener = new OnSwipeTouchListener(this, rootView);
        Date date = new Date();
        String dayCode = date.toString().toLowerCase(Locale.ROOT).substring(0, 3);
        Constants.setActualDayCode(dayCode);
        Constants.setActualDay(retrieveActualDay(dayCode));
        setButtonContent();
        createOnClickListener();
        createOnLongClickListener();
        createOnClickListenerInTimeButton();
    }
}



