package com.fara.firstbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Arrivals extends Activity implements OnClickListener, Runnable {
		
	//sostituire il numero finale con il numero delle fermate letto dall'utente

	private HashMap<String, String> map;
	//private AlertDialog alert;
	private ProgressDialog dialog_load;
 	
	AlertDialog buildMyDialog(Integer type){
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (type){

		case 1: 
			builder.setMessage("Bravo")
			.setCancelable(false)
			.setTitle(R.string.about_label)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			break;
		case 2:
			builder.setMessage("error")
			.setCancelable(false)
			.setTitle(R.string.error_text)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			break;
		}
		return builder.create();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.arrivals);

	     // Set up click listeners for all the buttons
        View selectLine = findViewById(R.id.selectLine);
        selectLine.setOnClickListener(this);
        View search = findViewById(R.id.search);
		search.setOnClickListener(this);
		View allBus = findViewById(R.id.all_bus);
		allBus.setOnClickListener(this);
		
		
		Button s = (Button) findViewById(R.id.search);
		s.setEnabled(false);
		
		Spinner busList = (Spinner) findViewById(R.id.busList);
		busList.setEnabled(false);
	}
	
	
	public void onClick(View v) {
	
		switch (v.getId()) {
			case R.id.all_bus:
				CheckBox all = (CheckBox)findViewById(R.id.all_bus);
				Spinner busList = (Spinner) findViewById(R.id.busList);
				Button s1 = (Button) findViewById(R.id.selectLine);
				Button s2 = (Button) findViewById(R.id.search);
				if(all.isChecked()){
					busList.setEnabled(false);
					s1.setEnabled(false);
					s2.setEnabled(true);
				}
				else{
					busList.setEnabled(false);
					s1.setEnabled(true);
					s2.setEnabled(false);
				}
				break;
			case R.id.selectLine:
				EditText fermata = (EditText) findViewById(R.id.fermata);
				if(fermata.getText().length()==0)
					break;
				else{
					dialog_load = ProgressDialog.show(this,"","Loading data...", true);
					Thread thread = new Thread(this);
	                thread.start();		
					break;
				}
			case R.id.search:
				CheckBox all2 = (CheckBox)findViewById(R.id.all_bus);
				
				if(all2.isChecked())
				{
					EditText fermata2 = (EditText) findViewById(R.id.fermata);
					if(fermata2.getText().length()==0)
						break;
					else{
						dialog_load = ProgressDialog.show(this,"","Loading data...", true);
						Thread thread2 = new Thread(this);
		                thread2.start();		
					}
					Spinner busList1 = (Spinner) findViewById(R.id.busList);
					Button b = (Button) findViewById(R.id.selectLine);
					b.setEnabled(false);
					busList1.setEnabled(false);
				}
				else{
					try {
						arrives();
					} catch (ParseException e) {
						buildMyDialog(2);
					}
				}
				break;
			// More buttons go here (if any) ...
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}

	private void search_bus(){
		map = new HashMap<String,String>();
		
		ArrayList<String> spinnerArray = new ArrayList<String>();
		
		EditText fermata = (EditText) findViewById(R.id.fermata);
		
	    
		String my_url = "http://www.5t.torino.it/pda/it/arrivi.jsp?n=";
		
    	my_url = my_url.concat(fermata.getText().toString());
		
		Document doc = null;
		try {
			doc = Jsoup.connect(my_url).get();
		} catch (IOException e) {
			//connessione persa
			Message msg = new Message();
			msg.what=3;
	    	handler.sendMessage(msg);
	    	return;
		}
		
	    Elements passaggi = doc.getElementsByClass("passaggi");
	    if (passaggi.isEmpty()){
	    	//Fermata errata
	    	Message msg = new Message();
			msg.what=3;
	    	handler.sendMessage(msg);
	    	return;
	    }
	    Elements linee = passaggi.first().getElementsByTag("li");
	    for (Element linea : linee) {
	    	
	    	Elements orari = linea.children();
	    	String bus = orari.first().text();
	    	String time = orari.get(1).text();
	    	
	    	map.put(bus, time);
	    	
			spinnerArray.add(bus);
	    }

	    Message msg1 = new Message();
    	msg1.what=1;
    	msg1.obj=spinnerArray;
    	handler.sendMessage(msg1);

		Message msg0 = new Message();
		msg0.what=0;
		handler.sendMessage(msg0);
	}

	
	private void arrives() throws ParseException{

		Spinner busList = (Spinner) findViewById(R.id.busList);
	    
	    
		//Date time = StringToTime(ora_bus);
	
		//EditText fermata = (EditText) findViewById(R.id.fermata);
	    
		CheckBox all = (CheckBox)findViewById(R.id.all_bus);
		String message="";
	    if(!all.isChecked())
	    {
	    	String bus = busList.getSelectedItem().toString();
		    String ora_bus = map.get(bus);
	    	message="Prossimo passaggio della " + bus + " alle ore " + ora_bus.toString() +"\n";
	    }	
	    else
	    {
	    	//ciclare sulla mappa e recuperare tutti gli orari con i bus per il messaggio
	    	for(Entry<String, String> bus : map.entrySet()){
	    		message += bus.getKey().toString() + " alle ore " + bus.getValue().toString() + "\n";
	    	}
	    }
	    message += "\n* = Real time prevision";
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(R.string.app_name)
		.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		
		Spinner busList1 = (Spinner) findViewById(R.id.busList);
		Button b = (Button) findViewById(R.id.selectLine);
		b.setEnabled(false);
		busList1.setEnabled(false);	
		
	}
	
	public Date StringToTime(String time) throws ParseException{
    
		DateFormat dateformat = new SimpleDateFormat("hh:mm");
		Date date = dateformat.parse(time);
  
		return date;
	}

	@Override
	public void run() {
		search_bus();
	}	
	
	private void buslist(ArrayList<String> list){
    	Spinner busList = (Spinner) findViewById(R.id.busList);
    	ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_dropdown_item,
		        list);
		busList.setAdapter(spinnerArrayAdapter);
		busList.setEnabled(true);
	}
	
	
	private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
        	switch(msg.what){
        	case 0: 
        		Button s = (Button) findViewById(R.id.search);
        		s.setEnabled(true);
        		dialog_load.dismiss();
        		CheckBox all = (CheckBox)findViewById(R.id.all_bus);
        		if(all.isChecked())
        		{
        			try {
						arrives();
					} catch (ParseException e) {
						buildMyDialog(2);
        			}		
        		}
        		break;
        	case 1:
        		buslist((ArrayList<String>) msg.obj);
    			break;
        	case 3:
        		dialog_load.dismiss();
        		buildMyDialog(2);
				break;
        	}        	
        }
    };
}