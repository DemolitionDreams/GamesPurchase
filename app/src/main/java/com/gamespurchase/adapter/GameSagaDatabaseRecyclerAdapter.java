package com.gamespurchase.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.BuyActivity;
import com.gamespurchase.activities.DatabaseActivity;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.utilities.DatabaseUtility;
import com.gamespurchase.utilities.Utility;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.firebase.database.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifImageView;

public class GameSagaDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameSagaDatabaseRecyclerAdapter.SagheDatabaseViewHolder> {

    Dialog dialog;
    Context context;
    Activity activity;
    public List<SagheDatabaseGame> gameSagheDatabaseList;

    public GameSagaDatabaseRecyclerAdapter(List<SagheDatabaseGame> gameSagheDatabaseList, Context context, Activity activity) {
        this.gameSagheDatabaseList = gameSagheDatabaseList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SagheDatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_saghe_database_view, parent, false);
        dialog = new Dialog(context);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        TextView actualText = relativeLayout.findViewById(R.id.actual_text);
        TextView totalText = relativeLayout.findViewById(R.id.total_text);

        ImageView sagaImage = relativeLayout.findViewById(R.id.saga_image);
        ImageView buyedImage = relativeLayout.findViewById(R.id.buyed_image);
        ImageView finishImage = relativeLayout.findViewById(R.id.finish_image);
        ImageView downImage = relativeLayout.findViewById(R.id.down_image);

        ImageView fireImage = relativeLayout.findViewById(R.id.image_fire);
        GifImageView fireGif = relativeLayout.findViewById(R.id.gif_fire);

        RecyclerView recyclerView = relativeLayout.findViewById(R.id.game_database);

        return new SagheDatabaseViewHolder(relativeLayout, nameText, actualText, totalText, sagaImage, buyedImage, finishImage, downImage, fireImage, fireGif, recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull SagheDatabaseViewHolder holder, int position) {
        GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter = this;

        holder.relativeLayout.setOnLongClickListener(view -> {
            View popupView = Utility.createPopUp(R.layout.popup_saghe_database_game, context, dialog);
            AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
            nameText.setText(gameSagheDatabaseList.get(holder.getAdapterPosition()).getName());
            AppCompatButton editButton = popupView.findViewById(R.id.add_button);
            int index = holder.getAdapterPosition();
            editButton.setOnClickListener(v -> {
                SagheDatabaseGame newSagheDatabaseGame = gameSagheDatabaseList.get(index);
                newSagheDatabaseGame.setName(nameText.getText().toString());
                DatabaseUtility.insertNewGameSagaDatabaseDBAndCode(newSagheDatabaseGame, gameSagaDatabaseRecyclerAdapter, Boolean.FALSE, index);
                dialog.dismiss();
                notifyItemChanged(index);
            });

            dialog.show();
            return true;
        });

        holder.nameText.setText(gameSagheDatabaseList.get(holder.getAdapterPosition()).getName());
        String saga = gameSagheDatabaseList.get(holder.getAdapterPosition()).getName().toLowerCase(Locale.ROOT);
        int idSagaResource = context.getResources().getIdentifier(getIdSagaResource(saga), null, null);
        holder.sagaImage.setImageResource(idSagaResource);
        List<DatabaseGame> buyGames = gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesBuy();
        List<DatabaseGame> notBuyGames = gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesNotBuy();

        int buyListSize = CollectionUtils.isEmpty(buyGames) ? 0 : buyGames.size();
        int notBuyListSize = CollectionUtils.isEmpty(notBuyGames) ? 0 : notBuyGames.size();

        holder.actualText.setText(String.valueOf(activity instanceof BuyActivity ? notBuyListSize : buyListSize));
        holder.totalText.setText(String.valueOf(buyListSize + notBuyListSize));
        int idBuyResource = context.getResources().getIdentifier(gameSagheDatabaseList.get(holder.getAdapterPosition()).getBuyAll() ? "com.gamespurchase:drawable/icon_buyed" : "com.gamespurchase:drawable/icon_not_buyed", null, null);
        holder.buyedImage.setImageResource(idBuyResource);
        int idFinishResource = context.getResources().getIdentifier(gameSagheDatabaseList.get(holder.getAdapterPosition()).getFinishAll() ? "com.gamespurchase:drawable/icon_finish_all" : "com.gamespurchase:drawable/icon_not_finish_all", null, null);
        holder.finishImage.setImageResource(idFinishResource);

        holder.downImage.setOnClickListener(view -> {
            if (holder.recyclerView.getVisibility() == View.VISIBLE) {
                holder.recyclerView.setVisibility(View.GONE);
                holder.downImage.setRotation(-90);
            } else {
                if((activity instanceof BuyActivity && !CollectionUtils.isEmpty(gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesNotBuy())) || (activity instanceof DatabaseActivity && !CollectionUtils.isEmpty(gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesBuy()))) {
                    createRecyclerAdapter(activity instanceof BuyActivity ? gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesNotBuy() : gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesBuy(), holder);
                }
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.downImage.setRotation(0);
            }
        });

        if(gameSagheDatabaseList.get(holder.getAdapterPosition()).getFinishAll() && gameSagheDatabaseList.get(holder.getAdapterPosition()).getBuyAll()) {
            holder.fireImage.setVisibility(View.INVISIBLE);
            holder.fireGif.setVisibility(View.VISIBLE);
        } else{
            holder.fireGif.setVisibility(View.INVISIBLE);
            holder.fireImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return gameSagheDatabaseList.size();
    }

    public static final class SagheDatabaseViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout relativeLayout;
        private final TextView nameText;
        private final TextView actualText;
        private final TextView totalText;
        private final ImageView sagaImage;
        private final ImageView buyedImage;
        private final ImageView finishImage;
        private final ImageView downImage;
        private final ImageView fireImage;
        private final GifImageView fireGif;
        private final RecyclerView recyclerView;

        public SagheDatabaseViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, TextView actualText, TextView totalText, ImageView sagaImage, ImageView buyedImage, ImageView finishImage, ImageView downImage, ImageView fireImage, GifImageView gifFire, RecyclerView recyclerView) {

            super(relativeLayout);
            this.nameText = nameText;
            this.actualText = actualText;
            this.totalText = totalText;
            this.sagaImage = sagaImage;
            this.buyedImage = buyedImage;
            this.finishImage = finishImage;
            this.downImage = downImage;
            this.fireImage = fireImage;
            this.fireGif = gifFire;
            this.relativeLayout = relativeLayout;
            this.recyclerView = recyclerView;
        }
    }

    private String getIdSagaResource(String saga) {
        String imageSagaResource = "com.gamespurchase:drawable/icon_" + saga;
        imageSagaResource = imageSagaResource.replace(" ", "_");
        if (saga.equals("pokémon")) {
            imageSagaResource = "com.gamespurchase:drawable/icon_pokemon";
        }
        if (saga.equals("assassin's creed")) {
            imageSagaResource = "com.gamespurchase:drawable/icon_assassins_creed";
        }
        if (saga.equals("asterix & obelix")) {
            imageSagaResource = "com.gamespurchase:drawable/icon_asterix_e_obelix";
        }
        return imageSagaResource;
    }

    private void createRecyclerAdapter(List<DatabaseGame> databaseGameList, SagheDatabaseViewHolder sagheDatabaseViewHolder) {
        RecyclerView recyclerView = sagheDatabaseViewHolder.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter = new GameDatabaseRecyclerAdapter(gameSagheDatabaseList.get(sagheDatabaseViewHolder.getAdapterPosition()), databaseGameList.stream().sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList()), this, context, activity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameDatabaseRecyclerAdapter);
    }
}


