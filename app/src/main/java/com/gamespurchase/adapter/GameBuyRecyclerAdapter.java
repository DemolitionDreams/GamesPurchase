package com.gamespurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.BuyActivity;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.Data;

public class GameBuyRecyclerAdapter extends RecyclerView.Adapter<GameBuyRecyclerAdapter.BuyViewHolder> {

    Dialog dialog;
    Context context;
    Queries queries;
    View buyActivityView;
    List<BuyGame> gameBuyList;

    public GameBuyRecyclerAdapter(List<BuyGame> gameBuyList, Context context, View buyActivityView){
        this.gameBuyList = gameBuyList;
        this.context = context;
        this.buyActivityView = buyActivityView;
    }

    public List<BuyGame> getGameBuyList() {
        return gameBuyList;
    }

    @NonNull
    @Override
    public BuyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_buy_view, parent, false);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        TextView sagaText = relativeLayout.findViewById(R.id.saga_text);
        ImageView platformImage = relativeLayout.findViewById(R.id.platform_image);
        ImageView priority1Image = relativeLayout.findViewById(R.id.priority1_image);
        ImageView priority2Image = relativeLayout.findViewById(R.id.priority2_image);
        ImageView priority3Image = relativeLayout.findViewById(R.id.priority3_image);
        ImageView transitImage = relativeLayout.findViewById(R.id.transit_image);
        ImageView notTransitImage = relativeLayout.findViewById(R.id.not_transit_image);

        return new BuyViewHolder(relativeLayout, nameText, sagaText, platformImage, priority1Image, priority2Image, priority3Image, transitImage, notTransitImage);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyViewHolder holder, int position) {

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View popupView = createPopUp(R.layout.popup_buy_game);
               // buyActivity.addAutoCompleteVoice(popupView, R.id.saga_text);
                AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
                AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
                Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
                Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
                CheckBox inTransitCheckbox = popupView.findViewById(R.id.transit_checkbox);

                nameText.setText(gameBuyList.get(holder.getAdapterPosition()).getName());
                sagaText.setText(gameBuyList.get(holder.getAdapterPosition()).getSaga());
                int consolePosition = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(gameBuyList.get(holder.getAdapterPosition()).getPlatform());
                consoleSpinner.setSelection(consolePosition);
                int priorityPosition = Arrays.stream(context.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(gameBuyList.get(holder.getAdapterPosition()).getPriority());
                prioritySpinner.setSelection(priorityPosition);
                inTransitCheckbox.setChecked(gameBuyList.get(holder.getAdapterPosition()).getCheckInTransit());

                TextView editTextView = popupView.findViewById(R.id.add_text_view);
                editTextView.setText("Modifica");
                ImageButton editButton = popupView.findViewById(R.id.add_button);
                editButton.setOnClickListener(v -> {
                    BuyGame buyGame = new BuyGame();
                    buyGame.setId(gameBuyList.get(holder.getAdapterPosition()).getId());
                    buyGame.setName(nameText.getText().toString());
                    buyGame.setSaga(sagaText.getText().toString());
                    buyGame.setPlatform(consoleSpinner.getSelectedItem().toString());
                    buyGame.setPriority(prioritySpinner.getSelectedItem().toString());
                    buyGame.setCheckInTransit(inTransitCheckbox.isChecked());
                    BuyActivity.insertNewGameBuyDBAndCode(holder.getAdapterPosition(), buyGame, buyActivityView);

                    dialog.dismiss();
                    notifyItemChanged(holder.getAdapterPosition());
                });

                dialog.show();
                return true;
            }
        });

        holder.nameText.setText(gameBuyList.get(holder.getAdapterPosition()).getName());
        holder.sagaText.setText(gameBuyList.get(holder.getAdapterPosition()).getSaga());

        String imagePlatformResource = "com.gamespurchase:drawable/icon_" + gameBuyList.get(holder.getAdapterPosition()).getPlatform().toLowerCase(Locale.ROOT);
        int id = context.getResources().getIdentifier(imagePlatformResource, null, null);
        holder.platformImage.setImageResource(id);

        holder.priority2Image.setVisibility(View.INVISIBLE);
        holder.priority3Image.setVisibility(View.INVISIBLE);

        if("MEDIUM".equals(gameBuyList.get(holder.getAdapterPosition()).getPriority())){
            holder.priority2Image.setVisibility(View.VISIBLE);
        }

        if("HIGH".equals(gameBuyList.get(holder.getAdapterPosition()).getPriority())){
            holder.priority2Image.setVisibility(View.VISIBLE);
            holder.priority3Image.setVisibility(View.VISIBLE);
        }

        if(gameBuyList.get(holder.getAdapterPosition()).getCheckInTransit().equals(Boolean.TRUE)){
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_shape_alternative));
            holder.transitImage.setVisibility(View.VISIBLE);
            holder.notTransitImage.setVisibility(View.INVISIBLE);
        } else{
            holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_shape));
            holder.transitImage.setVisibility(View.INVISIBLE);
            holder.notTransitImage.setVisibility(View.VISIBLE);
        }

        holder.transitImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                gameBuyList.get(holder.getAdapterPosition()).setCheckInTransit(Boolean.FALSE);
                BuyActivity.insertNewGameBuyDBAndCode(holder.getAdapterPosition(), gameBuyList.get(holder.getAdapterPosition()), buyActivityView);
                holder.notTransitImage.setVisibility(View.VISIBLE);
                holder.transitImage.setVisibility(View.INVISIBLE);
                holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_shape));

                return true;
            }
        });

        holder.notTransitImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                gameBuyList.get(holder.getAdapterPosition()).setCheckInTransit(Boolean.TRUE);
                BuyActivity.insertNewGameBuyDBAndCode(holder.getAdapterPosition(), gameBuyList.get(holder.getAdapterPosition()), buyActivityView);
                holder.notTransitImage.setVisibility(View.INVISIBLE);
                holder.transitImage.setVisibility(View.VISIBLE);
                holder.relativeLayout.setBackground(context.getResources().getDrawable(R.drawable.custom_shape_alternative));

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return gameBuyList.size();
    }

    protected static final class BuyViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView nameText;
        private TextView sagaText;
        private ImageView platformImage;
        private ImageView priority1Image;
        private ImageView priority2Image;
        private ImageView priority3Image;
        private ImageView transitImage;
        private ImageView notTransitImage;

        public BuyViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, TextView sagaText, ImageView platformImage, ImageView priority1Image, ImageView priority2Image, ImageView priority3Image, ImageView transitImage, ImageView notTransitImage){

            super(relativeLayout);
            this.nameText = nameText;
            this.sagaText = sagaText;
            this.platformImage = platformImage;
            this.priority1Image = priority1Image;
            this.priority2Image = priority2Image;
            this.priority3Image = priority3Image;
            this.relativeLayout = relativeLayout;
            this.transitImage = transitImage;
            this.notTransitImage = notTransitImage;
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





