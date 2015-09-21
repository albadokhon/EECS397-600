package com.example.haotian.haotianalp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.opencsv.CSVWriter;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Testing GitHub AB new

public class ALPActivity extends Activity implements SensorEventListener {
    protected LockPatternView mPatternView;
    protected PatternGenerator mGenerator;
    protected Button mGenerateButton;
    protected Button mDesigner;
    protected ToggleButton mPracticeToggle;
    private List<Point> mEasterEggPattern;
    protected SharedPreferences mPreferences;
    protected int mGridLength=0;
    protected int mPatternMin=0;
    protected int mPatternMax=0;
    protected String mHighlightMode;
    protected boolean mTactileFeedback;

    private static final String TAG = "SensorActivity";
    private static final String TAGmotion = "motionEvent";
    private SensorManager mSensorManager = null;

    public List<Sensor> deviceSensors;
    private  Sensor mAccelerometer, mMagnetometer, mGyroscope, mRotation, mGravity, myLinearAcc;

    private File file;
    public static String[] mLineHW4;
    public static String[] mLineHW3; //AB HW3 To be used to collect data for HW3
    public static List<String[]> mLineBuffer;
    public BufferedWriter bufferedWriter;
    private VelocityTracker mVelocityTracker = null;
    private int control = 0;
    private int gridLength; //AB HW1 better to make it private variable to be used separately without calling updateFromPrefs()
    DateFormat mDateFormat;
    String mTimestamp;
    private int counter=0; //AB HW4 For correct counter
    private String myStr = "";
    private final String[] columnHW3 = {"TimeStamp", "TYPE_ACCELEROMETER_X", "TYPE_ACCELEROMETER_Y", "TYPE_ACCELEROMETER_Z",
            "TYPE_MAGNETIC_FIELD_X", "TYPE_MAGNETIC_FIELD_Y", "TYPE_MAGNETIC_FIELD_Z",
            "TYPE_GYROSCOPE_X", "TYPE_GYROSCOPE_Y", "TYPE_GYROSCOPE_Z",
            "TYPE_ROTATION_VECTOR_X", "TYPE_ROTATION_VECTOR_Y", "TYPE_ROTATION_VECTOR_Z",
            "TYPE_LINEAR_ACCELERATION_X", "TYPE_LINEAR_ACCELERATION_Y", "TYPE_LINEAR_ACCELERATION_Z",
            "TYPE_GRAVITY_X", "TYPE_GRAVITY_Y", "TYPE_GRAVITY_Z"};
    private final String[] columnHW2 = {"position_X","position_Y","velocity_X","velocity_Y","pressure","size"};
    private final String[] columnHW4last = {"mCurrentPattern","Counter"};
    private final String[] columnHW4 = ArrayUtils.addAll(ArrayUtils.addAll(columnHW3,columnHW2), columnHW4last) ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGenerator = new PatternGenerator();

        setContentView(R.layout.activity_alp);
        mPatternView = (LockPatternView) findViewById(R.id.pattern_view);
        mGenerateButton = (Button) findViewById(R.id.generate_button);

        mGenerateButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View btnView) {
                        if (mPatternView.getPracticeMode()) {
                            Context context = getApplicationContext();
                            CharSequence text = "Please Toggle the Practice Mode BTN";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else {
                            mGenerator.setGridLength(gridLength); //AB HW1 there was a problem with the function updateFromPrefs() in ALPActivity, as it halted the app...
                            mPatternView.invalidate(); //CB HW1 refresh the view
                            // Toast Block
                            Context context = getApplicationContext();
                            CharSequence text = "Pattern # " + (mGenerator.count+1); //Toast me which pattern
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration); //AB CB HW1 just a simple pop-out msg for the user
                            toast.show();
                            // End of Toast Block
                            mEasterEggPattern = mGenerator.getPattern(); //AB CB Generates pattern 1,2,3, or 4 accordingly and saves it to an array
                            mPatternView.setPattern(mEasterEggPattern); //AB CB HW1 setting the pattern to be the new randomized pattern
                            mPatternView.invalidate(); //CB HW1 refresh the view again (required)
                        }
                    }
                });


        mPracticeToggle = (ToggleButton) findViewById(R.id.practice_toggle);

        mPracticeToggle.setOnCheckedChangeListener(
                new ToggleButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            Context context = getApplicationContext();
                            CharSequence text = "Try the password now...";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration); //AB CB HW1 just a simple pop-out msg for the user
                            toast.show();
                            mPatternView.setPracticeMode(true); //AB HW1 has to be on practice mode after btn is toggled
                        }
                        else if (!isChecked) mPatternView.setPracticeMode(false);

                    }
                });

        //AB HW3 Sensor Initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        myLinearAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mLineHW3 = new String[19]; //AB HW3 The number of all variables used in the CSV of HW3.
        mLineHW4 = new String[27]; //AB HW4 The number of all variables used in the CSV of HW4.
        mLineBuffer = new ArrayList<String[]>();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateFromPrefs();

        //AB HW3 Registering all sensors for updating values wrt the sampling rate (SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, myLinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_al, menu);
        return true;
    }

    @Override
    protected void onPause() {

        super.onPause();

        //AB HW3 Unregistering or pausing the updating for sensor data while the app is onPause().
        mSensorManager.unregisterListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFromPrefs()
    {
         gridLength =
                mPreferences.getInt("grid_length", Defaults.GRID_LENGTH);
        int patternMin =
                mPreferences.getInt("pattern_min", Defaults.PATTERN_MIN);
        int patternMax =
                mPreferences.getInt("pattern_max", Defaults.PATTERN_MAX);
        String highlightMode =
                mPreferences.getString("highlight_mode", Defaults.HIGHLIGHT_MODE);
        boolean tactileFeedback = mPreferences.getBoolean("tactile_feedback",
                Defaults.TACTILE_FEEDBACK);

        // sanity checking
        if(gridLength < 1)
        {
            gridLength = 1;
        }
        if(patternMin < 1)
        {
            patternMin = 1;
        }
        if(patternMax < 1)
        {
            patternMax = 1;
        }
        int nodeCount = (int) Math.pow(gridLength, 2);
        if(patternMin > nodeCount)
        {
            patternMin = nodeCount;
        }
        if(patternMax > nodeCount)
        {
            patternMax = nodeCount;
        }
        if(patternMin > patternMax)
        {
            patternMin = patternMax;
        }

        // only update values that differ
        if(gridLength != mGridLength)
        {
            setGridLength(gridLength);
        }
        if(patternMax != mPatternMax)
        {
            setPatternMax(patternMax);
        }
        if(patternMin != mPatternMin)
        {
            setPatternMin(patternMin);
        }
        if(!highlightMode.equals(mHighlightMode))
        {
            setHighlightMode(highlightMode);
        }
        if(tactileFeedback ^ mTactileFeedback)
        {
            setTactileFeedback(tactileFeedback);
        }
    }

    private void setGridLength(int length)
    {
        mGridLength = length;
        mGenerator.setGridLength(length);
        mPatternView.setGridLength(length);
    }
    private void setPatternMin(int nodes)
    {
        mPatternMin = nodes;
        mGenerator.setMinNodes(nodes);
    }
    private void setPatternMax(int nodes)
    {
        mPatternMax = nodes;
        mGenerator.setMaxNodes(nodes);
    }
    private void setHighlightMode(String mode)
    {
        if("no".equals(mode))
        {
            mPatternView.setHighlightMode(new LockPatternView.NoHighlight());
        }
        else if("first".equals(mode))
        {
            mPatternView.setHighlightMode(new LockPatternView.FirstHighlight());
        }
        else if("rainbow".equals(mode))
        {
            mPatternView.setHighlightMode(
                    new LockPatternView.RainbowHighlight());
        }

        mHighlightMode = mode;
    }
    private void setTactileFeedback(boolean enabled)
    {
        mTactileFeedback = enabled;
        mPatternView.setTactileFeedbackEnabled(enabled);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //AB CB HW3 Here we will log all sensor data along with a timestamp in a new CSV file
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mLineHW3[1] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[2] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[3] = event.values[2]+""; //AB HW3 Value of Z
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mLineHW3[4] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[5] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[6] = event.values[2]+""; //AB HW3 Value of Z
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mLineHW3[7] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[8] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[9] = event.values[2]+""; //AB HW3 Value of Z
        }
        else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            mLineHW3[10] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[11] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[12] = event.values[2]+""; //AB HW3 Value of Z
        }
        else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            mLineHW3[13] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[14] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[15] = event.values[2]+""; //AB HW3 Value of Z
        }
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mLineHW3[16] = event.values[0]+""; //AB HW3 Value of X
            mLineHW3[17] = event.values[1]+""; //AB HW3 Value of Y
            mLineHW3[18] = event.values[2]+""; //AB HW3 Value of Z
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
        mTimestamp = simpleDateFormat.format(new Date()); //AB HW3 Timestamp...
        mLineHW3[0] = mTimestamp; //AB HW3 to store the current time.
        this.saveCSVHW3(mLineHW3);

        //AB HW4 Start storing data of HW4
        mPatternView.invalidate();
        if (mPatternView.isDrawing) {

            //AB HW4 Formatting pattern point data
            String lastpattern = "{";
            String p = "Point(";
            for (int i =0; i<mEasterEggPattern.size(); i++) {
                Point temp = mEasterEggPattern.get(i);
                lastpattern =lastpattern+ p+temp.x+","+temp.y+")";
            }

            //AB HW4 we will get the rest of data from LockPattern
            //AB HW4 These two lines generate the Array of HW4 then adds it to the buffer.

            for (int i = 0; i<mLineHW3.length; i++) mLineHW4[i] = mLineHW3[i];
            String[] HW2Data = mPatternView.getHW2Data();
            for (int i = 0; i<HW2Data.length; i++) mLineHW4[i+19] = HW2Data[i];
            mLineHW4[25] = lastpattern;
            mLineHW4[26] = counter+"";
            mPatternView.invalidate();
            //System.out.println(mLineHW4.toString());
            if(mLineHW4 != null) mLineBuffer.add(mLineHW4);
        }
        else if (!mPatternView.isDrawing) {
            if (mPatternView.correctPattern) { //AB HW4 Only if pattern was correct
                //this.saveCSVHW4(mLineHW4);
                this.saveCSVHW4(mLineBuffer);//AB HW4 Here we would want to save the data to the CSV file because pattern is correct
                mPatternView.correctPattern = false;
                counter++;
                mLineBuffer.clear();
                mLineBuffer = new ArrayList<String[]>(); //AB HW4 just to release the memory after saving the data.
            }
            else if (!mPatternView.correctPattern) {
                mLineBuffer.clear();
                mLineBuffer = new ArrayList<String[]>();; //AB HW4 Here we would want to discard the data because pattern is not correct
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //AB CB HW3 not required for this tutorial
    }

    //AB HW3 Method to create CSV and save HW3 data to it.
    private void saveCSVHW3(String[] data) {
        //AB HW3 CSV file creation
        if (mPatternView.getPracticeMode()) { //AB HW3 Store sensor data only in practice mode...
            CSVWriter writer = null;
            try
            {
                String baseDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/";
                //System.out.println(baseDir);
                String fileName = "AnalysisDataHW3.csv";
                String filePath = baseDir + File.separator + fileName;
                File f = new File(filePath);
                if(!f.exists()){
                    writer = new CSVWriter(new FileWriter(filePath));
                    String[] column = columnHW3;
                    writer.writeNext(column);
                    writer.close();
                    System.out.println("HW3 Created for the first time");
                }
                else if (f.exists()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
                    mTimestamp = simpleDateFormat.format(new Date()); //AB HW3 Timestamp...
                    mLineHW3[0] = mTimestamp; //AB HW3 to store the current time.
                    writer = new CSVWriter(new FileWriter(filePath, true)); ////AB HW3 (true) is to append into the file
                    String[] values = mLineHW3; //AB HW3 All should be strings
                    writer.writeNext(values);
                    writer.close();
                }
            }
            catch (IOException e) {
                //error
            }
            //AB HW3 end of CSV File creation

        }

    }

    private void saveCSVHW4(List<String[]> data) {
        //AB HW4 CSV file creation
        if (mPatternView.getPracticeMode()) { //AB HW4 Store sensor data only in practice mode...
            CSVWriter writer = null;
            try
            {
                String baseDir = android.os.Environment.getExternalStorageDirectory()+"/DCIM/";
                //System.out.println(baseDir);
                String fileName = "AnalysisDataHW4.csv";
                String filePath = baseDir + File.separator + fileName;
                File f = new File(filePath);
                if(!f.exists()){
                    writer = new CSVWriter(new FileWriter(filePath));
                    String[] column = columnHW4;
                    writer.writeNext(column);
                    writer.close();
                    System.out.println("HW4 Created for the first time");
                }
                else if (f.exists()) {
                    writer = new CSVWriter(new FileWriter(filePath, true)); ////AB HW4 (true) is to append into the file
                    //String[] values = data;
                    //writer.
                    //writer.writeNext(values);
                    writer.writeAll(data); //AB HW4 All list String[] should be saved to CSV here
                    writer.close();
                }
            }
            catch (IOException e) {
                //error
            }
            //AB HW4 end of CSV File creation

        }
    }
}
