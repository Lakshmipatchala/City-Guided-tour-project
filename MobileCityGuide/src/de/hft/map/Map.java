package de.hft.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import de.hft.R;
import de.hft.activity.MainMenuActivity;
import de.hft.common.JsonConnection;
import de.hft.navigation.MapService;
import de.hft.navigation.NavigationDataSet;

/**
 * This class displays the different Points of Interest on a Map. 
 * It shows your own position and also a Route between the Points of interest.
 * The Map supports 3 Use cases: <br> <blockquote>
 * <li> Display n nearby Points of interest within a defined range <br>
 * <li> Display n Points of interest with the Route between them <br>
 * <li> Display a route from your own position to one Point  of interest <br>
 * </blockquote>
 * 
 * 
 * @author Markus
 *
 */
public class Map extends MapActivity{
	
	/**
	 * The {@link MapView} for the map
	 */
	private MapView mapView;
	/**
	 * The Overlays for the map
	 */
	private List<Overlay> mapOverlays;
	/**
	 * The controller to set the view of the map
	 */
	private MapController mapController;
	/**
	 * the manager for the locations
	 */
	private LocationManager locationManager;
	/**
	 * the listener for the location
	 */
	private  LocationListener locationListener;
	/**
	 * used to focus on own position once the location is retrieved
	 */
	boolean once=true;
	/**
	 * used to follow own position and update the focus
	 */
	boolean followMyPosition=false;
	/**
	 * the azimut of the own position
	 */
	private float azimut;
	/**
	 * object for the own position
	 */
	private MyPositionOverlay me;
	/**
	 * the manager for the Sensors (Compass)
	 */
	private SensorManager mSensorManager;
	/**
	 * the listeners for the Sensors (Compass)
	 */
	private SensorEventListener mSensorListener;
	/**
	 * download dialog 
	 */
	private  ProgressDialog dialog;
	/**
	 * minimum time for updating the GPS Position
	 */
	private static long minTimeMillis = 2000;
	/**
	 * minimum distance for updating the GPS Position
	 */
    private static long minDistanceMeters = 2;
    /**
     * Textfield for the distance for the nearby POI search
     */
    private EditText editTextDistance;
    /**
     * the search button for the nerby POI search
     */
	private View searchButton;
	/**
	 * 
	 */
    private int lastStatus = 0;
    /**
     * used to distinguish between best location
     */
	private Location myLocation;
	/**
	 * list which contains nearby POIs within a given range
	 */
	private LinkedList<NearbyPoiOverlay> listNearbyPois;
   
		
	/**
	 * subclass to calculate and show the nearby Points of interest in the background
	 * @author Markus
	 *
	 */
	private class NearbyPoiBackground extends AsyncTask<String, Void, String>{

	
		/**
		 * stores nearby Points of interest within a given range
		 */
		@Override
		protected String doInBackground(String... params) {
		editTextDistance.getText();
		
		calculateNearbyPOIs(Integer.valueOf(editTextDistance.getText().toString()));
			return null;
		}
		
		/**
		 * disables gui elements before searching for nearby Points of interest
		 * registers GPS and compass sensor listeners
		 */
		@Override
		public void onPreExecute(){
			 
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTextDistance.getWindowToken(), 0);
	        searchButton.setEnabled(false);
	        editTextDistance.setEnabled(false);
			registerLocationListeners();
			registerSensors();
			
		}
		
