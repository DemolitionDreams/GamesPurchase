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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    public void insertProgressGameInScheduleDBAndCode(String day, String position, ProgressGame progressGame) {

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

    public void insertScheduleGameInScheduleDBAndCode(String day, String id, HashMap<String, ProgressGame> positionAndGame) {

        ScheduleGame sc = new ScheduleGame();
        sc.setId(id);
        sc.setDay(day);
        sc.setPositionAndGame(positionAndGame);
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

    public void changeProgress(Boolean sum, ProgressGame pg, AppCompatButton button, TextView currentText, TextView totalText, TextView percentageText, ProgressBar progressBar) {
        if (sum) {
            pg.setCurrentProgress(pg.getCurrentProgress() + 1);
            if (pg.getCurrentProgress() == pg.getTotal()) {
                addCompleteGame(pg, button.getTag().toString());
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", Boolean.FALSE, Boolean.FALSE);
                    insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), button.getTag().toString(), newProgressGame);
                }, 100);
            }
        } else {
            pg.setCurrentProgress((pg.getCurrentProgress()) <= 0 ? 0 : pg.getCurrentProgress() - 1);
        }
        insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), button.getTag().toString(), pg);
        updateAllProgressGame(pg, pg.getName());
        currentText.setText(String.valueOf(pg.getCurrentProgress()));
        totalText.setText(String.valueOf(pg.getTotal()));
        int newPercentage = (pg.getCurrentProgress() * 100) / pg.getTotal();
        String newPercentageWithSymbol = newPercentage + "%";
        percentageText.setText(newPercentageWithSymbol);
        progressBar.setProgress(newPercentage);
    }

    public void addAutoCompleteVoice(View view, int id) {
        AutoCompleteTextView nameAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        Constants.getTotalGameStartList().forEach(g -> {
            if (!autoCompleteVoice.contains(g.getName())) {
                autoCompleteVoice.add(g.getName());
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteVoice);
        nameAutoComplete.setAdapter(arrayAdapter);
    }

    public void addAutoCompleteSagaVoice(View view, int id) {
        AutoCompleteTextView nameAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        Constants.getTotalGameStartList().forEach(g -> {
            if (!autoCompleteVoice.contains(g.getSaga())) {
                autoCompleteVoice.add(g.getSaga());
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteVoice);
        nameAutoComplete.setAdapter(arrayAdapter);
    }

    public void createOnLongClickListener() {
        Constants.getGameButtonList().forEach(x -> x.setOnLongClickListener(v -> {
            Constants.getGameButtonList().forEach(y -> {
                if (x.getText().equals(y.getText())) {
                    Dialog dialog = new Dialog(this);
                    ProgressGame progressGame = Constants.getGameProgressList().get(Integer.parseInt(y.getTag().toString().substring(y.getTag().toString().length() - 1)));
                    View popupView = createPopUp(R.layout.popup_progress_game, dialog);
                    AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
                    nameText.setText(progressGame.getName());
                    addAutoCompleteVoice(popupView, R.id.name_text);
                    String oldName = progressGame.getName();
                    TextView sagaText = popupView.findViewById(R.id.saga_text);
                    sagaText.setText(progressGame.getSaga());
                    addAutoCompleteSagaVoice(popupView, R.id.saga_text);
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
                        insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), y.getTag().toString(), newProgressGame);
                        List<ProgressGame> progressGameList = Queries.filterStartDB("id", "name", newProgressGame.getName());
                        updateAllProgressGame(newProgressGame, oldName);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            fillGameButton(rootView);
                            try {
                                newProgressGame.setId(progressGameList.get(0).getId());
                                Queries.deleteStartDB(newProgressGame);
                            } catch (Exception e) {
                                Log.i("GamesPurchase", "Elemento non presente");
                            }
                        }, 100);
                        dialog.dismiss();
                    });
                    ImageButton resetButton = popupView.findViewById(R.id.reset_button);
                    resetButton.setOnClickListener(w ->
                    {
                        // TODO: Inserimento StartDB del precedente ProgressGame
                        ProgressGame oldProgressGame = new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), 0, dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), "", buyedCheckbox.isChecked(), transitCheckbox.isChecked());
                        oldProgressGame.setId(String.valueOf(Constants.getMaxIdStartList() + 1));
                        oldProgressGame.setLabel("Still");
                        Queries.insertUpdateStartDB(oldProgressGame);
                        ProgressGame newProgressGame = new ProgressGame(0, 1, 0, "TBS", "TBD", "", "Digital", "LOW", "", Boolean.FALSE, Boolean.FALSE);
                        insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), y.getTag().toString(), newProgressGame);
                        Handler handler = new Handler();
                        handler.postDelayed(() -> fillGameButton(rootView), 100);
                        dialog.dismiss();
                    });
                    nameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            List<ProgressGame> pgList = Queries.filterStartDB("id", "name", nameText.getText().toString());
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                if (!pgList.isEmpty()) {
                                    nameText.setText(pgList.get(0).getName());
                                    sagaText.setText(pgList.get(0).getSaga());
                                    dataText.setText(pgList.get(0).getStartDate());
                                    actualText.setText(String.valueOf(pgList.get(0).getCurrentProgress()));
                                    totalText.setText(String.valueOf(pgList.get(0).getTotal()));
                                    int consolePosition = Arrays.stream(getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(pgList.get(0).getPlatform());
                                    consoleSpinner.setSelection(consolePosition);
                                    int priorityPosition = Arrays.stream(getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(pgList.get(0).getPriority());
                                    prioritySpinner.setSelection(priorityPosition);
                                    buyedCheckbox.setChecked(pgList.get(0).getBuyed());
                                    transitCheckbox.setChecked(pgList.get(0).getCheckInTransit());
                                }
                            }, 100);
                        }
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
            insertProgressGameInScheduleDBAndCode(Constants.getActualDay(), tag, newProgressGame);
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
            dayButton.setText(scheduleGameList.isEmpty() ? "DB don't load" : scheduleGameList.get(0).getDay());
            if (!scheduleGameList.isEmpty()) {
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
            }
        }, 150);
    }

    public void fillTimeButton() {
        List<TimeGame> timeGameList = Queries.selectTimeDB("id");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!timeGameList.isEmpty()) {
                Constants.getTimeButtonList().forEach(x -> x.setText(timeGameList.get(Integer.parseInt(x.getTag().toString())).getHour()));
            }
        }, 150);
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
        List<ProgressGame> progressGameList = Queries.selectStartDB("name");

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.setTotalGameStartList(progressGameList);
        }, 100);
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
                        Log.i("GamesPurchase", "Modificato: " + progressGame.getName() + " " + progressGame.getCurrentProgress());
                    }
                });
                insertScheduleGameInScheduleDBAndCode(x.getDay(), x.getId(), x.getPositionAndGame());
            }
        }), 100);
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



