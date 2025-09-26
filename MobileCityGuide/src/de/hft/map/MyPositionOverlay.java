package de.hft.map;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
/**
 * This class is used to draw an turning arrow of your own position.
 * @author Markus
 *
 */
public class MyPositionOverlay extends Overlay {

	/**
	 * your own Position
	 */
	private Location location;
	/**
	 * the angle of your Position
	 */
	private float angle;
	/**
	 * get the Angle of the Position
	 * @return angle between 0 and 360
	 */
	public synchronized  float getAngle() {
		return angle;
	}

	/**
	 * set the Angle of the Position
	 * @param angle between 0 and 360
	 */
	public synchronized  void setAngle(float angle) {
		this.angle = angle;
	}

	/**
	 * get the actual Position as Location {@link android.location.Location }
	 * 
	 * @return location 
	 *  
	 */
	public synchronized  Location getLocation() {
		return location;
	}

	/**
	 * set the actual Position as Location {@link android.location.Location }
	 * @return location 
	 *  
	 */
	public synchronized  void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * draws the Position as an arrow with the direction in which the user is looking
	 */
	@Override
	public synchronized  void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		canvas.save();
		if (shadow == false) {
			
			// Get the current location
			Double latitude = location.getLatitude() * 1E6;
			Double longitude = location.getLongitude() * 1E6;
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(),
					longitude.intValue());

			// Convert the location to screen pixels
			Point point = new Point();
			projection.toPixels(geoPoint, point);

			// Setup the paint
			Paint paint = new Paint();
			paint.setARGB(255, 0, 191, 255);
			paint.setAntiAlias(true);
			Path mPath = new Path();
			mPath.moveTo(point.x, point.y);
			mPath.lineTo(point.x-10, point.y+30);
            mPath.lineTo(point.x+0, point.y+25);
            mPath.lineTo(point.x+10, point.y+30);
			mPath.close();
			// rotate the arrow
			Matrix m = canvas.getMatrix();
			m.preRotate(angle, point.x, point.y);
			canvas.setMatrix(m);
//			canvas.rotate(angle, point.x, point.y);
			canvas.drawPath(mPath, paint);

		}
		
		//mapView.postInvalidate();
		super.draw(canvas, mapView, shadow);
		mapView.postInvalidate();
		canvas.restore();
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.maps.Overlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
	 */
	@Override
	public synchronized  boolean onTap(GeoPoint point, MapView mapView) {
		return false;
	}
}
