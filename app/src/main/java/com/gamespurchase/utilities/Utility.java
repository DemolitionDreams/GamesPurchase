package com.gamespurchase.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamespurchase.R;
import com.gamespurchase.adapter.NothingSelectedSpinnerAdapter;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utility {

    public static void removedItemFromDatabase(String nameDB, String idElement){
        Queries.deleteItemDB(nameDB, idElement);
    }

    public static String checkIfSpinnerIsNull(View popupView, int id, String errorMessage) {

        Spinner nameSpinner = popupView.findViewById(id);
        String stringSpinner = "";
        if (nameSpinner != null && nameSpinner.getSelectedItem() != null) {
            stringSpinner = nameSpinner.getSelectedItem().toString();
        }
        if (stringSpinner.isEmpty()) {
            ((TextView) Objects.requireNonNull(nameSpinner).getSelectedView()).setError(errorMessage);
        }
        return stringSpinner;
    }

    public static String checkIfEditTextIsNull(AutoCompleteTextView textInputEditText) {
        if (textInputEditText != null && textInputEditText.getText() != null) {
            return textInputEditText.getText().toString();
        } else {

            return "";
        }
    }

    public static String checkIfEditTextIsNull(EditText textInputEditText, String value) {
        if (textInputEditText != null && textInputEditText.getText() != null && !textInputEditText.getText().toString().equals("")) {
            return textInputEditText.getText().toString();
        } else {
            return value;
        }
    }

    public static void setEntriesAndDefaultToSpinner(Context context, int arrayString, int spinnerLayout, int spinner, View view) {
        List<String> itemList = Arrays.stream(context.getResources().getStringArray(arrayString)).collect(Collectors.toList());
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(context, spinnerLayout, itemList);
        Spinner consoleSpinner = view.findViewById(spinner);
        consoleSpinner.setAdapter(spinnerAdapter);
        consoleSpinner.setAdapter(new NothingSelectedSpinnerAdapter(spinnerAdapter, spinnerLayout, context));
    }

    public static <V> void addAutoCompleteVoice(Context context, View view, int id, List<V> gameList, Function<V, String> getterToCompare) {
        AutoCompleteTextView sagaAutoComplete = view.findViewById(id);
        List<String> autoCompleteVoice = new ArrayList<>();
        gameList.forEach(g -> {
            if (!autoCompleteVoice.contains(getterToCompare.apply(g))) {
                autoCompleteVoice.add(getterToCompare.apply(g));
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, autoCompleteVoice);
        sagaAutoComplete.setAdapter(arrayAdapter);
    }

    public static <V extends RecyclerView.ViewHolder, T extends RecyclerView.Adapter<V>> void createRecyclerAdapter(View view, int id, Context context, T adapter, ItemTouchHelper itemTouchHelper) {
        RecyclerView recyclerView = view.findViewById(id);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static <V> void setFilterButton(Comparator<V> comparator, View view, String sortConstant, List<V> gameList) {

        ImageButton sortButton = view.findViewById(R.id.sort_button);
        if (sortConstant.equals("ASC")) {
            sortButton.setImageResource(R.drawable.icon_sort_asc);
        } else {
            sortButton.setImageResource(R.drawable.icon_sort_desc);
        }

        sortButton.setOnClickListener(v -> {
            if (sortConstant.equals("ASC")) {
                setSortOrientation(comparator, gameList, sortButton, "ASC", R.drawable.icon_sort_desc);
            } else if (Constants.sortDatabaseGame.equals("DESC")) {
                setSortOrientation(comparator, gameList, sortButton, "DESC", R.drawable.icon_sort_asc);
            }
        });
    }

    public static <V> void setSortOrientation(Comparator<V> comparator, List<V> gameList, ImageButton sortButton, String oldOrientation, int id) {
        if (oldOrientation.equals("ASC")) {
            gameList.sort(comparator);
        } else {
            gameList.sort(comparator.reversed());
        }
        sortButton.setImageResource(id);
    }

    public static View createPopUp(int id, Context context, Dialog dialog) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(id, null);
        dialog.setContentView(popupView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return popupView;
    }

    public static <T> List<T> sortList(Function<T, String> getterFunction, List<T> listToSort, CompareUtility.Order order) {
        return listToSort.stream()
                .sorted((CompareUtility.comparatorOf(getterFunction,
                        order, CompareUtility.Nulls.LAST)))
                .collect(Collectors.toList());
    }
}
