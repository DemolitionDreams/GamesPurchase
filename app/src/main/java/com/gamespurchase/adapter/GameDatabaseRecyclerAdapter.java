package com.gamespurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.DatabaseActivity;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.entities.DatabaseGame;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GameDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameDatabaseRecyclerAdapter.DatabaseViewHolder> {

    Dialog dialog;
    Context context;
    Queries queries;
    View databaseActivityView;
    List<DatabaseGame> gameDatabaseList;

    public GameDatabaseRecyclerAdapter(List<DatabaseGame> gameDatabaseList, Context context, View databaseActivityView){
        this.gameDatabaseList = gameDatabaseList;
        this.context = context;
        this.databaseActivityView = databaseActivityView;
    }

    public List<DatabaseGame> getGameDatabaseList() {
        return gameDatabaseList;
    }

    @NonNull
    @Override
    public DatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_database_view, parent, false);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        ImageView sagaImage = relativeLayout.findViewById(R.id.saga_image);
        ImageView platformImage = relativeLayout.findViewById(R.id.platform_image);
        ImageView finishedImage = relativeLayout.findViewById(R.id.finished_image);
        ImageView notFinishedImage = relativeLayout.findViewById(R.id.not_finished_image);

        return new DatabaseViewHolder(relativeLayout, nameText, sagaImage, platformImage, finishedImage, notFinishedImage);
    }

    @Override
    public void onBindViewHolder(@NonNull DatabaseViewHolder holder, int position) {

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
        });

        holder.nameText.setText(gameDatabaseList.get(holder.getAdapterPosition()).getName());

        String saga = gameDatabaseList.get(holder.getAdapterPosition()).getSaga().toLowerCase(Locale.ROOT);
        String imageSagaResource = "com.gamespurchase:drawable/icon_" + saga;
        imageSagaResource = imageSagaResource.replace(" ", "_");
        if(saga.equals("pokémon")){
            imageSagaResource = "com.gamespurchase:drawable/icon_pokemon";
        }
        if(saga.equals("assassin's creed")){
            imageSagaResource = "com.gamespurchase:drawable/icon_assassins_creed";
        }
        if(saga.equals("asterix & obelix")){
            imageSagaResource = "com.gamespurchase:drawable/icon_asterix_e_obelix";
        }
        int idSagaResource = context.getResources().getIdentifier(imageSagaResource, null, null);
        holder.sagaImage.setImageResource(idSagaResource);

        String imagePlatformResource = "com.gamespurchase:drawable/icon_" + gameDatabaseList.get(holder.getAdapterPosition()).getPlatform().toLowerCase(Locale.ROOT);
        int idPlatformResource = context.getResources().getIdentifier(imagePlatformResource, null, null);
        holder.platformImage.setImageResource(idPlatformResource);

        if(gameDatabaseList.get(holder.getAdapterPosition()).getFinished()){
            holder.finishedImage.setVisibility(View.VISIBLE);
            holder.notFinishedImage.setVisibility(View.INVISIBLE);
        } else{
            holder.notFinishedImage.setVisibility(View.VISIBLE);
            holder.finishedImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return gameDatabaseList.size();
    }

    protected static final class DatabaseViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView nameText;
        private ImageView sagaImage;
        private ImageView platformImage;
        private ImageView finishedImage;
        private ImageView notFinishedImage;

        public DatabaseViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, ImageView sagaImage, ImageView platformImage, ImageView finishedImage, ImageView notFinishedImage){

            super(relativeLayout);
            this.nameText = nameText;
            this.sagaImage = sagaImage;
            this.platformImage = platformImage;
            this.finishedImage = finishedImage;
            this.notFinishedImage = notFinishedImage;
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
}


