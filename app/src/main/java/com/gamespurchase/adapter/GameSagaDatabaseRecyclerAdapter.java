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
import com.gamespurchase.entities.SagheDatabaseGame;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GameSagaDatabaseRecyclerAdapter extends RecyclerView.Adapter<GameSagaDatabaseRecyclerAdapter.SagheDatabaseViewHolder> {

    Dialog dialog;
    Context context;
    List<SagheDatabaseGame> gameSagheDatabaseList;
    List<GameDatabaseRecyclerAdapter> gameDatabaseRecyclerAdapterList;

    public GameSagaDatabaseRecyclerAdapter(List<SagheDatabaseGame> gameSagheDatabaseList, Context context){
        this.gameSagheDatabaseList = gameSagheDatabaseList;
        this.context = context;
    }

    @NonNull
    @Override
    public SagheDatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_saghe_database_view, parent, false);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        TextView actualText = relativeLayout.findViewById(R.id.actual_text);
        TextView totalText = relativeLayout.findViewById(R.id.total_text);

        ImageView sagaImage = relativeLayout.findViewById(R.id.saga_image);
        ImageView buyedImage = relativeLayout.findViewById(R.id.buyed_image);
        ImageView finishImage = relativeLayout.findViewById(R.id.finish_image);
        ImageView downImage = relativeLayout.findViewById(R.id.down_image);

        RecyclerView recyclerView = relativeLayout.findViewById(R.id.game_database);
        gameDatabaseRecyclerAdapterList = new ArrayList<>();

        return new SagheDatabaseViewHolder(relativeLayout, nameText, actualText, totalText, sagaImage, buyedImage, finishImage, downImage, recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull SagheDatabaseViewHolder holder, int position) {

       /* holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View popupView = createPopUp(R.layout.popup_database_game);
                databaseActivity.addAutoCompleteVoice(popupView, R.id.saga_text);
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

        holder.nameText.setText(gameSagheDatabaseList.get(holder.getAdapterPosition()).getName());
        String saga = gameSagheDatabaseList.get(holder.getAdapterPosition()).getName().toLowerCase(Locale.ROOT);
        int idSagaResource = context.getResources().getIdentifier(getIdSagaResource(saga), null, null);
        holder.sagaImage.setImageResource(idSagaResource);
        int idBuyResource = context.getResources().getIdentifier(gameSagheDatabaseList.get(holder.getAdapterPosition()).getBuyAll() ? "com.gamespurchase:drawable/icon_buyed" : "com.gamespurchase:drawable/icon_not_buyed", null, null);
        holder.buyedImage.setImageResource(idBuyResource);
        int idFinishResource = context.getResources().getIdentifier(gameSagheDatabaseList.get(holder.getAdapterPosition()).getFinishAll() ? "com.gamespurchase:drawable/icon_finished" : "com.gamespurchase:drawable/icon_not_finished", null, null);
        holder.finishImage.setImageResource(idFinishResource);

        holder.downImage.setOnClickListener(view -> {
            if(holder.recyclerView.getVisibility() == View.VISIBLE){
                holder.recyclerView.setVisibility(View.GONE);
                holder.downImage.setRotation(-90);
                //TODO: cambiare immagine
            } else {
                createRecyclerAdapter(gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesBuy(), holder);
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.downImage.setRotation(0);
                //TODO: cambiare immagine
            }
        });
    }

    public void updateData(List<SagheDatabaseGame> sagheDatabaseGameList) {
        gameSagheDatabaseList.clear();
        //createRecyclerAdapter(gameSagheDatabaseList.get(holder.getAdapterPosition()).getGamesBuy(), holder);

        for(int i = 0; i < gameDatabaseRecyclerAdapterList.size(); i++){
            gameDatabaseRecyclerAdapterList.get(i).updateData(sagheDatabaseGameList.get(i).getGamesBuy().stream()
                    .sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList()));
        }
        gameSagheDatabaseList.addAll(sagheDatabaseGameList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return gameSagheDatabaseList.size();
    }

    protected static final class SagheDatabaseViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView nameText;
        private TextView actualText;
        private TextView totalText;
        private ImageView sagaImage;
        private ImageView buyedImage;
        private ImageView finishImage;
        private ImageView downImage;
        private RecyclerView recyclerView;

        public SagheDatabaseViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, TextView actualText, TextView totalText, ImageView sagaImage, ImageView buyedImage, ImageView finishImage, ImageView downImage, RecyclerView recyclerView) {

            super(relativeLayout);
            this.nameText = nameText;
            this.actualText= actualText;
            this.totalText = totalText;
            this.sagaImage = sagaImage;
            this.buyedImage = buyedImage;
            this.finishImage = finishImage;
            this.downImage = downImage;
            this.relativeLayout = relativeLayout;
            this.recyclerView = recyclerView;
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

    private String getIdSagaResource(String saga) {
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
        return imageSagaResource;
    }

    private void createRecyclerAdapter(List<DatabaseGame> databaseGameList, SagheDatabaseViewHolder sagheDatabaseViewHolder) {
        RecyclerView recyclerView = sagheDatabaseViewHolder.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        GameDatabaseRecyclerAdapter gameDatabaseRecyclerAdapter = new GameDatabaseRecyclerAdapter(databaseGameList.stream().sorted(Comparator.comparing(DatabaseGame::getName)).collect(Collectors.toList()), context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(gameDatabaseRecyclerAdapter);
        gameDatabaseRecyclerAdapterList.add(sagheDatabaseViewHolder.getAdapterPosition(), gameDatabaseRecyclerAdapter);
    }
}


