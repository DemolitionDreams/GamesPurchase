package com.gamespurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.entities.DatabaseGame;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class GameDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameDatabaseRecyclerAdapter.DatabaseViewHolder> {

    Dialog dialog;
    Context context;
    List<DatabaseGame> gameDatabaseList;

    public GameDatabaseRecyclerAdapter(List<DatabaseGame> gameDatabaseList, Context context){
        this.gameDatabaseList = gameDatabaseList;
        this.context = context;
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

        /*holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View popupView = createPopUp(R.layout.popup_database_game);
//                databaseActivity.addAutoCompleteVoice(popupView, R.id.saga_text);
                AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
                AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
                Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
                CheckBox finishedCheckbox = popupView.findViewById(R.id.finished_checkbox);

                nameText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getName());
                sagaText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getSaga());
                int position = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(gameDatabaseList.get(holder.getAdapterPosition()).getPlatform());
                consoleSpinner.setSelection(position);
                finishedCheckbox.setChecked(gameDatabaseList.get(holder.getAdapterPosition()).getFinished());

                TextView editTextView = popupView.findViewById(R.id.add_text_view);
                editTextView.setText("Modifica");
                ImageButton editButton = popupView.findViewById(R.id.add_button);
                editButton.setOnClickListener(v -> {
                    DatabaseGame databaseGame = new DatabaseGame();
                    databaseGame.setId(gameDatabaseList.get(holder.getAdapterPosition()).getId());
                    databaseGame.setName(nameText.getText().toString());
                    databaseGame.setSaga(sagaText.getText().toString());
                    databaseGame.setPlatform(consoleSpinner.getSelectedItem().toString());
                    databaseGame.setFinished(finishedCheckbox.isChecked());
                    DatabaseActivity.insertNewGameDatabaseDBAndCode(holder.getAdapterPosition(), databaseGame, databaseActivityView);
                    dialog.dismiss();
                    notifyItemChanged(holder.getAdapterPosition());
                });

                dialog.show();
                return true;
            }
        });*/

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
        GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter = new GameDatabaseRecyclerAdapter(databaseGameList, context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameDatabaseRecyclerAdapter);
        //TODO: aggiungerlo
        //itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}


