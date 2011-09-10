package com.fara.firstbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

public class Search extends Activity implements OnClickListener, Runnable {
		
	//sostituire il numero finale con il numero delle fermate letto dall'utente

	private HashMap<String, String> map;
	private AlertDialog alert;
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
		case 3:
			builder.setMessage(R.string.min_fermate)
			.setCancelable(false)
			.setTitle("Info")
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
		setContentView(R.layout.search);

	     // Set up click listeners for all the buttons
        View selectLine = findViewById(R.id.selectLine);
        selectLine.setOnClickListener(this);
        View whoWins = findViewById(R.id.whoWins);
		whoWins.setOnClickListener(this);
		View stop_piu = findViewById(R.id.stop_piu);
		stop_piu.setOnClickListener(this);
		View stop_meno = findViewById(R.id.stop_meno);
		stop_meno.setOnClickListener(this);
		
		Button w = (Button) findViewById(R.id.whoWins);
		w.setEnabled(false);
		
		Spinner busList1 = (Spinner) findViewById(R.id.busList1);
		Spinner busList2 = (Spinner) findViewById(R.id.busList2);
		busList1.setEnabled(false);
		busList2.setEnabled(false);		
	}
	
	
	public void onClick(View v) {
		EditText n_stop = (EditText) findViewById(R.id.stop);
		Integer stop = Integer.parseInt(n_stop.getText().toString());

		switch (v.getId()) {
			case R.id.selectLine:
				EditText fermata1 = (EditText) findViewById(R.id.fermata1);
				EditText fermata2 = (EditText) findViewById(R.id.fermata2);
				if(fermata1.getText().length()==0 || fermata2.getText().length()==0 )
					break;
				else{
					dialog_load = ProgressDialog.show(this,"","Loading data...", true);
					Thread thread = new Thread(this);
	                thread.start();		
					break;
				}
			case R.id.whoWins:
				try {
					firstBus();
				} catch (ParseException e) {
					buildMyDialog(2);
				}
				break;
			case R.id.stop_piu:
				stop = stop + 1;
				n_stop.setText(stop.toString());
				break;
			case R.id.stop_meno:
				stop = stop - 1;
				if(stop < 2){
					alert = buildMyDialog(3);
					alert.show();
					stop=2;
				}
				n_stop.setText(stop.toString());
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
		//ArrayList<String> fermate = new ArrayList<String>();
		
		ArrayList<String> spinnerArray1 = new ArrayList<String>();
		ArrayList<String> spinnerArray2 = new ArrayList<String>();
		
		EditText fermata1 = (EditText) findViewById(R.id.fermata1);
		EditText fermata2 = (EditText) findViewById(R.id.fermata2);
		
		//fermate.add(fermata1.getText().toString());
		//fermate.add(fermata2.getText().toString());
	    
		//nel caso di + di 2 fermate leggere il numero dall'interfaccia
	    Integer nf=1;
	    
		while(nf<3)
		{
			String my_url = "http://www.5t.torino.it/pda/it/arrivi.jsp?n=";
			
			switch (nf) {
			    case 1:
			    	my_url = my_url.concat(fermata1.getText().toString());
			    	break;
			    case 2:
			    	my_url = my_url.concat(fermata2.getText().toString());
			    	break;
			}
			
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
		    	
		    	switch (nf) {
				    case 1:
				    	spinnerArray1.add(bus);
				    	break;
				    case 2:
				    	spinnerArray2.add(bus);
				    	break;
		    	}
		    }
		    
		    switch (nf) {
			    case 1:
			    	Message msg1 = new Message();
			    	msg1.what=1;
			    	msg1.obj=spinnerArray1;
			    	handler.sendMessage(msg1);
			    	break;
				case 2:
					Message msg2 = new Message();
			    	msg2.what=2;
			    	msg2.obj=spinnerArray2;
			    	handler.sendMessage(msg2);
			    	break;
		   }
		   nf++;
		}
		Message msg0 = new Message();
		msg0.what=0;
		handler.sendMessage(msg0);
	}

	
	private void firstBus() throws ParseException{

		Spinner busList1 = (Spinner) findViewById(R.id.busList1);
	    Spinner busList2 = (Spinner) findViewById(R.id.busList2);
		
	    String bus1 = busList1.getSelectedItem().toString();
	    String bus2 = busList2.getSelectedItem().toString();
	    
	    String ora_bus1 = map.get(bus1);
	    String ora_bus2 = map.get(bus2);
	    
		Date time1 = StringToTime(ora_bus1);
		Date time2 = StringToTime(ora_bus2);
		
		EditText fermata1 = (EditText) findViewById(R.id.fermata1);
		EditText fermata2 = (EditText) findViewById(R.id.fermata2);

		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Integer h = now.getHours();
		Integer m = now.getMinutes();	
		String now_str = h.toString().concat(":").concat(m.toString());
		Date now_red = StringToTime(now_str);
		
		
		long diff1 = time1.getTime() - now_red.getTime();
		long diff2 = time2.getTime() - now_red.getTime();
		
		if(diff2 <= diff1){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Prendi la " + bus2 + " alla fermata " + fermata2.getText().toString())
			.setCancelable(false)
			.setTitle(R.string.app_name)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		}
		else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Prendi la " + bus1 + " alla fermata " + fermata1.getText().toString())
			.setCancelable(false)
			.setTitle(R.string.app_name)
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		}
		
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
	
	private void buslist1(ArrayList<String> list){
    	Spinner busList1 = (Spinner) findViewById(R.id.busList1);
    	ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_dropdown_item,
		        list);
		busList1.setAdapter(spinnerArrayAdapter1);
		busList1.setEnabled(true);
	}
	
	private void buslist2(ArrayList<String> list){
    	Spinner busList2 = (Spinner) findViewById(R.id.busList2);
    	ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_dropdown_item,
		        list);
		busList2.setAdapter(spinnerArrayAdapter2);
		busList2.setEnabled(true);	
	}
	
	private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
        	switch(msg.what){
        	case 0: 
        		Button w = (Button) findViewById(R.id.whoWins);
        		w.setEnabled(true);
        		dialog_load.dismiss();
        		break;
        	case 1:
        		buslist1((ArrayList<String>) msg.obj);
    			break;
        	case 2:
        		buslist2((ArrayList<String>) msg.obj);
    			break;  
        	case 3:
        		dialog_load.dismiss();
        		buildMyDialog(2);
				break;
        	}        	
        }
    };
}