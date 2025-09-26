package de.hft.common;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.webkit.WebView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import de.hft.activities.R;
import de.hft.activities.Route;
import de.hft.activities.TourInformation;
import de.hft.activities.Web;

public class Poi extends ItemizedOverlay  {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public Poi(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	@Override
	public int size() {
		 return mOverlays.size();

	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  String url = "http://10.0.2.2:8080/CityGuideServerPrototype/PresentPointOfInterestById.jsp?id=";
	 
	  Intent i = new Intent(mContext, Web.class);
	  i.putExtra("URL",url+item.getTitle());
	  mContext.startActivity(i);
	 	  
 //	  Intent i = new Intent(Intent.ACTION_VIEW);
//	  i.setData(Uri.parse(url+item.getTitle()));
//	  mContext.startActivity(i);

	  return true;
	}
	
}
