package com.example.haotian.tutorial32;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity {
    public static final String TAG = "MapsActivity";
    public static final int THUMBNAIL = 1;



    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button picButton; //takes user to camera
    private EditText edit_title, edit_snippet;
    private LinearLayout textLO;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        edit_title = (EditText) findViewById(R.id.editText);
        edit_title.setHint("Title");
        edit_title.setHintTextColor(Color.BLACK);
        edit_snippet = (EditText) findViewById(R.id.editText2);
        edit_snippet.setHint("Snippet");
        edit_snippet.setHintTextColor(Color.BLACK);

        picButton = (Button) findViewById(R.id.photobutton);

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureAction();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = String.valueOf(edit_title.getText());
                String snippet = String.valueOf(edit_snippet.getText());
                String curTitle = marker.getTitle();
                String curSnip = marker.getSnippet();

                if (curTitle == null) curTitle="";
                if (curSnip == null) curSnip="";

                marker.setTitle((title.equals(""))? ( (curTitle.equals(""))? String.valueOf("No Title!") : curTitle ): title);
                marker.setSnippet((snippet.equals(""))? ( (curSnip.equals(""))? String.valueOf("No Snippet!") : curSnip ): snippet);
                edit_title.setText("");
                edit_snippet.setText("");
                edit_title.setVisibility(View.INVISIBLE);
                edit_snippet.setVisibility(View.INVISIBLE);
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                edit_title.setVisibility(View.VISIBLE);
                edit_snippet.setVisibility(View.VISIBLE);
                Context context = getApplicationContext();
                CharSequence text = "Click on the Marker to Save, and Click anywhere on the Map to Discard..";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                edit_title.setText("");
                edit_snippet.setText("");
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
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(20, 20))
                .title("EECS397/600")
                .visible(true));
    }

    private void dispatchTakePictureAction() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity((getPackageManager())) != null)
            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap); // AB CB or we can actually save it the phone DCIM/ and put it on the map from there
            Context context = getApplicationContext();
            CharSequence text = mMap.getMyLocation().getLatitude()+ ",  "+mMap.getMyLocation().getLongitude();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }

}
