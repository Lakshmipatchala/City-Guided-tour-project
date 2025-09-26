package de.hft;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapsServlet extends MapActivity {

	int longitude;
	int latitude;
	Connection servlet =null;
	private static final String URL = "http://192.168.43.189:8080/Test/HelloJSON";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
       
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
        
       
        	servlet= new Connection(URL);        	
        
	
        JSONObject json =servlet.getJSONObject();
        try {
        	String slongitude = json.getString("longitude");
        	String slatitude =  json.getString("latitude");
			longitude= Integer.parseInt(slongitude);
			latitude= Integer.parseInt(slatitude);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

       
        GeoPoint point = new GeoPoint(latitude,longitude);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "Stuttgart Main Station");
      
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
        
        
        
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