		/**
		 * displays nearby found Points of interest. 
		 * If no Points of Interest were found it displays a hint to increase the radius
		 */
		@Override
		protected void onPostExecute(String result) {
			
			if(listNearbyPois.size() > 0){
				for(int i=0; i < listNearbyPois.size();i++){
					mapOverlays.add(listNearbyPois.get(i));				
				}
			}else {
				Toast.makeText(getBaseContext(),"no nerby POI found, try larger radius",
                        Toast.LENGTH_SHORT).show();
			}
			searchButton.setEnabled(true);
			editTextDistance.setEnabled(true);
			
		}
		
		
	}
	
	/**
	 * subclass to show Points of interest with a route and own position
	 * @author Markus
	 *
	 */
	private class PoiBackground extends AsyncTask<String, Void, String>{

		
		/**
		 * draws the route and shows the Point of Interest on the map
		 */
		@Override
		protected String doInBackground(String... params) {
			drawRoute(Map.this.getIntent().getExtras());
			showPOIs(Map.this.getIntent().getExtras());
			mapView.postInvalidate();
			return null;
			
		}
		
		/**
		 * disables the Progress dialog and registers GPS and compass sensor listeners
		 */
		@Override
		protected void onPostExecute(String result) {
			
			if (dialog != null ) {
				dialog.dismiss();
			}
			once = true;
			 
			LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);    
	        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
	             createGpsDisabledAlert();  
	        }  
			registerLocationListeners();
			registerSensors();
		}
		
		/**
		 * disables Progress dialog if procedure is cancelled
		 */
		@Override
		protected void onCancelled(){
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			//super.onCancelled();
		}
		
		/**
		 * show the Progress dialog before executing
		 */
		@Override
		public void onPreExecute(){
				dialog = ProgressDialog.show(Map.this, "Download", "downloading");

		}
		
	}
	
	
	/**
	 * creates the whole {@link MapView}. Decides which use case it should show up 
	 * either Nearby points of interest or Points of interest with a route
	 * it also forces the user to enable GPS if it is not available
	 */
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		 
		  me= new MyPositionOverlay();
		  
		 locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if( locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null){
			myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}else if( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
		}

		  if(this.getIntent().getPackage() != null && this.getIntent().getPackage().contains(MainMenuActivity.class.getName())){
			  //map was called form main menu
		  }
		  
	        if(this.getIntent().getExtras() == null){
			setContentView(R.layout.nearbymap);
			editTextDistance = (EditText) findViewById(R.id.textDistance);
			searchButton = findViewById(R.id.searchButtonMap);
			searchButton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					
					LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) | locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						
						if (!editTextDistance.getText().toString().equals("")) {
							mapOverlays.clear();
							NearbyPoiBackground task = (NearbyPoiBackground) getLastNonConfigurationInstance();
							if (task != null
									&& task.getStatus() == AsyncTask.Status.RUNNING) {
								// do nothing
							} else {
								task = new NearbyPoiBackground();
								task.execute();

							}
						} else {
							Toast.makeText(getBaseContext(), "no input",
									Toast.LENGTH_SHORT).show();
						}
						
					
					} else {
						
						
						createGpsDisabledAlert();
						
					}
				}
			});
	        	
	        	
	        }else{
	        	setContentView(R.layout.map);
	        	LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        	if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) | locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	        	PoiBackground  task = (PoiBackground) getLastNonConfigurationInstance();
	        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
	        	// do nothing
	        }else{			
	        	task = new PoiBackground();
	        	task.execute();
	        	
	        }
	        	}else {
					createGpsDisabledAlert();
				}
	        
	}
	   
	     
	       
	        mapView = (MapView) findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(true);
	       	      
	       mapOverlays = mapView.getOverlays();
	       mapController = mapView.getController();
	        //Center of Stuttgart
	        mapController.setCenter(new GeoPoint((int)(48.783 * 1E6) ,(int)(9.183* 1E6)));
	        mapController.setZoom(14); // Zoom 1 is world view
	        	     	
	 }
	
	/**
	 * collects all nearby Points of Interest within a given range
	 * @param maxdistance the max range in which the Point of interest should be
	 */
	private void calculateNearbyPOIs(int maxdistance) {

		Drawable drawablePoi = this.getResources().getDrawable(R.drawable.poi);
		List<double[]> listOfPois = new LinkedList<double[]>();
		NearbyPoiOverlay itemizedoverlay = new NearbyPoiOverlay(drawablePoi, this);
		listNearbyPois = new LinkedList<NearbyPoiOverlay>();
		
		String poiUrl = this.getString(R.string.server_url).concat("/LoadAllPointsOfInterest");//"http://hft.dyndns.biz:8080/MobileCityGuideServer/LoadAllPointsOfInterest";
		String object = JsonConnection.getStringRepresentation(poiUrl);
		
		try {
			JSONArray pois = new JSONArray(object);
			for (int i = 0; i < pois.length(); i++) {
				double[] lonlat = new double[3];
				lonlat[0] = (pois.getJSONObject(i).getDouble("latitude"));
				lonlat[1] = (pois.getJSONObject(i).getDouble("longitude") );
				lonlat[2]= pois.getJSONObject(i).getInt("id");
				listOfPois.add(lonlat);
			}
		} catch (JSONException e) {
			Log.d("Map error", e.getMessage());
		}

		
			
				for (Iterator<double[]>it=listOfPois.iterator(); it.hasNext(); ) {
					double[] tmp= it.next();
					double distance = maxdistance+1;
					if(myLocation != null){
						
						distance = distance(tmp[0], tmp[1], myLocation.getLatitude(), myLocation.getLongitude());
					}
					
					
					if(distance <= maxdistance){
						
						GeoPoint point = new GeoPoint((int) (tmp[0] *1E6 ),
								(int) (tmp[1] *1E6 ));
						OverlayItem overlayitem = new OverlayItem(point,
								String.valueOf((int) (tmp[2])), "");
						itemizedoverlay.addOverlay(overlayitem);
						listNearbyPois.add(itemizedoverlay);
					}
				}
				
	}
	
	/**
	 * register the GPS location and/or network Provider location
	 */
	private void registerLocationListeners(){
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation == null) {
            locationProvider = LocationManager.GPS_PROVIDER;
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        }
        if (lastKnownLocation != null) {
        	showMyPosition(lastKnownLocation);
        }

        

		if(locationListener== null){
		  locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		     showMyPosition(location);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	String showStatus = null;
                if (status == LocationProvider.AVAILABLE)
                        showStatus = "Available";
                if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
                        showStatus = "Temporarily Unavailable";
                if (status == LocationProvider.OUT_OF_SERVICE)
                        showStatus = "Out of Service";
                if (status != lastStatus) {
//                        Toast.makeText(getBaseContext(),"new status: " + showStatus,
//                                        Toast.LENGTH_SHORT).show();
                }
                lastStatus = status;

		    }

		    public void onProviderEnabled(String provider) {
//		    	Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
//                        Toast.LENGTH_SHORT).show();

		    }

		    public void onProviderDisabled(String provider) {
//		    	Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
//                        Toast.LENGTH_SHORT).show();
		    }
		  };
		  
		 
		// Register the listener with the Location Manager to receive location updates
		  
	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	            locationManager.requestLocationUpdates(
	                    LocationManager.GPS_PROVIDER, minTimeMillis, minDistanceMeters, locationListener);
	        } else {
	            locationManager.requestLocationUpdates(
	                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	        }
		}
	}
	
	/**
	 * draws the Points of interest on the map from a given set of coordinates
	 * @param bundle with the coordinates bundle is a Double array <br>
	 * key is poi+n n is an iterator for the points of interest <br>
	 * value is a double Array: <blockquote>
	 * [0] is latitude <br> [1] is longitude <br> [2] is id of point of interest <br>
	 * <blockquote>
	 * 
	 */
	private void showPOIs(Bundle bundle){
		
		 Drawable drawablePoi = this.getResources().getDrawable(R.drawable.poi);
	       
	       PoiOverlay itemizedoverlay = new PoiOverlay(drawablePoi, this);
		 
	     
		for (int i = 0; i < bundle.size(); i++) {
        	double[] poiCoordinates = bundle.getDoubleArray("poi"+i);
        	GeoPoint point = new GeoPoint((int) (poiCoordinates[0] * 1E6),(int)(poiCoordinates[1] * 1E6));
            OverlayItem overlayitem = new OverlayItem(point, String.valueOf((int)(poiCoordinates[2])),"");
            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
		
	}
	}
	
	/**
	 * draws the Route between one or more Points of interest
	 * @param bundle with the coordinates bundle is a Double array <br>
	 * key is poi+n n is an iterator for the points of interest <br>
	 * value is a double Array: <blockquote>
	 * [0] is latitude <br> [1] is longitude <br> [2] is id of point of interest <br>
	 * <blockquote>
	 */
	private void drawRoute(Bundle bundle){
		
		if(bundle.size()==1 && myLocation != null){
			double[] poiCoordinates = bundle.getDoubleArray("poi0");
			NavigationDataSet route = MapService.calculateRoute(myLocation.getLatitude(), myLocation.getLongitude(), poiCoordinates[0], poiCoordinates[1], MapService.MODE_WALKING);
		drawPath(route, Color.parseColor("#add331"), this.mapView);	
		}else if(bundle.size() > 1){
			for (int i = 0; i < bundle.size()-1; i++) {
	        	double[] poiCoordinates = bundle.getDoubleArray("poi"+i);
	        	double[] nextpoiCoordinates = bundle.getDoubleArray("poi"+(i+1));
	        	NavigationDataSet route = MapService.calculateRoute(poiCoordinates[0], poiCoordinates[1], nextpoiCoordinates[0], nextpoiCoordinates[1], MapService.MODE_WALKING);
	        	drawPath(route, Color.parseColor("#add331"), this.mapView);	
			}
		}
}
	
	/**
	 * registers the Compass sensor listener to show the direction in which the user is holding his phone
	 */
