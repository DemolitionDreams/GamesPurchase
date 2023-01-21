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
import com.gamespurchase.adapter.GameBuyRecyclerAdapter;
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

public class BuyActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    GameBuyRecyclerAdapter gameBuyRecyclerAdapter;
    GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter;

    // Queries.DELETE in DatabaseActivity
    // Queries.INSERT SagheDatabaseGame in DatabaseActivity

    // Richiama Queries.INSERT
    public static void insertNewGameDatabaseDBAndCode(DatabaseGame databaseGame, String saga) {

        SagheDatabaseGame sagheDatabaseGame;
        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(saga)).findAny();
        if (optSagheGame.isPresent()) {
            DatabaseActivity.removedDatabaseGameFromSaga(optSagheGame.get(), databaseGame.getName());
            optSagheGame.get().getGamesNotBuy().add(databaseGame);
            optSagheGame.get().getGamesNotBuy().stream()
                    .sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList());
            sagheDatabaseGame = optSagheGame.get();
            Log.i("GamesPurchase", "Aggiunto " + databaseGame.getName() + " alla saga " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") con " + sagheDatabaseGame.getGamesBuy().size() + " giochi comprati");
        } else {
            Constants.maxIdDatabaseList++;
            List<DatabaseGame> buyGames = new ArrayList<>();
            List<DatabaseGame> notBuyGames = new ArrayList<>();
            notBuyGames.add(databaseGame);
            sagheDatabaseGame = new SagheDatabaseGame(String.valueOf(Constants.getMaxIdDatabaseList()), saga, Boolean.TRUE, databaseGame.getFinished() ? Boolean.TRUE : Boolean.FALSE, buyGames, notBuyGames);
            Log.i("GamesPurchase", "Inserita nuova saga: " + sagheDatabaseGame.getName() + "(ID = " + sagheDatabaseGame.getId() + ") e aggiunto " + databaseGame.getName() + " alla lista buyGames");
        }

        Boolean[] check = DatabaseActivity.checkBuyAllOrFinishAll(sagheDatabaseGame);
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

   /*
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
*/

    public void changeNotBuyGameinBuyGame(DatabaseGame databaseGame) {

        Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getGamesNotBuy().contains(databaseGame)).findAny();
        DatabaseActivity.insertNewGameDatabaseDBAndCode(databaseGame, optSagheGame.get().getName());
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
        DatabaseActivity.insertNewGameSagaDatabaseDBAndCode(sagheDatabaseGame);
        //gameBuyRecyclerAdapter.notifyItemChanged(position);
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

    /*
    public void onClickAddDatabaseDB(BuyGame buyGame, Boolean finished) {
        changeNotBuyGameinBuyGame(buyGame, finished);
        removedItemFromBuyDBAndCode(buyGame);
        finish();
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }*/

    public void onClickOnlyRemove(SagheDatabaseGame sagheDatabaseGame, int position) {
        DatabaseActivity.removedItemFromDatabaseDBAndCode(sagheDatabaseGame);
        gameSagaDatabaseRecyclerAdapter.notifyItemRemoved(position);
        dialog.dismiss();
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_buy);
        createRecyclerAdapter(Constants.getGameSagheDatabaseList());
        Constants.setSortDatabaseGame("DESC");
    }

    public void onClickClose(View view) {
        finish();
    }

    public void onClickOpenAddBuyGamePopup(View view) {

        View popupView = createPopUp(R.layout.popup_database_game);
        addAutoCompleteVoice(popupView, R.id.saga_text);
        setEntriesAndDefaultToSpinner(R.array.Console, R.layout.console_spinner_default_value, R.id.console_spinner, popupView);
        setEntriesAndDefaultToSpinner(R.array.Priority, R.layout.priority_spinner_default_value, R.id.priority_spinner, popupView);
        popupView.findViewById(R.id.add_button).setOnClickListener(v -> onClickAddDatabaseGame(popupView));
        dialog.show();
    }

    private void setEntriesAndDefaultToSpinner(int arrayString, int spinnerLayout, int spinner, View view) {
        List<String> itemList = Arrays.stream(getResources().getStringArray(arrayString)).collect(Collectors.toList());
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, spinnerLayout, itemList);
        Spinner consoleSpinner = view.findViewById(spinner);
        consoleSpinner.setAdapter(spinnerAdapter);
        consoleSpinner.setAdapter(new NothingSelectedSpinnerAdapter(spinnerAdapter, spinnerLayout, this));
    }

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

    private void createRecyclerAdapter(List<SagheDatabaseGame> sagheDatabaseGameList) {
        List<SagheDatabaseGame> noBuySagheDatabaseList = sagheDatabaseGameList.stream().filter(x -> !x.getGamesNotBuy().isEmpty()).collect(Collectors.toList());
        RecyclerView recyclerView = findViewById(R.id.game_saghe_buy);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameSagaDatabaseRecyclerAdapter = new GameSagaDatabaseRecyclerAdapter(noBuySagheDatabaseList, this, new BuyActivity(), null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameSagaDatabaseRecyclerAdapter);
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
        if (transitFilter.isChecked() && !notTransitFilter.isChecked()) {
            filterList = filterList.stream().filter(g -> g.getCheckInTransit().equals(true)).collect(Collectors.toList());
        } else if (notTransitFilter.isChecked() && !transitFilter.isChecked()) {
            filterList = filterList.stream().filter(g -> g.getCheckInTransit().equals(false)).collect(Collectors.toList());
        }
        createRecyclerAdapter(filterList);
        dialog.dismiss();

        return filterList;
    }*/

    private void setFilterButton() {
        ImageButton sortButton = findViewById(R.id.sort_button);
        if (Constants.sortBuyGame.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }

        sortButton.setOnClickListener(view -> {
            if (Constants.sortBuyGame.equals("ASC")) {
                setSortOrientation(sortButton, "ASC", "DESC", R.drawable.icon_sort_desc);
            } else if (Constants.sortBuyGame.equals("DESC")) {
                setSortOrientation(sortButton, "DESC", "ASC", R.drawable.icon_sort_asc);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        createRecyclerAdapter(Constants.getGameSagheDatabaseList().stream()
                .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
        setFilterButton();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}