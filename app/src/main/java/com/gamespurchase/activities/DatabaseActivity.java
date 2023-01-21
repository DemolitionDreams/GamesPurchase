package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameDatabaseRecyclerAdapter;
import com.gamespurchase.adapter.GameSagaDatabaseRecyclerAdapter;
import com.gamespurchase.adapter.NothingSelectedSpinnerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatabaseActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter;
    GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter;

    // Richiama Queries.DELETE
    public static void removedItemFromDatabaseDBAndCode(SagheDatabaseGame sagheDatabaseGame) {
        Queries.deleteDatabaseDB(sagheDatabaseGame);

        Optional<SagheDatabaseGame> optGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(sagheDatabaseGame.getName())).findAny();
        if (optGame.isPresent()) {
            Constants.getGameSagheDatabaseList().remove(sagheDatabaseGame);
        }
    }

    public static void removedDatabaseGameFromSaga(SagheDatabaseGame optSagheGame, String name){
        Optional<DatabaseGame> optGameBuy = optSagheGame.getGamesBuy().stream().filter(x -> x.getName().equals(name)).findAny();
        if (optGameBuy.isPresent()) {
            Log.i("GamesPurchase", "Rimosso dagli acquistati" + name);
            optSagheGame.getGamesBuy().remove(optGameBuy.get());
        }
        Optional<DatabaseGame> optGameNotBuy = optSagheGame.getGamesNotBuy().stream().filter(x -> x.getName().equals(name)).findAny();
        if (optGameNotBuy.isPresent()) {
            Log.i("GamesPurchase", "Rimosso dai non acquistati" + name);
            optSagheGame.getGamesNotBuy().remove(optGameBuy.get());
        }
    }

    // Richiama Queries.INSERT
    public static void insertNewGameDatabaseDBAndCode(DatabaseGame databaseGame, String saga) {

        SagheDatabaseGame sagheDatabaseGame;
        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(saga)).findAny();
        if (optSagheGame.isPresent()) {
            removedDatabaseGameFromSaga(optSagheGame.get(), databaseGame.getName());
            optSagheGame.get().getGamesBuy().add(databaseGame);
            optSagheGame.get().getGamesBuy().stream()
                    .sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList());
            sagheDatabaseGame = optSagheGame.get();
            Log.i("GamesPurchase", "Aggiunto " + databaseGame.getName() + " alla saga " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati");
        } else {
            Constants.maxIdDatabaseList++;
            List<DatabaseGame> buyGames = new ArrayList<>();
            buyGames.add(databaseGame);
            List<DatabaseGame> notBuyGames = new ArrayList<>();
            sagheDatabaseGame = new SagheDatabaseGame(String.valueOf(Constants.getMaxIdDatabaseList()), saga, Boolean.TRUE, databaseGame.getFinished() ? Boolean.TRUE : Boolean.FALSE, buyGames, notBuyGames);
            Log.i("GamesPurchase", "Inserita nuova saga: " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") e aggiunto " + databaseGame.getName() + " alla lista buyGames");
        }

        Boolean[] check = checkBuyAllOrFinishAll(sagheDatabaseGame);
        sagheDatabaseGame.setBuyAll(check[0]);
        sagheDatabaseGame.setFinishAll(check[1]);

        Queries.insertUpdateDatabaseDB(sagheDatabaseGame);
        List<SagheDatabaseGame> sagheDatabaseGameList = Queries.selectDatabaseDB("name");
        Handler handler = new Handler();
        handler.postDelayed(() ->

        {
            Constants.setGameSagheDatabaseList(sagheDatabaseGameList);
            Constants.setCounterSagheDatabaseGame(sagheDatabaseGameList.size());
        }, 100);
    }

    public static void insertNewGameSagaDatabaseDBAndCode(SagheDatabaseGame sagheDatabaseGame) {
        Queries.insertUpdateDatabaseDB(sagheDatabaseGame);
        List<SagheDatabaseGame> sagheDatabaseGameList = Queries.selectDatabaseDB("name");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.setGameSagheDatabaseList(sagheDatabaseGameList);
            Constants.setCounterSagheDatabaseGame(sagheDatabaseGameList.size());
        }, 100);
    }

    public static Boolean[] checkBuyAllOrFinishAll(SagheDatabaseGame sagheDatabaseGame) {
        Boolean[] check = new Boolean[2];
        check[0] = sagheDatabaseGame.getGamesNotBuy().isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        check[1] = Boolean.TRUE;
        for (DatabaseGame dg : sagheDatabaseGame.getGamesBuy()) {
            if (!dg.getFinished()) {
                check[1] = Boolean.FALSE;
            }
        }
        for (DatabaseGame dg : sagheDatabaseGame.getGamesNotBuy()) {
            if (!dg.getFinished()) {
                check[1] = Boolean.FALSE;
            }
        }

        return check;
    }

    public void onClick(View view) {

        ImageView orderButton = findViewById(R.id.sort_button);
        orderButton.setVisibility(View.INVISIBLE);
        AppCompatButton returnButton = findViewById(R.id.return_button);
        returnButton.setVisibility(View.VISIBLE);
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setVisibility(View.INVISIBLE);
        returnButton.setLayoutParams(closeButton.getLayoutParams());
        findViewById(R.id.search_button).setLayoutParams(orderButton.getLayoutParams());
        TextInputLayout textInputLayout = findViewById(R.id.search_input);
        textInputLayout.setVisibility(View.VISIBLE);

        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<SagheDatabaseGame> filterList = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(textInputLayout.getEditText().getText().toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
                createRecyclerAdapter(filterList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_database);
        createRecyclerAdapter(Constants.getGameSagheDatabaseList());
        Constants.setSortDatabaseGame("DESC");
    }

    public void onClickClose(View view) {
        finish();
    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT) {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            SagheDatabaseGame sagheDatabaseGame = Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition());
            View popupView = createPopUp(R.layout.popup_delete_database_game);
            TextView textView = popupView.findViewById(R.id.edit_text);
            String newText = "Rimuovere " + Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition()).getName() + "?";
            textView.setText(newText);
            ImageButton removeButton = popupView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(view -> onClickOnlyRemove(Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
            ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
            Log.i("GamesPurchase", "VIEWHOLDEER " + viewHolder.getAdapterPosition());

            nullifyActionButton.setOnClickListener(view -> onClickPopupDismiss(viewHolder.getAdapterPosition(), sagheDatabaseGame));
            dialog.setCancelable(false);
            dialog.show();
        }
    });

    public void onClickPopupDismiss(int position, SagheDatabaseGame sagheDatabaseGame) {
        insertNewGameSagaDatabaseDBAndCode(sagheDatabaseGame);
        gameSagaDatabaseRecyclerAdapter.notifyDataSetChanged();
        gameSagaDatabaseRecyclerAdapter.notifyItemInserted(position);
        dialog.dismiss();
    }

    public void onClickOnlyRemove(SagheDatabaseGame sagheDatabaseGame, int position) {
        removedItemFromDatabaseDBAndCode(sagheDatabaseGame);
        gameSagaDatabaseRecyclerAdapter.notifyItemRemoved(position);
        dialog.dismiss();
    }

    private String checkIfEditTextIsNull(AutoCompleteTextView textInputEditText) {
        if (textInputEditText != null && textInputEditText.getText() != null) {
            return textInputEditText.getText().toString();
        } else {

            return "";
        }
    }

    private String checkIfSpinnerIsNull(Spinner spinner) {

        if (spinner != null && spinner.getSelectedItem() != null) {
            return spinner.getSelectedItem().toString();
        } else {
            return "";
        }
    }

    public void onClickAddDatabaseGame(View popupView) {

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        String name = checkIfEditTextIsNull(nameText);
        if (name.isEmpty()) {
            nameText.setError(getResources().getString(R.string.notEmpty));
        }

        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        String saga = checkIfEditTextIsNull(sagaText);
        if (saga.isEmpty()) {
            sagaText.setError(getResources().getString(R.string.notEmpty));
        }

        Spinner platformSpinner = popupView.findViewById(R.id.console_spinner);
        String platform = checkIfSpinnerIsNull(platformSpinner);
        if (platform.isEmpty()) {
            ((TextView) platformSpinner.getSelectedView()).setError(getResources().getString(R.string.notNotSelected));
        }

        Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
        String priority = checkIfSpinnerIsNull(prioritySpinner);
        if (priority.isEmpty()) {
            ((TextView) prioritySpinner.getSelectedView()).setError(getResources().getString(R.string.notNotSelected));
        }

        CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

        if (!name.isEmpty() && !saga.isEmpty() && !platform.isEmpty() && !priority.isEmpty()) {
            DatabaseGame databaseGame = new DatabaseGame(name, platform, priority, finishedCheckbox.isChecked(), transitCheckbox.isChecked());
            insertNewGameDatabaseDBAndCode(databaseGame, saga);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList().stream()
                        .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
                createRecyclerAdapter(Constants.getGameSagheDatabaseList());
            }, 100);
            dialog.dismiss();
        }
    }

    public void onClickOpenAddDatabaseGamePopup(View view) {

        View popupView = createPopUp(R.layout.popup_database_game);
        addAutoCompleteVoice(popupView, R.id.saga_text);
        setEntriesAndDefaultToSpinner(R.array.Console, R.layout.console_spinner_default_value, R.id.console_spinner, popupView);
        setEntriesAndDefaultToSpinner(R.array.Priority, R.layout.priority_spinner_default_value, R.id.priority_spinner, popupView);
        popupView.findViewById(R.id.add_button).setOnClickListener(v -> onClickAddDatabaseGame(popupView));
        dialog.show();
    }

    // Aggiunge allo spinner le possibly scelte e il valore di default
    private void setEntriesAndDefaultToSpinner(int arrayString, int spinnerLayout, int spinner, View view) {
        List<String> itemList = Arrays.stream(getResources().getStringArray(arrayString)).collect(Collectors.toList());
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, spinnerLayout, itemList);
        Spinner consoleSpinner = view.findViewById(spinner);
        consoleSpinner.setAdapter(spinnerAdapter);
        consoleSpinner.setAdapter(new NothingSelectedSpinnerAdapter(spinnerAdapter, spinnerLayout, this));
    }

    // Creazione Pop Up
    private View createPopUp(int id) {
        dialog = new Dialog(DatabaseActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private void setSortOrientation(ImageButton sortButton, String oldOrientation, String newOrientation, int id) {
        Constants.sortDatabaseGame = newOrientation;
        List<SagheDatabaseGame> sagheDatabaseGameList = Constants.getGameSagheDatabaseList();
        if (oldOrientation.equals("ASC")) {
            sagheDatabaseGameList = sagheDatabaseGameList.stream()
                    .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList());
        } else {
            sagheDatabaseGameList = sagheDatabaseGameList.stream()
                    .sorted(Comparator.comparing(SagheDatabaseGame::getName).reversed()).collect(Collectors.toList());
        }
        Constants.setGameSagheDatabaseList(sagheDatabaseGameList);
        gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList());
        createRecyclerAdapter(Constants.getGameSagheDatabaseList());
        sortButton.setImageResource(id);
    }

    /*
    private List<DatabaseGame> applyFilter(View view) {
        AutoCompleteTextView sagaFilter = view.findViewById(R.id.saga_filter);

        Spinner consoleFilter = view.findViewById(R.id.console_filter);
        CheckBox finishedFilter = view.findViewById(R.id.finished_filter);
        CheckBox notFinishedFilter = view.findViewById(R.id.not_finished_filter);

        List<DatabaseGame> filterList = Constants.getGameDatabaseList();
        if (sagaFilter.getText() != null) {
            filterList = filterList.stream().filter(g -> g.getSaga().contains(sagaFilter.getText())).collect(Collectors.toList());
        }
        if (consoleFilter.getSelectedItem() != null) {
            filterList = filterList.stream().filter(g -> g.getPlatform().equals(consoleFilter.getSelectedItem().toString())).collect(Collectors.toList());
        }
        if (finishedFilter.isChecked() && !notFinishedFilter.isChecked()) {
            filterList = filterList.stream().filter(g -> g.getFinished().equals(true)).collect(Collectors.toList());
        } else if (notFinishedFilter.isChecked() && !finishedFilter.isChecked()) {
            filterList = filterList.stream().filter(g -> g.getFinished().equals(false)).collect(Collectors.toList());
        }
        createRecyclerAdapter(filterList);
        dialog.dismiss();

        return filterList;
    }
     */

    public void addAutoCompleteVoice(View view, int id) {
        AutoCompleteTextView sagaAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        Constants.getGameSagheDatabaseList().forEach(g -> {
            if (!autoCompleteVoice.contains(g.getName())) {
                autoCompleteVoice.add(g.getName());
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteVoice);
        sagaAutoComplete.setAdapter(arrayAdapter);
    }

    private void setFilterButton() {
        ImageButton sortButton = findViewById(R.id.sort_button);
        if (Constants.sortDatabaseGame.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }

        sortButton.setOnClickListener(view -> {
            if (Constants.sortDatabaseGame.equals("ASC")) {
                setSortOrientation(sortButton, "ASC", "DESC", R.drawable.icon_sort_desc);
            } else if (Constants.sortDatabaseGame.equals("DESC")) {
                setSortOrientation(sortButton, "DESC", "ASC", R.drawable.icon_sort_asc);
            }
        });
    }
    // TODO: implementare counter
    /*
    public static void setCounter(View view, List<BuyGame> buyGameList, List<DatabaseGame> databaseGameList) {

        count(buyGameList, databaseGameList);

        Button totalCounter = view.findViewById(R.id.total_counter_button);
        Button finishCounter = view.findViewById(R.id.finish_counter_button);
        Button digitalCounter = view.findViewById(R.id.digital_counter_button);
        totalCounter.setText("Totali:\n");
        finishCounter.setText("Finiti:\n");
        digitalCounter.setText("Digitali:\n");

        totalCounter.setText(totalCounter.getText() + Constants.getCounterDatabaseGame().get("Totale").toString());
        finishCounter.setText(finishCounter.getText() + Constants.getCounterDatabaseGame().get("Finiti").toString());
        digitalCounter.setText(digitalCounter.getText() + Constants.getCounterDatabaseGame().get("Digitali").toString());

    }*/
/*
    public void onClickPopupInfoShow(View view) {
        View popupView = createPopUp(R.layout.popup_info_saghe);
        TextView sagheTextView = popupView.findViewById(R.id.saghe_text_view);
        sagheTextView.setText(filterBySaga());
        dialog.show();
    }
    */

/*    private String filterBySaga() {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, Integer> mapSaga = new TreeMap<>();
        gameDatabaseRecyclerAdapter.getGameDatabaseList().forEach(x -> {
            if (!mapSaga.containsKey(x.getSaga())) {
                mapSaga.put(x.getSaga(), 1);
            } else {
                mapSaga.put(x.getSaga(), mapSaga.get(x.getSaga()) + 1);
            }
        });
        int counter = 0;
        for (String s : mapSaga.keySet()) {
            sb.append(s).append(" - ").append(mapSaga.get(s));
            if (counter != mapSaga.size() - 1) {
                sb.append(System.getProperty("line.separator"));
            }
            counter++;
        }
        return sb.toString();
    }
*/

    private void createRecyclerAdapter(List<SagheDatabaseGame> sagheDatabaseGameList) {
        RecyclerView recyclerView = findViewById(R.id.game_saghe_database);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameSagaDatabaseRecyclerAdapter = new GameSagaDatabaseRecyclerAdapter(sagheDatabaseGameList, this, null, new DatabaseActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameSagaDatabaseRecyclerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        createRecyclerAdapter(Constants.getGameSagheDatabaseList().stream()
                .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
        setFilterButton();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}