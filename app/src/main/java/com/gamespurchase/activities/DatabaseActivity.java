package com.gamespurchase.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameSagaDatabaseRecyclerAdapter;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.utilities.CompareUtility;
import com.gamespurchase.utilities.DatabaseUtility;
import com.gamespurchase.utilities.Utility;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseActivity extends AppCompatActivity {

    Dialog dialog;
    ViewGroup rootView;
    GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter;

    public void onClickSearch(View view) {
        DatabaseUtility.onClickSearch(rootView, gameSagaDatabaseRecyclerAdapter);
    }

    public void onClickReturn(View view) {
        setContentView(R.layout.activity_database);
        gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList());
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
            DatabaseUtility.swipeSagheDatabase(DatabaseActivity.this, R.layout.popup_delete_database_game, viewHolder, gameSagaDatabaseRecyclerAdapter);
        }
    });

    public void onClickOpenAddDatabaseGamePopup(View view) {

        DatabaseUtility.onClickOpenAddDatabaseGamePopup(this, dialog, R.layout.popup_database_game, Constants.getGameSagheDatabaseList(), SagheDatabaseGame::getName, new DatabaseActivity(), gameSagaDatabaseRecyclerAdapter);
        dialog.show();
    }

    private void createRecyclerAdapter(List<SagheDatabaseGame> sagheDatabaseGameList) {
        List<SagheDatabaseGame> buySagheDatabaseList = sagheDatabaseGameList.stream().filter(x -> !x.getGamesBuy().isEmpty()).collect(Collectors.toList());
        gameSagaDatabaseRecyclerAdapter = new GameSagaDatabaseRecyclerAdapter(buySagheDatabaseList, this, new DatabaseActivity());
        Utility.createRecyclerAdapter(rootView, R.id.game_saghe_database, this, gameSagaDatabaseRecyclerAdapter, itemTouchHelper);
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

    }

    public void onClickPopupInfoShow(View view) {
        View popupView = createPopUp(R.layout.popup_info_saghe);
        TextView sagheTextView = popupView.findViewById(R.id.saghe_text_view);
        sagheTextView.setText(filterBySaga());
        dialog.show();
    }

    private String filterBySaga() {
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
        createRecyclerAdapter(Constants.getGameSagheDatabaseList().stream()
                .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
        Utility.setFilterButton(CompareUtility.comparatorOf(SagheDatabaseGame::getName, CompareUtility.Order.ASCENDING, CompareUtility.Nulls.LAST), rootView, Constants.sortDatabaseGame, Constants.getGameSagheDatabaseList());
        gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList());
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}