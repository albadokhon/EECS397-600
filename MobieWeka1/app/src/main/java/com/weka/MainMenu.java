package com.weka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class MainMenu extends Activity{
	
	private Button classifier, clusterer, associate, exit;
	private Button csv2arff; // AB Adding the CSV conversion button in the main menu
	private Convert2Arff converter; //AB an external class method to convert all CSV files at once

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
        setContentView(R.layout.mainmenu);
        findViewById();
        addbuttonlistener();
    }
    
    public void findViewById() {
    	classifier = (Button)findViewById(R.id.classifier_button);
    	clusterer = (Button)findViewById(R.id.clusterer_button);
    	associate = (Button)findViewById(R.id.associate_button);  	
    	exit = (Button)findViewById(R.id.exit_button);

        csv2arff = (Button) findViewById(R.id.csv2arff_button); // AB Added button handler
    }
    
    public void addbuttonlistener (){
    	OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg) {
				setTitle("DDDF");
				
				if (arg == classifier) {
					Intent intent = new Intent();
					intent.setClass(MainMenu.this, BuildClassifier.class);
					startActivity(intent);
				}else if (arg == clusterer) {  
					Intent intent = new Intent();
					intent.setClass(MainMenu.this, BuildClusterer.class);
					startActivity(intent);
				}else if (arg == associate) {  
					Intent intent = new Intent();
					intent.setClass(MainMenu.this, BuildAssociate.class);
					startActivity(intent);
				}else if (arg == exit) {
					Intent intent=new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}

                //AB Why not handle a quick CSV 2 ARFF conversion here :)
                else if (arg == csv2arff) {
                    try {
                        converter.CSV2ARFF(4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
			}    		
    	};
    	classifier.setOnClickListener(listener);
    	clusterer.setOnClickListener(listener);
    	associate.setOnClickListener(listener);
    	exit.setOnClickListener(listener);

        csv2arff.setOnClickListener(listener); //AB adding listener
    }
}
