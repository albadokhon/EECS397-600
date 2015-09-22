package com.weka;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

//import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

/**
 * Created by aab119 on 9/21/2015.
 */
public class Convert2Arff {

    public Convert2Arff() {}

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
        String csvdata = "AnalysisDataWeka1.csv";
        // load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(csvDir + csvdata));
        //Thread.sleep(1000);
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        String arffOut = csvdata.replaceFirst("[.][^.]+$", ""); // AB This line will remove the extension of ".csv"
        saver.setFile(new File(csv2ArffDir+arffOut));
        //saver.setDestination(new File(args[1]));
        saver.writeBatch();
    }

    public void ReadParseCSV(int hw) throws Exception {
        String csvDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV/";
        String csv2ArffDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV2ARFF/";
        String csvdata = "AnalysisDataWeka.csv";
        CSVReader reader = new CSVReader(new FileReader(csvDir+csvdata));
        BufferedWriter writer = null;
        List<String[]> csvAll = reader.readAll(); // AB Here we will have our csv file converted to a local ArrayList of Strings for processing.
        Integer firstCounter = Integer.parseInt(csvAll.get(1)[csvAll.get(csvAll.size()-1).length-1]);
        Integer lastCounter = Integer.parseInt(csvAll.get(csvAll.size()-1)[csvAll.get(csvAll.size()-1).length-1])+1;
        // AB This line will have the last number of counter
        String counterFormat = "{";
        for (int i = firstCounter; i<lastCounter-1; i++) counterFormat = counterFormat + i +",";
        counterFormat = counterFormat + (lastCounter-1)+"}";

        // AB At first we will create an empty arff file
        File arffFile = new File(csv2ArffDir+"AnalysisDataWeka.arff");

        // AB Then we will start preparing the file arff as per format.
        /*
        ARFF File Format
        EX)
        @relation sensorAndMotion

        @attribute TYPE_ACCELEROMETER_X numeric
        @attribute TYPE_ACCELEROMETER_Y numeric
        @attribute TYPE_ACCELEROMETER_Z numeric
        @attribute TYPE_MAGNETIC_FIELD_X numeric
        @attribute TYPE_MAGNETIC_FIELD_Y numeric
        @attribute TYPE_MAGNETIC_FIELD_Z numeric
        ....

        @data
        -2.74614,6.727692,6.46434,37.27722,-27.62909,-30.218506....
        ......
         */
        // AB We will not be appending to this file, it will just be a conversion (no true for FileWriter)
        writer = new BufferedWriter(new FileWriter(arffFile));
        writer.write("@relation AB&CBsensorAndMotion\n\n"); // AB The first line, leaving a line empty
        // AB The first line of CSV is for attributes SO;
        String[] attr = csvAll.get(0);
        for (int i = 0; i<attr.length; i++) {
            if (i==6 || i==7 || i==8) continue; // AB skip Gyroscope data since it is not used in this assignment
            if (i==attr.length-1) writer.write("@attribute " + attr[i] + " "+counterFormat+"\n");
            else writer.write("@attribute " + attr[i] + " numeric\n");
        }
        writer.write("\n@data\n"); // AB leaving two lines before the Data.
        // AB for the data, it is going to be a ArrayList of Strings, one way is to make
        // two for loops to handle all entries...
        for (int i = 1; i<csvAll.size()-1; i++) { // Since 0 is for attr, we will start with 1
            String[] dataLine = csvAll.get(i);
            for (int j = 0; j<dataLine.length; j++) {
                if (j==6 || j==7 || j==8) continue; // AB skip Gyroscope data since it is not used in this assignment
                if (j<dataLine.length-1) writer.write(dataLine[j]+","); //AB if it was not the last element of the array
                else writer.write(dataLine[j]+"\n"); //the last element with no comma. but go to next line
            }
        }

        writer.close(); // AB close and save the ARFF file
    }
}
