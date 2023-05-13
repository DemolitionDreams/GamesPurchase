package com.gamespurchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.entities.SagheGame;

import java.util.List;

public class GameCompletedRecyclerAdapter extends RecyclerView.Adapter<GameCompletedRecyclerAdapter.ViewHolder> {

    Context context;
    List<SagheGame> gameSagheList;

    public GameCompletedRecyclerAdapter(List<SagheGame> gameSagheList, Context context){
        this.gameSagheList = gameSagheList;
        this.context = context;
    }

    @NonNull
    @Override
    public GameCompletedRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_saghe_view, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameCompletedRecyclerAdapter.ViewHolder holder, int position) {
        holder.saghe_name.setText(gameSagheList.get(position).getName());

        GameSerieRecyclerAdapter gameSerieRecyclerAdapter = new GameSerieRecyclerAdapter(gameSagheList.get(position).getSerie(), context);
        holder.serie_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.serie_recycler_view.setAdapter(gameSerieRecyclerAdapter);
        gameSerieRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return gameSagheList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView saghe_name;
        RecyclerView serie_recycler_view;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            saghe_name = itemView.findViewById(R.id.serie_name);
            serie_recycler_view = itemView.findViewById(R.id.serie_recycler_view);
        }
    }
}
