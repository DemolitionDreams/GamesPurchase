package com.gamespurchase.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.GameSagheRecyclerAdapter;
import com.gamespurchase.entities.SagheGame;
import com.gamespurchase.entities.SerieGame;

import java.util.ArrayList;
import java.util.List;

public class SagheActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    List<String> games = new ArrayList<>();
    List<String> games2 = new ArrayList<>();
    List<String> games3 = new ArrayList<>();
    List<SagheGame> sagheGameList = new ArrayList<>();
    List<SerieGame> serieGameList = new ArrayList<>();
    List<SerieGame> serieGameList2 = new ArrayList<>();
    List<SerieGame> serieGameList3 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saghe);

        getSupportActionBar().hide();

        games.add("Horizon ZD");
        games.add("Horizon FW");

        games2.add("God of War");
        games2.add("God of War II");
        games2.add("God of War II");

        games3.add("TLOU");
        games3.add("TLOU 2");

        recyclerView.findViewById(R.id.parent_recycler_view);

        serieGameList.add(new SerieGame("Horizon", games));
        serieGameList2.add(new SerieGame("GOW", games2));
        serieGameList3.add(new SerieGame("The Last of Us", games3));

        sagheGameList.add(new SagheGame("Ultimi", serieGameList));
        sagheGameList.add(new SagheGame("Primi", serieGameList2));
        sagheGameList.add(new SagheGame("Secondi", serieGameList3));

        GameSagheRecyclerAdapter gameSagheRecyclerAdapter = new GameSagheRecyclerAdapter(sagheGameList, SagheActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gameSagheRecyclerAdapter);
        gameSagheRecyclerAdapter.notifyDataSetChanged();
    }
}