package com.example.haotian.tutorial32;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MapsActivity extends FragmentActivity {
    public static final String TAG = "MapsActivity";
    public static final int THUMBNAIL = 1;



    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button picButton; //takes user to camera
    private EditText edit_title, edit_snippet; // AB two EditText views to update Marker info.
    private LinearLayout textLO; // AB The layout used for the Text Editors.
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String imageBldgName;
    private String MAPCSVDir, MAPImageDir;
    private final String[] columnCSV = {"TimeStamp", "Latitude", "Longitude",
                                    "Building Title", "Building Snippet", "Picture Directory"};
    private String mTimestamp;
    private final String fileName = "MapMarkerData.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //AB Create Map CSV Folder if it does not exist.
        MAPCSVDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/MAPCSV";
        MAPImageDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/MapImages";
        File CSVdir = new File(MAPCSVDir);
        File ImageDir = new File(MAPImageDir);
        if (!CSVdir.exists()) new File(MAPCSVDir).mkdir();
        if (!ImageDir.exists()) new File(MAPImageDir).mkdir();

        setUpMapIfNeeded();

        // AB Initializing EditText fields and Layout
        edit_title = (EditText) findViewById(R.id.editText);
        edit_title.setHint("Title");
        edit_title.setHintTextColor(Color.BLACK);
        edit_snippet = (EditText) findViewById(R.id.editText2);
        edit_snippet.setHint("Snippet");
        edit_snippet.setHintTextColor(Color.BLACK);
        textLO = (LinearLayout) findViewById(R.id.linearLO);


        picButton = (Button) findViewById(R.id.photobutton);

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureAction();
            }
        });

        // AB CB Here we set a listener to when the marker icon is touched
        // only to compare with EditText updates for title and snippet
        // and save the new values, if any, to the marker information
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = String.valueOf(edit_title.getText());
                String snippet = String.valueOf(edit_snippet.getText());
                String oldTitle = marker.getTitle();
                String oldSnip = marker.getSnippet();

                if (oldTitle == null) oldTitle = "";
                if (oldSnip == null) oldSnip = "";

                // (Statement) ? if true : if not true;

                marker.setTitle((title.equals("")) ? ((oldTitle.equals("")) ? String.valueOf("No Title!") : oldTitle) : title);
                marker.setSnippet((snippet.equals("")) ? ((oldSnip.equals("")) ? String.valueOf("No Snippet!") : oldSnip) : snippet);
                edit_title.setText("");
                edit_snippet.setText("");
                textLO.setVisibility(View.INVISIBLE);
                edit_title.setVisibility(View.INVISIBLE);
                edit_snippet.setVisibility(View.INVISIBLE);
                marker.showInfoWindow();
                //AB CB if there was a change in the information, update the CSV file
                if (!oldTitle.equals(marker.getTitle()) || !oldSnip.equals(marker.getSnippet()))
                    updateCSVTitSnip(oldTitle, marker.getTitle(), marker.getSnippet());
                return true;
            }
        });
        // AB CB a listener was added to the InformationWindow of the marker to only view the update
        // fields only if it was touched.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                textLO.setVisibility(View.VISIBLE);
                edit_title.setVisibility(View.VISIBLE);
                edit_snippet.setVisibility(View.VISIBLE);
                Context context = getApplicationContext();
                CharSequence text = "Click on the Marker to Save, and Click anywhere on the Map to Discard..";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        // AB CB The listener here is to discard any changes that were made on the update view
        // because the user touched the map instead of the marker, which would have saved the values.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                edit_title.setText("");
                edit_snippet.setText("");
                textLO.setVisibility(View.INVISIBLE);
                edit_title.setVisibility(View.INVISIBLE);
                edit_snippet.setVisibility(View.INVISIBLE);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment smf = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mMap = smf.getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
            else {
                Context context = getApplicationContext();
                CharSequence text = "Map is still NULL!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
/*        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(20, 20))
                .title("EECS397/600")
                .visible(true));*/
        //CB Read the CSV File to add correct markers to map. First make sure the CSV trying to be read exists
        try{
            CSVReader reader = new CSVReader(new FileReader(MAPCSVDir + File.separator + fileName));
            List<String[]> csvAll = reader.readAll();

            //CB Adds details for every location currently saved in the CSV File onto the map
            for (int i = 1; i < csvAll.size(); i++) { //CB start from 1, 0 is for column names in csv
                String[] thisRow = csvAll.get(i);
                float lat = Float.parseFloat(thisRow[1]);
                float lonit =  Float.parseFloat(thisRow[2]);

                Context context = getApplicationContext();
                CharSequence text = lat +", "+ lonit;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                // CB Add the read values and plot them to the map (BETA) still in progress.
                Bitmap temp = BitmapFactory.decodeFile(thisRow[5]);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lonit))
                        .title(thisRow[3])
                        .snippet(thisRow[4])
                        .icon(BitmapDescriptorFactory.fromBitmap(temp))
                        .visible(true));
            }

        }
        catch (Exception e0) {
            e0.printStackTrace();
        }
    }
    // AB CB function to prompt the camera view. Results are handled in OnActivityResult()
    private void dispatchTakePictureAction() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity((getPackageManager())) != null)
            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
    }
    // AB function to save or append new CSV data, (TS, Lat, Longt, Title, snip, pic_dir)
    private void saveCSV(String[] data) {
        //AB CSV file creation
            CSVWriter writer = null;
            try
            {
                String baseDir = MAPCSVDir;
                //System.out.println(baseDir);
                //String fileName = "MapMarkerData.csv";
                String filePath = baseDir + File.separator + fileName;
                File f = new File(filePath);
                if(!f.exists()){
                    writer = new CSVWriter(new FileWriter(filePath));
                    String[] column = columnCSV;
                    writer.writeNext(column);
                    writer.close();
                    System.out.println("CSV file Created for the first time");
                }
                if (f.exists()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
                    mTimestamp = simpleDateFormat.format(new Date()); //AB Timestamp...
                    data[0] = mTimestamp; //AB to store the current time.
                    writer = new CSVWriter(new FileWriter(filePath, true)); ////AB (true) is to append into the file
                    String[] values = data; //AB All should be strings
                    writer.writeNext(values); //AB Means append to the file...
                    writer.close();
                }
            }
            catch (IOException e) {
                //error
            }
            //AB end of CSV File creation

    }

    //AB CB a function to save the captured picture as a PNG image in a directory in DCIM
    private boolean saveBitmap(String filenamedir, Bitmap image) {
        File dest = new File(filenamedir);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            image.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //AB CB if there was a change in the information
    // AB read ALL CSV File, seek for oldTitle value in data[all][3], update Array with new values of title and snippet
    // Save the entire array back to the CSV.
    private void updateCSVTitSnip (String oldTitle, String newTit, String newSnip) {
        int rowOfValue = -1;
        try {
            CSVReader reader = new CSVReader(new FileReader(MAPCSVDir+File.separator+fileName));
            List<String[]> csvAll = reader.readAll(); // AB Here we will have our csv file converted to a local ArrayList of Strings for processing.
            for (int j = 1; j < csvAll.size(); j++) {
                if (csvAll.get(j)[3].equals(oldTitle)) {
                    rowOfValue = j;
                    break;
                }
                else if (j == csvAll.size()-1) { //AB Meaning last one and value was not found...
                    Context context = getApplicationContext();
                    CharSequence text = "There was a problem finding the original value!!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return; // AB to get out of the function without continuing the data process.
                }
            }
            if (rowOfValue == -1) return; // AB to get out of the function without continuing the data process.
            String[] updatedRow = csvAll.get(rowOfValue);
            updatedRow[3] = newTit;
            updatedRow[4] = newSnip;
            csvAll.set(rowOfValue, updatedRow);

            CSVWriter writer = new CSVWriter(new FileWriter(MAPCSVDir+File.separator+fileName, false)); //AB (true) is to append into the file
            writer.writeAll(csvAll); //AB All list String[] should be saved to CSV here
            writer.close();
        }
        catch (Exception e0) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Save This image into a directory in DCIM/MapImages

            // AB CB we want to save the Bitmap image to the phone DCIM/ and put it on the map from there
            final Double latit = mMap.getMyLocation().getLatitude();
            final Double longt = mMap.getMyLocation().getLongitude();

            // AB Alert Text Input Block, to have a title to the image and marker.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

            // AB Set up the input
            final EditText input = new EditText(this);
            // AB Specify the type of input expected; this, for example, sets the input with autocurrect feature
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            builder.setView(input);

            // AB Set up the buttons for the dialog, only if ok is pressed, then plot the new marker
            // and save the image with the title typed.
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageBldgName = input.getText().toString();
                    // Marker Block for picture
                    mMap.setMyLocationEnabled(true);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latit, longt))
                            .title(imageBldgName) //AB Only before modification
                            .icon(BitmapDescriptorFactory.fromBitmap(imageBitmap))
                            .visible(true));
                    //End of Marker Block
                    String fullImageName = MAPImageDir+File.separator+imageBldgName+".PNG";
                    //AB Small function to save the image AND Beginning of CSV Saving block
                    if (saveBitmap(fullImageName,imageBitmap)) { // AB if saving image went through...
                        String[] markerData = {"ts", latit+"",longt+"", imageBldgName,"",fullImageName};
                        saveCSV(markerData); // AB Here save or append values to the csv
                    }
                    else {
                        Context context = getApplicationContext();
                        CharSequence text = "There was a problem saving the image!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    // AB Store CSV File Entry HERE....

                }
            });
            // AB Here we handle if user canceled, just don't polt marker or save csv.
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Context context = getApplicationContext();
                    CharSequence text = "Once you relaunch the app, you WON'T be able to see this marker!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            builder.show();
            // AB End of Alert Input Block

            Context context = getApplicationContext();
            CharSequence text = mMap.getMyLocation().getLatitude()+ ",  "+mMap.getMyLocation().getLongitude();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }
}
