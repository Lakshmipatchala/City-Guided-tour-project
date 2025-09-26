package de.hft.map;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import de.hft.R;
import de.hft.activity.Web;
/**
 * This class is used to display a list of Points of Interest
 * @author Markus
 *
 */
public class PoiOverlay extends ItemizedOverlay  {

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
	public PoiOverlay(Drawable defaultMarker, Context context) {
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
	 * on tap on a specific Point of Interest the information of the Point of Interest
	 */
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  String url = mContext.getString(R.string.server_url).concat("/PresentPointOfInterestById.jsp?id="); //"http://hft.dyndns.biz:8080/MobileCityGuideServer/PresentPointOfInterestById.jsp?id=";
	 
	  Intent i = new Intent(mContext, Web.class);
	  i.putExtra("URL",url+item.getTitle());
	  mContext.startActivity(i);
	 	  
 //	  Intent i = new Intent(Intent.ACTION_VIEW);
//	  i.setData(Uri.parse(url+item.getTitle()));
//	  mContext.startActivity(i);

	  return true;
	}
	
}
