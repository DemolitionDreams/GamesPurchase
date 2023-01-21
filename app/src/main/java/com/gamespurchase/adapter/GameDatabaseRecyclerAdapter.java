package com.gamespurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.BuyActivity;
import com.gamespurchase.activities.DatabaseActivity;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.DatabaseGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameDatabaseRecyclerAdapter.DatabaseViewHolder> {

    Dialog dialog;
    Context context;
    BuyActivity buyActivity;
    DatabaseActivity databaseActivity;
    List<DatabaseGame> gameDatabaseList;

    public GameDatabaseRecyclerAdapter(List<DatabaseGame> gameDatabaseList, Context context, BuyActivity buyActivity, DatabaseActivity databaseActivity){
        this.gameDatabaseList = gameDatabaseList;
        this.context = context;
        this.buyActivity = buyActivity;
        this.databaseActivity = databaseActivity;
    }

    @NonNull
    @Override
    public DatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_database_view, parent, false);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        ImageView platformImage = relativeLayout.findViewById(R.id.platform_image);
        ImageView finishedImage = relativeLayout.findViewById(R.id.finished_image);

        return new DatabaseViewHolder(relativeLayout, nameText, platformImage, finishedImage);
    }

    @Override
    public void onBindViewHolder(@NonNull DatabaseViewHolder holder, int position) {

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View popupView = createPopUp(R.layout.popup_database_game);
                AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
                AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
                Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
                Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
                CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);
                CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);

                nameText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getName());
                int consolePosition = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(gameDatabaseList.get(holder.getAdapterPosition()).getPlatform());
                consoleSpinner.setSelection(consolePosition);
                int priorityPosition = Arrays.stream(context.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(gameDatabaseList.get(holder.getAdapterPosition()).getPriority());
                prioritySpinner.setSelection(priorityPosition);
                finishedCheckbox.setChecked(gameDatabaseList.get(holder.getAdapterPosition()).getFinished());
                transitCheckbox.setChecked(gameDatabaseList.get(holder.getAdapterPosition()).getCheckInTransit());

                AppCompatButton editButton = popupView.findViewById(R.id.add_button);
                editButton.setText("Modifica");
                editButton.setOnClickListener(v -> {
                    Optional<SagheDatabaseGame> optSagheGame = Constants.getGameSagheDatabaseList().stream().filter(x -> x.getName().equals(sagaText.getText().toString())).findAny();
                    DatabaseGame databaseGame = new DatabaseGame(gameDatabaseList.get(holder.getAdapterPosition()).getId(), nameText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), finishedCheckbox.isChecked(), transitCheckbox.isChecked());
                    if(optSagheGame.isPresent()) {
                        if (databaseActivity == null) {
                            BuyActivity.insertNewGameDatabaseDBAndCode(databaseGame, optSagheGame.get().getName());
                        } else {
                            DatabaseActivity.insertNewGameDatabaseDBAndCode(databaseGame, optSagheGame.get().getName());
                        }
                    } else{

                    }
                    dialog.dismiss();
                    notifyItemChanged(holder.getAdapterPosition());
                });

                dialog.show();
                return true;
            }
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

    protected static final class DatabaseViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView nameText;
        private ImageView platformImage;
        private ImageView finishedImage;

        public DatabaseViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, ImageView platformImage, ImageView finishedImage){

            super(relativeLayout);
            this.nameText = nameText;
            this.platformImage = platformImage;
            this.finishedImage = finishedImage;
            this.relativeLayout = relativeLayout;
        }
    }

    private View createPopUp(int id) {
        
        dialog = new Dialog(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    public void createRecyclerAdapter(List<DatabaseGame> databaseGameList, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.game_database);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter = new GameDatabaseRecyclerAdapter(databaseGameList, context, null, null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameDatabaseRecyclerAdapter);
        //TODO: aggiungerlo
        //itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}


