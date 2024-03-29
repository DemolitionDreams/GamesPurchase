package com.gamespurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.gamespurchase.activities.ProgressActivity;
import com.gamespurchase.activities.ScheduleActivity;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.utilities.RecyclerAdapterUtility;
import com.gamespurchase.utilities.Utility;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GameProgressRecyclerAdapter extends RecyclerView.Adapter<GameProgressRecyclerAdapter.ProgressViewHolder> {

    Dialog dialog;
    public Context context;
    public List<ProgressGame> progressGameList;

    public GameProgressRecyclerAdapter(List<ProgressGame> progressGameList, Context context, Dialog dialog){
        this.progressGameList = progressGameList;
        this.context = context;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_progress_view, parent, false);
        TextView nameText = relativeLayout.findViewById(R.id.name_text);
        TextView hourText = relativeLayout.findViewById(R.id.hour_text);
        ImageView priorityImage = relativeLayout.findViewById(R.id.priority_image);
        ImageView buyImage = relativeLayout.findViewById(R.id.buy_image);


        return new ProgressViewHolder(relativeLayout, nameText, hourText, priorityImage, buyImage);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        holder.relativeLayout.setOnLongClickListener(view -> {
            int p = holder.getAdapterPosition();
            View popupView = Utility.createPopUp(R.layout.popup_progress_game, context, dialog);
            AutoCompleteTextView nameText = popupView.findViewById(R.id.name_text);
            AutoCompleteTextView sagaText = popupView.findViewById(R.id.saga_text);
            Spinner consoleSpinner = popupView.findViewById(R.id.console_spinner);
            Spinner prioritySpinner = popupView.findViewById(R.id.priority_spinner);
            Spinner labelSpinner = popupView.findViewById(R.id.label_spinner);
            TextView dataText = popupView.findViewById(R.id.data_edit_text);
            TextView actualText = popupView.findViewById(R.id.actual_edit_text);
            TextView totalText = popupView.findViewById(R.id.total_edit_text);
            TextView hourText = popupView.findViewById(R.id.hour_edit_text);
            CheckBox buyCheckbox = popupView.findViewById(R.id.buyed_checkbox);
            CheckBox transitCheckbox = popupView.findViewById(R.id.transit_checkbox);
            Log.i("GamesPurchase", "HolderPosition 1: " + p);
            nameText.setText(progressGameList.get(holder.getAdapterPosition()).getName());
            sagaText.setText(progressGameList.get(holder.getAdapterPosition()).getSaga());
            int consolePosition = Arrays.stream(context.getResources().getStringArray(R.array.Console)).collect(Collectors.toList()).indexOf(progressGameList.get(holder.getAdapterPosition()).getPlatform());
            consoleSpinner.setSelection(consolePosition);
            int priorityPosition = Arrays.stream(context.getResources().getStringArray(R.array.Priority)).collect(Collectors.toList()).indexOf(progressGameList.get(holder.getAdapterPosition()).getPriority());
            prioritySpinner.setSelection(priorityPosition);
            int labelPosition = Arrays.stream(context.getResources().getStringArray(R.array.Label)).collect(Collectors.toList()).indexOf(progressGameList.get(holder.getAdapterPosition()).getLabel());
            labelSpinner.setSelection(labelPosition);
            dataText.setText(progressGameList.get(holder.getAdapterPosition()).getStartDate());
            actualText.setText(String.valueOf(progressGameList.get(holder.getAdapterPosition()).getCurrentProgress()));
            totalText.setText(String.valueOf(progressGameList.get(holder.getAdapterPosition()).getTotal()));
            hourText.setText(String.valueOf(progressGameList.get(holder.getAdapterPosition()).getHour()));

            buyCheckbox.setChecked(progressGameList.get(holder.getAdapterPosition()).getBuyed());
            transitCheckbox.setChecked(progressGameList.get(holder.getAdapterPosition()).getCheckInTransit());
            ImageButton updateButton = popupView.findViewById(R.id.update_button);
            updateButton.setOnClickListener(v -> {
                Log.i("GamesPurchase", "HolderPosition 2: " + p);

                String oldName = progressGameList.get(p).getName();
                String oldLabel = progressGameList.get(p).getLabel();
                ProgressGame progressGame = new ProgressGame(Integer.parseInt(actualText.getText().toString()), Integer.parseInt(totalText.getText().toString()), Integer.parseInt(hourText.getText().toString()), dataText.getText().toString(), nameText.getText().toString(), sagaText.getText().toString(), consoleSpinner.getSelectedItem().toString(), prioritySpinner.getSelectedItem().toString(), labelSpinner.getSelectedItem().toString(), buyCheckbox.isChecked(), transitCheckbox.isChecked());
                ProgressActivity.insertNewGameStartDBAndCode(progressGame, oldName, this, false);
                RecyclerAdapterUtility.updateItemAt(ProgressGame::getId, progressGameList, Constants.getActualLabelProgressGameList(), this, p, progressGame);
                if(!oldLabel.equals(progressGame.getLabel())) {
                    RecyclerAdapterUtility.removeItem(progressGameList, Constants.getActualLabelProgressGameList(), this, p, progressGame);
                    Constants.getProgressGameMap().get(progressGame.getLabel()).add(progressGame);
                    ScheduleActivity.fillAllLabelFromLabelMap();
                }
                dialog.dismiss();
            });
            actualText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(totalText.getText()) && !TextUtils.isEmpty(actualText.getText()) && !totalText.getText().toString().equals("0")) {
                        if (Integer.parseInt(actualText.getText().toString()) >= Integer.parseInt(totalText.getText().toString())) {
                            actualText.setText(String.valueOf(Integer.parseInt(totalText.getText().toString()) - 1));
                        }
                    }
                }
            });
            totalText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!TextUtils.isEmpty(totalText.getText()) && !TextUtils.isEmpty(actualText.getText())) {
                        if (Integer.parseInt(totalText.getText().toString()) <= Integer.parseInt(actualText.getText().toString())) {
                            totalText.setText(String.valueOf(Integer.parseInt(actualText.getText().toString()) + 1));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            dialog.show();
            return true;
        });

        holder.nameText.setText(progressGameList.get(holder.getAdapterPosition()).getName());
        holder.hourText.setText(String.valueOf(progressGameList.get(holder.getAdapterPosition()).getHour()));
        String imagePriorityResource = "com.gamespurchase:drawable/icon_" + progressGameList.get(holder.getAdapterPosition()).getPriority().toLowerCase(Locale.ROOT);
        int idPriorityResource = context.getResources().getIdentifier(imagePriorityResource, null, null);
        holder.priorityImage.setImageResource(idPriorityResource);

        holder.buyImage.setImageResource(context.getResources().getIdentifier(progressGameList.get(holder.getAdapterPosition()).getBuyed() ? "com.gamespurchase:drawable/icon_buyed" : "com.gamespurchase:drawable/icon_not_buyed", null, null));
    }

    @Override
    public int getItemCount() {

        return progressGameList == null ? 0 : progressGameList.size();
    }

    protected static final class ProgressViewHolder extends RecyclerView.ViewHolder{

        private final RelativeLayout relativeLayout;
        private final TextView nameText;
        private final TextView hourText;
        private final ImageView priorityImage;
        private final ImageView buyImage;


        public ProgressViewHolder(@NotNull RelativeLayout relativeLayout, TextView nameText, TextView hourText, ImageView priorityImage, ImageView buyImage){

            super(relativeLayout);
            this.nameText = nameText;
            this.hourText = hourText;
            this.priorityImage = priorityImage;
            this.buyImage = buyImage;
            this.relativeLayout = relativeLayout;
        }
    }
}