private void registerSensors(){
		
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new SensorEventListener() {
			
			
			  float[] mGravity;
			  float[] mGeomagnetic;
			@Override
			public void onSensorChanged(SensorEvent event) {
				 if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				      mGravity = event.values;
				    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				      mGeomagnetic = event.values;
				    if (mGravity != null && mGeomagnetic != null) {
				      float R[] = new float[9];
				      float I[] = new float[9];
				      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
				      if (success) {
				        float orientation[] = new float[3];
				        SensorManager.getOrientation(R, orientation);
				        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
				        
				      }
				    }
				
		        if(me != null){
		        	

		        	me.setAngle(azimut*360/(2*3.14159f));
		        }
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	
	/**
	 * registers GPS and Compass sensors listeners
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
			
		if(locationManager != null && mSensorManager != null){
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				1, locationListener);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	/**
	 * remove GPS and Compass sensor listeners
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if(locationManager != null && mSensorManager != null){
		locationManager.removeUpdates(locationListener);
		mSensorManager.unregisterListener(mSensorListener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.mapsubmenu, menu);
	    return true;
	}
	
	/**
	 * submenu for follow my position and show my position
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.mylocation:
	    	Location location=null;
	    	Location locationNetwork =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	Location locationGPS =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	if(locationGPS != null){
	    		location = locationGPS;
	    	}
	    	else if(locationNetwork != null) {
	    		location = locationNetwork;
	    	}
	    	if(location != null){
	    	int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint myPosition = new GeoPoint(lat, lng);
	    	mapController.animateTo(myPosition);
	    	}
	        return true;
	    case R.id.followMyPosition:
	    	
	       followMyPosition = !followMyPosition;
	       if(followMyPosition){
	    	   item.setTitle(R.string.menu_dont_follow_position);
	       }else{
	    	   item.setTitle(R.string.menu_follow_position);
	       }
	       
	       return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    }
	

	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
	/**
	 * shows own position
	 * @param location the actual location of the device
	 */
	public void showMyPosition(Location location){
		Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
        for (Iterator<Overlay> iter = mapView.getOverlays().iterator(); iter.hasNext();) {
            Overlay o = iter.next();
            Log.d("Stuff", "overlay type: " + o.getClass().getName());
            if (PoiOverlay.class.getName().equals(o.getClass().getName())
            		|| RouteOverlay.class.getName().equals(o.getClass().getName())
            		|| NearbyPoiOverlay.class.getName().equals(o.getClass().getName())
            		) {
                // mMapView01.getOverlays().remove(o);
                overlaysToAddAgain.add(o);
            }
        }
        mapView.getOverlays().clear();
        mapView.getOverlays().addAll(overlaysToAddAgain);

        
		
//		 me= new MyPositionOverlay();
		 this.myLocation= location;
		me.setLocation(location);
//		me.setAngle(location.getBearing());
		
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint myPosition = new GeoPoint(lat, lng);
		
		if(once){
			mapController.animateTo(myPosition); //	mapController.setCenter(point);	
			once = false;
		}
		if(followMyPosition){
			mapController.animateTo(myPosition);
		}
		
		
		mapOverlays.add(me);
		
	}
	
	/**
	 * creates the Dialog to enable GPS or Network Location
	 */
	private void createGpsDisabledAlert(){  
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		builder.setMessage("Your GPS is disabled! Would you like to enable it?")  
		     .setCancelable(false)  
		     .setPositiveButton("Enable GPS",  
		          new DialogInterface.OnClickListener(){  
		          public void onClick(DialogInterface dialog, int id){  
		               showGpsOptions();  
		          }  
		     });  
		     builder.setNegativeButton("Do nothing",  
		          new DialogInterface.OnClickListener(){  
		          public void onClick(DialogInterface dialog, int id){  
		               dialog.cancel();  
		              
		          }  
		     });  
		AlertDialog alert = builder.create();  
		alert.show();  
		}  
		  /**
		   * opens the settings for Location on the device
		   */
		private void showGpsOptions(){  
		        Intent gpsOptionsIntent = new Intent(  
		                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
		        startActivity(gpsOptionsIntent);  
		}  

		
		/**
		 * calculates the distance between to Points on a Map by using Pythagorean theorem
		 * @param lat1 latitude of first point
		 * @param lon1 longitude of first point
		 * @param lat2 latitude of second point
		 * @param lon2 longitude of second point
		 * @return distance in meter
		 */
		private double distance(double lat1, double lon1, double lat2, double lon2) {
			  double theta = lon1 - lon2;
			  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
			  dist = Math.acos(dist);
			  dist = rad2deg(dist);
			  dist = dist * 60 * 1.1515;
			  
			    dist = dist * 1.609344 * 1000;
			 
			  return (dist);
			}

		/**
		 * This function converts decimal degrees to radiant     
		 * @param deg to be converted
		 * @return radiant
		 */
			private double deg2rad(double deg) {
			  return (deg * Math.PI / 180.0);
			}

			/**
			 * This function converts radiant to decimal degrees 
			 * @param rad to be converted
			 * @return decimal
			 */
			private double rad2deg(double rad) {
			  return (rad * 180.0 / Math.PI);
			}
			
	
	
/**
 * Does the actual drawing of the route, based on the geo points provided in the nav set
 *
 * @param navSet     Navigation set bean that holds the route information, incl. geo pos
 * @param color      Color in which to draw the lines
 * @param mMapView01 Map view to draw onto
 */
public void drawPath(NavigationDataSet navSet, int color, MapView mMapView01) {

    Log.d("Stuff", "map color before: " + color);        

    // color correction for dining, make it darker
    if (color == Color.parseColor("#add331")) color = Color.parseColor("#6C8715");
    Log.d("Stuff", "map color after: " + color);

    Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
    for (Iterator<Overlay> iter = mMapView01.getOverlays().iterator(); iter.hasNext();) {
        Overlay o = iter.next();
        Log.d("Stuff", "overlay type: " + o.getClass().getName());
        if (!RouteOverlay.class.getName().equals(o.getClass().getName())) {
            // mMapView01.getOverlays().remove(o);
            overlaysToAddAgain.add(o);
        }
    }
  //  mMapView01.getOverlays().clear();
    mMapView01.getOverlays().addAll(overlaysToAddAgain);

    String path = navSet.getRoutePlacemark().getCoordinates();
    Log.d("Stuff", "path=" + path);
    if (path != null && path.trim().length() > 0) {
        String[] pairs = path.trim().split(" ");

        Log.d("Stuff", "pairs.length=" + pairs.length);

        String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height

       Log.d("Stuff", "lnglat =" + lngLat + ", length: " + lngLat.length);

        if (lngLat.length<3) lngLat = pairs[1].split(","); // if first pair is not transferred completely, take seconds pair //TODO 

        try {
            GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
            mMapView01.getOverlays().add(new RouteOverlay(startGP, startGP, 1));
            GeoPoint gp1;
            GeoPoint gp2 = startGP;

            for (int i = 1; i < pairs.length; i++) // the last one would be crash
            {
                lngLat = pairs[i].split(",");

                gp1 = gp2;

                if (lngLat.length >= 2 && gp1.getLatitudeE6() > 0 && gp1.getLongitudeE6() > 0
                        && gp2.getLatitudeE6() > 0 && gp2.getLongitudeE6() > 0) {

                    // for GeoPoint, first:latitude, second:longitude
                    gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));

                    if (gp2.getLatitudeE6() != 22200000) { 
                        mMapView01.getOverlays().add(new RouteOverlay(gp1, gp2, 2, color));
                        Log.d("Stuff", "draw:" + gp1.getLatitudeE6() + "/" + gp1.getLongitudeE6() + " TO " + gp2.getLatitudeE6() + "/" + gp2.getLongitudeE6());
                    }
                }
                 Log.d("Stuff","pair:" + pairs[i]);
            }
            //routeOverlays.add(new RouteOverlay(gp2,gp2, 3));
            mMapView01.getOverlays().add(new RouteOverlay(gp2, gp2, 3));
        } catch (NumberFormatException e) {
           Log.e("Stuff", "Cannot draw route.", e);
        }
    }
    // mMapView01.getOverlays().addAll(routeOverlays); // use the default color
    mMapView01.setEnabled(true);
}

}
