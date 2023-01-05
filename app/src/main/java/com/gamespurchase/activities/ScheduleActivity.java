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
import java.util.Objects;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    OnSwipeTouchListener onSwipeTouchListener;

    public void insertTimeGameInTimeDB(String id, String hour) {

        TimeGame timeGame = new TimeGame();
        timeGame.setId(id);
        timeGame.setHour(hour);
        Queries.insertUpdateTimeDB(timeGame);
    }

    public void insertScheduleGameInScheduleDBAndCode(String day, String position, ProgressGame progressGame) {

        List<ScheduleGame> scheduleGameList = Queries.filterScheduleDB("day", "day", day);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!scheduleGameList.isEmpty()) {
                scheduleGameList.get(0).getPositionAndGame().put(position, progressGame);
                ScheduleGame scheduleGame = new ScheduleGame(scheduleGameList.get(0).getId(), day, scheduleGameList.get(0).getPositionAndGame());
                Queries.insertUpdateScheduleDB(scheduleGame);
            }
        }, 100);
    }

    public void inserTo(String day, String id, HashMap<String, ProgressGame> h) {

        ScheduleGame sc = new ScheduleGame();
        sc.setId(id);
        sc.setDay(day);
        sc.setPositionAndGame(h);
        Queries.insertUpdateScheduleDB(sc);
    }

    private View createPopUp(int id, Dialog dialog) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private static String changeDay(List<String> dayList, String actualDay, Boolean sum) {

        int oldIndex = Constants.getDayCodeList().indexOf(actualDay);
        String newDay;
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

    //TODO
    public void createOnClickListenerInTimeButton() {
        Constants.getTimeButtonList().forEach(x -> x.setOnLongClickListener(v -> {
            View popupView = createPopUp(R.layout.popup_progress_game, dialog);
            return false;
        }));
    }

    public void createOnClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnClickListener(view -> Constants.getGameProgressList().forEach(y ->
        {
            if (y.getTotal() == 0) {
                y.setTotal(1);
            }
            int percentage = (y.getCurrentProgress() * 100) / y.getTotal();
            if (x.getText().equals(y.getName())) {
                View popupView = createPopUp(R.layout.popup_info_progress_game, dialog);
                TextView nameText = popupView.findViewById(R.id.name_text);
                nameText.setText(y.getName());
                ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
                progressBar.setProgress(percentage);
                TextView percentageText = popupView.findViewById(R.id.percentage_text_view);
                String percentageWithSymbol = percentage + "%";
                percentageText.setText(percentageWithSymbol);
                TextView dataText = popupView.findViewById(R.id.data_text_view);
                dataText.setText(y.getStartDate());

                AppCompatButton moreButton = popupView.findViewById(R.id.more_progress);
                moreButton.setOnClickListener(v -> changeProgress(Boolean.TRUE, y, x, percentageText, progressBar));

                AppCompatButton lessButton = popupView.findViewById(R.id.less_progress);
                lessButton.setOnClickListener(v -> changeProgress(Boolean.FALSE, y, x, percentageText, progressBar));
                dialog.show();
            }
        })));
    }

    public void changeProgress(Boolean sum, ProgressGame pg, AppCompatButton button, TextView percentageText, ProgressBar progressBar) {
        if (sum) {
            pg.setCurrentProgress(pg.getCurrentProgress() + 1);
            if (pg.getCurrentProgress() == pg.getTotal()) {
                addCompleteGame(pg, button.getTag().toString());
                Handler handler = new Handler();
                handler.postDelayed(() -> button.setText("TBD"), 100);
            }
        } else {
            pg.setCurrentProgress((pg.getCurrentProgress()) <= 0 ? 0 : pg.getCurrentProgress() - 1);
        }
        insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), button.getTag().toString(), pg);
        updateAllProgressGame(pg, pg.getName());
        int newPercentage = (pg.getCurrentProgress() * 100) / pg.getTotal();
        String newPercentageWithSymbol = newPercentage + "%";
        percentageText.setText(newPercentageWithSymbol);
        progressBar.setProgress(newPercentage);
    }

    public void createOnLongClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnLongClickListener(v -> {
            Constants.getGameButtonList().forEach(y -> {
                if (x.getText().equals(y.getText())) {
                    Dialog dialog = new Dialog(this);
                    ProgressGame progressGame = Constants.getGameProgressList().get(Integer.parseInt(y.getTag().toString().substring(y.getTag().toString().length() - 1)));
                    View popupView = createPopUp(R.layout.popup_progress_game, dialog);
                    TextView nameText = popupView.findViewById(R.id.name_text);
                    nameText.setText(progressGame.getName());
                    String oldName = progressGame.getName();
                    TextView sagaText = popupView.findViewById(R.id.saga_text);
                    sagaText.setText(progressGame.getSaga());
                    TextView dataText = popupView.findViewById(R.id.data_edit_text);
                    dataText.setText(progressGame.getStartDate());
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
                        ProgressGame newProgressGame = new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), 0, dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), "", buyedCheckbox.isChecked(), transitCheckbox.isChecked());
                        insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), y.getTag().toString(), newProgressGame);
                        updateAllProgressGame(newProgressGame, oldName);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> fillGameButton(rootView), 100);
                        dialog.dismiss();
                    });
                    ImageButton resetButton = popupView.findViewById(R.id.reset_button);
                    resetButton.setOnClickListener(w ->
                    {
                        ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", Boolean.FALSE, Boolean.FALSE);
                        insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), y.getTag().toString(), newProgressGame);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> fillGameButton(rootView), 100);
                        dialog.dismiss();
                    });
                    dialog.show();
                    //dialog.setCancelable(false);
                }
            });
            return true;
        }));
    }

    public void insertOtherGameAndChangeActivity(Boolean buyed, String tag, String oldName) {
        finish();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", buyed, Boolean.FALSE);
            insertScheduleGameInScheduleDBAndCode(Constants.getActualDay(), tag, newProgressGame);
            updateAllProgressGame(newProgressGame, oldName);
        }, 100);

        Intent intent = new Intent(this, buyed ? DatabaseActivity.class : BuyActivity.class);
        startActivity(intent);
    }

    public void addCompleteGame(ProgressGame progressGame, String tag) {

        View popupView = createPopUp(progressGame.getBuyed() ? R.layout.popup_database_game : R.layout.popup_buy_game, dialog);

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        nameText.setText(progressGame.getName());
        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        sagaText.setText(progressGame.getSaga());
        Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
        int consolePosition = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(progressGame.getPlatform());
        consoleSpinner.setSelection(consolePosition);
        Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
        ImageButton addButton = popupView.findViewById(R.id.add_button);
        if (progressGame.getBuyed()) {
            CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
            finishedCheckbox.setChecked(Boolean.TRUE);
            addButton.setOnClickListener(x -> {
                DatabaseGame databaseGame = new DatabaseGame(nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), Boolean.TRUE, Boolean.TRUE);
                DatabaseActivity.insertOtherGameInDatabaseDBAndCode(null, databaseGame);
                insertOtherGameAndChangeActivity(progressGame.getBuyed(), tag, databaseGame.getName());
            });
        } else {
            int priorityPosition = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(progressGame.getPriority());
            prioritySpinner.setSelection(priorityPosition);
            CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
            transitCheckbox.setChecked(progressGame.getCheckInTransit());
            addButton.setOnClickListener(y -> {
                BuyGame buyGame = new BuyGame(nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), transitCheckbox.isChecked());
                BuyActivity.insertOtherGameInDatabaseDBAndCode(null, buyGame);
                insertOtherGameAndChangeActivity(progressGame.getBuyed(), tag, buyGame.getName());
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
            for (int i = 0; i < 9; i++) {
                String key = "ID".concat(String.valueOf(i));
                progressGameList.add(gameMap.get(key));
            }
            Constants.setGameProgressList(progressGameList);
            Constants.getGameButtonList().forEach(x -> {
                if (gameMap.containsKey(x.getTag())) {
                    x.setText(Objects.requireNonNull(gameMap.get(x.getTag())).getName());
                }
            });
            for (ProgressGame pg : Constants.getGameProgressList()) {
                Log.i("GamesPurchase", "ProgressGame Nome: " + pg.getName() + " e Saga: " + pg.getSaga());
            }
        }, 100);
    }

    public void fillTimeButton() {
        List<TimeGame> timeGameList = Queries.selectTimeDB("id");
        Handler handler = new Handler();
        handler.postDelayed(() -> Constants.getTimeButtonList().forEach(x -> x.setText(timeGameList.get(Integer.parseInt(x.getTag().toString())).getHour())), 100);
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

    public void updateAllProgressGame(ProgressGame progressGame, String oldName) {
        List<ScheduleGame> scheduleGameList = Queries.selectScheduleDB("id");

        Handler handler = new Handler();
        handler.postDelayed(() -> scheduleGameList.forEach(x -> {
            if (!oldName.equals("TBD")) {
                x.getPositionAndGame().keySet().forEach(y -> {
                    if (x.getPositionAndGame().get(y).getName().equals(oldName)) {
                        x.getPositionAndGame().put(y, progressGame);
                    }
                });
                inserTo(x.getDay(), x.getId(), x.getPositionAndGame());
            }
        }), 100);
    }

    public void onClicked(View v) {

        ProgressGame pg0 = new ProgressGame();
        pg0.setName("The Tarnishing of Juxtia");
        pg0.setSaga("Other");
        pg0.setStartDate("5 Agosto 2022");
        pg0.setCurrentProgress(1);
        pg0.setTotal(18);
        pg0.setPlatform("Digital");
        pg0.setPriority("HIGH");
        pg0.setBuyed(Boolean.FALSE);
        pg0.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg1 = new ProgressGame();
        pg1.setName("The Tarnishing of Juxtia");
        pg1.setSaga("Other");
        pg1.setStartDate("5 Agosto 2022");
        pg1.setCurrentProgress(1);
        pg1.setTotal(18);
        pg1.setPlatform("Digital");
        pg1.setPriority("HIGH");
        pg1.setBuyed(Boolean.FALSE);
        pg1.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg2 = new ProgressGame();
        pg2.setName("Mario & Luigi: Superstar Saga");
        pg2.setSaga("Super Mario");
        pg2.setStartDate("20 Ottobre 2022");
        pg2.setCurrentProgress(13);
        pg2.setTotal(28);
        pg2.setPlatform("3DS");
        pg2.setPriority("HIGH");
        pg2.setBuyed(Boolean.FALSE);
        pg2.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg3 = new ProgressGame();
        pg3.setName("Journey");
        pg3.setSaga("Other");
        pg3.setStartDate("TBS");
        pg3.setCurrentProgress(0);
        pg3.setTotal(8);
        pg3.setPlatform("Digital");
        pg3.setPriority("HIGH");
        pg3.setBuyed(Boolean.FALSE);
        pg3.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg4 = new ProgressGame();
        pg4.setName("Journey");
        pg4.setSaga("Other");
        pg4.setStartDate("TBS");
        pg4.setCurrentProgress(0);
        pg4.setTotal(8);
        pg4.setPlatform("Digital");
        pg4.setPriority("HIGH");
        pg4.setBuyed(Boolean.FALSE);
        pg4.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg5 = new ProgressGame();
        pg5.setName("Ratchet: Gladiator");
        pg5.setSaga("Ratchet & Clank");
        pg5.setStartDate("TBS");
        pg5.setCurrentProgress(0);
        pg5.setTotal(1);
        pg5.setPlatform("PS2");
        pg5.setPriority("HIGH");
        pg5.setBuyed(Boolean.TRUE);
        pg5.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg6 = new ProgressGame();
        pg6.setName("Ratchet: Gladiator");
        pg6.setSaga("Ratchet & Clank");
        pg6.setStartDate("TBS");
        pg6.setCurrentProgress(0);
        pg6.setTotal(1);
        pg6.setPlatform("PS2");
        pg6.setPriority("HIGH");
        pg6.setBuyed(Boolean.TRUE);
        pg6.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg7 = new ProgressGame();
        pg7.setName("Super Mario 3D - World");
        pg7.setSaga("Super Mario");
        pg7.setStartDate("4 Aprile 2022");
        pg7.setCurrentProgress(6);
        pg7.setTotal(12);
        pg7.setPlatform("Switch");
        pg7.setPriority("HIGH");
        pg7.setBuyed(Boolean.FALSE);
        pg7.setCheckInTransit(Boolean.FALSE);
        ProgressGame pg8 = new ProgressGame();
        pg8.setName("Super Mario 3D - World");
        pg8.setSaga("Super Mario");
        pg8.setStartDate("4 Aprile 2022");
        pg8.setCurrentProgress(6);
        pg8.setTotal(12);
        pg8.setPlatform("Switch");
        pg8.setPriority("HIGH");
        pg8.setBuyed(Boolean.FALSE);
        pg8.setCheckInTransit(Boolean.FALSE);

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

        inserTo("Domenica", "sun", hp);
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
        setGlobalVariables();
        fillTimeButton();
        fillGameButton(rootView);
        createOnClickListener();
        createOnLongClickListener();
    }
}



