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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameProgressRecyclerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgressActivity extends AppCompatActivity {

    Dialog dialog;
    GameProgressRecyclerAdapter gameStartRecyclerAdapter;
    ViewGroup rootView;

    // Richiama Queries.DELETE
    public void removedItemFromStartDBAndCode(ProgressGame progressGame) {
        Queries.deleteProgressDB(progressGame);
        Optional<ProgressGame> optGame = Constants.getActualLabelGameProgressList().stream().filter(x -> x.getName().equals(progressGame.getName())).findAny();
        if (optGame.isPresent()) {
            Constants.getActualLabelGameProgressList().remove(progressGame);
        }
    }

    // Richiama Queries.INSERT
    public static void insertNewGameStartDBAndCode(ProgressGame progressGame) {
        Optional<ProgressGame> optGame = Constants.getActualLabelGameProgressList().stream().filter(x -> x.getName().equals(progressGame.getName())).findAny();
        if (optGame.isPresent()) {
            progressGame.setId(optGame.get().getId());
            Log.i("GamesPurchase", "ID Aggiornato " + progressGame.getLabel());
        } else {
            Constants.maxIdProgressList++;
            progressGame.setId(String.valueOf(Constants.maxIdProgressList));
            Log.i("GamesPurchase", "ID Aggiunto " + progressGame.getLabel());
        }
        Queries.insertUpdateProgressDB(progressGame);
        List<ProgressGame> progressGameList = Queries.filterProgressDB("name", "label", progressGame.getLabel());
        Log.i("GamesPurchase", "Label filtrata " + progressGame.getLabel());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.getScheduledGameMap().put(progressGame.getLabel(), progressGameList);
            Log.i("GamesPurchase", "Label modificato " + progressGame.getLabel());
            Constants.setActualLabelGameProgressList(Constants.getScheduledGameMap().get(progressGame.getLabel()));
        }, 100);
    }

    public void onClickOpenAddStartGamePopup(View view) {

        View popupView = createPopUp(R.layout.popup_progress_game);
        addAutoCompleteVoice(popupView, R.id.saga_text);
        TextView addButton = popupView.findViewById(R.id.update_text_view);
        addButton.setText("Aggiungi");
        TextView dataText = popupView.findViewById(R.id.data_edit_text);
        dataText.setText("TBS");
        TextView hourText = popupView.findViewById(R.id.hour_edit_text);
        hourText.setText("1");
        TextView actualText = popupView.findViewById(R.id.actual_edit_text);
        actualText.setText("0");
        TextView totalText = popupView.findViewById(R.id.total_edit_text);
        totalText.setText("1");
        Spinner labelSpinner = popupView.findViewById(R.id.label_spinner);
        int labelPosition = Arrays.stream(getResources().getStringArray(R.array.Label)).collect(Collectors.toList()).indexOf(Constants.getActualList());
        labelSpinner.setSelection(labelPosition);
        popupView.findViewById(R.id.update_button).setOnClickListener(v -> onClickAddStartGame(popupView));
        dialog.show();
    }

    public void addAutoCompleteVoice(View view, int id){
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

    public void onClickAddStartGame(View popupView) {

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        String name = checkIfAutoCompleteTextIsNull(nameText);
        if (name.isEmpty()) {
            nameText.setError(getResources().getString(R.string.notEmpty));
        }

        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        String saga = checkIfAutoCompleteTextIsNull(sagaText);
        if (saga.isEmpty()) {
            sagaText.setError(getResources().getString(R.string.notEmpty));
        }

        EditText dataText = popupView.findViewById(R.id.data_edit_text);
        String data = checkIfEditTextIsNull(dataText, "TBS");

        EditText hourText = popupView.findViewById(R.id.hour_edit_text);
        String hour = checkIfEditTextIsNull(hourText, "0");

        EditText actualText = popupView.findViewById(R.id.actual_edit_text);
        String actual = checkIfEditTextIsNull(actualText, "0");

        EditText totalText = popupView.findViewById(R.id.total_edit_text);
        String total = checkIfEditTextIsNull(totalText, "1");

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

        Spinner labelSpinner = popupView.findViewById(R.id.label_spinner);
        String label = labelSpinner.getSelectedItem().toString();

        CheckBox buyedCheckbox = popupView.findViewById(R.id.buyed_checkbox);
        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

        if (!name.isEmpty() && !saga.isEmpty() && !platform.isEmpty()) {
            ProgressGame progressGame = new ProgressGame(Integer.parseInt(actual), Integer.parseInt(total), Integer.parseInt(hour), data, name, saga, platform, priority, label, buyedCheckbox.isChecked(), transitCheckbox.isChecked());
            insertNewGameStartDBAndCode(progressGame);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                gameStartRecyclerAdapter.updateData(Constants.getActualLabelGameProgressList().stream()
                        .sorted(Comparator.comparing(ProgressGame::getName)).collect(Collectors.toList()));
                selectListByLabel(progressGame.getLabel());
            }, 100);
            dialog.dismiss();
        }
    }

    private String checkIfEditTextIsNull(EditText textInputEditText, String value) {
        if (textInputEditText != null && textInputEditText.getText() != null && !textInputEditText.getText().toString().equals("")) {
            return textInputEditText.getText().toString();
        } else {
            return value;
        }
    }

    private String checkIfAutoCompleteTextIsNull(AutoCompleteTextView textInputEditText) {
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

    public void onClickSearch(View view) {

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
                List<ProgressGame> filterList = Constants.getActualLabelGameProgressList().stream().filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(textInputLayout.getEditText().getText().toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
                createRecyclerAdapter(filterList);
                TextView header = rootView.findViewById(R.id.number_text);
                String newHeader = filterList.size() + (filterList.size() == 1 ? " Gioco" : " Giochi");
                header.setText(newHeader);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_progress);
        setFilterAndSortButton();
        selectListByLabel(Constants.getActualList());
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
            ProgressGame progressGame = Constants.getActualLabelGameProgressList().get(viewHolder.getAdapterPosition());
            View popupView = createPopUp(R.layout.popup_delete_database_game);
            TextView textView = popupView.findViewById(R.id.edit_text);
            String newText = "Rimuovere " + Constants.getActualLabelGameProgressList().get(viewHolder.getAdapterPosition()).getName() + "?";
            textView.setText(newText);
            ImageButton removeButton = popupView.findViewById(R.id.delete_button);
            removeButton.setOnClickListener(view -> onClickOnlyRemove(Constants.getActualLabelGameProgressList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition()));
            ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
            nullifyActionButton.setOnClickListener(view -> onClickPopupDismiss(viewHolder.getAdapterPosition(), progressGame));
            dialog.setCancelable(false);
            dialog.show();
        }
    });

    public void onClickPopupDismiss(int position, ProgressGame progressGame) {
        insertNewGameStartDBAndCode(progressGame);
        selectListByLabel(progressGame.getLabel());
        gameStartRecyclerAdapter.notifyItemChanged(position);
        dialog.dismiss();
    }

    public void onClickOnlyRemove(ProgressGame progressGame, int position) {
        removedItemFromStartDBAndCode(progressGame);
        selectListByLabel(progressGame.getLabel());
        gameStartRecyclerAdapter.notifyItemRemoved(position);
        dialog.dismiss();
    }

    // Creazione Pop Up
    private View createPopUp(int id) {
        dialog = new Dialog(ProgressActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    private void setSortOrientation(ImageButton sortButton, String oldOrientation, String newOrientation, int id) {
        Constants.sortListGame = newOrientation;
        List<ProgressGame> gameStartList = Constants.getActualLabelGameProgressList();
        if (oldOrientation.equals("ASC")) {
            gameStartList = gameStartList.stream()
                    .sorted(Comparator.comparing(ProgressGame::getName)).collect(Collectors.toList());
        } else {
            gameStartList = gameStartList.stream()
                    .sorted(Comparator.comparing(ProgressGame::getName).reversed()).collect(Collectors.toList());
        }
        Constants.setActualLabelGameProgressList(gameStartList);
        createRecyclerAdapter(Constants.getActualLabelGameProgressList());
        sortButton.setImageResource(id);
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
        gameStartRecyclerAdapter = new GameProgressRecyclerAdapter(progressGameList, this, rootView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameStartRecyclerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onClickChangeRightLabelList(View v) {
        Constants.setActualList(changeLabel(Boolean.TRUE));
        selectListByLabel(Constants.getActualList());
    }

    public void onClickChangeLeftLabelList(View v) {
        Constants.setActualList(changeLabel(Boolean.FALSE));
        selectListByLabel(Constants.getActualList());
    }

    private static String changeLabel(Boolean sum) {

        List<String> labelList = Constants.getListGameList();
        int oldLabel = labelList.indexOf(Constants.getActualList());
        String newLabel;
        if (sum) {
            newLabel = (oldLabel == Constants.getListGameList().size() - 1) ? labelList.get(0) : labelList.get(oldLabel + 1);
        } else {
            newLabel = (oldLabel == 0) ? labelList.get(Constants.getListGameList().size() - 1) : labelList.get(oldLabel - 1);
        }
        return newLabel;
    }

    public void setGlobalVariables() {
        HashMap<String, List<ProgressGame>> startDB = selectAllStartDB();
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            Constants.setScheduledGameMap(startDB);
            selectListByLabel(Constants.getActualList());

        }, 100);
    }

    public void selectListByLabel(String label) {
        List<ProgressGame> startGameList = Constants.getScheduledGameMap().get(label);
        Constants.setActualLabelGameProgressList(startGameList);
        Constants.setCounterProgressGame(Constants.getActualLabelGameProgressList() == null ? 0 : Constants.getActualLabelGameProgressList().size());
        TextView labelHeader = rootView.findViewById(R.id.label_text);
        TextView numberHeader = rootView.findViewById(R.id.number_text);
        int dimension = Constants.getCounterProgressGame();
        labelHeader.setText(label);
        String newText = dimension + (1 == dimension ? " Gioco" : " Giochi");
        numberHeader.setText(newText);
        createRecyclerAdapter(Constants.getActualLabelGameProgressList());
    }

    public HashMap<String, List<ProgressGame>> selectAllStartDB() {
        HashMap<String, List<ProgressGame>> startGameMap = new HashMap<>();
        List<ProgressGame> progressGameList = Queries.selectProgressDB("name");

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.setAllLabelGameProgressList(progressGameList);
            Constants.getAllLabelGameProgressList().forEach(x -> {
                List<ProgressGame> tempStartGameList = new ArrayList<>();
                if (startGameMap.containsKey(x.getLabel())) {
                    tempStartGameList = startGameMap.get(x.getLabel());
                }
                Objects.requireNonNull(tempStartGameList).add(x);
                startGameMap.put(x.getLabel(), tempStartGameList);
            });
        }, 100);
        return startGameMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        Constants.setSortListGame("DESC");
        Constants.setActualList("AAA");
        setGlobalVariables();
        setFilterAndSortButton();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}