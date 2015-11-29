package com.example.aab119.restclientexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends Activity {
    private Button ledon, ledoff, sensor;
    private TextView replyPane;

    private final String authKey = "authorization=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXYiOiJlc3AxMiIsImlhdCI6MTQ0ODUwODc5MSwianRpIjoiNTY1NjdkNzc1Yjk2MTAwMTQ3NDFmNDIyIiwidXNyIjoiYWxiYWRvIn0.r654Dn68Mm3klmnXvk6i5aEqGB6RptM0U4BL55loZeI";
    private final String ledURL = "https://api.thinger.io/v2/users/albado/devices/esp12/led?"+authKey;
    private final String checkURL = "https://api.thinger.io/v2/users/albado/devices/esp12/check?"+authKey;
    private final String senssorURL = "https://api.thinger.io/v2/users/albado/devices/esp12/millis?"+authKey;

    private final String switchStateON = "{\"in\":false}";
    private final String switchStateOFF = "{\"in\":true}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        addButtonListener();
        String checkLeds = GetSensor(checkURL);
        if (checkLeds.equals("1")) {
            ledoff.setEnabled(true);
            ledon.setEnabled(false);
            //ledon.setEnabled(false);
        }
        else if (checkLeds.equals("0")) {
            ledoff.setEnabled(false);
            ledon.setEnabled(true);
        }
    }

    public void findViewById() {
        ledon = (Button) findViewById(R.id.ledon);
        ledoff = (Button) findViewById(R.id.ledoff);
        sensor = (Button) findViewById(R.id.button3);
        replyPane = (TextView) findViewById(R.id.editText);
    }

    public void addButtonListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == ledon) {
                    String result = SetSwitch(ledURL, switchStateON);
                    if (result.equals("{}")) {
                        replyPane.setText("Switch was turned ON :)");
                        ledoff.setEnabled(true);
                        ledon.setEnabled(false);
                    }
                    else replyPane.setText(result+"\n There was a problem...");

                }
                else if (v == ledoff) {
                    String result = SetSwitch(ledURL, switchStateOFF);
                    if (result.equals("{}")) {
                        replyPane.setText("Switch was turned OFF :)");
                        ledoff.setEnabled(false);
                        ledon.setEnabled(true);
                    }
                    else replyPane.setText(result+"\n There was a problem...");
                }
                else if (v == sensor) {
                    replyPane.setText(GetSensor(senssorURL));
                }
            }
        };
        ledon.setOnClickListener(listener);
        ledoff.setOnClickListener(listener);
        sensor.setOnClickListener(listener);
    }

    private String SetSwitch(String url, String state) {
        final RestClient client = new RestClient(url);
        final String[] result = new String[1];
        client.setJSONString(state);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    client.execute(RequestMethod.POST);
                    if (client.getResponseCode() != 200) {
                        //return server error
                        result[0] = client.getErrorMessage();
                        return;
                    }
                    //return valid data
                    JSONObject jObj = new JSONObject(client.getResponse());
                    //result = new String[1];
                    result[0] = jObj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    result[0] = e.toString();
                }
            }
        });
        thread.start();
        while (result[0] == null);
        return result[0];
    }

    private String GetSensor(String url) {
        final RestClient client = new RestClient(url);
        final String[] result = new String[1];
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    client.execute(RequestMethod.GET);
                    if (client.getResponseCode() != 200) {
                        //return server error
                        result[0] = client.getErrorMessage();
                        return;
                    }
                    //return valid data
                    JSONObject jObj = new JSONObject(client.getResponse());
                    //result = new String[1];
                    result[0] = jObj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    result[0] = e.toString();
                }
            }
        });
        thread.start();
        while (result[0] == null);
        return result[0].substring(result[0].indexOf(':')+1, result[0].length()-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
