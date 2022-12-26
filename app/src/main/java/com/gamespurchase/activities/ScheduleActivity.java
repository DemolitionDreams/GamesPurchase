package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
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
import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.ScheduleGame;
import com.gamespurchase.entities.TimeGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    Context context;
    OnSwipeTouchListener onSwipeTouchListener;

    public void insertTimeGameInTimeDB(String id, String hour) {

        TimeGame timeGame = new TimeGame();
        timeGame.setId(id);
        timeGame.setHour(hour);
        Queries.insertUpdateTimeDB(timeGame);
    }

    public void insertScheduleGameInScheduleDBAndCode(String day, String position, ProgressGame progressGame) {

        ScheduleGame scheduleGame = new ScheduleGame();
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!scheduleGameList.isEmpty()) {
                scheduleGameList.get(0).getPositionAndGame().put(position, progressGame);

                scheduleGame.setId(scheduleGameList.get(0).getId());
                scheduleGame.setDay(day);
                scheduleGame.setPositionAndGame(scheduleGameList.get(0).getPositionAndGame());
            }
            Queries.insertUpdateScheduleDB(scheduleGame);
        }, 1000);
    }

    private View createPopUp(int id) {
        dialog = new Dialog(ScheduleActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private static String changeDay(List<String> dayList, String actualDay, Boolean sum) {

        String newDay = "";
        int oldIndex = Constants.getDayCodeList().indexOf(actualDay);
        if (sum) {
            newDay = (oldIndex == 6) ? dayList.get(0) : dayList.get(oldIndex + 1);
        } else {
            newDay = (oldIndex == 0) ? dayList.get(6) : dayList.get(oldIndex - 1);
        }
        return newDay;
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
            String actualDayCode = Constants.getActualDayCode();
            String newDayCode = changeDay(Constants.getDayCodeList(), actualDayCode, Boolean.FALSE);
            Log.i("GamesPurchase", "Movement Right -> Go to " + actualDayCode.toUpperCase(Locale.ROOT) + " a " + newDayCode.toUpperCase(Locale.ROOT));
            Constants.setActualDayCode(newDayCode);
            Constants.setActualDay(retrieveActualDay(newDayCode));
            fillGameButton(rootView);
            this.onSwipe.swipeRight();
        }

        void onSwipeLeft() {
            String actualDayCode = Constants.getActualDayCode();
            String newDayCode = changeDay(Constants.getDayCodeList(), actualDayCode, Boolean.TRUE);
            Log.i("GamesPurchase", "Movement Left -> Go to " + actualDayCode.toUpperCase(Locale.ROOT) + " a " + newDayCode.toUpperCase(Locale.ROOT));
            Constants.setActualDayCode(newDayCode);
            Constants.setActualDay(retrieveActualDay(newDayCode));
            fillGameButton(rootView);
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

    public void createOnClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnClickListener(view -> Constants.getGameProgressList().forEach(y ->
        {
            if (y.getTotal() == 0) {
                y.setTotal(1);
            }
            int percentage = (y.getCurrentProgress() * 100) / y.getTotal();
            if (x.getText().equals(y.getName())) {
                View popupView = createPopUp(R.layout.popup_info_progress_game);
                TextView nameText = popupView.findViewById(R.id.name_text);
                nameText.setText(y.getName());
                ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
                progressBar.setProgress(percentage);
                TextView percentageText = popupView.findViewById(R.id.percentage_text_view);
                percentageText.setText(String.valueOf(percentage) + "%");
                TextView dataText = popupView.findViewById(R.id.data_text_view);
                dataText.setText(y.getStartDate());
                AppCompatButton moreButton = popupView.findViewById(R.id.more_progress);
                moreButton.setOnClickListener(v -> {
                    y.setCurrentProgress(y.getCurrentProgress() + 1);
                    if (y.getCurrentProgress() == y.getTotal()) {
                        addCompleteGame(y);
                    }
                    Log.i("GamesPurchase", "ActualDay: " + Constants.getActualDay());
                    insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), y);
                    int newPercentage = (y.getCurrentProgress() * 100) / y.getTotal();
                    percentageText.setText(String.valueOf(newPercentage) + "%");
                    progressBar.setProgress(newPercentage);
                });
                AppCompatButton lessButton = popupView.findViewById(R.id.less_progress);
                lessButton.setOnClickListener(v -> {
                    y.setCurrentProgress(y.getCurrentProgress() - 1);
                    if (y.getCurrentProgress() < 0) {
                        y.setCurrentProgress(0);
                    }
                    insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), y);
                    int newPercentage = (y.getCurrentProgress() * 100) / y.getTotal();
                    percentageText.setText(String.valueOf(newPercentage) + "%");
                    progressBar.setProgress(newPercentage);
                });
                dialog.show();
            }
        })));
    }

    public void createOnLongClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Constants.getGameProgressList().forEach(y ->
                {
                    if (x.getText().equals(y.getName())) {
                        View popupView = createPopUp(R.layout.popup_progress_game);
                        TextView nameText = popupView.findViewById(R.id.name_text);
                        nameText.setText(y.getName());
                        TextView sagaText = popupView.findViewById(R.id.saga_text);
                        sagaText.setText(y.getSaga());
                        TextView dataText = popupView.findViewById(R.id.data_edit_text);
                        dataText.setText(y.getStartDate());
                        TextView actualText = popupView.findViewById(R.id.actual_edit_text);
                        actualText.setText(String.valueOf(y.getCurrentProgress()));
                        TextView totalText = popupView.findViewById(R.id.total_edit_text);
                        totalText.setText(String.valueOf(y.getTotal()));
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
                        int consolePosition = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(y.getSaga());
                        consoleSpinner.setSelection(consolePosition);
                        Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
                        int priorityPosition = Arrays.stream(context.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(y.getPriority());
                        prioritySpinner.setSelection(priorityPosition);
                        CheckBox buyedCheckbox = popupView.findViewById(R.id.buyed_checkbox);
                        buyedCheckbox.setChecked(y.getBuyed() != null ? y.getBuyed() : false);
                        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
                        transitCheckbox.setChecked(y.getCheckInTransit() != null ? y.getCheckInTransit() : false);
                        ImageButton updateButton = popupView.findViewById(R.id.update_button);
                        updateButton.setOnClickListener(w ->

                        {
                            ProgressGame progressGame = new ProgressGame();
                            progressGame.setName(nameText.getText().toString());
                            progressGame.setSaga(sagaText.getText().toString());
                            progressGame.setPriority(prioritySpinner.getSelectedItem().toString());
                            progressGame.setPlatform(consoleSpinner.getSelectedItem().toString());
                            progressGame.setBuyed(buyedCheckbox.isChecked());
                            progressGame.setCheckInTransit(transitCheckbox.isChecked());
                            progressGame.setTotal(Integer.parseInt(totalText.getText().toString()));
                            progressGame.setCurrentProgress(Integer.parseInt(actualText.getText().toString()));
                            progressGame.setStartDate(dataText.getText().toString());
                            insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), x.getTag().toString(), progressGame);
                            fillGameButton(rootView);

                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                dialog.dismiss();
                            }, 1000);

                        });
                        dialog.show();
                        dialog.setCancelable(false);
                    }
                });
                return true;
            }
        }));
    }

    public void addCompleteGame(ProgressGame progressGame) {

        View popupView;
        if (progressGame.getBuyed()) {
            popupView = createPopUp(R.layout.popup_database_game);
        } else {
            popupView = createPopUp(R.layout.popup_buy_game);
        }

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        nameText.setText(progressGame.getName());
        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        sagaText.setText(progressGame.getSaga());
        Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
        int consolePosition = Arrays.stream(this.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(progressGame.getPlatform());
        consoleSpinner.setSelection(consolePosition);
        if (progressGame.getBuyed()) {
            CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
            finishedCheckbox.setChecked(true);
            ImageButton addButton = popupView.findViewById(R.id.add_button);
            addButton.setOnClickListener(x -> {
                DatabaseGame databaseGame = new DatabaseGame();
                databaseGame.setName(nameText.getText().toString());
                databaseGame.setSaga(sagaText.getText().toString());
                databaseGame.setPlatform(consoleSpinner.getSelectedItem().toString());
                databaseGame.setFinished(Boolean.TRUE);
                databaseGame.setBuyed(Boolean.TRUE);
                DatabaseActivity.insertOtherGameInDatabaseDBAndCode(null, databaseGame);
                finish();
                Intent intent = new Intent(this, DatabaseActivity.class);
                startActivity(intent);
            });
        } else {
            Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
            int priorityPosition = Arrays.stream(this.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(progressGame.getPriority());
            prioritySpinner.setSelection(priorityPosition);
            CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
            transitCheckbox.setChecked(progressGame.getCheckInTransit());
            ImageButton addButton = popupView.findViewById(R.id.add_button);
            addButton.setOnClickListener(y -> {
                BuyGame buyGame = new BuyGame();
                buyGame.setName(nameText.getText().toString());
                buyGame.setSaga(sagaText.getText().toString());
                buyGame.setPlatform(consoleSpinner.getSelectedItem().toString());
                buyGame.setPriority(prioritySpinner.getSelectedItem().toString());
                buyGame.setCheckInTransit(transitCheckbox.isChecked());
                BuyActivity.insertOtherGameInDatabaseDBAndCode(null, buyGame);
                finish();
                Intent intent = new Intent(this, BuyActivity.class);
                startActivity(intent);
            });
        }
        dialog.show();
    }

    public static void fillGameButton(View rView) {
        List<ProgressGame> progressGameList = new ArrayList<>();
        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("id", "id", Constants.getActualDayCode());
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            AppCompatButton dayButton = rView.findViewById(R.id.day_button);
            dayButton.setText(scheduleGameList.get(0).getDay());

            HashMap<String, ProgressGame> gameMap = scheduleGameList.get(0).getPositionAndGame();
            for (String key : gameMap.keySet()) {
                progressGameList.add(gameMap.get(key));
            }
            Constants.setGameProgressList(progressGameList);

            Constants.getGameButtonList().forEach(x -> {

                if (gameMap.containsKey(x.getTag())) {
                    x.setText(gameMap.get(x.getTag()).getName());
                }
            });
        }, 100);
    }

    public void fillTimeButton() {
        List<TimeGame> timeGameList = Queries.selectTimeDB("id");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.getTimeButtonList().forEach(x -> {
                x.setText(timeGameList.get(Integer.valueOf(x.getTag().toString())).getHour());
            });
        }, 100);
    }

    public void setGlobalVariables() {
        List<AppCompatButton> timeButtonList = new ArrayList<>();
        RelativeLayout timeRelativeLayout = rootView.findViewById(R.id.time_relative_layout);
        for (int i = 0; i < timeRelativeLayout.getChildCount(); i++) {
            AppCompatButton timeButton = (AppCompatButton) timeRelativeLayout.getChildAt(i);
            timeButtonList.add(timeButton);
        }
        Constants.setTimeButtonList(timeButtonList);

        List<AppCompatButton> gameButtonList = new ArrayList<>();
        RelativeLayout gameRelativeLayout = rootView.findViewById(R.id.game_relative_layout);
        for (int i = 0; i < gameRelativeLayout.getChildCount(); i++) {
            AppCompatButton gameButton = (AppCompatButton) gameRelativeLayout.getChildAt(i);
            gameButtonList.add(gameButton);
        }
        Constants.setGameButtonList(gameButtonList);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        context = this;
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        getSupportActionBar().hide();
        onSwipeTouchListener = new OnSwipeTouchListener(this, rootView);
        Date date = new Date();
        String dayCode = date.toString().toLowerCase(Locale.ROOT).substring(0, 3);
        Constants.setActualDayCode(dayCode);
        Constants.setActualDay(retrieveActualDay(dayCode));
        setGlobalVariables();
        fillTimeButton();
        fillGameButton(rootView);
        createOnClickListener();
        createOnLongClickListener();
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
        hp.put("ID0", pg0);
        hp.put("ID1", pg1);
        hp.put("ID2", pg2);
        hp.put("ID3", pg3);
        hp.put("ID4", pg4);
        hp.put("ID5", pg5);
        hp.put("ID6", pg6);
        hp.put("ID7", pg7);
        hp.put("ID8", pg8);
        insertSchedule("Lunedi", "mon", hp);
        insertSchedule("Martedi", "tue", hp);
        insertSchedule("Mercoledi", "wed", hp);
        insertSchedule("Giovedi", "thu", hp);
        insertSchedule("Venerdi", "fri", hp);
        insertSchedule("Sabato", "sat", hp);
        insertSchedule("Domenica", "sun", hp);
        insertTimeGameInTimeDB("0", "11:00");
        insertTimeGameInTimeDB("1", "12:00");
        insertTimeGameInTimeDB("2", "14:00");
        insertTimeGameInTimeDB("3", "15:00");
        insertTimeGameInTimeDB("4", "16:00");
        insertTimeGameInTimeDB("5", "22:00");
        insertTimeGameInTimeDB("6", "23:00");
        insertTimeGameInTimeDB("7", "00:00");
        insertTimeGameInTimeDB("8", "01:00");
 */



