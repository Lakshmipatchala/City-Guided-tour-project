package de.hft.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import de.hft.R;
import de.hft.activity.Web;

/**
 * This class is used to display a list of nearby Points of Interest
 * @author Markus
 *
 */
public  class NearbyPoiOverlay extends ItemizedOverlay  {

	/**
	 * the list for the Points of Interest
	 */
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	/**
	 * the context on which the Point of interest id drawn
	 */
	private Context mContext;
	
	/**
	 * Constructor for the Point of Interest 
	 * @param defaultMarker the Picture for the POI
	 * @param context on which context the POI should be drawn
	 */
	public  NearbyPoiOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		
	}

	/**
	 * creates a Nearby Points of Interest
	 */
	@Override
	protected synchronized  OverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	/**
	 * returns the size of the list of Nearby Points of Interest
	 */
	@Override
	public synchronized  int size() {
		 return mOverlays.size();

	}

	/**
	 *
	 * adds a Point of Interest to list
	 * @param overlay the Point of Interest
	 */
	public synchronized  void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	
	/**
	 * on tap on a Point of Interest it opens a dialog to choose between two options
	 * one option is to display the information of the taped Point of Interest
	 * the other option is to show a route from your position to the position of the Point of Interest
	 */
	@Override
	protected boolean onTap( int index) {
		 final OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
		builder.setMessage("You have now two option which one would you like to choose")  
		     .setCancelable(true)  
		     .setPositiveButton("show info of POI",  
		          new DialogInterface.OnClickListener(){  
		          public void onClick(DialogInterface dialog, int id){  
		        	 
		        	  
		        	  String url =mContext.getString(R.string.server_url).concat("/PresentPointOfInterestById.jsp?id=");//"http://hft.dyndns.biz:8080/MobileCityGuideServer/PresentPointOfInterestById.jsp?id=";
		        	 
		        	  Intent i = new Intent(mContext, Web.class);
		        	  i.putExtra("URL",url+item.getTitle());
		        	  mContext.startActivity(i);
		          }  
		     });  
		     builder.setNegativeButton("Navigate to POI",  
		          new DialogInterface.OnClickListener(){  
		          public void onClick(DialogInterface dialog, int id){  
		        	  if(mContext instanceof Map){
		        	  Bundle bundle = new Bundle();
		        	  List<double[]> listForPoiCoordinates = new LinkedList<double[]>();
		        	  double[] poiCoordinates = new double[5];
		        	  	poiCoordinates[0] = (item.getPoint().getLatitudeE6()/1E6);
		        	  	poiCoordinates[1] = (item.getPoint().getLongitudeE6()/1E6);
						poiCoordinates[2] = Integer.valueOf(item.getTitle());
											
						listForPoiCoordinates.add(poiCoordinates);
		        	  
						for (int i = 0; i < listForPoiCoordinates.size(); i++) {
							bundle.putDoubleArray("poi" + i,
									listForPoiCoordinates.get(i));
						}
						
						Intent myIntent = new Intent(mContext, Map.class);
						myIntent.putExtras(bundle);
						myIntent.setPackage(this.getClass().getName());
//						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mContext.startActivity(myIntent);
		        	  }
		          }  
		     });  
		AlertDialog alert = builder.create();  
		alert.show();  
		
	  
 //	  Intent i = new Intent(Intent.ACTION_VIEW);
//	  i.setData(Uri.parse(url+item.getTitle()));
//	  mContext.startActivity(i);

	  return true;
	}
	
}
