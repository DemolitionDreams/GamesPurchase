package com.gamespurchase.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameProgressRecyclerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.utilities.ActivityUtility;
import com.gamespurchase.utilities.CompareUtility;
import com.gamespurchase.utilities.RecyclerAdapterUtility;
import com.gamespurchase.utilities.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgressActivity extends AppCompatActivity {

    Dialog dialog;
    GameProgressRecyclerAdapter gameProgressRecyclerAdapter;
    ViewGroup rootView;

    private void updateAdapterByActualLabel() {
        Constants.setActualLabelProgressGameList(Constants.getProgressGameMap().get(Constants.getActualLabel()));

        if (Constants.getActualLabelProgressGameList() == null) {
            Constants.setActualLabelProgressGameList(new ArrayList<>());
        }
        createRecyclerAdapter
                (Utility.sortList(ProgressGame::getName, Constants.getActualLabelProgressGameList(),
                        CompareUtility.Order.ASCENDING));
    }

    public static void updateListByActualLabel(String label, ViewGroup rootView) {
        Constants.getProgressGameMap().put(label, Constants.getActualLabelProgressGameList());
        Constants.setCounterProgressGame(Constants.getActualLabelProgressGameList() == null ? 0 : Constants.getActualLabelProgressGameList().size());
        TextView labelHeader = rootView.findViewById(R.id.label_text);
        TextView numberHeader = rootView.findViewById(R.id.number_text);
        int dimension = Constants.getCounterProgressGame();
        labelHeader.setText(label);
        String newText = dimension + (1 == dimension ? " Gioco" : " Giochi");
        numberHeader.setText(newText);
    }

    public static void onClickPopupDismiss(ProgressGame progressGame, GameProgressRecyclerAdapter gameProgressRecyclerAdapter) {
        insertNewGameStartDBAndCode(progressGame, progressGame.getName(), gameProgressRecyclerAdapter, false);
        Constants.getProgressGameMap().put(progressGame.getLabel(), Constants.getActualLabelProgressGameList());
        RecyclerAdapterUtility.updateData(gameProgressRecyclerAdapter.progressGameList, Constants.getActualLabelProgressGameList(), gameProgressRecyclerAdapter);
    }

    public static void onClickOnlyRemove(ProgressGame progressGame, int position, GameProgressRecyclerAdapter gameProgressRecyclerAdapter, ViewGroup rootView) {
        Utility.removedItemFromDatabase(Constants.PROGRESSDB, progressGame.getId());
        RecyclerAdapterUtility.removeItem(gameProgressRecyclerAdapter.progressGameList, Constants.getActualLabelProgressGameList(), gameProgressRecyclerAdapter, position, progressGame);
        updateListByActualLabel(progressGame.getLabel(), rootView);
    }

    public void onClickSearch(View view) {
        ActivityUtility.onClickSearch(rootView, gameProgressRecyclerAdapter, Constants.getActualLabelProgressGameList(), ProgressGame::getName, gameProgressRecyclerAdapter.progressGameList);
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_progress);
        RecyclerAdapterUtility.updateData(gameProgressRecyclerAdapter.progressGameList, Constants.getActualLabelProgressGameList(), gameProgressRecyclerAdapter);
        Constants.setSortProgressGame("DESC");
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
            ProgressGame progressGame = Constants.getActualLabelProgressGameList().get(viewHolder.getAdapterPosition());
            ActivityUtility.swipeGame(ProgressActivity.this, R.layout.popup_delete_database_game, viewHolder, gameProgressRecyclerAdapter, progressGame, progressGame.getName(), rootView);
        }
    });

    public void onClickOpenAddStartGamePopup(View view) {
        View popupView = Utility.createPopUp(R.layout.popup_progress_game, this, dialog);
        Utility.addAutoCompleteVoice(this, popupView, R.id.saga_text, Constants.getSagheDatabaseGameList(), SagheDatabaseGame::getName);
        TextView addButton = popupView.findViewById(R.id.update_text_view);
        addButton.setText(getResources().getString(R.string.addText));
        TextView dataText = popupView.findViewById(R.id.data_edit_text);
        dataText.setText(getResources().getString(R.string.tbs));
        TextView hourText = popupView.findViewById(R.id.hour_edit_text);
        hourText.setText("1");
        TextView actualText = popupView.findViewById(R.id.actual_edit_text);
        actualText.setText("0");
        TextView totalText = popupView.findViewById(R.id.total_edit_text);
        totalText.setText("1");
        Spinner labelSpinner = popupView.findViewById(R.id.label_spinner);
        int labelPosition = Arrays.stream(getResources().getStringArray(R.array.Label)).collect(Collectors.toList()).indexOf(Constants.getActualLabel());
        labelSpinner.setSelection(labelPosition);
        popupView.findViewById(R.id.update_button).setOnClickListener(v -> onClickAddStartGame(popupView));
        dialog.show();
    }

    // Richiama Queries.INSERT
    public static void insertNewGameStartDBAndCode(ProgressGame progressGame, String name, GameProgressRecyclerAdapter gameProgressRecyclerAdapter, Boolean insert) {
        Optional<ProgressGame> optGame = Constants.getActualLabelProgressGameList().stream().filter(x -> x.getName().equals(name)).findAny();
        Log.i("GamesPurchase", "Dimensioni 1 " + Constants.getActualLabelProgressGameList().size());
        if (optGame.isPresent()) {
            progressGame.setId(optGame.get().getId());
            Log.i("GamesPurchase", "ID Aggiornato " + progressGame.getLabel());
        } else {
            Constants.maxIdProgressList++;
            progressGame.setId(String.valueOf(Constants.maxIdProgressList));
            Log.i("GamesPurchase", "ID Aggiunto " + progressGame.getLabel());
            Log.i("GamesPurchase", "Dimensioni 2 " + Constants.getActualLabelProgressGameList().size());
        }
        Queries.insertUpdateItemDB(progressGame, progressGame.getId(), Constants.PROGRESSDB);
        Log.i("GamesPurchase", "Dimensioni 3 " + Constants.getActualLabelProgressGameList().size());
        if (insert) {
            Log.i("GamesPurchase", "Dimensioni 4 " + Constants.getActualLabelProgressGameList().size());
            RecyclerAdapterUtility.insertItem(
                    CompareUtility.comparatorOf(ProgressGame::getName, CompareUtility.Order.ASCENDING, CompareUtility.Nulls.LAST),
                    gameProgressRecyclerAdapter.progressGameList, Constants.getActualLabelProgressGameList(),
                    progressGame, gameProgressRecyclerAdapter);
            Log.i("GamesPurchase", "Dimensioni 5 " + Constants.getActualLabelProgressGameList().size());
        }
    }

    public void onClickAddStartGame(View popupView) {

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        String name = Utility.checkIfEditTextIsNull(nameText);
        if (name.isEmpty()) {
            nameText.setError(getResources().getString(R.string.notEmpty));
        }

        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        String saga = Utility.checkIfEditTextIsNull(sagaText);
        if (saga.isEmpty()) {
            sagaText.setError(getResources().getString(R.string.notEmpty));
        }

        EditText dataText = popupView.findViewById(R.id.data_edit_text);
        String data = Utility.checkIfEditTextIsNull(dataText, "TBS");

        EditText hourText = popupView.findViewById(R.id.hour_edit_text);
        String hour = Utility.checkIfEditTextIsNull(hourText, "0");

        EditText actualText = popupView.findViewById(R.id.actual_edit_text);
        String actual = Utility.checkIfEditTextIsNull(actualText, "0");

        EditText totalText = popupView.findViewById(R.id.total_edit_text);
        String total = Utility.checkIfEditTextIsNull(totalText, "1");

        String platform = Utility.checkIfSpinnerIsNull(popupView, R.id.console_spinner, getResources().getString(R.string.notNotSelected));
        String priority = Utility.checkIfSpinnerIsNull(popupView, R.id.priority_spinner, getResources().getString(R.string.notNotSelected));

        Spinner labelSpinner = popupView.findViewById(R.id.label_spinner);
        String label = labelSpinner.getSelectedItem().toString();

        CheckBox buyCheckbox = popupView.findViewById(R.id.buyed_checkbox);
        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

        if (!name.isEmpty() && !saga.isEmpty() && !platform.isEmpty()) {
            ProgressGame progressGame = new ProgressGame(Integer.parseInt(actual), Integer.parseInt(total), Integer.parseInt(hour), data, name, saga, platform, priority, label, buyCheckbox.isChecked(), transitCheckbox.isChecked());

            Log.i("GamesPurchase", "Dimensioni prima " + Constants.getActualLabelProgressGameList().size());
            insertNewGameStartDBAndCode(progressGame, progressGame.getName(), gameProgressRecyclerAdapter, true);
            if(Constants.getActualLabelProgressGameList().size() == 1) {
                createRecyclerAdapter(Constants.getActualLabelProgressGameList());
                Log.i("GamesPurchase", "Sono 1");
            }
            updateListByActualLabel(progressGame.getLabel(), rootView);

            Log.i("GamesPurchase", "Dimensioni dopo " + Constants.getActualLabelProgressGameList().size());
            dialog.dismiss();
        }
    }

    private void createRecyclerAdapter(List<ProgressGame> progressGameList) {
        gameProgressRecyclerAdapter = new GameProgressRecyclerAdapter(progressGameList, this, dialog);
        Utility.createRecyclerAdapter(rootView, R.id.game_start, this,
                gameProgressRecyclerAdapter, itemTouchHelper);
    }

    public void onClickChangeRightLabelList(View v) {

        Constants.setActualLabel(changeLabel(Boolean.TRUE));
        updateAdapterByActualLabel();
        updateListByActualLabel(Constants.getActualLabel(), rootView);
    }

    public void onClickChangeLeftLabelList(View v) {
        Constants.setActualLabel(changeLabel(Boolean.FALSE));
        updateAdapterByActualLabel();
        updateListByActualLabel(Constants.getActualLabel(), rootView);
    }

    private static String changeLabel(Boolean sum) {

        List<String> labelList = Constants.getListGameList();
        int oldLabel = labelList.indexOf(Constants.getActualLabel());
        String newLabel;
        if (sum) {
            newLabel = (oldLabel == labelList.size() - 1) ? labelList.get(0) : labelList.get(oldLabel + 1);
        } else {
            newLabel = (oldLabel == 0) ? labelList.get(labelList.size() - 1) : labelList.get(oldLabel - 1);
        }
        return newLabel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        dialog = new Dialog(this);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        Utility.setFilterButton(CompareUtility.comparatorOf(ProgressGame::getName, CompareUtility.Order.ASCENDING,
                CompareUtility.Nulls.LAST), rootView, Constants.sortProgressGame, Constants.getActualLabelProgressGameList());
        Constants.setSortProgressGame(Constants.getSortProgressGame().equals("ASC") ? "DESC" : "ASC");
        Objects.requireNonNull(getSupportActionBar()).hide();
        updateAdapterByActualLabel();
        updateListByActualLabel(Constants.getActualLabel(), rootView);
    }
}