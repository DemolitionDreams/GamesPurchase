package com.gamespurchase.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.gamespurchase.utilities.DatabaseUtility;
import com.gamespurchase.utilities.Utility;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GameDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameDatabaseRecyclerAdapter.DatabaseViewHolder> {

    Dialog dialog;
    Context context;
    Activity activity;
    GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter;
    SagheDatabaseGame sagheDatabaseGame;
    List<DatabaseGame> gameDatabaseList;

    public GameDatabaseRecyclerAdapter(SagheDatabaseGame sagheDatabaseGame, List<DatabaseGame> gameDatabaseList, GameSagaDatabaseRecyclerAdapter gameSagaDatabaseRecyclerAdapter, Context context, Activity activity) {
        this.sagheDatabaseGame = sagheDatabaseGame;
        this.gameDatabaseList = gameDatabaseList;
        this.gameSagaDatabaseRecyclerAdapter = gameSagaDatabaseRecyclerAdapter;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public DatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_database_view, parent, false);
        dialog = new Dialog(context);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        ImageView platformImage = relativeLayout.findViewById(R.id.platform_image);
        ImageView finishedImage = relativeLayout.findViewById(R.id.finished_image);

        return new DatabaseViewHolder(relativeLayout, nameText, platformImage, finishedImage);
    }

    @Override
    public void onBindViewHolder(@NonNull DatabaseViewHolder holder, int position) {
        holder.relativeLayout.setOnLongClickListener(view -> {
            View popupView = Utility.createPopUp(R.layout.popup_database_game, context, dialog);
            AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
            AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
            Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
            Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
            CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
            CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

            nameText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getName());
            sagaText.setText(sagheDatabaseGame.getName());
            int consolePosition = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(gameDatabaseList.get(holder.getAdapterPosition()).getPlatform());
            consoleSpinner.setSelection(consolePosition);
            int priorityPosition = Arrays.stream(context.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(gameDatabaseList.get(holder.getAdapterPosition()).getPriority());
            prioritySpinner.setSelection(priorityPosition);
            finishedCheckbox.setChecked(gameDatabaseList.get(holder.getAdapterPosition()).getFinished());
            transitCheckbox.setChecked(gameDatabaseList.get(holder.getAdapterPosition()).getCheckInTransit());

            AppCompatButton editButton = popupView.findViewById(R.id.add_button);
            editButton.setText("Modifica");
            editButton.setOnClickListener(v -> {
                DatabaseGame databaseGame = new DatabaseGame(gameDatabaseList.get(holder.getAdapterPosition()).getId(), nameText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), finishedCheckbox.isChecked(), transitCheckbox.isChecked());
                DatabaseUtility.insertNewGameDatabaseDBAndCode(databaseGame, sagaText.getText().toString(), activity, gameSagaDatabaseRecyclerAdapter);
                if(!sagaText.getText().toString().equals(sagheDatabaseGame.getName())){
                    DatabaseUtility.removedDatabaseGameFromSaga(gameDatabaseList, databaseGame.getName());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
                dialog.dismiss();
            });
            dialog.show();
            return true;
        });

        holder.nameText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getName());

        String imagePlatformResource = "com.gamespurchase:drawable/icon_" + gameDatabaseList.get(holder.getAdapterPosition()).getPlatform().toLowerCase(Locale.ROOT);
        int idPlatformResource = context.getResources().getIdentifier(imagePlatformResource, null, null);
        holder.platformImage.setImageResource(idPlatformResource);

        int idFinishResource = context.getResources().getIdentifier(gameDatabaseList.get(holder.getAdapterPosition()).getFinished() ? "com.gamespurchase:drawable/icon_finished" : "com.gamespurchase:drawable/icon_not_finished", null, null);
        holder.finishedImage.setImageResource(idFinishResource);
    }

    @Override
    public int getItemCount() {

        return gameDatabaseList.size();
    }

    protected static final class DatabaseViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout relativeLayout;
        private final TextView nameText;
        private final ImageView platformImage;
        private final ImageView finishedImage;

        public DatabaseViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, ImageView platformImage, ImageView finishedImage) {

            super(relativeLayout);
            this.nameText = nameText;
            this.platformImage = platformImage;
            this.finishedImage = finishedImage;
            this.relativeLayout = relativeLayout;
        }
    }
}


