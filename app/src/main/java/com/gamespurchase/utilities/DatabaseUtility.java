package com.gamespurchase.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.BuyActivity;
import com.gamespurchase.activities.DatabaseActivity;
import com.gamespurchase.adapter.GameSagaDatabaseRecyclerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DatabaseUtility {

    // Richiama Queries.DELETE
    public static void removedItemFromDatabaseDBAndCode(String nameDB, String idElement){
        Queries.deleteItemDB(nameDB, idElement);
    }

    public void changeBuyOrNotBuy(DatabaseGame databaseGame, Boolean notBuyInBuy, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {

        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getGamesNotBuy().contains(databaseGame)).findAny();
        DatabaseUtility.insertNewGameDatabaseDBAndCode(databaseGame, optSagheGame.get().getName(), notBuyInBuy ? new DatabaseActivity() : new BuyActivity(), gameSagaDatabaseRecyclerAdapter);
    }

    public static void insertNewGameDatabaseDBAndCode(DatabaseGame databaseGame, String saga, Activity activity, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {

        SagheDatabaseGame sagheDatabaseGame;
        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(saga)).findAny();
        if (optSagheGame.isPresent()) {
            removedDatabaseGameFromSaga(optSagheGame.get().getGamesBuy(), databaseGame.getName());
            removedDatabaseGameFromSaga(optSagheGame.get().getGamesNotBuy(), databaseGame.getName());
            sagheDatabaseGame = optSagheGame.get();
            Log.i("GamesPurchase", "Aggiunto " + databaseGame.getName() + " alla saga " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati");
        } else {
            Constants.maxIdDatabaseList++;
            List<DatabaseGame> buyGames = new ArrayList<>();
            List<DatabaseGame> notBuyGames = new ArrayList<>();
            sagheDatabaseGame = new SagheDatabaseGame(String.valueOf(Constants.getMaxIdDatabaseList()), saga, Boolean.TRUE, databaseGame.getFinished() ? Boolean.TRUE : Boolean.FALSE, buyGames, notBuyGames);
            Log.i("GamesPurchase", "Inserita nuova saga: " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") e aggiunto " + databaseGame.getName() + " alla lista buyGames");
        }
        if (activity instanceof DatabaseActivity) {
            sagheDatabaseGame.getGamesBuy().add(databaseGame);
        } else {
            sagheDatabaseGame.getGamesNotBuy().add(databaseGame);
        }

        Boolean[] check = checkBuyAllOrFinishAll(sagheDatabaseGame);
        sagheDatabaseGame.setBuyAll(check[0]);
        sagheDatabaseGame.setFinishAll(check[1]);

        Queries.insertUpdateItemDB(sagheDatabaseGame, sagheDatabaseGame.getId(), Constants.DATABASEDB);
        gameSagaDatabaseRecyclerAdapter.insertItem(sagheDatabaseGame);
    }

    public static void insertNewGameSagaDatabaseDBAndCode(SagheDatabaseGame sagheDatabaseGame, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {
        Queries.insertUpdateItemDB(sagheDatabaseGame, sagheDatabaseGame.getId(), Constants.DATABASEDB);
        gameSagaDatabaseRecyclerAdapter.insertItem(sagheDatabaseGame);
    }

    public static void removedDatabaseGameFromSaga(List<DatabaseGame> databaseGameList, String name){
        Optional<DatabaseGame> optGameBuy = databaseGameList.stream().filter(x -> x.getName().equals(name)).findAny();
        if (optGameBuy.isPresent()) {
            Log.i("GamesPurchase", "Rimosso " + name);
            databaseGameList.remove(optGameBuy.get());
        }
    }

    private static Boolean[] checkBuyAllOrFinishAll(SagheDatabaseGame sagheDatabaseGame) {
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

    public static <V> void onClickOpenAddDatabaseGamePopup(Context context, Dialog dialog, int id, List<V> gameList, Function<V, String> getterToCompare, Activity activity, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {

        View popupView = createPopUp(id, context, dialog);
        Utility.addAutoCompleteVoice(context, popupView, R.id.saga_text, gameList, getterToCompare);
        Utility.setEntriesAndDefaultToSpinner(context, R.array.Console, R.layout.console_spinner_default_value, R.id.console_spinner, popupView);
        Utility.setEntriesAndDefaultToSpinner(context, R.array.Priority, R.layout.priority_spinner_default_value, R.id.priority_spinner, popupView);
        popupView.findViewById(R.id.add_button).setOnClickListener(v -> onClickAddDatabaseGame(popupView, dialog, context, activity, gameSagaDatabaseRecyclerAdapter));
    }

    public static void onClickAddDatabaseGame(View popupView, Dialog dialog, Context context, Activity activity, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {

        AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
        String name = Utility.checkIfEditTextIsNull(nameText);
        if (name.isEmpty()) {
            nameText.setError(context.getResources().getString(R.string.notEmpty));
        }

        AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
        String saga = Utility.checkIfEditTextIsNull(sagaText);
        if (saga.isEmpty()) {
            sagaText.setError(context.getResources().getString(R.string.notEmpty));
        }

        Spinner platformSpinner = popupView.findViewById(R.id.console_spinner);
        String platform = Utility.checkIfSpinnerIsNull(platformSpinner);
        if (platform.isEmpty()) {
            ((TextView) platformSpinner.getSelectedView()).setError(context.getResources().getString(R.string.notNotSelected));
        }

        Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
        String priority = Utility.checkIfSpinnerIsNull(prioritySpinner);
        if (priority.isEmpty()) {
            ((TextView) prioritySpinner.getSelectedView()).setError(context.getResources().getString(R.string.notNotSelected));
        }

        CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
        CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

        if (!name.isEmpty() && !saga.isEmpty() && !platform.isEmpty() && !priority.isEmpty()) {
            DatabaseGame databaseGame = new DatabaseGame(name, platform, priority, finishedCheckbox.isChecked(), transitCheckbox.isChecked());
            insertNewGameDatabaseDBAndCode(databaseGame, saga, activity, gameSagaDatabaseRecyclerAdapter);
        }
        dialog.dismiss();
    }

    public static void onClickPopupDismiss(SagheDatabaseGame sagheDatabaseGame, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {
        insertNewGameSagaDatabaseDBAndCode(sagheDatabaseGame, gameSagaDatabaseRecyclerAdapter);
        gameSagaDatabaseRecyclerAdapter.insertItem(sagheDatabaseGame);
    }

    public static void onClickOnlyRemove(SagheDatabaseGame sagheDatabaseGame, int position, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter) {
        removedItemFromDatabaseDBAndCode(Constants.DATABASEDB, sagheDatabaseGame.getId());
        gameSagaDatabaseRecyclerAdapter.removeItem(position, sagheDatabaseGame);
    }

    public static void onClickSearch(View view, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter){
        ImageView orderButton = view.findViewById(R.id.sort_button);
        orderButton.setVisibility(View.INVISIBLE);
        AppCompatButton returnButton = view.findViewById(R.id.return_button);
        returnButton.setVisibility(View.VISIBLE);
        ImageView closeButton = view.findViewById(R.id.close_button);
        closeButton.setVisibility(View.INVISIBLE);
        returnButton.setLayoutParams(closeButton.getLayoutParams());
        view.findViewById(R.id.search_button).setLayoutParams(orderButton.getLayoutParams());
        TextInputLayout textInputLayout = view.findViewById(R.id.search_input);
        textInputLayout.setVisibility(View.VISIBLE);

        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<SagheDatabaseGame> filterList = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(textInputLayout.getEditText().getText().toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
                gameSagaDatabaseRecyclerAdapter.updateData(filterList);            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public static void swipeSagheDatabase(Context context, int id, RecyclerView.ViewHolder viewHolder, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter){
        Dialog dialog = new Dialog(context);
        SagheDatabaseGame sagheDatabaseGame = Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition());
        View popupView = createPopUp(id, context, dialog);
        TextView textView = popupView.findViewById(R.id.edit_text);
        String newText = "Rimuovere " + Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition()).getName() + "?";
        textView.setText(newText);
        ImageButton removeButton = popupView.findViewById(R.id.delete_button);
        removeButton.setOnClickListener(view -> {
            DatabaseUtility.onClickOnlyRemove(Constants.getGameSagheDatabaseList().get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition(), gameSagaDatabaseRecyclerAdapter);
            dialog.dismiss();
        });
        ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
        nullifyActionButton.setOnClickListener(view -> {
            DatabaseUtility.onClickPopupDismiss(sagheDatabaseGame, gameSagaDatabaseRecyclerAdapter);
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private static View createPopUp(int id, Context context, Dialog dialog) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

}
