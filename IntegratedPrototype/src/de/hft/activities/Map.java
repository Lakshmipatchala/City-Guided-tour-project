package de.hft.activities;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import de.hft.activities.R;
import de.hft.common.Connection;
import de.hft.common.MyPosition;
import de.hft.common.Poi;

public class Map extends MapActivity {
	
	
	int longitude;
	int latitude;
	Connection servlet =null;
	private MapController mapController;
	private LocationManager locationManager;
	 Poi itemizedoverlay;
	 MyPosition me;
	 Drawable drawable;
	 Drawable drawable2;
	 MapView mapView;
	List<Overlay> mapOverlays;
	List<Overlay> mapOverlays2;
//	private static final String URL = "http://192.168.43.189:8080/CG/LoadPointOfInterestById?id=1";
//	private static final String URL = "http://192.168.43.38:8080/CG/LoadPointOfInterestById?id=1";
//	private static final String URL = "http://10.0.2.2:8080/CG/LoadPointOfInterestById?id=1";
//	private static final String URL = "http://10.0.2.2:8080/CityGuideServerPrototype/LoadPointOfInterestById?id=1";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
       
       mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        drawable2 = this.getResources().getDrawable(R.drawable.androidmarkerr);
        itemizedoverlay = new Poi(drawable, this);
        
        Bundle bundle = this.getIntent().getExtras();
        System.out.println(bundle.size());
        for (int i = 0; i < bundle.size(); i++) {
        	double[] poiCoordinates = bundle.getDoubleArray("poi"+i);
        	
        	       	
        	GeoPoint point = new GeoPoint((int) (poiCoordinates[1] * 1E6),(int)(poiCoordinates[0] * 1E6));
            OverlayItem overlayitem = new OverlayItem(point, String.valueOf((int)(poiCoordinates[2])), "Stuttgart Main Station");

            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
		}
        
        mapController = mapView.getController();
		mapController.setZoom(14); // Zoon 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				1, new GeoUpdateHandler());
           
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class GeoUpdateHandler implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			mapOverlays2 = mapView.getOverlays();
			me = new MyPosition(drawable2, Map.this);
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point); //	mapController.setCenter(point);
			OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "Stuttgart Main Station");
			if(overlayitem != null){
				mapOverlays2.remove(me);
				mapView.getOverlays().remove(me);
				me.removeOverlay(overlayitem);
				
			}
			mapView.invalidate();
			me.addOverlay(overlayitem);
			mapOverlays2.add(me);
//			int lat = (int) (location.getLatitude() * 1E6);
//			int lng = (int) (location.getLongitude() * 1E6);
//			GeoPoint point = new GeoPoint(lat, lng);
//			mapController.animateTo(point); //	mapController.setCenter(point);
//			 OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "Stuttgart Main Station");
//			 	
//	            itemizedoverlay.addOverlay(overlayitem);
//	            mapOverlays.add(itemizedoverlay);
//			
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

}
