package com.gamespurchase.activities;

import static com.gamespurchase.activities.MainActivity.count;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameBuyRecyclerAdapter;
import com.gamespurchase.adapter.NothingSelectedSpinnerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BuyActivity extends AppCompatActivity {

    Dialog dialog;
    Queries queries = new Queries();
    GameBuyRecyclerAdapter gameBuyRecyclerAdapter;
    ViewGroup rootView;

    // Richiama Queries.SELECT_ALL
    private List<BuyGame> retrieveGamesList() {
        return queries.selectBuyDB("id");
    }

    // Richiama Queries.DELETE
    public void removedItemFromBuyDBAndCode(BuyGame buyGame) {
        Queries.deleteBuyDB(buyGame);
        Optional<BuyGame> optGame = Constants.getGameBuyList().stream().filter(x -> x.getName().equals(buyGame.getName())).findAny();
        if (optGame.isPresent()) {
            Constants.getGameBuyList().remove(buyGame);
        }
        setCounter(rootView, Constants.getGameBuyList(), Constants.getGameDatabaseList());
    }

    // Richiama Queries.SELECT
    public static <T> List<BuyGame> filterFromBuyDBOrCode(String orderElement, String fieldToCompare, T valueToCompare, String name, String where) {

        List<BuyGame> filterList = new ArrayList<>();

        if (where.equals("DB")) {
            filterList = Queries.filterBuyDB(orderElement, fieldToCompare, valueToCompare);
            final int size = filterList.size();
            Handler handler = new Handler();
            handler.postDelayed(() -> Log.i("GamesPurchase", "Filtrati " + size + " elementi dal BuyDB"), 3000);
        } else if (where.equals("CODE")) {
            filterList = Constants.getGameBuyList().stream().filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))).sorted(Comparator.comparing(BuyGame::getSaga).thenComparing(BuyGame::getPriority).thenComparing(BuyGame::getName)).collect(Collectors.toList());
            Log.i("GamesPurchase", "Trovati " + filterList.size() + " elementi BuyGame");
        }

        return filterList;
    }

    // Richiama Queries.INSERT
    public static void insertNewGameBuyDBAndCode(Integer position, BuyGame buyGame, View view) {

        Optional<BuyGame> optGame = Constants.getGameBuyList().stream().filter(x -> x.getName().equals(buyGame.getName())).findAny();
        if (optGame.isPresent()) {
            buyGame.setId(optGame.get().getId());
        } else {
            Constants.maxIdBuyList++;
            buyGame.setId(String.valueOf(Constants.maxIdBuyList));
        }
        Queries.insertUpdateBuyDB(buyGame);
        if(position != null) {
            Constants.getGameBuyList().set(position, buyGame);
        } else{
            Constants.getGameBuyList().add(buyGame);
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setCounter(view, Constants.getGameBuyList(), Constants.getGameDatabaseList());
        }, 500);
    }

    // Divide i giochi in base alla saga
    private void classifyGame(List<BuyGame> buyGameList) {
        TreeMap<String, List<String>> classifyMap = new TreeMap<>();
        for (BuyGame g : buyGameList) {

            if (classifyMap.containsKey(g.getSaga())) {
                Objects.requireNonNull(classifyMap.get(g.getSaga())).add(g.getName());
            } else {
                List<String> newList = new ArrayList<>();
                newList.add(g.getName());
                classifyMap.put(g.getSaga(), newList);
            }
        }
        for (Map.Entry<String, List<String>> entry : classifyMap.entrySet()) {
            Log.i("GamesPurchase", entry.getKey() + " (" + entry.getValue().size() + ") = " + entry.getValue());
        }
    }

    // Aggiunge il BuyGame cancellato al DatabaseDB
    public void addDeleteItemToDatabaseDB(BuyGame buyGame, Boolean finished) {

        DatabaseGame databaseGame = new DatabaseGame();
        databaseGame.setName(buyGame.getName());
        databaseGame.setSaga(buyGame.getSaga());
        databaseGame.setPlatform(buyGame.getPlatform());
        databaseGame.setBuyed(Boolean.TRUE);
        databaseGame.setFinished(finished);

        DatabaseActivity.insertBuyGameInDatabaseDBAndCode(null, databaseGame);
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
                List<BuyGame> filterList = filterFromBuyDBOrCode(null, null, null, textInputLayout.getEditText().getText().toString(), "CODE");
                createRecyclerAdapter(filterList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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

            BuyGame buyGame = Constants.getGameBuyList().get(viewHolder.getAdapterPosition());

            View popupView = createPopUp(R.layout.popup_remove_then_add);
            TextView name = popupView.findViewById(R.id.edit_text);
            name.setText(buyGame.getName());
            ImageButton addButton = popupView.findViewById(R.id.add_button);
            CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
            CheckBox notFinishedCheckbox = popupView.findViewById(R.id.not_finished_checkbox);
            notFinishedCheckbox.setChecked(true);
            finishedCheckbox.setOnCheckedChangeListener((v, isChecked) -> changeChecked(finishedCheckbox, notFinishedCheckbox));
            notFinishedCheckbox.setOnCheckedChangeListener((v, isChecked) -> changeChecked(notFinishedCheckbox, finishedCheckbox));
            addButton.setOnClickListener(view -> onClickAddDatabaseDB(Constants.getGameBuyList().get(viewHolder.getAdapterPosition()), finishedCheckbox.isChecked()));
            ImageButton addRemoveButton = popupView.findViewById(R.id.remove_button);
            addRemoveButton.setOnClickListener(view -> onClickOnlyRemove(Constants.getGameBuyList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
            ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
            nullifyActionButton.setOnClickListener(view -> onClickPopupDismiss(viewHolder.getAdapterPosition(), buyGame, rootView));
            dialog.setCancelable(false);
            dialog.show();
        }
    });

    private void changeChecked(CheckBox checkBox, CheckBox checkBox2) {
        if (checkBox.isChecked()) {
            checkBox2.setChecked(false);
        }
    }

    public void onClickPopupDismiss(int position, BuyGame buyGame, View view) {
        insertNewGameBuyDBAndCode(position, buyGame, view);
        gameBuyRecyclerAdapter.notifyItemChanged(position);
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

    public void onClickAddBuyGame(View popupView, View rootView) {

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

        CheckBox inTransitCheckBox = popupView.findViewById(R.id.transit_checkbox);

        if (!name.isEmpty() && !saga.isEmpty() && !platform.isEmpty() && !priority.isEmpty()) {
            BuyGame buyGame = new BuyGame();
            buyGame.setName(name);
            buyGame.setSaga(saga);
            buyGame.setPlatform(platform);
            buyGame.setPriority(priority);
            buyGame.setCheckInTransit(inTransitCheckBox.isChecked());
            insertNewGameBuyDBAndCode(null, buyGame, rootView);
            List<BuyGame> buyGameListSorted = Constants.getGameBuyList();
            buyGameListSorted = buyGameListSorted.stream()
                    .sorted(Comparator.comparing(BuyGame::getSaga).thenComparing(BuyGame::getPriority).thenComparing(BuyGame::getName)).collect(Collectors.toList());
            Constants.setGameBuyList(buyGameListSorted);
            createRecyclerAdapter(Constants.getGameBuyList());
            dialog.dismiss();
        }
    }

    public void onClickAddDatabaseDB(BuyGame buyGame, Boolean finished) {
        addDeleteItemToDatabaseDB(buyGame, finished);
        removedItemFromBuyDBAndCode(buyGame);
        finish();
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }

    public void onClickOnlyRemove(BuyGame buyGame, int position) {
        removedItemFromBuyDBAndCode(buyGame);
        gameBuyRecyclerAdapter.notifyItemRemoved(position);
        dialog.dismiss();
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_buy);
        createRecyclerAdapter(Constants.getGameBuyList());
        setFilterAndSortButton();
    }

    public void onClickClose(View view) {
        finish();
    }

    public void onClickOpenAddBuyGamePopup(View view) {

        View popupView = createPopUp(R.layout.popup_buy_game);
        addAutoCompleteVoice(popupView, R.id.saga_text);
        setEntriesAndDefaultToSpinner(R.array.Console, R.layout.console_spinner_default_value, R.id.console_spinner, popupView);
        setEntriesAndDefaultToSpinner(R.array.Priority, R.layout.priority_spinner_default_value, R.id.priority_spinner, popupView);
        popupView.findViewById(R.id.add_button).setOnClickListener(v -> onClickAddBuyGame(popupView, rootView));
        dialog.show();
    }

    // Aggiunge allo spinner le possibili scelte e il valore di default
    private void setEntriesAndDefaultToSpinner(int arrayString, int spinnerLayout, int spinner, View view) {
        List<String> itemList = Arrays.stream(getResources().getStringArray(arrayString)).collect(Collectors.toList());
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, spinnerLayout, itemList);
        Spinner consoleSpinner = view.findViewById(spinner);
        consoleSpinner.setAdapter(spinnerAdapter);
        consoleSpinner.setAdapter(new NothingSelectedSpinnerAdapter(spinnerAdapter, spinnerLayout, this));
    }

    private void createRecyclerAdapter(List<BuyGame> buyGameList) {
        RecyclerView recyclerView = findViewById(R.id.game_buy);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameBuyRecyclerAdapter = new GameBuyRecyclerAdapter(buyGameList, this, rootView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameBuyRecyclerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Creazione Pop Up
    private View createPopUp(int id) {
        dialog = new Dialog(BuyActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private void setSortOrientation(ImageButton sortButton, String oldOrientation, String newOrientation, int id) {
        Constants.sortBuyGame = newOrientation;
        List<BuyGame> buyGameList = Constants.getGameBuyList();
        if (oldOrientation.equals("ASC")) {
            buyGameList = buyGameList.stream()
                    .sorted(Comparator.comparing(BuyGame::getSaga).thenComparing(BuyGame::getPriority).thenComparing(BuyGame::getName)).collect(Collectors.toList());
        } else {
            buyGameList = buyGameList.stream()
                    .sorted(Comparator.comparing(BuyGame::getSaga).reversed().thenComparing(BuyGame::getPriority).thenComparing(BuyGame::getName)).collect(Collectors.toList());
        }
        Constants.setGameBuyList(buyGameList);
        createRecyclerAdapter(Constants.getGameBuyList());
        sortButton.setImageResource(id);
    }

    private List<BuyGame> applyFilter(View view) {
        AutoCompleteTextView sagaFilter = view.findViewById(R.id.saga_filter);
        Spinner consoleFilter = view.findViewById(R.id.console_filter);
        Spinner priorityFilter = view.findViewById(R.id.priority_filter);
        CheckBox transitFilter = view.findViewById(R.id.transit_filter);
        CheckBox notTransitFilter = view.findViewById(R.id.not_transit_filter);

        List<BuyGame> filterList = Constants.getGameBuyList();
        if (sagaFilter.getText() != null) {
            filterList = filterList.stream().filter(g -> g.getSaga().contains(sagaFilter.getText())).collect(Collectors.toList());
        }
        if (consoleFilter.getSelectedItem() != null) {
            filterList = filterList.stream().filter(g -> g.getPlatform().equals(consoleFilter.getSelectedItem().toString())).collect(Collectors.toList());
        }
        if (priorityFilter.getSelectedItem() != null) {
            filterList = filterList.stream().filter(g -> g.getPriority().equals(priorityFilter.getSelectedItem().toString())).collect(Collectors.toList());
        }
        if(transitFilter.isChecked() && !notTransitFilter.isChecked()){
            filterList = filterList.stream().filter(g -> g.getCheckInTransit().equals(true)).collect(Collectors.toList());
        } else if(notTransitFilter.isChecked() && !transitFilter.isChecked()){
            filterList = filterList.stream().filter(g -> g.getCheckInTransit().equals(false)).collect(Collectors.toList());
        }
        createRecyclerAdapter(filterList);
        dialog.dismiss();

        return filterList;
    }

    public void addAutoCompleteVoice(View view, int id) {
        AutoCompleteTextView sagaAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        Constants.getGameBuyList().forEach(g -> {
            if (!autoCompleteVoice.contains(g.getSaga())) {
                autoCompleteVoice.add(g.getSaga());
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteVoice);
        sagaAutoComplete.setAdapter(arrayAdapter);
    }

    private void setFilterAndSortButton() {
        ImageButton sortButton = findViewById(R.id.sort_button);
        ImageButton filterButton = findViewById(R.id.filter_button);
        if (Constants.sortBuyGame.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }
        if (Constants.filterBuyGame.equals("NO")) {
            filterButton.setImageResource(R.drawable.icon_filter);
        } else {
            filterButton.setImageResource(R.drawable.icon_no_filter);
        }

        sortButton.setOnClickListener(view -> {
            if (Constants.sortBuyGame.equals("ASC")) {
                setSortOrientation(sortButton, "ASC", "DESC", R.drawable.icon_sort_desc);
            } else if (Constants.sortBuyGame.equals("DESC")) {
                setSortOrientation(sortButton, "DESC", "ASC", R.drawable.icon_sort_asc);
            }
        });

        sortButton.setOnLongClickListener(view -> {
            sortButton.setVisibility(View.INVISIBLE);
            filterButton.setVisibility(View.VISIBLE);
            return true;
        });

        filterButton.setOnClickListener(view -> {
            if (Constants.filterBuyGame.equals("NO")) {
                Constants.filterBuyGame = "YES";
                View popupView = createPopUp(R.layout.popup_filter_buy);
                setEntriesAndDefaultToSpinner(R.array.Console, R.layout.console_spinner_default_value, R.id.console_filter, popupView);
                setEntriesAndDefaultToSpinner(R.array.Priority, R.layout.priority_spinner_default_value, R.id.priority_filter, popupView);
                addAutoCompleteVoice(popupView, R.id.saga_filter);
                ImageButton filtraButton = popupView.findViewById(R.id.filter_button);
                filtraButton.setOnClickListener(v -> {
                    List<BuyGame> filterList = applyFilter(popupView);
                    setCounter(rootView, filterList, null);
                });
                dialog.setCancelable(false);
                dialog.show();
                filterButton.setImageResource(R.drawable.icon_no_filter);
            } else if (Constants.filterBuyGame.equals("YES")) {
                Constants.filterBuyGame = "NO";
                dialog.dismiss();
                createRecyclerAdapter(Constants.getGameBuyList());
                setCounter(rootView, Constants.getGameBuyList(), null);
                filterButton.setImageResource(R.drawable.icon_filter);
            }
        });

        filterButton.setOnLongClickListener(view -> {
            sortButton.setVisibility(View.VISIBLE);
            filterButton.setVisibility(View.INVISIBLE);
            return true;
        });
    }

    public static void setCounter(View view, List<BuyGame> buyGameList, List<DatabaseGame> databaseGameList){

        count(buyGameList, databaseGameList);

        Button totalCounter = view.findViewById(R.id.total_counter_button);
        Button transitCounter = view.findViewById(R.id.transit_counter_button);
        Button highCounter = view.findViewById(R.id.high_counter_button);
        Button mediumCounter = view.findViewById(R.id.medium_counter_button);
        Button lowCounter = view.findViewById(R.id.low_counter_button);
        totalCounter.setText("Totali:\n");
        transitCounter.setText("Transito:\n");
        highCounter.setText("High:\n");
        mediumCounter.setText("Medium:\n");
        lowCounter.setText("Low:\n");

        totalCounter.setText(totalCounter.getText() + Constants.getCounterBuyGame().get("Totale").toString());
        transitCounter.setText(transitCounter.getText() + Constants.getCounterBuyGame().get("Transito").toString());
        highCounter.setText(highCounter.getText() + Constants.getCounterBuyGame().get("HIGH").toString());
        mediumCounter.setText(mediumCounter.getText() + Constants.getCounterBuyGame().get("MEDIUM").toString());
        lowCounter.setText(lowCounter.getText() + Constants.getCounterBuyGame().get("LOW").toString());
    }

    public void onClickPopupInfoShow(View view){
        View popupView = createPopUp(R.layout.popup_info_saghe);
        TextView sagheTextView = popupView.findViewById(R.id.saghe_text_view);
        sagheTextView.setText(filterBySaga());
        dialog.show();
    }

    private String filterBySaga(){
        StringBuilder sb = new StringBuilder();
        TreeMap<String, Integer> mapSaga = new TreeMap<>();
        gameBuyRecyclerAdapter.getGameBuyList().forEach(x -> {
            if (!mapSaga.containsKey(x.getSaga())) {
                mapSaga.put(x.getSaga(), 1);
            } else {
                mapSaga.put(x.getSaga(), mapSaga.get(x.getSaga()) + 1);
            }
        });
        int counter = 0;
        for (String s : mapSaga.keySet()) {
            sb.append(s).append(" - ").append(mapSaga.get(s));
            if (counter != mapSaga.size() - 1){
                sb.append(System.getProperty("line.separator"));
            }
            counter++;
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        Constants.sortBuyGame = "DESC";
        Constants.filterBuyGame = "NO";
        List<BuyGame> buyGameListSorted = Constants.getGameBuyList();
        buyGameListSorted = buyGameListSorted.stream()
                .sorted(Comparator.comparing(BuyGame::getSaga).thenComparing(BuyGame::getPriority).thenComparing(BuyGame::getName)).collect(Collectors.toList());
        Constants.setGameBuyList(buyGameListSorted);
        createRecyclerAdapter(Constants.getGameBuyList());
        setCounter(rootView, Constants.getGameBuyList(), Constants.getGameDatabaseList());
        setFilterAndSortButton();
        getSupportActionBar().hide();
    }
}