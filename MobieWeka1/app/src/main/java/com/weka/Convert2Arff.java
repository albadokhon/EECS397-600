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

    public Convert2Arff() {
        // Constructor
    }

    /*
    Alaa Badokhon

     This function is a CSV to Text (ARFF Format) parser that converts CSV to ARFF.
     It will take one CSV file and generate two ARFF files. One for training and the other is for testing
     As default naming; it will add a "TEST" to the end of the testing file name to distinguish between
      the two...
     As for default entries; 35 counter values will be as training values and the rest (min 15) will be for testing.

    */
    public boolean ReadParseCSV(String csvStr) throws Exception {
        String csvDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV/";
        String csv2ArffDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/CSV2ARFF/";
        String test = "TEST";
        //String csvdata = "AnalysisDataWeka.csv";
        CSVReader reader = new CSVReader(new FileReader(csvDir+csvStr));
        BufferedWriter writer = null;
        BufferedWriter writerT = null;
        List<String[]> csvAll = reader.readAll(); // AB Here we will have our csv file converted to a local ArrayList of Strings for processing.
        Integer firstCounter = Integer.parseInt(csvAll.get(1)[csvAll.get(csvAll.size() - 1).length - 2]); // AB The first counter value
        Integer lastCounter = Integer.parseInt(csvAll.get(csvAll.size()-1)[csvAll.get(csvAll.size()-1).length-2])+1; // AB The last counter value
        //String[] initials = {"AB", "CB", "U1", "U2"}; //AB CB All possible initial values
        String initials = "{AB,CB}";
        String initialsT = "{U1,U2}";

        //AB For data to comply, it has to be more than 50 pattern trials
        if (lastCounter-firstCounter<50) return false;
        // 50 --> 35 training, 15 or more is testing.

        // AB to produce a string with the format {0,1,2,...,lastCounter}
        String TcounterFormat = "";
        String counterFormat = "{";
        for (int i = firstCounter; i<lastCounter-1; i++) counterFormat = counterFormat + i +",";
        counterFormat = counterFormat + (lastCounter-1)+"}";

        String[] temp = counterFormat.split(",36,");
        counterFormat = temp[0]+"}";
        TcounterFormat = "{36,"+temp[1];

        // AB At first we will create an empty arff file
        String csvNoExt = csvStr.replaceFirst("[.][^.]+$", ""); // AB temp.csv --> temp (removes extension)
        //String initials = csvNoExt.substring(Math.max(csvNoExt.length() - 2, 0));

        File arffFile = new File(csv2ArffDir+csvNoExt+".arff");
        File arffTest = new File(csv2ArffDir+csvNoExt+test+".arff");
        String arffConstructor = ""; //AB This string will hold all the lines befor @data, because
        String arffConstTest = "";   // the same is going to be used for both training and testing

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
        writerT = new BufferedWriter(new FileWriter(arffTest));

        //writer.write("@relation "+initials+"sensorAndMotion\n\n"); // AB The first line, leaving a line empty
        arffConstructor = arffConstructor+"@relation "+"sensorAndMotion\n\n";
        arffConstTest = arffConstTest + "@relation "+"sensorAndMotion\n\n";
        // AB The first line of CSV is for attributes SO;
        String[] attr = csvAll.get(0);
        for (int i = 0; i<attr.length; i++) {
            if (i==6 || i==7 || i==8 || i==attr.length-2) continue; // AB skip Gyroscope data since it is not used in this assignment // and Counter for now

/*            if (i==attr.length-2) { // meaning Counter column
                arffConstructor = arffConstructor + "@attribute " + attr[i] + " "+counterFormat+"\n";
                arffConstTest = arffConstTest + "@attribute " + attr[i] + " "+TcounterFormat+"\n";
            }*/
            if (i==attr.length-1) { // meaning initials column
                arffConstructor = arffConstructor + "@attribute " + attr[i] + " "+initials+"\n";
                arffConstTest = arffConstTest + "@attribute " + attr[i] + " "+initialsT+"\n";
            }
            else {
                arffConstructor = arffConstructor + "@attribute " + attr[i] + " numeric\n";
                arffConstTest = arffConstTest +  "@attribute " + attr[i] + " numeric\n";
            }
        }
        arffConstructor = arffConstructor + "\n@data\n";// AB leaving two lines before the Data.
        arffConstTest = arffConstTest + "\n@data\n";// AB leaving two lines before the Data.

        writer.write(arffConstructor);
        writerT.write(arffConstTest);

        //writer.write("\n@data\n");
        // AB for the data, it is going to be a ArrayList of Strings, one way is to make
        // two for loops to handle all entries...
        for (int i = 1; i<csvAll.size()-1; i++) { // Since 0 is for attr, we will start with 1
            String[] dataLine = csvAll.get(i);
            //AB Training loop..
            //AB if initials equals AB or CB then this is training data
            if (dataLine[dataLine.length-1].equals("AB") || dataLine[dataLine.length-1].equals("CB")) {
                for (int j = 0; j<dataLine.length; j++) {
                    if (j==6 || j==7 || j==8 || j==dataLine.length-2) continue; // AB skip Gyroscope data since it is not used in this assignment // and Counter for now
                    if (j<dataLine.length-1) writer.write(dataLine[j]+","); //AB if it was not the last element of the array
                    else writer.write(dataLine[j]+"\n"); //the last element with no comma. but go to next line
                }
            }
            //Testing loop
            //AB if initials were anonymous then it is training data.
            else  {
                for (int j = 0; j<dataLine.length; j++) {
                    if (j==6 || j==7 || j==8 || j==dataLine.length-2) continue; // AB skip Gyroscope data since it is not used in this assignment
                    if (j<dataLine.length-1) writerT.write(dataLine[j]+","); //AB if it was not the last element of the array
                    else writerT.write(dataLine[j]+"\n"); //the last element with no comma. but go to next line
                }
            }
        }

        writer.close(); //AB Close and save the ARFF file
        writerT.close(); //AB Close and save the TEST ARFF file
        return true;
    }
}
