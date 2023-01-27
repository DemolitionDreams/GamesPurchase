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
import com.gamespurchase.entities.ProgressGame;
import com.gamespurchase.entities.SagheDatabaseGame;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    AssetManager assetManager;
    Workbook workbook;

    private void uploadExcelData() {

        InputStream inputStream = null;
        POIFSFileSystem fileSystem;
        assetManager = this.getAssets();
        String filename = "Collezioneg.xls";
        Queries queries = new Queries();

        try {
            inputStream = assetManager.open(filename);
            fileSystem = new POIFSFileSystem(inputStream);
            workbook = new HSSFWorkbook(fileSystem);
        } catch (Exception e) {

            Log.e("GamesPurchase", "Trovata eccezione: {}", e);
        }

        List<ProgressGame> progressGameList = new ArrayList<>();

        // Recupera il foglio con indice scelto
        Sheet sheet = workbook.getSheetAt(0);
        int counter = 0;
        // Carica nel workbook le righe e le colonne
        for (Row row : sheet) {

            ProgressGame pg = new ProgressGame();
            try {

                pg.setId(String.valueOf(counter));
                pg.setName(row.getCell(0).getStringCellValue());
                Log.i("GamesPurchase", "Nome " + pg.getName());
                pg.setLabel(row.getCell(1).getStringCellValue());
                pg.setCurrentProgress((int) row.getCell(2).getNumericCellValue());
                pg.setTotal((int) row.getCell(3).getNumericCellValue());
                pg.setHour((int) row.getCell(4).getNumericCellValue());
                pg.setStartDate(row.getCell(5).getStringCellValue());
                pg.setSaga(row.getCell(6).getStringCellValue());
                pg.setPlatform(row.getCell(7).getStringCellValue());
                pg.setPriority(row.getCell(8).getStringCellValue());
                pg.setBuyed(row.getCell(9).getNumericCellValue() == 1);
                pg.setCheckInTransit(row.getCell(10).getNumericCellValue() == 1);
            } catch (Exception e) {
                break;
            }
            //Queries.insertUpdateProgressDB(pg);
            counter++;
            progressGameList.add(pg);
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
        List<ProgressGame> startGameList = Queries.selectProgressDB("id");
        Constants.setActualLabelGameProgressList(startGameList);
        //List<BuyGame> buyGameList = Queries.selectBuyDB("id");
        //Constants.setGameBuyList(buyGameList);
        List<SagheDatabaseGame> sagheDatabaseGameList = Queries.selectDatabaseDB("name");
        Constants.setGameSagheDatabaseList(sagheDatabaseGameList);
        //fillMaps(Constants.getCounterBuyGame(), Constants.getCounterDatabaseGame());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Constants.maxIdProgressList = Integer.parseInt(startGameList.stream().max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
            //Constants.maxIdBuyList = Integer.parseInt(buyGameList.stream().max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
            if (!sagheDatabaseGameList.isEmpty()) {
                Constants.maxIdDatabaseList = Integer.parseInt(sagheDatabaseGameList.stream().max(Comparator.comparingInt(x -> Integer.parseInt(x.getId()))).get().getId());
            } else {
                Constants.maxIdProgressList = 0;
            }
        }, 3000);
    }

    private static void fillMaps(HashMap<String, Integer> counterBuyGame, HashMap<String, Integer> counterDatabaseGame) {

        counterBuyGame.put("Totale", 0);
        counterBuyGame.put("Transito", 0);
        counterBuyGame.put("HIGH", 0);
        counterBuyGame.put("MEDIUM", 0);
        counterBuyGame.put("LOW", 0);

        counterDatabaseGame.put("Totale", 0);
        counterDatabaseGame.put("Digitali", 0);
        counterDatabaseGame.put("Finiti", 0);
    }

    /*
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
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setGlobalVariables();
    }

    public void callSagheActivity(View view) {
        Intent intent = new Intent(this, SagheActivity.class);
        startActivity(intent);
    }

    public void callScheduleActivity(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void callStartActivity(View view) {
        Intent intent = new Intent(this, ProgressActivity.class);
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



