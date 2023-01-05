package com.gamespurchase.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.gamespurchase.adapter.GameStartRecyclerAdapter;
import com.gamespurchase.adapter.NothingSelectedSpinnerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class StartActivity extends AppCompatActivity {

    Dialog dialog;
    GameStartRecyclerAdapter gameStartRecyclerAdapter;
    ViewGroup rootView;

    // Richiama Queries.DELETE
    public void removedItemFromStartDBAndCode(ProgressGame progressGame) {
        Queries.deleteStartDB(progressGame);
        Optional<ProgressGame> optGame = Constants.getGameStartList().stream().filter(x -> x.getName().equals(progressGame.getName())).findAny();
        if (optGame.isPresent()) {
            Constants.getGameStartList().remove(progressGame);
        }
    }

    // Richiama Queries.INSERT
    public static void insertNewGameStartDBAndCode(Integer position, ProgressGame progressGame, View view) {

        Queries.insertUpdateStartDB(progressGame);
        List<ProgressGame> progressGameList = Queries.filterStartDB("name", "label", progressGame.getLabel());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.getStartGameMap().put(progressGame.getLabel(), progressGameList);
        }, 100);
    }

    public void onClickSearch(View view) {

        ImageView orderButton = findViewById(R.id.sort_button);
        orderButton.setVisibility(View.INVISIBLE);
        ImageView returnButton = findViewById(R.id.return_button);
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
                List<ProgressGame> filterList = Constants.getGameStartList().stream().filter(x -> x.getName().substring(0, textInputLayout.getEditText().getText().toString().length() - 1).equals(textInputLayout.getEditText().getText().toString())).collect(Collectors.toList());
                createRecyclerAdapter(filterList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_database);
        createRecyclerAdapter(Constants.getGameStartList());
        setFilterAndSortButton();
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
            ProgressGame progressGame = Constants.getGameStartList().get(viewHolder.getAdapterPosition());
            View popupView = createPopUp(R.layout.popup_delete_database_game);
            TextView textView = popupView.findViewById(R.id.edit_text);
            textView.setText("Rimuovere " + Constants.getGameStartList().get(viewHolder.getAdapterPosition()).getName() + "?");
            ImageButton removeButton = popupView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(view -> onClickOnlyRemove(Constants.getGameStartList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
            ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
            nullifyActionButton.setOnClickListener(view -> onClickPopupDismiss(viewHolder.getAdapterPosition(), progressGame, rootView));
            dialog.setCancelable(false);
            dialog.show();
        }
    });

    public void onClickPopupDismiss(int position, ProgressGame progressGame, View view){
        insertNewGameStartDBAndCode(position, progressGame, view);
        gameStartRecyclerAdapter.notifyItemChanged(position);
        dialog.dismiss();
    }

    public void onClickOnlyRemove(ProgressGame progressGame, int position){
        removedItemFromStartDBAndCode(progressGame);
        gameStartRecyclerAdapter.notifyItemRemoved(position);
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
        dialog = new Dialog(StartActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private void setSortOrientation(ImageButton sortButton, String oldOrientation, String newOrientation, int id) {
        Constants.sortListGame = newOrientation;
        List<ProgressGame> gameStartList = Constants.getGameStartList();
        if (oldOrientation.equals("ASC")) {
            gameStartList = gameStartList.stream()
                    .sorted(Comparator.comparing(ProgressGame::getName)).collect(Collectors.toList());
        } else {
            gameStartList = gameStartList.stream()
                    .sorted(Comparator.comparing(ProgressGame::getName).reversed()).collect(Collectors.toList());
        }
        Constants.setGameStartList(gameStartList);
        createRecyclerAdapter(Constants.getGameStartList());
        sortButton.setImageResource(id);
    }

    public void addAutoCompleteVoice(View view, int id) {
        AutoCompleteTextView sagaAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        Constants.getGameDatabaseList().forEach(g -> {
            if (!autoCompleteVoice.contains(g.getSaga())) {
                autoCompleteVoice.add(g.getSaga());
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteVoice);
        sagaAutoComplete.setAdapter(arrayAdapter);
    }

    private void setFilterAndSortButton() {
        ImageButton sortButton = findViewById(R.id.sort_button);
        if (Constants.sortListGame.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }

        sortButton.setOnClickListener(view -> {
            if (Constants.sortListGame.equals("ASC")) {
                setSortOrientation(sortButton, "ASC", "DESC", R.drawable.icon_sort_desc);
            } else if (Constants.sortListGame.equals("DESC")) {
                setSortOrientation(sortButton, "DESC", "ASC", R.drawable.icon_sort_asc);
            }
        });
    }

    private void createRecyclerAdapter(List<ProgressGame> progressGameList) {
        RecyclerView recyclerView = findViewById(R.id.game_start);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameStartRecyclerAdapter = new GameStartRecyclerAdapter(progressGameList, this, rootView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameStartRecyclerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onClickChangeRightLabelList(View v){
        Constants.setActualList(changeLabel(Boolean.TRUE));
    }

    public void onClickChangeLeftLabelList(View v){
        Constants.setActualList(changeLabel(Boolean.FALSE));
    }

    private static String changeLabel(Boolean sum) {

        List<String> labelList = Constants.getListGameList();
        int oldLabel = labelList.indexOf(Constants.getActualList());
        String newLabel;
        if (sum) {
            newLabel = (oldLabel == Constants.getListGameList().size() - 1) ? labelList.get(0) : labelList.get(oldLabel + 1);
        } else {
            newLabel = (oldLabel == 0) ? labelList.get(6) : labelList.get(oldLabel - 1);
        }
        return newLabel;
    }

    public void setGlobalVariables() {
        Constants.setSortListGame("DESC");
        Constants.setActualList("AAA");
        Constants.setStartGameMap(selectAllStartDB());
        List<ProgressGame> startGameList = Constants.getStartGameMap().get(Constants.getActualList());
        EditText header = rootView.findViewById(R.id.name_text);
        Constants.setGameStartList(startGameList);
        String newHeader = "Lista " + Constants.getActualList() + " di " + Constants.getGameStartList().size() + " giochi";
        header.setText(newHeader);
        createRecyclerAdapter(Constants.getGameStartList());
    }

    public HashMap<String, List<ProgressGame>> selectAllStartDB() {
        HashMap<String, List<ProgressGame>> startGameMap = new HashMap<>();
        List<ProgressGame> startGameList = Queries.selectStartDB("name");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startGameList.forEach(x -> {
                List<ProgressGame> tempStartGameList = new ArrayList<>();
                if (startGameMap.containsKey(x.getLabel())) {
                    tempStartGameList = startGameMap.get(x.getLabel());
                }
                tempStartGameList.add(x);
                startGameMap.put(x.getLabel(), tempStartGameList);
            });
        }, 100);
        return startGameMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        setGlobalVariables();
        setFilterAndSortButton();
        getSupportActionBar().hide();
    }
}