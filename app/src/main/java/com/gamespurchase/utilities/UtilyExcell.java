package com.gamespurchase.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.gamespurchase.classes.Queries;
import com.gamespurchase.entities.ProgressGame;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UtilyExcell {

    AssetManager assetManager;
    Workbook workbook;

    private void uploadExcelData(Context context) {

        InputStream inputStream = null;
        POIFSFileSystem fileSystem;
        assetManager = context.getAssets();
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
                pg.setBuy(row.getCell(9).getNumericCellValue() == 1);
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
}
