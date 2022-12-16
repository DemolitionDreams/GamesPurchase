package com.gamespurchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.entities.SerieGame;

import java.util.List;

public class GameSerieRecyclerAdapter extends RecyclerView.Adapter<GameSerieRecyclerAdapter.ViewHolder> {

    Context context;
    List<SerieGame> gameSerieList;

    public GameSerieRecyclerAdapter(List<SerieGame> gameSerieList, Context context){
        this.gameSerieList = gameSerieList;
        this.context = context;
    }
    @NonNull
    @Override
    public GameSerieRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_serie_view, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSerieRecyclerAdapter.ViewHolder holder, int position) {
        holder.serie_name.setText(gameSerieList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return gameSerieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView serie_name;
        ImageView serie_image;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            serie_name = itemView.findViewById(R.id.serie_name);
            serie_image = itemView.findViewById(R.id.serie_image);
        }
    }
}
