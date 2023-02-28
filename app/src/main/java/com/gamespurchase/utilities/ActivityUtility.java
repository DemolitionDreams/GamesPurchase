package com.gamespurchase.utilities;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.activities.ProgressActivity;
import com.gamespurchase.adapter.GameProgressRecyclerAdapter;
import com.gamespurchase.adapter.GameSagaDatabaseRecyclerAdapter;
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActivityUtility {

    public static <K, V extends RecyclerView.ViewHolder, T extends RecyclerView.Adapter<V>> void onClickSearch(View view, T recyclerAdapter, List<K> constantList, Function<K, String> getterToCompare, List<K> recyclerGameList){
        ImageView orderButton = view.findViewById(R.id.sort_button);
        orderButton.setVisibility(View.INVISIBLE);
        AppCompatButton returnButton = view.findViewById(R.id.return_button);
        returnButton.setVisibility(View.VISIBLE);
        ImageView closeButton = view.findViewById(R.id.close_button);
        closeButton.setVisibility(View.INVISIBLE);
        returnButton.setLayoutParams(closeButton.getLayoutParams());
        view.findViewById(R.id.search_button).setLayoutParams(orderButton.getLayoutParams());
        TextInputLayout textInputLayout = view.findViewById(R.id.search_input);
        textInputLayout.setVisibility(View.VISIBLE);

        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<K> filterList = constantList.stream().filter(x -> getterToCompare.apply(x).toLowerCase(Locale.ROOT).contains(textInputLayout.getEditText().getText().toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
                RecyclerAdapterUtility.updateData(recyclerGameList, filterList, recyclerAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public static <K, V extends RecyclerView.ViewHolder, T extends RecyclerView.Adapter<V>> void swipeGame(Context context, int id, RecyclerView.ViewHolder viewHolder, T recyclerAdapter, K game, String name, ViewGroup rootView){
        Dialog dialog = new Dialog(context);
        View popupView = Utility.createPopUp(id, context, dialog);
        TextView textView = popupView.findViewById(R.id.edit_text);
        String newText = "Rimuovere " + name + "?";
        textView.setText(newText);
        ImageButton removeButton = popupView.findViewById(R.id.delete_button);
        removeButton.setOnClickListener(view -> {
            if(game instanceof SagheDatabaseGame) {
                DatabaseUtility.onClickOnlyRemove((SagheDatabaseGame) game, viewHolder.getAdapterPosition(), (GameSagaDatabaseRecyclerAdapter) recyclerAdapter);
            } else if(game instanceof ProgressGame){
                ProgressActivity.onClickOnlyRemove((ProgressGame) game, viewHolder.getAdapterPosition(), (GameProgressRecyclerAdapter) recyclerAdapter, rootView);
            }
            dialog.dismiss();
        });
        ImageButton nullifyActionButton = popupView.findViewById(R.id.nullify_button);
        nullifyActionButton.setOnClickListener(view -> {
            if(game instanceof SagheDatabaseGame) {
                DatabaseUtility.onClickPopupDismiss((SagheDatabaseGame) game, (GameSagaDatabaseRecyclerAdapter) recyclerAdapter);
            } else if(game instanceof ProgressGame){
                ProgressActivity.onClickPopupDismiss((ProgressGame) game, (GameProgressRecyclerAdapter) recyclerAdapter);
            }
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}