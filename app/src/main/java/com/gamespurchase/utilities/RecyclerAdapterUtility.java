package com.gamespurchase.utilities;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.constant.Constants;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RecyclerAdapterUtility {

    @SuppressLint("NotifyDataSetChanged")
    public static <T, K extends RecyclerView.ViewHolder, V extends RecyclerView.Adapter<K>> void updateData(List<T> gameList, List<T> newGameList, V recyclerAdapter) {
        gameList.clear();
        gameList.addAll(newGameList);
        recyclerAdapter.notifyDataSetChanged();
    }

    public static <T, K extends RecyclerView.ViewHolder, V extends RecyclerView.Adapter<K>> void  insertItem(Comparator<T> getterToCompare, List<T> gameList, List<T> constantGameList, T game, V recyclerAdapter) {
        constantGameList.add(game);
        constantGameList.sort(getterToCompare);
        Log.i("GamesPurchase", "Dimensioni 5.5 " + Constants.getActualLabelProgressGameList().size());
        gameList.add(game);
        gameList.sort(getterToCompare);
        Log.i("GamesPurchase", "Dimensioni 5.6 " + Constants.getActualLabelProgressGameList().size());
        recyclerAdapter.notifyItemInserted(gameList.indexOf(game));
        Log.i("GamesPurchase", "Dimensioni 5.7 " + Constants.getActualLabelProgressGameList().size());
    }

    public static <T, K extends RecyclerView.ViewHolder, V extends RecyclerView.Adapter<K>> void  updateItemAt(Function<T, String> getter, List<T> gameList, List<T> constantGameList, V recyclerAdapter, int position, T game) {
        Optional<T> optGame = constantGameList.stream().filter(x -> getter.apply(x).equals(getter.apply(game))).findAny();
        if(optGame.isPresent()){
            int index = constantGameList.indexOf(optGame.get());
            constantGameList.set(index, game);
        }
        gameList.set(position, game);
        recyclerAdapter.notifyItemChanged(position);
    }

    public static <T, K extends RecyclerView.ViewHolder, V extends RecyclerView.Adapter<K>> void  removeItem(List<T> gameList, List<T> constantGameList, V recyclerAdapter, int position, T game) {
        constantGameList.remove(game);
        gameList.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
    }
}
