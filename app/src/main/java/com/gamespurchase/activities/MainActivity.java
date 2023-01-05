package com.gamespurchase.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gamespurchase.R;
import com.gamespurchase.classes.Queries;
import com.gamespurchase.constant.Constants;
import com.gamespurchase.entities.BuyGame;
import com.gamespurchase.entities.DatabaseGame;
import com.google.android.gms.common.util.CollectionUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AssetManager assetManager;
    Workbook workbook;
    Queries queries = new Queries();

    private void uploadExcelData() {

        InputStream inputStream = null;
        POIFSFileSystem fileSystem;
        assetManager = this.getAssets();
        String filename = "Excel_Collezione_Old.xls";
        Queries queries = new Queries();

        try {
            inputStream = assetManager.open(filename);
            fileSystem = new POIFSFileSystem(inputStream);
            workbook = new HSSFWorkbook(fileSystem);
        } catch (Exception e) {

            Log.e("GamesPurchase", "Trovata eccezione: {}", e);
        }

        List<DatabaseGame> databaseGameList = new ArrayList<>();

        // Recupera il foglio con indice scelto
        Sheet sheet = workbook.getSheetAt(0);
        int counter = 0;
        // Carica nel workbook le righe e le colonne
        for (Row row : sheet) {

            DatabaseGame databaseGame = new DatabaseGame();

            databaseGame.setId(String.valueOf(counter));
            databaseGame.setName(row.getCell(0).getStringCellValue());
            databaseGame.setSaga(row.getCell(1).getStringCellValue());
            databaseGame.setPlatform(row.getCell(2).getStringCellValue());
            databaseGame.setFinished(row.getCell(3).getNumericCellValue() == 1);
            databaseGame.setBuyed(true);

            Queries.insertUpdateDatabaseDB(databaseGame);
            databaseGameList.add(databaseGame);

            counter++;
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {

            Log.e("GamesPurchase", "Errore nella chiusura del file");
        }
    }

    private String convertDoubleToString(double dou) {

        String temp = String.valueOf(dou);
        String[] arrayTemp = temp.split("[.]");
        return arrayTemp[0];
    }

    private void setGlobalVariables() {
        List<BuyGame> buyGameList = queries.selectBuyDB("id");
        Constants.setGameBuyList(buyGameList);
        List<DatabaseGame> databaseGameList = queries.selectDatabaseDB("id");
        Constants.setGameDatabaseList(databaseGameList);
        fillMaps(Constants.getCounterBuyGame(), Constants.getCounterDatabaseGame());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.maxIdBuyList = Integer.parseInt(buyGameList.stream().max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
            Constants.maxIdDatabaseList = Integer.parseInt(databaseGameList.stream().max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
        }, 3000);
    }

    private static void fillMaps(HashMap<String, Integer> counterBuyGame, HashMap<String, Integer> counterDatabaseGame){

        counterBuyGame.put("Totale", 0);
        counterBuyGame.put("Transito", 0);
        counterBuyGame.put("HIGH", 0);
        counterBuyGame.put("MEDIUM", 0);
        counterBuyGame.put("LOW", 0);

        counterDatabaseGame.put("Totale", 0);
        counterDatabaseGame.put("Digitali", 0);
        counterDatabaseGame.put("Finiti", 0);
    }

    public static void count(List<BuyGame> buyGameList, List<DatabaseGame> databaseGameList) {

        fillMaps(Constants.getCounterBuyGame(), Constants.getCounterDatabaseGame());

        if(!CollectionUtils.isEmpty(buyGameList)){
            buyGameList.stream().forEach(x -> {

                Constants.getCounterBuyGame().put("Totale", Constants.getCounterBuyGame().get("Totale")+1);
                if(Constants.getCounterBuyGame().containsKey(x.getPriority())){
                    Constants.getCounterBuyGame().put(x.getPriority(), Constants.getCounterBuyGame().get(x.getPriority())+1);
                }
                if(x.getCheckInTransit()){
                    Constants.getCounterBuyGame().put("Transito", Constants.getCounterBuyGame().get("Transito")+1);
                }
            });
        }

        if(!CollectionUtils.isEmpty(databaseGameList)) {
            databaseGameList.stream().forEach(x -> {

                Constants.getCounterDatabaseGame().put("Totale", Constants.getCounterDatabaseGame().get("Totale") + 1);
                if (x.getFinished()) {
                    Constants.getCounterDatabaseGame().put("Finiti", Constants.getCounterDatabaseGame().get("Finiti") + 1);
                }
                if (x.getPlatform().equals("Digital")){
                    Constants.getCounterDatabaseGame().put("Digitali", Constants.getCounterDatabaseGame().get("Digitali") + 1);
                }
            });
        }
        Log.i("GamesPurchase", "HashMap Buy:" + Constants.getCounterBuyGame());
        Log.i("GamesPurchase", "HashMap Database:" + Constants.getCounterDatabaseGame());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        setGlobalVariables();
    }

    public void callSagheActivity(View view){
        Intent intent = new Intent(this, SagheActivity.class);
        startActivity(intent);
    }

    public void callScheduleActivity(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void callStartActivity(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    public void callDatabaseActivity(View view) {
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }

    public void callBuyActivity(View view) {
        Intent intent = new Intent(this, BuyActivity.class);
        startActivity(intent);
    }
}



