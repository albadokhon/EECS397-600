package com.weka;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Created by aab119 on 9/21/2015.
 */
public class Convert2Arff {

    public static void CSV2ARFF() throws Exception {
        //CSV2Arff <input.csv> <output.arff>
        String csvDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV/";
        String csv2ArffDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV2ARFF/";
        File csvFolder = new File(csvDir);
        File[] allCsvAvailable = csvFolder.listFiles();
/*        if (allCsvAvailable.length == 0) { // AB check if there was no files in the directory
            Context context = getApplicationContext();
            CharSequence text = "Please go to Tutorial 1 App and test some patterns to generate data...";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration); //AB CB HW1 just a simple pop-out msg for the user
            toast.show();
            return;
        }*/
        //Thread thread = new Thread() {
        //    public void run() {}
        //};
        for (File csvFile : allCsvAvailable) { // AB This loop will convert all CSV files in /CSV and saves them into /CSV2ARFF
            if (csvFile.isFile() && csvFile.getName().endsWith(".csv")) {
                try {
                    // load CSV
                    CSVLoader loader = new CSVLoader();
                    loader.setSource(new File(csvDir+csvFile.getName()));
                    Instances data;
                    if (loader != null)
                        data = loader.getDataSet();
                    else continue;
                    // save ARFF
                    String arffOut = csvFile.getName().replaceFirst("[.][^.]+$", ""); // AB This line will remove the extension of ".csv"
                    ArffSaver saver = new ArffSaver();
                    saver.setInstances(data);
                    saver.setFile(new File(csv2ArffDir + arffOut + ".arff"));
                    //saver.setDestination(new File( csv2ArffDir+arffOut+".arff" ));
                    saver.writeBatch();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void CSV2ARFF(int hw) throws Exception {
        //CSV2Arff <input.csv> <output.arff>
        String csvDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV/";
        String csv2ArffDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV2ARFF/";
        String csvhw4 = "AnalysisDataHW4.csv";
        // load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(csvDir+csvhw4));
        Thread.sleep(1000);
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        String arffOut = csvhw4.replaceFirst("[.][^.]+$", ""); // AB This line will remove the extension of ".csv"
        saver.setFile(new File(csv2ArffDir+arffOut));
        //saver.setDestination(new File(args[1]));
        saver.writeBatch();
    }
}
