package com.fara.firstbus;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Map extends MapActivity implements LocationListener{
	
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private PlacesItemizedOverlay placesItemizedOverlay;
	private AlertDialog alert;
	private GeoPoint point;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 

		super.onCreate(savedInstanceState); 
		setContentView(R.layout.map);
		initialiseMapView();
		}
	
	@Override
	public void onStart(){
		super.onStart();
		initialiseOverlays();	
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private AlertDialog buildMyDialog(Integer type){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (type){
		//No localization service active
		case 1: 
				builder.setMessage(R.string.location_text)
				.setCancelable(false)
				.setTitle(R.string.lacation_error)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				break;
		//GPS is better
		case 2:
			builder.setMessage(R.string.gps_text)
			.setCancelable(false)
			.setTitle(R.string.gps_is_better)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			break;
		}
		return builder.create();
	}

	private void initialiseMapView() {
		
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapController = mapView.getController();

		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		
		mapController.setZoom(19);		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 70, this);
		}
		else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			//GPS is better!
			alert = buildMyDialog(2);
			alert.show();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 70, this);
		}
		else{
			//No localization service active
			alert = buildMyDialog(1);
			alert.show();
		}
	}
	
	private void myPosition (Location location){
		
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		point = new GeoPoint(lat, lng);
		
		mapController.animateTo(point); //mapController.setCenter(point);
		
		placesItemizedOverlay.addOverlayItem(new OverlayItem(point, "Me", "My actual position"));
		mapView.getOverlays().add(placesItemizedOverlay);
		mapView.showContextMenu();
		mapView.setClickable(true);
		mapView.setHapticFeedbackEnabled(true);
	}
	
	
	private void initialiseOverlays() {		
		Drawable myMarker = getResources().getDrawable(R.drawable.icon_36);
		placesItemizedOverlay = new PlacesItemizedOverlay(this, myMarker);
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		myPosition(location);	
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}