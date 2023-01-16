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

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatabaseActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter;
    GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter;

    // Richiama Queries.DELETE
    public void removedItemFromDatabaseDBAndCode(SagheDatabaseGame sagheDatabaseGame) {
        Queries.deleteDatabaseDB(sagheDatabaseGame);

        Optional<SagheDatabaseGame> optGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(sagheDatabaseGame.getName())).findAny();
        if (optGame.isPresent()) {
            Constants.getGameDatabaseList().remove(sagheDatabaseGame);
        }
    }

    // Richiama Queries.INSERT
    public static void insertNewGameDatabaseDBAndCode(DatabaseGame databaseGame, String saga) {

        SagheDatabaseGame sagheDatabaseGame;
        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(saga)).findAny();
        if (optSagheGame.isPresent()) {
            List<DatabaseGame> databaseGameList = optSagheGame.get().getGamesBuy();
            Optional<DatabaseGame> optGame = databaseGameList.stream().filter(x -> x.getName().equals(databaseGame.getName())).findAny();
            if (optGame.isPresent()) {
                Log.i("GamesPurchase", "Rimosso " + databaseGame.getName());
                databaseGameList.remove(optGame.get());
            }
            databaseGameList.add(databaseGame);
            databaseGameList.stream()
                    .sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList());
            sagheDatabaseGame = optSagheGame.get();
            Log.i("GamesPurchase", "Aggiunto " + databaseGame.getName() + " alla saga " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() +") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati");
        } else {
            Constants.maxIdDatabaseList++;
            List<DatabaseGame> buyGames = new ArrayList<>();
            buyGames.add(databaseGame);
            List<DatabaseGame> notBuyGames = new ArrayList<>();
            sagheDatabaseGame = new SagheDatabaseGame(String.valueOf(Constants.getMaxIdDatabaseList()), saga, Boolean.TRUE, databaseGame.getFinished() ? Boolean.TRUE : Boolean.FALSE, buyGames, notBuyGames);
            Log.i("GamesPurchase", "Inserita nuova saga: " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() +") e aggiunto " + databaseGame.getName() + " alla lista buyGames");
        }
        Queries.insertUpdateDatabaseDB(sagheDatabaseGame);
        List<SagheDatabaseGame> sagheDatabaseGameList = Queries.selectDatabaseDB("name");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.setGameSagheDatabaseList(sagheDatabaseGameList);
            Constants.setCounterSagheDatabaseGame(sagheDatabaseGameList.size());
        }, 100);
    }

    private void createRecyclerAdapter(List<SagheDatabaseGame> sagheDatabaseGameList) {
        RecyclerView recyclerView = findViewById(R.id.game_database);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameSagaDatabaseRecyclerAdapter = new GameSagaDatabaseRecyclerAdapter(sagheDatabaseGameList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameSagaDatabaseRecyclerAdapter);
        //TODO: aggiungerlo
        //itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onClick(View view) {

        ImageView orderButton = findViewById(R.id.sort_button);
        orderButton.setVisibility(View.INVISIBLE);
        ImageView returnButton = findViewById(R.id.return_button);
        returnButton.setVisibility(View.VISIBLE);
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setVisibility(View.INVISIBLE);
        ImageView filterButton = findViewById(R.id.filter_button);
        filterButton.setVisibility(View.INVISIBLE);
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
                // TODO: filtrare per il search
                //List<DatabaseGame> filterList = filterFromDatabaseDBOrCode(null, null, null, textInputLayout.getEditText().getText().toString(), "CODE");
                //createRecyclerAdapter(filterList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_database);
        gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList());
    }

    public void onClickClose(View view) {
        finish();
    }

    /*
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
            DatabaseGame databaseGame = Constants.getGameDatabaseList().get(viewHolder.getAdapterPosition());
            View popupView = createPopUp(R.layout.popup_delete_database_game);
            TextView textView = popupView.findViewById(R.id.edit_text);
            textView.setText("Rimuovere " + Constants.getGameDatabaseList().get(viewHolder.getAdapterPosition()).getName() + "?");
            ImageButton removeButton = popupView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(view -> onClickOnlyRemove(Constants.getGameDatabaseList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
            ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
            nullifyActionButton.setOnClickListener(view -> onClickPopupDismiss(viewHolder.getAdapterPosition(), databaseGame, rootView));
            dialog.setCancelable(false);
            dialog.show();
        }
    });
*/
    //TODO: gestire
    /*
    public void onClickPopupDismiss(int position, DatabaseGame databaseGame, View view) {
        insertNewGameDatabaseDBAndCode(databaseGame, );
        gameDatabaseRecyclerAdapter.notifyItemChanged(position);
        dialog.dismiss();
    }

    public void onClickOnlyRemove(DatabaseGame databaseGame, int position) {
        removedItemFromDatabaseDBAndCode(databaseGame);
        gameDatabaseRecyclerAdapter.notifyItemRemoved(position);
        dialog.dismiss();
    }
*/
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

    // TODO: implementare sort e filter
    /*
    private void setFilterAndSortButton() {
        ImageButton sortButton = findViewById(R.id.sort_button);
        ImageButton filterButton = findViewById(R.id.filter_button);
        if (Constants.sortDatabaseGame.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }
        if (Constants.filterDatabaseGame.equals("NO")) {
            filterButton.setImageResource(R.drawable.icon_filter);
        } else {
            filterButton.setImageResource(R.drawable.icon_no_filter);
        }

        sortButton.setOnClickListener(view -> {
            if (Constants.sortDatabaseGame.equals("ASC")) {
                setSortOrientation(sortButton, "ASC", "DESC", R.drawable.icon_sort_desc);
            } else if (Constants.sortDatabaseGame.equals("DESC")) {
                setSortOrientation(sortButton, "DESC", "ASC", R.drawable.icon_sort_asc);
            }
        });

        sortButton.setOnLongClickListener(view -> {
            sortButton.setVisibility(View.INVISIBLE);
            filterButton.setVisibility(View.VISIBLE);
            return true;
        });

        filterButton.setOnClickListener(view -> {
            if (Constants.filterDatabaseGame.equals("NO")) {
                Constants.filterDatabaseGame = "YES";
                View popupView = createPopUp(R.layout.popup_filter_database);
                setEntriesAndDefaultToSpinner(R.array.Console, R.layout.console_spinner_default_value, R.id.console_filter, popupView);
                addAutoCompleteVoice(popupView, R.id.saga_filter);
                ImageButton filtraButton = popupView.findViewById(R.id.filter_button);
                filtraButton.setOnClickListener(v -> {
                    List<DatabaseGame> filterList = applyFilter(popupView);
                    setCounter(rootView, null, filterList);
                });
                dialog.setCancelable(false);
                dialog.show();
                filterButton.setImageResource(R.drawable.icon_no_filter);
            } else if (Constants.filterDatabaseGame.equals("YES")) {
                Constants.filterDatabaseGame = "NO";
                dialog.dismiss();
                createRecyclerAdapter(Constants.getGameDatabaseList());
                setCounter(rootView, null, Constants.getGameDatabaseList());
                filterButton.setImageResource(R.drawable.icon_filter);
            }
        });

        filterButton.setOnLongClickListener(v -> {
            sortButton.setVisibility(View.VISIBLE);
            filterButton.setVisibility(View.INVISIBLE);
            return true;
        });
    }

     */
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        Constants.sortDatabaseGame = "DESC";
        Constants.filterDatabaseGame = "NO";
        createRecyclerAdapter(Constants.getGameSagheDatabaseList().stream()
                .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
        Log.i("GamesPurchase", " " + Constants.getGameSagheDatabaseList());
        //setFilterAndSortButton();
        getSupportActionBar().hide();
    }
}