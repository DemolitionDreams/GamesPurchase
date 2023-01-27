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

public class BuyActivity extends AppCompatActivity {

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
            DatabaseUtility.swipeSagheDatabase(BuyActivity.this, R.layout.popup_delete_database_game, viewHolder, gameSagaDatabaseRecyclerAdapter);
        }
    });

    public void onClickOpenAddBuyGamePopup(View view) {

        DatabaseUtility.onClickOpenAddDatabaseGamePopup(this, dialog, R.layout.popup_database_game, Constants.getGameSagheDatabaseList(), SagheDatabaseGame::getName, new BuyActivity(), gameSagaDatabaseRecyclerAdapter);
        dialog.show();
    }

    private void createRecyclerAdapter(List<SagheDatabaseGame> sagheDatabaseGameList) {
        List<SagheDatabaseGame> notBuySagheDatabaseList = sagheDatabaseGameList.stream().filter(x -> !x.getGamesNotBuy().isEmpty()).collect(Collectors.toList());
        gameSagaDatabaseRecyclerAdapter = new GameSagaDatabaseRecyclerAdapter(notBuySagheDatabaseList, this, new BuyActivity());
        Utility.createRecyclerAdapter(rootView, R.id.game_saghe_buy, this, gameSagaDatabaseRecyclerAdapter, itemTouchHelper);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        createRecyclerAdapter(Constants.getGameSagheDatabaseList().stream()
                .sorted(Comparator.comparing(SagheDatabaseGame::getName)).collect(Collectors.toList()));
        Utility.setFilterButton(CompareUtility.comparatorOf(SagheDatabaseGame::getName, CompareUtility.Order.ASCENDING, CompareUtility.Nulls.LAST), rootView, Constants.sortDatabaseGame, Constants.getGameSagheDatabaseList());
        gameSagaDatabaseRecyclerAdapter.updateData(Constants.getGameSagheDatabaseList());
        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}