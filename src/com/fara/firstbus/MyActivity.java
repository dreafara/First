package com.fara.firstbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MyActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	private ConnectivityManager cm1;
	private ConnectivityManager cm2;
	private ConnectivityManager cm3;
	private AlertDialog alert;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

     // Set up click listeners for all the buttons
        View newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(this);
        View mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(this);
        View arrivalsButton = findViewById(R.id.arrivals_button);
        arrivalsButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }    
	
private AlertDialog buildMyDialog(Integer type){
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (type){
		//No localization service active
		case 1: 
				builder.setMessage(R.string.dialog_text)
				.setCancelable(false)
				.setTitle(R.string.about_label)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				break;
		case 2:
			builder.setMessage(R.string.no_network_text)
			.setCancelable(false)
			.setTitle(R.string.no_network)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			break;
		}
		return builder.create();
	}
	
	public void onClick(View v) {
		
		switch (v.getId()) {
		/*
		case R.id.about_button:
			alert = buildMyDialog(1);
			alert.show();
			break;
			*/
		case R.id.new_button:
			
			cm1 =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo1 = cm1.getActiveNetworkInfo();
			
			if(netInfo1==null){
				alert = buildMyDialog(2);
				alert.show();
			}
			else{
				if(!(cm1.getActiveNetworkInfo().isConnected())){
					alert = buildMyDialog(2);
					alert.show();
				}
				else{
					Intent i_search = new Intent(this, Search.class);
					startActivity(i_search);
				}
			}
			break;
			
		case R.id.arrivals_button:
			cm3 =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo3 = cm3.getActiveNetworkInfo();
			
			if(netInfo3==null){
				alert = buildMyDialog(2);
				alert.show();
			}
			else{
				if(!(cm3.getActiveNetworkInfo().isConnected())){
					alert = buildMyDialog(2);
					alert.show();
				}
				else{
					Intent i_arrivals = new Intent(this, Arrivals.class);
					startActivity(i_arrivals);
				}
			}
			break;
			
		case R.id.map_button:	
			
			cm2 =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo2 = cm2.getActiveNetworkInfo();
			
			if(netInfo2==null){
				alert = buildMyDialog(2);
				alert.show();
			}
			else{
				if(!(cm2.getActiveNetworkInfo().isConnected())){
					alert = buildMyDialog(2);
					alert.show();
				}
				else{
					Intent i_map = new Intent(this, Map.class);
					startActivity(i_map);
				}
			}
			break;
			
		case R.id.exit_button:
			super.finish();
			break;
			
		// More buttons go here (if any) ...
		}
	}

}