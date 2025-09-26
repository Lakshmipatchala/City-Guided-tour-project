package de.hft.common;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyPosition extends ItemizedOverlay  {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public MyPosition(Drawable defaultMarker, Context context) {
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
	
	public void removeOverlay(OverlayItem overlay){
		mOverlays.remove(overlay);
		populate();
		 
	}
	
	
	
	@Override
	protected boolean onTap(int index) {
	  return true;
	}
	
}
